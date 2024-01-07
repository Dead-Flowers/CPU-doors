import datetime
from typing import List, Optional, Union
import uuid
from ninja import Field, ModelSchema, Router, Schema
from ninja_jwt.authentication import JWTAuth, AsyncJWTAuth
from controllers.apps import set_state
from controllers.models import ControllerDevice, ControllerDeviceStateEvent
from controllers.schemas import ControllerDeviceState, MqttControllerSetState
from doors.exceptions import ApiException
from doors.constants import ErrorCode
from doors.utils import get_now_ms
from users.helpers import UserDeviceChallengeSchema, validate_device_challenge
from asgiref.sync import sync_to_async


router = Router(auth=AsyncJWTAuth(), tags=["Controllers"])


class ControllerSchema(ModelSchema):
    state: Union[ControllerDeviceState, None] = None
    is_online: bool = Field(False, alias="is_online")
    last_seen: Union[datetime.datetime, None] = Field(..., alias="last_seen")

    class Config:
        model = ControllerDevice
        model_fields = ["id", "internal_name", "given_name"]

    @staticmethod
    def resolve_state(obj):
        return (
            ControllerDeviceStateEvent.objects.filter(
                controller=obj, was_acknowledged=True
            )
            .values_list("state", flat=True)
            .first()
        )


class ControllerEventInvokerSchema(Schema):
    user: str
    device: str

    @staticmethod
    def resolve_device(obj):
        return obj.name

    @staticmethod
    def resolve_user(obj):
        return obj.user.email


class ControllerEventSchema(ModelSchema):
    invoker: Union[None, ControllerEventInvokerSchema] = None

    class Config:
        model = ControllerDeviceStateEvent
        model_fields = ["id", "date", "state"]

    @staticmethod
    def resolve_invoker(obj):
        return obj.invoking_device


class AuthorizeSetStateRequest(UserDeviceChallengeSchema):
    previous_state: Union[ControllerDeviceState, None] = None
    new_state: ControllerDeviceState


def query_controllers(request):
    user = request.user
    return ControllerDevice.objects.filter(users=user)


@router.get("/", response=List[ControllerSchema])
def list_controllers(request):
    return query_controllers(request)


@router.get("/{id}", response=ControllerSchema)
def get_controller(request, id: uuid.UUID):
    return query_controllers(request).get(pk=id)


@router.get("/{id}/events", response=List[ControllerEventSchema])
def get_controller_events(request, id: uuid.UUID):
    return (
        query_controllers(request)
        .get(pk=id)
        .state_events.filter(was_acknowledged=True)[:100]
    )


@router.post("/{id}/set-state")
async def set_controller_state(request, id: uuid.UUID, body: AuthorizeSetStateRequest):
    @sync_to_async
    def validate_request():
        controller = get_controller(request, id)
        device = validate_device_challenge(request, body)
        last_state = controller.state_events.filter(was_acknowledged=True).first()
        if last_state and last_state.state != body.previous_state:
            raise ApiException(
                ErrorCode.INVALID_STATE,
                f"Operation was canceled due to invalid state. Remote state is '{last_state.state}' while expected '{body.previous_state}'",
                409,
            )
        event = ControllerDeviceStateEvent.objects.create(
            invoking_device=device, controller=controller, state=body.new_state
        )
        event.refresh_from_db()
        return (controller, event)

    @sync_to_async(thread_sensitive=True)
    def return_value(controller):
        return ControllerSchema.from_orm(controller)

    (controller, event) = await validate_request()
    try:
        result = await set_state(
            controller.internal_name,
            MqttControllerSetState(
                request_id=str(event.pk),
                timestamp=get_now_ms(),
                previous_state=body.previous_state == ControllerDeviceState.OPEN,
                new_state=body.new_state == ControllerDeviceState.OPEN,
            ),
        )
        if result.is_ack:
            await ControllerDeviceStateEvent.objects.filter(pk=event.pk).aupdate(
                was_acknowledged=True
            )
            await controller.arefresh_from_db()
            return await return_value(controller)
        await event.adelete()
        raise ApiException(
            ErrorCode.INVALID_STATE,
            f"Operation was canceled by the remote device due to invalid state.",
            409,
        )
    except TimeoutError as tex:
        raise ApiException(
            ErrorCode.TIMEOUT, f"Operation timed out. The device is not available", 400
        )
    except Exception as ex:
        raise ApiException(
            ErrorCode.INVALID_OPERATION, f"Operation failed: {str(ex)}", 400
        )

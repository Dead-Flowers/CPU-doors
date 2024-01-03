from typing import List, Optional, Union
import uuid
from ninja import ModelSchema, Router, Schema
from ninja_jwt.authentication import JWTAuth

from controllers.models import ControllerDevice, ControllerDeviceStateEvent
from controllers.schemas import ControllerDeviceState
from users.helpers import UserDeviceChallengeSchema


router = Router(
    auth=JWTAuth()
)

class ControllerSchema(ModelSchema):
    state: Union[ControllerDeviceState, None] = None
    class Config:
        model = ControllerDevice
        model_fields = ['id', 'internal_name', 'given_name']

    @staticmethod
    def resolve_state(obj):
        return ControllerDeviceStateEvent.objects.filter(controller=obj, was_acknowledged=True).values_list('state', flat=True).first()

class AuthorizeSetStateRequest(UserDeviceChallengeSchema):
    previous_state: Union[ControllerDeviceState, None] = None
    new_state: ControllerDeviceState


def query_controllers(request):
    user = request.user
    return ControllerDevice.objects.filter(user=user)

@router.get("/", response=List[ControllerSchema])
def list_controllers(request):
    return query_controllers(request)

@router.get("/{id}", response=ControllerSchema)
def get_controller(request, id: uuid.UUID):
    return query_controllers(request).get(pk=id)



@router.post("/{id}/set-state", response=ControllerSchema)
def set_controller_state(request, id: uuid.UUID, body: AuthorizeSetStateRequest):
    controller = get_controller(request, id)
    
    return controller


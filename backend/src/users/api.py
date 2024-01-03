from typing import List
import uuid
from ninja import Field, ModelSchema, Router, Schema
from ninja_jwt.authentication import JWTAuth
from django.core.exceptions import ValidationError
from django.db import transaction
from users.helpers import ConfirmUserPasswordSchema, UserDeviceChallengeResponse, UserDeviceChallengeSchema, get_device_challenge_token, query_devices, validate_device_challenge, validate_user_password
from users.models import UserDevice


router = Router(
    auth=JWTAuth()
)

class GetMeResponse(Schema):
    id: uuid.UUID
    email: str

class UserDeviceSchema(ModelSchema):
    class Config:
        model = UserDevice
        model_fields = ['id', 'name', 'created_at']



class AddDeviceRequest(ConfirmUserPasswordSchema):
    device_name: str
    device_public_key: str = Field(..., description="Base64-encoded DER string of the RSA public key")

@router.get("/me", response=GetMeResponse)
def get_me(request):
    return request.user

@router.get("/devices", response=List[UserDeviceSchema])
def list_devices(request):
    return query_devices(request)

@router.get("/devices/{id}", response=UserDeviceSchema)
def get_device(request, id: uuid.UUID):
    return query_devices(request).get(pk=id)

@router.post("/devices", response=UserDeviceSchema)
@transaction.atomic
def add_device(request, body: AddDeviceRequest):
    user = request.user
    if user.devices.filter(name=body.device_name).exists():
        raise ValidationError({'device_name': 'Device with this name already exists.'})
    validate_user_password(request, body)
    device = UserDevice(
        name = body.device_name,
        public_key=body.device_public_key,
        user=user,
    )
    device.full_clean()
    device.save()
    device.refresh_from_db()
    return device

@router.get("/devices/{id}/challenge", response=UserDeviceChallengeResponse)
def get_device_challenge(request, id: uuid.UUID):
    device = get_device(request, id)
    return UserDeviceChallengeResponse(challenge_token=get_device_challenge_token(request, device))

@router.delete("/devices/{id}")
def delete_device(request, id: uuid.UUID):
    device = get_device(request, id)
    device.delete()


@router.post("/devices/{id}/remove")
def remove_device(request, id: uuid.UUID, body: UserDeviceChallengeSchema):
    validate_device_challenge(request, body)
    device = get_device(request, id)
    device.delete()



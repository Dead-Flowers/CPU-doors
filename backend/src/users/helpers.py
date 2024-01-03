import base64
from datetime import timedelta
from typing import TypedDict
import uuid
from ninja import Field, Schema
from django.core.exceptions import ValidationError
from ninja_jwt.tokens import Token
import ninja_jwt.exceptions
from doors.exceptions import ChallengeFailedError
from users.models import User, UserDevice
from cryptography.hazmat.primitives.hashes import SHA256
from cryptography.hazmat.primitives.asymmetric.padding import PKCS1v15

JWT_DEVICE_ID_CLAIM = 'device_id'

class UserDeviceChallengeToken(Token):
    token_type: str = "challenge"
    lifetime: timedelta = timedelta(minutes=1)

    @classmethod
    def for_device(cls, user_device: UserDevice) -> 'UserDeviceChallengeToken':
        token = cls()
        token[JWT_DEVICE_ID_CLAIM] = str(user_device.pk)
        return token

class ConfirmUserPasswordSchema(Schema):
    user_password: str

class UserDeviceChallengeResponse(Schema):
    challenge_token: str = Field(..., description="Device challenge token issued by the server")

class UserDeviceChallengeSchema(UserDeviceChallengeResponse):
    challenge_token_signature: str = Field(..., description="Base-64 encoded, SHA1 of`challenge_token`, signed with device's private key.")


class ChallengeContent(Schema):
    class Config:
        extra='allow'
    device_id: uuid.UUID
    
def query_devices(request):
    user = request.user
    return UserDevice.objects.filter(user=user)

def validate_user_password(request, body: ConfirmUserPasswordSchema):
    user: User = request.user
    if not user.check_password(body.user_password):
        raise ValidationError({'user_password': "Invalid user password"})
    
def get_device_challenge_token(request, device: UserDevice) -> str:
    token = UserDeviceChallengeToken.for_device(device)
    return str(token)

def validate_device_challenge(request, body: UserDeviceChallengeSchema) -> UserDevice:
    try:
        token = UserDeviceChallengeToken(body.challenge_token, verify=True)
        device = query_devices(request).get(pk=token[JWT_DEVICE_ID_CLAIM])
    except (ninja_jwt.exceptions.TokenError, ninja_jwt.exceptions.InvalidToken, ninja_jwt.exceptions.ValidationError) as ex:
        raise ChallengeFailedError("Invalid token (%s)" % str(ex))
    try:
        public_key = device.rsa_public_key
        public_key.verify(base64.b64decode(body.challenge_token_signature), body.challenge_token.encode(), padding=PKCS1v15(), algorithm=SHA256())
    except Exception as ex:
        raise ChallengeFailedError("Invalid signature (%s)" % str(ex))

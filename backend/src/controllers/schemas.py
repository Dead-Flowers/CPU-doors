from django.db import models
from ninja import Schema


class ControllerDeviceState(models.TextChoices):
    OPEN = "open"
    CLOSED = "closed"


class MqttControllerSetState(Schema):
    request_id: str
    previous_state: bool
    new_state: bool
    timestamp: int


class MqttControllerSetStateAck(Schema):
    request_id: str
    timestamp: int
    is_ack: bool


class MqttControllerState(Schema):
    timestamp: int
    state: bool


class MqttControllerPing(Schema):
    timestamp: int

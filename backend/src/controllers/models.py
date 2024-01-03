import uuid
from django.db import models
from controllers.schemas import ControllerDeviceState

from users.models import User, UserDevice

class ControllerDevice(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    created_at = models.DateTimeField(auto_now_add=True)
    internal_name = models.CharField(unique=True, max_length=64)
    given_name = models.CharField(max_length=64)
    user = models.ForeignKey(User, blank=True, null=True, on_delete=models.SET_NULL)

    class Meta:
        ordering = ('internal_name',)
        unique_together = [('user', 'given_name')]

    def __str__(self):
        return f"{self.internal_name} ({self.given_name})"

class ControllerDeviceStateEvent(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)

    invoking_device = models.ForeignKey(UserDevice, blank=True, null=True, on_delete=models.SET_NULL)
    controller = models.ForeignKey(ControllerDevice, related_name='state_events', on_delete=models.CASCADE)
    date = models.DateTimeField(auto_now_add=True)
    was_acknowledged = models.BooleanField(default=False)
    state = models.CharField(max_length=16, choices=ControllerDeviceState.choices)

    class Meta:
        ordering = ('controller', '-date')

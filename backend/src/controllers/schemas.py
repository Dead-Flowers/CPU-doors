
from django.db import models

class ControllerDeviceState(models.TextChoices):
    OPEN = "open"
    CLOSED = "closed"
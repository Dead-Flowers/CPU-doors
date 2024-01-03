import uuid
from django.db import models

from django.contrib.auth.models import AbstractUser
from django.db import models
from django.utils.translation import gettext_lazy as _
import base64
from cryptography.hazmat.primitives import serialization
from django.core.exceptions import ValidationError
from users.managers import UserManager


class User(AbstractUser):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    email = models.EmailField(_("email address"), unique=True)
    username = None

    USERNAME_FIELD = "email"
    REQUIRED_FIELDS = []

    objects = UserManager()

    def __str__(self):
        return self.email
    

class UserDevice(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    name = models.CharField(max_length=128)
    created_at = models.DateTimeField(auto_now_add=True)
    public_key = models.TextField(max_length=4096)

    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='devices')

    @property
    def rsa_public_key(self):
        base64_decoded = base64.b64decode(self.public_key, validate=True)
        key = serialization.load_der_public_key(base64_decoded)
        return key
    
    def clean(self):
        try:
            _ = self.rsa_public_key
        except Exception as ex:
            raise ValidationError("Invalid public key (%s). It must be a base64-encoded RSA public key." % str(ex))

    class Meta:
        ordering = ('user', 'name',)
        unique_together = [('user', 'name')]

    def __str__(self):
        return self.name

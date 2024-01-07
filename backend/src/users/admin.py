from django.contrib import admin
from users.models import User, UserDevice

# Register your models here.


@admin.register(User)
class UserAdmin(admin.ModelAdmin):
    pass


@admin.register(UserDevice)
class UserDeviceAdmin(admin.ModelAdmin):
    pass

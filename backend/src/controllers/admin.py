from django.contrib import admin

from controllers.models import ControllerDevice, ControllerDeviceStateEvent

# Register your models here.


class ControllerDeviceStateEventInline(admin.StackedInline):
    model = ControllerDeviceStateEvent
    extra = 1


@admin.register(ControllerDevice)
class ControllerDeviceAdmin(admin.ModelAdmin):
    inlines = [ControllerDeviceStateEventInline]

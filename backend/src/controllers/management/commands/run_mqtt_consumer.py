from datetime import datetime, timezone
from django.core.management import BaseCommand

from controllers.apps import (
    ControllersConfig,
    _try_handle_ping,
    _try_handle_set_state_ack,
    _try_handle_state,
    build_client,
)
import paho.mqtt.client as mqtt

from controllers.models import ControllerDevice, ControllerDeviceStateEvent
from controllers.schemas import ControllerDeviceState


def on_connect(client: mqtt.Client, userdata, flags, rc):
    client.subscribe("controllers/#")
    client.subscribe("backend/controllers/#")


def on_message(client: mqtt.Client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))
    internal_name = str(msg.topic).split("/")[-2]
    payload = _try_handle_ping(msg)
    if payload:
        ControllerDevice.objects.filter(internal_name=internal_name).update(
            last_ping_timestamp=payload.timestamp
        )
    payload = _try_handle_set_state_ack(msg)
    if payload:
        qs = ControllerDeviceStateEvent.objects.filter(pk=payload.request_id)
        if payload.is_ack:
            qs.update(was_acknowledged=True)
        else:
            qs.delete()
    payload = _try_handle_state(msg)
    if payload:
        controller = ControllerDevice.objects.filter(
            internal_name=internal_name
        ).first()
        if not controller:
            return
        date = datetime.fromtimestamp(payload.timestamp / 1000, tz=timezone.utc)
        state_str = (
            ControllerDeviceState.OPEN
            if payload.state
            else ControllerDeviceState.CLOSED
        )
        recent_event = ControllerDeviceStateEvent.objects.filter(
            controller=controller, was_acknowledged=True
        ).first()
        if recent_event and (
            recent_event.state == state_str or recent_event.date >= date
        ):
            return
        ControllerDeviceStateEvent.objects.create(
            controller=controller, was_acknowledged=True, date=date, state=state_str
        )


class Command(BaseCommand):
    help = "Start Main Loop"

    def handle(self, *args, **options):
        print("Running")
        client = build_client("backend-consumer")
        client.on_connect = on_connect
        client.on_message = on_message
        client.loop_forever()

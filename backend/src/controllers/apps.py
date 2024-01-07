from asyncio import Future
import datetime
import json
import os
from typing import Dict, Union
import uuid
import asyncio
from django.apps import AppConfig
import paho.mqtt.client as mqtt
import logging
from django.conf import settings
from django.utils.functional import classproperty

from controllers.schemas import (
    MqttControllerPing,
    MqttControllerSetState,
    MqttControllerSetStateAck,
    MqttControllerState,
)

mqtt_logger = logging.getLogger("mqtt")


def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe("backend/controllers/#", qos=2)


def _try_handle_set_state_ack(msg) -> Union[MqttControllerSetStateAck, None]:
    if not msg.topic.endswith("/ack-set-state"):
        return None
    return MqttControllerSetStateAck.model_validate_json(msg.payload)


def _try_handle_state(msg) -> Union[MqttControllerState, None]:
    if not msg.topic.endswith("/state"):
        return None
    return MqttControllerState.model_validate_json(msg.payload)


def _try_handle_ping(msg) -> Union[MqttControllerPing, None]:
    if not msg.topic.endswith("/ping"):
        return None
    return MqttControllerPing.model_validate_json(msg.payload)


def on_message(client: mqtt.Client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))
    payload = _try_handle_set_state_ack(msg)
    if not payload:
        return
    fut = pending_set_state_acks.pop(payload.request_id, None)
    if fut is not None:
        fut.set_result(payload)


class PollingFuture:
    def __init__(self) -> None:
        self.done = False
        self.result = None
        self.exception = None

    def _get_result(self):
        if not self.done:
            raise ValueError()
        if self.exception:
            raise self.exception
        return self.result

    def set_result(self, result):
        self.result = result
        self.done = True

    async def get_result(self, poll_rate=0.01, timeout=15):
        t0 = datetime.datetime.now()
        while not self.done:
            await asyncio.sleep(poll_rate)
            if (datetime.datetime.now() - t0).total_seconds() > timeout:
                raise TimeoutError()
        return self._get_result()


def build_client(name: str) -> mqtt.Client:
    client = mqtt.Client(name, clean_session=False)
    client.enable_logger(mqtt_logger)
    client.tls_set(
        ca_certs=settings.MQTT_BROKER_CA_CERT_PATH,
        certfile=settings.MQTT_CLIENT_CERT_PATH,
        keyfile=settings.MQTT_CLIENT_KEY_PATH,
    )
    client.tls_insecure_set(True)
    client.on_connect = on_connect
    client.connect(settings.MQTT_BROKER_HOST, port=8883)
    return client


pending_set_state_acks: Dict[str, PollingFuture] = {}

_mqtt_publisher = None


def get_mqtt_publisher() -> mqtt.Client:
    global _mqtt_publisher
    if _mqtt_publisher:
        return _mqtt_publisher
    client = build_client(f"backend-publisher-{uuid.uuid4()}")
    client.on_message = on_message
    client.on_connect = on_connect
    client.loop_start()
    _mqtt_publisher = client
    return client


async def set_state(
    internal_name: str, body: MqttControllerSetState
) -> MqttControllerSetStateAck:
    future = PollingFuture()
    pending_set_state_acks[body.request_id] = future
    try:
        get_mqtt_publisher().publish(
            f"controllers/{internal_name}/set-state", body.model_dump_json(), qos=2
        )
        return await future.get_result()
    finally:
        pending_set_state_acks.pop(body.request_id, None)


class ControllersConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "controllers"

#include <Arduino.h>
#include <HardwareSerial.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <string>
#include <stack>
#include "credentials.h"

const size_t MQTT_BUFSIZ = 256;

WiFiClient wlanClient;
PubSubClient mqttClient(wlanClient);

struct ScheduledReply {
  std::string topic;
  byte* data;
  unsigned int len;
};

std::stack<ScheduledReply> replies;

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  printf("Received message on topic %s: %.*s\n", topic, length, payload);

  auto reply = new byte[MQTT_BUFSIZ];
  unsigned int len = snprintf((char*)reply, MQTT_BUFSIZ, "Reply to '%.*s'", length, payload);
  replies.push({"/root/test2", reply, len});
}

void setup() {
  Serial.begin(9600);
  printf("Setup starting\n");
  WiFi.begin(WIFI_SSID, WIFI_PWD);
  printf("Connecting\n");
  while (WiFi.status() != WL_CONNECTED) {
    printf("wifi status: %d\n", WiFi.status());
    delay(100);
  }
  printf("Connected\n");
  Serial.println(WiFi.localIP());
  mqttClient.setServer(MQTT_HOST, 1883);
  mqttClient.setCallback(mqttCallback);
  if (mqttClient.connect("ESP", MQTT_USER, MQTT_PWD)) {
    printf("Connected to MQTT\n");
    mqttClient.subscribe("/root/test1");
  } else {
    printf("MQTT connection failed: %d\n", mqttClient.state());
    delay(1337);
  }
}

void loop() {
  mqttClient.loop();
  while (!replies.empty()) {
    auto& reply = replies.top();
    if(!mqttClient.publish(reply.topic.c_str(), reply.data, reply.len)) 
      break;
    delete[] reply.data;
    replies.pop();
  }
}

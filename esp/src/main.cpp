#include <Arduino.h>
#include <ArduinoJson.h>

#include <ESP8266WiFi.h>
#include <HardwareSerial.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include <stack>
#include <string>
#include "credentials.h"
#include "utils.h"

X509List mqttRootCA(CERT_ROOT_CA);
X509List mqttClientCert(CERT_CLIENT_CERT);
PrivateKey mqttClientKey(CERT_CLIENT_KEY);
WiFiClientSecure secureClient;

const size_t MQTT_BUFSIZ = 256;
static byte buffer[MQTT_BUFSIZ];
CertStore certStore;

PubSubClient mqttClient(secureClient);

bool stateIsOpen = false;
uint64_t lastSetStateTimestamp = 0;
uint64_t lastServerKnownStateTimestamp = 0;
uint64_t lastPingTimestamp = 0;
const uint64_t PING_INTERVAL = 10000;

String topicPrefix = "controllers/" + String(MQTT_CLIENT_NAME);

void _setState(const bool &state) {
  stateIsOpen = state;
  Serial.printf("State is now %s\n", state ? "open" : "closed");
}

void manuallySetState(const bool &state) {
  if (state == stateIsOpen)
    return;
  _setState(state);
  lastSetStateTimestamp = getNowMs();
}

void sendStateToServer() {
  JsonDocument reply;
  reply["timestamp"] = lastSetStateTimestamp;
  reply["state"] = stateIsOpen;
  auto numBytes = serializeJson(reply, buffer, MQTT_BUFSIZ);
  auto topicStr = "controllers/" + String(MQTT_CLIENT_NAME) + "/state";
  if (!mqttClient.publish(topicStr.c_str(), buffer, numBytes, true)) {
    return;
  }
  lastServerKnownStateTimestamp = lastSetStateTimestamp;
}

void handleSetState(const JsonDocument &doc) {
  bool prevState = doc["previous_state"].as<bool>();
  bool newState = doc["new_state"].as<bool>();
  const auto now = doc["timestamp"].as<uint64_t>();

  bool isAck = prevState == stateIsOpen && newState != stateIsOpen &&
               lastSetStateTimestamp <= now;

  JsonDocument reply;
  reply["timestamp"] = now;
  reply["request_id"] = doc["request_id"];
  reply["is_ack"] = isAck;
  auto numBytes = serializeJson(reply, buffer, MQTT_BUFSIZ);
  auto topicStr = "backend/" + topicPrefix + "/ack-set-state";
  if (!mqttClient.publish(topicStr.c_str(), buffer, numBytes) || !isAck) {
    return;
  }

  _setState(newState);
  lastSetStateTimestamp = lastServerKnownStateTimestamp = now;
}

void sendPingToServer(const uint64_t &timestamp) {
  String topic = topicPrefix + "/ping";
  JsonDocument doc;
  doc["timestamp"] = timestamp;
  auto numBytes = serializeJson(doc, buffer, MQTT_BUFSIZ);
  if (mqttClient.publish(topic.c_str(), buffer, numBytes)) {
    lastPingTimestamp = timestamp;
  }
}

void mqttCallback(char *topic, byte *payload, unsigned int length) {
  printf("Received message on topic %s: %.*s\n", topic, length, payload);
  auto topic_str = String(topic);
  JsonDocument doc;
  // Deserialize the JSON document
  DeserializationError error = deserializeJson(doc, payload);

  // Test if parsing succeeds.
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }
  if (topic_str.endsWith("/set-state")) {
    handleSetState(doc);
  }
}

bool ensureMqttConnected() {
  char err_buf[256];
  if (mqttClient.connected())
    return true;
  mqttClient.setServer(MQTT_HOST, 8883);
  mqttClient.setCallback(mqttCallback);
  if (mqttClient.connect(MQTT_CLIENT_NAME, NULL, NULL, NULL, 0, false, NULL,
                         false)) {
    printf("Connected to MQTT\n");
    String topic = topicPrefix + "/#";
    mqttClient.subscribe(topic.c_str(), 2 /* exactly once */);
    return true;
  }
  printf("MQTT connection failed: %d\n", mqttClient.state());
  Serial.print("failed, rc=");
  Serial.println(mqttClient.state());
  secureClient.getLastSSLError(err_buf, sizeof(err_buf));
  Serial.print("SSL error: ");
  Serial.println(err_buf);
  delay(1000);
  return false;
}

void setup() {
  secureClient.setTrustAnchors(&mqttRootCA);
  // secureClient.setInsecure(); // skip CN validation for local testing
  secureClient.setClientRSACert(&mqttClientCert, &mqttClientKey);
  Serial.begin(9600);
  printf("Setup starting\n");
  WiFi.begin(WIFI_SSID, WIFI_PWD);
  printf("Connecting\n");
  while (WiFi.status() != WL_CONNECTED) {
    printf("%d\n", WiFi.status());
    delay(500);
  }
  printf("Connected\n");
  Serial.println(WiFi.localIP());
  setClock(); // Required for X.509 validation
  printf("Current time is %llu\n", getNowMs());
  lastSetStateTimestamp = getNowMs();
  pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
  mqttClient.loop();
  if (ensureMqttConnected()) {
    if (lastSetStateTimestamp > lastServerKnownStateTimestamp) {
      sendStateToServer();
    }
    auto now = getNowMs();
    if (now - lastPingTimestamp > PING_INTERVAL) {
      sendPingToServer(now);
    }
  }
  digitalWrite(LED_BUILTIN, stateIsOpen ? HIGH : LOW);
  delay(10);
}

package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.services.MqttPublisher;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Development/testing endpoint to publish arbitrary MQTT messages from Postman or curl.
 *
 * How the round-trip works:
 *   POST /mqtt/debug/publish  { "topic": "greenhouse/192.168.1.100/status", "payload": "{...}" }
 *   → server publishes to broker
 *   → server receives its own message (subscribed to greenhouse/+/status)
 *   → updateTelemetry() runs, model is pushed to greenhouse/192.168.1.100/set/model
 *   → MQTTX (subscribed to greenhouse/+/set/model) shows the pushed model
 *
 * Disable or secure this controller before deploying to production.
 */
@RestController
@RequestMapping("/mqtt/debug")
public class MqttDebugController {

    private final MqttPublisher mqttPublisher;

    public MqttDebugController(MqttPublisher mqttPublisher) {
        this.mqttPublisher = mqttPublisher;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String payload = request.get("payload");
        if (topic == null || topic.isBlank() || payload == null || payload.isBlank()) {
            return ResponseEntity.badRequest().body("Both 'topic' and 'payload' are required");
        }
        try {
            mqttPublisher.sendCommand(topic, payload);
            return ResponseEntity.ok("Published to [" + topic + "]: " + payload);
        } catch (MqttException e) {
            return ResponseEntity.internalServerError().body("MQTT error: " + e.getMessage());
        }
    }
}
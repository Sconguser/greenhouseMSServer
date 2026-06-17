package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryGreenhouseDTO;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MqttService implements MqttCallbackExtended, MqttPublisher {

    private final MqttClient client;
    private final MqttConnectOptions connectOptions;
    private final GreenhouseService greenhouseService;
    private final ConfigService configService;
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper;

    public MqttService(@Value("${mqtt.broker}") String broker,
                       @Value("${mqtt.clientId}") String clientId,
                       @Value("${mqtt.username:}") String username,
                       @Value("${mqtt.password:}") String password,
                       @Lazy GreenhouseService greenhouseService,
                       @Lazy ConfigService configService,
                       @Lazy AnalyticsService analyticsService,
                       ObjectMapper objectMapper) throws MqttException {
        client = new MqttClient(broker, clientId);
        this.greenhouseService = greenhouseService;
        this.configService = configService;
        this.analyticsService = analyticsService;
        this.objectMapper = objectMapper;
        connectOptions = new MqttConnectOptions();
        if (!username.isEmpty()) {
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
        }
        connectOptions.setCleanSession(true);
        connectOptions.setAutomaticReconnect(true);
        client.setCallback(this);
        try {
            client.connect(connectOptions);
        } catch (MqttException e) {
            System.err.println("MQTT broker unavailable at startup (will retry): " + e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());

        if (topic.endsWith("/status")) {
            handleTelemetry(payload);
        } else if (topic.endsWith("/log")) {
            String ip = extractIp(topic);
            if (ip != null) handleDeviceLog(ip, payload);
        } else if (topic.endsWith("/ack/config")) {
            String ip = extractIp(topic);
            if (ip != null) configService.markDeviceConfigSynced(ip);
        } else if (topic.endsWith("/ack/mapping")) {
            String ip = extractIp(topic);
            if (ip != null) configService.markMappingConfigSynced(ip);
        } else if (topic.endsWith("/ack/model")) {
            String ip = extractIp(topic);
            if (ip != null) greenhouseService.markModelSynced(ip);
        }
    }

    private void handleTelemetry(String payload) {
        try {
            TelemetryGreenhouseDTO telemetry = objectMapper.readValue(payload, TelemetryGreenhouseDTO.class);
            greenhouseService.updateTelemetry(telemetry);
        } catch (Exception e) {
            System.err.println("Failed to parse telemetry: " + e.getMessage());
        }
    }

    // Device log payload: {"lvl":..,"code":..,"msg":..,"rep":?,"up":sec,"heap":bytes}
    private void handleDeviceLog(String ip, String payload) {
        try {
            JsonNode n = objectMapper.readTree(payload);
            String level = n.path("lvl").asText("INFO");
            String code = n.path("code").asText(null);
            String message = n.path("msg").asText("");
            // The board folds suppressed duplicates into a "rep" count; surface it inline.
            if (n.hasNonNull("rep") && n.path("rep").asInt() > 0) {
                message = message + " (×" + (n.path("rep").asInt() + 1) + ")";
            }
            Integer heap = n.hasNonNull("heap") ? n.path("heap").asInt() : null;
            Long uptime = n.hasNonNull("up") ? n.path("up").asLong() : null;
            analyticsService.recordDeviceLogByIp(ip, level, code, message, heap, uptime);
        } catch (Exception e) {
            System.err.println("Failed to parse device log: " + e.getMessage());
        }
    }

    // Topic format: greenhouse/{ip}/ack/config  →  extract {ip}
    private String extractIp(String topic) {
        String[] parts = topic.split("/");
        return parts.length >= 2 ? parts[1] : null;
    }

    @Override
    public void sendCommand(String topic, String payload) throws MqttException {
        if (!client.isConnected()) {
            System.out.println("MQTT not connected, attempting reconnect...");
            client.connect(connectOptions);
        }
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false);
        client.publish(topic, message);
        System.out.printf("Sent to [%s]: %s%n", topic, payload);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("MQTT connection lost: " + cause.getMessage());
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        try {
            client.subscribe("greenhouse/+/status", 1);
            client.subscribe("greenhouse/+/ack/+", 1);
            client.subscribe("greenhouse/+/log", 0);
            System.out.println("MQTT " + (reconnect ? "re" : "") + "connected – subscribed to status, ack and log topics");
        } catch (MqttException e) {
            System.err.println("MQTT subscribe failed after connect: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
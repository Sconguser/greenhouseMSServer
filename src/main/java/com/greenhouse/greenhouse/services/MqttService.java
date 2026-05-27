package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryGreenhouseDTO;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MqttService implements MqttCallbackExtended, MqttPublisher {

    private final MqttClient client;
    private final GreenhouseService greenhouseService;
    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    public MqttService(@Value("${mqtt.broker}") String broker,
                       @Value("${mqtt.clientId}") String clientId,
                       @Value("${mqtt.username:}") String username,
                       @Value("${mqtt.password:}") String password,
                       @Lazy GreenhouseService greenhouseService,
                       @Lazy ConfigService configService,
                       ObjectMapper objectMapper) throws MqttException {
        client = new MqttClient(broker, clientId);
        this.greenhouseService = greenhouseService;
        this.configService = configService;
        this.objectMapper = objectMapper;
        MqttConnectOptions options = new MqttConnectOptions();
        if (!username.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.setCallback(this);
        try {
            client.connect(options);
        } catch (MqttException e) {
            System.err.println("MQTT broker unavailable at startup (will retry): " + e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());

        if (topic.endsWith("/status")) {
            handleTelemetry(payload);
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

    // Topic format: greenhouse/{ip}/ack/config  →  extract {ip}
    private String extractIp(String topic) {
        String[] parts = topic.split("/");
        return parts.length >= 2 ? parts[1] : null;
    }

    @Override
    public void sendCommand(String topic, String payload) throws MqttException {
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
            System.out.println("MQTT " + (reconnect ? "re" : "") + "connected – subscribed to status and ack topics");
        } catch (MqttException e) {
            System.err.println("MQTT subscribe failed after connect: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
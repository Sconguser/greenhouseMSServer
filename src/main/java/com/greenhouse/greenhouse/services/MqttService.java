package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryGreenhouseDTO;
import com.greenhouse.greenhouse.models.Greenhouse;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MqttService implements MqttCallbackExtended, MqttPublisher {

    private final MqttClient client;

    private final GreenhouseService greenhouseService;

    @Autowired
    public MqttService (@Value("${mqtt.broker}") String broker, @Value("${mqtt.clientId}") String clientId,
                        @Value("${mqtt.username:}") String username, @Value("${mqtt.password:}") String password,
                        @Lazy GreenhouseService greenhouseService) throws MqttException
    {
        client = new MqttClient(broker, clientId);
        this.greenhouseService = greenhouseService;
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
            // connectComplete() handles subscription for both initial connect and reconnects,
            // so no explicit subscribe() call is needed here.
        } catch (MqttException e) {
            // Broker not reachable at startup — automaticReconnect will keep retrying.
            // connectComplete() will subscribe once the connection is established.
            System.err.println("MQTT broker unavailable at startup (will retry): " + e.getMessage());
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payloadStr = new String(message.getPayload());
        ObjectMapper objectMapper = new ObjectMapper();
// 1. Parse into Telemetry DTO (Lightweight)
        try {
            TelemetryGreenhouseDTO telemetry = objectMapper.readValue(payloadStr, TelemetryGreenhouseDTO.class);

            // 2. Delegate the update to a specialized service method
            // (You need to create this method in GreenhouseService)
            greenhouseService.updateTelemetry(telemetry);

        } catch (Exception e) {
            System.err.println("Failed to parse telemetry: " + e.getMessage());
        }
    }

    @Override
    public void sendCommand (String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false); // Important: Commands shouldn't be retained
        client.publish(topic, message);
        System.out.printf("Sent to [%s]: %s%n", topic, payload);
    }

    // Other mandatory callbacks
    @Override
    public void connectionLost (Throwable cause) {
        System.out.println("MQTT connection lost: " + cause.getMessage());
    }

    @Override
    public void connectComplete (boolean reconnect, String serverURI) {
        // Re-subscribe after every connect (required because cleanSession=true discards
        // server-side subscriptions; without this the server stops receiving telemetry
        // after any reconnect).
        try {
            client.subscribe("greenhouse/+/status", 1);
            System.out.println("MQTT " + (reconnect ? "re" : "") + "connected – subscribed to greenhouse/+/status");
        } catch (MqttException e) {
            System.err.println("MQTT subscribe failed after connect: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete (IMqttDeliveryToken token) {
    }
}

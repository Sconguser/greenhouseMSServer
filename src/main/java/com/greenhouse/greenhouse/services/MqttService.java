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
public class MqttService implements MqttCallback, MqttPublisher {

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

        client.setCallback(this);
        client.connect(options);
        client.subscribe("greenhouse/+/+", 1);
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
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommand (String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
        System.out.printf("Sent to [%s]: %s%n", topic, payload);
    }

    // Other mandatory callbacks
    @Override
    public void connectionLost (Throwable cause) {
        System.out.println("MQTT connection lost: " + cause.getMessage());

    }

    @Override
    public void deliveryComplete (IMqttDeliveryToken token) {
    }
}

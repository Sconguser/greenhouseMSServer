package com.greenhouse.greenhouse.services;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService implements MqttCallback {

    private final MqttClient client;

    public MqttService (@Value("${mqtt.broker}") String broker, @Value("${mqtt.clientId}") String clientId,
                        @Value("${mqtt.username:}") String username,
                        @Value("${mqtt.password:}") String password) throws MqttException
    {

        client = new MqttClient(broker, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        if (!username.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }

        client.setCallback(this);
        client.connect(options);
        client.subscribe("greenhouse/+/+", 1);
    }


    // Receive messages from Arduino
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.printf("Received on [%s]: %s%n", topic, new String(message.getPayload()));

        // Example: topic="greenhouse/1/temperature"
        String[] levels = topic.split("/"); // [ "greenhouse", "1", "temperature" ]
        String greenhouseId = levels[1];
        String dataType = levels[2];

        // Now use greenhouseId and dataType as needed in your processing
    }

    // Publish messages to Arduino
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

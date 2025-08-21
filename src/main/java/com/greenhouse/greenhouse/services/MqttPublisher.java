package com.greenhouse.greenhouse.services;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttPublisher {
    void sendCommand (String topic, String payload) throws MqttException;
}

package com.greenhouse.greenhouse.services;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationService {
    private final FirebaseMessaging firebaseMessaging;

    public NotificationService (FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendNotification (String title, String body, List<String> tokens) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();


        /// Dla jednego urządzenia
        Message message = Message.builder()
                .setToken(tokens.get(0))
                .setNotification(notification)
                .build();
        firebaseMessaging.sendAsync(message);

        /// Dla wielu urządzeń
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();
        firebaseMessaging.sendEachForMulticastAsync(multicastMessage);
    }
}

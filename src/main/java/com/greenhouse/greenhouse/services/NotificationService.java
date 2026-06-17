package com.greenhouse.greenhouse.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greenhouse.greenhouse.exceptions.NotificationNotSentException;
import com.greenhouse.greenhouse.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService (@Autowired(required = false) FirebaseMessaging firebaseMessaging, UserRepository userRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.userRepository = userRepository;
    }

    public void setUserFCMToken (Long userId, String fcmToken) {
        userRepository.findById(userId)
                .ifPresentOrElse((user) -> {
                    user.setFcmToken(fcmToken);
                    userRepository.save(user);
                }, () -> {
                    throw new UsernameNotFoundException("User was not found");
                });
    }

    public void deleteUserFCMToken (Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse((user) -> {
                    user.setFcmToken(null);
                    userRepository.save(user);
                }, () -> {
                    throw new UsernameNotFoundException("User was not found");
                });
    }

    public void sendTestNotificationToAlLUsers () {
        userRepository.findAll()
                .forEach((user) -> {
                    try {
                        sendNotification("This is a test", "Test", List.of(user.getFcmToken()));
                    } catch (FirebaseMessagingException e) {
                        throw new NotificationNotSentException(e.getMessage());
                    }
                });
    }

    /**
     * Sends a push to every user that has a registered FCM token. Safe to call
     * when Firebase is not configured or no users have tokens — it simply no-ops
     * and never throws, so background callers (e.g. the plant-health scheduler)
     * can fire-and-forget.
     */
    public void broadcast (String title, String body) {
        if (firebaseMessaging == null) return; // Firebase not configured
        userRepository.findAll().stream()
                .map(u -> u.getFcmToken())
                .filter(t -> t != null && !t.isBlank())
                .forEach(token -> {
                    try {
                        sendNotification(title, body, List.of(token));
                    } catch (Exception e) {
                        System.err.println("[NOTIFY] Failed to push to a device: " + e.getMessage());
                    }
                });
    }

    public void sendNotification (String title, String body, List<String> tokens) throws FirebaseMessagingException {
        if (firebaseMessaging == null || tokens == null || tokens.isEmpty()) return;
        String token = tokens.get(0);
        if (token == null || token.isBlank()) return;

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();


        /// Dla jednego urządzenia
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();
        firebaseMessaging.sendAsync(message);
        /// Dla wielu urządzeń
//        MulticastMessage multicastMessage = MulticastMessage.builder()
//                .addAllTokens(tokens)
//                .setNotification(notification)
//                .build();
//        firebaseMessaging.sendEachForMulticastAsync(multicastMessage);
    }
}

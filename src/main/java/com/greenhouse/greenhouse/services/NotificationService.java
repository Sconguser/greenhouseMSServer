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
//        MulticastMessage multicastMessage = MulticastMessage.builder()
//                .addAllTokens(tokens)
//                .setNotification(notification)
//                .build();
//        firebaseMessaging.sendEachForMulticastAsync(multicastMessage);
    }
}

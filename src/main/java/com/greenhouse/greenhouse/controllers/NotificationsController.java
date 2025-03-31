package com.greenhouse.greenhouse.controllers;


import com.greenhouse.greenhouse.repositories.UserRepository;
import com.greenhouse.greenhouse.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationsController (NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> setUserFCMToken (@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String fcmToken = body.get("fcmToken");
        notificationService.setUserFCMToken(userId, fcmToken);
        return ResponseEntity.ok("User token updated");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserFCMToken (@PathVariable Long userId) {
        notificationService.deleteUserFCMToken(userId);
        return ResponseEntity.ok("User token was deleted");
    }

    @GetMapping("/test")
    public ResponseEntity<?> testSendToAll () {
        notificationService.sendTestNotificationToAlLUsers();
        return ResponseEntity.ok("Sent");
    }
}

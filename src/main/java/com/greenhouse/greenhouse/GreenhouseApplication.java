package com.greenhouse.greenhouse;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

///TODO: vulnerability w dependencies
@SpringBootApplication
@EnableScheduling
public class GreenhouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenhouseApplication.class, args);
	}

//	@Value("${firebase.settings}")
//	private String firebaseSettings;

	@Bean
	FirebaseMessaging firebaseMessaging () throws IOException {

		try {
		GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials)
				.build();

		FirebaseApp app;
		if (FirebaseApp.getApps()
				.isEmpty())
		{
			app = FirebaseApp.initializeApp(firebaseOptions, "Greenhouse@Makerspace");
		} else {
			// Spróbuj pobrać po nazwie; jeśli brak, zainicjalizuj
			try {
				app = FirebaseApp.getInstance("Greenhouse@Makerspace");
			} catch (IllegalStateException | IllegalArgumentException ex) {
				app = FirebaseApp.initializeApp(firebaseOptions, "Greenhouse@Makerspace");
			}
		}
		return FirebaseMessaging.getInstance(app);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}

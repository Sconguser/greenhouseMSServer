package com.greenhouse.greenhouse.tasks;

import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.Status;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GreenhouseStatusScheduler {

    @Autowired
    private GreenhouseRepository greenhouseRepository;

    // Run every 60 seconds
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkGreenhouseStatus() {
        List<Greenhouse> greenhouses = greenhouseRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Greenhouse gh : greenhouses) {
            // If status is OFF, we don't care about timeouts
            if (gh.getStatus() == Status.OFF) continue;

            // If we haven't heard from it in > 5 minutes, mark as NOT_RESPONSIVE
            // (Assuming you have a 'lastUpdate' field. If not, you should add one!)
            if (gh.getLastUpdate() == null || gh.getLastUpdate().isBefore(now.minusMinutes(5))) {
                if (gh.getStatus() != Status.NOT_RESPONSIVE) {
                    gh.setStatus(Status.NOT_RESPONSIVE);
                    greenhouseRepository.save(gh);
                    System.out.println("Greenhouse " + gh.getName() + " is now NOT_RESPONSIVE (Timeout)");
                }
            }
        }
    }
}
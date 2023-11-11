package com.timothymou.cfrank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@Slf4j
public class ScheduledUpdater {
    private final ContestUpdater contestUpdater;

    ScheduledUpdater(ContestUpdater contestController) {
        this.contestUpdater = contestController;
    }

    @Scheduled(fixedRate = 4000) // TODO: In production, the rate can probably be hourly or even longer.
    public void checkForNewContests() {
        log.info("Scanning for new contests...");
        contestUpdater.checkContests();
    }
}

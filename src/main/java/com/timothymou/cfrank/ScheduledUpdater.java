package com.timothymou.cfrank;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ScheduledUpdater {
    private final ContestUpdater contestUpdater;

    ScheduledUpdater(ContestUpdater contestController) {
        this.contestUpdater = contestController;
    }

    @Scheduled(fixedRate = 10000) // TODO: In production, the rate can probably be hourly or even longer.
    public void checkForNewContests() {
        System.out.println("SHOULD NOT HAPPEN IN TESTING!!!");
        contestUpdater.checkContests();
    }
}

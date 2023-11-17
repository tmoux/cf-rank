package com.timothymou.cfrank;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class SchedulerUpdaterRunner {
    private final ScheduledUpdater scheduledUpdater;

    public SchedulerUpdaterRunner(ScheduledUpdater scheduledUpdater) {
        this.scheduledUpdater = scheduledUpdater;
    }

    @Scheduled(fixedRate = 86400) // TODO: In production, the rate can probably be hourly or even longer.
    public void runCheckForNewContests() {
        scheduledUpdater.checkForNewContests();
    }
}

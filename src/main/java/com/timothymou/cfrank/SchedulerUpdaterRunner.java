package com.timothymou.cfrank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@Slf4j
public class SchedulerUpdaterRunner {
    private final ScheduledUpdater scheduledUpdater;

    public SchedulerUpdaterRunner(ScheduledUpdater scheduledUpdater) {
        this.scheduledUpdater = scheduledUpdater;
    }

    @Scheduled(fixedRate = 86400 * 1000)
    public void runCheckForNewContests() {
        log.info("Scheduled scan for new contests...");
        scheduledUpdater.checkForNewContests();
    }
}

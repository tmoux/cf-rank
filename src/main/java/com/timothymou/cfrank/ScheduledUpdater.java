package com.timothymou.cfrank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
public class ScheduledUpdater {
    private final ContestUpdater contestUpdater;

    ScheduledUpdater(ContestUpdater contestUpdater) {
        this.contestUpdater = contestUpdater;
    }

    public void checkForNewContests() {
        log.info("Scanning for new contests...");
        contestUpdater.checkContests();
    }
}

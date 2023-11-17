package com.timothymou.cfrank;

public class ScheduledUpdater {
    private final ContestUpdater contestUpdater;

    ScheduledUpdater(ContestUpdater contestUpdater) {
        this.contestUpdater = contestUpdater;
    }

    public void checkForNewContests() {
        contestUpdater.checkContests();
    }
}

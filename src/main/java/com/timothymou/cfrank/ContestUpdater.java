package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfContest;
import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// The ContestUpdater has methods for checking for available contests,
// and adding new rating changes to the repository and rankInfo data structure.

@Service
public class ContestUpdater {
    private final RatingChangeRepository repository;
    private final ICfApiHandler cfApiHandler;
    private final RankInfo rankInfo;

    public ContestUpdater(
            RatingChangeRepository repository, ICfApiHandler cfApiHandler, RankInfo rankInfo) {
        this.repository = repository;
        this.cfApiHandler = cfApiHandler;
        this.rankInfo = rankInfo;

        // TODO: Initialize repo here?
    }

    public void updateContest(Integer contestId) {
        Optional<List<CfRatingChange>> cfRatingChangesOption = cfApiHandler.getRatingChangesFromContest(contestId);
        if (cfRatingChangesOption.isPresent()) {
            List<CfRatingChange> cfRatingChanges = cfRatingChangesOption.get();
            rankInfo.addContest(contestId, cfRatingChanges);
            repository.saveAll(cfRatingChanges);
        }
    }

    public void checkContests() {
        List<CfContest> contests = cfApiHandler.getAvailableContests();
        contests.sort(Comparator.comparing(CfContest::startTimeSeconds));
        System.out.println(contests.stream().map(CfContest::id).collect(Collectors.toList()));
        for (CfContest contest : contests) {
            if (!rankInfo.hasContest(contest.id())) {
                System.out.printf("UPDATING CONTEST %d%n", contest.id());
                this.updateContest(contest.id());
            }
        }
    }
}

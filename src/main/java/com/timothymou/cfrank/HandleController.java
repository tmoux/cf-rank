package com.timothymou.cfrank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.RatingChange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandleController {
    private final RatingChangeRepository repository;
    private final RankInfo rankInfo;

    public HandleController(RatingChangeRepository repository, RankInfo rankInfo) {
        this.repository = repository;
        this.rankInfo = rankInfo;
    }

    private ContestRankUpdate getContestRankUpdate(Contest contest, Integer rating) {
        return new ContestRankUpdate(contest, rankInfo.queryRank(contest.getId(), rating));
    }

    @GetMapping("/gethandle")
    public List<ContestRankUpdate> getHandle(@RequestParam(value = "handle") String handle) {
        List<RatingChange> cfRatingChanges = repository.findByHandle(handle);
        ArrayList<ContestRankUpdate> ranks = new ArrayList<>(cfRatingChanges.stream()
                .sorted(Comparator.comparing(c -> c.getContest().getStartTime()))
                .map(c -> getContestRankUpdate(c.getContest(), c.getNewRating()))
                .toList());
        Contest lastContest = rankInfo.getLastContest();
        if (!cfRatingChanges.isEmpty()) {
            Contest lastContestForHandle = cfRatingChanges.get(cfRatingChanges.size() - 1).getContest();
            if (!lastContestForHandle.equals(lastContest)) {
                Integer currentRating = rankInfo.getCurrentRating(handle);
                ranks.add(getContestRankUpdate(lastContest, currentRating));
            }
        }
        return ranks;
    }
}

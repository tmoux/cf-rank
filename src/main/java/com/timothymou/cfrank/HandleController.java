package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.RatingChange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
public class HandleController {
    private final RatingChangeRepository repository;
    private final RankInfo rankInfo;

    public HandleController(RatingChangeRepository repository, RankInfo rankInfo) {
        this.repository = repository;
        this.rankInfo = rankInfo;
    }

    private ContestRankUpdate getContestRankUpdate(Contest contest, Integer rating) {
        return new ContestRankUpdate(contest, rankInfo.queryRank(contest.getId(), rating), rating);
    }

    public List<ContestRankUpdate> getHandle(String handle) {
        if (rankInfo.hasHandle(handle)) {
            List<RatingChange> cfRatingChanges = repository.findByHandle(handle);
            ArrayList<ContestRankUpdate> ranks = new ArrayList<>(cfRatingChanges.stream()
                    .sorted(Comparator.comparing(c -> c.getContest().getStartTime()))
                    .map(c -> getContestRankUpdate(c.getContest(), c.getNewRating()))
                    .toList());
            Contest lastContest = rankInfo.getLastContest();
            if (!cfRatingChanges.isEmpty()) {
                Contest lastContestForHandle = cfRatingChanges.get(cfRatingChanges.size() - 1).getContest();
                if (lastContest != null && !lastContestForHandle.equals(lastContest)) {
                    Integer currentRating = rankInfo.getCurrentRating(handle);
                    ranks.add(getContestRankUpdate(lastContest, currentRating));
                }
            }
            return ranks;
        }
        else {
            return List.of();
        }
    }

    @GetMapping("/gethandle")
    public ResponseEntity<List<ContestRankUpdate>> getHandleAPI(@RequestParam(value = "handle") String handle) {
        List<ContestRankUpdate> rankUpdates = getHandle(handle);
        return ResponseEntity.of(Optional.of(rankUpdates));
    }
}

package com.timothymou.cfrank;

import java.util.ArrayList;
import java.util.List;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandleController {
    private final ContestRepository contestRepository;
    private final RatingChangeRepository repository;
    private final RankInfo rankInfo;

    public HandleController(
            ContestRepository contestRepository, RatingChangeRepository repository, RankInfo rankInfo) {
        this.contestRepository = contestRepository;
        this.repository = repository;
        this.rankInfo = rankInfo;
    }

    @GetMapping("/gethandle")
    // TODO: Instead of returning a list, should return a list of pairs (rank, contest)
    public List<Integer> getHandle(@RequestParam(value = "handle") String handle) {
        // TODO: Sort rating changes by their start time.
        List<CfRatingChange> cfRatingChanges = repository.findByHandle(handle);
        ArrayList<Integer> ranks = new ArrayList<>(cfRatingChanges.stream()
                .map(c -> rankInfo.queryRank(c.getContestId(), c.getNewRating())).toList());
        Integer lastContest = rankInfo.getLastContestId();
        if (!cfRatingChanges.isEmpty()) {
            Integer lastContestForHandle = cfRatingChanges.get(cfRatingChanges.size() - 1).getContestId();
            if (!lastContestForHandle.equals(lastContest)) {
                Integer currentRating = rankInfo.getCurrentRating(handle);
                ranks.add(rankInfo.queryRank(rankInfo.getLastContestId(), currentRating));
            }
        }
        return ranks;
    }
}

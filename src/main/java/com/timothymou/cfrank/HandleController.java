package com.timothymou.cfrank;

import java.util.ArrayList;
import java.util.Comparator;
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

    private ContestRankUpdate getContestRankUpdate(Integer contestId, Integer rating) {
        return new ContestRankUpdate(
                contestRepository.findById(contestId).get(),
                rankInfo.queryRank(contestId, rating)
        );
    }

    @GetMapping("/gethandle")
    public List<ContestRankUpdate> getHandle(@RequestParam(value = "handle") String handle) {
        // TODO: Sort rating changes by their start time. (Probably need to read start times from contest repository)
        List<CfRatingChange> cfRatingChanges = repository.findByHandle(handle);
        ArrayList<ContestRankUpdate> ranks = new ArrayList<>(cfRatingChanges.stream()
                .map(c -> getContestRankUpdate(c.getContestId(), c.getNewRating())).
                toList());
        ranks.sort(Comparator.comparing(c -> c.contest().getStartTime()));
        Integer lastContest = rankInfo.getLastContestId();
        if (!cfRatingChanges.isEmpty()) {
            Integer lastContestForHandle = cfRatingChanges.get(cfRatingChanges.size() - 1).getContestId();
            if (!lastContestForHandle.equals(lastContest)) {
                Integer currentRating = rankInfo.getCurrentRating(handle);
                ranks.add(getContestRankUpdate(lastContest, currentRating));
            }
        }
        return ranks;
    }
}

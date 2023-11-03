package com.timothymou.cfrank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HandleController {
    // TODO: add Contest repository
    private final RatingChangeRepository repository;
    private final RankInfo rankInfo;

    public HandleController(
            RatingChangeRepository repository, RankInfo rankInfo) {
        this.repository = repository;
        this.rankInfo = rankInfo;
    }

    @GetMapping("/gethandle")
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

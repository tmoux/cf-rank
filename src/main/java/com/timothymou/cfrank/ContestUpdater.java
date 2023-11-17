package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import com.timothymou.cfrank.cfapi.RatingChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// The ContestUpdater has methods for checking for available contests,
// and adding new rating changes to the repository and rankInfo data structure.

@Service
@Slf4j
public class ContestUpdater {
    private final ContestRepository contestRepository;
    private final RatingChangeRepository ratingChangeRepository;
    private final ICfApiHandler cfApiHandler;
    private final RankInfo rankInfo;

    public ContestUpdater(
            ContestRepository contestRepository, RatingChangeRepository ratingChangeRepository, ICfApiHandler cfApiHandler, RankInfo rankInfo) {
        this.contestRepository = contestRepository;
        this.ratingChangeRepository = ratingChangeRepository;
        this.cfApiHandler = cfApiHandler;
        this.rankInfo = rankInfo;
    }

    public void updateContest(Contest contest) {
        Optional<List<CfRatingChange>> cfRatingChangesOption = cfApiHandler.getRatingChangesFromContest(contest.getId());
        if (cfRatingChangesOption.isPresent()) {
            contestRepository.save(contest);
            List<RatingChange> cfRatingChanges = cfRatingChangesOption.get()
                    .stream()
                    .map(c -> new RatingChange(contest, c)).toList();
            rankInfo.addContest(contest, cfRatingChanges);
            ratingChangeRepository.saveAll(cfRatingChanges);
        }
    }

    public void checkContests() {
        List<Contest> contests = cfApiHandler.getAvailableContests()
                .stream()
                .map(Contest::new)
                .filter(c -> !rankInfo.hasContest(c.getId()))
                .sorted(Comparator.comparing(Contest::getStartTime)).toList();
        log.info("Adding contests: {}", contests.stream().map(Contest::getId).toList());
        for (Contest contest : contests) {
            if (!rankInfo.hasContest(contest.getId())) {
                log.info("Updating contest {}, time = {}", contest.getId(), contest.getStartTime());
                this.updateContest(contest);
            }
        }
    }
}

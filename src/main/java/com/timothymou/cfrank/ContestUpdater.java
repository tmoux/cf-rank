package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import com.timothymou.cfrank.cfapi.RatingChange;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// The ContestUpdater has methods for checking for available contests,
// and adding new rating changes to the repository and rankInfo data structure.

@Service
public class ContestUpdater {
    private final ContestRepository contestRepository;
    private final RatingChangeRepository repository;
    private final ICfApiHandler cfApiHandler;
    private final RankInfo rankInfo;

    public ContestUpdater(
            ContestRepository contestRepository, RatingChangeRepository repository, ICfApiHandler cfApiHandler, RankInfo rankInfo) {
        this.contestRepository = contestRepository;
        this.repository = repository;
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
            repository.saveAll(cfRatingChanges);
        }
    }

    public void checkContests() {
        List<Contest> contests = cfApiHandler.getAvailableContests()
                .stream()
                .map(Contest::new)
                .sorted(Comparator.comparing(Contest::getStartTime)).toList();
        System.out.println(contests);
        for (Contest contest : contests) {
            if (!rankInfo.hasContest(contest.getId())) {
                System.out.format("Updating Contest %d\n" , contest.getId());
                this.updateContest(contest);
            }
        }
    }
}

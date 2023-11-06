package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.Contest;
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
            List<CfRatingChange> cfRatingChanges = cfRatingChangesOption.get();
            rankInfo.addContest(contest.getId(), cfRatingChanges);
            repository.saveAll(cfRatingChanges);
        }
    }

    public void checkContests() {
        List<Contest> contests = cfApiHandler.getAvailableContests()
                .stream()
                .sorted(Comparator.comparing(Contest::getStartTime)).toList();
        for (Contest contest : contests) {
            if (!rankInfo.hasContest(contest.getId())) {
                this.updateContest(contest);
            }
        }
    }
}

package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import com.timothymou.cfrank.cfapi.RatingChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Comparator;
import java.util.List;

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

    private boolean contestIsPresentAndUnrated(Contest c) {
        return contestRepository.findById(c.getId())
                .filter(contest -> !contest.isRated())
                .isPresent();
    }

    // Need to update cfApiHandler:
    // Exception if the contest is not rated (400):
    // Exception if the request failed (5xx error):
    // Don't handle in fetchRatingUpdates, this just returns a plain old List.
    // Handle in updateContest:
    // - If 400, add to list of contests to ignore (do this later)
    // - Otherwise, just ignore the error and move on.
    private List<RatingChange> fetchRatingUpdates(Contest contest) {
        if (contestRepository.existsById(contest.getId())) {
            return ratingChangeRepository.findAllByContest(contest);
        } else {
            return cfApiHandler.getRatingChangesFromContest(contest.getId())
                    .stream()
                    .map(c -> new RatingChange(contest, c)).toList();
        }
    }

    public void updateContest(Contest contest) {
        if (contestIsPresentAndUnrated(contest)) {
            log.info("Found unrated contest {}, skipping", contest.getId());
            return;
        }
        try {
            log.info("Contest {}", contest.getId());
            List<RatingChange> cfRatingChanges = fetchRatingUpdates(contest);
            contestRepository.save(contest);
            ratingChangeRepository.saveAll(cfRatingChanges);

            rankInfo.addContestWithUpdates(contest, this::fetchRatingUpdates);
        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            log.info("4xx error for contest {}, assuming it's unrated and ignoring from now on", contest.getId());
            contestRepository.save(contest.makeUnrated());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void checkContests() {
        List<Contest> contests = cfApiHandler.getAvailableContests()
                .stream()
                .map(Contest::new)
                .filter(c -> rankInfo.doesNotHaveContest(c.getId()))
                .sorted(Comparator.comparing(Contest::getStartTime)).toList();
        log.info("Adding contests: {}", contests.stream().map(Contest::getId).toList());
        for (Contest contest : contests) {
            if (rankInfo.doesNotHaveContest(contest.getId())) {
                log.info("Updating contest {}, startTime = {}", contest.getId(), contest.getStartTime());
                this.updateContest(contest);
            }
        }
    }
}

package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.RatingChange;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Answers the question "what global rank would a rating R be after contest C"?
public class RankInfo {
    private static final int MIN_RATING = -100;
    private static final int MAX_RATING = 5000; // add some buffer for tourist :)

    private static int clamp(int x) {
        return Math.max(MIN_RATING, Math.min(MAX_RATING, x));
    }
    private final Map<Integer, Map<Integer, Integer>> contestIdToRanks;
    private final Map<String, Integer> handleToCurrentRating;
    @Getter
    private Contest lastContest;

    private static Map<Integer, Integer> rankToFreq(Map<Integer, Integer> e) {
        Map<Integer, Integer> f = new HashMap<>(e);
        for (int i = MAX_RATING - 1; i >= MIN_RATING; i--) {
            f.put(i, e.get(i) - e.get(i + 1));
        }
        return f;
    }

    private static Map<Integer, Integer> freqToRank(Map<Integer, Integer> f) {
        Map<Integer, Integer> e = new HashMap<>(f);
        for (int i = MAX_RATING - 1; i >= MIN_RATING; i--) {
            e.put(i, e.get(i + 1) + e.get(i));
        }
        return e;
    }

    public RankInfo() {
        this.contestIdToRanks = new HashMap<>();
        this.handleToCurrentRating = new HashMap<>();
    }

    public boolean hasContest(Integer contestId) {
        return contestIdToRanks.containsKey(contestId);
    }

    public Integer getCurrentRating(String handle) {
        return handleToCurrentRating.get(handle);
    }

    public void addContest(Contest contest, List<RatingChange> cfRatingChanges) {
        assert (!contestIdToRanks.containsKey(contest.getId()));
        assert (lastContest == null || contest.getStartTime() >= lastContest.getStartTime());
        Map<Integer, Integer> frequencies;
        if (lastContest != null) {
            frequencies = rankToFreq(contestIdToRanks.get(lastContest.getId()));
        } else {
            frequencies = new HashMap<>();
            for (int i = MIN_RATING; i <= MAX_RATING; i++) frequencies.put(i, 0);
        }
        for (RatingChange r : cfRatingChanges) {
            Integer oldRating = clamp(r.getOldRating());
            Integer newRating = clamp(r.getNewRating());
            if (handleToCurrentRating.containsKey(r.getHandle())) {
                frequencies.put(oldRating, frequencies.get(oldRating) - 1);
            }
            frequencies.put(newRating, frequencies.get(newRating) + 1);

            handleToCurrentRating.put(r.getHandle(), newRating);
        }
        Map<Integer, Integer> newRanks = freqToRank(frequencies);

        contestIdToRanks.put(contest.getId(), newRanks);
        this.lastContest = contest;
    }

    public Integer queryRank(Integer contestId, Integer rating) {
        return contestIdToRanks.get(contestId).get(rating);
    }
}

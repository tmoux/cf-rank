package com.timothymou.cfrank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import lombok.Getter;

// Answers the question "what global rank would rating R be after contest C"?
public class RankInfo {
    private static int clamp(int x) {
        return Math.max(MIN_RATING, Math.min(MAX_RATING, x));
    }

    private static final int MIN_RATING = -100;
    private static final int MAX_RATING = 5000; // add some buffer for tourist :)
    private final Map<Integer, Map<Integer, Integer>> contestIdToRanks;
    private final Map<String, Integer> handleToCurrentRating;
    @Getter
    private Integer lastContestId;

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

    public void addContest(Integer contestId, List<CfRatingChange> cfRatingChanges) {
        assert (!contestIdToRanks.containsKey(contestId));
        Map<Integer, Integer> frequencies;
        if (lastContestId != null) {
            frequencies = rankToFreq(contestIdToRanks.get(lastContestId));
        } else {
            frequencies = new HashMap<>();
            for (int i = MIN_RATING; i <= MAX_RATING; i++) frequencies.put(i, 0);
        }
        for (CfRatingChange r : cfRatingChanges) {
            Integer oldRating = clamp(r.getOldRating());
            Integer newRating = clamp(r.getNewRating());
            if (handleToCurrentRating.containsKey(r.getHandle())) {
                frequencies.put(oldRating, frequencies.get(oldRating) - 1);
            }
            frequencies.put(newRating, frequencies.get(newRating) + 1);

            handleToCurrentRating.put(r.getHandle(), newRating);
        }
        Map<Integer, Integer> newRanks = freqToRank(frequencies);

        contestIdToRanks.put(contestId, newRanks);
        this.lastContestId = contestId;
    }

    public Integer queryRank(Integer contestId, Integer rating) {
        return contestIdToRanks.get(contestId).get(rating);
    }
}
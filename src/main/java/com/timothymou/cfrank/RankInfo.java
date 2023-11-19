package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.RatingChange;

import java.util.*;
import java.util.function.Function;

// Answers the question "what global rank would a rating R be after contest C"?
public class RankInfo {
    private static final int MIN_RATING = -100;
    private static final int MAX_RATING = 4200; // add some buffer for tourist :)

    private static int clamp(int x) {
        return Math.max(MIN_RATING, Math.min(MAX_RATING, x));
    }

    private final Map<Integer, List<Integer>> contestIdToRanks;
    private final Map<String, Integer> handleToCurrentRating;

    private final Deque<Contest> contestStack;
    private final Map<String, Contest> firstContest;

    public Contest getLastContest() {
        return contestStack.isEmpty() ? null : contestStack.peekFirst();
    }

    private static ArrayList<Integer> rankToFreq(List<Integer> e) {
        ArrayList<Integer> f = new ArrayList<>(e);
        for (int i = MAX_RATING - 1; i >= MIN_RATING; i--) {
            f.set(i - MIN_RATING, e.get(i - MIN_RATING) - e.get(i + 1 - MIN_RATING));
        }
        return f;
    }

    private static ArrayList<Integer> freqToRank(List<Integer> f) {
        ArrayList<Integer> e = new ArrayList<>(f);
        for (int i = MAX_RATING - 1; i >= MIN_RATING; i--) {
            e.set(i - MIN_RATING, e.get(i + 1 - MIN_RATING) + e.get(i - MIN_RATING));
        }
        return e;
    }

    public RankInfo() {
        this.contestIdToRanks = new HashMap<>();
        this.handleToCurrentRating = new HashMap<>();
        this.contestStack = new ArrayDeque<>();
        this.firstContest = new HashMap<>();
    }

    public boolean doesNotHaveContest(Integer contestId) {
        return !contestIdToRanks.containsKey(contestId);
    }

    public Integer getCurrentRating(String handle) {
        return handleToCurrentRating.get(handle);
    }

    // addContest assumes that contest is the absolute latest contest in the contestIdToRanks
    // Adds contest to contestStack
    public void addContest(Contest contest, List<RatingChange> ratingChanges) {
        assert (!contestIdToRanks.containsKey(contest.getId()));
        assert (contestStack.isEmpty() || contest.getStartTime() >= contestStack.peekFirst().getStartTime());
        ArrayList<Integer> frequencies;
        if (!contestStack.isEmpty()) {
            frequencies = rankToFreq(contestIdToRanks.get(contestStack.peekFirst().getId()));
        } else {
            frequencies = new ArrayList<>(Collections.nCopies(MAX_RATING - MIN_RATING + 1, 0));
        }
        for (RatingChange r : ratingChanges) {
            int oldRating = clamp(r.getOldRating());
            int newRating = clamp(r.getNewRating());
            if (!firstContest.containsKey(r.getHandle()) ||
                    r.getContest().getStartTime() <= firstContest.get(r.getHandle()).getStartTime()) {
                firstContest.put(r.getHandle(), r.getContest());
            } else {
                frequencies.set(oldRating - MIN_RATING, frequencies.get(oldRating - MIN_RATING) - 1);
            }
            frequencies.set(newRating - MIN_RATING, frequencies.get(newRating - MIN_RATING) + 1);
            handleToCurrentRating.put(r.getHandle(), newRating);
        }
        List<Integer> newRanks = freqToRank(frequencies);
        contestIdToRanks.put(contest.getId(), newRanks);
        contestStack.addFirst(contest);
    }

    // Add a contest, possibly not in time order.
    // To update rankInfo, we pop off all later contests and recompute them.
    public void addContestWithUpdates(Contest contest, Function<Contest, List<RatingChange>> contestToChanges) {
        Deque<Contest> contestsToAdd = new ArrayDeque<>();
        while (!contestStack.isEmpty() && contestStack.peekFirst().getStartTime() > contest.getStartTime()) {
            Contest lastContest = contestStack.removeFirst();
            contestIdToRanks.remove(lastContest.getId());
            contestsToAdd.addFirst(lastContest);
        }
        contestsToAdd.addFirst(contest);
        for (Contest c : contestsToAdd) {
            addContest(c, contestToChanges.apply(c));
        }
    }

    public Integer queryRank(Integer contestId, Integer rating) {
        return contestIdToRanks.get(contestId).get(rating - MIN_RATING);
    }

    public boolean hasHandle(String handle) {
        return handleToCurrentRating.containsKey(handle);
    }
}

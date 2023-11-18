package com.timothymou.cfrank.cfapi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SampleContestData {
    private static final CfRatingChange ra1 = new CfRatingChange(1, "A", 1500, 1600);
    private static final CfRatingChange rb1 = new CfRatingChange(1, "B", 1500, 1700);
    private static final CfRatingChange rc1 = new CfRatingChange(1, "C", 1500, 1500);
    // 1500 1600 1700
    public static final List<CfRatingChange> contest1 = List.of(ra1, rb1, rc1);
    public static final Contest C1 = new Contest(1, 1L);
    public static final List<RatingChange> contest1Parsed = contest1.stream().map(c -> new RatingChange(C1 , c)).toList();

    private static final CfRatingChange ra2 = new CfRatingChange(2, "A", 1600, 2000);
    private static final CfRatingChange rb2 = new CfRatingChange(2, "B", 1700, 1500);
    private static final CfRatingChange rd2 = new CfRatingChange(2, "D", 1500, 2500);
    // 1500 1500 2000 2500
    public static final List<CfRatingChange> contest2 = List.of(ra2, rb2, rd2);
    public static final Contest C2 = new Contest(2, 2L);
    public static final List<RatingChange> contest2Parsed = contest2.stream().map(c -> new RatingChange(C2 , c)).toList();

    private static final CfRatingChange ra3 = new CfRatingChange(3, "A", 2000, -10);
    // -10 1500 1500 2500
    public static final List<CfRatingChange> contest3 = List.of(ra3);
    public static final Contest C3 = new Contest(3, 3L);
    public static final List<RatingChange> contest3Parsed = contest3.stream().map(c -> new RatingChange(C3 , c)).toList();


}

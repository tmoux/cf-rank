package com.timothymou.cfrank.cfapi;

import java.util.Arrays;
import java.util.List;

public class SampleContestData {
    private static final CfRatingChange ra1 = new CfRatingChange(1, "A", 1500, 1600);
    private static final CfRatingChange rb1 = new CfRatingChange(1, "B", 1500, 1700);
    private static final CfRatingChange rc1 = new CfRatingChange(1, "C", 1500, 1500);
    // 1500 1600 1700
    public static final List<CfRatingChange> contest1 = Arrays.asList(ra1, rb1, rc1);

    private static final CfRatingChange ra2 = new CfRatingChange(2, "A", 1600, 2000);
    private static final CfRatingChange rb2 = new CfRatingChange(2, "B", 1700, 1500);
    private static final CfRatingChange rd2 = new CfRatingChange(2, "D", 1500, 2500);
    // 1500 1500 2000 2500
    public static final List<CfRatingChange> contest2 = Arrays.asList(ra2, rb2, rd2);

    private static final CfRatingChange ra3 = new CfRatingChange(3, "A", 2000, -10);
    // -10 1500 1500 2500
    public static final List<CfRatingChange> contest3 = Arrays.asList(ra3);


}

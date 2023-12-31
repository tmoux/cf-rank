package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.SampleContestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RankInfoTest {
    @Test
    public void rankInfoTest() {
        RankInfo rankInfo = new RankInfo();
        // Contest 1
        // 1500 1600 1700
        Contest C1 = new Contest(1, 1L);
        rankInfo.addContest(C1, SampleContestData.contest1Parsed);

        assertThat(rankInfo.queryRank(1, 1700)).isEqualTo(1);
        assertThat(rankInfo.queryRank(1, 1699)).isEqualTo(1);
        assertThat(rankInfo.queryRank(1, 1600)).isEqualTo(2);
        assertThat(rankInfo.queryRank(1, 1500)).isEqualTo(3);

        // Contest 2
        // 1500 1500 2000 2500
        Contest C2 = new Contest(2, 2L);
        rankInfo.addContest(C2, SampleContestData.contest2Parsed);
        assertThat(rankInfo.queryRank(2, 3000)).isEqualTo(0);
        assertThat(rankInfo.queryRank(2, 2500)).isEqualTo(1);
        assertThat(rankInfo.queryRank(2, 2000)).isEqualTo(2);
        assertThat(rankInfo.queryRank(2, 1500)).isEqualTo(4);
        assertThat(rankInfo.queryRank(2, 1300)).isEqualTo(4);

        // Contest 3
        Contest C3 = new Contest(3, 3L);
        rankInfo.addContest(C3, SampleContestData.contest3Parsed);
        // -10 1500 1500 2500
        assertThat(rankInfo.queryRank(3, -10)).isEqualTo(4);
    }
}

package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import com.timothymou.cfrank.cfapi.SampleContestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HandleControllerTest {
    @MockBean
    private ICfApiHandler mockCfApiHandler;

    @Autowired
    private HandleController handleController;

    @Autowired
    private ContestUpdater contestController;

    @Test
    public void testGetHandleInfo() {
        when(mockCfApiHandler.getRatingChangesFromContest(1)).thenReturn(Optional.of(SampleContestData.contest1));
        Contest c1 = new Contest(1, 1L);
        contestController.updateContest(c1);
        verify(mockCfApiHandler).getRatingChangesFromContest(1);

        List<ContestRankUpdate> expectedA1 = List.of(new ContestRankUpdate(c1, 2, 1600));
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA1);


        when(mockCfApiHandler.getRatingChangesFromContest(2)).thenReturn(Optional.of(SampleContestData.contest2));
        Contest c2 = new Contest(2, 2L);
        contestController.updateContest(c2);
        verify(mockCfApiHandler).getRatingChangesFromContest(2);

        List<ContestRankUpdate> expectedA2 = List.of(
                new ContestRankUpdate(c1, 2, 1600),
                new ContestRankUpdate(c2, 2, 2000));
        List<ContestRankUpdate> expectedB2 = List.of(
                new ContestRankUpdate(c1, 1, 1700),
                new ContestRankUpdate(c2, 4, 1500));
        List<ContestRankUpdate> expectedC2 = List.of(
                new ContestRankUpdate(c1, 3, 1500),
                new ContestRankUpdate(c2, 4, 1500));
        List<ContestRankUpdate> expectedD2 = List.of(
                new ContestRankUpdate(c2, 1, 2500));
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA2);
        assertThat(handleController.getHandle("B")).isEqualTo(expectedB2);
        assertThat(handleController.getHandle("C")).isEqualTo(expectedC2);
        assertThat(handleController.getHandle("D")).isEqualTo(expectedD2);
        assertThat(handleController.getHandle("notARealHandle")).isEqualTo(List.of());

        when(mockCfApiHandler.getRatingChangesFromContest(3)).thenReturn(Optional.of(SampleContestData.contest3));
        Contest c3 = new Contest(3, 3L);
        contestController.updateContest(c3);
        verify(mockCfApiHandler).getRatingChangesFromContest(3);

        List<ContestRankUpdate> expectedA3 = List.of(
                new ContestRankUpdate(c1, 2, 1600),
                new ContestRankUpdate(c2, 2, 2000),
                new ContestRankUpdate(c3, 4, -10));
        List<ContestRankUpdate> expectedD3 = List.of(
                new ContestRankUpdate(c2, 1, 2500),
                new ContestRankUpdate(c3, 1, 2500));
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA3);
        assertThat(handleController.getHandle("D")).isEqualTo(expectedD3);
    }
}

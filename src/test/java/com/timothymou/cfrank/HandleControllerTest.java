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
        contestController.updateContest(new Contest(1, 1L));
        // mockCfApiHandler.getRatingChangesFromContest(1);
        verify(mockCfApiHandler).getRatingChangesFromContest(1);
        // verify(mockCfApiHandler).getAvailableContests();

        List<Integer> expectedA1 = List.of(2);
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA1);

        when(mockCfApiHandler.getRatingChangesFromContest(2)).thenReturn(Optional.of(SampleContestData.contest2));
        contestController.updateContest(new Contest(2, 2L));
        verify(mockCfApiHandler).getRatingChangesFromContest(2);

        List<Integer> expectedA2 = List.of(2, 2);
        List<Integer> expectedB2 = List.of(1, 4);
        List<Integer> expectedC2 = List.of(3, 4);
        List<Integer> expectedD2 = List.of(1);
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA2);
        assertThat(handleController.getHandle("B")).isEqualTo(expectedB2);
        assertThat(handleController.getHandle("C")).isEqualTo(expectedC2);
        assertThat(handleController.getHandle("D")).isEqualTo(expectedD2);
        assertThat(handleController.getHandle("notARealHandle")).isEqualTo(List.of());

        when(mockCfApiHandler.getRatingChangesFromContest(3)).thenReturn(Optional.of(SampleContestData.contest3));
        contestController.updateContest(new Contest(3, 3L));
        verify(mockCfApiHandler).getRatingChangesFromContest(3);

        List<Integer> expectedA3 = List.of(2, 2, 4);
        List<Integer> expectedD3 = List.of(1, 1);
        assertThat(handleController.getHandle("A")).isEqualTo(expectedA3);
        assertThat(handleController.getHandle("D")).isEqualTo(expectedD3);
    }
}

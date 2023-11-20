package com.timothymou.cfrank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timothymou.cfrank.cfapi.*;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.timothymou.cfrank.cfapi.SampleContestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ContestUpdaterTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    @SpyBean
    private ContestUpdater contestUpdater;

    @Autowired
    @SpyBean
    private RatingChangeRepository ratingChangeRepository;

    @Autowired
    @SpyBean
    private ContestRepository contestRepository;

    @MockBean
    ICfApiHandler cfApiHandler;

    @Autowired
    private RankInfo rankInfo;

    @Test
    public void updateContestSucceeds() throws IOException {
        File cf1889File = new ClassPathResource("1889.json").getFile();
        List<CfRatingChange> cf1889List = objectMapper.readValue(cf1889File, CfRatingChangeList.class).result();

        Contest contest = new Contest(1889, 0L);

        when(cfApiHandler.getRatingChangesFromContest(contest.getId())).thenReturn(cf1889List);

        contestUpdater.updateContest(contest);
        verify(contestRepository).save(contest);
        verify(ratingChangeRepository).saveAll(Mockito.any());

        assertThat(ratingChangeRepository.findByHandle("Benq")).hasSize(1);
        assertThat(ratingChangeRepository.findByHandle("Geothermal")).hasSize(1);
        assertThat(ratingChangeRepository.findByHandle("notARealHandle")).hasSize(0);
    }

    @Test
    public void checkContestsSucceeds() {
        List<CfContest> contests = List.of(
                new CfContest(1, "CF", 5L, "FINISHED"),
                new CfContest(2, "CF", 3L, "FINISHED"),
                new CfContest(3, "CF", 10L, "FINISHED"),
                new CfContest(4, "CF", 2L, "FINISHED"),
                new CfContest(5, "CF", 0L, "BEFORE"));
        when(cfApiHandler.getAvailableContests()).thenReturn(contests);
        when(cfApiHandler.getRatingChangesFromContest(Mockito.any())).thenReturn(List.of());

        contestUpdater.checkContests();

        InOrder orderVerifier = Mockito.inOrder(contestUpdater);
        orderVerifier.verify(contestUpdater).updateContest(new Contest(4, 2L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(2, 3L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(1, 5L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(3, 10L));
    }

    @Test
    public void checkUpdatingOutOfOrder() {
        when(cfApiHandler.getRatingChangesFromContest(1)).thenReturn(contest1);
        when(cfApiHandler.getRatingChangesFromContest(2)).thenReturn(contest2);
        when(cfApiHandler.getRatingChangesFromContest(3)).thenReturn(contest3);

        contestUpdater.updateContest(C3);
        contestUpdater.updateContest(C2);
        contestUpdater.updateContest(C1);

        assertThat(rankInfo.queryRank(3, -10)).isEqualTo(4);
        assertThat(rankInfo.queryRank(3, 2500)).isEqualTo(1);
        assertThat(rankInfo.queryRank(2, 1500)).isEqualTo(4);
        assertThat(rankInfo.queryRank(2, 2500)).isEqualTo(1);
        assertThat(rankInfo.queryRank(1, 1500)).isEqualTo(3);

    }

    @Test
    public void unratedContestsAreIgnored() {
        when(cfApiHandler.getRatingChangesFromContest(1)).thenReturn(contest1);
        when(cfApiHandler.getRatingChangesFromContest(2)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        contestUpdater.updateContest(C1);
        contestUpdater.updateContest(C2);
        contestUpdater.updateContest(C2);

        verify(contestRepository).save(C1);
        verify(contestRepository).save(C2.makeUnrated());
    }
}
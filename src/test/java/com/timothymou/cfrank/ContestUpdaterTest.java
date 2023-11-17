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
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
}
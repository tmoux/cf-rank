package com.timothymou.cfrank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.CfRatingChangeList;
import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
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

        when(cfApiHandler.getRatingChangesFromContest(1889)).thenReturn(Optional.of(cf1889List));

        Contest contest = new Contest(1889, 0L);
        contestUpdater.updateContest(contest);

        verify(contestRepository).save(contest);
        verify(ratingChangeRepository).saveAll(Mockito.any());

        assertThat(ratingChangeRepository.findByHandle("Benq")).hasSize(1);
        assertThat(ratingChangeRepository.findByHandle("Geothermal")).hasSize(1);
        assertThat(ratingChangeRepository.findByHandle("notARealHandle")).hasSize(0);
    }

    @Test
    public void checkContestsSucceeds() {
        List<Contest> contests = List.of(
                new Contest(1, 5L),
                new Contest(2, 3L),
                new Contest(3, 10L),
                new Contest(4, 2L));
        when(cfApiHandler.getAvailableContests()).thenReturn(contests);
        when(cfApiHandler.getRatingChangesFromContest(Mockito.any())).thenReturn(Optional.empty());

        contestUpdater.checkContests();

        InOrder orderVerifier = Mockito.inOrder(contestUpdater);
        orderVerifier.verify(contestUpdater).updateContest(new Contest(4, 2L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(2, 3L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(1, 5L));
        orderVerifier.verify(contestUpdater).updateContest(new Contest(3, 10L));
    }
}
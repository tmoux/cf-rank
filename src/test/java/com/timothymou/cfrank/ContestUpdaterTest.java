package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.Contest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ContestUpdaterTest {
    @Autowired
    private RatingChangeRepository repository;

    @Autowired
    private ContestUpdater controller;

    @Autowired
    @SpyBean
    private ContestRepository contestRepository;

    @Test
    public void updateContestSucceeds() {
        // TODO: instead of calling actual API, using MockBean with downloaded test data
        Contest contest = new Contest(1889, 0L);
        controller.updateContest(contest);

        verify(contestRepository).save(contest);

        assertThat(repository.findByHandle("Benq")).hasSize(1);
        assertThat(repository.findByHandle("Geothermal")).hasSize(1);
        assertThat(repository.findByHandle("notARealHandle")).hasSize(0);
    }
}

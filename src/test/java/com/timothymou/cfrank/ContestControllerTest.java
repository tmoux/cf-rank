package com.timothymou.cfrank;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContestControllerTest {
    @Autowired
    private RatingChangeRepository repository;

    @Autowired
    private ContestUpdater controller;

    @Test
    public void updateContestSucceeds() {
        controller.updateContest(1889);
        assertThat(repository.findByHandle("Benq")).hasSize(1);
        assertThat(repository.findByHandle("Geothermal")).hasSize(1);
        assertThat(repository.findByHandle("notARealHandle")).hasSize(0);
    }
}

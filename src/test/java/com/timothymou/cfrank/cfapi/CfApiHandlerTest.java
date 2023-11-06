package com.timothymou.cfrank.cfapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.web.client.RestTemplate;

public class CfApiHandlerTest {
  ICfApiHandler apiHandler = new CfApiHandler(new RestTemplate());

  @Test
  public void getRatingChangesFromContestSucceeds() {
    Contest contest = new Contest(1889, 0L);
    List<CfRatingChange> cfRatingChanges = apiHandler.getRatingChangesFromContest(1889).get();
    CfRatingChange r1 = new CfRatingChange(1889, "Benq", 3724, 3833);
    CfRatingChange r2 = new CfRatingChange(1889, "Petr", 3134, 3333);
    CfRatingChange r3 = new CfRatingChange(1889, "ecnerwala", 3354, 3466);
    assertThat(cfRatingChanges.get(0)).isEqualTo(r1);
    assertThat(cfRatingChanges.get(1)).isEqualTo(r2);
    assertThat(cfRatingChanges.get(2)).isEqualTo(r3);
  }

  @Test
  public void getAvailableContestsSucceeds() {
    List<CfContest> contests = apiHandler.getAvailableContests();
    assertThat(contests).hasSizeGreaterThan(1300);
  }
}

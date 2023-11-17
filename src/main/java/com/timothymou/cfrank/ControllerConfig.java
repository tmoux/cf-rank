package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfApiHandler;
import com.timothymou.cfrank.cfapi.Contest;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import com.timothymou.cfrank.cfapi.RatingChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class ControllerConfig {
    @Bean
    public RankInfo rankInfo(ContestRepository contestRepository, RatingChangeRepository ratingChangeRepository) {
        List<Contest> contests = contestRepository.findAllByOrderByStartTimeAsc();
        log.info("Initial contests: {}", contests);
        RankInfo rankInfo = new RankInfo();
        for (Contest contest: contests) {
            List<RatingChange> ratingChanges = ratingChangeRepository.findAllByContest(contest);
            rankInfo.addContest(contest, ratingChanges);
        }
        return rankInfo;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ICfApiHandler cfApiHandler(RestTemplate restTemplate) {
        return new CfApiHandler(restTemplate);
    }

    @Bean
    public ScheduledUpdater scheduledUpdater(ContestUpdater contestUpdater) {
        return new ScheduledUpdater(contestUpdater);
    }
}

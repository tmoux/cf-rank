package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfApiHandler;
import com.timothymou.cfrank.cfapi.ICfApiHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ControllerConfig {
    @Bean
    public RankInfo rankInfo() {
        return new RankInfo();
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

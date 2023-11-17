package com.timothymou.cfrank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timothymou.cfrank.cfapi.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduledUpdaterTest {
    @LocalServerPort
    private int port;

    @Autowired
    ScheduledUpdater scheduledUpdater;

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ICfApiHandler cfApiHandler;

    @SpyBean
    ContestUpdater contestUpdater;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testScheduler() throws IOException {
        List<CfRatingChange> cf1887List = objectMapper.readValue(
                new ClassPathResource("1887.json").getFile(),
                CfRatingChangeList.class).result();
        when(cfApiHandler.getRatingChangesFromContest(1887)).thenReturn(cf1887List);
        List<CfRatingChange> cf1888List = objectMapper.readValue(
                new ClassPathResource("1888.json").getFile(),
                CfRatingChangeList.class).result();
        when(cfApiHandler.getRatingChangesFromContest(1888)).thenReturn(cf1888List);

        List<CfRatingChange> cf1889List = objectMapper.readValue(
                new ClassPathResource("1889.json").getFile(),
                CfRatingChangeList.class).result();
        when(cfApiHandler.getRatingChangesFromContest(1889)).thenReturn(cf1889List);

        List<CfRatingChange> cf1890List = objectMapper.readValue(
                new ClassPathResource("1890.json").getFile(),
                CfRatingChangeList.class).result();
        when(cfApiHandler.getRatingChangesFromContest(1890)).thenReturn(cf1890List);

        List<CfRatingChange> cf1895List = objectMapper.readValue(
                new ClassPathResource("1895.json").getFile(),
                CfRatingChangeList.class).result();
        when(cfApiHandler.getRatingChangesFromContest(1895)).thenReturn(cf1895List);

        List<CfContest> contests = List.of(
                new CfContest(1895, "CF", 1699029300L, "FINISHED"),
                new CfContest(1887, "CF", 1697972700L, "FINISHED"),
                new CfContest(1888, "CF", 1697979900L, "FINISHED"),
                new CfContest(1889, "CF", 1698512700L, "FINISHED"),
                new CfContest(1890, "CF", 1698512700L, "FINISHED")
        );
        when(cfApiHandler.getAvailableContests()).thenReturn(contests);

        // TODO: move these into separate file
        // Refactor test

        scheduledUpdater.checkForNewContests();
        scheduledUpdater.checkForNewContests();
        verify(contestUpdater, times(5)).updateContest(any());

        ResponseEntity<List<ContestRankUpdate>> response = restTemplate.exchange(
                "http://localhost:" + port + "/gethandle?handle=rainboy",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of(
                new ContestRankUpdate(new Contest(1890, 1698512700L), 808, 2116),

                new ContestRankUpdate(new Contest(1895, 1699029300L), 845, 2116)
        ));
    }
}

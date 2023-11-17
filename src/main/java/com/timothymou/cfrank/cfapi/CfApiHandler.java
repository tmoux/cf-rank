package com.timothymou.cfrank.cfapi;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class CfApiHandler implements ICfApiHandler {
    private final RateLimiter rateLimiter = RateLimiter.create(0.4);
    private final RestTemplate restTemplate;

    public CfApiHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<CfRatingChange> getRatingChangesFromContest(Integer contestId) throws HttpClientErrorException, HttpServerErrorException {
        rateLimiter.acquire();
        String url = String.format("https://codeforces.com/api/contest.ratingChanges?contestId=%d", contestId);
        ResponseEntity<CfRatingChangeList> response = restTemplate.getForEntity(url, CfRatingChangeList.class);
        return response.getBody().result();
    }

    @Override
    public List<CfContest> getAvailableContests() {
        rateLimiter.acquire();
        String url = "https://codeforces.com/api/contest.list";
        try {
            ResponseEntity<CfContestList> response = restTemplate.getForEntity(url, CfContestList.class);
            List<CfContest> contests = response.getBody().result();
            return contests.stream()
                    .filter(c -> c.phase().equals("FINISHED"))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            return List.of();
        }
    }
}

package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CfRankApplication {
  private static final Logger log = LoggerFactory.getLogger(CfRankApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CfRankApplication.class, args);
  }

/*  // @Bean
  public CommandLineRunner demo(RatingChangeRepository repository) {
    return args -> {
      repository.save(new CfRatingChange(1000, "silxi", 1200, 1250));
      repository.save(new CfRatingChange(1000, "abc", 3000, 2500));
      repository.save(new CfRatingChange(1100, "silxi", 1250, 1300));

      log.info("RATING CHANGES found with findAll()");
      repository.findAll().forEach(change -> log.info(change.toString()));
      log.info("");
      repository.findByHandle("silxi").forEach(change -> log.info(change.toString()));
    };
  }*/
}

/*
How to add information from a contest to the rankInfo class (add as a bean?)
  - Initialize it from repository? Or save it between sessions?
    - Might need to order contests by their date in former case.
  - Might need repository of contests and their start times to handle ordering.
  - How to use authentication? Or do some automatic updates daily to look for new contests.

  Repositories:
  - List of rating changes
  - Contests and start times
  - Table of ranks for each rating after each contest (need to save or no?)

How to query a handle's ranks
  - Main goal: limit API calls at all costs. Don't need to make separate API call for handle,
    just look up handle's rating changes, then compute ranks using rankInfo
  - Caching for a handle/at a particular point in time? Probably not worth it.

How to connect to a website/browser extension


Stretch goals:
  - Handle inactive users (ignored for now)
  -

API interface:
- PUT /checkcontests: Check for new contest updates

- PUT /updatecontest?contest={id}: Update a new contest (ID)

- GET /gethandle?handle={handle}: Get a handle's ranks (active/inactive)

TODO:
- Add repository for contests and start time (in HandleController get function)
- Ensure contests are processed in order of start time
- Write http API tests? (random port, mockmvc)?
- Make sure RankInfo handles out-of-order updates properly (probably just recompute everything)
 */

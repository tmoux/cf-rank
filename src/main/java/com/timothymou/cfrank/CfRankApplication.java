package com.timothymou.cfrank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
- Cache unrated contests so we don't needlessly query them again.

- Add handler for root, simple web page UI



- Write http API tests? (random port, mockmvc)?
  - Ensure contests are processed in order of start time
- Should we combine Div. 1/ Div. 2 contests?

- Refactor RankInfo offset-list into its own class (parameterized by offset/min rating)

 */

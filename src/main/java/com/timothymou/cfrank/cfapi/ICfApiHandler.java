package com.timothymou.cfrank.cfapi;

import java.util.List;
import java.util.Optional;

// Note: there is a rating history API call, but we don't use it in order to limit unnecessary API
// calls.
// Instead, we store our own table of rating changes and SELECT the relevant ones for a user.
// This way, we only need to call the API to get the rating changes (about 30000 per contest) and
// derive the global rank information from that.
public interface ICfApiHandler {
  public Optional<List<CfRatingChange>> getRatingChangesFromContest(Integer contestId);

  public List<Contest> getAvailableContests();
}

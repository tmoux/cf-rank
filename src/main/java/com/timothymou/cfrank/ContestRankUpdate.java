package com.timothymou.cfrank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.timothymou.cfrank.cfapi.Contest;

// Represents the rank of a user after a contest.

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContestRankUpdate(Contest contest, Integer rank, Integer rating) {
}

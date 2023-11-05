package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfContest;
import com.timothymou.cfrank.cfapi.Contest;

// Represents the rank of a user after a contest.
public record ContestRankUpdate(Contest contest, Integer rank) {
}

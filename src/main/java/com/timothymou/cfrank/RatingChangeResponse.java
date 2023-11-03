package com.timothymou.cfrank;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.timothymou.cfrank.cfapi.CfRatingChange;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RatingChangeResponse(String status, List<CfRatingChange>result) {}

package com.timothymou.cfrank.cfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CfContest(@Id Integer id, String type, Long startTimeSeconds, String phase) {}

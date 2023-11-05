package com.timothymou.cfrank.cfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;


@JsonIgnoreProperties(ignoreUnknown = true)
public record CfContest(Integer id, String type, Long startTimeSeconds, String phase) {}

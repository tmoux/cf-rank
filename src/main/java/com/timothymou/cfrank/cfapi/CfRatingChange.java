package com.timothymou.cfrank.cfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class CfRatingChange {
  private Integer contestId;
  private String handle;
  private Integer oldRating;
  private Integer newRating;

  protected CfRatingChange() {}
}

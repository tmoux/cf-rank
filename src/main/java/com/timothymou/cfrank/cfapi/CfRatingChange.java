package com.timothymou.cfrank.cfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CfRatingChange {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Integer contestId;
  private String handle;
  private Integer oldRating;
  private Integer newRating;

  protected CfRatingChange() {}

  public CfRatingChange(Integer contestId, String handle, Integer oldRating, Integer newRating) {
    this.contestId = contestId;
    this.handle = handle;
    this.oldRating = oldRating;
    this.newRating = newRating;
  }

  @Override
  public String toString() {
    return String.format(
        "RatingChange[id = %d, contestId = %d, handle = %s, oldRating = %d, newRating = %d]",
        this.id, contestId, handle, oldRating, newRating);
  }
}

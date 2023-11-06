package com.timothymou.cfrank.cfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@EqualsAndHashCode
@Getter
@ToString
public class RatingChange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Contest contest;

    private String handle;
    private Integer oldRating;
    private Integer newRating;

    public Integer getContestId() {
        return contest.getId();
    }

    RatingChange() {}

    public RatingChange(Contest contest, String handle, Integer oldRating, Integer newRating) {
        this.contest = contest;
        this.handle = handle;
        this.oldRating = oldRating;
        this.newRating = newRating;
    }

    public RatingChange(Contest contest, CfRatingChange cfRatingChange) {
        this.contest = contest;
        this.handle = cfRatingChange.getHandle();
        this.oldRating = cfRatingChange.getOldRating();
        this.newRating = cfRatingChange.getNewRating();
    }
}

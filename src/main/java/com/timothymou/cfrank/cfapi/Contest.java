package com.timothymou.cfrank.cfapi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
@EqualsAndHashCode
public class Contest {
    @Id
    private Integer id;

    private Long startTime;

    Contest() {
    }

    public Contest(Integer id, Long startTime) {
        this.id = id;
        this.startTime = startTime;
    }

    Contest(CfContest c) {
        this(c.id(), c.startTimeSeconds());
    }
}

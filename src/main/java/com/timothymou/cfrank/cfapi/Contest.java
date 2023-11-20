package com.timothymou.cfrank.cfapi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Contest {
    @Id
    private Integer id;

    private Long startTime;
    private boolean rated;

    protected Contest() {
    }

    public Contest(Integer id, Long startTime) {
        this(id, startTime, true);
    }

    public Contest(CfContest c) {
        this(c.id(), c.startTimeSeconds());
    }
    public Contest makeUnrated() {
        return new Contest(this.getId(), this.getStartTime(), false);
    }
}

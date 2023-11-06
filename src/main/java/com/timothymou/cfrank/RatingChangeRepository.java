package com.timothymou.cfrank;

import java.util.List;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import com.timothymou.cfrank.cfapi.RatingChange;
import org.springframework.data.repository.CrudRepository;

public interface RatingChangeRepository extends CrudRepository<RatingChange, Long> {
    List<RatingChange> findByHandle(String handle);
}

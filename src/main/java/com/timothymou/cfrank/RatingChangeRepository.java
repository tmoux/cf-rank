package com.timothymou.cfrank;

import java.util.List;

import com.timothymou.cfrank.cfapi.CfRatingChange;
import org.springframework.data.repository.CrudRepository;

public interface RatingChangeRepository extends CrudRepository<CfRatingChange, Long> {
    List<CfRatingChange> findByHandle(String handle);
}

package com.timothymou.cfrank;

import com.timothymou.cfrank.cfapi.CfContest;
import com.timothymou.cfrank.cfapi.Contest;
import org.springframework.data.repository.CrudRepository;

public interface ContestRepository extends CrudRepository<Contest, Integer> {

}

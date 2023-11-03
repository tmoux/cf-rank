package com.timothymou.cfrank.cfapi;

import java.util.List;

public record CfRatingChangeResponse(String status, List<CfRatingChange> result) {

}

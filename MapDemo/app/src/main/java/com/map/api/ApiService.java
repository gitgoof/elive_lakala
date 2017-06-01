package com.map.api;

import com.map.bean.RoutePlan;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by xg on 2016/6/24.
 */
public interface ApiService {
    /**
     * 得到路线规划
     *
     * @return
     */
    @GET("service")
    Observable<RoutePlan> getRoutePlan(@QueryMap Map<String, String> options, @Query("optimize") boolean optimize);
}

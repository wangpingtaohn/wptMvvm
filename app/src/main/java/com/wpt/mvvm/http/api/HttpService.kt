package com.wpt.mvvm.http.api

import com.wpt.mvvm.bean.Banner
import com.wpt.mvvm.http.ResponseData
import retrofit2.http.GET

/**
 * Author: wpt
 * Time: 2022/8/4
 * @Desc：
 */
interface HttpService {

    @GET("banner/json")
    suspend fun getBanner(): ResponseData<List<Banner>>
}


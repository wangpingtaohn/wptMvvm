package com.wpt.mvvm.http.intercetor

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * author : wpt
 * date   : 2021/7/2816:23
 * desc   : 共用header参数
 */
class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("device", Build.MANUFACTURER + " " + Build.MODEL)
            .addHeader("platform", "android")
            .addHeader("Connection", "keep-alive")
            .addHeader("channel","")
            .build()
        return chain.proceed(builder.build())
    }
}
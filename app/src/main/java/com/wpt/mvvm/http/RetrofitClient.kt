package com.wpt.mvvm.http

import com.google.gson.GsonBuilder
import com.wpt.mvvm.base.BaseApplication
import com.wpt.mvvm.http.intercetor.HeaderInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Author: wpt
 * Time: 2022/8/4
 * @Desc：
 */
object RetrofitClient {

    private const val BASE_URL = "https://wanandroid.com/"

    private var retrofit: Retrofit? = null

    //缓存请求API对象
    private val apiCacheMap = ConcurrentHashMap<String, Any>()

    /*val service: HttpService by lazy {
        getRetrofit().create(HttpService::class.java)
    }*/

    /**
     * 网络请求的Api调用方法，不需要传入根路径
     */
    fun <T> getApi(serviceClass: Class<T>): T {
        val cacheApi = apiCacheMap[serviceClass.name]
        if (cacheApi != null) {
            return cacheApi as T
        }
        synchronized(apiCacheMap) {
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
            val apiClass = setRetrofitBuilder(retrofitBuilder).build().create(serviceClass)
            apiCacheMap[serviceClass.name] = apiClass!!
            return apiClass
        }
    }

    /**
     * 网络请求Api调用方法，传入根路径
     */
    fun <T> getApi(serviceClass: Class<T>, baseUrl: String): T {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getOkHttpClient())
        return setRetrofitBuilder(retrofitBuilder).build().create(serviceClass)
    }

    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
     */
    private fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        }
    }

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofit!!
    }


    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()

        val cacheFile = File(BaseApplication.context?.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50)// 50M 的缓存大小

        builder.run {
            cache(cache)
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            addInterceptor(HeaderInterceptor())
            retryOnConnectionFailure(true)//错误重连
            sslSocketFactory(SSLSocketUtil.getSslSocketFactory().sSLSocketFactory,
            SSLSocketUtil.getSslSocketFactory().trustManager)
        }

        return builder.build()
    }

}
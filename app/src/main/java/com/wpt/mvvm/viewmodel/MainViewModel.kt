package com.wpt.mvvm.viewmodel
import androidx.lifecycle.MutableLiveData
import com.wpt.mvvm.bean.Banner
import com.wpt.mvvm.http.ResponseData
import com.wpt.mvvm.http.api.HttpService
import com.wpt.mvvm.http.RetrofitClient
import com.zhongke.common.base.viewmodel.BaseViewModel

/**
 * Author: wpt
 * Time: 2022/8/4
 * @Desc：
 */
class MainViewModel : BaseViewModel() {

    private val bannerData by lazy {
        MutableLiveData<List<Banner>>()
    }

    fun testRequest(success: (ResponseData<List<Banner>>) -> Unit,
                    error: (Any) -> Unit
    ) {
        request({ RetrofitClient.getApi(HttpService::class.java).getBanner()},success,error);
    }
}
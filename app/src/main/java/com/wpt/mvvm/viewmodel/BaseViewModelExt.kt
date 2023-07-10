package com.wpt.mvvm.viewmodel

import androidx.lifecycle.viewModelScope
import com.wpt.mvvm.http.ResponseData
import com.zhongke.common.base.viewmodel.BaseViewModel
import kotlinx.coroutines.*


/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param success 成功回调
 * @param error 失败回调 可不给
 */
fun <T> BaseViewModel.request(
    block: suspend () -> ResponseData<T>,//请求体
    success: (T) -> Unit, //成功
    failed: (response:ResponseData<T>) -> Unit, //业务逻辑失败
    error: (Throwable) -> Unit = {} //网络请求错误
): Job  {
    return viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            if (it.errorCode == 0){
                success(it.data)
            } else {
                failed(it)
            }
        }.onFailure {
            error(it)
        }
    }
}

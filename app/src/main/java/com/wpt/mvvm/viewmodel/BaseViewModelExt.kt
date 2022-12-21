package com.wpt.mvvm.viewmodel

import androidx.lifecycle.viewModelScope
import com.zhongke.common.base.viewmodel.BaseViewModel
import kotlinx.coroutines.*

/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param success 成功回调
 * @param error 失败回调 可不给
 */
fun <T> BaseViewModel.launch(
    block: suspend () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit = {}
): Job  {
    return viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}

fun <T> BaseViewModel.request(
    block: suspend () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit = {}
): Job  {
    return viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}

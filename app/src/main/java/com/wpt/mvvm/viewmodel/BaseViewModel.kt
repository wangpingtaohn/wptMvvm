package com.zhongke.common.base.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.Serializable
import java.lang.Exception

/**
 *
 */
open class BaseViewModel @JvmOverloads constructor() :
    ViewModel(), LifecycleObserver,Serializable {

    private val error by lazy { MutableLiveData<Exception>() }
    private val finally by lazy { MutableLiveData<Int>() }



    //运行在UI线程的协程
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
        try {
            withTimeout(5000){
                block()
            }
        } catch (e: Exception) {
            error.value = e
        } finally {
            finally.value = 200
        }
    }
}
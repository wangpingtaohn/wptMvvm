package com.wpt.mvvm.http

/**
 * Author: wpt
 * Time: 2022/8/4
 * @Desc：
 */
//接受数据的基类
data class ResponseData<out T>(

    val errorCode: Int,
    val errorMsg: String,
    val data: T
)
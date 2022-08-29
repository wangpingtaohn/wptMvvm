package com.wpt.mvvm.bean

/**
 * Author: wpt
 * Time: 2022/8/4
 * @Desc：
 */

data class Banner(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)

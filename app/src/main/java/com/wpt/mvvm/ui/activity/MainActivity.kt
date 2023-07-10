package com.wpt.mvvm.ui.activity

import android.os.Bundle
import com.wpt.mvvm.R
import com.wpt.mvvm.BR
import com.wpt.mvvm.base.BaseActivity
import com.wpt.mvvm.databinding.ActivityMainBindingImpl
import com.wpt.mvvm.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBindingImpl, MainViewModel>() {

    override fun initView() {
        btnQuest.setOnClickListener {
            showLoading()
            viewModel.testRequest(
                {
                    tvContent.text = it.toString()
                    dismissLoading()
                },
                {
                    dismissLoading()
                    tvContent.text = it.toString()
                }
            )
        }
    }

    override fun loadData() {

    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main;
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }



}
package com.wpt.mvvm.ui.activity

import android.os.Bundle
import com.wpt.mvvm.R
import com.wpt.mvvm.BR
import com.wpt.mvvm.databinding.ActivityMainBindingImpl
import com.wpt.mvvm.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBindingImpl, MainViewModel>() {

    override fun initView(savedInstanceState: Bundle?) {

        binding.btnQuest.setOnClickListener {
            viewModel.testRequest(
                {
                    binding.tvContent.text = it.toString()
                },
                {
                    binding.tvContent.text = it.toString()
                }
            )
        }

    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main;
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


}
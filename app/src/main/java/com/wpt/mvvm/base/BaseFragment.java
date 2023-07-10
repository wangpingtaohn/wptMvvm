package com.wpt.mvvm.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.wpt.mvvm.ui.dialog.LoadingDialog;
import com.zhongke.common.base.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author wpt
 * @Date 2023/2/22-18:03
 * @desc
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends Fragment {
    protected String TAG;
    protected BaseApplication mApp;
    protected View mContentView;
    protected BaseActivity mActivity;
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    /**
     * 表示是否已经加载过了
     */
    private boolean isLoaded = false;
    /**
     * 表示是否可见
     */
    private boolean isVisible = false;

    private LoadingDialog mDialog;

    protected void showLoading(){
        if (getActivity() == null || getActivity().isDestroyed()|| getActivity().isFinishing()){
            return;
        }
        if (mDialog == null){
            mDialog = new LoadingDialog(getActivity());
        }
        mDialog.show();
    }

    protected void dismissLoading(){
        if (mDialog == null || getActivity() == null
                || getActivity().isDestroyed()|| getActivity().isFinishing()){
            return;
        }
        mDialog.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
        mActivity = (BaseActivity) getActivity();
    }

    /**
     * 配合ViewPager使用时的懒加载
     * FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
     * BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT：只有当前的fragment可见的时候，才会调用onResume方法，其他的只会调用到onStart方法
     */
    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        onLazyLoadOnce();
    }



    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    public void onLazyLoadOnce() {
        if (!isLoaded && isVisible){
            loadData();
            isLoaded = true;
        }
    }

    protected abstract void loadData();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(), container, false);
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            initViewDataBinding();
            onCreateObserver();
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        mContentView = binding.getRoot();
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    protected void onCreateObserver() {

    }
    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    protected abstract int initContentView();

    /* 注入绑定
     */
    private void initViewDataBinding() {
        viewModelId = initVariableId();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);
    }

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    protected abstract int initVariableId();

    protected abstract void initView();

    protected abstract void initListener();


}

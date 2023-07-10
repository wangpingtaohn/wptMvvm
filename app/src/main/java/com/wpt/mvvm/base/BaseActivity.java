package com.wpt.mvvm.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.wpt.mvvm.ui.dialog.LoadingDialog;
import com.zhongke.common.base.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author wpt
 * @Date 2023/2/22-18:05
 * @desc Activity基类
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {


    protected V binding;
    protected VM viewModel;
    protected int viewModelId;
    protected Bundle savedInstanceState;
    public static int screenW, screenH;

    private LoadingDialog mDialog;


    /**
     * Initialize the root layout
     *
     * @return the id of the layout
     */
    public abstract int initContentView(Bundle savedInstanceState);

    /**
     * Initialize the id of the ViewModel
     *
     * @return BR id
     */
    public abstract int initVariableId();

    /**
     *
     * Initialize layout and view control
     */
    protected abstract void initView();

    protected void initListener(){}

    protected abstract void loadData();

    protected void onCreateObserver() {

    }


    /********************************Lifecycle debugging*****************************************/
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenW = getWindowManager().getDefaultDisplay().getWidth();
        screenH = getWindowManager().getDefaultDisplay().getHeight();
        setOrientation();
        setStatusBar();
        initViewDataBinding(savedInstanceState);
        loadData();
        initView();
        initListener();
        onCreateObserver();
    }

    protected void setOrientation() { //固定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 默认都是沉浸栏，light主题
     */
    private void setStatusBar(){
        //关键代码,沉浸
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        //设置专栏栏和导航栏的底色，透明
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(Color.TRANSPARENT);
        }

        //设置沉浸后专栏栏和导航字体的颜色，
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(findViewById(android.R.id.content));
        if (controller != null){
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }
    }

    protected void setStatusBarColor(int colorRes){
        Window window = getWindow();
        //设置专栏栏和导航栏的底色，透明
        window.setStatusBarColor(colorRes);
    }

    protected void setDarkStatusBars(){
        //设置沉浸后专栏栏和导航字体的颜色，
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(findViewById(android.R.id.content));
        if (controller != null){
            controller.setAppearanceLightStatusBars(false);
            controller.setAppearanceLightNavigationBars(false);
        }
    }

    protected void showLoading(){
        if (isDestroyed()|| isFinishing()){
            return;
        }
        if (mDialog == null){
            mDialog = new LoadingDialog(this);
        }
        mDialog.show();
    }

    protected void dismissLoading(){
        if (mDialog == null || isFinishing() || isDestroyed()){
            return;
        }
        mDialog.dismiss();
    }

    /**
     * inject binding
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        viewModelId = initVariableId();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //If no generic parameter is specified, it defaults to BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        //Association ViewModel
        binding.setVariable(viewModelId, viewModel);
        //support LiveData bind xml，data change，UI will automatically update
        binding.setLifecycleOwner(this);
        //Let the ViewModel have the lifecycle sensing of the View
        getLifecycle().addObserver(viewModel);
    }



    /**
     * Create ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

}

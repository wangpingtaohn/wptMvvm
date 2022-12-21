package com.wpt.mvvm.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.wpt.mvvm.R;
import com.wpt.mvvm.utils.StatusBarUtil;
import com.zhongke.common.base.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {


    protected V binding;
    protected VM viewModel;
    protected int viewModelId;
    protected Bundle savedInstanceState;
    public static int screenW, screenH;

    /**
     *
     * Initialize layout and view control
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * Handle business logic, state restoration, etc.
     *
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);

    protected void onCreateObserver() {

    }

    /**
     * find View
     *
     * @param id   the id of the control
     * @param <VT> View type
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }


    /********************************Lifecycle debugging*****************************************/
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenW = getWindowManager().getDefaultDisplay().getWidth();
        screenH = getWindowManager().getDefaultDisplay().getHeight();
        initViewDataBinding(savedInstanceState);
        initView(savedInstanceState);
        initData(savedInstanceState);
        setStatusBar();
        onCreateObserver();
    }

    /**
     * inject binding
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        viewModelId = initVariableId();
        viewModel = initViewModel();
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
     * Initialize ViewModel
     *
     * @return ViewModel that inherits BaseViewModel
     */
    public VM initViewModel() {
        return null;
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

    /**
     * Set transparent status bar
     */
    protected void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (isStatusBarWhite()) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            StatusBarUtil.setLightMode(this);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setDarkMode(this);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0);
    }

    protected boolean isStatusBarWhite() {
        return false;
    }


    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null, null);
    }

    public void openActivityForeResult(Class<?> pClass, int requestCode) {
        openActivityFoReResult(pClass, null, requestCode);
    }

    public void openActivity(Class<?> pClass, Bundle bundle) {
        openActivity(pClass, bundle, null);
    }

    public void openActivityFoReResult(Class<?> pClass, Bundle bundle,int requestCode) {
        openActivityForResult(pClass, bundle, null,requestCode);
    }

    public void openActivity(Class<?> pClass, Bundle bundle, Uri uri) {
        openActivityForResult(pClass,bundle,uri,-1);
    }
    public void openActivityForResult(Class<?> pClass, Bundle bundle, Uri uri, int requestCode) {
        Intent intent = new Intent(this, pClass);
        if (bundle != null)
            intent.putExtras(bundle);
        if (uri != null)
            intent.setData(uri);
        startActivityForResult(intent,requestCode);
        //Add animation=======
        overridePendingTransition(R.anim.anim_activity_right_in, R.anim.anim_activity_bottom_out);
    }

    public void openActivity(String action) {
        openActivity(action, null, null);
    }

    public void openActivity(String action, Bundle bundle) {
        openActivity(action, bundle, null);
    }

    public void openActivity(String action, Bundle bundle, Uri uri) {
        Intent intent = new Intent(action);
        if (bundle != null)
            intent.putExtras(bundle);
        if (uri != null)
            intent.setData(uri);
        startActivity(intent);
        //Add animation
        overridePendingTransition(R.anim.anim_activity_right_in, R.anim.anim_activity_bottom_out);
    }

}

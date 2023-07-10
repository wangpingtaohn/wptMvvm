package com.wpt.mvvm.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wpt.mvvm.R;

public class LoadingDialog extends Dialog {

    private Context context;

    private Animation animation;


    public LoadingDialog(Context context) {
        super(context, R.style.LoadProgressDialog);
        this.context = context;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loadding);

        setCanceledOnTouchOutside(false);
        setCancelable(true);

        initView();

    }

    private void initView(){
        ImageView imageView = findViewById(R.id.loading_image);
        animation = AnimationUtils.loadAnimation(context,R.anim.loading_animation);
        imageView.setAnimation(animation);
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.cancel();
    }
}

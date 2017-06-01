package com.lakala.platform.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lakala.core.base.LKLActionBarActivity;
import com.lakala.core.base.LKLActivityDelegate;
import com.lakala.core.launcher.ActivityLauncher;
import com.lakala.platform.R;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.roundprogressbar.UIUtils;

import butterknife.ButterKnife;

/**
 * Created by xyz on 13-12-10.
 */
public abstract class BaseActionBarActivity extends LKLActionBarActivity implements NavigationBar.OnNavBarClickListener, View.OnClickListener {
    //点击同一 view 最小的时间间隔，如果小于这个数则忽略此次单击。
    private static long intervalTime = 800;
    //最后点击时间
    private long lastClickTime = 0;
    //最后被单击的 View 的ID
    private long lastClickView = 0;

    //导航栏
    protected static NavigationBar navigationBar;
    protected Context           mContext;

    //view容器，所有子类的根布局都会添加到此容器中
    private   FrameLayout       baseContainer;

    public BaseActionBarActivity() {
        super();
    }

    protected boolean isInitbar=true;//是否做沉浸式处理

    @Override
    protected LKLActivityDelegate delegate() {
        return BusinessLauncher.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContext = this;


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.plat_activity_base);
        if(isInitbar){
            initBars();
        }
        //初始化导航栏
        navigationBar = (NavigationBar) findViewById(R.id.navigation_bar);
        ColorStateList colors = getResources().getColorStateList(R.color.action_text_color_selector);
        navigationBar.setActionBtnTextColor(colors);
        navigationBar.setOnNavBarClickListener(this);
        if (getIntent() != null){
            String title = getIntent().getStringExtra(ActivityLauncher.INTENT_PARAM_KEY_TITLE);
            if (title != null){
                navigationBar.setTitle(title);
            }
        }
        //初始化根布局容器
        baseContainer = (FrameLayout) findViewById(R.id.base_container);
        ViewGroup.inflate(this, layoutResID, baseContainer);

        ButterKnife.bind(this);
    }

    public NavigationBar getNavigationBar() {
        return navigationBar;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    public void initBars(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        View view = findViewById(R.id.viewf);
        if (view != null) {
            if (Build.VERSION.SDK_INT < 19) {
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            } else {
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.getStatusHeight(this)));
            }
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    /**
     * 隐藏导航栏，splash页面，主页面会用到
     */
    protected void hideNavigationBar() {
        navigationBar.setVisibility(View.GONE);
        navigationBar.setOnClickListener(this);
    }

    /**
     * 导航栏点击事件回调
     *
     * @param navBarItem
     */
    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        if (navBarItem == NavigationBar.NavigationBarItem.back) finish();
    }

    @Override
    public void onClick(View view) {
        if (!isFastMultiClick(view)) {
            onViewClick(view);
        }
    }

    /**
     * 防短时重复点击回调 <br>
     * 子类使用 View.OnClickListener 设置监听事件时直接覆写该方法完成点击回调事件
     *
     * @param view 被单击的View
     */
    protected void onViewClick(View view) {
        //供字类重写用事件
    }

    /**
     * 是否快速多次点击(连续多点击）
     * @param  view 被点击view，如果前后是同一个view，则进行双击校验
     * @return 认为是重复点击时返回true。
     */
    private boolean isFastMultiClick(View view) {
        long time = System.currentTimeMillis() - lastClickTime;

        if (time < intervalTime && lastClickView == view.getId()) {
            lastClickTime = System.currentTimeMillis();
            return true;
        }

        lastClickTime = System.currentTimeMillis();
        lastClickView = view.getId();

        return false;
    }
}


package com.lakala.shoudan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.common.avos.AvosSdkManager;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.AppInit;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.ScreenUtil;
import com.lakala.shoudan.util.SignaturesCheckPlugin;
import com.lakala.shoudan.util.WindowDialog;


/**
 * 应用程序启动时的第一个页面
 * 在此页面进行一些必要的网络请求，获取配置或更新的信息
 */
public class SplashActivity extends FragmentActivity {

    /**
     * 当前需要显示的splash图片对应的key
     */
    public static final String CURRENT_SPLASH_KEY = "currentSplashPngKey";
    /**
     * 已下载的的splash图片的有效期
     */
    public static final String CURRENT_SPLASH_ENDTIME_KEY = "currentSplashEndTime";
    /**
     * 已下载的的splash图片的更新时间
     */
    public static final String CURRENT_SPLASH_UPDATETIME_KEY = "currentSplashUpdateTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        LogUtil.print("Splash oncreate...");
        super.onCreate(savedInstanceState);
        integrityVerification();
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        String validSign = "7531046A950B627A9F6414EFEEC41D28";
        if (!SignaturesCheckPlugin.checkSignMd5(this, validSign)) {
//            ToastUtil.toast(SplashActivity.this,"请使用正确的签名");
//            ApplicationEx.getInstance().exit();
            finish();
            return;
        }

        ApplicationEx.getInstance().setActiveContext(this);
        View view = findViewById(R.id.container);
        ImageView iv = (ImageView) findViewById(R.id.iv_splash);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ScreenUtil.getScrrenWidthAndHeight(SplashActivity.this);
                LklPreferences preferences = LklPreferences.getInstance();
                preferences.putInt(UniqueKey.STATUS_BAR_HEIGHT, Parameters.statusBarHeight);
            }
        });
        new AppInit(this, "home").execute();
        AvosSdkManager.initPush(this, new Intent(), SplashActivity.class);
        String channel=String.format(ShoudanStatisticManager.appChannel, PropertiesUtil.channelName);
        ShoudanStatisticManager.getInstance().onEvent(channel, this);
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.appLaunch, this);
        if (ApplicationEx.getInstance().isFirstStart()) {
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.appActivate, this);
        }
        initAd(iv);

        BusinessLauncher.getInstance().onCreate(this, savedInstanceState);

        //除生产环境，其他环境显示悬浮窗显示版本信息
        WindowDialog.getInstance().initWindowDialog(this);

    }

    private void initAd(final ImageView view) {
        String path = LklPreferences.getInstance().getString(CURRENT_SPLASH_KEY);
        if (TextUtils.isEmpty(path)) {
            view.setImageDrawable(getResources().getDrawable(R.drawable.app_splash));
            return;
        }
        Glide.with(this)
                .load(path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        view.setImageDrawable(getResources().getDrawable(R.drawable.app_splash));
                    }

                });
    }
//    private void initAd(View view) {
//
//        String path = LklPreferences.getInstance().getString(CURRENT_SPLASH_KEY);
//        File file = new File(path);
//        if (!file.exists()) {
//            return;
//        }
//        Uri uri = Uri.fromFile(file);
//        LogUtil.print("uri isissisisisis  " + uri.toString());
//        try {
//            InputStream is = FileUtil.getFileFromUri(this, uri.toString());
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            Drawable drawable = new BitmapDrawable(bitmap);
//            String endTime = LklPreferences.getInstance().getString(SplashActivity.CURRENT_SPLASH_ENDTIME_KEY);
//            if (!TextUtils.isEmpty(endTime)) {
//                long endtime = Long.parseLong(endTime);
//                long curtime = new Date().getTime();
//                if (curtime < endtime) {
//                    view.setBackgroundDrawable(drawable);。。
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    protected void onDestroy() {
        LogUtil.print("Splash onDestroy...");
        super.onDestroy();
        ApplicationEx.getInstance().setActiveContext(null);
        BusinessLauncher.getInstance().onDestroy(this);
    }

    private int backPressCounter = 0;

    @Override
    public void onBackPressed() {
        if (backPressCounter < 5) {
            backPressCounter++;
            return;
        }
        ApplicationEx.getInstance().exit();
    }

    /**
     * app完整性验证
     */

    public void integrityVerification() {
        String appHash = Util.getAppHash(this);
        if (appHash.isEmpty()) {
            return;
        } else {
            //TODO huangjp 获取服务端哈希值进行比较并做提示用户处理
        }
    }
}
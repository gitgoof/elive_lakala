package com.lakala.shoudan.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;

import com.lakala.core.fileupgrade.FileUpgradeExternalInvoke;
import com.lakala.library.util.AppUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.loginservice.TokenRefreshService;

/**
 * app初始化操作
 * <p/>
 * Created by jerry on 14-4-23.
 * modified by LL on 14-11-26
 */
public class AppInit {
    private final static String App_Code = "app_code";

    /**
     * 表示是否是第一次打开app,如果是，则开启引导页面
     */
    private final static String FIRST_OPEN_APP = "first_open_app";

    /**
     * 是否来自第三方-------用于处理初始化完毕后的 应用跳转&分发
     */
    private boolean isExternal;

    private Activity context;

    //Splash 屏之后要启动的主屏页面的 key
    private String homeActivity;

    /**
     * @param context      Splash 屏之后要启动的主屏页面
     * @param homeActivity
     */
    public AppInit(Activity context, String homeActivity) {
        this(context, homeActivity, false);
    }


    public AppInit(Activity context, String homeActivity, boolean isExternal) {
        this.context = context;
        this.homeActivity = homeActivity;
        this.isExternal = isExternal;
        //若appCode为空，则为true；若不为空，则和保存的appCode比较，相等为false，反之为true
        String appCode = AppUtil.getAppVersionCode(context);
    }

    /**
     * 初始化Bundle配置
     */
    public void execute() {
        (new AsyncTask<String,Integer,Integer>(){
            @Override
            protected Integer doInBackground(String... params) {
                //初始化Bundle
                if (ApplicationEx.getInstance().isFirstStart()){

                    FileUpgradeExternalInvoke.getInstance(null).fileInit();
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer result) {
                BusinessLauncher.getInstance().initConfig();

                if (!isExternal) {
                    LklPreferences.getInstance().putString(App_Code, AppUtil.getAppVersionCode(context));
                    startDelayed(1000);
                }
            }
        }).execute();
    }

    /**
     * 延迟启动
     */
    private void startDelayed(final long time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //在这里加载启动页
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//                if (ApplicationEx.getInstance().isFirstStart()){
//                    LklPreferences.getInstance().putBoolean(FIRST_OPEN_APP, false);
//                    intent.putExtra(GuideActivity.KEY_NEXT_PAGE, homeActivity);
//                    BusinessLauncher.getInstance().startForResult(context, "guide", intent, 0);
//                }
//                else {
//                    BusinessLauncher.getInstance().startForResult(context, homeActivity, intent, 0);
//                }

//                BusinessLauncher.getInstance().startForResult(context, homeActivity, intent, 0);
//                context.finish();
                final boolean hasLoginUser = UserDao.getInstance().isHasLoginUser();

                long delayTime = time;

                if(!hasLoginUser ){
                    LocationManager.getInstance().statLocating();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TokenRefreshService.getInstance().start(hasLoginUser, TokenRefreshService.ServiceType.LOGIN, context);
                    }
                },1000);


            }
        }, 0);
    }

}


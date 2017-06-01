package com.lakala.elive;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.lakala.elive.common.utils.ActivityTaskCacheUtil;
import com.lakala.elive.common.utils.DictionaryUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;


/**
 * Created by hongzhiliang on 2016/9/16.
 */
public class EliveApplication extends Application {

    public LocationClient mLocationClient;

    public static EliveApplication mApplication;

    private static RequestQueue mQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(getApplicationContext());

        mApplication = this;

        mQueue = Volley.createHttpsRequestQueue(getApplicationContext(), null);

        try {

            CrashReport.initCrashReport(getApplicationContext(), "0059247ac2", true);

            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            //注意该方法要再setContentView方法之前实现
            SDKInitializer.initialize(getApplicationContext());

            if (!"generic".equalsIgnoreCase(Build.BRAND)) {
                SDKInitializer.initialize(getApplicationContext());
            }

            //xutils集成
            x.Ext.init(this);
            x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志

        } catch (Exception e) {
            e.printStackTrace();
        }


        //异常处理日志
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext(), this);
        DictionaryUtil.getInstance().init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static RequestQueue getHttpQueue() {
        return mQueue;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onTerminate() {
        super.onTerminate();
        ActivityTaskCacheUtil.getIntance().clearAllAct();
        System.exit(0);
    }

    public static EliveApplication getInstance() {
        return mApplication;
    }

    public static void setImageProgress(ImageView imageView, int progress) {//设置步骤图片
        switch (progress) {
            case 1:
                imageView.setImageResource(R.drawable.icon_one);
                break;
            case 2:
                imageView.setImageResource(R.drawable.icon_two);
                break;
            case 3:
                imageView.setImageResource(R.drawable.icon_three);
                break;
            case 4:
                imageView.setImageResource(R.drawable.icon_four);
                break;
            case 5:
                imageView.setImageResource(R.drawable.icon_five);
                break;
            case 6:
                imageView.setImageResource(R.drawable.icon_six);
                break;
        }
    }

    public static String PUBLICQCODE = "PUBLICQCODE";
    public static String PRIVICEQCODE = "PRIVICEQCODE";

    //设置Q码进件的步骤
    public static void setImageQCodeProgress(ImageView imageView, int progress, String type) {//设置步骤图片
        if (type.equals(EliveApplication.PRIVICEQCODE)) {//由于对私比对公少一部
            switch (progress) {
                case 1:
                    imageView.setImageResource(R.drawable.ic_qcode_one);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_qcode_two);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.ic_qcode_three);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.ic_qcode_four);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.ic_qcode_five);
                    break;
            }
        } else if (type.equals(EliveApplication.PUBLICQCODE)) {
            switch (progress) {
                case 1:
                    imageView.setImageResource(R.drawable.icon_one);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.icon_two);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.icon_three);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.icon_four);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.icon_five);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.icon_six);
                    break;
            }
        }
    }

}

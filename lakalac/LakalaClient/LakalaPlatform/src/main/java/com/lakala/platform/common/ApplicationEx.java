package com.lakala.platform.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lakala.core.LibApplicationEx;
import com.lakala.core.fileupgrade.ConfigEntity;
import com.lakala.core.fileupgrade.FileUpgradeExternalInvoke;
import com.lakala.library.DebugConfig;
import com.lakala.library.jni.LakalaNative;
import com.lakala.library.util.AppUtil;import com.lakala.library.util.CertificateCode;import com.lakala.library.util.LogUtil;import com.lakala.platform.BuildConfig;
import com.lakala.platform.activity.BaseActivity;
import com.lakala.platform.bean.Session;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.avos.AvosSdkManager;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.common.meiqia.MeiQia;
import com.lakala.platform.config.Config;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManager;
import com.loopj.lakala.http.AsyncHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;import java.math.BigInteger;import java.security.MessageDigest;import java.util.Arrays;import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xyz on 13-12-20.
 */
public class ApplicationEx extends LibApplicationEx {
    private static ApplicationEx mInstance;
    private GestureLockReceiver mGestureLockReceiver;
    private FragmentActivity activeContext;

    /**交易Session*/
    private Session mSession;
    /** 商户数据*/
    private Uri data;
    //是否是第一次启动应用
    private boolean isFirstStart = false;
    /**是否需要检查登录状态*/
    private boolean checkLoginState = true;

    public Bitmap getBitmap() {
        return bitmap;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap=null;

    private LinkedList<FragmentActivity> mActivities = new LinkedList<FragmentActivity>();

    public static ApplicationEx getInstance() {
        return mInstance;
    }

    public FragmentActivity getActiveContext() {
        return activeContext;
    }

    public void setActiveContext(FragmentActivity activeContext) {
        this.activeContext = activeContext;
    }

    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "unkwown process";
    }

    @Override
    public void onCreate() {
        UILUtils.init(this);
        if (this.getPackageName().equals(getCurProcessName())) {
            //设置debug标记，在调用super之前初始化debug标志，解决数据库没有加密的bug
//            DebugConfig.setDebug(Config.isDebug());
            DebugConfig.setDebug(isApkDebugable(this));
            super.onCreate();

            mInstance = this;
            mSession = new Session();
//屏蔽锁屏监听            registGestureReceiver();
            initConfig();
            AsyncHttpClient.SHA1=getSha1();
            AsyncHttpClient.CRC32=getCrc32();
            LogUtil.print("System.out",getSha1());
            LogUtil.print("System.out",getCrc32()+"");
            CertificateCode.application=this;

        }
//        LogUtil.print("ishome", Arrays.toString(PropertiesUtil.bks));
    }


    public String getResString(int id) {
        return getResources().getString(id);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void addActivity(FragmentActivity mFragmentActivity){
        this.mActivities.add(mFragmentActivity);
    }

    public void removeActivity(FragmentActivity mFragmentActivity){
        this.mActivities.remove(mFragmentActivity);
    }

    public boolean isCheckLoginState() {
        return checkLoginState;
    }

    public void setCheckLoginState(boolean checkLoginState) {
        this.checkLoginState = checkLoginState;
    }

    /**
     * 程序退出时调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(mGestureLockReceiver);
        SwiperManager.getInstance(null).onDestroy();
    }

    public User getUser() {
        return mSession.getUser();
    }

    public Session getSession() {
        return mSession;
    }

    public Uri getData() {
        return data;
    }

    public void setData(Uri data) {
        this.data = data;
    }


    /**
     * 退出本应用
     */
    public void exit() {

        mInstance.isFirstStart      = false;
        mInstance.checkLoginState   = false;
//        removeAll();
        BusinessLauncher launcher   = BusinessLauncher.getInstance();
        launcher.pop(launcher.getActivities().size());
        //先保存登录相关状态,后再清楚数据
        ApplicationEx.getInstance().getUser().save();
        ApplicationEx.getInstance().getSession().clear();
        SwiperManager.getInstance(null).onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());

    }



    /**
     * 是否是第一次启动应用，第一次启动的判断是与版本有关的，每次应用更新后的第一次启动该方法都会返回 true。
     * @return
     */
    public boolean isFirstStart(){
        return isFirstStart;
    }

    /**
     * app激活时初始化一次配置 **/
    protected void initConfig() {

        //初始化崩溃日志统计
//        Crashlytics.start(ApplicationEx.getInstance());
        Fabric.with(this, new Crashlytics());
        MeiQia.getInstance().init();

        LocationManager.getInstance().statLocating();

        writeTimestamp();
        //AVOS初始化
        AvosSdkManager.init();
        //初始化 Upgrade 模块核心数据
        ConfigEntity config = new ConfigEntity();
        config.setDEBUG(BuildConfig.DEBUG);
        config.setContext(this);
        config.setPublicKey(LakalaNative.getLoginPublicKey());
        //
        config.setRequestBaseUrl(PropertiesUtil.getUrl());
        config.setRequestParams(new String[]{
                "_Platform", "android",
                "_AppVersion", AppUtil.getAppVersionName(this),
                "_SubChannelId", "10000017",
                "X-Client-PV", "MPOS",
                "X-Client-Ver", AppUtil.getAppVersionCode(this)
        });


        FileUpgradeExternalInvoke.getInstance(config);

    }

    private void writeTimestamp(){
        //读取 assets/buildtime.tt 时间戳，与 bundle 下的时间戳比较如果不同则重新初始化 Bundle
        AssetManager assets = this.getAssets();
        String bundleTimeDir = PackageFileManager.getInstance().getRootPath();
        String bundleTimePath = bundleTimeDir.concat("/buildtime.tt");

        InputStream is = null;
        FileInputStream fs = null;
        String buildTime = null;
        String bundleTime = null;

        try {
            is = assets.open("buildtime.tt");
            int count = is.available();
            byte[] content = new byte[count];
            is.read(content);
            buildTime = new String(content);
            is.close();

            //取 Bundle 下的时间戳
            fs = new FileInputStream(bundleTimePath);
            count = fs.available();
            content = new byte[count];
            fs.read(content);
            bundleTime = new String(content);
            fs.close();
        }
        catch (IOException e) {

            LogUtil.d("Timestamp", "Read timestamp failed!");
        }
        finally{
            try {
                if (is != null) is.close();
                if (fs != null) fs.close();
            } catch (IOException e) {}
        }

        //时间戳比对不匹配，说明是第一次启动。
        if (buildTime == null || !buildTime.equals(bundleTime)){

            LogUtil.d("Timestamp","First Start");
            isFirstStart = true;

            //将时间戳文件写到 Bundle 根目录下
            File file = new File(bundleTimeDir);
            if (!file.exists()){
                file.mkdirs();
            }


            FileOutputStream fso = null;
            try {
                fso = new FileOutputStream(bundleTimePath,false);
                fso.write(buildTime.getBytes());
                fso.flush();
            }
            catch (FileNotFoundException e) {

                LogUtil.d("Timestamp","Write timestamp failed!");
            }
            catch (IOException e) {

                LogUtil.d("Timestamp","Write timestamp failed!");
            }
            finally{
                try {
                    if (fso != null) is.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * 注册锁屏广播
     */
    private void registGestureReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mGestureLockReceiver = new GestureLockReceiver();
        registerReceiver(mGestureLockReceiver, intentFilter);
    }

    /**
     * 锁屏广播
     */
    private class GestureLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null
                    && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                    && AppUtil.isAppRunningForeground(context)
                    && getSession().isUserLogin()
                    && getUser().isExistGesturePassword() && !getUser().ifSkipGesture()) {
                if(BusinessLauncher.getInstance().getActivities().isEmpty()){
                    return;
                }
                //peekLast 替换getLast 若列表为空，返回null
                Activity lastActivity = (Activity) BusinessLauncher.getInstance().getActivities().peekLast();
                if (lastActivity != null && !((Object) lastActivity).getClass().getName().equals("com.lakala.shoudan.activity.login.GestrueActivity")) {
                    ApplicationEx.getInstance().getSession().setUserLogin(false);
                    Intent gestureIntent = new Intent();
                    gestureIntent.setAction("com.lakala.shoudan.action.gestrue");
                    gestureIntent.putExtra("from",GestrueType.LOGIN_GESTRUE);
//                    gestureIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lastActivity.startActivityForResult(gestureIntent, BaseActivity.REQUEST_CODE_LOCK_SCREEN);
//                    lastActivity.finish();
                }
            }
        }
    }

    public void loginOut(){
        //最后一次登录标致为false
        UserDao.getInstance().setAllUserLoginFalse();
        //保存当前登录用户名
        String loginName = getUser().getLoginName();
        LklPreferences.getInstance().putString(LKlPreferencesKey.KEY_LOGIN_NAME, loginName);
        //清除用户信息
        getSession().clear();

    }

    /***
     * app是否在前台
     * @return
     */
    public boolean isRunningForeground(){
        //TODO
        return false;
    }

    /**
     * 判断是否为dubug模式
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }

    public String getCrc32(){
        String apkPath = getPackageCodePath();
        Long dexCrc = Long.parseLong("11111");
        //建议将dexCrc值放在服务器做校验
        LogUtil.print("safe","classes.dexcrc");
        try {
            ZipFile zipfile = new ZipFile(apkPath);
            ZipEntry dexentry = zipfile.getEntry("classes.dex");
            LogUtil.print("safe","classes.dexcrc="+dexentry.getCrc());
            return dexentry.getCrc()+"";
        } catch (IOException e) {


            e.printStackTrace();

        }
        return "";
    }
    public String getSha1(){
        String apkPath = getPackageCodePath();
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath));
            while ((byteCount = fis.read(bytes)) > 0)
            {
                msgDigest.update(bytes, 0, byteCount);
            }
            BigInteger bi = new BigInteger(1, msgDigest.digest());
            fis.close();
            return bi.toString(16);

        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }

}

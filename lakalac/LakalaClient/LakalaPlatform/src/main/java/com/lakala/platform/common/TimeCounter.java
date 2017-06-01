package com.lakala.platform.common;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lakala.library.util.AppUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.Session;
import com.lakala.platform.bean.User;
import com.lakala.platform.launcher.BusinessLauncher;

/**
 * Created by More on 15/3/26.
 */
public class TimeCounter {
    private static final int TIMEOUT_2_GESTURE = 180 * 1000;//超时进入手势密码的时间(毫秒）
    private static TimeCounter ourInstance = new TimeCounter();

    public static TimeCounter getInstance() {
        return ourInstance;
    }

    private TimeCounter() {
    }


    public void saveCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        LklPreferences.getInstance()
                .putLong(LKlPreferencesKey.APP_INTO_BACKGROUND_TIME + ApplicationEx.getInstance().getUser().getLoginName(), currentTimeMillis);
    }

    public long get2BackgroundTime() {
        return LklPreferences.getInstance()
                .getLong(LKlPreferencesKey.APP_INTO_BACKGROUND_TIME + ApplicationEx.getInstance().getUser().getLoginName(), System.currentTimeMillis());
    }

    public void clearSavedTime() {
        LklPreferences.getInstance().remove(LKlPreferencesKey.APP_INTO_BACKGROUND_TIME + ApplicationEx.getInstance().getUser().getLoginName());
    }

    /**
     * 根据情况保存APP进入后台的时间
     * (仅当返回前台需要进入手势密码时保存)
     * @param context
     */
    public void save2BackgroundTime(Activity context) {
        Session session = ApplicationEx.getInstance().getSession();
        if(!session.isUserLogin()){
            return;
        }
        User user = ApplicationEx.getInstance().getSession().getUser();

        String topActivityName = BusinessLauncher.getInstance().getTopActivity().getClass()
                .getName();
        if((TextUtils.equals(topActivityName,"com.lakala.shoudan.activity.login.GestrueActivity")) ){
            return;
        }
        if (user.isExistGesturePassword() && !AppUtil.isAppRunningForeground(context)) {
            TimeCounter.getInstance().saveCurrentTime();
        }

    }

    /**
     * 根据情况进入手势密码页
     *
     * @param context
     */
    public void may2Gesture(Activity context) {

        if(!ApplicationEx.getInstance().getSession().isUserLogin()){
            return;
        }

        long savedTime = TimeCounter.getInstance().get2BackgroundTime();
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - savedTime;

        if(ApplicationEx.getInstance().getUser().isExistGesturePassword()){
            if (interval >= TIMEOUT_2_GESTURE) {

                if (!TimeCounter.getInstance().isTokenValidate()) {
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName("com.lakala.shoudan.activity.SplashActivity");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(
                            new Intent(context, clazz).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    context.finish();
                    return;
                }

                try {
                    ApplicationEx.getInstance().getSession().setUserLogin(false);
                    Class<?> gestrueClazz = Class
                            .forName("com.lakala.shoudan.activity.login.GestrueActivity");
                    Intent intent = new Intent(context, gestrueClazz);
                    intent.putExtra("from", GestrueType.LOGIN_GESTRUE);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        TimeCounter.getInstance().clearSavedTime();



    }

    /**
     * token 是否有效判断
     *
     * @return
     */
    public boolean isTokenValidate() {

        User user = ApplicationEx.getInstance().getUser();

        if (user == null || TextUtils.isEmpty(user.getRefreshToken()) || TextUtils
                .isEmpty(user.getExpirein())) {
            //当前用户登陆过并且 token等数据应该是有效的
            return false;
        }

        long interval = System.currentTimeMillis() - getTokenUpdateTimeMillis();

        LogUtil.print("getExpirein = " + Long
                .parseLong(ApplicationEx.getInstance().getUser().getExpirein()));
//        return interval < 60 * 1000;//TODO 暂时处理成一分钟
        return interval < Long
                .parseLong(ApplicationEx.getInstance().getUser().getExpirein()) * 1000;
    }

    public void saveTokenUpdateTime() {

        if (!TextUtils.isEmpty(ApplicationEx.getInstance().getUser().getLoginName())) {
            LklPreferences.getInstance().putLong(
                    LKlPreferencesKey.KEY_REFRESH_TIME + ApplicationEx.getInstance().getUser()
                            .getLoginName(), System.currentTimeMillis());
        }
    }


    private long getTokenUpdateTimeMillis() {

        String loginName = ApplicationEx.getInstance().getUser().getLoginName();
        if (TextUtils.isEmpty(loginName)) {
            return 0;
        }
        long defalut = 0;
        return LklPreferences.getInstance()
                .getLong(LKlPreferencesKey.KEY_REFRESH_TIME + loginName, defalut);
    }
}

package com.lakala.elive.common.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaofeng on 2017/4/19.
 */

public class ActivityTaskCacheUtil {
    private final static ActivityTaskCacheUtil mActivityTaskCacheUtil = new ActivityTaskCacheUtil();

    public static ActivityTaskCacheUtil getIntance(){
        return mActivityTaskCacheUtil;
    }
    private final List<Activity> mActCache = new ArrayList<Activity>();
    public void addActivity(Activity activity){
        mActCache.add(activity);
    }

    public void delActivity(Activity activity){
        if(mActCache.contains(activity)){
            mActCache.remove(activity);
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    public void clearActExceptLogin(){
        Activity logAct = null;
        for (Activity activity:mActCache){
            if(activity == null){
                continue;
            }
            if(activity.getClass().toString().contains("com.lakala.elive.user.activity.UserLoginActivity")){
                if(logAct==null){
                    logAct = activity;
                } else {
                    if(!activity.isFinishing())
                        activity.finish();
                }
            } else {
                if(!activity.isFinishing())
                activity.finish();
            }
        }
        mActCache.clear();
        if(logAct != null)
        mActCache.add(logAct);
    }

    public void clearAllAct(){
        for (Activity activity:mActCache){
            if(activity == null){
                continue;
            }
            if(!activity.isFinishing())
                activity.finish();
        }
        mActCache.clear();
    }

    public void clearJinJianAct(){
        List<String> jinJianClassName = new ArrayList<String>();
        jinJianClassName.add("com.lakala.elive.merapply.activity.PublicPhotoOcrActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.InformationInputActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.FaceRecognitionActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.BusinessLicenseDiscernActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.LicenseEntryPhotoActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.BasicInfoActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.MachinesToolsInfoActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.MerApplyCompleteActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.PrivatePhotoOcrActivity");
        jinJianClassName.add("com.lakala.elive.merapply.activity.PayeeProveInfoActivity");

        Iterator<Activity> iterator = mActCache.iterator();
        while(iterator.hasNext()){
            Activity activity = iterator.next();
            if(activity == null){
                iterator.remove();
                continue;
            }
            if(jinJianClassName.contains(activity.getClass().toString())){
                activity.finish();
                iterator.remove();
            }
        }
    }
}

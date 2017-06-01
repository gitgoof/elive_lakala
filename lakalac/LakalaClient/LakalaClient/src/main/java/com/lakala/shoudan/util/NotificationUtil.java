package com.lakala.shoudan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertListDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LMQ on 2015/3/17.
 */
public class NotificationUtil {
    private static NotificationUtil instance = null;
    private NotificationUtil() {
    }
    public static NotificationUtil getInstance(){
        synchronized (NotificationUtil.class){
            if(instance == null){
                instance = new NotificationUtil();
            }
        }
        return instance;
    }

    public void addNotification(String cardNo,String dateText){
        addNotification(ApplicationEx.getInstance().getUser().getLoginName(), cardNo, dateText);
    }
    private void addNotification(String userName, String cardNo, String dateText){
        Context context = ApplicationEx.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (userName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cardNo, dateText).commit();
    }
    private void setNotifyTag(boolean isNotify){
        String userName = ApplicationEx.getInstance().getUser().getLoginName();
        Context context = ApplicationEx.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (userName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifyTag",isNotify);
    }
    public Map<String,String> getNotifications(){
        return getNotifications(ApplicationEx.getInstance().getUser().getLoginName());
    }
    private Map<String, String> getNotifications(String userName){
        Context context = ApplicationEx.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences(userName,
                                                                           Context.MODE_PRIVATE);
        Map<String,String> notifications = (HashMap<String,String>)sharedPreferences.getAll();
        return notifications;
    }
    public static void doNotify(){
        NotificationUtil notificationUtil = NotificationUtil.getInstance();
        Map<String, String> notifications = notificationUtil.getNotifications();
        Set<String> keys = notifications.keySet();
        if(keys == null){
            LogUtil.i("test","没有还款提醒");
            return;
        }
        List<String> list = new ArrayList<String>();
        Calendar nowTime = Calendar.getInstance();
        for(String key:keys){
            int nowDay = nowTime.get(Calendar.DAY_OF_MONTH);
            int nowMonth = nowTime.get(Calendar.MONTH);
            String value = notifications.get(key);
            if(TextUtils.isEmpty(value) || !Character.isDigit(value.charAt(0))){
                continue;
            }
            int alarmDay = 0;
            int preAlarmMonth = 0;
            if(value.contains("-")){
                String[] strs = value.split("-");
                alarmDay = Integer.parseInt(strs[0]);
                preAlarmMonth = Integer.parseInt(strs[1]);
                if(nowDay == alarmDay && preAlarmMonth != nowMonth){
                    list.add(CardUtil.formatCardNumberWithStar(key));
                    notificationUtil.addNotification(key,new StringBuffer().append(alarmDay)
                            .append("-").append(nowMonth).toString());
                }
            }else{
                alarmDay = Integer.parseInt(value);
                if(nowDay == alarmDay){
                    list.add(CardUtil.formatCardNumberWithStar(key));
                    notificationUtil.addNotification(key,
                                                     new StringBuffer().append(alarmDay).append("-")
                                                             .append(nowMonth).toString());
                }
            }
        }
        if(list.size() == 0){
            return;
        }
        AlertListDialog dialog = new AlertListDialog();
        dialog.setTitle("今日还款信用卡");
        dialog.setButtons(new String[]{ApplicationEx.getInstance().getString(R.string.button_ok)});
        dialog.setItems(list, null);
        dialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
            }
        });
        ErrorDialogActivity.startSelf(dialog);
        notificationUtil.setNotifyTag(true);
    }
}

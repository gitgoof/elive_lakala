package com.lakala.shoudan.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.activity.SplashActivity;
import com.lakala.shoudan.activity.messagecenter.MessageCenter2Activity;

/**
 * Created by LMQ on 2015/4/21.
 */
public class NotificationClickedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationEx app = ApplicationEx.getInstance();
        if (app != null && app.isRunningForeground()) {//点击消息时，客户端前台运行
//            if(!TextUtils.isEmpty(Parameters.user.userName)){//用户已登录
//                Intent intent1 = new Intent(context, MessageCenterActivity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent1);
//            }
        } else if (!TextUtils.isEmpty(app.getUser().getUserId())
                || !TextUtils.isEmpty(app.getUser().getLoginName())) {//客户端后台运行且已登录
            Intent intent1 = new Intent(context, MessageCenter2Activity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } else {//客户端已退出，启动app
            Intent intent1 = new Intent(context, SplashActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}

package com.lakala.shoudan.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lakala.library.util.AppUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.platform.statistic.ShoudanStatisticManager;

import org.json.JSONObject;

import java.util.Set;


public class PushMessageReceiver extends BroadcastReceiver {

    private final static String CHANNEL = "com.avos.avoscloud.Channel";

    private final static String DATA = "com.avos.avoscloud.Data";

    private final static String SHT_ACTION = "com.lakala.shoudan.action.push";


//    {"action":"com.lakala.shoudan.action.push","alert":"测试111","badge":"Increment","id":"20150323113749","sound":"default","title":"拉卡拉超级手机银行"}

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
//            LogUtil.print("Get Push message");
//            Set<String> keys = intent.getExtras().keySet();
//            for(String key:keys){
//                LogUtil.print("key=="+key+";value=="+intent.getExtras().get(key));
//            }
            JSONObject json = null;
            try {
                String data = intent.getExtras().getString(DATA);
                if(data == null){
                    return;
                }
                json = new JSONObject(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(json != null && SHT_ACTION.equals(json.optString("action"))){
                String alert = json.optString("title", "您有一条新消息");
//                String title = json.optString("title", "");
                String title = context.getString(R.string.app_name);
                showPushMessage(title, alert);
            }
        } catch (Exception e) {
            LogUtil.print(getClass().getName(), "ex", e);
        }
    }

    /**
     *
     * @param title 标题
     * @param alert 内容
     */
    private void showPushMessage(String title, String alert){

        if(AppUtil.isAppRunningForeground(ApplicationEx.getInstance())){
            //收款宝需求，app前台运行时不显示推送消息
//
//            AlertDialog alertDialog  = new AlertDialog();
//            alertDialog.setTitle(title);
//            alertDialog.setMessage(alert);
//            alertDialog.setButtons(new String[]{ApplicationEx.getInstance().getString(R.string.button_ok)});
//            alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
//                @Override
//                public void onButtonClick(AlertDialog dialog, View view, int index) {
//                    super.onButtonClick(dialog, view, index);
//                    dialog.dismiss();
//                }
//            });
//            ErrorDialogActivity.startSelf(alertDialog);

        }else{
            NotificationPusher.showNotification(title, alert);
        }

    }


}
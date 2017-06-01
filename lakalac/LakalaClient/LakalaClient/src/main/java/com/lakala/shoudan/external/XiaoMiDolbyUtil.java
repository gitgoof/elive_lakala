package com.lakala.shoudan.external;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;

/**
 * Created by lianglong on 14-3-19.
 */
public class XiaoMiDolbyUtil {
    //小米杜比
    public static final String PHONE_MODEL          = "MI2";
    public static final String STABLETAG            = "JLB";
    public static final String STABLEVERSION        = "JLB16.0";//稳定版必须大于等于此版本
    public static final String ENGVERSION           = "3.5.7";//必须大于此版本才能使用广播关闭杜比
    public static final String ACTION_DOLBY_UPDATE  = "com.xiaomi.action_dolby_update";//更新杜比状态
    public static final String ACTION_DOLBY_ACK     = "com.xiaomi.action_dolby_ack";//存在杜比音效
    public static final String CHECK_STATUS_ACTION  = "com.dolby.dm.srvcmd.checkstatus";//检测杜比音效

    private static XiaoMiDolbyUtil instance;
    private Context mContext;
    private DolbyBroadcastReceiver intentReceiver;

    private XiaoMiDolbyUtil(Context mContext) {
        this.mContext   = mContext;
        intentReceiver  = new DolbyBroadcastReceiver();
    }

    public static XiaoMiDolbyUtil getInstance(Context context) {
        if (instance == null) {
            instance = new XiaoMiDolbyUtil(context);
        } else {
            instance.mContext = context;
        }
        return instance;
    }


    /**
     * 处理小米2杜比音效问题，高版本通过发送广播来关闭杜比，低版本则提示用户自己去设置里面关闭
     */
    public void processDolby() {
        if (!isMITwo()) //判断是否为MI2并且版本高于指定版本
            return;

        try {
            if (isNewVersion()) {
                mContext.registerReceiver(intentReceiver, new IntentFilter(ACTION_DOLBY_ACK));//注册监听器
                checkDolbyIntent(); //发送广播检查
            }
            ToastUtil.toast(mContext, R.string.xiaomi_bolby_closed);
//            else {
//                throw new Exception();
//            }
        } catch (Exception e) {

        }
    }

    public void destroy(){
        try {
            mContext.unregisterReceiver(intentReceiver);
        } catch (Exception e){

        }
    }

    /**
     * 根据手机model判断是否是小米2
     *
     * @return
     */
    private boolean isMITwo() {
        String model = DeviceUtil.getPhoneModel();
        model = formatString(model);
        return PHONE_MODEL.equals(model);
    }

    /**
     * 根据rom版与最低版本比较，如果手机rom版本高于指定版本，则通过广播关闭杜比
     *
     * @return
     */
    private boolean isNewVersion() {
        String osVersion = Build.VERSION.INCREMENTAL;
        if (osVersion.startsWith(STABLETAG)) { //稳定版
            // osVersion.compareTo(string) 这是使用有问题，是按照字符比对，JLB4.0会大于JLB16.0。
            String replaceVersion = osVersion.replace(STABLETAG, "");
            String replaceTarget = STABLEVERSION.replace(STABLETAG, "");
            return strCompare(replaceVersion, replaceTarget);
        } else {
            return strCompare(osVersion, ENGVERSION);
        }
    }

    /**
     * 整形待分割符字符串比对
     *
     * @param dist          "3.5.8"
     * @param targetVersion "3.5.7"
     * @return "3.5.8">"3.5.7"  "3.5.10">"3.5.7" ...
     */
    private boolean strCompare(String dist, String targetVersion) {
        String[] osVersions = dist.split("\\.");
        String[] targetVersions = targetVersion.split("\\.");
        int osLength = osVersions.length;
        int tarLength = targetVersions.length;
        int length = Math.min(osLength, tarLength);

        int isNew = 0;
        for (int i = 0; i < length; i++) {
            int version = Integer.parseInt(osVersions[i]);
            int target = Integer.parseInt(targetVersions[i]);
            if (version > target) {
                isNew = version - target;
                break;
            } else if (version < target) {
                isNew = version - target;
                break;
            } else {
                isNew = 0;
            }
        }

        if ((isNew == 0) && (osLength > tarLength)) {
            isNew = osLength - tarLength;
        }

        return isNew >= 0;
    }

    /**
     * 广播检测杜比
     */
    private void checkDolbyIntent() {
        Intent intent = new Intent();
        intent.setAction(CHECK_STATUS_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * 广播更新杜比
     * @param bEnable 1 开启; 0关闭
     */
    private void updateDolbyIntent(int bEnable) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DOLBY_UPDATE);
        intent.putExtra("enable", bEnable);
        mContext.sendBroadcast(intent);
    }

    /**
     * 去除字符中间的 "空格/-/," 等间隔符
     *
     * @param string 要格式化的字符
     * @return 格式化后的字符
     */
    private String formatString(String string) {
        return string == null ? "" : string.replaceAll(" ", "").replaceAll("-", "").replaceAll(",", "");
    }

    /**
     * 小米杜比音效receiver
     */
    private class DolbyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DOLBY_ACK.equals(action)) { //存在杜比音效action
                updateDolbyIntent(0);//关闭杜比音效
            } else {
                ToastUtil.toast(mContext, R.string.xiaomi_bolby_closed);
            }
        }
    }

}

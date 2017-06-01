package com.lakala.core.fileupgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lakala.library.util.AppUtil;

/**
 * <p>Description  : 检验文件广播接收器.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/20.</p>
 * <p>Time         : 上午11:50.</p>
 */
public class CheckLocalFileReceiver extends BroadcastReceiver {

    private FileUpgradeExternalInvoke invoke;

    public CheckLocalFileReceiver(){
        try {
            invoke = FileUpgradeExternalInvoke.getInstance(null);
        }catch (IllegalArgumentException e){
            invoke = null;
        }
    }

    @Override public void onReceive(Context context, Intent intent) {
        if (context == null){
            return;
        }
        //如果当前应该不在前台那么，return
        if (!AppUtil.isAppRunningForeground(context)){
            return;
        }

        //如果检测到本地文件无效，那么发送广播
        if (invoke != null && !invoke.isLocalFileValid()){
            context.sendStickyBroadcast(new Intent(FileUpgradeExternalInvoke.ACTION_LOCAL_FILE_INVALIDED));
        }
    }
}

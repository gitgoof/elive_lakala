package com.lakala.platform.FileUpgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * <p>Description  : 当检验本地文件无效时候触发，提示用户卸载，下载正式版本.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/19.</p>
 * <p>Time         : 下午5:01.</p>
 */
public class ValidateFileHandler extends BroadcastReceiver{

    @Override public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equalsIgnoreCase(FileUpgradeManager.ACTION_LOCAL_FILE_INVALIDED)){
            Intent serviceIntent = new Intent(context, HandleResultActivity.class);
            serviceIntent.putExtra(HandleResultActivity.ACTION_KEY, HandleResultActivity.LOCAL_FILE_INVALIDATE);
            context.startActivity(serviceIntent);
        }

        context.removeStickyBroadcast(intent);
    }
}

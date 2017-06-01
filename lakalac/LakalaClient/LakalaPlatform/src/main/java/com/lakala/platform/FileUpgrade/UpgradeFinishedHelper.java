package com.lakala.platform.FileUpgrade;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.lakala.core.fileupgrade.FileEntity;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.ProgressDialog;

import java.util.Vector;

/**
 * Created by LMQ on 2015/7/14.
 */
public class UpgradeFinishedHelper {
    final public static String ACTION_NOTIFY_UPDATE =
            "com_lakala_platform_fileUpgrade_action_notify_update";
    public static void showAskInstallDialog(){
        final Activity context = BusinessLauncher.getInstance().getTopActivity();
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage("发现有更新版本，请立即安装");
        alertDialog.setButtons(new String[]{"立即安装"});
        alertDialog.setDialogDelegate(
                new AlertDialog.AlertDialogDelegate() {
                    @Override
                    public void onButtonClick(final AlertDialog dialog, View view, final int index) {
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setProgressMessage("正在更新中，请稍后");
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //解压缩
                                Vector<FileEntity> successList = FileUpgradeManager.getInstance().getSuccessEntityList();

                                for (final FileEntity entity : successList) {
                                    entity.decompressEntity();
                                }
                                Intent intent = new Intent();
                                intent.setAction(ACTION_NOTIFY_UPDATE);
                                intent.putExtra("isUserClicked",true);
                                progressDialog.getContext().sendBroadcast(intent);
                                progressDialog.dismiss();
                            }
                        }).start();
                    }
                }
        );
        ErrorDialogActivity.startSelf(alertDialog);
    }
}

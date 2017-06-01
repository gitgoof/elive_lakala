package com.lakala.platform.FileUpgrade;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.lakala.core.fileupgrade.FileEntity;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.ui.dialog.AlertDialog;

import java.util.Vector;

/**
 * <p>Description  : 处理下载结果类.
 *                   主要处理需要UI展示的结果.
 *                   当文件全部下载完成后，弹出对话框提示，点击确认会把当前下载的所有文件解压，
 *                   此时会发出一个广播</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/20.</p>
 * <p>Time         : 下午5:26.</p>
 */
@Deprecated
public class HandleResultActivity extends FragmentActivity {
    /*                  广播 action 通知所有更新的文件解压成功                    */
    final public static String ACTION_NOTIFY_UPDATE =
            "com_lakala_platform_fileUpgrade_e";

    final public static String ACTION_KEY                = "action_key";
    final public static int    CHECK_ALL_SUCCESS         = 1;
    final public static int    LOCAL_FILE_INVALIDATE     = 3;
    final private static int   FINISH                    = 10;

    private FragmentActivity me;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case CHECK_ALL_SUCCESS:
//TODO                    DialogController.getInstance().dismiss();
                    ToastUtil.toast(me, "更新成功!");
                    LogUtil.print("", "成功~~~~");
                    //发送更新通知
                    me.sendStickyBroadcast(new Intent(ACTION_NOTIFY_UPDATE));
                    sendMessage(FINISH);
                    break;
                case LOCAL_FILE_INVALIDATE:
                    break;
                case FINISH:
                    me.finish();
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        me = this;

        int action = getIntent().getIntExtra(ACTION_KEY, 0);
        switch (action){
            case CHECK_ALL_SUCCESS:
                handleCheckAllSuccess();
                break;
            case LOCAL_FILE_INVALIDATE:
                handleCheckFileValidate();
                break;
        }

    }

    /**
     * 进入这个方法表示:所有文件下载成功,等待解压缩
     */
    private void handleCheckAllSuccess(){
        //获取更新提示

        String updateInfo = FileUpgradeManager.getInstance().getUpdateMessage();

        LogUtil.print("handleCheckAllSuccess");
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage("发现有更新版本，请立即安装");
        alertDialog.setButtons(new String[]{"立即安装"});
        alertDialog.setDialogDelegate(
                new AlertDialog.AlertDialogDelegate() {
                    @Override
                    public void onButtonClick(AlertDialog dialog, View view, int index) {
                        ToastUtil.toast(HandleResultActivity.this,"正在更新业务，请稍候");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //解压缩
                                Vector<FileEntity> successList = FileUpgradeManager.getInstance().getSuccessEntityList();

                                for (final FileEntity entity : successList) {
                                    entity.decompressEntity();
                                }
                                sendMessage(CHECK_ALL_SUCCESS);
                            }
                        }).start();
                    }
                }
        );
        alertDialog.show(getSupportFragmentManager());
//        AlertDialog expDialog = DialogUtil.createAlertDialog(getSupportFragmentManager(), 0, null, updateInfo, null, null, "确定", new AlertDialog.Builder.AlertDialogClickListener() {
//
//            @Override
//            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//
//                DialogController.getInstance().showProgressDialog(me, "更新中...");
//
//            }
//        });
//
//        expDialog.setCancelable(false);
//        expDialog.show();
    }

    /**
     * 处理检测到本地文件无效
     */
    private void handleCheckFileValidate(){
//        DialogController.getInstance().showAlertDialog(
//                this,
//                "警告!",
//                "检测到您正在使用非拉卡拉官方渠道版本,为了交易安全,请卸载并从正规渠道下载安装最新版本!",
//                new AlertDialog.Builder.AlertDialogClickListener(){
//                    boolean isOver = false;
//                    @Override
//                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                        super.clickCallBack(typeEnum, alertDialog);
//                        FileUpgradeManager.getInstance().stopCheckLocalFileService();
//                        ApplicationEx.getInstance().exit();
//                        alertDialog.dismiss();
//                        me.finish();
//                        isOver = true;
//                    }
//
//                    @Override
//                    public void onDestroy() {
//                        super.onDestroy();
//                        if (!isOver){
//                            FileUpgradeManager.getInstance().stopCheckLocalFileService();
//                            ApplicationEx.getInstance().exit();
//                            me.finish();
//                        }
//                    }
//                });
        LogUtil.print("handleCheckAllSuccess");
    }

    /**
     * 发送消息
     *
     * @param what type
     */
    private void sendMessage(int what){
        Message message = handler.obtainMessage(what);
        handler.sendMessage(message);
    }
}

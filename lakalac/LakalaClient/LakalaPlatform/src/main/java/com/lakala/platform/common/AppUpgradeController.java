package com.lakala.platform.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.upgrade.AppUpgradeService;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.ui.dialog.AlertDialog;

import org.json.JSONObject;

import java.io.File;

/**
 * app升级服务
 * 区分强制升级与非强制升级,强制升级使用bindService在app内获取下载进度,如果取消安装则退出app;
 * 非强制升级使用startService并且调用系统通知获取下载进度,安装与否不影响使用;
 * <p/>
 * Created by jerry on 14-1-2.
 * Modified by LL on 15-1-5.
 */
public class AppUpgradeController implements ServiceConnection {
    private final static String KEY_SHOW_UPGRADE_TIME = "KEY_SHOW_UPGRADE_TIME";

    private FragmentActivity ctx;

    /**
     * 绑定升级ProgressDialog管理类
     */
    private UpgradeProgressDialog progressDialog = new UpgradeProgressDialog();

    /**
     * 是否需要强制升级
     */
    private boolean ForceUpdate;

    /**
     * 升级apk URL
     */
    private String ClientUrl = "";

    /**
     * 贷款是否升级
     */
    private boolean isLoanUpdate=false;

    /**
     * 绑定服务接口
     */
    private AppUpgradeService bindService;


    public boolean isAppUpdate() {
        return appUpdate;
    }

    public void setAppUpdate(boolean appUpdate) {
        this.appUpdate = appUpdate;
    }

    private boolean appUpdate = false;

    public static AppUpgradeController appUpgradeController = new AppUpgradeController();

    private AppUpgradeController() {


    }

    public void checkUpgradeMessage(FragmentActivity fragmentActivity) {
        this.ctx = fragmentActivity;
        if (appUpdate && !TextUtils.isEmpty(ClientUrl)) {
            if(ClientUrl.contains("download.lakala.com.cn/Lakala")&&ClientUrl.length()<58){
                showPromptUpgradeDialog(title, content);
            }
        }
    }

    /**
     * 初始化升级服务
     *
     * @param isManualUpgrade 是否提示已经更新到最新版
     */
    public void check(boolean isManualUpgrade, boolean showDialog) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //如果是自动更新
            if (!isManualUpgrade) {
                long showUpgradeTime = LklPreferences.getInstance().getLong(KEY_SHOW_UPGRADE_TIME, 0);
                if (showUpgradeTime != 0) {
                    //12小时以内不需要检测更新
                    long hourTime = (System.currentTimeMillis() - showUpgradeTime) / 1000 / 60 / 60;
                    if (hourTime < 12) {
                        return;
                    }
                }
            }
            checkAppUpgrade(isManualUpgrade, showDialog);
        }
    }

    public AppUpgradeController setCtx(FragmentActivity ctx) {
        this.ctx = ctx;
        return appUpgradeController;
    }

    private String title;

    private String content;

    /**
     * 检测app版本是否需要升级
     */
    private void checkAppUpgrade(final boolean isManualUpgrade, final boolean showAlert) {

        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/upgradeinfo", HttpRequest.RequestMethod.GET);

        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {


                if (!TextUtils.isEmpty(resultServices.retData) || resultServices.retData.length() <= 3) {
                    try {

                        JSONObject jsonObject = new JSONObject(resultServices.retData);

                        title = jsonObject.optString("title");
                        content = jsonObject.optString("remark");

                        ClientUrl = jsonObject.optString("url");//升级的url

                        ForceUpdate = jsonObject.optBoolean("must");
                        if (TextUtils.isEmpty(ClientUrl)) {
                            if (isManualUpgrade) {
                                ToastUtil.toast(ApplicationEx.getInstance(), "当前版本已经为最新版本，无需升级");
                            }
                            return;
                        }
                        if(!ClientUrl.contains("download.lakala.com.cn/Lakala")||ClientUrl.length()>56){
                            if(isManualUpgrade){
                                ToastUtil.toast(ApplicationEx.getInstance(), "版本信息有误");
                            }
                            return;
                        }
                        appUpdate = true;
                        if (showAlert) {
                            showPromptUpgradeDialog(title, content);
                        }
                    } catch (Exception e) {

                    }
                } else if (isManualUpgrade) {
                    ToastUtil.toast(ApplicationEx.getInstance(), "当前版本已经为最新版本，无需升级");
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                LogUtil.print(getClass().getName(), "ConnectEvent" + connectEvent);

            }
        }));
        businessRequest.execute();
    }

        /**
         * 检测app版本是否需要升级
         */
        public void checkAppUpgradeLoan() {

        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/upgradeinfo", HttpRequest.RequestMethod.GET);

        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (!TextUtils.isEmpty(resultServices.retData) || resultServices.retData.length() <= 3) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        ClientUrl = jsonObject.optString("url");//升级的url
//                        ClientUrl="http://183.56.150.170/imtt.dd.qq.com/16891/BBD3C1A808304099914133C96AA32A3A.apk?mkey=586f1ca20d45befb&f=8a5d&c=0&fsname=com.lakala.shoudan_4.5.0_58.apk&hsr=4d5s&p=.apk";
                        if (TextUtils.isEmpty(ClientUrl)) {
                            isLoanUpdate=false;
                        }else {
                            isLoanUpdate=true;
                            ForceUpdate=false;
                        }
                    } catch (Exception e) {
                        isLoanUpdate=false;
                    }
                } else {
                    isLoanUpdate=false;
                }
                showLoanDialog();
            }
            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                isLoanUpdate=false;
                showLoanDialog();
            }
        }));
        businessRequest.execute();
    }

    /**
     * 提示升级对话框
     *
     * @param descTitle  标题
     * @param clientDesc 描述
     * @return
     */
    private void showPromptUpgradeDialog(String descTitle, String clientDesc) {
        //中间显示版本更新描述的信息
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMode(true);
        alertDialog.setTitle(descTitle);
        alertDialog.setMessage(clientDesc);
        String[] buttons = ForceUpdate ? new String[]{ctx.getString(R.string.core_upgrade_lakala)} : new String[]{ctx.getString(R.string.com_cancel), ctx.getString(R.string.core_upgrade_lakala)};
        alertDialog.setButtons(buttons);
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {

            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                if (ForceUpdate) {
                    startDownload();
                } else {

                    switch (index) {
                        case 0:
                            if (ForceUpdate) {
                                ApplicationEx.getInstance().exit();
                            }
                            break;
                        case 1:
                            startDownload();
                            break;
                    }
                }
            }
        });
        if (ctx != null && !ctx.isFinishing()) {
            try {
                alertDialog.show(ctx.getSupportFragmentManager());
            } catch (Exception e) {
                LogUtil.print(e);
            }
        }
    }

    private void showLoanDialog() {
        //中间显示版本更新描述的信息
        String have="当前版本不支持该贷款业务，请升级到最新版本,或者使用其他贷款业务";
        String no="当前版本不支持该贷款业务，您可选择使用其他贷款业务";
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMode(false);
        alertDialog.setTitle("提示");
        alertDialog.setMessage(isLoanUpdate?have:no);
        String[] buttons = isLoanUpdate ? new String[]{"取消","更新"} : new String[]{"确定"};
        alertDialog.setButtons(buttons);
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                if (!isLoanUpdate) {
                    dialog.dismiss();
                } else {
                    switch (index) {
                        case 0:
                            dialog.dismiss();
                            break;
                        case 1:
                            startDownload();
                            break;
                    }
                }
            }
        });
        if (ctx != null && !ctx.isFinishing()) {
            try {
                alertDialog.show(ctx.getSupportFragmentManager());
            } catch (Exception e) {
                LogUtil.print(e);
            }
        }


    }

    public synchronized static AppUpgradeController getInstance() {
        if (appUpgradeController == null) {
            appUpgradeController = new AppUpgradeController();
        }
        return appUpgradeController;
    }

    /**
     * 开始下载升级apk,强制升级使用bindService,非强制升级使用startService
     */
    public void startDownload() {
        LogUtil.print("checkUpdate", "Download APK ClientUrl    ：" + ClientUrl);
        Intent intent = new Intent(ctx, AppUpgradeService.class);
        intent.putExtra(AppUpgradeService.KEY_URL, ClientUrl);
        if (ForceUpdate) {
            ctx.bindService(intent, this, Activity.BIND_AUTO_CREATE);
        } else {
            ctx.startService(intent);
        }
    }

    /* intent是跳转到service的intent，如 Intent intent = new Intent(); intent.setClass(this,MyService.class);
     conn则是一个代表与service连接状态的类，当我们连接service成功或失败时，
     会主动触发其内部的onServiceConnected或onServiceDisconnected方法。
     如果我们想要访问service中的数据，可以在onServiceConnected()方法中进行实现*/
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        bindService = ((AppUpgradeService.UpgradeServiceBinder) service).getService();
        bindService.setOnUpgradeProgressListener(new AppUpgradeService.OnUpgradeProgressListener() {

            @Override
            public void onStart() {
                progressDialog.showProgressDialog();
                progressDialog.updateProgress(0);
            }

            @Override
            public void onProgress(int progress) {
                progressDialog.updateProgress(progress);
            }

            @Override
            public void onComplete(File file) {
                if (progressDialog != null && progressDialog.progressDialog != null) {
                    progressDialog.progressDialog.dismiss();
                }
                showSuccessUpgradeDialog(file);
            }

            @Override
            public void onFailure() {

                if (progressDialog != null && progressDialog.progressDialog != null)
                    progressDialog.progressDialog.dismiss();
                showFailUpgradeDialog();
            }

            @Override
            public void onFinished() {
                ctx.unbindService(AppUpgradeController.this);
            }

        });
        bindService.startUpgrade(ClientUrl);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bindService = null;
    }

    /**
     * 创建升级成功对话框提示
     */
    private void showSuccessUpgradeDialog(final File file) {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(ctx.getString(R.string.core_tips));
        alertDialog.setMessage(ctx.getString(R.string.core_download_lakala_complete_prompt));
        if (ForceUpdate) {
            alertDialog.setButtons(new String[]{ctx.getString(R.string.com_confirm)});
        } else {
            alertDialog.setButtons(new String[]{ctx.getString(R.string.com_cancel), ctx.getString(R.string.com_confirm)});
        }

        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {

            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                if (ForceUpdate) {
                    bindService.installApk(file);
                } else {
                    if (index == 0 && ForceUpdate) {
                        ApplicationEx.getInstance().exit();
                    } else if (index == 1) {
                        bindService.installApk(file);
                    }
                }


            }
        });
        alertDialog.show(ctx.getSupportFragmentManager());
    }

    /**
     * 创建升级错误对话框提示
     */
    private void showFailUpgradeDialog() {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(ctx.getString(R.string.core_upgrade_lakala));
        alertDialog.setMessage(ctx.getString(R.string.core_download_lakala_fail));
        alertDialog.setButtons(new String[]{ctx.getString(R.string.com_cancel), ctx.getString(R.string.plat_retry)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {

            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                if (index == 0 && ForceUpdate) {
                    ApplicationEx.getInstance().exit();
                } else if (index == 1) {
                    startDownload();
                }
            }
        });
        alertDialog.show(ctx.getSupportFragmentManager());
    }

    /**
     * 下载拉卡拉进度对话框操作类
     */
    private class UpgradeProgressDialog {
        private ProgressDialog progressDialog;

        private void showProgressDialog() {

            progressDialog = new ProgressDialog(ctx);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(ctx.getString(R.string.core_downloading_lakala));
            progressDialog.setIcon(R.drawable.realname_help);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgress(100);

            progressDialog.show();
        }

        /**
         * 更新进度条
         *
         * @param progress 进度 (max 100)
         */
        private void updateProgress(int progress) {
            progressDialog.setProgress(progress);
        }
    }

}

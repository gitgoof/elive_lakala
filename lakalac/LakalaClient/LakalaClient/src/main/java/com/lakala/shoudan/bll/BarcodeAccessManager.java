package com.lakala.shoudan.bll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.ScancodeAccess;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.bscanc.BScanCActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.revocation.RevocationPwdVerifyActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarcodeAmountInputActivity;
import com.lakala.shoudan.activity.shoudan.barcodecollection.apply.BarcodeInstructionActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/9/16.
 */
public class BarcodeAccessManager {

    private static final String TAG = "BarcodeAccessManager";

    private AppBaseActivity context;

    private String statistic;

    private JSONObject jsonObject;
    private boolean collectionOrRevocation;
    private boolean isOn=false;

    public BarcodeAccessManager(AppBaseActivity context) {
        this.context = context;
    }

    public void check(boolean collectionOrRevocation) {

        this.collectionOrRevocation = collectionOrRevocation;
        getUpgradeStatus();
//        isProcessing();
    }


    //c扫b流程
    public void check(boolean collectionOrRevocation, boolean C_B) {
        this.collectionOrRevocation = collectionOrRevocation;
        getFirstUpgradeStatus();
    }

    private void getFirstUpgradeStatus() {
        showDialog();
        final BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_STATUS);
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String firstUpgradeStatus = jsonObject.optString("firstUpgradeStatus");
                        boolean dialogType = false;
                        boolean firstComplete = false;
                        UpgradeStatus firstStatus = UpgradeStatus.valueOf(firstUpgradeStatus);
                        switch (firstStatus) {
                            case NONE:
                                //没有申请升级
                                resultServices.retMsg = getResString(R.string.firstupgrade_none);
                                dialogType = true;
                                break;
                            case FAILURE:
                                resultServices.retMsg = getResString(R.string.firstupgrade_failure);
                                dialogType = true;
                                break;
                            case COMPLETED:
                                firstComplete = true;
                                break;
                            case PROCESSING:
                                //审核中
                                resultServices.retMsg = "您的资料已提交，请等待人工审核。";
                                break;
                        }
                        if (dialogType == true) {
                            final boolean isFirstUpgrade = !firstComplete;
                            DialogCreator.createFullContentDialog(
                                    context, "取消", "确定", resultServices.retMsg,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            context.hideProgressDialog();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            MerchantUpdateActivity.start(context, isFirstUpgrade);
                                        }
                                    }).show();
                        } else {
                            if (firstComplete)
                                isFirstUpgradeProcessing();
                            else {
                                DialogCreator.createOneConfirmButtonDialog(
                                        context, "确定", resultServices.retMsg,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                hideProgressDialog();
                            }

                        }

                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                context.hideProgressDialog();
            }
        };
        businessRequest.setResponseHandler(callback);
        businessRequest.execute();
    }


    private void getUpgradeStatus() {
        final BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_STATUS);
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String firstUpgradeStatus = jsonObject.optString("firstUpgradeStatus");
                        String secondUpgradeStatus = jsonObject.optString("secondUpgradeStatus");
                        switch (UpgradeStatus.valueOf(secondUpgradeStatus)) {
                            //二类升级状态为未申请状态
                            case NONE:
                                boolean dialogType = false;
                                boolean secondFlag = false;
                                switch (UpgradeStatus.valueOf(firstUpgradeStatus)) {
                                    case NONE:
                                        //没有申请升级
                                        resultServices.retMsg = getResString(R.string.firstupgrade_none);
                                        dialogType = true;
                                        break;
                                    case FAILURE:
                                        resultServices.retMsg = getResString(R.string.firstupgrade_failure);
                                        dialogType = true;
                                        break;
                                    case COMPLETED:
                                        secondFlag = true;
                                        dialogType = true;
                                        break;
                                    case PROCESSING:
                                        //审核中
                                        resultServices.retMsg = "您的资料已提交，请等待人工审核。";
                                        break;
                                }
                                if (secondFlag == true) {
                                    resultServices.retMsg = "您的持证资料审核已通过，请提交经营信息完成开通。";
                                }
                                if (dialogType == true) {

                                    final boolean isFirstUpgrade = !secondFlag;
                                    DialogCreator.createFullContentDialog(
                                            context, "取消", "确定", resultServices.retMsg,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    MerchantUpdateActivity.start(context, isFirstUpgrade);
                                                }
                                            }).show();
                                } else {
                                    DialogCreator.createOneConfirmButtonDialog(
                                            context, "确定", resultServices.retMsg,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                                break;
                            case FAILURE:
                                resultServices.retMsg = "很抱歉，您提交的资料审核未通过，快点击“确定”重新提交吧!";
                                DialogCreator.createFullContentDialog(
                                        context, "取消", "确定", resultServices.retMsg,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                MerchantUpdateActivity.start(context, false);
                                            }
                                        }).show();
                                break;
                            case PROCESSING:
                                resultServices.retMsg = "您的资料已提交，请等待人工审核。";
                                DialogCreator.createOneConfirmButtonDialog(
                                        context, "确定", resultServices.retMsg,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                break;
                            case COMPLETED:
                                isProcessing();
                                break;
                        }
                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }


            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                context.hideProgressDialog();
            }
        };
        businessRequest.setResponseHandler(callback);
        businessRequest.execute();
    }

    private String getResString(int resId) {
        String words = context.getResources().getString(resId);
        return words;
    }

    /**
     * 一类升级成功后进行微信开户查询
     */

    JSONObject status_json;

    private void isFirstUpgradeProcessing() {
        ShoudanService.getInstance().getBarcodeAccess(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                if (resultForService.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultForService.retData);
                        status_json = jsonObject.getJSONObject("status");
                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
//                    showNotSupportDialog(resultForService.retMsg);
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultForService.retMsg);
                }
                handleBarCodeAccessState(status_json);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
//                handleBarCodeAccessState(status_json);
            }
        });


    }

    /**
     * 一类升级成功后进入C扫B页面的校验
     *
     * @param status_json
     */
    private void handleBarCodeAccessState(JSONObject status_json) {
        if (status_json == null) {
            return;
        }
        String status_c2b = null;
        try {
            status_c2b = status_json.getString("c2b");
            user.setScancodeAccess(ScancodeAccess.valueOf(status_c2b));
            if (TextUtils.isEmpty(status_c2b))
                return;
            ScancodeAccess scancodeAccess = ScancodeAccess.valueOf(status_c2b);
            switch (scancodeAccess) {
                case NONE:
                    BScanCActivity.start(context, status_c2b);
                    break;
                case COMPLETED:
                    getMerQRCode();
                    break;
                default:
                    BScanCActivity.start(context, status_c2b);
                    break;
            }

        } catch (JSONException e) {
            context.hideProgressDialog();
            e.printStackTrace();
        }
    }

    /**
     * 获取商户的二维码
     */
    String merQrCode;

    private void getMerQRCode() {
        ShoudanService.getInstance().getMerQRCode(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        merQrCode = jsonObject.optString("merQrcode");
                        LogUtil.print(TAG,"Glide.with");
                        isOn=true;
                        Glide.with(context)
                                .load(BuildConfig.typUrl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        context.hideProgressDialog();
                                        LogUtil.print(TAG,"onResourceReady");
                                        ApplicationEx.getInstance().setBitmap(null);
                                        ApplicationEx.getInstance().setBitmap(resource);
                                        BScanCActivity.start(context, "COMPLETED", merQrCode,resource);
                                    }
                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                        super.onLoadFailed(e, errorDrawable);
                                        context.hideProgressDialog();
                                        if(isOn){
                                            isOn=false;
                                            LogUtil.print(TAG,"onLoadFailed");
                                            ApplicationEx.getInstance().setBitmap(null);
                                            BScanCActivity.start(context, "COMPLETED", merQrCode,null);
                                        }
                                    }
                                });
                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
//                BScanCActivity.start(context, "COMPLETED", merQrCode);
            }
        });

    }


    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    private User user = ApplicationEx.getInstance().getUser();

    /**
     * 二类升级完成被调用
     */
    private void isProcessing() {
        showDialog();
        ShoudanService.getInstance().getBarcodeAccess(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                if (resultForService.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultForService.retData);
                        status_json = jsonObject.getJSONObject("status");
                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    showNotSupportDialog(resultForService.retMsg);
                }
                handleAccessState();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, R.string.socket_fail);
                handleAccessState();
            }
        });

    }

    private void showNotSupportDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideProgressDialog();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("帮助", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ProtocalActivity.open(context, ProtocalType.SCAN_COLLECTION_PROTOCOL);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void showFailureDialog(String okString, String message) {
        context.showMessage(message);
    }

    private void showOpenDialog(String okString, String canccelString, String message) {

        DialogCreator.createFullContentDialog(context, okString, canccelString, message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startApply();
                        dialogInterface.cancel();
                    }
                }).show(context.getSupportFragmentManager());

    }

    private void startApply() {
        Intent intent = new Intent(context, BarcodeInstructionActivity.class);
        intent.putExtra(Constants.BARCODE_STATUS, false);
        context.startActivity(intent);
    }

    /**
     * 设置扫码收款交易次数，交易间隔,超时时间
     */
    public void setTransParam() {

        ShoudanService.getInstance().getScancodeLimit(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                if (resultForService.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultForService.retData);
                        JSONObject SIGN_PAY = jsonObject.getJSONObject("SIGN_PAY");
                        Parameters.repeatCount = SIGN_PAY.optInt("RETRY_COUNT");
                        Parameters.repeatTime = SIGN_PAY.optInt("RETRY_TIME");
                        Parameters.timeout = SIGN_PAY.optInt("TRADE_TIMEOUT");

                        if (collectionOrRevocation) {
                            checkWalletTerminal();
                        } else {
                            forwardRevocation();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultForService.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, context.getResources().getString(R.string.socket_fail));
                hideProgressDialog();
            }
        });

    }

    private void hideProgressDialog() {
        context.hideProgressDialog();
    }

    String status_b2c;

    private void handleAccessState() {

        if (status_json == null) {
            return;
        }
        try {
            status_b2c = status_json.getString("b2c");
            if (TextUtils.isEmpty(status_b2c))
                return;
            user.setScancodeAccess(ScancodeAccess.valueOf(status_b2c));
            ScancodeAccess scancodeAccess = ScancodeAccess.valueOf(status_b2c);
            switch (scancodeAccess) {
                case NOTSUPPORT:
                    hideProgressDialog();
                    ToastUtil.toast(context, "您目前还不符合开通条件，更多信息请点击帮助");
                    break;
                case NONE:
                case SUPPORT:
                    hideProgressDialog();
                    if (collectionOrRevocation)
                        //开始申请
                        startApply();
                    else
                        showOpenDialog("取消", "确定", "你还没有开通扫码收款,请点击确定进行开通");
                    break;
                case FAILURE:
                    hideProgressDialog();
                    showFailureDialog("确认", "您上次的业务开通申请没有成功，请咨询客服");
                    break;
                case PROCESSING:
                    hideProgressDialog();
                    DialogCreator.createConfirmDialog(context,
                            "确定",
                            "拉卡拉正在处理您的业务申请，请稍后再试").show();
                    break;
                case COMPLETED:
                    setTransParam();
                    break;
                case CLOSED:
                    hideProgressDialog();
                    DialogCreator.createConfirmDialog(context,
                            context.getResources().getString(R.string.ok),
                            "您暂时不能使用扫码收款业务，请稍后再试").show();
                    break;
                case UNKNOWN:
                    hideProgressDialog();
                    LogUtil.e("handleAccessState", "BarcodeAccessManager line 58");
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.toast(context, "扫码开通失败");
        }
    }

    private void showDialog() {
        context.showProgressWithNoMsg();
    }

    private void runOnUiThread(Runnable runnable) {
        ((Activity) context).runOnUiThread(runnable);
    }

    /**
     * 进行虚拟终端签到
     */
    public void checkWalletTerminal() {

        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(String msg) {
                ToastUtil.toast(context, msg);
                hideProgressDialog();
            }

            @Override
            public void onSuccess() {
                hideProgressDialog();
                forwardBarCodeCollection();
            }
        });

    }

    /**
     * 二类升级成功
     */
    private void forwardBarCodeCollection() {
        Intent intent = new Intent(context, BarcodeAmountInputActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentKey.TRANS_STATE, TransactionType.BARCODE_COLLECTION);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ShoudanStatisticManager.getInstance().onEvent(statistic, context);
    }

    private void forwardRevocation() {
        hideProgressDialog();
        Intent intent = new Intent(context, RevocationPwdVerifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentKey.TRANS_STATE, TransactionType.ScanRevocation);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


}

package com.lakala.shoudan.component;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.records.BusinessOpenActivity;
import com.lakala.shoudan.activity.shoudan.records.DrawMoneyActivity;
import com.lakala.shoudan.activity.shoudan.records.DrawMoneyServiceCloseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.util.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/5/20.
 * "立即提款"按钮点击事件
 */
public class DrawButtonClickListener implements View.OnClickListener {
    private AppBaseActivity mAct;
    private DialogInterface.OnClickListener negativeListener;
    private DialogInterface.OnClickListener positiveListener;
	private DialogInterface.OnClickListener helpListener;
    private String statistic;

    public DrawButtonClickListener(final AppBaseActivity act) {
        mAct = act;
        positiveListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                Intent intent1 = new Intent(act,
                                            BusinessOpenActivity.class);
                act.startActivity(intent1);
            }
        };
        negativeListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        };
        helpListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                ProtocalActivity.open(act, ProtocalType.D0_DESCRIPTION);
            }
        };
    }

    public DrawButtonClickListener setStatistic(String statistic) {
        this.statistic = statistic;
        return this;
    }

    @Override
    public void onClick(View v) {
        initMeInfo();
	}

    //更新商户信息
    public void initMeInfo(){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(mAct,"v1.0/getMerchantInfo", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if(resultServices.isRetCodeSuccess()){
                    try {
                        User user = ApplicationEx.getInstance().getSession().getUser();
                        JSONObject data = new JSONObject(resultServices.retData);
                        user.initMerchantAttrWithJson(data);
                        ApplicationEx.getInstance().getUser().save();

                        if (CommonUtil.isMerchantCompleted2(mAct)) {
                            mAct.showProgressWithNoMsg();
                            // getUpgradeStatus();
                            getT0Status();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(mAct,"数据解析异常");
                    }
                }else{
                    ToastUtil.toast(mAct,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(mAct,mAct.getString(R.string.socket_fail));
            }
        }));
        businessRequest.execute();
    }

    private void getUpgradeStatus(){
        BusinessRequest businessRequest= RequestFactory.getRequest(mAct, RequestFactory.Type.UPDATE_STATUS);
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String firstUpgradeStatus = jsonObject.optString("firstUpgradeStatus");
                        if (TextUtils.equals(firstUpgradeStatus, UpgradeStatus.NONE.name())
                                ||TextUtils.equals(firstUpgradeStatus,UpgradeStatus.FAILURE.name())) {
                            if(TextUtils.equals(firstUpgradeStatus, UpgradeStatus.NONE.name())){
                                resultServices.retMsg = "您的资料不完整，请点击“确定”按键完善资料";
                            }else {
                                resultServices.retMsg = mAct.getString(R.string.firstupgrade_failure);
                            }
                            mAct.hideProgressDialog();
                            DialogCreator.createFullContentDialog(
                                    mAct, "取消", "确定", resultServices.retMsg,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            MerchantUpdateActivity.start(mAct,true);
                                        }
                                    }).show();
                        }else if(TextUtils.equals(firstUpgradeStatus,UpgradeStatus.COMPLETED.name())){
                            DialogCreator.createFullContentDialog(
                                    mAct, "取消", "确定", "您还未开通“立即提款”服务，点击“确定”去开通.",
                                    negativeListener, positiveListener
                            ).show();
                        }else {
                            resultServices.retMsg = "您的资料已提交，请等待人工审核。";
                            mAct.hideProgressDialog();
                            DialogCreator.createOneConfirmButtonDialog(
                                    mAct, "确定", resultServices.retMsg,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        mAct.hideProgressDialog();
                        e.printStackTrace();
                    }
                }else {
                    mAct.hideProgressDialog();
                    ToastUtil.toast(mAct,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(mAct,mAct.getString(R.string.socket_fail));
                mAct.hideProgressDialog();
            }
        };
        businessRequest.setResponseHandler(callback);
        businessRequest.execute();
    }

    private void getT0Status(){
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                T0Status status = T0Status.NOTSUPPORT;
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData.toString());
                        int length = 0;
                        if(jsonArray != null){
                            length = jsonArray.length();
                        }
                        for(int i = 0;i<length;i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if(jsonObject.isNull("inpan")){
                                continue;
                            }
                            String s = jsonObject.getString("inpan");
                            status = T0Status.valueOf(s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(TextUtils.equals(resultServices.retCode,"005020")){
                    status = T0Status.NOTSUPPORT;
                }else if(TextUtils.equals(resultServices.retCode,"001023")){
                    status = T0Status.UNKNOWN;
                }else if(TextUtils.equals(resultServices.retCode,"005043")
                        ||TextUtils.equals(resultServices.retCode,"005044")){
                    //未升级或升级失败
                    mAct.hideProgressDialog();
                    DialogCreator.createFullContentDialog(
                            mAct,"取消", "确定", resultServices.retMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    MerchantUpdateActivity.start(mAct,true);
                                }
                            }).show();
                    return;
                }else if(TextUtils.equals(resultServices.retCode,"005045")){
                    //一类升级审核中
                    mAct.hideProgressDialog();
                    DialogCreator.createOneConfirmButtonDialog(
                            mAct, "确定", resultServices.retMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return;
                }else if(TextUtils.equals(resultServices.retCode,"050506")){
                    //是核心商户
                    mAct.hideProgressDialog();
                    DialogCreator.createOneConfirmButtonDialog(
                            mAct, "确定", resultServices.retMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return;
                }
                status.setErrMsg(resultServices.retMsg);
                dealResult(status);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                mAct.hideProgressDialog();
            }
        };
        ShoudanService.getInstance().isOpenD0(callback);
    }

    private void dealResult(final T0Status status) {
        if(status != T0Status.UNKNOWN){//更新T0开户状态
            ApplicationEx.getInstance().getUser().getMerchantInfo().setT0(status);
        }
        {
            if (status != T0Status.COMPLETED) {
                mAct.hideProgressDialog();
            }
            switch (status) {
                case COMPLETED: {
                    getDrawEnable();
                    break;
                }
                case SUPPORT:
                case FAILURE: {
                    DialogCreator.createFullContentDialog(
                            mAct, "取消", "确定", "您还未开通“立即提款”服务，点击“确定”去开通.",
                            negativeListener, positiveListener
                    ).show();
//                    getUpgradeStatus();
                    break;
                }
                case NOTSUPPORT: {
                    DialogCreator.createFullContentDialog(
                            mAct, "知道了", "帮助", status.getErrMsg(), negativeListener,
                            helpListener
                    ).show();
                    break;
                }
                case PROCESSING: {
                    DialogCreator.createConfirmDialog(
                            mAct, "确定", "您已成功申请立即提款业务，系统处理中，请稍候"
                    ).show();
                    break;
                }
                default: {
                    LogUtil.e(mAct.getLocalClassName(), "错误的状态");
                    String msg = status.getErrMsg();
                    if (TextUtils.isEmpty(msg)) {
                        msg = mAct.getString(R.string.socket_fail);
                    }
                    ToastUtil.toast(mAct,msg);
                    break;
                }
            }
        }
    }


    private void getDrawEnable(){
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                mAct.hideProgressDialog();
                analyzeDrawEnableResult(resultServices);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                mAct.hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAccountBalance(callback);
    }

    private void analyzeDrawEnableResult(final ResultServices resultForService) {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject retData;
                if(resultForService == null){
                    ToastUtil.toast(mAct,mAct.getString(R.string.socket_fail));
                    return;
                }
                if (resultForService.isRetCodeSuccess()) {
                    try {
                        retData = new JSONObject(resultForService.retData.toString());
                        boolean drawEnable = retData.getBoolean("enable");
                        if (drawEnable) {
                            Intent intent2 = new Intent(mAct, DrawMoneyActivity.class);
                            intent2.putExtra("jsonStr", retData.toString());
                            mAct.startActivity(intent2);
                            if(!TextUtils.isEmpty(statistic)){
                                ShoudanStatisticManager.getInstance().onEvent(statistic, mAct);
                            }
                        } else {
                            //非提款时段提款
                            Intent intent2 = new Intent(mAct, DrawMoneyServiceCloseActivity.class);
                            intent2.putExtra("jsonStr", retData.toString());
                            mAct.startActivity(intent2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("110601".equals(resultForService.retCode)) {
                    DialogCreator.createConfirmDialog(mAct, "确定", "拉卡拉正在处理您的业务申请，请稍后再试")
                            .show();
                } else {
                    String msg = resultForService.retMsg;
                    if(TextUtils.isEmpty(msg)){
                        msg = "网络异常";
                    }
                    ToastUtil.toast(mAct,msg);
                }
            }
        });
    }
}

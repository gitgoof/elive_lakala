package com.lakala.shoudan.activity.quickArrive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.records.SecondOpenActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一日贷申请
 * Created by fengx on 2015/9/11.
 */
public class OneDayLoanApplyActivity extends AppBaseActivity implements View.OnClickListener {

    private static final String TAG = "OneDayLoanApplyActivity";
    private WebView wvBarcode;
    private TextView btnApply;
    private static String URL1 = "https://download.lakala.com/lklmbl/html/skb_yrd_yrdNote.html";
    private DialogInterface.OnClickListener negativeListener;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener helpListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_unopen);
        initUI();
    }


    @Override
    protected void initUI() {
        super.initUI();
//        if (getIntent() != null)
//            URL1 = getIntent().getStringExtra("url");
        navigationBar.setTitle("一日贷业务说明");
        wvBarcode = (WebView) findViewById(R.id.wv_barcode);
        wvBarcode.loadUrl(URL1);
        btnApply = (TextView) findViewById(R.id.btn_barcode_apply);
        btnApply.setOnClickListener(this);

        negativeListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_jsType_Leave, OneDayLoanApplyActivity.this);
            }
        };
        helpListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                ProtocalActivity.open(OneDayLoanApplyActivity.this, ProtocalType.D0_DESCRIPTION);
            }
        };

        //设置埋点
        if(PublicEnum.Business.isWithdrawal()){
            //从立即提款过来的
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal, this);
            LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal);
        }else if(PublicEnum.Business.isDirection()){
            //定向业务-一日贷
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction, this);
            LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction);
        }
    }


    @Override
    public void onClick(View view) {

        getT0Status();
    }



    private void getT0Status(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                T0Status status = T0Status.NOTSUPPORT;
                LogUtil.print("<S>", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
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
                            LogUtil.print("<><><>",s);
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
                    hideProgressDialog();
                    DialogCreator.createFullContentDialog(
                            OneDayLoanApplyActivity.this,"取消", "确定", resultServices.retMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    MerchantUpdateActivity.start(OneDayLoanApplyActivity.this,true);
                                }
                            }).show();
                    return;
                }else if(TextUtils.equals(resultServices.retCode,"005045")){
                    //一类升级审核中
                    hideProgressDialog();
                    DialogCreator.createOneConfirmButtonDialog(
                            OneDayLoanApplyActivity.this, "确定", resultServices.retMsg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return;
                } else if(TextUtils.equals(resultServices.retCode,"050506")){
                    //是核心商户
                    hideProgressDialog();
                    DialogCreator.createOneConfirmButtonDialog(
                            OneDayLoanApplyActivity.this, "确定", resultServices.retMsg,
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
                hideProgressDialog();
            }
        };
        ShoudanService.getInstance().isOpenOnDayLoan(callback);
    }

    private void dealResult(final T0Status status) {
        if(status != T0Status.UNKNOWN){//更新T0开户状态
            ApplicationEx.getInstance().getUser().getMerchantInfo().setT0(status);
        }
        {
//            if (status != T0Status.COMPLETED) {
                hideProgressDialog();
//            }
            switch (status) {
                case COMPLETED:
                    break;
                case ONEDAYLOAN:
                    break;
                case SUPPORT:
                case FAILURE: {
                    //设置埋点
                    if(PublicEnum.Business.isWithdrawal()){
                        //从立即提款过来的
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied, this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied);

                    }else if(PublicEnum.Business.isDirection()){
                        //定向业务-一日贷-业务说明-申请提交
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied, this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied);

                    }
                    Intent intent1 = new Intent(OneDayLoanApplyActivity.this,
                            SecondOpenActivity.class);
                    intent1.putExtra("isOne",true);
                    OneDayLoanApplyActivity.this.startActivity(intent1);
                    finish();
                    break;
                }
                case NOTSUPPORT: {
//                    DialogCreator.createFullContentDialog(
//                            OneDayLoanApplyActivity.this, "知道了", "帮助", status.getErrMsg(), negativeListener,
//                            helpListener
//                    ).show();
                    DialogCreator.createConfirmDialog(
                            this, "确定", status.getErrMsg()).
                            show();
                    break;
                }
                case PROCESSING: {
                    DialogCreator.createConfirmDialog(
                            this, "确定", "您已成功申请立即一日贷，系统处理中，请稍候"
                    ).show();
                    break;
                }
                default: {
                    LogUtil.e(this.getLocalClassName(), "错误的状态");
                    String msg = status.getErrMsg();
                    if (TextUtils.isEmpty(msg)) {
                        msg = this.getString(R.string.socket_fail);
                    }
                    ToastUtil.toast(this,msg);
                    break;
                }
            }
        }
    }
}

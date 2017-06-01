package com.lakala.shoudan.activity.happybean;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardAdditionActivity;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.treasure.TreasureSMSCheckActivity;
import com.lakala.shoudan.activity.treasure.TreasureSwipePaymentActivity;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.PayType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjp on 2016/5/17.
 */
public class HappyBeanRechargeActivity extends AppBaseActivity {
    private static final int PAY_PWD_REQUEST = 0x2343;
    private HappyBeanTransInfo happyBeanTransInfo;

    private Button btnPay;
    private LinearLayout llReceiver;
    private LinearLayout llRechargeType;
    private TextView tvAmount;
    private TextView tvBuyTypeName;
    private List<PayType> buyTypes = new ArrayList<>();

    private Double walletBalance = 0d;
    private PayType selectedBuyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happybean_recharge);
        init();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("欢乐豆充值");
    }
    private void init(){
        initUI();
        initView();
        initData();
    }

    private void initData(){

        happyBeanTransInfo=new HappyBeanTransInfo();
    }
    private void initView(){
        btnPay=(Button)findViewById(R.id.btn_pay);
        llReceiver=(LinearLayout)findViewById(R.id.ll_receiver);
        llRechargeType=(LinearLayout)findViewById(R.id.ll_recharge_type);
        tvAmount=(TextView)findViewById(R.id.tv_amount);
        tvBuyTypeName = (TextView) findViewById(R.id.tv_buy_type_name);
        btnPay.setOnClickListener(guideListener);
        llRechargeType.setOnClickListener(buyTypeListener);
    }

    View.OnClickListener buyTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showBuyTypeDialog();
        }
    };

    private void showBuyTypeDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectTypePostion(which);
            }
        };
        DialogCreator.showPayTypeChooseDialog(context, listener, buyTypes);
    }

    private void selectTypePostion(int which) {
        PayType tmpType = buyTypes.get(which);
        String lastAppendStr = "";
        if(TextUtils.equals("W", tmpType.PaymentType)){//钱包
            lastAppendStr = new StringBuilder().append("  ")
                    .append(Util.formatTwo(walletBalance)).append("元")
                    .toString();
        }else{

        }
        if(TextUtils.isEmpty(tmpType.PaymentType)){
            //使用新卡支付
            Intent intent = new Intent(context, BankCardAdditionActivity.class);
            startActivity(intent);
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(tmpType.getShowText());
        if(!TextUtils.isEmpty(lastAppendStr)){
            text.append(lastAppendStr);
        }
        tvBuyTypeName.setText(text.toString());
        selectedBuyType = tmpType;
//        typePosition = which;
    }
    View.OnClickListener guideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //下一步
            if(TextUtils.equals("C",selectedBuyType.PaymentType)){
                //刷卡交易
                Intent intent = new Intent(context,TreasureSwipePaymentActivity.class);
                intent.putExtra(ConstKey.TRANS_INFO,happyBeanTransInfo);
                startActivity(intent);
            }
//            else if(TextUtils.equals("2",mData.getPaymentTypes().getSafetyToolFlag())){//未设置支付密码
//
//                DialogCreator.createFullContentDialog(
//                        context, "取消", "设置", "为了保证您的交易安全，需要先设置支付密码和密保问题",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                Intent intent = new Intent(context, SetPaymentPasswordActivity.class);
//                                intent.putExtra("fromOneTreasure",true);
//                                startActivity(intent);
//                                updateOnResume = true;
//                            }
//                        }
//                ).show();
//            }
            else{
//                if(needWalletRechange){
//                    toast("零钱余额不足");
//                    return;
//                }
                Intent intent = new Intent(context,InputPayPwdDialogActivity.class);
                startActivityForResult(intent,PAY_PWD_REQUEST);
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAY_PWD_REQUEST && resultCode == RESULT_OK){
            String pwdTmp = data.getStringExtra(Constants.IntentKey.PASSWORD);
            if(TextUtils.equals("W",selectedBuyType.PaymentType)){
                queryWallet(pwdTmp);
            }else if(TextUtils.equals("S",selectedBuyType.PaymentType)){
                //获取短信验证码
//                TreasureSMSCheckActivity.open(context,selectedBuyType.shortCard,dataStr,pwdTmp);
            }
        }
    }
    private void queryWallet(String pwd){
        BusinessRequest businessRequest= HappyBeanRequestFactory.getRequest(context,HappyBeanRequestFactory.HappyBeanType.VERIFY_PAY_PWD);
        //TODO huangjp 接口调用
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {

                }else{
                    hideProgressDialog();
                    toast(resultServices.retMsg);
                    happyBeanTransInfo.setMsg(resultServices.retMsg);
                    happyBeanTransInfo.setTransResult(TransResult.FAILED);
                    tradeOver2Result();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        });
    }
    private void tradeOver2Result(){
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        intent.putExtra(ConstKey.TRANS_INFO,happyBeanTransInfo);
        startActivity(intent);
    }
}

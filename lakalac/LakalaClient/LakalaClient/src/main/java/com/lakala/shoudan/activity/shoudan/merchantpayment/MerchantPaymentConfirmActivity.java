package com.lakala.shoudan.activity.shoudan.merchantpayment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 14-6-10.
 *
 * 特约商户缴费金额确认页面
 *
 */
public class MerchantPaymentConfirmActivity extends AppBaseActivity {
    private MerchantPayTransInfo merchantPayTransInfo;
    private EditText instBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_payment_confirm);
        merchantPayTransInfo = (MerchantPayTransInfo) getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        TextView tvAmount = (TextView)findViewById(R.id.amount);
        tvAmount.setText(Util.formatDisplayAmount(merchantPayTransInfo.getAmount()));
        instBill = (EditText)findViewById(R.id.edit_merchant_no);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(checkInput()){
                    queryContributePayment(instBill.getText().toString());
                }else{
                    toast("请输入正确的商户编号");
                }
            }
        });
        navigationBar.setTitle("特约商户缴费");
    }

    private boolean checkInput(){
        if(instBill.getText() == null || instBill.getText().toString().length() == 0){
            return false;
        }
        return true;
    }

    private void feeQuery() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String fee = Util.fen2Yuan(jsonObject.getString("fee"));
                        String price = Util.fen2Yuan(jsonObject.getString("price"));
                        merchantPayTransInfo.setPrice(price);
                        merchantPayTransInfo.setFee(fee);
                        merchantPayTransInfo.setFeeSid(jsonObject.getString("sid"));
                        Intent intent = new Intent();
                        intent.setClass(context, MerchantPaySwiperActivity.class);
                        intent.putExtra(Constants.IntentKey.TRANS_INFO,merchantPayTransInfo);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onEvent(HttpConnectEvent.RESPONSE_ERROR);
                    }
                }else{
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().queryCtrbtFee(merchantPayTransInfo.getInstBill(),
                                                   merchantPayTransInfo.getQuerySid(),
                                                   merchantPayTransInfo.getAmount(),
                                                   merchantPayTransInfo.getBillNo(),
                                                   callback
        );
    }

    private void queryContributePayment(final String instBill){
        merchantPayTransInfo.setInstBill(instBill);
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                String sid = "";
                String billNo = "";
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject retData = new JSONObject(resultServices.retData);
                        if(retData.has("sid")){
                            sid = retData.getString("sid");
                        }
                        merchantPayTransInfo.setQuerySid(sid);
//                        if(retData.has("billno")){
//                            billNo = retData.getString("billno");
//                        }
                        String isbind = retData.getString("isbind");

                        merchantPayTransInfo.setBind(isbind);
                        if(retData.has("bmercname")){
                            merchantPayTransInfo.setMercname(retData.getString("bmercname"));
                        }
                        if(retData.has("address")){
                            merchantPayTransInfo.setAddress(retData.getString("address"));
                        }

                        if(merchantPayTransInfo.isBind()){
                            feeQuery();
                        }else{
                            hideProgressDialog();
                            ToastUtil.toast(context,"您尚未绑定该商户编号");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onEvent(HttpConnectEvent.RESPONSE_ERROR);
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        String bill=merchantPayTransInfo.getInstBill();
        ShoudanService.getInstance().contributeQuery(bill, callback);
    }

}

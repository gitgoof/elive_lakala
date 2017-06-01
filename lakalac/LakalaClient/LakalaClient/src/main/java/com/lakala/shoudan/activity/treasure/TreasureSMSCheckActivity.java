package com.lakala.shoudan.activity.treasure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.wallet.request.QrySMSParams;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.CaptchaTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LMQ on 2015/12/29.
 */
public class TreasureSMSCheckActivity extends AppBaseActivity {
    private TextView tips;
    private String tipsString = "为保证交易安全,已经将验证码发送至手机:%s,请等待,过期时间30分钟!";
    private CaptchaTextView tvGetCaptcha;
    private QrySMSParams qrySmsParams;
    private EditText etInputCaptcha;
    private PartnerPayResponse mData;
    private String pwd;
    private PayType.ShortCard shortCard;
    private Map<String,String> smsRetParams = new HashMap<>();
    private TreasureTransInfo transInfo;

    public static void open(Context context, PayType.ShortCard shortCard, String data, String pwd){
        Intent intent = new Intent(context,TreasureSMSCheckActivity.class);
        intent.putExtra("shortCard",shortCard.getTreasureJSONObject().toString());
        intent.putExtra("data",data);
        intent.putExtra("pwd",pwd);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_sms);
        initPreData();
        initUI();


        transInfo = new TreasureTransInfo();
        transInfo.setAmount(mData.getOrderAmount());
        transInfo.setBillId(mData.getBillId());
        transInfo.setLakalaOrderId(mData.getLakalaOrderId());
        transInfo.setProductName(mData.getSubject());
        transInfo.setParams(mData.getParams());
        transInfo.setNotifyURL(mData.getNotifyURL());
    }

    /**初始化前一个页面传过来的数据*/
    private void initPreData() {
        qrySmsParams = new QrySMSParams(context);

        String data = getIntent().getStringExtra("data");
        try {
            JSONObject jsonObject = new JSONObject(data);
            mData = PartnerPayResponse.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pwd = getIntent().getStringExtra("pwd");

        String jsonStr = getIntent().getStringExtra("shortCard");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            shortCard = PayType.ShortCard.parseTreasure(jsonObject);
            qrySmsParams.setBillAmount(mData.getOrderAmount());
            qrySmsParams.setMobileInBank(shortCard.getMobileInBank());
            qrySmsParams.setPayerAcNo(shortCard.getAccountNo());
            qrySmsParams.setBillId(mData.getBillId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("输入验证码");
        tips = (TextView)findViewById(R.id.tips);
        tips.setText(String.format(tipsString,qrySmsParams.getMobileInBank()));
        etInputCaptcha = (EditText)findViewById(R.id.et_input_captcha);
        etInputCaptcha.setOnFocusChangeListener(new HintFocusChangeListener());

        tvGetCaptcha = (CaptchaTextView)findViewById(R.id.tv_get_captcha);
        Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCaptcha();
                    }
                }
        );
        findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trade();
            }
        });
    }

    private void getCaptcha() {
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.QRY_PAYSMS);
        showProgressWithNoMsg();
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    tvGetCaptcha.startCaptchaDown(60);
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String srcsid = jsonObject.optString("srcsid");
                        String ordno = jsonObject.optString("ordno");
                        String ordtime = jsonObject.optString("ordtime");
                        String ordexpdat = jsonObject.optString("ordexpdat");
                        smsRetParams.put("srcsid",srcsid);
                        smsRetParams.put("ordno",ordno);
                        smsRetParams.put("ordtime",ordtime);
                        smsRetParams.put("ordexpdat",ordexpdat);
                        smsRetParams.put("isFirstSub",qrySmsParams.getIsFirstSub());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    tvGetCaptcha.resetCaptchaDown();
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                tvGetCaptcha.resetCaptchaDown();
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(qrySmsParams,request);
    }

    private Point getViewSize(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
    }
    private void storageViewSize(View view,Point size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null){
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
    }

    private void trade(){
        String busid = "19U";
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.WALLET_TRADE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                try {
                    hideProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    if (resultServices.isRetCodeSuccess()) {
                        transInfo.setTransResult(TransResult.SUCCESS);
                    } else {
                        transInfo.setTransResult(TransResult.FAILED);
                        transInfo.setMsg(resultServices.retMsg);
                    }
                    String url = jsonObject.optString("callBackURL", "");
                    String params = jsonObject.optString("orderid","");
                    String jnlId = jsonObject.optString("jnlId","");
                    transInfo.setJnlId(jnlId);
                    transInfo.setCallbackUrl(url, params);
                    tradeOver2Result();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                transInfo.setMsg(getString(R.string.socket_error));
                transInfo.setTransResult(TransResult.TIMEOUT);
                tradeOver2Result();
            }
        });
        JSONArray typesArray = new JSONArray();
        JSONObject jsonObject = shortCard.getTreasureJSONObject();
        try {
            jsonObject.put("sMSCode",etInputCaptcha.getText().toString());
            jsonObject.put("paymentType","S");
            jsonObject.put("paymentAmount",mData.getOrderAmount());
            Set<String> keys = smsRetParams.keySet();
            for(String key:keys){
                jsonObject.put(key,smsRetParams.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        typesArray.put(jsonObject);
        WalletTradeRequest params = new WalletTradeRequest(context);
        params.setBusid(busid);//刷卡 钱包 走1CN;;快捷走的19U
        params.setBillId(mData.getBillId());
        params.setLastPaymentType("S");
        params.setAmount(mData.getOrderAmount());
        params.setPaymentTypes(typesArray.toString());
        params.setTerminalId(ApplicationEx.getInstance().getUser().getTerminalId());
        params.setOrderToPayFlag("1");
        params.setLakalaOrderId(mData.getLakalaOrderId());
        params.setParams(mData.getParams());
        params.setNotifyURL(mData.getNotifyURL());
        if(!TextUtils.isEmpty(pwd)){
            params.setTrsPassword(pwd);
        }
        params.setFee(Utils.yuan2Fen("0"));
        params.setPan(qrySmsParams.getPayerAcNo());
        showProgressWithNoMsg();
        WalletServiceManager.getInstance().start(params, request);
    }

    private void tradeOver2Result(){
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        intent.putExtra(ConstKey.TRANS_INFO, transInfo);
        startActivity(intent);
    }

}

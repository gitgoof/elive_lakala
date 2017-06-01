package com.lakala.shoudan.activity.shoudan.bankitcard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.password.PayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.BankCardBean;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.activity.wallet.request.QuickSignRequest;
import com.lakala.shoudan.activity.wallet.request.QuickSmsUnsignrRequest;
import com.lakala.shoudan.activity.wallet.request.QuickUnsignRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.ui.component.CaptchaTextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 快捷签约、解约短信验证页面
 * Created by HJP on 2015/11/23.
 */
public class SMSVerificationActivity extends AppBaseActivity {
    private TextView tvTips;
    private CaptchaTextView tvGetCaptcha;
    private EditText etInputCaptcha;
    private TextView idCommonGuideButton;
    private String agstat;//签约状态
    private String mobileInBank;//银行预留手机号
    private QuickCardBinBean quickCardBinBean;
    private String sid;
    private String srcid;//SID值
    private BankCardBean bankCardBean;
    private String signOrUnsign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_pay);
        init();
    }
    public  void init(){
        initUI();
        quickCardBinBean= (QuickCardBinBean) getIntent().getSerializableExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN);
        signOrUnsign=getIntent().getStringExtra(Constants.IntentKey.SIGN_OR_UNSIGN);
        bankCardBean= (BankCardBean) getIntent().getSerializableExtra(Constants.IntentKey.BANK_CARD_BEAN);

        if(signOrUnsign.equals("sign")){
            mobileInBank=quickCardBinBean.getMobileInBank();
        }else{
            mobileInBank=bankCardBean.getBkMobile();
        }


        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvGetCaptcha = (CaptchaTextView) findViewById(R.id.tv_get_captcha);
        etInputCaptcha = (EditText) findViewById(R.id.et_input_captcha);
        idCommonGuideButton =(TextView)findViewById(R.id.id_common_guide_button);
        Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvTips.setText("请输入手机尾号" + mobileInBank.substring(7, 11) + "收到的短信验证码：");
        etInputCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if((etInputCaptcha.getText().toString().trim()).length()>=6){
                    idCommonGuideButton.setEnabled(true);
                }else{
                    idCommonGuideButton.setEnabled(false);
                }
            }
        });
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(signOrUnsign.equals("sign")){
                            getSignSMSCode();
                        }else{
                            getUnsignSMSCode();
                        }


                    }
                }
        );
        idCommonGuideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(etInputCaptcha.getText().toString())){
                            if(signOrUnsign.equals("sign")){
                                quickSign();
                            }else{
                                quickUnsign();
                            }
                        }else{
                            ToastUtil.toast(context,"验证码不能为空！");
                        }


                    }
                }
        );
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.sms_verification_title);
    }


    private void storageViewSize(View view,Point size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null){
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
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
    /**
     * 获取快捷签约短信验证码
     */
    private void getSignSMSCode(){
        showProgressWithNoMsg();
        QuickSignRequest params=new QuickSignRequest(context);
        params.setCustomerName(quickCardBinBean.getCustomerName());//签约人名称
        params.setIdentifierType(quickCardBinBean.getIdentifierType());//签约人证件类型
        params.setIdentifier(quickCardBinBean.getIdentifier());//签约人证件号
        params.setBankName(quickCardBinBean.getBankName());//签约银行名称
        params.setBankCode(quickCardBinBean.getBankCode());//签约银行行号
        params.setMobileInBank(quickCardBinBean.getMobileInBank());//银行预留手机号
        params.setSupportSMS("1");//快捷签约是否银行支持发送短信验证码：1：支持；0：不支持
//        params.setIsShortcutSign("1");//是否签约快捷 实名认证时选择，1：是；0：否
        params.setAuthFlag(quickCardBinBean.getAuthFlag());//实名认证标识
        params.setAccountNo(quickCardBinBean.getAccountNo());//银行卡号
        params.setAccountType(quickCardBinBean.getAccountType());//签约卡类型
        params.setCVN2(quickCardBinBean.getCVN2());//信用卡必传
        params.setCardExp(quickCardBinBean.getCardExp());//信用卡必传
        BusinessRequest businessRequest=RequestFactory.getRequest(this,RequestFactory.Type.QUICK_SMS_SIGN);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    tvGetCaptcha.startCaptchaDown(59);
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        srcid=jsonObject.optString("sid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    toast(resultServices.retMsg);
                    tvGetCaptcha.setEnabled(true);
                    tvGetCaptcha.setText("重新获取");
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params,businessRequest);
    }
    /**
     * 获取快捷解约短信验证码
     */
    private void getUnsignSMSCode(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.QUICK_SMS_UNSIGN);
        QuickSmsUnsignrRequest params=new QuickSmsUnsignrRequest(context);
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());//用户手机号
        params.setAccountNo(bankCardBean.getAccountNo());//签约卡卡号
        params.setCardId(bankCardBean.getCardId());//银行卡ID
        params.setMobileInBank(bankCardBean.getBkMobile());//银行预留手机号
        params.setAccountType(bankCardBean.getAccountType());//签约卡类型 1：借记卡；2：信用卡
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    tvGetCaptcha.startCaptchaDown(59);
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        sid=jsonObject.optString("sid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    toast(resultServices.retMsg);
                    tvGetCaptcha.setEnabled(true);
                    tvGetCaptcha.setText("重新获取");
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params,businessRequest);
    }
    /**
     * 快捷解约成功失败接口
     */
    private void quickUnsign(){
        showProgressWithNoMsg();
        QuickUnsignRequest params=new QuickUnsignRequest(context);
        params.setAccountNo(bankCardBean.getAccountNo());
        params.setAccountType(bankCardBean.getAccountType());
        params.setSid(sid);
        params.setSMSCode(etInputCaptcha.getText().toString().trim());
        params.setCardId(bankCardBean.getCardId());
        params.setCardId(bankCardBean.getSignFlag());
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.QUICK_UNSIGN);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            Intent intent = new Intent();
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    ToastUtil.toast(context,"删除成功");
                    intent.putExtra("back_delete_result", bankCardBean.getPosition());
                }else{
                    ToastUtil.toast(context, "删除失败");
                    intent.putExtra("back_delete_result", 0);
                }
                setResult(ConstKey.RESULT_DELETE_BACK,intent);
                finish();

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
                intent.putExtra("back_delete_result", "delet_fail");
                setResult(ConstKey.RESULT_DELETE_BACK, intent);
                finish();
            }
        });
        WalletServiceManager.getInstance().start(params,businessRequest);
    }

    /**
     * 快捷签约成功失败接口
     */
    private void quickSign(){
        String code = etInputCaptcha.getText().toString();
        if(TextUtils.isEmpty(code)){
            ToastUtil.toast(context,"请输入验证码");
        }else{
            showProgressWithNoMsg();
            BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.QUICK_SIGN);
            QuickSignRequest params=new QuickSignRequest(context);
            params.setCustomerName(quickCardBinBean.getCustomerName());//签约人名称
            params.setIdentifierType(quickCardBinBean.getIdentifierType());//证件类型
            params.setIdentifier(quickCardBinBean.getIdentifier());//证件号码 01：身份证
            params.setBankName(quickCardBinBean.getBankName());//银行名称
            params.setBankCode(quickCardBinBean.getBankCode());//银行行号
            params.setMobileInBank(quickCardBinBean.getMobileInBank());//银行预留手机号
//            params.setSupportSMS("1");//是否支持短信发送
            params.setAccountNo(quickCardBinBean.getAccountNo());//账户号
            params.setAccountType(quickCardBinBean.getAccountType());//签约卡类型  1:借记卡 2:信用卡
            params.setCVN2(quickCardBinBean.getCVN2());//信用卡CVN2/CVV2 信用卡必送
            params.setCardExp(quickCardBinBean.getCardExp());//信用卡有效期 信用卡必送
            params.setOTPPassword(code);//短信验证码
            params.setSrcid(srcid);//查询的SID值
            businessRequest.setResponseHandler(new ServiceResultCallback() {
                Intent intent = new Intent();
                @Override
                public void onSuccess(ResultServices resultServices) {
                    hideProgressDialog();
                    if (resultServices.isRetCodeSuccess()) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultServices.retData);
                            agstat = jsonObject.getString("agstat");
                            if (TextUtils.isEmpty(agstat)) {
                                return;
                            } else {
                                //agstat 签约状态 C n1 0:签约成功 1:签约失败
                                if (agstat.equals("0")) {
                                    ToastUtil.toast(context, R.string.contract_sucess);
                                    intent.putExtra("back_result", "add_success");
                                    intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN,quickCardBinBean);
                                    setResult(ConstKey.RESULT_ADD_BACK, intent);
                                    finish();
                                }else {
                                    ToastUtil.toast(context,resultServices.retMsg);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        toast(resultServices.retMsg);
                        finish();
                    }
                }

                @Override
                public void onEvent(HttpConnectEvent connectEvent) {
                    hideProgressDialog();
                    ToastUtil.toast(context, R.string.socket_fail);
                    LogUtil.print(connectEvent.getDescribe());
                    intent.putExtra("back_result", "add_fail");
                    setResult(ConstKey.RESULT_ADD_BACK, intent);
                    finish();
                }
            });
            WalletServiceManager.getInstance().start(params,businessRequest);
        }

    }
}

package com.lakala.shoudan.activity.shoudan.finance.question;

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

import com.android.volley.VolleyError;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.open.SetPayPwdActivity;
import com.lakala.ui.component.CaptchaTextView;

import android.widget.Button;

import org.json.JSONObject;

/**
 * Created by LMQ on 2015/10/20.
 */
public class CaptchaPayActivity extends AppBaseActivity {

    protected TextView tvTips;
    protected CaptchaTextView tvGetCaptcha;
    protected EditText etInputCaptcha;
    protected TextView idCommonGuideButton;

    public static void open(Activity activity){
        Intent intent = new Intent(activity,CaptchaPayActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_pay);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("找回支付密码");
        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvGetCaptcha = (CaptchaTextView) findViewById(R.id.tv_get_captcha);
        etInputCaptcha = (EditText) findViewById(R.id.et_input_captcha);
        idCommonGuideButton = (TextView) findViewById(R.id.id_common_guide_button);
        Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSMSCode();
                    }
                }
        );
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
        idCommonGuideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifySMSCode();
                    }
                }
        );
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
    protected void verifySMSCode(){
        String code = etInputCaptcha.getText().toString();
        if(TextUtils.isEmpty(code)){
            ToastUtil.toast(context, "请输入验证码");
        }
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    Intent intent=new Intent(CaptchaPayActivity.this,SetPaymentPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    ToastUtil.toast(context, returnHeader.getErrMsg());

                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().verifySMSCode(code,listener);
    }

    /**
     * 获取短信验证码
     */
    protected void getSMSCode(){
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    tvGetCaptcha.startCaptchaDown(59);
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                    tvGetCaptcha.resetCaptchaDown();
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

                ToastUtil.toast(context,R.string.socket_fail);
                tvGetCaptcha.resetCaptchaDown();
            }
        };
        FinanceRequestManager.getInstance().getSMSCode(listener);
    }
}

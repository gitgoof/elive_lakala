package com.lakala.elive.user.activity;


import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ForgetCodeReq;
import com.lakala.elive.beans.ForgetSubmitReq;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;


/**
 * 用户短信或者邮箱验证
 *
 * @author hongzhiliang
 */
public class UserCheckActivity extends BaseActivity {

    Session mSession;
    private String name;

    //配置信息
    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_user_check);
    }


    private Button userCheckBtn;//用户认证提交按钮
    private Button checkSendBtn; //验证码发送按钮
    private EditText etCheckcode;  //手机号和密码重置输入框
    //标题名称
    private TextView tvTitleName = null;
    private ImageView btnIvBack;

    @Override
    protected void bindView() {
        name = getIntent().getStringExtra("login_name");
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        tvTitleName.setText(R.string.home_check_ui);
        btnIvBack = (ImageView) findViewById(R.id.btn_iv_back);
        btnIvBack.setVisibility(View.VISIBLE);
        userCheckBtn = (Button) findViewById(R.id.btn_user_check);//提交服务端验证
        checkSendBtn = (Button) findViewById(R.id.btn_checkcode_send); //验证码发送
        etCheckcode = (EditText) findViewById(R.id.et_user_checkcode);
        if (!TextUtils.isEmpty(name)) {
            userCheckBtn.setText("重置密码");
        }
    }

    @Override
    protected void bindEvent() {
        userCheckBtn.setOnClickListener(this);
        checkSendBtn.setOnClickListener(this);
        btnIvBack.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        mSession = Session.get(this);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_CHECK_CODE:
                Utils.showToast(this, "发送成功!");
                break;
            case NetAPI.ACTION_FORGET_SUBMIT:
                Utils.showToast(this, "重置密码成功!");
                finish();
                break;
            case NetAPI.ACTION_USER_CHECK_SUBMIT:
                Utils.showToast(this, "验证成功!");
                if ("1".equals(mSession.getUserLoginInfo().getIsNeedUpdatePwd())) {//定期修改密码
                    UiUtils.startActivity(UserCheckActivity.this, UserPwdSetActivity.class);
                    finish();
                } else {
                    UiUtils.startActivity(UserCheckActivity.this, UserMainActivity.class);
                    finish();

                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {

            case NetAPI.ACTION_GET_CHECK_CODE:
                Utils.showToast(this, "发送失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_FORGET_SUBMIT:
                Utils.showToast(this, "重置密码失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_USER_CHECK_SUBMIT:
                Utils.showToast(this, "验证失败:" + statusCode + "!");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_check:
                if (!TextUtils.isEmpty(name)) {
                    frogetSubmit();
                } else {
                    checkMobileSubmit();
                }
                break;
            case R.id.btn_checkcode_send:
                if (!TextUtils.isEmpty(name)) {
                    getFrogetCode();
                } else {
                    getCheckCodeSubmit();
                }
                break;
            case R.id.btn_iv_back:
                finish();
        }
    }

    private void frogetSubmit() {
        showProgressDialog("请求中...");
        String etCheckcodeStr = etCheckcode.getText().toString().trim();
        ForgetSubmitReq forgetSubmitReq = new ForgetSubmitReq();
        forgetSubmitReq.setDevCode(mSession.getDeviceId());
        forgetSubmitReq.setCreditCode(etCheckcodeStr);
        forgetSubmitReq.setPlatformType("1");
        forgetSubmitReq.setLoginName(name);
        NetAPI.forgetsubmit(this, this, forgetSubmitReq);
    }

    private void getFrogetCode() {
        ForgetCodeReq forgetCodeReq = new ForgetCodeReq();
        forgetCodeReq.setCodeType("1");
        forgetCodeReq.setDevCode(mSession.getDeviceId());
        forgetCodeReq.setLoginName(name);
        NetAPI.getForgetCode(this, this, forgetCodeReq);
    }

    /**
     * 用户登录处理
     */
    private void checkMobileSubmit() {
        String etCheckcodeStr = etCheckcode.getText().toString().trim();
        if (TextUtils.isEmpty(etCheckcodeStr)) {
            Utils.showToast(this, "请输入短信验证码!");
            return;
        } else {
            showProgressDialog("请求中...");
            UserReqInfo reqInfo = new UserReqInfo();
            reqInfo.setDevCode(mSession.getDeviceId());
            reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            reqInfo.setCreditCode(etCheckcodeStr);
            NetAPI.userCheckSubmit(this, this, reqInfo);
        }
    }

    /**
     * 发送手机号验证码
     */
    private void getCheckCodeSubmit() {
        showProgressDialog("请求中...");
        UserReqInfo reqInfo = new UserReqInfo();
        reqInfo.setDevCode(mSession.getDeviceId());
        reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        reqInfo.setCodeType("1");//1、授信码 2、动态密码
        reqInfo.setPlatformType("ANDROID");
        NetAPI.getUserCheckCode(this, this, reqInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
    }
}

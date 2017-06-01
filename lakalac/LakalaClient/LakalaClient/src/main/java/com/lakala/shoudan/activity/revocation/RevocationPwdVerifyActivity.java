package com.lakala.shoudan.activity.revocation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.ScancodeCancelActivity;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.ui.component.ClearEditText;
import com.lakala.ui.component.NavigationBar;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONObject;

/**
 * 撤销交易
 */
public class RevocationPwdVerifyActivity extends AppBaseActivity {

    private EditText cancelPwd;
    private TextView cancelPhone;
    private TransactionType transactionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_collections_cancel_psw_input);
        transactionType = (TransactionType) getIntent().getExtras().getSerializable(Constants.IntentKey.TRANS_STATE);
        initUI();
    }

    protected void initUI() {
        navigationBar.setTitle(getString(R.string.revocation_transaction));
        navigationBar.setActionBtnText(getString(R.string.help));
        navigationBar.setActionBtnEnabled(true);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if(navBarItem == NavigationBar.NavigationBarItem.action){
                    startProtocalActivity();
                }else if(navBarItem == NavigationBar.NavigationBarItem.back){
                    finish();
                }
            }
        });
        TextView next = (TextView) findViewById(R.id.shoudan_cancel_pwd_next);
        next.setOnClickListener(this);
        cancelPhone = (TextView) findViewById(R.id.tv_phone);
        cancelPwd = (EditText) findViewById(R.id.shoudan_cancel_pwd);
        cancelPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof ClearEditText) {
                    ClearEditText clearEditText = (ClearEditText) v;
                    clearEditText.onFocusChange(v, hasFocus);
                }
                if (v instanceof EditText) {
//                    EditText editText = (EditText) v;
//                    if (hasFocus) {
//                        CharSequence hint = editText.getHint();
//                        editText.setTag(hint);
//                        editText.setHint("");
//                    } else {
//                        Object tag = editText.getTag();
//                        if (tag instanceof CharSequence) {
//                            CharSequence hint = (CharSequence) tag;
//                            editText.setHint(hint);
//                        }
//                    }
                }
            }
        });
        String userName = ApplicationEx.getInstance().getUser().getLoginName();
//        cancelPhone.setFocusable(false);
        cancelPhone.setText(StringUtil.formatPhoneN3S4N4(userName));
        cancelPhone.requestFocus();
    }

    /**
     * 收费规则
     */
    private void startProtocalActivity(){

        ProtocalActivity.open(RevocationPwdVerifyActivity.this, ProtocalType.COLLECTION_CANCEL_HELP);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        switch (id) {
            case R.id.shoudan_cancel_pwd_next: {
                String pwd = cancelPwd.getText().toString().trim();
                if (pwd.length() == 0) {
                    ToastUtil.toast(this, getString(R.string.havnt_input_pwd));
                    cancelPwd.setText("");
                    return;
                }
                if (pwd.length() < 6) {
                    ToastUtil.toast(this, "密码不正确");
                    cancelPwd.setText("");
                    return;
                }else if (pwd.length() > getResources().getInteger(R.integer.max_password_limit)){
                    ToastUtil.toast(this, "请输入正确的8~32位密码");
                    cancelPwd.setText("");
                    return;
                }
                pwdVerified(pwd);
                break;
            }
        }
    }

    /**
     * 验证登录密码
     *
     * @param pwd 登录密码
     */
    private void pwdVerified(String pwd) {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.CHECK_PWD);
        request.setAutoShowProgress(true);//显示进度条
        RequestParams requestParams = request.getRequestParams();
        requestParams.put("loginName", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("password", CommonEncrypt.loginEncrypt(pwd));

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    //Forward revocationPaymentActivity
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        if (jsonObject.optBoolean("check", false)) {
                            LogUtil.print("<QB>","OK");
                            if (transactionType != null){
                                LogUtil.print("<QB>","have");
                                switch (transactionType){
                                    case ScanRevocation:
                                        LogUtil.print("<QB>","ScanRevocation");
                                        Intent intent = new Intent();
                                        intent.setClass(RevocationPwdVerifyActivity.this, ScancodeCancelActivity.class);
                                        startActivity(intent);
                                        break;
                                    case Revocation:
                                        LogUtil.print("<QB>","Revocation");
                                        startActivityForResult(new Intent(RevocationPwdVerifyActivity.this,
                                                RevocationPaymentActivity.class)
                                                .putExtra(ConstKey.TRANS_INFO,
                                                        new RevocationTransinfo()), 0);
                                        break;
                                    default:
                                        break;
                                }
                            }else {
                                LogUtil.print("<QB>","null");
                            }

                        } else {
                            ToastUtil.toast(RevocationPwdVerifyActivity.this, getString(R.string.password_error));

                        }
                    } catch (Exception e) {
                        ToastUtil.toast(ApplicationEx.getInstance(), getString(R.string.plat_http_004));
                    }
                } else {
                    ToastUtil.toast(RevocationPwdVerifyActivity.this, getString(R.string.password_error));
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(RevocationPwdVerifyActivity.this, getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(cancelPwd != null){
            cancelPwd.setText("");
            cancelPwd.clearFocus();
        }
    }
}

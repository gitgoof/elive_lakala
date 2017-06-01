package com.lakala.shoudan.activity.password;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseSafeManageActivity;
import com.lakala.shoudan.activity.safetymanagement.SafetyManagementActivity;
import com.lakala.shoudan.activity.treasure.TreasureBuyActivity;
import com.lakala.shoudan.activity.wallet.WalletTransferActivity;
import com.lakala.shoudan.activity.wallet.request.SetPwdRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.ui.component.NavigationBar;

/**
 * 设置支付密码
 * <p/>
 * Created by fengx
 */
public class SetPaymentPasswordActivity extends BaseSafeManageActivity {
    /**
     * 支付密码:第一次,确认
     */
    private String firstPassword, confirmPassword;
    private String QuestionFlag;//密保标志
    private Boolean isResetPwd = false;//判断是不是由重置支付密码页面过来的
    private boolean isWalletTransfer; //零钱转出跳来设置支付密码
    private boolean fromOneTreasure = false;//来自于一块夺宝


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("设置支付密码");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                }
            }
        });
        String data = getIntent().getStringExtra("reset_pwd");
        if (!TextUtils.isEmpty(data)) {
            isResetPwd = true;
        }
        isWalletTransfer = getIntent().getBooleanExtra("isWalletTransfer", false);
        fromOneTreasure = getIntent().getBooleanExtra("fromOneTreasure", false);
    }

    //物理键盘
    @Override
    public void onBackPressed() {
        if (!isResetPwd) {
            //第一次设置支付密码
            if (firstPassword == null) {
                clear();
                finish();
            } else {
                //不处理
            }
        } else {
            //重置支付密码
            clear();
            if (firstPassword == null) {
                finish();
            } else {
                firstPassword = null;
                tvNotice.setText(R.string.set_password_hint);
                navigationBar.setBackBtnVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 提交请求验证
     */
    private void submit() {

        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.SET_PAYMENT_PWD);
        String trsPassword = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), firstPassword);
        final String confirmTrsPassword = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), confirmPassword);
        SetPwdRequest params = new SetPwdRequest(context);
        params.setTrsPassword(trsPassword);
        params.setConfirmPassword(confirmTrsPassword);
        params.setBusid("110005");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    if (isWalletTransfer) {
                        WalletTransferActivity.isSetPwd = true;
                        if (WalletTransferActivity.isSetQuestion) {
                            clearTop(WalletTransferActivity.class);
                            return;
                        } else {
                            Intent intent = new Intent(SetPaymentPasswordActivity.this, SetSecurityQuestionActivity.class);
                            intent.putExtra("isFromWallet", true);
                            startActivity(intent);
                            return;
                        }

                    }
                    if (fromOneTreasure) {
                        clearTop(TreasureBuyActivity.class);
                        return;
                    }
                    QuestionFlag = WalletServiceManager.getInstance().getQuestionFlag();
                    if (QuestionFlag.equals("0")) {
                        Intent intent = new Intent(SetPaymentPasswordActivity.this, SetSecurityQuestionActivity.class);
                        intent.putExtra(SetSecurityQuestionActivity.KEY_QUESTION_TAG, SetSecurityQuestionActivity.KEY_QUESTION_TAG);
                        startActivity(intent);
                        if (isResetPwd) {
                            ShoudanStatisticManager.getInstance()
                                    .onEvent(ShoudanStatisticManager.Reset_Pay_Pwd_Success, context);
                        } else {
                            ShoudanStatisticManager.getInstance()
                                    .onEvent(ShoudanStatisticManager.Set_Pay_Pwd_Success, context);
                        }

                    } else if (QuestionFlag.equals("1")) {
                        ToastUtil.toast(context, "支付密码重置成功！");
                        keyBoardPopWindow.dismiss();
                        clearTop(SafetyManagementActivity.class);
                    }
                } else {
                    keyBoardPopWindow.dismiss();
                    ToastUtil.toast(context, "支付密码重置失败，请重新设置");
                    clearTop(SetPaymentPasswordActivity.class);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                keyBoardPopWindow.dismiss();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }

    private void clearTop(Class clazz) {
        clear();
        BusinessLauncher.getInstance().clearTop(clazz);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = inputStack.size();
                if (size < 6) {
                    ToastUtil.toast(context, "请按照规则输入正确的支付密码");
                    return;
                }
                if (firstPassword == null) {
                    firstPassword = getString(inputStack);
                    clear();
                    tvNotice.setText("请再次输入支付密码");
                    if (!isResetPwd) {
                        navigationBar.setBackBtnVisibility(View.GONE);
                    }
                } else if (firstPassword != null) {
                    confirmPassword = getString(inputStack);
                    if (!TextUtils.equals(firstPassword, confirmPassword)) {
                        ToastUtil.toast(context, "密码不一致请重新输入");
                        clear();
                        if (!isResetPwd) {
                            tvNotice.setText(R.string.set_password_hint);
                            firstPassword = null;
                            navigationBar.setBackBtnVisibility(View.VISIBLE);
                        }
                        return;
                    } else {
                        showProgressWithNoMsg();
                        //终端签到
                        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onError(String msg) {
                                hideProgressDialog();
                                toastInternetError();
                                LogUtil.print(msg);
                            }

                            @Override
                            public void onSuccess() {
                                submit();
                            }
                        });

                    }

                }
            }
        });
    }
}

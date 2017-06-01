package com.lakala.shoudan.activity.password;

import android.os.Bundle;
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
import com.lakala.shoudan.activity.wallet.request.SetPwdRequest;
import com.lakala.shoudan.activity.wallet.request.VerifyPayPwdRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.ui.component.NavigationBar;

/**修改支付密码
 * Created by HJP on 2015/12/8.
 */
public class ModifyPayPwdActivity extends BaseSafeManageActivity {
    private String currentPwd, newPwd, confirmPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_czLoginPwd_confirm,this);
        navigationBar.setTitle(R.string.modify_payment_password);
        tvNotice.setText("请输入当前支付密码");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                }
            }
        });
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
                if (currentPwd == null) {
                    currentPwd = getString(inputStack);
                    terminalSignInVerifyPayPwd(currentPwd);
                    return;
                } else if (currentPwd != null && newPwd == null) {
                    newPwd = getString(inputStack);
                    if (isPwdSame(currentPwd, newPwd)) {
                        ToastUtil.toast(context, R.string.password_new_old_same);
                        newPwd = null;
                    } else {
                        tvNotice.setText("再次输入进行确认");
                    }
                } else if (newPwd != null && confirmPwd == null) {
                    confirmPwd = getString(inputStack);
                    if (!isPwdSame(confirmPwd, newPwd)) {
                        ToastUtil.toast(context, "密码不一致请重新输入");
                        confirmPwd = null;
                    } else {
                        submit(currentPwd, confirmPwd);
                        return;
                    }

                }
                clear();
            }
        });

    }
    /**
     *修改支付密码接口
     *
     */
    public void submit(String oldString,String newString) {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.SET_PAYMENT_PWD);
        String trsPassword = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), oldString);
        String confirmTrsPassword = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), newString);
        SetPwdRequest params=new SetPwdRequest(context);
        params.setTrsPassword(trsPassword);
        params.setConfirmPassword(confirmTrsPassword);
        params.setNewTrsPassword(confirmTrsPassword);
        params.setBusid("110005");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    ToastUtil.toast(context,"修改支付密码成功！");
                    ShoudanStatisticManager.getInstance()
                            .onEvent(ShoudanStatisticManager.Modify_Pay_Pwd_Success, context);
                    BusinessLauncher.getInstance().clearTop(SafetyManagementActivity.class);
                } else {
                    ToastUtil.toast(context,resultServices.retMsg);
                    currentPwd=null;
                    newPwd=null;
                    confirmPwd=null;
                    tvNotice.setText("请输入当前支付密码");
                    ToastUtil.toast(context,"密码设置失败，请重新设置");
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
    protected boolean isPwdSame(String oldPwd,String newPwd) {
        if (oldPwd.equals(newPwd)){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        clear();
        if (newPwd!=null && confirmPwd==null) {
            tvNotice.setText("请设置新的支付密码");
            newPwd=null;
        }else if (currentPwd!=null && newPwd==null) {
            tvNotice.setText("请输入当前支付密码");
            currentPwd=null;
        }else if(currentPwd==null){
            finish();
        }
    }


    /**
     * 终端签到及校验支付密码
     * @param trsPassword
     */
    private void terminalSignInVerifyPayPwd(final String trsPassword){
        showProgressWithNoMsg();

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
                verifyPayPwd(trsPassword);
            }
        });




    }
    private void verifyPayPwd(String trsPassword){
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.VERIFY_PAY_PWD);
        VerifyPayPwdRequest params=new VerifyPayPwdRequest(context);
        String TrsPassword = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), trsPassword);
        params.setTrsPassword(TrsPassword);
//        params.setBusid("110005");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    tvNotice.setText("请设置新的支付密码");
                }else{
                    ToastUtil.toast(context,resultServices.retMsg);
                    currentPwd = null;
                }
                clear();
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
}

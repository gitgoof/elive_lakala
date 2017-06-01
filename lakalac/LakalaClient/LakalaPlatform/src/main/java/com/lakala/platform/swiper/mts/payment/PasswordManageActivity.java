package com.lakala.platform.swiper.mts.payment;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.mts.paypwd.PayPwdChangeActivity;
import com.lakala.platform.swiper.mts.paypwd.PayPwdProcess;
import com.lakala.platform.swiper.mts.paypwd.PayPwdSetActivity;
import com.lakala.platform.swiper.mts.paypwd.PayPwdSetQuestionActivity;
import com.lakala.ui.component.SingleLineTextView;

/**
 * Created by xu on 14-1-7.
 * <p/>
 * 设置——账号安全——密码管理页面
 */
public class PasswordManageActivity extends BaseActionBarActivity implements View.OnClickListener {

    //设置支付密码
    private View containerSetPayPwd;
    private SingleLineTextView mSetPayPwd;

    //管理支付密码
    private View containerModifySetPayPwd;
    //修改,找回
    private SingleLineTextView mChangePayPwd,mFindPayPwd;

    //设置密保问题
    private View containerSetProblem;
    private SingleLineTextView mSetPayPwdProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_accountsafe_manage_pwd);
        navigationBar.setTitle(R.string.accountsafe_managepwd);

        containerSetPayPwd = findViewById(R.id.container_set_pay_pwd);
        mSetPayPwd = (SingleLineTextView) findViewById(R.id.id_managepwd_set_pay_pwd);
        mSetPayPwd.setOnClickListener(this);

        containerModifySetPayPwd = findViewById(R.id.container_modify_pay_pwd);
        mChangePayPwd = (SingleLineTextView) findViewById(R.id.id_managepwd_change_pay_pwd);
        mChangePayPwd.setOnClickListener(this);
        mFindPayPwd = (SingleLineTextView) findViewById(R.id.id_managepwd_find_pay_pwd);
        mFindPayPwd.setOnClickListener(this);

        containerSetProblem = findViewById(R.id.container_set_pay_pwd_problem);
        mSetPayPwdProblem = (SingleLineTextView) findViewById(R.id.id_managepwd_set_pay_pwd_problem);
        mSetPayPwdProblem.setOnClickListener(this);

    }

    private void changeUI(){
        //是否设置支付密码
        boolean hasPwd = ApplicationEx.getInstance().getUser().isTrsPasswordFlag();
        //是否设置密保问题
        boolean hasPwdQuestion = ApplicationEx.getInstance().getUser().isQuestionFlag();

        if (hasPwd){
            containerSetPayPwd.setVisibility(View.GONE);
            containerModifySetPayPwd.setVisibility(View.VISIBLE);
        } else {
            containerSetPayPwd.setVisibility(View.VISIBLE);
            containerModifySetPayPwd.setVisibility(View.GONE);
        }

        if (hasPwd && !hasPwdQuestion){
            containerSetProblem.setVisibility(View.VISIBLE);
        } else {
            containerSetProblem.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeUI();
    }

    private boolean isNetWorkAvaiable() {
        ConnectivityManager connect = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean isWifi      = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean isInternet  = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        return isInternet || isWifi;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        final int vId = view.getId();
        if (view.equals(mSetPayPwd)){
            startActivity(new Intent(this, PayPwdSetActivity.class));
        } else if (view.equals(mChangePayPwd)){
            startActivity(new Intent(this, PayPwdChangeActivity.class));
        } else if (view.equals(mFindPayPwd)){
            PayPwdProcess.findPayPassword(this);
        } else if (view.equals(mSetPayPwdProblem)){
            startActivity(new Intent(this, PayPwdSetQuestionActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}

package com.lakala.shoudan.activity.safetymanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.accountmanagement.ForgotPasswordActivity;
import com.lakala.shoudan.activity.accountmanagement.changepwd.InputNewPasswordActivity;
import com.lakala.shoudan.activity.password.ModifyPayPwdActivity;
import com.lakala.shoudan.activity.password.PayPwdDialogActivity;
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.activity.password.SetSecurityQuestionActivity;
import com.lakala.shoudan.activity.password.VerifyUserQuestionActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.component.SingleLineTextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 密码管理
 * Created by HJP on 2015/11/19.
 */
public class SafetyManagementActivity extends AppBaseActivity implements View.OnClickListener {
    private SingleLineTextView modifyLoginPassword;
    private SingleLineTextView resetLoginPasswor;
    private SingleLineTextView setPaymentPassword;
    private SingleLineTextView modifyPaymentPassword;
    private SingleLineTextView resetPaymentPassword;
    //    private LabelSwitch openOrCloseNonePassword;
    private SingleLineTextView setSecurityQuestion;
    private TextView exitApp;
    private TextView tvSwitchNotice;
    private LinearLayout llModifyPaymentPassword;
    private LinearLayout llResetPaymentPassword;
    private LinearLayout llSetSecurityQuestion;
    //    private LinearLayout llOpenOrCloseNonePassword;
    private LinearLayout llSetPaymentPassword;

    private String state;
    private static final int INPUT_PWD_REQUEST = 0x1241;
    private String questionContent;//密保问题
    private String answerNote;//密保问题答案说明
    private String questionId;//密保问题Id
    private Question question = new Question();
    private String noPwdFlag;//免密标识
    private String trsPasswordFlag;//支付密码标识
    private String QuestionFlag;//密保标识
    private String noPwdAmount;//免密金额

    //    private boolean isSwitchFirstTime=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_management);
        initUI();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.safety_management);
    }

    public void initView() {
        llModifyPaymentPassword = (LinearLayout) findViewById(R.id.ll_modify_payment_password);
        llResetPaymentPassword = (LinearLayout) findViewById(R.id.ll_reset_payment_password);
        llSetSecurityQuestion = (LinearLayout) findViewById(R.id.ll_set_security_question);
//        llOpenOrCloseNonePassword=(LinearLayout)findViewById(R.id.ll_open_or_close_none_password);
        llSetPaymentPassword = (LinearLayout) findViewById(R.id.ll_set_payment_password);

        tvSwitchNotice = (TextView) findViewById(R.id.tv_switch_notice);
        modifyLoginPassword = (SingleLineTextView) findViewById(R.id.modify_login_password);
        resetLoginPasswor = (SingleLineTextView) findViewById(R.id.reset_login_password);
        setPaymentPassword = (SingleLineTextView) findViewById(R.id.set_payment_password);
        modifyPaymentPassword = (SingleLineTextView) findViewById(R.id.modify_payment_password);
        resetPaymentPassword = (SingleLineTextView) findViewById(R.id.reset_payment_password);
//        openOrCloseNonePassword = (LabelSwitch) findViewById(R.id.open_or_close_none_password);
        setSecurityQuestion = (SingleLineTextView) findViewById(R.id.set_security_question);
        exitApp = (TextView) findViewById(R.id.set_exit_app);
//        openOrCloseNonePassword.setAutoCheck(false);
//        openOrCloseNonePassword.getSwitchView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    terminalSignInPop();
//            }
//        });
        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingItemClick(v);
            }
        });

        int[] settingIds = new int[]{
                R.id.modify_login_password, R.id.set_payment_password, R.id.modify_payment_password,
                R.id.reset_payment_password, R.id.reset_login_password,
                R.id.set_security_question};

        for (int i : settingIds) {
            findViewById(i).setOnTouchListener(settingOnTouchListener);
        }
    }

    public void initData() {
        noPwdFlag = WalletServiceManager.getInstance().getNoPwdFlag();
        trsPasswordFlag = WalletServiceManager.getInstance().getTrsPasswordFlag();
        QuestionFlag = WalletServiceManager.getInstance().getQuestionFlag();
        noPwdAmount = WalletServiceManager.getInstance().getNoPwdAmount();
        if (TextUtils.isEmpty(noPwdAmount)) {
            WalletServiceManager.getInstance().setNoPwdAmount("0");
            noPwdAmount = "0";
        }

        if (TextUtils.equals("1", trsPasswordFlag)) {
            llSetPaymentPassword.setVisibility(View.GONE);
            llModifyPaymentPassword.setVisibility(View.VISIBLE);
            if (TextUtils.equals("0", QuestionFlag)) {
                //支付密码已设置，密保未设置
                llSetSecurityQuestion.setVisibility(View.VISIBLE);
            } else if (TextUtils.equals("1", QuestionFlag)) {
                //支付密码、密保已设置
//                llOpenOrCloseNonePassword.setVisibility(View.VISIBLE);
                llResetPaymentPassword.setVisibility(View.VISIBLE);
                llSetSecurityQuestion.setVisibility(View.GONE);
//                //0未设置，1已设置
//                if(TextUtils.equals("0",noPwdFlag)){
//                    openOrCloseNonePassword.setSwitchStatus(LabelSwitch.ESwitchStatus.OFF);
//                    tvSwitchNotice.setText("您已关闭开关，支付时需要输入支付密码");
//                }else{
//                    openOrCloseNonePassword.setSwitchStatus(LabelSwitch.ESwitchStatus.ON);
//                    tvSwitchNotice.setText("您已开启开关，金额≤"+noPwdAmount+"元/笔,无需输入支付密码");
//                }
            }
        }
    }


    View.OnTouchListener settingOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            SingleLineTextView singleLineTextView = (SingleLineTextView) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.gray));
                    break;
                case MotionEvent.ACTION_UP:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    settingItemClick(v);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    private void settingItemClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.modify_login_password:// 修改登录密码
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_pwdChange, this);
                startActivity(InputNewPasswordActivity.class);
                break;
            case R.id.reset_login_password://重置登录密码
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_getSmsCode, this);
                startActivity(ForgotPasswordActivity.class);
                break;
            case R.id.set_payment_password:// 设置支付密码
//                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_paypwdChange, this);
                startActivity(SetPaymentPasswordActivity.class);
                break;
            case R.id.modify_payment_password://修改支付密码
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_paypwdChange, this);
                startActivity(ModifyPayPwdActivity.class);
                break;
            case R.id.reset_payment_password:// 重置支付密码
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_getPaySmsCode, this);
                qryUserQuestion();
                break;
            case R.id.set_security_question:// 设置密保问题
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Security_Management_pwdChange, this);
                startActivity(SetSecurityQuestionActivity.class);
                break;
            case R.id.set_exit_app:
                logout();

                break;

        }
    }

    /**
     * Activity 跳转
     *
     * @param targetActivity
     */
    protected void startActivity(Class targetActivity) {
        Intent intent = getIntent();
        intent.setClass(this, targetActivity);
        intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
        startActivity(intent);
    }

    private void logout() {

        DialogCreator.createFullContentDialog(context, "取消", "确定", getString(R.string.sure_cancellation_current_login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Exit_Login_Success, context);
                LoginUtil.clearSession2Login(context);

            }
        }).show(getSupportFragmentManager());

    }

//    /**
//     * 4.0版本不上
//     * 设置小额免密接口
//     */
//    private void qrySmallAmountCancelPwd(final String status,String pwd){
//        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.SMALL_AMOUNT_CANCEL_PWD);
//        SmallAmountCancelPwdRequest params=new SmallAmountCancelPwdRequest(context);
//        params.setState(status);//state 状态 0:关闭 1:开启
//        params.setAmount(noPwdAmount);//amount 免密金额
//        params.setTrsPassword(pwd);//trsPassword 支付密码
//        businessRequest.setResponseHandler(new ServiceResultCallback() {
//            @Override
//            public void onSuccess(ResultServices resultServices) {
//                if (resultServices.isRetCodeSuccess()) {
//                    if(status.equals("1")){
//                        tvSwitchNotice.setText("您已开启开关，金额≤" + noPwdAmount + "元/笔,无需输入支付密码");
//                        openOrCloseNonePassword.setSwitchStatus(LabelSwitch.ESwitchStatus.ON);
//                    }else{
//                        tvSwitchNotice.setText("您已关闭开关，支付时需要输入支付密码");
//                        openOrCloseNonePassword.setSwitchStatus(LabelSwitch.ESwitchStatus.OFF);
//                    }
//                }else {
//                    toast(resultServices.retMsg);
//                }
//            }
//
//            @Override
//            public void onEvent(HttpConnectEvent connectEvent) {
//                toastInternetError();
//                LogUtil.print(connectEvent.getDescribe());
//            }
//        });
//        WalletServiceManager.getInstance().start(params,businessRequest);
//    }

    /**
     * 查询用户密保问题
     */
    private void qryUserQuestion() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.QRY_USER_SECURITY_QUESTION);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        questionContent = jsonObject.optString("questionContent");
                        answerNote = jsonObject.optString("answerNote");
                        questionId = jsonObject.optString("questionId");
                        question.setQuestionContent(questionContent);
                        question.setAnswerNote(answerNote);
                        question.setQuestionId(questionId);
                        VerifyUserQuestionActivity.open(context, question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(context, businessRequest);
    }
//    /**
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == INPUT_PWD_REQUEST && resultCode == ConstKey.RESULT_PWD_BACK){
//            final String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
//            //0未设置，1已设置
//            if (openOrCloseNonePassword.getSwitchStatus()==LabelSwitch.ESwitchStatus.ON) {
//                state = "0";
//            } else {
//                state = "1";
//            }
//            qrySmallAmountCancelPwd(state, pwd);
//        }
//    }

    /**
     * 终端签到并弹出支付密码页面
     */
    private void terminalSignInPop() {
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
                hideProgressDialog();
                Intent intent = new Intent(context, PayPwdDialogActivity.class);
                startActivityForResult(intent, INPUT_PWD_REQUEST);

            }
        });
    }
}

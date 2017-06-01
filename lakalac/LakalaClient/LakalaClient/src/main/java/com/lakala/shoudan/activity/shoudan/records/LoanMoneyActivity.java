package com.lakala.shoudan.activity.shoudan.records;


import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一日贷
 * 一日贷贷款页面
 */
public class LoanMoneyActivity extends BasePwdAndNumberKeyboardActivity {

    private static final String TAG = "LoanMoneyActivity";
    private View viewGroup;
    private TextView nextBtn;
    private double accountBalance;
    /**
     * 今日上限
     */
    private double dLimitMax;
    /**
     * 单笔下限
     */
    private double pLimitMin;
    /**
     * 单笔上限
     */
    private double pLimitMax;
    private EditText input_amount;
    private String mAmount = "";
    private String mFee = "";
    TextView amount_tip;
    TextView tv_free;
    String remind1 = "本次可贷%s元，共可贷%s元";
    String remind2 = "单次贷款金额需大于等于%s元,小于等于%s元\n单日贷款上限为%s元";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_trade_loan_money);
        initUI();

    }

    protected void initUI() {
        nextBtn = (TextView) findViewById(R.id.next_draw_money);
        tv_free = (TextView) findViewById(R.id.tv_free);
        nextBtn.setOnClickListener(next_onClickListener);
        navigationBar.setTitle("一日贷");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        navigationBar.setActionBtnText("业务说明");
        input_amount = (EditText) findViewById(R.id.input_amount);
        viewGroup = findViewById(R.id.view_group);
        setOnDoneButtonClickListener(onClickListener);
        initNumberKeyboard();
        initNumberEdit(input_amount, CustomNumberKeyboard.EDIT_TYPE_FLOAT, 30);
        initTips();
        setHintOnBack(true);
        //设置埋点：
        if(PublicEnum.Business.isDirection()){
            //定向业务-一日贷
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction, LoanMoneyActivity.this);
      LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction);
        }
    }




    /**
     * toolbar点击事件
     */
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                ProtocalActivity.open(context, ProtocalType.ONE_DAY_LOAN_NOTE);

                //设置埋点：
                if(PublicEnum.Business.isDirection()){
                    //定向业务-一日贷
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc, LoanMoneyActivity.this);
                    LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc);
                }else if(PublicEnum.Business.isWithdrawal()){
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc, LoanMoneyActivity.this);
                    LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc);
                }
            }
        }
    };
    /**
     * 下一步
     */
    OnClickListener next_onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toSubmit();

        }
    };
    /**
     * 软键盘完成按钮
     */
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //设置埋点：
            if(PublicEnum.Business.isDirection()){
                //定向业务-一日贷
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_inputmoney, LoanMoneyActivity.this);
                LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_inputmoney);
            }else if(PublicEnum.Business.isWithdrawal()){
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_inputmoney, LoanMoneyActivity.this);
                LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_inputmoney);
            }
            viewGroup.requestFocus();
            mAmount = input_amount.getText().toString();
            if (TextUtils.isEmpty(mAmount)) {
                nextBtn.setEnabled(false);
                initErro(false, "");
                return;
            }
            String erro = isValidAmount(Double.parseDouble(mAmount));
            if (mAmount.length() > 0 &&
                    TextUtils.isEmpty(erro)) {
                nextBtn.setEnabled(true);
                initErro(false, "");
                getFee(mAmount);
            } else {
                nextBtn.setEnabled(false);
                initErro(true, erro);
            }
        }
    };

    private void initTips() {
        String jsonStr = getIntent().getStringExtra("jsonStr");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            accountBalance = jsonObject.getDouble("amount");
            dLimitMax = jsonObject.getDouble("dLimitMax");
            pLimitMin = jsonObject.getDouble("pLimitMin");
            pLimitMax = jsonObject.getDouble("pLimitMax");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        amount_tip = (TextView) findViewById(R.id.amount_tip);
        if (accountBalance < pLimitMax) {
            remind1 = String.format(remind1, Util.formatAmount(accountBalance), Util.formatAmount(accountBalance));
        } else {
            remind1 = String.format(remind1, Util.formatAmount(pLimitMax), Util.formatAmount(accountBalance));
        }
        amount_tip.setText(remind1);
        TextView limit_tip = (TextView) findViewById(R.id.limit_tip);
        limit_tip.setText(String.format(remind2, Util.formatAmount
                (pLimitMin), Util.formatAmount(pLimitMax), Util.formatAmount(dLimitMax)));
    }

    /**
     * 获取手续费
     *
     * @param amount
     */
    private void getFee(final String amount) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                analyzeResult(resultServices);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getT0Fee(amount, callback);
    }

    private void analyzeResult(final ResultServices result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    try {
                        JSONObject retData = new JSONObject(result.retData.toString());
                        mFee = String.valueOf(retData.getDouble("fee"));
                        tv_free.setText(mFee + "元");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(LoanMoneyActivity.this, result.retMsg);
                }
            }
        });
    }

    /**
     * 获取相应的信息
     *
     * @param amount
     * @return
     */
    private String isValidAmount(double amount) {
        String msg = "";
        double realMax = dLimitMax < accountBalance ? dLimitMax : accountBalance;
        double realMin = pLimitMin;
        LogUtil.print("<v>", "\n" + "amount:" + amount + "\nrealMax:"
                + realMax + "\npLimitMax:" + pLimitMax + "\nrealMin:" + realMin
                + "\naccountBalance:" + accountBalance
                + "\ndLimitMax:" + dLimitMax);
        if (amount == 0) {
            msg = "请输入贷款金额";
        } else if (amount < realMin) {
            msg = "贷款金额小于最低可贷金额，请修改";
        } else if (realMax < amount) {
            msg = "贷款金额大于可贷金额,请修改";
        } else if (amount <= realMax && amount > pLimitMax) {
            msg = "贷款金额大于单笔限额，请修改";
        }
        return msg;
    }

    /**
     * 初始化提示控件
     *
     * @param isErro
     * @param str
     */
    public void initErro(boolean isErro, String str) {
        if (isErro) {
            amount_tip.setText(str);
            amount_tip.setTextColor(getResources().getColor(R.color.yelo));
            nope(amount_tip).start();
        } else {
            amount_tip.setText(remind1);
            amount_tip.setTextColor(getResources().getColor(R.color.font_gray_three2));
        }
    }

    /**
     * 按钮抖动
     *
     * @param view
     * @return
     */
    public static ObjectAnimator nope(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.abc_dropdownitem_text_padding_left);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }

    /**
     * 贷款
     *
     */
    private void toSubmit() {
//        showProgressWithNoMsg();

        //设置埋点：
        if(PublicEnum.Business.isDirection()){
            //定向业务-一日贷
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_inputmoney_submitfee, LoanMoneyActivity.this);
            LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_inputmoney_submitfee);
        }

        Intent intent = new Intent(LoanMoneyActivity.this, DrawMoneySubmitActivity.class);
        intent.putExtra("amount", Util.formatAmount(Double.parseDouble(mAmount)));
        intent.putExtra("fee", mFee);
        intent.putExtra("isOne", true);
        startActivity(intent);
//        ServiceResultCallback callback = new ServiceResultCallback() {
//            @Override
//            public void onSuccess(ResultServices resultServices) {
//                hideProgressDialog();
//                analyzeGetCashResult(resultServices);
//            }
//
//            @Override
//            public void onEvent(HttpConnectEvent connectEvent) {
//                hideProgressDialog();
//                LogUtil.print(connectEvent.getDescribe());
//            }
//        };
//        CommonServiceManager
//                .getInstance().getT0Cash(amount, callback);
    }

    private void toReCheckDrawAmount() {

    }

    private void analyzeGetCashResult(final ResultServices result) {
        if (result.isRetCodeSuccess()) {
//            DialogCreator.createOneConfirmButtonDialog(
//                    context, "确定", "系统正在为您划款，请及时关注您的账户资金变化", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            callMainActivity();
//                        }
//                    }
//            ).show();
            try {
                JSONObject retData = new JSONObject(result.retData.toString());
                mFee = String.valueOf(retData.getDouble("fee"));
                toReCheckDrawAmount();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.e(result.retMsg);
            ToastUtil.toast(context, result.retMsg);
        }
    }
}

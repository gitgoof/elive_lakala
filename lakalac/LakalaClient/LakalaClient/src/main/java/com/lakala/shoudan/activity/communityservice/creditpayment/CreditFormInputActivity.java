package com.lakala.shoudan.activity.communityservice.creditpayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.encryption.Mac;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CreditCardEnum;
import com.lakala.platform.statistic.PayForYouEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.shoudan.loan.LoanTrailActivity;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.component.MoneyInputWatcher;
import com.lakala.shoudan.util.CardNumberHelper;
import com.lakala.shoudan.util.ContactBookUtil;
import com.lakala.shoudan.util.NotificationUtil;
import com.lakala.shoudan.util.ScreenUtil;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.LabelSwitch;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.PhoneNumberInputWatcher;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.DateInputDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by HUASHO on 2015/1/16.
 * 信用卡还款
 */
public class CreditFormInputActivity extends BasePwdAndNumberKeyboardActivity {

    private static final String TAG_CARD_NO = "cardNo";
    private static final String TAG_REPEAT_CARD_NO = "repeatCardNO";
    private static final String TAG_AMOUNT = "amount";
    private static final String TYPE_CREDIT_CARD = "C";
    private LabelEditText let_credit_number;//信用卡卡号
    private LabelEditText let_sure_credit_number;//确认信用卡卡号
    private LabelEditText let_repayment_money;//还款金额
    private LabelSwitch ls_repayment_notify;//还款提醒
    private LabelSwitch ls_sms_notify;//短信提醒
    private LabelEditText let_phone_number;//手机号码
    private TextView tv_date_prompt;//信用卡到账
    private TextView tv_next_step;//下一步
    private Context mContext;
    private View seperator_below_sure_credit;
    private boolean isCardFromEdit = true;
    private View seperator_below_phone_number;
    private String dateText;
    private ViewGroup viewGroup;

    private enum UpdateDateStatus {
        updateDateToShowView,//查询到账日期后显示到账日期
        updateDateToStartNext//更新显示日期后跳转到下一界面
    }

    private UpdateDateStatus status = UpdateDateStatus.updateDateToShowView;
    private String preCardNumber = "";
    private String cardNumberFromHistory;// 从历史记录中得到的信用卡卡号
    private boolean isFromHistory = false;//是否从历史列表带过来的数据
    private String cardNO = "";// 信用卡卡号
    private String repeatCardNO = "";//重复信用卡号
    private String amount = "";//还款金额
    private String repayDate = "";// 信用卡到账日期
    private String phoneString;//电话号码
    private String agreement = "";// 是否支持签约还款    string  0 - 不支持  1 - 支持
    private final String pointWhere_0 = "信用卡首页";
    private final String pointWhere_1 = "还款提醒";
    private final String pointWhere_2 = "短信提醒";
    private boolean isPayRemind, isSmsRemind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditpayment);
        viewGroup = (ViewGroup) findViewById(R.id.group);
        mContext = this;
        initUI();
    }

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.scroll_keyboard));
    }

    protected void initUI() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent(pointWhere_0), context);
        //设置自定义键盘“完成键”事件
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });

        navigationBar.setTitle(R.string.credit_payment);
        navigationBar.setActionBtnText(R.string.history_data);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else {
                    hideNumberKeyboard();
                    startActivityToPaymentHistoryRecordCardActivity();
                }
            }
        });


        //信用卡卡号
        let_credit_number = (LabelEditText) findViewById(R.id.let_credit_number);
        let_credit_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        let_credit_number.getEditText().setLongClickable(false);
        let_credit_number.getEditText().setTag(TAG_CARD_NO);
        let_credit_number.getEditText().addTextChangedListener(new CardNOHelper());// 过滤卡号格式
        let_credit_number.getEditText().setOnFocusChangeListener(null);

        //信用卡卡号（确认）
        let_sure_credit_number = (LabelEditText) findViewById(R.id.let_sure_credit_number);
        let_sure_credit_number.getEditText().setTag(TAG_REPEAT_CARD_NO);
        let_sure_credit_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        let_sure_credit_number.getEditText().setLongClickable(false);
        let_sure_credit_number.getEditText().setOnFocusChangeListener(null);
        seperator_below_sure_credit = findViewById(R.id.seperator_below_sure_credit);

        //还款金额
        let_repayment_money = (LabelEditText) findViewById(R.id.let_repayment_money);
        let_repayment_money.getEditText().setTag(TAG_AMOUNT);
        let_repayment_money.getEditText().setLongClickable(false);
        let_repayment_money.getEditText().setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        // 如果输入的内容是金额，则设置TextWatcher，控制小数点后只能输入两位
        let_repayment_money.getEditText().addTextChangedListener(new MoneyInputWatcher());
        let_repayment_money.getEditText().setOnFocusChangeListener(null);

        //信用卡到账
        tv_date_prompt = (TextView) findViewById(R.id.tv_date_prompt);

        //还款提醒
        ls_repayment_notify = (LabelSwitch) findViewById(R.id.ls_repayment_notify);
        ls_repayment_notify.setOnSwitchListener(new LabelSwitch.OnSwitchListener() {
            @Override
            public void onSwitch(LabelSwitch.ESwitchStatus status) {
                hideNumberKeyboard();
                if (status == LabelSwitch.ESwitchStatus.ON) {
                    showNotifyDateDialog();
                }
            }
        });

        //短信提醒
        ls_sms_notify = (LabelSwitch) findViewById(R.id.ls_sms_notify);
        ls_sms_notify.setOnSwitchListener(new LabelSwitch.OnSwitchListener() {
            @Override
            public void onSwitch(LabelSwitch.ESwitchStatus status) {
                if (status == LabelSwitch.ESwitchStatus.ON) {
                    CreditCardEnum.PayBack.setSmsOpen(true);
                    isSmsRemind = true;
                    smsEnable(true);
                } else {
                    CreditCardEnum.PayBack.setSmsOpen(false);
                    isSmsRemind = false;
                    smsEnable(false);
                }
                ShoudanStatisticManager.getInstance().onEvent(getEvent(pointWhere_2), context);
            }
        });

        //手机号码
        let_phone_number = (LabelEditText) findViewById(R.id.let_phone_number);
        let_phone_number.getEditText()
                .addTextChangedListener(new PhoneNumberInputWatcher());// 过滤电话号码格式
        let_phone_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        let_phone_number.getEditText()
                .setText(ApplicationEx.getInstance().getUser().getLoginName());//登入用户名
        let_phone_number.getRightIconImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNumberKeyboard();
                ContactBookUtil.onContactBookClick(CreditFormInputActivity.this);
            }
        });
        seperator_below_phone_number = findViewById(R.id.seperator_below_phone_number);

        //下一步
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
        tv_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //250毫秒后恢复点击事件
                        view.setEnabled(true);
                    }
                }, 250);
                hideNumberKeyboard();
                status = UpdateDateStatus.updateDateToStartNext;
                checkInput();
//                startNextStepActivity();
            }
        });

        //替你还
        findViewById(R.id.btn_loan_for_you).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayForYouEnum.PayForYou.setPayCredit(true);
                LoanTrailActivity.open(context);
            }
        });

        //键盘
        initNumberKeyboard();
        initNumberEdit(let_credit_number.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        initNumberEdit(let_sure_credit_number.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD,
                30
        );
        initNumberEdit(let_repayment_money.getEditText(), CustomNumberKeyboard.EDIT_TYPE_FLOAT, 30);

        //屏幕
        ScreenUtil.getScrrenWidthAndHeight(CreditFormInputActivity.this);
    }

    /**
     * 信用卡页面进入埋点
     */
    private String getEvent(String pointWhere) {
        String event = "";
        switch (pointWhere) {
            case "信用卡首页":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Credit_Card_Repayment_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Credit_Card_Repayment_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Credit_Card_Repayment_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Credit_Card_Repayment_public;
                }
                break;
            case "还款提醒":
            case "短信提醒":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Credit_Card_checkSituation_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Credit_Card_checkSituation_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Credit_Card_checkSituation_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Credit_Card_checkSituation_public;
                }
                event = checkStaticStatus(event);
                break;
        }

        return event;
    }


    /**
     * 短信还款提醒的开通状态
     *
     * @param event
     * @return
     */
    private String checkStaticStatus(String event) {
        if (isPayRemind) {
            if (isSmsRemind)
                event = String.format(event, "是", "是");
            else
                event = String.format(event, "是", "否");
        } else {
            if (isSmsRemind)
                event = String.format(event, "否", "是");
            else
                event = String.format(event, "否", "否");
        }
        return event;
    }

    private void smsEnable(boolean enable) {
        if (enable) {
            let_phone_number.setVisibility(View.VISIBLE);
            seperator_below_phone_number.setVisibility(View.VISIBLE);
        } else {
            ((ViewGroup) let_phone_number.getParent()).requestFocus();
            hideSoftInputFromWindow();
            let_phone_number.setVisibility(View.GONE);
            seperator_below_phone_number.setVisibility(View.GONE);
        }
    }

    /**
     * 查询信用卡到账日期
     */
    private void queryRepayDate() {
        cardNO = let_credit_number.getEditText().getText().toString().replace(" ", "");
        BusinessRequest request = RequestFactory
                .getRequest(this, RequestFactory.Type.CREDIT_CARD_INFO);
        if (status == UpdateDateStatus.updateDateToStartNext) {
//            request.setAutoShowProgress(true);
        } else {
            request.setAutoShowProgress(false);
        }
        request.setRequestURL(request.getRequestURL().replace("{cardNo}", cardNO));
        request.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        repayDate = jsonObject.optString("tipsMsg", "");
                        agreement = jsonObject.getString("agreement");
                        preCardNumber = cardNO;
                        if (status == UpdateDateStatus.updateDateToStartNext) {//如果是点击下一步
                            startNextStepActivity();
                        } else if (!TextUtils.isEmpty(repayDate)) {
                            hideProgressDialog();
                            tv_date_prompt.setText(repayDate);
                            tv_date_prompt.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    if (status == UpdateDateStatus.updateDateToStartNext) {
                        let_credit_number.setText("");
                        shouldCheckCard(true);
                        tv_date_prompt.setVisibility(View.GONE);
                        ToastUtil.toast(CreditFormInputActivity.this, resultServices.retMsg);
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                tv_date_prompt.setVisibility(View.GONE);
                ToastUtil.toast(CreditFormInputActivity.this,
                        getResources().getString(R.string.socket_fail)
                );
            }
        }));
        request.execute();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (v instanceof EditText) {
            String tag = String.valueOf(v.getTag());
            EditText editText = (EditText) v;
            if (TAG_CARD_NO.equals(tag) || TAG_REPEAT_CARD_NO.equals(tag)) {
                if (hasFocus) {
                    String newText = editText.getText().toString().replace(" ", "");
                    editText.setText(newText);
                    editText.setSelection(newText.length());
                } else {
                    String text = CardUtil.formatCardNumberWithSpace(editText.getText().toString());
                    editText.setText(text);
                    cardNO = text.trim().replace(" ", "");
                    if (TAG_CARD_NO.equals(tag) && !TextUtils.isEmpty(cardNO) && cardNO
                            .length() >= 16) {
                        if (!TextUtils.equals(cardNO, preCardNumber)) {
                            status = UpdateDateStatus.updateDateToShowView;
                            queryRepayDate();
                        }
                    }
                    if (TAG_CARD_NO.equals(tag) && !TextUtils
                            .equals(cardNO, cardNumberFromHistory) &&
                            let_sure_credit_number.getVisibility() != View.VISIBLE) {
                        shouldCheckCard(true);
                        let_sure_credit_number.getEditText().setText("");
                        ToastUtil.toast(CreditFormInputActivity.this,
                                getString(R.string.toast_input_edit_history),
                                Toast.LENGTH_LONG
                        );
                        isFromHistory = false;
                    }
                }
            } else if (TAG_AMOUNT.equals(tag)) {
                String amount = editText.getText().toString().replace(",", "");
                if (hasFocus) {
                    if (TextUtils.isEmpty(amount)) {
                        return;
                    }
                    double doubleAmount = Double.parseDouble(amount);
                    int intAmount = (int) doubleAmount;
                    if (intAmount == doubleAmount) {
                        editText.setText(String.valueOf(intAmount));
                    } else {
                        editText.setText(amount);
                    }
                    editText.setSelection(editText.getText().length());
                } else {
                    amount = StringUtil.formatAmount(amount);
                    editText.setText(amount);
                }
            }
        }
    }

    /**
     * 检测卡号的有效性，
     */
    private void checkInput() {
        cardNO = let_credit_number.getEditText().getText().toString().trim().replace(" ", "");
        repeatCardNO = let_sure_credit_number.getEditText().getText().toString().trim()
                .replace(" ", "");
        isCardFromEdit = !TextUtils.equals(cardNO, cardNumberFromHistory);
        // 判断是否从历史记录中获取卡号，并判断是否重新编辑了记录的卡号
        if (!isFromHistory || isCardFromEdit) {
            if (let_sure_credit_number.getVisibility() == View.VISIBLE) {
                isCardFromEdit = true;
            } else {
                shouldCheckCard(true);
                let_sure_credit_number.getEditText().setText("");
                ToastUtil.toast(CreditFormInputActivity.this,
                        getString(R.string.toast_input_edit_history), Toast.LENGTH_LONG);
                isFromHistory = false;
                return;
            }
        }

        // 没有编辑历史记录卡号，或者是直接输入卡号
        if (StringUtil.isEmpty(cardNO)) {
            ToastUtil
                    .toast(CreditFormInputActivity.this, getString(R.string.toast_card_number_null),
                            Toast.LENGTH_LONG);
            return;
        }

        // 确认输入栏是否显示
        if (let_sure_credit_number.getVisibility() == View.VISIBLE) {
            if (StringUtil.isEmpty(repeatCardNO)) {
                ToastUtil.toast(CreditFormInputActivity.this,
                        getString(R.string.toast_card_number_null), Toast.LENGTH_LONG);
                return;
            } else if (!cardNO.equals(repeatCardNO)) {
                ToastUtil.toast(CreditFormInputActivity.this,
                        getString(R.string.toast_input_not_equal), Toast.LENGTH_LONG);
                return;
            }
        }

        //金额验证
        amount = StringUtil.formatString(let_repayment_money.getEditText().getText().toString());
        if (StringUtil.isEmpty(amount)) { // 金额是否填写
            ToastUtil.toast(CreditFormInputActivity.this, getString(R.string.toast_money_null),
                    Toast.LENGTH_LONG);
            return;
        } else {
            if (!StringUtil.isAmountVaild(amount)) {
                ToastUtil.toast(CreditFormInputActivity.this,
                        getString(R.string.toast_money_format_error), Toast.LENGTH_LONG);
                return;
            } else {
                try {
                    if (new BigDecimal(amount).compareTo(new BigDecimal("9999999.99")) > 0) {
                        ToastUtil.toast(CreditFormInputActivity.this,
                                getString(R.string.monty_input_super), Toast.LENGTH_LONG);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.toast(CreditFormInputActivity.this,
                            getString(R.string.money_input_error), Toast.LENGTH_LONG);
                    return;
                }
            }
        }

        //手机号验证
        if (ls_sms_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.ON) {
            phoneString = let_phone_number.getEditText().getText().toString();
            if (!PhoneNumberUtil.checkPhoneNumber(StringUtil.formatString(phoneString))) {
                ToastUtil.toast(CreditFormInputActivity.this,
                        getString(R.string.phone_illegal_content), Toast.LENGTH_LONG
                );
                return;
            }
        }
//        queryRepayDate();
//        startNextStepActivity();
        checkCreditNum();
    }

    private void checkCreditNum() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.BANK_BY_CARD);
        request.setRequestURL(
                request.getRequestURL().replace("{cardNo}", cardNO).replace("{type}", "nm"));
        request.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                JSONObject jsonObject = null;
                try {
                    if (ResponseCode.SuccessCode.equals(resultForService.retCode)) {
                        jsonObject = new JSONObject(resultForService.retData);
                        String type = jsonObject.getString("cardType");
                        if (TYPE_CREDIT_CARD.equals(type)) {
                            if ("".equals(repayDate) || "".equals(agreement) || !preCardNumber
                                    .equals(cardNO)) {//如果日期及是否支持签约还款状态还没有查询ok，则去调用接口查询
                                status = UpdateDateStatus.updateDateToStartNext;
                                queryRepayDate();
                            } else {//如果日期已经查询ok，则开始查询信用卡相关银行信息
                                startNextStepActivity();
                            }
                        } else {
                            invalidCreditCard();
                        }
                    } else {//非法卡号
                        hideProgressDialog();
                        invalidCreditCard();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                    LogUtil.print(e.toString());
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        }));
        request.execute();
    }

    /**
     * 无效卡
     */
    private void invalidCreditCard() {
        hideProgressDialog();
        enableView(tv_next_step);
        ToastUtil.toast(CreditFormInputActivity.this, getString(R.string.toast_creditcard));
    }


    protected void enableView(View view) {
        if (view != null && !view.isEnabled()) {
            view.setEnabled(true);
            view.setPressed(false);
        }
    }

    /**
     * 显示提醒日期对话框
     */
    private void showNotifyDateDialog() {
        DateInputDialog dialog = new DateInputDialog();
        dialog.setTitle(getString(R.string.notify_date));
        dialog.setButtons(new String[]{getString(R.string.cancel), getString(R.string.setting)});
        dialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                switch (index) {
                    case 0: {
                        isPayRemind = false;
                        ls_repayment_notify.setSwitchStatus(LabelSwitch.ESwitchStatus.OFF);
                        dialog.dismiss();
                        break;
                    }
                    case 1: {
                        isPayRemind = true;
                        dateText = ((DateInputDialog) dialog).getDateText();
                        if (TextUtils.isEmpty(dateText)) {
                            ToastUtil.toast(CreditFormInputActivity.this, "请输入提醒日期");
                        } else {
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("每月").append(dateText).append("日");
                            ls_repayment_notify.setTipText(stringBuffer.toString());
                            dialog.dismiss();
                        }
                        break;
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager());
        ShoudanStatisticManager.getInstance().onEvent(getEvent(pointWhere_1), context);
    }

    /**
     * 跳转到历史还款信用卡
     */
    private void startActivityToPaymentHistoryRecordCardActivity() {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Credit_Card_HistoryList, context);
        Intent intent = new Intent(CreditFormInputActivity.this,
                PaymentHistoryRecordCardActivity.class
        );
        startActivityForResult(intent, UniqueKey.CARD_NUMBER_REQUEST_CODE);
    }

    /**
     * 跳转到信用卡还款界面（界面同余额查询界面）
     */
    private void startNextStepActivity() {
        if (ls_repayment_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.OFF) {
            dateText = "";
        }
        saveNotifications(cardNO, dateText);
        CreditCardPaymentInfo creditCardPaymentInfo = new CreditCardPaymentInfo();
        creditCardPaymentInfo.setAmount(amount);
        creditCardPaymentInfo.setNumber(cardNO);
        String mobileno = StringUtil
                .formatString(let_phone_number.getEditText().getText().toString());
        String issms = TextUtils.isEmpty(mobileno) ? "0" : "1";
        if (ls_sms_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.OFF) {
            issms = "0";
            mobileno = "";
        }
        creditCardPaymentInfo.setMobileno(mobileno);
        creditCardPaymentInfo.setIssms(issms);
        terminalSignUp(creditCardPaymentInfo);
//        Intent intent = new Intent(CreditFormInputActivity.this,
//                                   CreditCardPaymentActivity.class);
//        intent.putExtra(ConstKey.TRANS_INFO, creditCardPaymentInfo);
//        startActivity(intent);
    }

    /**
     * 终端签到
     *
     * @param creditCardPaymentInfo
     */
    private void terminalSignUp(final CreditCardPaymentInfo creditCardPaymentInfo) {
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onError(String msg) {
                hideProgressDialog();
                ToastUtil.toast(CreditFormInputActivity.this, getString(R.string.socket_fail));
            }

            @Override
            public void onSuccess() {
                queryHandlingCharge(creditCardPaymentInfo);
            }
        });
    }

    /**
     * 手续费查询
     */
    private void queryHandlingCharge(final CreditCardPaymentInfo creditCardPaymentInfo) {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.TRADE);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid", "M50001");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("mobileno", creditCardPaymentInfo.getMobileno());
        params.put("inpan", creditCardPaymentInfo.getNumber());
        params.put("amount", Utils.yuan2Fen(creditCardPaymentInfo.getAmount()));
        params.put("issms", creditCardPaymentInfo.getIssms());

        //这里是一个坑, mac计算使用的ksn判断在连上物理终端后,导致使用的mac秘钥为物理终端签到下发的
        String ksn = ApplicationEx.getInstance().getSession().getUser().getTerminalId();

        params.put("termid", ksn);
        params.put("rnd", Mac.getRnd());
        params.put("chntype", BusinessRequest.CHN_TYPE);
        params.put("chncode", BusinessRequest.CHN_CODE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String price = jsonObject.getString("price");
                        creditCardPaymentInfo.setPrice(Utils.fen2Yuan(price));
                        String fee = Utils.fen2Yuan(jsonObject.getString("fee"));
                        creditCardPaymentInfo.setFee(fee);
                        String srcsid = jsonObject.getString("sid");
                        creditCardPaymentInfo.setSrcSid(srcsid);
                        if (jsonObject.has("username") && !"null"
                                .equals(jsonObject.optString("username")) && !TextUtils
                                .isEmpty(jsonObject.optString("username"))) {
                            String userName = jsonObject.getString("username");
                            creditCardPaymentInfo.setUserName(userName);
                        }
                        Intent intent = new Intent(CreditFormInputActivity.this,
                                CreditCardPaymentActivity.class
                        );
                        intent.putExtra(ConstKey.TRANS_INFO, creditCardPaymentInfo);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(CreditFormInputActivity.this, resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(CreditFormInputActivity.this, getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    /**
     * 保存还款提醒
     *
     * @param cardNO
     * @param text
     */
    private void saveNotifications(String cardNO, String text) {
        NotificationUtil notificationsUtil = NotificationUtil.getInstance();
        notificationsUtil.addNotification(cardNO, text);
    }

    /**
     * 卡号输入控制类，主要处理输入信用卡号后到账日期的显示及隐藏
     */
    class CardNOHelper extends CardNumberHelper {

        @Override
        public void afterTextChanged(Editable text) {
            if (text.length() < 19) {//如果卡号不足为正常卡号，则隐藏到账日期提醒
                tv_date_prompt.setVisibility(View.GONE);
            } else {//如果前后两次要检查到账日期的信用卡号一致，则直接显示
                cardNO = let_credit_number.getEditText().getText().toString().trim()
                        .replace(" ", "");
                if (cardNO.equals(preCardNumber)) {
                    tv_date_prompt.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 被调用的Activity返回结果时，处理相应数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果没有返回结果，直接跳过
        if (resultCode == RESULT_OK) {
            if (requestCode == UniqueKey.CARD_NUMBER_REQUEST_CODE) {
                // 根据请求的code，处理返回结果
                shouldCheckCard(false);
                cardNumberFromHistory = data.getStringExtra(UniqueKey.CREDIT_CARD_NUMBER);
                let_credit_number
                        .setText(CardUtil.formatCardNumberWithSpace(cardNumberFromHistory));
                status = UpdateDateStatus.updateDateToShowView;
                isFromHistory = true;
                queryRepayDate();
            } else if (requestCode == UniqueKey.PHONE_NUMBER_REQUEST_CODE) {
                new ContactBookUtil(this, let_phone_number.getEditText(), data)
                        .setOnPhoneNumberSelectedListener(
                                new ContactBookUtil.OnPhoneNumberSelectedListener() {
                                    @Override
                                    public void onPhoneNumberSelected(String phoneNumber) {
                                        let_phone_number.getEditText().setText(
                                                StringUtil.formatString(phoneNumber));
                                    }

                                    @Override
                                    public void onNoNumber(String hint) {

                                    }
                                });
            }
        }
    }

    private void shouldCheckCard(boolean should) {
        if (should) {
            let_sure_credit_number.setVisibility(View.VISIBLE);
            seperator_below_sure_credit.setVisibility(View.VISIBLE);
        } else {
            let_sure_credit_number.setVisibility(View.GONE);
            seperator_below_sure_credit.setVisibility(View.GONE);
        }
    }
}

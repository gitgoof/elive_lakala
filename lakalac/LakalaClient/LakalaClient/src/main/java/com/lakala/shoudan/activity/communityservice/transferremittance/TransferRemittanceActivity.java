package com.lakala.shoudan.activity.communityservice.transferremittance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.BankInfo;
import com.lakala.platform.bean.CardInfo;
import com.lakala.platform.bean.TransferFeeInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.TransferEnum;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.OpenBankInfoCallback;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.component.MoneyInputWatcher;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.CardEditFocusChangeListener;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.ContactBookUtil;
import com.lakala.shoudan.util.ScreenUtil;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.LabelSwitch;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.PhoneNumberInputWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.text.InputFilter.LengthFilter;
import static com.lakala.shoudan.R.id.support_bank_list;


/**
 * Created by HUASHO on 2015/1/16.
 * 转账汇款
 */
public class TransferRemittanceActivity extends BasePwdAndNumberKeyboardActivity implements OnClickListener {

    private static final String TAG_CARD_NUMBER = "INPUT_CREDIT_NUMBER";
    private static final String TAG_AMOUNT = "amount";
    private LabelEditText let_open_bank;//开户银行
    private LabelEditText let_input_credit_number;//收款人卡号
    private LabelEditText let_real_name;//收款人姓名
    private LabelEditText let_transfer_money;//转账金额
    private LabelEditText let_charing_money;//手续费
    //    private ListView lv_remit_mode_list;//转账的listview
    private LabelEditText let_remark;//备注
    private LabelSwitch ls_free_sms_notify;//免费短信通知对方
    private LabelEditText let_phone_number;//手机号码
    private CheckBox cb_read_and_accept;
    private TextView tv_lkl_transfer_remit_service_protocol;//转账汇款协议
    private TextView btn_next;//下一步

    private List<TransferFeeInfo> feeRules = null;//费率列表

    private String bankname = "";//银行名称
    private String bankCode = "";//银行代码
    private String openBank;
    private String cardNumber;//收款账号
    private String name;//真实姓名
    private String amount;//金额
    private String phone;//电话号码

    private boolean paymentflag;//实时支付
    private boolean commonflag;//普通支付

    private int selectPosition = -1;//选择转账方式所在位置
    private final int SLOW_MODE = 1;//慢支付
    private final int JIT_MODE = 3;//实时支付

    private BankInfo bankInfo;
    private int transType;
    private String transTypeText;
    private ViewGroup viewGroup;
    private String cardHistory;
    private ArrayList<TransferFeeInfo> supportList = new ArrayList<>();
    private ListSelectAdapter adapter;
    private ListView feeListView;
    private TextView modeTitleText;
    private View receiveTime;
    private CardEditFocusChangeListener cardChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_remittance);
        viewGroup = (ViewGroup) findViewById(R.id.group);
        initUI();
        ShoudanStatisticManager.getInstance().onEvent(getEvent("转账进入"), context);
        getFeeRules();
    }


    private void getFeeRules() {
        feeRules = new ArrayList<>();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.FEE_TRANSFER);
        request.setAutoShowProgress(false);
        HttpRequestParams params = request.getRequestParams();
        params.put("feeType", "M50010");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        int length = 0;
                        if (jsonArray != null) {
                            length = jsonArray.length();
                        }
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = null;
                            String value = String.valueOf(jsonArray.get(i));
                            if (TextUtils.isEmpty(value) || "null".equals(value)) {
                                continue;
                            }
                            try {
                                jsonObject = new JSONObject(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            TransferFeeInfo info = JSON.parseObject(jsonObject.toString(),
                                    TransferFeeInfo.class);
                            feeRules.add(info);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.socket_fail));
                    }
                } else {
                    ToastUtil.toast(TransferRemittanceActivity.this, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.scroll_keyboard));
    }

    protected void initUI() {
        TransferEnum.transfer.setContanct(false);
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        navigationBar.setTitle(R.string.transfer_remittance);
        navigationBar.setActionBtnText(R.string.common_transfer_user);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else {
                    hideNumberKeyboard();
                    startRemittanceHistoryCardDataActivity();
                }
            }
        });

        //开户银行
        let_open_bank = (LabelEditText) findViewById(R.id.let_open_bank);
        let_open_bank.getEditText().setCursorVisible(false);
        let_open_bank.getEditText().setFocusableInTouchMode(false);
        let_open_bank.getEditText().setClickable(false);
        findViewById(support_bank_list).setOnClickListener(openBankClickListener);
//        let_open_bank.setOnClickItemListener(new IconItemView.OnClickItemListener() {
//            @Override
//            public void OnClickItem(View view, IconItemView.ItemType type) {
//                openBankClickListener.onClick(view);
//            }
//        });
//        let_open_bank.getEditText().setOnClickListener(openBankClickListener);

        //收款人卡号
        let_input_credit_number = (LabelEditText) findViewById(R.id.let_input_credit_number);
        let_input_credit_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        let_input_credit_number.getEditText().setLongClickable(false);
        let_input_credit_number.getEditText().setTag(TAG_CARD_NUMBER);
        let_input_credit_number.setOnFocusChangeListener(null);
        let_input_credit_number.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cardNum = StringUtil.trim(editable.toString());
                if (cardNum.equals("")) {
                    let_open_bank.setText("");
                }
            }
        });

        cardChangeListener = new CardEditFocusChangeListener();
        cardChangeListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if (openBankInfo == null) {
                    return;
                }
                bankInfo = OpenBankInfo.construct(openBankInfo);
                UpdateRemiteModeLayout();
                let_open_bank.getEditText().setText(bankInfo.getBankName());
            }
        });
//
//        //确认卡号
//        let_sure_credit_number = (LabelEditText) findViewById(R.id.let_sure_credit_number);
//        let_sure_credit_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
//        let_sure_credit_number.getEditText().setLongClickable(false);
//        let_sure_credit_number.getEditText().setTag(TAG_CARD_NUMBER);
//        let_sure_credit_number.setOnFocusChangeListener(null);

        //收款人姓名
        let_real_name = (LabelEditText) findViewById(R.id.let_real_name);
        let_real_name.getEditText().setFocusableInTouchMode(true);
        LengthFilter[] filters1 = {new LengthFilter(20)};
        let_real_name.getEditText().setFilters(filters1);
        let_real_name.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        let_real_name.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        let_real_name.getEditText().setOnFocusChangeListener(this);
        let_real_name.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                CommonUtil.hideKeyboard(let_real_name.getEditText());
                return false;
            }
        });

        //转账金额
        let_transfer_money = (LabelEditText) findViewById(R.id.let_transfer_money);
        let_transfer_money.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LengthFilter[] filters2 = {new LengthFilter(10)};
        let_transfer_money.getEditText().setFilters(filters2);
        let_transfer_money.getEditText().addTextChangedListener(amountTextWatcher);
        let_transfer_money.getEditText().setLongClickable(false);
        let_transfer_money.getEditText().setTag(TAG_AMOUNT);
        let_transfer_money.setOnFocusChangeListener(null);

        //手续费
        let_charing_money = (LabelEditText) findViewById(R.id.let_charing_money);
        let_charing_money.getEditText().setInputType(InputType.TYPE_NULL);
        let_charing_money.getEditText().setCursorVisible(false);
        let_charing_money.getEditText().setFocusable(false);
        let_charing_money.setOnClickListener(this);
        let_charing_money.getEditText().setTag(TAG_AMOUNT);
        //收费规则
        TextView charing_rules = (TextView) findViewById(R.id.charing_rules);
//        charing_rules.getRightText().getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        charing_rules.setOnClickListener(this);

        //备注
        let_remark = (LabelEditText) findViewById(R.id.let_remark);
        let_remark.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        LengthFilter[] filters3 = {new LengthFilter(10)};
        let_remark.getEditText().setFilters(filters3);
        let_remark.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        let_remark.getEditText().setOnFocusChangeListener(this);
        let_remark.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                CommonUtil.hideKeyboard(let_remark.getEditText());
                return false;
            }
        });

        //免费短信通知对方
        final View seperator_below_ls_free_sms_nofify = findViewById(
                R.id.seperator_below_ls_free_sms_nofify);
        ls_free_sms_notify = (LabelSwitch) findViewById(R.id.ls_free_sms_notify);
        ls_free_sms_notify.setOnSwitchListener(new LabelSwitch.OnSwitchListener() {
            @Override
            public void onSwitch(LabelSwitch.ESwitchStatus status) {
                if (status == LabelSwitch.ESwitchStatus.ON) {
                    TransferEnum.transfer.setSmsOpen(true);
                    let_phone_number.setVisibility(View.VISIBLE);
                    seperator_below_ls_free_sms_nofify.setVisibility(View.VISIBLE);
                } else {
                    TransferEnum.transfer.setSmsOpen(false);
                    ((ViewGroup) let_phone_number.getParent()).requestFocus();
                    let_phone_number.setVisibility(View.GONE);
                    hideSoftInputFromWindow();
                    seperator_below_ls_free_sms_nofify.setVisibility(View.GONE);
                }
            }
        });

        //手机号码
        let_phone_number = (LabelEditText) findViewById(R.id.let_phone_number);
        let_phone_number.getEditText().addTextChangedListener(new PhoneNumberInputWatcher());// 过滤电话号码格式
        let_phone_number.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        let_phone_number.getEditText().setText(ApplicationEx.getInstance().getUser().getLoginName());//登入用户名
        let_phone_number.getRightIconImageView().setOnClickListener(this);

        //checkbox 和 转账汇款协议
        cb_read_and_accept = (CheckBox) findViewById(R.id.cb_read_and_accept);
        tv_lkl_transfer_remit_service_protocol = (TextView) findViewById(R.id.tv_lkl_transfer_remit_service_protocol);
        tv_lkl_transfer_remit_service_protocol.setOnClickListener(this);
        cb_read_and_accept.setChecked(true);
        cb_read_and_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    btn_next.setEnabled(true);
                } else {
                    btn_next.setEnabled(false);
                }
            }
        });


        //下一步
        btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        //键盘
        initNumberKeyboard();
        initNumberEdit(let_input_credit_number.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        initNumberEdit(let_transfer_money.getEditText(), CustomNumberKeyboard.EDIT_TYPE_FLOAT, 30);

        //屏幕
        ScreenUtil.getScrrenWidthAndHeight(TransferRemittanceActivity.this);


        modeTitleText = (TextView) findViewById(R.id.mode_title);
        modeTitleText.setVisibility(View.GONE);
        receiveTime = findViewById(R.id.receive_time);
        feeListView = (ListView) findViewById(R.id.remit_mode_list);
        feeListView.setVisibility(View.GONE);
        feeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                TransferEnum.transfer.setReachTime(selectPosition == 0 ? true : false);
                adapter.notifyDataSetChanged();
                feeModeClicked(adapter.getItem(position));
            }
        });
    }


    private String event = "";

    private void checkCardBin(String cardNo) {
        showProgressWithNoMsg();
        CommonServiceManager.getInstance().getCardBIN(BankBusid.TRANSFER, cardNo, new OpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                hideProgressDialog();
                if (openBankInfo == null && !TextUtils.isEmpty(errMsg)) {
                    toast(errMsg);
                } else {
                    bankname = openBankInfo.bankname;
                    bankCode = openBankInfo.bankCode;
                    checkCardInfo(openBankInfo);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
    }

    private void feeModeClicked(TransferFeeInfo feeInfo) {
        if (feeInfo == null) {
            return;
        }
        transType = feeInfo.getFeeId();
        let_charing_money.getEditText().setText(getFee(transType));
        transTypeText = feeInfo.getTransName();
    }

    private OnClickListener openBankClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hideNumberKeyboard();
            startCommonBankListActivity();
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (v instanceof EditText) {
            String tag = String.valueOf(v.getTag());
            EditText editText = (EditText) v;
            if (TAG_CARD_NUMBER.equals(tag)) {
                cardChangeListener.onFocusChange(v, hasFocus);
                if (hasFocus) {
                    String newText = editText.getText().toString().replace(" ", "");
                    editText.setText(newText);
                    editText.setSelection(newText.length());
                } else {
                    String text = CardUtil.formatCardNumberWithSpace(editText.getText().toString());
                    editText.setText(text);
//                    if(let_sure_credit_number.getVisibility() == View.GONE && cardHistory != null
//                            && !TextUtils.equals(cardHistory, StringUtil.formatString(text))){
//                        //如果还没有确认卡号，并且卡号被修改了
//                        ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.credit_card_NO_repeat_hint));
//                        shouldCheckCard(true);
//                    }
                    hideNumberKeyboard();
                }
            } else if (TAG_AMOUNT.equals(tag)) {
                String amount = StringUtil.formatString(editText.getText().toString());
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.let_open_bank://开户行
//                hideNumberKeyboard();
//                startCommonBankListActivity();
                break;
            case R.id.charing_rules://收费规则
                startProtocalActivity();
                break;
            case R.id.right_icon://手机号
                hideNumberKeyboard();
                ContactBookUtil.onContactBookClick(TransferRemittanceActivity.this);
                break;
            case R.id.tv_lkl_transfer_remit_service_protocol://转账汇款协议
                startTransferRemitServiceProtocolActivity();
                break;
            case R.id.btn_next://下一步
                hideNumberKeyboard();
                if (!isInputValid()) {
                    return;
                }
                OpenBankInfo bankInfo = cardChangeListener.getOpenBankInfo(cardNumber);
                checkCardInfo(bankInfo);
                break;
        }
    }

    private void checkCardInfo(OpenBankInfo bankInfo) {
        if (bankInfo != null) {
            boolean isInputInfoValid = true;
            if (!TextUtils.equals(bankInfo.bankCode, bankCode)) {
                isInputInfoValid = false;
            }
            if (!TextUtils.equals(bankInfo.bankname, bankname)) {
                isInputInfoValid = false;
            }
            if (!TextUtils.equals(bankInfo.acccountType, "D")) {
                isInputInfoValid = false;
            }
            if (!isInputInfoValid) {
                toast(getString(R.string.not_support_bank));
            } else {
                nextStep();
            }
        } else {
            checkCardBin(cardNumber);
        }
    }

    /**
     * 银行列表页面
     */
    private void startCommonBankListActivity() {
//        Intent intent = CommonBankListActivity.getIntent(this,
//                let_open_bank.getEditText().getText().toString().trim(),
//                UniqueKey.ZHUANGZHANG_ID);
        ShoudanStatisticManager.getInstance().onEvent(getEvent("查看支持银行"), this);
        BankChooseActivity.openForResult(context, BankBusid.TRANSFER, BankInfoType.DEFAULT, 40080);
    }

    /**
     * 收费规则
     */
    private void startProtocalActivity() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent("收费规则"), this);
        ProtocalActivity.open(context, ProtocalType.ZHUAN_ZHANG_FEE_RULE);
    }

    /**
     * 转账汇款服务协议
     */
    private void startTransferRemitServiceProtocolActivity() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent("汇款协议"), this);
        //此url原收款宝需要访问接口获取,吴春然已经确认使用商户通url即可
        String title = "转账服务协议";
        String url = "https://download.lakala.com/lklmbl/html/sht/sht_trans.html";
        ProtocalActivity.open(context, title, url);
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean isInputValid() {

        //姓名
        name = StringUtil.formatString(let_real_name.getEditText().getText().toString().trim());
        if (name.equals("")) {
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.input_name_prompt), Toast.LENGTH_LONG);
            return false;
        }
        //卡号
        cardNumber = StringUtil.formatString(let_input_credit_number.getEditText().getText().toString());
        if (StringUtil.isEmpty(cardNumber)) {
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.input_account_prompt), Toast.LENGTH_LONG);
            return false;
        }

        //银行
        openBank = let_open_bank.getEditText().getText().toString().trim();
        if (openBank.length() == 0) {
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.select_receiver_bank), Toast.LENGTH_LONG);
            return false;
        }

        //金额
        amount = let_transfer_money.getEditText().getText().toString().trim().replace(",", "");
        if (StringUtil.isEmpty(amount)) {
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.please_input_amount), Toast.LENGTH_LONG);
            return false;
        } else {
            if (!StringUtil.isAmountVaild(amount)) {
                ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.toast_money_format_error), Toast.LENGTH_LONG);
                return false;
            } else {
                try {
                    if (new BigDecimal(amount).compareTo(new BigDecimal("9999999.99")) > 0) {
                        ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.monty_input_super), Toast.LENGTH_LONG);
                        return false;
                    }

                    if (new BigDecimal(amount).compareTo(new BigDecimal("1")) < 0) {
                        ToastUtil.toast(TransferRemittanceActivity.this, "单笔金额小于限制，请重新输入（金额必须不小于1元）。", Toast.LENGTH_LONG);
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.money_input_error), Toast.LENGTH_LONG);
                    return false;
                }

            }
        }
        if (selectPosition == -1) {//未选择转账方式
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.select_remit_mode_prompt), Toast.LENGTH_LONG);
            return false;
        }
        //手机号码
        phone = let_phone_number.getEditText().getText().toString();
        TransferEnum.transfer.setPhone(!TextUtils.isEmpty(phone) ? true : false);
        if (ls_free_sms_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.ON) {
            if (!PhoneNumberUtil.checkPhoneNumber(StringUtil.formatString(phone))) {
                ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.phone_illegal_content), Toast.LENGTH_LONG);
                return false;
            }
        }
        return true;
    }

    /**
     * 下一步
     */
    private void nextStep() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent("汇款成功"), this);
        String fee = StringUtil.formatString(let_charing_money.getEditText().getText().toString());
        String remark = let_remark.getEditText().getText().toString();
        RemittanceTransInfo info = new RemittanceTransInfo();
        info.setOpenBank(openBank);
        info.setBankCode(bankCode);
        info.setCardNumber(cardNumber);
        info.setName(name);
        info.setAmount(amount);
        info.setTransFee(fee);
        if (ls_free_sms_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.ON) {
            info.setPhone(phone);
        }
        info.setTransType(transType);
        info.setTransTypeText(transTypeText);
        info.setRemark(remark);
        LogUtil.print(JSON.toJSONString(info));
        Intent intent = new Intent(this, RemittanceConfirmActivity.class);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    /**
     * 获取手续费
     */
    private String getFee(int type) {
        String amountString = let_transfer_money.getEditText().getText().toString().trim().replace(",", "");
        String fee = "";
        if ("".equals(amountString)) {
            return "0.00";
        }
        if (!amountString.equals(".") && feeRules.size() > 0) {
            BigDecimal amount = new BigDecimal(amountString);
            if (amount.compareTo(new BigDecimal("0")) == 0) {
                return "0.00";
            }
            TransferFeeInfo feeRule = getFeeItem(type);
            BigDecimal countFee = amount.multiply(new BigDecimal(feeRule.getChargeRate()));
            int count01 = countFee.compareTo(new BigDecimal(feeRule.getMinFee()).divide(new BigDecimal("100")));
            if (count01 == -1 || count01 == 0) {//小于等于最小手续费
                fee = new BigDecimal(feeRule.getMinFee()).divide(new BigDecimal("100"))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            }
            int count02 = countFee.compareTo(new BigDecimal(feeRule.getMaxFee()).divide(new BigDecimal("100")));
            if (count02 == 1 || count02 == 0) {//大于等于最大手续费
                fee = new BigDecimal(feeRule.getMaxFee()).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            }
            if (fee.equals("")) {//按比例计算
                fee = countFee.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            }
        }
        if (!fee.equals("")) {
            fee = StringUtil.formatAmount(fee);
        }
        return fee;
    }

    /**
     * 跳转到转账历史收款人卡号
     */
    private void startRemittanceHistoryCardDataActivity() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent("常用收款人"), this);
        Intent intent = new Intent(TransferRemittanceActivity.this, RemittanceHistoryRecordCardActivity.class);
        startActivityForResult(intent, UniqueKey.BANK_HISTORY_CODE);
    }

    private String getEvent(String type) {
        String event = "";
        switch (type) {
            case "转账进入":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_Remittance_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_Remittance_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_Remittance_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_Remittance_public;
                }
                ShoudanStatisticManager.getInstance().onEvent(event, context);
                break;
            case "常用收款人":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_commenPayee_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_commenPayee_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_commenPayee_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_commenPayee_public;
                }
                break;
            case "汇款协议":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_readInstruction_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_readInstruction_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_readInstruction_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_readInstruction_public;
                }
                break;
            case "收费规则":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_chargeRule_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_chargeRule_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_chargeRule_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_chargeRule_public;
                }
                break;
            case "查看支持银行":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_supportbankList_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_supportbankList_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_supportbankList_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_supportbankList_public;
                }
                break;
            case "汇款成功":
                if (PublicEnum.Business.isDirection()) {
                    event = ShoudanStatisticManager.Transfer_confirm_direction;
                } else if (PublicEnum.Business.isHome()) {
                    event = ShoudanStatisticManager.Transfer_confirm_home;
                } else if (PublicEnum.Business.isAd()) {
                    event = ShoudanStatisticManager.Transfer_confirm_advert;
                } else if (PublicEnum.Business.isPublic()) {
                    event = ShoudanStatisticManager.Transfer_confirm_public;
                }
                String contactStatus = TransferEnum.transfer.isContanct() ? "选" : "填";
                String smsStatus = TransferEnum.transfer.isSmsOpen() ? "是" : "否";
                String reachStatus = TransferEnum.transfer.isReachTime() ? "1" : "2";
                String phoneStatus = TransferEnum.transfer.isPhone() ? "填" : "选";
                event = String.format(event, contactStatus, reachStatus, smsStatus, phoneStatus);
                break;
        }
        return event;
    }

    /**
     * 更新转账汇款转账方式界面
     */
    private void UpdateRemiteModeLayout() {
        bankname = bankInfo.getBankName();
        paymentflag = bankInfo.isPaymentflag();
        commonflag = bankInfo.isCommonflag();
        bankCode = bankInfo.getBankCode();
        let_open_bank.getEditText().setText(bankname);
        let_charing_money.getEditText().setText("0.00");
        supportList.clear();
        if (isRemitModeValid(SLOW_MODE)) {//普通转账
            TransferFeeInfo item = getFeeItem(SLOW_MODE);
            supportList.add(item);
        }
        if (isRemitModeValid(JIT_MODE)) {//实时转账
            TransferFeeInfo item = getFeeItem(JIT_MODE);
            supportList.add(item);
        }
        if (supportList.size() == 1) {//如果只支持一种转账方式
            selectPosition = 0;
        }
        if (selectPosition != -1) {
            TransferFeeInfo item = supportList.get(selectPosition);
            feeModeClicked(item);
        }
        adapter = new ListSelectAdapter(TransferRemittanceActivity.this);
        feeListView.setAdapter(adapter);
//        ListUtil.setListViewHeightBasedOnChildren(feeListView);
        modeTitleText.setVisibility(View.VISIBLE);
        feeListView.setVisibility(View.VISIBLE);
        receiveTime.setVisibility(View.VISIBLE);
    }

    private class ListSelectAdapter extends BaseAdapter {
        private Context context;

        private class TypeHolder {
            public ImageView imageView;
            public TextView textView;
            public ImageView editImage;
        }

        public ListSelectAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return supportList.size();
        }

        @Override
        public TransferFeeInfo getItem(int position) {
            return supportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TypeHolder typeHolder;
            if (convertView == null) {
                convertView = (LinearLayout) LinearLayout.inflate(context, R.layout.shoudan_common_select_item_image, null);
                typeHolder = new TypeHolder();
                typeHolder.textView = (TextView) convertView.findViewById(R.id.id_check_text);
                typeHolder.imageView = (ImageView) convertView.findViewById(R.id.id_check_image);
                typeHolder.editImage = (ImageView) convertView.findViewById(R.id.id_check_check_btn);
                convertView.setTag(typeHolder);
            } else {
                typeHolder = (TypeHolder) convertView.getTag();
            }
            TransferFeeInfo transInfo = getItem(position);
            typeHolder.textView.setId(position);
            typeHolder.textView.setText(transInfo.getTransName());
            typeHolder.editImage.setId(position);

            if (position == selectPosition) {
                typeHolder.editImage.setBackgroundResource(R.drawable.home_icon_xzan_sel);
                typeHolder.editImage.setVisibility(View.VISIBLE);
            } else {
                typeHolder.editImage.setBackgroundResource(R.drawable.home_icon_xzan_nol);
                typeHolder.editImage.setVisibility(View.VISIBLE);
            }
            typeHolder.imageView.setVisibility(View.GONE);
            return convertView;
        }
    }

    /**
     * 是否支持某种转账方式
     *
     * @param type JIT_MODE实时转账    FAST_MODE快速转账   SLOW_MODE慢转账
     * @return true 支持       false不支持
     */
    private boolean isRemitModeValid(int type) {
        boolean isRemitModeValid = false;
        switch (type) {
            case JIT_MODE: {
                isRemitModeValid = paymentflag;
                break;
            }
            case SLOW_MODE: {
                isRemitModeValid = commonflag;
                break;
            }
        }
        return isRemitModeValid;
    }

    /**
     * 获取指定Id手续费规则项目
     *
     * @param feeId 手续费项目Id
     * @return
     */
    private TransferFeeInfo getFeeItem(int feeId) {
        TransferFeeInfo feeRule = null;
        int size = feeRules.size();
        for (int i = 0; i < size; i++) {
            if (feeRules.get(i).getFeeId() == feeId) {
                feeRule = feeRules.get(i);
                break;
            }
        }
        return feeRule;
    }

    /**
     * 返回的结果在这里处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == UniqueKey.BANK_HISTORY_CODE && resultCode == RESULT_OK) {//常用联系人
                TransferEnum.transfer.setContanct(true);
                CardInfo cardInfo = (CardInfo) data.getSerializableExtra(UniqueKey.KEY_CARDINFO);
                String cardNumber = CardUtil.formatCardNumberWithSpace(cardInfo.getCardNo());
                cardHistory = StringUtil.formatString(cardNumber);
                let_input_credit_number.getEditText().setText(cardNumber);
                let_open_bank.getEditText().setText(cardInfo.getBankName());
                let_real_name.getEditText().setText(cardInfo.getAccountName());
                getBankInfoByBankCode(cardInfo.getCardNo());
            } else if (requestCode == 40080 && resultCode == UniqueKey.BANK_LIST_CODE) {//开户行返回
                OpenBankInfo bank = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
                if (bank != null && bankInfo != null && !bank.bankCode.equals(bankInfo.getBankCode())) {
                    let_input_credit_number.getEditText().setText("");
                    let_real_name.getEditText().setText("");
                }
                bankInfo = OpenBankInfo.construct(bank);
                UpdateRemiteModeLayout();
            } else if (requestCode == UniqueKey.PHONE_NUMBER_REQUEST_CODE) {
                new ContactBookUtil(this, let_phone_number.getEditText(), data)
                        .setOnPhoneNumberSelectedListener(new ContactBookUtil.OnPhoneNumberSelectedListener() {
                            @Override
                            public void onPhoneNumberSelected(String phoneNumber) {
                                let_phone_number.getEditText().setText(StringUtil.formatString(phoneNumber));
                            }

                            @Override
                            public void onNoNumber(String hint) {

                            }
                        });
            }
        }
    }

    private void getBankInfoByBankCode(String cardNo) {
        CommonServiceManager.getInstance().getCardBIN(BankBusid.TRANSFER, cardNo, new OpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if (openBankInfo == null) {
                    if (TextUtils.isEmpty(errMsg)) {
                        errMsg = getString(R.string.socket_fail);
                    }
                    toast(errMsg);
                    return;
                }
                bankInfo = OpenBankInfo.construct(openBankInfo);
                UpdateRemiteModeLayout();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isShowNumberKeyboard()) {
                finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * TextWhatcher修改为MoneyInputWatcher
     */
    private TextWatcher amountTextWatcher = new MoneyInputWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0 || selectPosition == -1) {
                let_charing_money.getEditText().setText("0.00");
                return;
            }
            TransferFeeInfo item = adapter.getItem(selectPosition);
            int feeId = item.getFeeId();
            let_charing_money.getEditText().setText(getFee(feeId));
        }
    };
}

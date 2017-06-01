package com.lakala.shoudan.activity.shoudan.records;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.AreaEntity;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.module.holographlibrary.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.fraudmetrix.android.FMAgent.finish;

/**
 * D0开通
 * 业务开通(秒到)
 */
public class SecondOpenActivity extends BasePwdAndNumberKeyboardActivity implements OnClickListener {
    private View openBusinessBtn;
    private TextView agreeProtocal;
    private CheckBox agree;
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                if (isOneDayLoan) {
                    //设置埋点
                    if (PublicEnum.Business.isWithdrawal()) {
                        //从立即提款过来的
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_serDesc, SecondOpenActivity.this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_serDesc);

                    } else if (PublicEnum.Business.isDirection()) {
                        //定向业务-一日贷
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_serDesc, SecondOpenActivity.this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_serDesc);

                    }
                    ProtocalActivity.open(context, ProtocalType.ONE_DAY_LOAN_NOTE);
                } else {
                    ProtocalActivity.open(context, ProtocalType.D0_DESCRIPTION);
                }
                PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_jsType_Rule, context);
            }
        }
    };
    //信用卡编辑框
    private EditText creditCardNo;
    //信用卡所属银行
    private TextView creditCardType;
    private View viewGroup;
    private LinearLayout mLayout;
    boolean isBdFocus = false;
    boolean isCardBINSuc = false;//是否验证卡宾成功

    private OpenBankInfo mOpenBankInfo;//开户行信息
    private EditText idCardNo;
    private TextView tvProvice;
    private TextView tvCity;
    private TextView tvBank;
    private List<AreaEntity> provices = new ArrayList<AreaEntity>();
    private Map<String, List<AreaEntity>> cities = new HashMap<String, List<AreaEntity>>();
    private Map<String, List<AreaEntity>> banks = new HashMap<String, List<AreaEntity>>();

    private Boolean isOneDayLoan;//是否是一日贷申请
    private String note = "首次使用“一日贷”功能，需要开通拉卡拉“一日贷服务”，请确认以下商户信息，并接受《拉卡拉一日贷服务协议》，相关规则说明请参考页面右上角“业务说明”。";
    private TextView tv_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_open_second);
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Homepage, context);
        initUI();
    }

    protected void initUI() {
        isOneDayLoan = getIntent().getBooleanExtra("isOne", false);
        openBusinessBtn = findViewById(R.id.open_business);
        navigationBar.setTitle("立即提款服务开通");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        navigationBar.setActionBtnText("业务说明");

        agree = (CheckBox) findViewById(R.id.check_agree);
        tv_title = (TextView) findViewById(R.id.tv_title);
        agreeProtocal = (TextView) findViewById(R.id.scan_agreement);
        agreeProtocal.setOnClickListener(this);
        openBusinessBtn.setOnClickListener(this);
        tvProvice = (TextView) findViewById(R.id.provice);
        tvProvice.setOnClickListener(this);
        tvCity = (TextView) findViewById(R.id.city);
        tvCity.setOnClickListener(this);
        tvBank = (TextView) findViewById(R.id.bank_list);
        tvBank.setOnClickListener(this);
        mLayout = (LinearLayout) findViewById(R.id.mLayout);

        idCardNo = (EditText) findViewById(R.id.idCardNo);
//信用卡号
        creditCardNo = (EditText) findViewById(R.id.creditCardNo);
        //所属银行
        creditCardType = (TextView) findViewById(R.id.creditCardType);
        viewGroup = (View) findViewById(R.id.view_group);
        if (isOneDayLoan) {
            tv_title.setText(note);
            navigationBar.setTitle("一日贷业务开通");
            agreeProtocal.setText("《拉卡拉一日贷服务协议》");

        }
        creditCardNo.setInputType(InputType.TYPE_CLASS_PHONE);
        creditCardNo.setLongClickable(false);
        creditCardNo.setOnFocusChangeListener(null);

        //键盘
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(creditCardNo, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);

        initMerchantInfo();
    }

    private void initMerchantInfo() {
        TextView merchantName = (TextView) findViewById(R.id.merchantName);

        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();

        MerchantInfo.BusinessAddressEntity addressInfo = info.getBusinessAddress();
        if (addressInfo != null) {
            TextView detailAddress = (TextView) findViewById(R.id.detailAddress);
            detailAddress.setText(addressInfo.getFullDisplayAddress());

        }

        merchantName.setText(info.getBusinessName());

        TextView collectionBank = (TextView) findViewById(R.id.collectionBank);
        collectionBank.setText(String.valueOf(info.getBankName()));

        TextView collectCardNo = (TextView) findViewById(R.id.collectCardNo);
        collectCardNo.setText(String.valueOf(info.getAccountNo()));

        MerchantInfo.UserEntity userInfo = info.getUser();

        TextView realName = (TextView) findViewById(R.id.realName);
        realName.setText(userInfo.getRealName());

        MerchantInfo.UserEntity.IdCardInfoEntity idcardInfo = userInfo.getIdCardInfo();
        String idCard = idcardInfo.getIdCardId();
        if (!TextUtils.isEmpty(idCard)) {
            idCard = idCard.toUpperCase();

            setIdCardNoEditEnable(false);
            idCardNo.setText(idCard);
        }

    }

    private void setIdCardNoEditEnable(boolean isEnable) {
        if (isEnable) {
            idCardNo.setText(idCardNo.getText().toString(), TextView.BufferType.EDITABLE);
        } else {
            idCardNo.setText(idCardNo.getText().toString(), TextView.BufferType.NORMAL);
        }
        idCardNo.setEnabled(isEnable);
        idCardNo.setFocusableInTouchMode(isEnable);
        idCardNo.setFocusable(isEnable);
    }

    /**
     * T0开户方法
     *
     * @param idcardNo
     */
    private void doOpenMethod(final String idcardNo) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                String msg;
                boolean isUserVerified = true;
                if (result.isRetCodeSuccess()) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Homepage_Apply, context);
                    if (isOneDayLoan) {
                        msg = "系统正在处理您的一日贷开通申请,稍后会消息通知您审核结果";

                        //设置埋点
                        if (PublicEnum.Business.isWithdrawal()) {
                            //从立即提款过来的
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open, SecondOpenActivity.this);
                            LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open);
                        } else if (PublicEnum.Business.isDirection()) {
                            //定向业务-一日贷
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open, SecondOpenActivity.this);
                            LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open);
                        }
                    } else {
                        msg = "系统正在处理您的立即提款开通申请,稍后会消息通知您审核结果";
                    }
                } else if ("005019".equals(result.retCode)) {//实名认证未通过
                    msg = "您的商户实名验证未通过，请填写正确的身份证号码后重新提交！";
                    isUserVerified = false;
                } else {
                    msg = result.retMsg;
                }
                hideProgressDialog();
                final boolean isVerified = isUserVerified;
                DialogCreator.createOneConfirmButtonDialog(
                        context, "确定", msg, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                if (!isVerified) {
                                    idCardNo.setText("");
                                    setIdCardNoEditEnable(true);
                                } else {
                                    finish();
                                }
                            }
                        }).show();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().toOpenD02(idcardNo, callback, isOneDayLoan);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (!hasFocus) {
            if (!TextUtils.isEmpty(creditCardNo.getText().toString())) {
                //验证卡宾
                getCardBin();
            } else {
                tipDialog("请输入您的信用卡号");
                creditCardType.setText("请输入您的信用卡号");
                creditCardType.setTextColor(Color.parseColor("#ff8308"));
            }

        }
//        if (hasFocus) {
//            String newText = creditCardNo.getText().toString().replace(" ", "");
//            creditCardNo.setText(newText);
//            creditCardNo.setSelection(newText.length());
//        } else {
//            String text = CardUtil.formatCardNumberWithSpace(creditCardNo.getText().toString());
//            creditCardNo.setText(text);
//        }
        isBdFocus = hasFocus;
        if (hasFocus) {
            mLayout.scrollBy(0, 250);
        } else {
            mLayout.scrollBy(0, -250);
        }
    }


    //新增信用卡输入业务
    //变更后步骤：  1。验证卡宾，所属银行  2。验证三要素   3。确定提交

    /**
     * 验证是否为信用卡
     */
    private void isCreditCard() {

        String idNo = idCardNo.getText().toString();
        String creCardNo = creditCardNo.getText().toString();
        if (idNo.length() == 0 || !Util.checkIdCard(idNo)) {
            ToastUtil.toast(context, R.string.id_no_input_error);
            return;
        }

        showProgressWithNoMsg();

        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    //返回成功
                    doOtherthings();

                } else {
                    tipDialog(result.retMsg);

                }


            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };


        CommonServiceManager.getInstance().toCheckCreditCard(idNo, creCardNo, callback);
    }

    private void doOtherthings() {

        String idNo = idCardNo.getText().toString();

        if (idNo.length() == 0 || !Util.checkIdCard(idNo)) {
            ToastUtil.toast(context, R.string.id_no_input_error);
            return;
        }

        if (isOneDayLoan) {
            //设置埋点
            if (PublicEnum.Business.isWithdrawal()) {
                //从立即提款过来的
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open, this);
                LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open);

            } else if (PublicEnum.Business.isDirection()) {
                //定向业务-一日贷
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open, this);
                LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open);

            }
        }
        if (isOneDayLoan) {
            DialogCreator.createOneConfirmButtonDialog(
                    context, "确定", "是否确认商户信息，并申请一日贷", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            doOpenMethod(idCardNo.getText().toString());
                        }
                    }).show();
        } else {
            DialogCreator.createOneConfirmButtonDialog(
                    context, "确定", "是否确认商户信息，并申请立即提款", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            doOpenMethod(idCardNo.getText().toString());
                        }
                    }).show();
        }

    }

    /**
     * 验证卡宾
     */
    private void getCardBin() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(resultServices.retData);

                        if (jsonObject != null) {
                            OpenBankInfo mOpenBankInfo = OpenBankInfo.construct(jsonObject);

                            if (!TextUtils.isEmpty(mOpenBankInfo.acccountType)) {
                                if ("C".equals(mOpenBankInfo.acccountType)) {
                                    //信用卡
                                    isCardBINSuc = true;
                                    creditCardType.setText(mOpenBankInfo.bankname);
                                    creditCardType.setTextColor(Color.parseColor("#666666"));

                                } else {
                                    isCardBINSuc = false;
                                    //其他类型
                                    creditCardType.setText("请输入正确信用卡卡号");
                                    creditCardType.setTextColor(Color.parseColor("#ff8308"));
                                    tipDialog("卡号错误，请输入正确信用卡号");
                                }


                            } else {
                                isCardBINSuc = false;
                                tipDialog("卡号错误，请输入正确信用卡号");
                                creditCardType.setText("请输入正确信用卡卡号");
                                creditCardType.setTextColor(Color.parseColor("#ff8308"));
                            }

                        }
                    } catch (JSONException e) {
                        isCardBINSuc = false;
                        creditCardType.setText("请输入正确信用卡卡号");
                        creditCardType.setTextColor(Color.parseColor("#ff8308"));
                        e.printStackTrace();
                    }
                } else {
                    isCardBINSuc = false;
//                    tipDialog(resultServices.retMsg);
                    tipDialog("卡号错误，请输入正确信用卡号");
                    creditCardType.setText("请输入正确信用卡卡号");
                    creditCardType.setTextColor(Color.parseColor("#ff8308"));
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.i(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getCardBIN(BankBusid.MPOS_ACCT, creditCardNo.getText().toString(), callback);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.open_business:

                String cardNo = creditCardNo.getText().toString();
                if (TextUtils.isEmpty(cardNo)) {
                    tipDialog("请输入您的信用卡号");
                    return;
                }
                if (!isCardBINSuc) {
                    tipDialog("卡号错误，请输入正确信用卡号");
                    return;
                }

                if (agree.isChecked()) {

                    //验证三要素
                    isCreditCard();


                } else {
                    if (isOneDayLoan) {
                        tipDialog("请勾选同意《拉卡拉一日贷服务协议》");
                    } else {
                        tipDialog("请勾选同意《拉卡拉自然日提前划款服务协议》");
                    }


                }
                break;
            case R.id.scan_agreement:
                if (isOneDayLoan) {
                    //设置埋点
                    if (PublicEnum.Business.isWithdrawal()) {
                        //从立即提款过来的
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open_agreement, this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_withdrawal_serviceDesc_applied_open_agreement);
                    } else if (PublicEnum.Business.isDirection()) {
                        //定向业务-一日贷
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open_agreement, this);
                        LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_serviceDesc_applied_open_agreement);

                    }
                    ProtocalActivity.open(context, ProtocalType.ONE_DAY_SERVICE);
                } else {
                    ProtocalActivity.open(context, ProtocalType.D0_SERVICE);
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Homepage_Agreement, context);
                }
                break;
            case R.id.right_arrow1:
            case R.id.provice: {//省列表
                if (provices == null || provices.size() == 0) {
                    getProviceList();
                } else {
                    showListDialog(provices, proviceListener);
                }
                break;
            }
            case R.id.right_arrow2:
            case R.id.city: {//市列表
                AreaEntity provice = (AreaEntity) tvProvice.getTag();
                if (provice == null) {
                    ToastUtil.toast(context, "请选择省份");
                    return;
                }
                String code = provice.getCode();
                List<AreaEntity> codeCities = cities.get(code);
                if (codeCities == null || codeCities.size() == 0) {
                    getCityList(code);
                } else {
                    showListDialog(codeCities, cityListener);
                }
                break;
            }
            case R.id.right_arrow3:
            case R.id.bank_list: {//银行列表
                AreaEntity city = (AreaEntity) tvCity.getTag();
                if (city == null) {
                    ToastUtil.toast(context, "请选择城市");
                    return;
                }
                String code = city.getCode();
                List<AreaEntity> codeBanks = banks.get(code);
                if (codeBanks == null || codeBanks.size() == 0) {
                    getBankList(code);
                } else {
                    showListDialog(codeBanks, bankListener);
                }
                break;
            }
            default:
                break;
        }
    }

    private void getBankList(final String areaCode) {
        showProgressWithNoMsg();
        final String bankCode = ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if (jsonArray != null) {
                        length = jsonArray.length();
                    }
                    final List<AreaEntity> codeBanks = new ArrayList<AreaEntity>();
                    banks.put(areaCode, codeBanks);
                    AreaEntity area = null;
                    for (int i = 0; i < length; i++) {
                        area = new AreaEntity();
                        codeBanks.add(area);
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if (name.contains("\"")) {
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(codeBanks, bankListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getBankList(bankCode, areaCode, callback);
    }

    private void getCityList(final String code) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if (jsonArray != null) {
                        length = jsonArray.length();
                    }
                    final List<AreaEntity> codeCities = new ArrayList<AreaEntity>();
                    cities.put(code, codeCities);
                    AreaEntity area = null;
                    for (int i = 0; i < length; i++) {
                        area = new AreaEntity();
                        codeCities.add(area);

                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if (name.contains("\"")) {
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(codeCities, cityListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAreaList(code, callback);
    }

    private void getProviceList() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if (jsonArray != null) {
                        length = jsonArray.length();
                    }
                    AreaEntity area = null;
                    for (int i = 0; i < length; i++) {
                        area = new AreaEntity();
                        provices.add(area);

                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if (name.contains("\"")) {
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(provices, proviceListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAreaList(null, callback);
    }

    private DialogInterface.OnClickListener proviceListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity provice = provices.get(which);
            AreaEntity oldProvice = (AreaEntity) tvProvice.getTag();
            if (oldProvice == null || !oldProvice.equals(provice)) {
                tvProvice.setText(provice.getName());
                tvProvice.setTag(provice);
                tvCity.setText("");
                tvCity.setTag(null);
                tvBank.setText("");
                tvBank.setTag(null);
            }
        }
    };
    private DialogInterface.OnClickListener cityListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity provice = (AreaEntity) tvProvice.getTag();
            List<AreaEntity> codeCities = cities.get(provice.getCode());
            AreaEntity city = codeCities.get(which);
            AreaEntity oldCity = (AreaEntity) tvCity.getTag();
            if (oldCity == null || !city.equals(oldCity)) {//未选中城市或者修改了所在城市
                //设置所在城市
                tvCity.setText(city.getName());
                tvCity.setTag(city);
                //初始化银行名称
                tvBank.setText("");
                tvBank.setTag(null);
            }
        }
    };
    private DialogInterface.OnClickListener bankListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity city = (AreaEntity) tvCity.getTag();
            List<AreaEntity> codeBanks = banks.get(city.getCode());
            AreaEntity bank = codeBanks.get(which);
            tvBank.setText(bank.getName());
            tvBank.setTag(bank);
        }
    };

    private void showListDialog(List<AreaEntity> infos, DialogInterface.OnClickListener listener) {
        int size = infos.size();
        if (size == 0) {
            ToastUtil.toast(context, "没有获取到信息");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[size];
        for (int i = 0; i < size; i++) {
            items[i] = infos.get(i).getName();
        }
        builder.setItems(items, listener);
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isInitNumberKeyboard()) {
            //用户按下了物理返回键，如果自定义键盘正在显示，则它隐藏并忽略返回操作。
            if (isShowNumberKeyboard()) {

                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 单按钮提示
     *
     * @param msg 提示内容
     */
    private void tipDialog(String msg) {
        ToastUtil.toast(context, msg);
//        DialogCreator.createOneConfirmButtonDialog(
//                context, "确定", msg, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        arg0.dismiss();
//
//                    }
//                }).show();
    }
}

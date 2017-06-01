package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.activity.wallet.request.QuickCardBinRequest;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.SpaceTextWatcher;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 实名认证
 * Created by fengx on 2015/11/30.
 *
 */
public class WalletNameAuthActivity extends BasePwdAndNumberKeyboardActivity{
    private EditText walletTransferName;
    private EditText idCardNo;
    private TextView btnAdd;
    private LabelEditText letBankCardNum;
    private View viewGroup;
    private String accountNum;
    private String bankName;
    private String customType;//新老用户用户、已未开通成功标志
    private String bankListbankName;
    private String banListAccountType;//输入卡号的银行支持的类型
    private String accountType;//输入卡的类型
    private String tvSupportBank;//输入卡号类型
    private String tvBankname;//输入卡号
    private QuickCardBinBean quickCardBinBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_auth);
        initUI();
        assignViews();
    }
    private void assignViews() {
        walletTransferName = (EditText) findViewById(R.id.wallet_transfer_name);
        idCardNo = (EditText) findViewById(R.id.idCardNo);
        btnAdd = (TextView) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        letBankCardNum=(LabelEditText)findViewById(R.id.let_bank_card_num);
        letBankCardNum.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        letBankCardNum.getEditText().setLongClickable(false);
        letBankCardNum.setOnFocusChangeListener(null);
        viewGroup=(View)findViewById(R.id.view_group);
        //键盘
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(letBankCardNum.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);

        idCardNo.addTextChangedListener(new SpaceTextWatcher());
        findViewById(R.id.support_bank_list).setOnClickListener(this);

        MerchantInfo info= ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (info.getMerchantStatus()==MerchantStatus.COMPLETED){
            walletTransferName.setText(info.getAccountName());
            idCardNo.setText(info.getUser().getIdCardInfo().getIdCardId());
        }
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("实名认证");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                }
            }
        });
        quickCardBinBean=new QuickCardBinBean();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.support_bank_list:
                BankChooseActivity.openForResult(context, BankBusid.TRANSFER, BankInfoType.DEFAULT, 40080);
                break;
            case R.id.btn_add:
                if (check()){
                   nameAuth();
                    Intent intent =new Intent(WalletNameAuthActivity.this,WalletProcessAuthActivity.class);
                    quickCardBinBean.setCustomerName(walletTransferName.getText().toString());
                    quickCardBinBean.setIdentifier(idCardNo.getText().toString());
                    intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN, quickCardBinBean);
                    intent.putExtra("cardType",tvSupportBank);
                    startActivity(intent);
                }
                break;
        }
    }

    private void nameAuth() {
        context.showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.REAL_NAME_AHTU);
        HttpRequestParams params = request.getRequestParams();
        params.put("realName", walletTransferName.getText().toString().trim());
        params.put("accountNo", accountNum);
        params.put("idCardNo",idCardNo.getText().toString().trim() );
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
            }
        });
        request.execute();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if(!hasFocus){
            if(toCheckCardBin(letBankCardNum.getEditText().getText().toString().toString())){
                qryCarBin();
            }else{
                ToastUtil.toast(context,"银行卡号输入不正确");
            }

        }
        if (hasFocus) {
            String newText = letBankCardNum.getEditText().getText().toString().replace(" ", "");
            letBankCardNum.getEditText().setText(newText);
            letBankCardNum.getEditText().setSelection(newText.length());
        } else {
            String text = CardUtil.formatCardNumberWithSpace(letBankCardNum.getEditText().getText().toString());
            letBankCardNum.getEditText().setText(text);
        }
    }

    /**
     * 查询卡bin
     */
    public void qryCarBin(){
        showProgressWithNoMsg();
        QuickCardBinRequest params=new QuickCardBinRequest(context);
        accountNum=letBankCardNum.getEditText().getText().toString().trim();
        params.setAccountNo(accountNum);
        BusinessRequest request=RequestFactory.getRequest(this, RequestFactory.Type.QRY_QUICK_RELEASE_CARD_BIN);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        bankName = jsonObject.getString("bankName");
                        quickCardBinBean.setBankName(bankName);
                        if(!TextUtils.isEmpty(jsonObject.getString("identifier")) && !jsonObject.getString("identifier").equals("null")){
                            //老用户 已绑定快捷卡 商户状态无所谓审核通过
                            customType="has identifier";
                        }else{
                            MerchantInfo userMerchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
                            if(userMerchantInfo.getMerchantStatus()== MerchantStatus.COMPLETED){
                                //老用户 未绑定快捷卡 商户状态是审核通过
                                customType="merchantStatus completed";
                            }else{
                                //新用户，未绑定快捷卡，商户状态不是审核通过
                                customType="new custom";
                            }
                        }
                        accountType = jsonObject.getString("accountType");
                        quickCardBinBean.setBankCode(jsonObject.getString("bankCode"));
                        quickCardBinBean.setSupportSMS("1");
                        quickCardBinBean.setAccountNo(jsonObject.getString("accountNo"));
                        quickCardBinBean.setAccountType(accountType);
                        quickCardBinBean.setCustomerName(jsonObject.getString("customerName"));
                        quickCardBinBean.setIdentifierType(jsonObject.getString("identifierType"));
                        quickCardBinBean.setIdentifier(jsonObject.getString("identifier"));
                        quickCardBinBean.setAuthFlag(jsonObject.getString("authFlag"));
                        qryIsBankNumSupport();
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
        WalletServiceManager.getInstance().start(params,request);
    }
    /**
     * 判断卡是否支持签约类型
     */
    public void qryIsBankNumSupport(){
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.SUPPORTED_BUSINESS_BANK_LIST);
        SupportedBankListRequest params=new SupportedBankListRequest(context);
        params.setBusId("19J");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                   String busiBanksString = resultServices.retData.toString();
                    try {
                        JSONObject json=new JSONObject(resultServices.retData);
                        JSONArray jsonArray = json.getJSONArray("List");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bankListbankName=jsonObject.optString("bankName");
                            if(bankName.equals(bankListbankName)){
                                banListAccountType=jsonObject.optString("accountType");
                                if(banListAccountType.equals("0")){
                                    tvSupportBank=bankName;
                                    tvBankname=bankListbankName;
                                  //  letBankCardNum.getEditText().setText(bankListbankName);
                                    btnAdd.setEnabled(true);
                                }else{
                                    if(accountType.equals(banListAccountType)){
                                        if(banListAccountType=="1"){
                                            tvSupportBank="*仅支持储蓄卡";
                                        }else{
                                            tvSupportBank="*仅支持信用卡";
                                        }
                                        tvBankname=bankListbankName;
                                     //   letBankCardNum.getEditText().setText(tvBankname);
                                        btnAdd.setEnabled(true);
                                    }else {
                                        ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                       btnAdd.setEnabled(false);
                                    }
                                }
                                break;
                            }else{
                                if(i==jsonArray.length()-1){
                                    ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                   btnAdd.setEnabled(false);
                                }

                            }
                        }
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
        WalletServiceManager.getInstance().start(params,businessRequest);
    }
    //输入卡号是否规范
    private boolean toCheckCardBin(String text){
        if (text.length() >= 14 && text.length() <= 19) {
            return true;
        }
        return false;
    }
    private boolean check(){
        String idNo = idCardNo.getText().toString();
        if (TextUtils.isEmpty(walletTransferName.getText().toString())){
            toast("姓名不能为空");
            return false;
        } else if (TextUtils.isEmpty(idCardNo.getText().toString())){
            toast("身份证号不能为空");
            return false;
        }else   if(idNo.length() == 0 ||!Util.checkIdCard(idNo)){
            ToastUtil.toast(context,R.string.id_no_input_error);
            return false;
        } else if (TextUtils.isEmpty(letBankCardNum.getEditText().getText().toString().trim())){
            toast("卡号不能为空");
            return false;
        }else {
            return true;
        }
    }
}

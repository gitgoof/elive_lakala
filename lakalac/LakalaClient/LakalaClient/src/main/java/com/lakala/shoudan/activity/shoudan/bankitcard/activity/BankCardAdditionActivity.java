package com.lakala.shoudan.activity.shoudan.bankitcard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
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
import com.lakala.shoudan.activity.wallet.WalletSupportedBankListActivity;
import com.lakala.shoudan.activity.wallet.request.QuickCardBinRequest;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.ui.component.LabelEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加银行卡页面
 * Created by HJP on 2015/11/20.
 */
public class BankCardAdditionActivity extends BasePwdAndNumberKeyboardActivity{
    private static final int REQUEST_BANKS = 0x2315;
    private LabelEditText letBankCardNum;
    private TextView tvBankname;
    private TextView tvNextStep;
    private TextView tvSupportBank;
    private TextView tvShowSupportBank;
    private View viewGroup;
    private String banListAccountType;//输入卡号的银行支持的类型
    private String accountType;//输入卡的类型
    private String bankListbankName;
    private String bankName;
    private String customType;//新老用户用户、已未开通成功标志


    private String busiBanksString;

    private QuickCardBinBean quickCardBinBean;
    private String accountNum;//用户输入的卡号



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_addition);
        init();
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.add_bank_card);
    }
    public void init(){
        initUI();
        quickCardBinBean=new QuickCardBinBean();
        letBankCardNum=(LabelEditText)findViewById(R.id.let_bank_card_num);
        tvNextStep=(TextView)findViewById(R.id.tv_next_step);
        tvBankname=(TextView) findViewById(R.id.tv_bankname);
        tvSupportBank=(TextView)findViewById(R.id.tv_support_bank);
        tvShowSupportBank=(TextView)findViewById(R.id.support_bank_list);
        viewGroup=(View)findViewById(R.id.view_group);
        tvNextStep.setOnClickListener(this);
        tvShowSupportBank.setOnClickListener(this);
        letBankCardNum.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        letBankCardNum.getEditText().setLongClickable(false);
        letBankCardNum.setOnFocusChangeListener(null);
        //键盘
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(letBankCardNum.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId()){
            //查看支持的银行卡列表
            case R.id.support_bank_list:
                qryBusinessBankList();
                break;
            case R.id.tv_next_step:
                nextStep();
                break;
        }
    }

    /**
     *查询支持业务的银行列表
     */
    public void qryBusinessBankList(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.SUPPORTED_BUSINESS_BANK_LIST);
        SupportedBankListRequest params=new SupportedBankListRequest(context);
        params.setBusId("19J");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    busiBanksString = resultServices.retData.toString();
                    WalletSupportedBankListActivity.open(context, busiBanksString, REQUEST_BANKS);
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
        WalletServiceManager.getInstance().start(params, businessRequest);
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
                    busiBanksString = resultServices.retData.toString();
                    try {
                        JSONObject json=new JSONObject(resultServices.retData);
                        JSONArray jsonArray = json.getJSONArray("List");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bankListbankName=jsonObject.optString("bankName");
                            if(bankName.equals(bankListbankName)){
                                banListAccountType=jsonObject.optString("accountType");
                                if(banListAccountType.equals("0")){
                                    tvSupportBank.setText(bankName);
                                    tvBankname.setText("");
                                    tvNextStep.setEnabled(true);
                                }else{
                                    if(accountType.equals(banListAccountType)){
                                        if(banListAccountType=="1"){
                                            tvSupportBank.setText("*仅支持储蓄卡");
                                        }else{
                                            tvSupportBank.setText("*仅支持信用卡");
                                        }
                                        tvBankname.setText(bankListbankName);
                                        tvNextStep.setEnabled(true);
                                    }else {
                                        ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                        tvNextStep.setEnabled(false);
                                    }
                                }
                                break;
                            }else{
                                if(i==jsonArray.length()-1){
                                    ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                    tvNextStep.setEnabled(false);
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


                        quickCardBinBean.setBankCode(jsonObject.getString("bankCode"));
                        quickCardBinBean.setSupportSMS("1");
                        quickCardBinBean.setAccountNo(jsonObject.getString("accountNo"));
                        accountType = jsonObject.getString("accountType");
                        quickCardBinBean.setAccountType(accountType);
                        quickCardBinBean.setCustomerName(jsonObject.getString("customerName"));
                        quickCardBinBean.setIdentifierType(jsonObject.getString("identifierType"));
                        quickCardBinBean.setIdentifier(jsonObject.getString("identifier"));
                        quickCardBinBean.setAuthFlag(jsonObject.getString("authFlag"));
                        tvBankname.setText("");
                        tvSupportBank.setText("");
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
    //下一步
    private void nextStep(){
        Intent intent=new Intent(BankCardAdditionActivity.this,BankCardInfoActivity.class);
        intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN, quickCardBinBean);
        intent.putExtra(Constants.IntentKey.CUSTOM_TYPE,customType);
        startActivityForResult(intent, ConstKey.REQUEST_ADD);
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
    //输入卡号是否规范
    private boolean toCheckCardBin(String text){
        if (text.length() >= 14 && text.length() <= 19) {
            return true;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ConstKey.REQUEST_ADD && resultCode == ConstKey.RESULT_ADD_BACK){
            setResult(ConstKey.RESULT_ADD_BACK,data);
            finish();
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQUEST_BANKS && resultCode == RESULT_OK){
//            String bank = data.getStringExtra("data");
//            try {
//                JSONObject jsonObject = new JSONObject(bank);
//                tvBankname.setTag(jsonObject);
//                //开户行
//                tvBankname.setText(jsonObject.optString("bankName"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

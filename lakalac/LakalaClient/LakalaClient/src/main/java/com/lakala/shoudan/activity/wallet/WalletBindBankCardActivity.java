package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.BankInfo;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.communityservice.transferremittance.CommonBankListActivity;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.bean.AccountInfo;
import com.lakala.shoudan.activity.wallet.request.TransferCardBinRequest;
import com.lakala.shoudan.activity.wallet.request.WalletCardInfoRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengx on 2015/11/30.
 */
public class WalletBindBankCardActivity extends BasePwdAndNumberKeyboardActivity {


    private EditText walletTransferCard;
    private TextView walletTransferBank;
    private EditText walletTransferName;
    private TextView btnAdd;
    private String cardNumber = "";
    private String bankName;
    private String customerName = "";
    private View viewGroup;
    private WalletTransferActivity.Type type;
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_card);
        type = (WalletTransferActivity.Type) getIntent().getSerializableExtra(Constants.IntentKey.ADD_CARD_TYPE);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("零钱转出");
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

        assignViews();
        addTextWatch();

        btnAdd.setOnClickListener(this);
        walletTransferCard.setOnFocusChangeListener(null);

        viewGroup = findViewById(R.id.view_group);

        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(walletTransferCard, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);

        initType();

        findViewById(R.id.support_bank_list).setOnClickListener(this);
    }

    private void initType() {
        if (type == WalletTransferActivity.Type.UNBIND_CARD) {
            //如果已经开通商户从商户中带出卡信息
            MerchantInfo merchantInfo = ApplicationEx.getInstance().getSession().getUser().getMerchantInfo();
            walletTransferName.setText(merchantInfo.getAccountName());
            walletTransferCard.setText(merchantInfo.getAccountNo());
            walletTransferBank.setText(merchantInfo.getBankName());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        int id = v.getId();
        if (id == R.id.wallet_transfer_card) {
            if (!hasFocus) {
                int length = cardNumber.length();
                if (length >= 14 && length <= 19) {
                    toCheckCardBin(cardNumber);
                }
            }
        }
    }

    private void assignViews() {
        walletTransferCard = (EditText) findViewById(R.id.wallet_transfer_card);
        walletTransferBank = (TextView) findViewById(R.id.wallet_transfer_bank);
        walletTransferName = (EditText) findViewById(R.id.wallet_transfer_name);
        btnAdd = (TextView) findViewById(R.id.btn_add);
        walletTransferName.setOnFocusChangeListener(hintListener);
    }

    private void addTextWatch() {

        walletTransferCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cardNumber = StringUtil.trim(editable.toString());
                if (cardNumber.equals("")) {
                    walletTransferBank.setText("");
                }
            }
        });


    }

    private void toCheckCardBin(String cardNumber) {

        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TRANSFER_CARD_BIN);
        TransferCardBinRequest params = new TransferCardBinRequest(this);
        params.setPayeeAcNo(cardNumber);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        bankName = jsonObject.optString("bankName");
                        walletTransferBank.setText(bankName);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }

    private void getCardInfo(String cardNumber, final String name) {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TO_BANK_CARD_INFO);
        WalletCardInfoRequest params = new WalletCardInfoRequest(this);
        params.setPayeeAcNo(cardNumber);
        params.setPayeeName(name);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {

                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        AccountInfo accountInfo = WalletServiceManager.getInstance().getAccountInfo();
                        AccountInfo.ListEntity entity = new AccountInfo.ListEntity();
                        entity.setPayeeAcNo(jsonObject.optString("payeeAcNo"));
                        entity.setPayeeName(walletTransferName.getText().toString());
                        entity.setPayeeCoreBankId(jsonObject.optString("bankCode"));
                        entity.setCardTypeName(jsonObject.optString("cardTypeName"));
                        entity.setPayeeBankName(jsonObject.optString("bankName"));
                        entity.setLogoName(jsonObject.optString("logoName"));

                        List<AccountInfo.ListEntity.ArrivalTypeListEntity> arrivalTypeListEntityList = new ArrayList<AccountInfo.ListEntity.ArrivalTypeListEntity>();
                        JSONArray jsonArray = jsonObject.getJSONArray("arrivalTypeList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            AccountInfo.ListEntity.ArrivalTypeListEntity arrivalTypeListEntity = new AccountInfo.ListEntity.ArrivalTypeListEntity();
                            arrivalTypeListEntity.setArrivalType(json.optString("ArrivalType"));
                            arrivalTypeListEntity.setArrivalTypeName(json.optString("ArrivalTypeName"));
                            arrivalTypeListEntityList.add(arrivalTypeListEntity);
                        }
                        entity.setArrivalTypeList(arrivalTypeListEntityList);
                        accountInfo.getList().add(entity);

                        //更新收款人信息
                        WalletServiceManager.getInstance().setAccountInfo(accountInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, request);

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.support_bank_list:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Support_BankCard, context);
                Intent intent = CommonBankListActivity.getIntent(context, "", BankBusid.WALLET_TRANSFER.getValue());
                intent.putExtra(Constants.IntentKey.WALLET_TRANSFER_BANK, true);
                startActivityForResult(intent, WalletTransferActivity.GET_BANK);
                break;
            case R.id.btn_add:
                if (check()) {
                    getCardInfo(walletTransferCard.getText().toString(), walletTransferName.getText().toString());
                }
                break;
        }
    }

    private boolean check() {
        if (TextUtils.isEmpty(walletTransferCard.getText().toString())) {
            toast("收款储蓄卡号不能为空");
            return false;
        } else if (TextUtils.isEmpty(walletTransferBank.getText().toString())) {
            toast("开户银行不能为空");
            return false;
        } else if (TextUtils.isEmpty(walletTransferName.getText().toString())) {
            toast("持卡人姓名不能为空");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WalletTransferActivity.GET_BANK && resultCode == UniqueKey.BANK_LIST_CODE) {
            BankInfo bankInfo = (BankInfo) data.getSerializableExtra("bankInfo");
            walletTransferBank.setText(bankInfo.getBankName());
            if (customerName != null) {
                if (!bankInfo.getBankName().equals(customerName)) {
                    toast(getResources().getString(R.string.not_support_bank));
                }
            }
        }
    }
}

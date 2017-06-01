package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.BaseFinanceActivity;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.PaymentTypesEntity;
import com.lakala.shoudan.activity.shoudan.finance.bean.WithdrawTransInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPayWithdrawRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.GenerateBillIdRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryWalletNoRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.SingleChoiseListDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fengx on 2015/10/16.
 * <p>
 * <p>
 * 取出
 */
public class WithdrawalActivity extends BaseFinanceActivity implements View.OnClickListener {

    private static final int INPUT_PWD_REQUEST = 0x1231;
    private TextView withdrawal;
    private TextView card;
    private TextView tvType;
    private TextView type1, type2;
    private EditText amount;
    private GenerateBillIdRequest generateBillIdRequest = new GenerateBillIdRequest();
    private String billId;
    private double balance;
    private String cardNo;
    private String productId;
    private String contractId;
    private int productType;
    private String pan;
    private WithdrawTransInfo withdrawTransInfo;
    private String prodName;
    private String arrType;
    private String[] arrivalType;
    private JSONArray arrivalTypeList;
    private String bankName, fourCardNo;
    private JSONObject responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        findViewById(R.id.viewgroup).requestFocus();
        productId = getIntent().getStringExtra("productId");
        prodName = getIntent().getStringExtra("prodName");
        contractId = getIntent().getStringExtra("contractId");
        productType = getIntent().getIntExtra("productType", -1);
        withdrawTransInfo = new WithdrawTransInfo();
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("取出");
        navigationBar.setActionBtnText("规则");
        navigationBar.setOnNavBarClickListener(
                new NavigationBar.OnNavBarClickListener() {
                    @Override
                    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {

                        if (navBarItem == NavigationBar.NavigationBarItem.back) {
                            finish();
                        } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                            if (productId.equals("KL1H00001")) {//理财1号
                                ProtocalActivity.open(context, ProtocalType.LC_LC1NOTE);
                            } else if (productId.equals("000686")) {//理财2号
                                ProtocalActivity.open(context, ProtocalType.RULES);
                            } else if (productId.equals("KL1H00002")) {//理财3号
                                ProtocalActivity.open(context, ProtocalType.LC_LC3NOTE);
                            }
                        }
                    }
                }
        );

        withdrawal = (TextView) findViewById(R.id.go_to_withdrawal);
        card = (TextView) findViewById(R.id.withdrawal_card);
        amount = (EditText) findViewById(R.id.withdrawal_amount);
        amount.setOnFocusChangeListener(new HintFocusChangeListener());
        tvType = (TextView) findViewById(R.id.withdrawal_arrival_type);
        registerEditListener();

        queryCardMsg();
        tvType.setOnClickListener(this);
        withdrawal.setOnClickListener(this);
    }

    private void registerEditListener() {


        amount.addTextChangedListener(
                new TextWatcher() {

                    double amount;
                    int etStart, etEnd;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        etStart = s.length();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            amount = Double.valueOf(s.toString());
                        }
                        etEnd = s.length();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (etEnd > etStart) {
//                            return;
                        } else {
                            if ((amount > balance)) {
                                s.delete(etStart, etEnd);
                            }

                        }
                    }
                }
        );
    }

    /**
     * 1、查询签约卡
     */
    public void queryCardMsg() {

        String cardMsg = getIntent().getStringExtra("cardMsg");
        try {
            responseData = new JSONObject(cardMsg);

            JSONArray list = responseData.getJSONArray("List");
            arrivalTypeList = list.getJSONObject(0).getJSONArray("ArrivalTypeList");
            arrivalType = new String[arrivalTypeList.length()];
            for (int i = 0; i < arrivalTypeList.length(); i++) {
                arrivalType[i] = arrivalTypeList.getJSONObject(i).optString("ArrivalTypeName");
            }

            arrType = arrivalType[0];
            cardNo = list.getJSONObject(0).optString("PayeeAcNo");
            bankName = list.getJSONObject(0).optString("PayeeBankName");
            fourCardNo = list.getJSONObject(0).optString("CardTailFour");

            generateBillIdRequest.setArrivalType(arrivalTypeList.getJSONObject(0).optString("ArrivalType"));
            generateBillIdRequest.setPayeeName(list.getJSONObject(0).optString("PayeeName"));
            generateBillIdRequest.setBankCode(list.getJSONObject(0).optString("PayeeCoreBankId"));
            generateBillIdRequest.setBankName(bankName);
            generateBillIdRequest.setPayeeAcNo(cardNo);

            balance = responseData.optDouble("Balance");
            amount.setHint("当前余额" + balance + "元");
            card.setText(Util.formatFinanceAccout(fourCardNo, bankName));
            tvType.setText(arrType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 2、生成订单
     */
    public void generateBillId() {

        generateBillIdRequest.setAmount(amount.getText().toString());
        generateBillIdRequest.setAccountType(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountType().getValue());
        FinanceRequestManager.getInstance().generateBillId(generateBillIdRequest, new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    billId = responseData.optString("BillId");
                    if (billId != null) {
                        queryWalletNo();
                    }
                } else {
                    hideProgressDialog();
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toastInternetError();

            }
        });
    }

    /**
     * 3、查询钱包账号
     */
    public void queryWalletNo() {
        QueryWalletNoRequest request = new QueryWalletNoRequest();
        FinanceRequestManager.getInstance().queryWalletNo(
                request, new HttpResponseListener() {
                    @Override
                    public void onStart() {
                        showProgressWithNoMsg();
                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                        hideProgressDialog();
                        if (returnHeader.isSuccess()) {
                            pan = responseData.optString("WalletNo");
                            Intent intent = new Intent(context, InputPayPwdDialogActivity.class);
                            startActivityForResult(intent, INPUT_PWD_REQUEST);
                        } else {
                            toast(returnHeader.getErrMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        hideProgressDialog();

                        toastInternetError();
                    }
                }
        );
    }

    /**
     * 赎回
     *
     * @param pwd
     */
    public void doPay(String pwd) {

        final DoPayWithdrawRequest request = new DoPayWithdrawRequest();
        request.setBillId(billId);
        request.setLastPaymentType("F");
        request.setProductId(productId);
        request.setPan(pan);
        request.setInpan(cardNo);
        request.setAmount(amount.getText().toString());
        request.setTrsPassword(pwd);
        request.setBusid(DoPayWithdrawRequest.BusIdType.赎回);
        if (productType == 1) {
            if (contractId != null) {
                request.setContractId(contractId);
            }
        }
        List<PaymentTypesEntity> lst = new ArrayList<PaymentTypesEntity>();
        PaymentTypesEntity entity = new PaymentTypesEntity();
        entity.setPaymentType("F");
        entity.setPaymentAmount(amount.getText().toString());
        lst.add(entity);

        request.setPaymentTypes(lst);
        withdrawTransInfo.setAmount(Util.formatTwo(Double.valueOf(amount.getText().toString())));
        withdrawTransInfo.setProd(prodName);
        FinanceRequestManager.getInstance().doPay(request, new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (!FinanceRequestManager.isReturnValid(context, returnHeader)) {
                    return;
                }
                if (FinanceRequestManager.TRAN_TIMEOUT.equals(returnHeader.getRetCode())) {
                    startTranTimeout(returnHeader.getErrMsg(), withdrawTransInfo);
                    return;
                }
                if (returnHeader.isSuccess()) {
                    withdrawTransInfo.setSid(responseData.optString("JnlId"));
                    withdrawTransInfo.setTransResult(TransResult.SUCCESS);
                } else {
                    withdrawTransInfo.setTransResult(TransResult.FAILED);
                    withdrawTransInfo.setMsg(returnHeader.getErrMsg());
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = simpleDateFormat.format(new Date());
                withdrawTransInfo.setTime(date);
                Intent intent = new Intent(WithdrawalActivity.this, ConfirmResultActivity.class);
                intent.putExtra(Constants.IntentKey.TRANS_INFO, withdrawTransInfo);
                startActivity(intent);
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                startTranTimeout(getString(R.string.socket_error), withdrawTransInfo);

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.go_to_withdrawal:
                if (check(amount.getText().toString())) {
                    generateBillId();
                }
                break;
            case R.id.withdrawal_arrival_type:
                showTypeListDialog();
                break;
        }

    }

    private void showTypeListDialog() {
        if (arrivalType != null) {
            if (arrivalType.length < 2) {
                return;
            }
        }
        final SingleChoiseListDialog builder = new SingleChoiseListDialog();
        builder.setButtons(new String[]{"确定"});
        builder.setTitle("到账方式");
        List<String> items = new ArrayList<>();
        items.addAll(Arrays.asList(arrivalType));
        builder.setItems(items);
        builder.setCancelable(true);
        builder.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                int position = builder.getSelectedPosition();
                arrType = arrivalType[position];
                tvType.setText(arrType);
                try {
                    generateBillIdRequest.setArrivalType(
                            arrivalTypeList.getJSONObject(position)
                                    .optString("ArrivalType")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show(context.getSupportFragmentManager());
            }
        });

    }


    public boolean check(String amount) {

        if (TextUtils.isEmpty(amount)) {
            toast("请输入金额");
            return false;
        } else if (Double.valueOf(amount) > balance) {
            toast("您输入的金额超限，请重新输入");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_PWD_REQUEST && resultCode == RESULT_OK) {
            String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
            doPay(pwd);
        }
    }
}

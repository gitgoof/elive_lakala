package com.lakala.shoudan.activity.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.DateUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
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
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.activity.password.SetSecurityQuestionActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.wallet.bean.AccountInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletPaymentTypes;
import com.lakala.shoudan.activity.wallet.bean.WithdrawTransInfo;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.activity.wallet.request.WithdrawToBillRequest;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.ContactBookUtil;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.BaseDialog;
import com.lakala.ui.dialog.SingleChoiseListDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by fengx on 2015/11/19.
 */
public class WalletTransferActivity extends AppBaseActivity implements View.OnClickListener {

    private EditText walletTransferAmount;
    private TextView walletBindBankCard;
    private TextView walletArrivalTime;
    private TextView balance;
    private EditText walletArrivalPhone;
    private TextView confirmTransfer;
    private ImageView phoneIcon;
    private String[] arrivalTypeNames;
    private String[] arrivalTypes;
    private int selectedType;
    private AccountInfo accountInfo;        //收款人信息
    private WalletInfo walletInfo;  //钱包信息
    private String payeeAcNo;   //收款卡号
    private String payeeName;   //收款人姓名
    private String bankCode;
    private String bankName;
    private String amount;
    private WithdrawTransInfo transInfo = new WithdrawTransInfo();
    public static Type type;  //默认为未开通商户
    private List<AccountInfo.ListEntity> accountList;
    public static boolean isSetPwd = false;
    public static boolean isSetQuestion = false;
    private BaseDialog dialog;
    private String billid;
    public static final int BIND_CARD_REQUEST = 100;      //已绑卡到银行卡列表界面
    public static final int UNBIND_CARD_REQUEST = 101;    //未绑卡到添加卡界面根据是否开通选择是否带出商户信息
    public static final int ADD_BANK_REQUEST = 102;       //银行卡界面到添加卡界面
    public static final int GET_BANK = 103;
    private static final int INPUT_PWD = 104;
//    private boolean isAdJumpTo=false;

    public enum Type {
        BIND_CARD,      //已绑卡
        UNBIND_CARD,    //已开通但未绑卡
        UNOPEN          //未开通
    }

    private String hint1, hint2;

    private void assignViews() {
        walletTransferAmount = (EditText) findViewById(R.id.wallet_transfer_amount);
        walletBindBankCard = (TextView) findViewById(R.id.wallet_bind_bank_card);
        walletArrivalTime = (TextView) findViewById(R.id.wallet_arrival_time);
        walletArrivalPhone = (EditText) findViewById(R.id.wallet_arrival_phone);
        confirmTransfer = (TextView) findViewById(R.id.confirm_transfer);
        balance = (TextView) findViewById(R.id.transfer_balance_amount);
        phoneIcon = (ImageView) findViewById(R.id.wallet_phone_icon);
        walletTransferAmount.setOnFocusChangeListener(new HintFocusChangeListener());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_add_bank);
        walletInfo = (WalletInfo) getIntent().getSerializableExtra(Constants.IntentKey.WALLET_INFO);
        isSetPwd = walletInfo.getTrsPasswordFlag() == 1 ? true : false;
        isSetQuestion = walletInfo.getQuestionFlag() == 1 ? true : false;
//        String walletWithdraw=getIntent().getStringExtra(Constants.IntentKey.Wallet_Withdraw);
//        if(TextUtils.isEmpty(walletWithdraw)){
//            isAdJumpTo=true;
//        }
        assignViews();
        jsonToAccountInfo();
        hint1 = getResources().getString(R.string.transfer_str1);
        hint2 = getResources().getString(R.string.transfer_str2);
        initUI();
    }

    //初始化收款人信息
    private void jsonToAccountInfo() {
        String data = getIntent().getStringExtra(Constants.IntentKey.WALLET_ACCOUNT_INFO);
        accountInfo = new AccountInfo();
        try {
            JSONObject jsonObject = new JSONObject(data);
            accountInfo.parseObject(jsonObject);

            WalletServiceManager.getInstance().setAccountInfo(accountInfo);
            int size = accountInfo.getList().size();
            if (size > 0) {
                type = Type.BIND_CARD;
            } else if (ApplicationEx.getInstance().getUser().getMerchantInfo().getMerchantStatus() == MerchantStatus.COMPLETED) {
                type = Type.UNBIND_CARD;
                //  MerchantInfo info= ApplicationEx.getInstance().getUser().getMerchantInfo();
               /* String bankNo=info.getBankName()+"("+info.getAccountNo().substring(info.getAccountNo().length()-4,info.getAccountNo().length())+")";
                walletBindBankCard.setText(bankNo);*/
                //setText(info.getBankName(), info.getAccountNo());

            } else {
                type = Type.UNOPEN;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("零钱转出");


        walletBindBankCard.setOnClickListener(this);
        walletArrivalTime.setOnClickListener(this);
        confirmTransfer.setOnClickListener(this);
        phoneIcon.setOnClickListener(this);

        addTextWatch();

        if (accountInfo.getList().size() > 0) {
            refreshData(0);
        }
        String hint = StringUtil.getWalletDrawInfo(hint1, hint2, Double.valueOf(walletInfo.getWalletWithdrawBalance()));
        balance.setText(hint);
       /* if (type .equals(Type.UNBIND_CARD)){
            setInfo();
        }*/
    }

    private void setInfo() {
        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();
        AccountInfo.ListEntity entity = new AccountInfo.ListEntity();
        entity.setPayeeAcNo(info.getAccountNo());
        entity.setPayeeName(ApplicationEx.getInstance().getUser().getMtsCustomerName());
        entity.setPayeeCoreBankId(info.getBankNo());
        entity.setPayeeBankName(info.getBankName());
        accountInfo.getList().add(entity);

        Date d = new Date();
        int hour = d.getHours();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        d = calendar.getTime(); //这个时间就是日期往后推一天
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd号");
        String dateString = formatter.format(d);
        if (hour >= 8 && hour <= 22) {
            arrivalTypeNames = new String[2];
            arrivalTypeNames[0] = "2小时内到账";
            arrivalTypeNames[1] = dateString + "到账";
        } else {
            arrivalTypeNames = new String[1];
            arrivalTypeNames[0] = dateString + "到账";
        }

    }

    //根据收款人信息为ediitext赋值
    private void refreshData(int position) {

        String bank = accountInfo.getList().get(position).getPayeeBankName();
        String fourLast = accountInfo.getList().get(position).getCardTailFour();
        walletBindBankCard.setText(bank + "(" + fourLast + ")");
        walletBindBankCard.setTextColor(getResources().getColor(R.color.gray_333333));

        int size = accountInfo.getList().size();
        if (size > 0) {
            String bankName = accountInfo.getList().get(position).getPayeeBankName();
            String bankCard = accountInfo.getList().get(position).getPayeeAcNo();
            setText(bankName, bankCard);

            List<AccountInfo.ListEntity.ArrivalTypeListEntity> arrivalTypeListEntityList = accountInfo.getList().get(position).getArrivalTypeList();
            int arrSize = arrivalTypeListEntityList.size();
            arrivalTypes = new String[arrSize];
            arrivalTypeNames = new String[arrSize];
            for (int i = 0; i < arrSize; i++) {
                arrivalTypes[i] = arrivalTypeListEntityList.get(i).getArrivalType();
                arrivalTypeNames[i] = arrivalTypeListEntityList.get(i).getArrivalTypeName();
            }
            //如果只有一种到账方式，默认选中
            selectedType = 0;
            walletArrivalTime.setText(arrivalTypeNames[0]);
        }
        AccountInfo.ListEntity entity = accountInfo.getList().get(position);
        payeeAcNo = entity.getPayeeAcNo();
        payeeName = entity.getPayeeName();
        bankCode = entity.getPayeeCoreBankId();
        bankName = entity.getPayeeBankName();

    }

    public void checkWalletTerminal() {

        showProgressWithNoMsg();
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onError(String msg) {
                hideProgressDialog();
                toast(msg);
            }

            @Override
            public void onSuccess() {
                getBill();
            }
        });

    }

    /**
     * 零钱转出生成账单
     */
    private void getBill() {

        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TRANSFER_GENERATE_BILL);
        WithdrawToBillRequest params = new WithdrawToBillRequest(this);
        params.setPayeeAcNo(payeeAcNo);
        params.setPayeeName(payeeName);
        params.setAmount(walletTransferAmount.getText().toString());
        params.setBankCode(bankCode);
        params.setBankName(bankName);
        params.setBillno(payeeAcNo);
        params.setArrivalType(arrivalTypes[selectedType]);
        params.setBusid(WalletHomeActivity.WALLET_TRANSFER_TO_BILL);
        params.setPayeeMobile(walletArrivalPhone.getText().toString());

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {

                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        billid = jsonObject.optString("billId");
                        hideProgressDialog();
                        Intent intent = new Intent(WalletTransferActivity.this, InputPayPwdDialogActivity.class);
                        startActivityForResult(intent, INPUT_PWD);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    hideProgressDialog();
                    ToastUtil.toast(WalletTransferActivity.this, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgressDialog();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }


    /**
     * 提现
     */
    private void toDraw(String billId, String pwd) {

        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TRADE);

        WalletPaymentTypes types = new WalletPaymentTypes();
        types.setPaymentType("W");
        types.setRandom(Mac.getRnd());
        types.setPaymentAmount(Double.valueOf(amount));
        types.setPaymentTypes(types.getJsonArray());

        WalletTradeRequest params = new WalletTradeRequest(this);
        params.setLastPaymentType("W");
        params.setBusid(WalletHomeActivity.WALLET_TRANSFER);
        params.setBillId(billId);
        params.setBillno(payeeAcNo);    //收款人卡号
        params.setTrsPassword(pwd);
        params.setAmount(amount);
        params.setPaymentTypes(types.getPaymentTypes().toString());
        params.setTerminalId(ApplicationEx.getInstance().getSession().getUser().getTerminalId());
        //刷卡器线路号ApplicationEx.getInstance().getSession().getCurrentLineNo()
        params.setTelecode(TerminalKey.getTelecode());
        params.setPan(walletInfo.getWalletNo());
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String date = jsonObject.optString("jnlDate");
                        String billAmount = jsonObject.optString("billAmount");
                        String billFee = jsonObject.optString("billFee");
                        transInfo.setTransResult(TransResult.SUCCESS);
                        String amount = com.lakala.shoudan.common.util.StringUtil.formatTwo(Double.parseDouble(billAmount));
                        transInfo.setAmount(amount + "元");
                        transInfo.setDate(date);
                        transInfo.setFee(billFee);
                        transInfo.setCard(payeeAcNo);
//                        transInfo.setIsAdJumpTo(isAd);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    transInfo.setTransResult(TransResult.FAILED);
                    String patten = "yyyy-MM-dd hh:mm:ss";
                    String date = DateUtil.formatDate(new Date(), patten);
                    transInfo.setAmount(com.lakala.shoudan.common.util.StringUtil.formatTwo(Double.parseDouble(amount)) + "元");
                    transInfo.setDate(date);
                    transInfo.setFee("0.00");
                    transInfo.setCard(payeeAcNo);
                    transInfo.setMsg(resultServices.retMsg);
//                    transInfo.setIsAdJumpTo(isAd);
                }

                Intent intent = new Intent(WalletTransferActivity.this, ConfirmResultActivity.class);
//                intent.putExtra(Constants.IntentKey.Wallet_Withdraw, "walletWithdraw");
                intent.putExtra(ConstKey.TRANS_INFO, transInfo);
                startActivity(intent);
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
            case R.id.wallet_bind_bank_card:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Add_Bankcard, this);
                Intent intent = new Intent(WalletTransferActivity.this, WithdrawCardListActivity.class);
                intent.putExtra("fromLQ", "fromLQ");
                startActivityForResult(intent, BIND_CARD_REQUEST);
               /* if (type == Type.BIND_CARD){
                    startActivityForResult(intent,BIND_CARD_REQUEST);
                }else if (type == Type.UNBIND_CARD){
                    intent = new Intent(WalletTransferActivity.this,WalletBindBankCardActivity.class);
                    intent.putExtra(Constants.IntentKey.ADD_CARD_TYPE, Type.UNBIND_CARD);
                    startActivityForResult(intent, UNBIND_CARD_REQUEST);
                }else {
                    intent = new Intent(WalletTransferActivity.this,WalletBindBankCardActivity.class);
                    intent.putExtra(Constants.IntentKey.ADD_CARD_TYPE, Type.UNOPEN);
                    startActivityForResult(intent, UNBIND_CARD_REQUEST);
                }*/
                break;
            case R.id.wallet_arrival_time:
                if (walletBindBankCard.getText().toString().equals("请绑定银行卡")) {
                    toast("请先选择银行卡");
                } else {
                    showDialog();
                }
                // showDialog();
                break;
            case R.id.confirm_transfer:
                if (check()) {
                    amount = walletTransferAmount.getText().toString();
                    checkWalletTerminal();
                }
                break;
            case R.id.wallet_phone_icon:
                ContactBookUtil.onContactBookClick(WalletTransferActivity.this);
                break;

        }
    }

    private void showDialog() {
        if (arrivalTypeNames == null) {
            return;
        }

        final SingleChoiseListDialog builder = new SingleChoiseListDialog();
        builder.setButtons(new String[]{"确定"});
        builder.setTitle("请选择到账时间");
        List<String> items = new ArrayList<>();
        items.addAll(Arrays.asList(arrivalTypeNames));
        builder.setItems(items);
        builder.setCancelable(true);
        builder.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                selectedType = builder.getSelectedPosition();
                walletArrivalTime.setText(arrivalTypeNames[selectedType]);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show(context.getSupportFragmentManager());
            }
        });
    }

    private boolean check() {
        if (TextUtils.isEmpty(walletTransferAmount.getText().toString())) {
            toast("转出金额不能为空");
            return false;
        }
        if (TextUtils.isEmpty(walletBindBankCard.getText().toString())) {
            toast("请选择储蓄卡");
            return false;
        } else if (TextUtils.isEmpty(walletArrivalTime.getText().toString())) {
            toast("请选择到账时间");
            return false;
        } else if (isSetPwd == false) {
            showSetPwdDialog();
            return false;
        } else if (isSetQuestion == false) {
            Intent intent = new Intent(WalletTransferActivity.this, SetSecurityQuestionActivity.class);
            intent.putExtra("isFromWallet", true);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private void showSetPwdDialog() {
        dialog = DialogCreator.createFullContentDialog(this, "暂不设置", "去设置", "零钱转出需要设置支付密码，请您尽快设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(WalletTransferActivity.this, SetPaymentPasswordActivity.class);
                intent.putExtra("isWalletTransfer", true);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void setText(String bank, String card) {
        char[] chars = card.toCharArray();
        String fourLast = card.substring(chars.length - 4, chars.length);

        walletBindBankCard.setText(bank + "(" + fourLast + ")");
        walletBindBankCard.setTextColor(getResources().getColor(R.color.gray_333333));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //添加银行卡界面返回
        if (requestCode == UNBIND_CARD_REQUEST && resultCode == RESULT_OK) {

            accountInfo = WalletServiceManager.getInstance().getAccountInfo();
            refreshData(0);

        } else if (requestCode == INPUT_PWD && resultCode == RESULT_OK) {
            //输密码返回
            showProgressWithNoMsg();
            String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
            toDraw(billid, pwd);

        } else if (requestCode == BIND_CARD_REQUEST && resultCode == RESULT_OK) {
            //银行卡列表界面返回
            int position = data.getIntExtra("position", 0);
            //先清空到账时间
            walletArrivalPhone.setText("");
            refreshData(position);
        } else if (requestCode == UniqueKey.PHONE_NUMBER_REQUEST_CODE) {
            //通讯录中读取联系人的电话号码
            ContactBookUtil contactBookUtil = new ContactBookUtil(this, walletArrivalPhone, data);
            contactBookUtil.setOnPhoneNumberSelectedListener(new ContactBookUtil.OnPhoneNumberSelectedListener() {
                @Override
                public void onPhoneNumberSelected(String phoneNumber) {
                    String phoneNum = StringUtil.formatString(phoneNumber);

                    if (!PhoneNumberUtil.checkPhoneNumber(phoneNum)) {
                        ToastUtil.toast(context, "此联系人没有可供使用的手机号码，请重新选择", Toast.LENGTH_LONG);
                    } else {
                        walletArrivalPhone.setText(phoneNum);
                    }
                }

                @Override
                public void onNoNumber(String hint) {
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void addTextWatch() {
        walletTransferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        walletTransferAmount.setText(s);
                        walletTransferAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    walletTransferAmount.setText(s);
                    walletTransferAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        walletTransferAmount.setText(s.subSequence(0, 1));
                        walletTransferAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

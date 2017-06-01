package com.lakala.shoudan.activity.merchantmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.activity.shoudan.finance.adapter.AllProdAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.shoudan.activity.wallet.WalletSupportedBankListActivity;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.OpenBankInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/17 0017.
 */
public class UpdateAccountActivity extends BasePwdAndNumberKeyboardActivity {
    private static final int REQUEST_BANKS = 0x2315;
    private static final int reqCode = 160;
    private static final int typeCode = 161;
    /* @Bind(R.id.shop_distct)
     TextView shopDistct;
     @Bind(R.id.rl_distct)
     RelativeLayout rlDistct;
     @Bind(R.id.shop_place)
     TextView shopPlace;
     @Bind(R.id.rl_place)*/
    RelativeLayout rlPlace;
    @Bind(R.id.admin_type)
    TextView adminType;
    @Bind(R.id.rl_admin_type)
    LinearLayout rlAdminType;
    @Bind(R.id.bank_admin)
    EditText bankAdmin;
    @Bind(R.id.rl_bank_admin)
    LinearLayout rlBankAdmin;
    @Bind(R.id.bank_open)
    TextView bankOpen;
    @Bind(R.id.rl_bank_open)
    RelativeLayout rlBankOpen;
    @Bind(R.id.change_info_next)
    Button changeInfoNext;
    @Bind(R.id.user_name)
    TextView userName;
    private List<TransDetailProInfo> proList = new ArrayList<TransDetailProInfo>();
    private String[] pName = {"个人账户", "对公账户"};
    private AllProdAdapter adapter;
    private int Poposition;
    private  String type;
    private  String type1;
    private  String bankAccount;
    private  String tvSupportBank;
    private  String bankNo;
    private  String bankAdminBefore;
    private View viewGroup;
    private String bankName;
    private String bankListbankName;
    private String banListAccountType;
    private String accountType;//输入卡的类型
    private boolean isFist=true;
    private static final int REQUEST_OPENBANK_LIST = 99;
    private static final int REQUEST_COMPANY = 0x1234;
    private String opens = "-1";
    private boolean idUp=false;
    private String realName;
    private String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("收款账户变更");
        for (int i = 0; i < 2; i++) {
            TransDetailProInfo pro = new TransDetailProInfo();
            pro.setProName(pName[i]);
            proList.add(pro);
        }
        customerName = ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountName();
        realName = ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName();

//        String secondUpgradeStatus = getIntent().getStringExtra("secondUpgradeStatus");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("bankName")))
            bankOpen.setText(getIntent().getStringExtra("bankName"));
        type = getIntent().getStringExtra("accountType");
        type1=type;
        if (type.equals("0"))
            userName.setText(realName);
        else
            userName.setText(customerName);
       setType("");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("accountNo"))){
            bankAccount=getIntent().getStringExtra("accountNo");
            bankAdminBefore=bankAccount;
            String mobileNum=bankAdminBefore;
            mobileNum= mobileNum.substring(0,6)+"****"+mobileNum.substring(7,11);
           // bankAdmin.setText(mobileNum);
            MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
            if (merchantInfoData.getAccountType() == AccountType.PUBLIC) {// 企业
                bankAdmin.setText(bankAccount);
            } else {
                bankAdmin.setText(bankAccount);
            }
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("bankNo")))
            bankNo= getIntent().getStringExtra("bankNo");

        if (type.equals("0")){
            findViewById(R.id.right_arrow2).setVisibility(View.GONE);
            rlAdminType.setClickable(false);
        }

        viewGroup=(View)findViewById(R.id.parent);
        //键盘
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        //infoSub.setEnabled(false);
        initNumberKeyboard();
        initNumberEdit(bankAdmin, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
            String businessNameStr = bankAdmin.getText().toString().trim();
            if(!hasFocus){
                if(toCheckCardBin(businessNameStr.toString())){
                    idUp = true;
                    if (type1.equals("0")) {
                        checkCreditNum();
                    }else {
                        bankAccount=bankAdmin.getText().toString().trim();
                    }

                }else{
                    ToastUtil.toast(context,"银行卡号输入不正确");
                }

            }
            if (hasFocus) {
                idUp = false;
                if (isFist){
                    bankAdmin.setHint("请输入本人银行卡号");
                    bankAdmin.setText("");
                    bankOpen.setText("");
                    if (type1.equals("1")){
                        findViewById(R.id.right_arrow3).setVisibility(View.VISIBLE);
                        bankOpen.setHint("请选择开户银行");
                    }
                    else {
                        findViewById(R.id.right_arrow3).setVisibility(View.GONE);
                        bankOpen.setHint("");
                    }
                }else {
                    String newText = bankAdmin.getText().toString().replace(" ", "");
                    bankAdmin.setText(newText);
                    bankAdmin.setSelection(newText.length());
                }

            } else {
                String text = CardUtil.formatCardNumberWithSpace(bankAdmin.getText().toString());
                bankAdmin.setText(text);
        }
    }
    private void checkCreditNum() {
        showProgressWithNoMsg();
        bankAccount=bankAdmin.getText().toString().trim();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);

                    if (jsonObject != null) {
                        OpenBankInfo mOpenBankInfo = OpenBankInfo.construct(jsonObject);
                            bankName = mOpenBankInfo.bankname;
                            bankNo = mOpenBankInfo.bankCode;
                        getBankList();
                    }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgressDialog();
            }
        };
        CommonServiceManager.getInstance().getCardBIN(BankBusid.MPOS_ACCT, bankAccount, callback);
    }

    private void getBankList() {
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.BANK_LIST);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid",  BankBusid.MPOS_ACCT);
        params.put("infoType",BankInfoType.PRIVATE.name());
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONArray jsonArray=new JSONArray(resultServices.retData);
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bankListbankName=jsonObject.optString("bankName");

                            if(bankName.equals(bankListbankName)){
                                    tvSupportBank=bankName;
                                    bankOpen.setText(tvSupportBank);
                                break;
                            }else{
                                if(i==jsonArray.length()-1){
                                    bankOpen.setText("");
                                    ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                    // infoSub.setEnabled(false);
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showMessage(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        request.setResponseHandler(callback);
        request.execute();
    }
    //输入卡号是否规范
    private boolean toCheckCardBin(String text){
        if (text.length() >= 14 && text.length() <= 23) {
            return true;
        }
        return false;
    }
    private void updateAccount() {
        String acouStr=bankAdmin.getText().toString().trim();
        if (TextUtils.isEmpty(bankAdmin.getText().toString().trim())){
            toast("请输入本人银行卡号");
        } else   if (bankAdminBefore.equals(bankAccount)){
            createFullShueDialog("提交前请修改账号");
        } else if (!idUp){
            if (!toCheckCardBin(acouStr))
                toast("请输入正确的银行卡号");
        } else  if (TextUtils.isEmpty(bankOpen.getText().toString().trim())){
            toast("请选择开户银行");
        } else if (!type1.equals(type)){
            createFullShueDialog("银行账号和账户类型不匹配");
        } else {
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_MERCHAT_INGO);
        showProgressWithNoMsg();
        HttpRequestParams params = businessRequest.getRequestParams();
       // params.put("businessName",getIntent().getStringExtra("bankName") );
        params.put("accountType",type );
        params.put("bankNo",bankNo );
        params.put("bankName", tvSupportBank);
        params.put("accountNo", bankAccount.trim());
        params.put("optType","ACCOUNTNO" );

        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        showBaseDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });

        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }
    }

    private void  showBaseDialog(){
        DialogCreator.createFullShueDialog(
                context, "提示",   "确定", "您的收款账户变更正在审核中，请及时关注审核结果",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent =new Intent();
                        intent.putExtra("upDate","upDateAccount");
                        intent.putExtra("type",type);
                        intent.putExtra("upDateAccount",bankAccount.trim());
                        setResult(RESULT_OK, intent);
                        ShoudanStatisticManager.getInstance() .onEvent(ShoudanStatisticManager.Merchant_Change_Accout_Submit, context);
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }
        ).show();
    }

    private void  createFullShueDialog(String content){
        DialogCreator.createFullShueDialog(
                context, "提示",   "确定", content,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //  startActi(isUpgradeFirst);
                    }
                }
        ).show();
    }
    private void setType(String tps) {
        if (!TextUtils.isEmpty(type1)) {
            String txt = "";
            switch (type1) {
                case "0":
                    txt = "个人账户";
                    findViewById(R.id.bank_see).setClickable(false);
                    if (tps!=null&&!type.equals(type1)){
                        bankAdmin.setHint("请输入本人银行卡号");
                        bankAdmin.setText("");
                        bankOpen.setText("");
                        bankOpen.setHint("");
                        findViewById(R.id.right_arrow3).setVisibility(View.GONE);
                        type=type1;
                    }
                    userName.setText(realName);
                    break;
                case "1":
                    txt = "对公账户";
                    findViewById(R.id.bank_see).setClickable(true);
                    findViewById(R.id.right_arrow3).setVisibility(View.VISIBLE);
                    if (tps!=null&&!type.equals(type1)){
                        bankAdmin.setHint("请输入本人银行卡号");
                        bankAdmin.setText("");
                        bankOpen.setText("");
                        bankOpen.setHint("请选择开户银行");
                        type=type1;
                    }
                    userName.setText(customerName);
                    break;
            }
            findViewById(R.id.bank_see).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type1.equals("1")){


                    opens = "1";
                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType
                            .PUBLIC, REQUEST_COMPANY);
                } else{
                        opens = "0";
                    }
                }
            });

            adminType.setText(txt);
        }
    }

    @OnClick({R.id.change_info_next, R.id.see_bank_list, R.id.rl_admin_type})
    public void OnClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.change_info_next:
                updateAccount();
                break;
            case R.id.see_bank_list:
                if (type1.equals("1")) {
                    opens = "2";
                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType
                            .PUBLIC, REQUEST_COMPANY);
                } else {
                   // qryBusinessBankList();
                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType
                            .PRIVATE, REQUEST_OPENBANK_LIST);
                }
                PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_Change_Accout_BankList, context);
                break;
          /*  case R.id.rl_bank_admin:
                intent = new Intent(UpdateAccountActivity.this, UpdateMechatInfoActivity.class);
                intent.putExtra("bankAdmin", bankAdmin.getText().toString().trim());
                startActivityForResult(intent, reqCode);
                break;*/
            case R.id.rl_admin_type:
                intent = new Intent(UpdateAccountActivity.this, UpdateMechatInfoActivity.class);
                intent.putExtra("acountType", bankAdmin.getText().toString().trim());
                intent.putExtra("type", type);
                startActivityForResult(intent, typeCode);
                //showPopupWindow(Poposition);
                break;

        }
    }

    /**
     * 查询支持业务的银行列表
     */
    public void qryBusinessBankList() {
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.SUPPORTED_BUSINESS_BANK_LIST);
        SupportedBankListRequest params = new SupportedBankListRequest(context);
        params.setBusId("19J");
        showProgressWithNoMsg();
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    String busiBanksString = resultServices.retData.toString();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED)
            return;
        if (resultCode == RESULT_OK) {
            if (requestCode==typeCode){
                type1= data.getStringExtra("tagTick");

                setType(type1);
            }else if (requestCode==reqCode){
                bankAccount=data.getStringExtra("bankAdmin");
                String bankAccount1= bankAccount.substring(0,6)+"******"+bankAccount.substring(bankAccount.length()-4,bankAccount.length());
                bankAdmin.setText(bankAccount1);
                if (data.hasExtra("tvSupportBank")){
                    tvSupportBank=data.getStringExtra("tvSupportBank");
                    bankOpen.setText(tvSupportBank);
                }
                if (data.hasExtra("bankNo")){
                    bankNo=data.getStringExtra("bankNo");
                }
            }
        }
        if (data!=null){
            if (data.hasExtra("openBankInfo")&& opens .equals("1")) {//开户行返回
                OpenBankInfo bank = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
                findViewById(R.id.right_arrow3).setVisibility(View.VISIBLE);
                bankOpen.setText(bank.bankname);
                bankOpen.setCompoundDrawables(null,null,null,null);
                tvSupportBank=bank.bankname;
                bankNo=bank.bankCode;
            }
        }
    }

    private void shoePd() {
        Intent intent =getIntent();
        intent.putExtra("upDate","upDateAccount");
        finish();
        /*BaseDialog dialog = DialogCreator.createOneConfirmButtonDialog(this, getString(R.string.ok), "恭喜您，您的商户信息提交成功，请耐心等待我们的审核", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent =getIntent();
                intent.putExtra("upDate","upDateAccount");
                finish();
            }
        });
        dialog.show();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);//解除绑定，官方文档只对fragment做了解绑
    }


}

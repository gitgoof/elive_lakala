package com.lakala.shoudan.activity.merchantmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.wallet.request.QuickCardBinRequest;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/17 0017.
 */
public class UpdateMechatInfoActivity extends BasePwdAndNumberKeyboardActivity {

    @Bind(R.id.name_input)
    EditText nameInput;
    @Bind(R.id.iv_tick_public)
    ImageView ivTickPublic;
    @Bind(R.id.iv_tick_person)
    ImageView ivTickPerson;
    @Bind(R.id.exit)
    ImageView exit;
    @Bind(R.id.info_sub)
    Button infoSub;
    @Bind(R.id.types)
    LinearLayout types;
    @Bind(R.id.accout)
    LinearLayout accout;

    private View viewGroup;
    private View banks;
    private String shopName;
    private String shopName1;
    private String accountNum;
    private String bankName;
    private String customType;//新老用户用户、已未开通成功标志
    private String bankListbankName;
    private String banListAccountType;//输入卡号的银行支持的类型
    private String accountType;//输入卡的类型
    private String tvSupportBank;//输入卡号类型
    private String tvBankname;//输入卡号
    private String tagTick;//1个人账户  2对公账户
    private String bankNo;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mechat_info);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        intent=getIntent();
        banks=findViewById(R.id.banks);
        if (getIntent().hasExtra("shopName")) {
            types.setVisibility(View.GONE);
            accout.setVisibility(View.VISIBLE);
            shopName = getIntent().getStringExtra("shopName");
            shopName1 = shopName;
            exit.setVisibility(View.VISIBLE);
            navigationBar.setTitle("店铺名称变更");
        } else if (getIntent().hasExtra("acountType")){
            tagTick=getIntent().getStringExtra("type");
            if (tagTick.equals("0"))
                ivTickPerson.setVisibility(View.VISIBLE);
            else
                ivTickPublic.setVisibility(View.VISIBLE);
            types.setVisibility(View.VISIBLE);
            navigationBar.setTitle(R.string.shop_account_change);
            accout.setVisibility(View.GONE);
        } else {
            banks.setVisibility(View.GONE);
            types.setVisibility(View.GONE);
            accout.setVisibility(View.VISIBLE);
            //shopName = getIntent().getStringExtra("bankAdmin");
            exit.setVisibility(View.GONE);
            navigationBar.setTitle(R.string.shop_account_change1);
            infoSub.setText("保存");
            viewGroup=(View)findViewById(R.id.view_group);
            //键盘
            setOnDoneButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewGroup.requestFocus();
                }
            });
            //infoSub.setEnabled(false);
            initNumberKeyboard();
            initNumberEdit(nameInput, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        }

        if (!TextUtils.isEmpty(shopName)) {
            nameInput.setText(shopName);
            //nameInput.setSelection(shopName.length());
        }else {
            exit.setVisibility(View.GONE);
    }

        nameInput.addTextChangedListener(textWatcher);
        nameInput.setCursorVisible(true);
    }

    private TextWatcher textWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
                exit.setVisibility(s.length()>0? View.VISIBLE:View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private void toSave() {

        //商户名称验证
        String businessNameStr = nameInput.getText().toString().trim();
        if (getIntent().hasExtra("shopName")) {
            int chineseSize = StringUtil.matchChinese(businessNameStr).length();
            String patternStr = "[a-zA-Z0-9\\.\\,\\()]";
            int oneWordSize = StringUtil.matchTarget(patternStr, businessNameStr).length();
            if (chineseSize * 2 + oneWordSize < 10) {
                ToastUtil.toast(context, "店铺名称不能少于5个字，支持汉字、字母");
                return ;
            }else if(nameInput.getText().toString().trim().equals(shopName1)){
                ToastUtil.toast(context, "店铺名称不能与原有店铺名称相同");
                return ;
            }
            updateAccount();

        } else  if (getIntent().hasExtra("acountType")) {
            intent.putExtra("tagTick",tagTick);
            setResult(RESULT_OK, intent);
            finish();
        }else {
            intent.putExtra("bankNo", bankNo);
            intent.putExtra("bankAdmin", accountNum);
            if (tvSupportBank!=null)
            intent.putExtra("tvSupportBank", tvSupportBank);
            ToastUtil.toast(context, "保存成功");
            setResult(RESULT_OK, intent);
            finish();
        }


    }
    private void updateAccount() {

        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_MERCHAT_INGO);
        showProgressWithNoMsg();
        HttpRequestParams params = businessRequest.getRequestParams();
        params.put("businessName",nameInput.getText().toString().trim() );
        /*params.put("accountType",getIntent().getStringExtra("accountType") );
        params.put("bankNo",getIntent().getStringExtra("bankNo") );
        params.put("bankName", getIntent().getStringExtra("bankName"));
        params.put("accountNo", getIntent().getStringExtra("accountNo"));*/
        params.put("optType","BUSINESSNAME" );
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());
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

    private void  showBaseDialog(){
        DialogCreator.createFullShueDialog(
                context, "提示",   "确定", "您的店铺名称已提交成功",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        intent.putExtra("upDate","upDateName");
                        ApplicationEx.getInstance().getUser().getMerchantInfo().setBusinessName(nameInput.getText().toString().trim());
                        // ApplicationEx.getInstance().getUser().getMerchantInfo().setBusinessName("");
                        // ToastUtil.toast(context, "保存成功");
                        setResult(RESULT_OK, intent);
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
  /*  @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
       if (getIntent().hasExtra("bankAdmin")){
           String businessNameStr = nameInput.getText().toString().trim();
           if(!hasFocus){
               if(toCheckCardBin(businessNameStr.toString())){
                   qryCarBin();
               }else{
                   ToastUtil.toast(context,"银行卡号输入不正确");
               }

           }
           if (hasFocus) {
               String newText = nameInput.getText().toString().replace(" ", "");
               nameInput.setText(newText);
               nameInput.setSelection(newText.length());
           } else {
               String text = CardUtil.formatCardNumberWithSpace(nameInput.getText().toString());
               nameInput.setText(text);
           }
       }
    }*/
    /**
     * 查询卡bin
     */
    public void qryCarBin(){
        showProgressWithNoMsg();
        QuickCardBinRequest params=new QuickCardBinRequest(context);
        accountNum=nameInput.getText().toString().trim();
        params.setAccountNo(accountNum);
        BusinessRequest request= RequestFactory.getRequest(this, RequestFactory.Type.QRY_QUICK_RELEASE_CARD_BIN);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        bankName = jsonObject.optString("bankName");
                        bankNo = jsonObject.optString("bankCode");

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

                        qryIsBankNumSupport();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
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
                                    //nameInput.setText(bankListbankName);
                                   // infoSub.setEnabled(true);
                                    toSave();
                                }else{
                                    if(accountType.equals(banListAccountType)){
                                        if(banListAccountType=="1"){
                                           // tvSupportBank="*仅支持储蓄卡";
                                        }else{
                                          //  tvSupportBank="*仅支持信用卡";
                                        }
                                        tvBankname=bankListbankName;
                                       // nameInput.setText(tvBankname);
                                      //  infoSub.setEnabled(true);
                                        toSave();
                                    }else {
                                        ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                       // infoSub.setEnabled(false);
                                    }
                                }
                                break;
                            }else{
                                if(i==jsonArray.length()-1){
                                    ToastUtil.toast(context, "此银行卡暂不支持快捷卡业务");
                                   // infoSub.setEnabled(false);
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

    private void showBtn(float alp, boolean bl) {
        navigationBar.setActionBtnAlph(alp);
        navigationBar.setActionBtnCliced(bl);
    }

    @OnClick({R.id.exit,R.id.info_sub,R.id.type_person,R.id.type_public})
    public void pickDoor(View view) {
        switch (view.getId()) {
            case R.id.exit:
                nameInput.setText("");
                exit.setVisibility(View.GONE);
                break;
            case R.id.info_sub:
               /* String businessNameStr = nameInput.getText().toString().trim();
                if(toCheckCardBin(businessNameStr.toString().trim())){
                    qryCarBin();
                }else{
                    ToastUtil.toast(context,"银行卡号输入不正确");
                }*/
                toSave();
                break;
            case R.id.type_person:
              tagTick="0";
              ivTickPerson.setVisibility(View.VISIBLE);
              ivTickPublic.setVisibility(View.GONE);
                toSave();
                break;
            case R.id.type_public:
                tagTick="1";
                ivTickPerson.setVisibility(View.GONE);
                ivTickPublic.setVisibility(View.VISIBLE);
                toSave();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);//解除绑定，官方文档只对fragment做了解绑
    }
}

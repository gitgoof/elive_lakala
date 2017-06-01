package com.lakala.shoudan.activity.shoudan.finance;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryOTPPasswordRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ShortcutSignRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.CaptchaTextView;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/10/17.
 */
public class BindingCardActivity extends AppBaseActivity {
    private static final int REQUEST_BANKS = 0x2315;
    private EditText etName;
    private EditText etIdcard;
    private TextView etBankname;
    private EditText etBankcode;
    private EditText etUserphone;
    private CaptchaTextView tvGetCaptcha;
    private EditText etInputCaptcha;
    private String busiBanksString;
    private List<JSONObject> banksData = new ArrayList<JSONObject>();
    private View nextBtn;
    private String captchaSid;
    private OpenParams params;
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();

    public static class OpenParams implements Serializable{
        private String name;
        private String idcard;

        public String getName() {
            return name;
        }

        public OpenParams setName(String name) {
            this.name = name;
            return this;
        }

        public String getIdcard() {
            return idcard;
        }

        public OpenParams setIdcard(String idcard) {
            this.idcard = idcard;
            return this;
        }
    }

    public static void open(Activity context, int requestCode, OpenParams params){
        Intent intent = new Intent(context,BindingCardActivity.class);
        intent.putExtra(OpenParams.class.getName(),params);
        context.startActivityForResult(intent, requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_card);
        params = (OpenParams)getIntent().getSerializableExtra(OpenParams.class.getName());
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("绑定银行卡");

        View view = findV(R.id.include_name);
        TextView tvNameLabel = getItemLabel(view);
        tvNameLabel.setText("申请人姓名");
        etName = getItemEdit(view);
//        etName.setText(Util.formateUserNameKeepLast(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName()));
        etName.setText(Util.formateUserNameKeepLast(params.getName()));
        etName.setOnFocusChangeListener(hintListener);
        setEditEditableFalse(etName);

        view = findV(R.id.include_idcard);
        TextView tvIdcardLabel = getItemLabel(view);
        tvIdcardLabel.setText("申请人身份证号");
        etIdcard = getItemEdit(view);
//        etIdcard.setText(Parameters.merchantInfo.getIdCardNo());
        etIdcard.setText(params.getIdcard());
        etIdcard.setOnFocusChangeListener(hintListener);
        setEditEditableFalse(etIdcard);

        etBankname = findV(R.id.tv_bankname);
        etBankname.setHint("请选择开户行");
        etBankname.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qryBusiBanks(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        SupportBankListActivity.open(context,busiBanksString , REQUEST_BANKS);
                                    }
                                }
                        );
                    }
                }
        );
        etBankname.setOnFocusChangeListener(hintListener);

        view = findV(R.id.include_bankcode);
        TextView tvBankcodeLabel = getItemLabel(view);
        tvBankcodeLabel.setText("储蓄卡卡号");
        etBankcode = getItemEdit(view);
        etBankcode.setHint("请输入银行卡号");
        etBankcode.setInputType(InputType.TYPE_CLASS_NUMBER);
        etBankcode.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        hintListener.onFocusChange(v, hasFocus);
                        String cardNo = etBankcode.getText().toString();
                        if(!hasFocus && !TextUtils.isEmpty(cardNo)){
                            cardQry(cardNo);
                        }
                    }
                }
        );
//        if("1".equals(Parameters.merchantInfo.getAccountType())){
//            etBankcode.setText(Util.formatCompanyAccount(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo()));
//        }else{
//            etBankcode.setText(Util.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo()));
//        }
//        setEditEditableFalse(etBankcode);

        view = findV(R.id.include_userphone);
        TextView tvUserphoneLabel = getItemLabel(view);
        tvUserphoneLabel.setText("持卡人手机号");
        etUserphone = getItemEdit(view);
        etUserphone.setHint("请输入银行预留手机号码");
        etUserphone.setInputType(InputType.TYPE_CLASS_PHONE);
        etUserphone.setOnFocusChangeListener(hintListener);

        TextView tvCaptchaLabel = findV(R.id.tv_label);
        tvCaptchaLabel.setText("短信验证码");

        tvGetCaptcha = findV(R.id.tv_get_captcha);
        Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCaptcha();
                    }
                }
        );
        etUserphone.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tvGetCaptcha.setEnabled(s.length() == 11);
                    }
                }
        );
        etUserphone.setOnFocusChangeListener(hintListener);
        etInputCaptcha = findV(R.id.et_input_captcha);
        etInputCaptcha.setOnFocusChangeListener(hintListener);

        nextBtn = findV(R.id.id_common_guide_button);
        nextBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shortCutSign();
                    }
                }
        );

        CheckBox cboxAgreement = findV(R.id.cbox_agreement);
        cboxAgreement.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        nextBtn.setEnabled(isChecked);
                    }
                }
        );
        TextView tvAgreement = findV(R.id.tv_agreement);
        tvAgreement.setText(ProtocalType.LC_DSXY.getTitle());
        tvAgreement.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProtocalActivity.open(context, ProtocalType.LC_DSXY);
                    }
                }
        );
    }

    private void cardQry(String cardNo) {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader,
                                   final JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    qryBusiBanks(
                            new Runnable() {
                                @Override
                                public void run() {
                                    String bankCode = responseData.optString("BankCode");
                                    boolean isSupport = false;
                                    for(JSONObject supportBank:banksData){
                                        String supportCode = supportBank.optString("BankCode","");
                                        if(TextUtils.equals(supportCode,bankCode)){
                                            isSupport = true;
                                            break;
                                        }
                                    }
                                    if(isSupport){
                                        etBankname.setTag(responseData);
                                        etBankname.setText(responseData.optString("BankName",""));
                                    }else{
                                        ToastUtil.toast(context,"您输入的银行卡不支持此业务，请更换银行卡");
                                    }
                                }
                            }
                    );
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

                ToastUtil.toast(context,R.string.socket_fail);
            }
        };
        FinanceRequestManager.getInstance().cardQry(cardNo,listener);
    }

    public void qryBusiBanks(final Runnable onFinishListener){
        if(banksData != null && banksData.size() != 0){
            onFinishListener.run();
            return;
        }
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    try {
                        JSONArray jsonArray = responseData.getJSONArray("List");
                        if (jsonArray == null || jsonArray.length() == 0) {
                            return;
                        }
                        banksData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            banksData.add(jsonObject);
                        }
                        busiBanksString = responseData.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (onFinishListener != null) {
                        onFinishListener.run();
                    }
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

                ToastUtil.toast(context,R.string.socket_fail);
            }
        };
        FinanceRequestManager.getInstance().qryBusiBanks("1G2",listener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_BANKS && resultCode == RESULT_OK){
            String bank = data.getStringExtra("data");
            try {
                JSONObject jsonObject = new JSONObject(bank);
                etBankname.setTag(jsonObject);
                etBankname.setText(jsonObject.optString("BankName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCaptcha() {
        String idCard = etIdcard.getText().toString();
        if(!Util.checkIdCard(idCard)){
            ToastUtil.toast(context,R.string.lc_invalid_idcard);
            return;
        }
        String accountNo = etBankcode.getText().toString();
        if(TextUtils.isEmpty(accountNo)){
            ToastUtil.toast(context,"请输入储蓄卡卡号");
            return;
        }
        //验证码
        String phone = etUserphone.getText().toString();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.toast(context,"请输入持卡人手机号");
            return;
        }
        JSONObject bank = (JSONObject) etBankname.getTag();
        QueryOTPPasswordRequest request = new QueryOTPPasswordRequest();
        request.setCustomerName(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        request.setIdentifier(idCard);
        request.setBankCode(bank.optString("BankCode"));
        request.setBankName(bank.optString("BankName"));
        request.setMobileInBank(phone);
        request.setAccountNo(accountNo);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    captchaSid = responseData.optString("sid","");
                    tvGetCaptcha.startCaptchaDown(59);
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                    tvGetCaptcha.resetCaptchaDown();
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().queryOTPPassword(request, listener);
    }
    /**
     * 代收签约
     */
    private void shortCutSign(){
        String phone = etUserphone.getText().toString();
        String captcha = etInputCaptcha.getText().toString();
        String idCard = etIdcard.getText().toString();
        String accountNo = etBankcode.getText().toString();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.toast(context,"请输入持卡人手机号");
            return;
        }
        if(TextUtils.isEmpty(captcha)){
            ToastUtil.toast(context,"请输入验证码");
            return;
        }
        if(!Util.checkIdCard(idCard)){
            ToastUtil.toast(context,R.string.lc_invalid_idcard);
            return;
        }
        if(TextUtils.isEmpty(accountNo)){
            ToastUtil.toast(context,"请输入储蓄卡卡号");
            return;
        }
        JSONObject bank = (JSONObject)etBankname.getTag();
        ShortcutSignRequest request = new ShortcutSignRequest();
        request.setCustomerName(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        request.setIdentifier(idCard);
        request.setBankName(bank.optString("BankName"));
        request.setBankCode(bank.optString("BankCode"));
        request.setMobileInBank(phone);
        request.setAccountNo(accountNo);
        request.setOTPPassword(captcha);
        request.setSrcid(captchaSid);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    DialogCreator.createOneConfirmButtonDialog(
                            context, "确定", "添加银行卡成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }
                    ).show();
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);

            }
        };
        tvGetCaptcha.resetCaptchaDown();
        FinanceRequestManager.getInstance().shortCutSign(request,listener);
    }
    private void storageViewSize(View view,Point size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null){
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
    }

    private Point getViewSize(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
    }
    private void setEditEditableFalse(EditText et) {
        et.setInputType(InputType.TYPE_NULL);
        et.setCursorVisible(false);
        et.setFocusable(false);
    }

    private TextView getItemLabel(View view) {
        return findV(view, R.id.id_combinatiion_text_edit_text);
    }

    private EditText getItemEdit(View view) {
        return findV(view, R.id.id_combination_text_edit_edit);
    }

    private <T extends View> T findV(View view, int id) {
        View v;
        if (view == null) {
            v = findViewById(id);
        } else {
            v = view.findViewById(id);
        }
        return v == null ? null : (T) v;
    }

    private <T extends View> T findV(int id) {
        return findV(null, id);
    }
}

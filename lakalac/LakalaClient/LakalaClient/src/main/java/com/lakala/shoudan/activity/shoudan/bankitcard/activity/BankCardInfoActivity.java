package com.lakala.shoudan.activity.shoudan.bankitcard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.HintFocusChangeListener;

/**
 * Created by HJP on 2015/11/23.
 */
public class BankCardInfoActivity extends BasePwdAndNumberKeyboardActivity {

    private View includeValidityPeriod;
    private View includeSafetyCode;
    private View includePhoneNumber;
    private View includeName;
    private View includeIdCard;

    private CheckBox cboxAgreement ;
    private TextView btnNext;
    private TextView tvBankName;
    private TextView tvBankType;
    private TextView tvBankNumber;

    private ImageView ivBankIcon;
    private LinearLayout llValidityPeriod;
    private LinearLayout llSafetyCode;
    /**
     * 有效期
     */
    private TextView tvValidityPeriod;
    private EditText etValidityPeriod;
    /**
     * 安全码
     */
    private TextView tvSafetyCode;
    private EditText etSafetyCode;
    /**
     * 手机号码
     */
    private TextView tvPhoneNumber;
    private EditText etPhoneNumber;
    /**
     * 身份证
     */
    private TextView tvIdCardId;
    private EditText etIdCardId;
    /**
     * 姓名
     */
    private TextView tvName;
    private EditText etName;

    private String cardType;
    private QuickCardBinBean quickCardBinBean;
    private String customType;//新老用户用户、已未开通成功标志

    private boolean isCreditCard;//储蓄卡、信用卡判别
    private TextView tvAgreement;
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_info);
        init();
    }
    public void init() {
        initUI();
        initData();
    }

    public void initData(){
        quickCardBinBean = (QuickCardBinBean) getIntent().getSerializableExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN);
        customType=getIntent().getStringExtra(Constants.IntentKey.CUSTOM_TYPE);
        if(quickCardBinBean==null || TextUtils.isEmpty(customType)){
            return;
        }


        if (customType.equals("new custom")) {
            initNumberEdit(etPhoneNumber, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
            initNumberEdit(etIdCardId, CustomNumberKeyboard.EDIT_TYPE_IDCARD, 30);
            etName.setEnabled(true);
            etIdCardId.setEnabled(true);
        }else{
            if(customType.equals("has identifier")){
                etName.setText(quickCardBinBean.getCustomerName());
                etIdCardId.setText(quickCardBinBean.getIdentifier());
            }else if(customType.equals("merchantStatus completed")){
                etName.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
                etIdCardId.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
            }
            etName.setEnabled(false);
            etIdCardId.setEnabled(false);
        }
        initNumberEdit(etPhoneNumber, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);

        cardType = quickCardBinBean.getAccountType();
        String accountNo=quickCardBinBean.getAccountNo();
        String subAccountNo=accountNo.substring(accountNo.length()-4);
        tvBankNumber.setText("（尾号"+subAccountNo+")");
        //判断银行卡类型 1：借记卡；2：信用卡
        if (cardType.equals("2")) {
            //信用卡
            isCreditCard=true;
            llValidityPeriod = (LinearLayout) findViewById(R.id.ll_validity_period);
            llSafetyCode = (LinearLayout) findViewById(R.id.ll_safety_code);

            llValidityPeriod.setVisibility(View.VISIBLE);
            llSafetyCode.setVisibility(View.VISIBLE);
            tvSafetyCode = (TextView) includeSafetyCode.findViewById(R.id.id_combinatiion_text_edit_text);
            etSafetyCode = (EditText) includeSafetyCode.findViewById(R.id.id_combination_text_edit_edit);
            tvSafetyCode.setText(R.string.safetycode);
            etSafetyCode.setHint(R.string.safety_code_hint);
            etSafetyCode.setInputType(InputType.TYPE_CLASS_PHONE);
            etSafetyCode.setOnFocusChangeListener(hintListener);
            tvValidityPeriod = (TextView) includeValidityPeriod.findViewById(R.id.id_combinatiion_text_edit_text);
            etValidityPeriod = (EditText) includeValidityPeriod.findViewById(R.id.id_combination_text_edit_edit);
            tvValidityPeriod.setText(R.string.validity_period);
            etValidityPeriod.setHint(R.string.validity_period_hint);
            etValidityPeriod.setOnFocusChangeListener(hintListener);
            initNumberEdit(etValidityPeriod, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
            initNumberEdit(etSafetyCode, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
            tvBankType.setText("信用卡");
            quickCardBinBean.setCVN2(etSafetyCode.getText().toString());
            quickCardBinBean.setCardExp(etValidityPeriod.getText().toString());
        } else {
            //储蓄卡
            isCreditCard=false;
            tvBankType.setText("储蓄卡");
        }
        tvBankName.setText(quickCardBinBean.getBankName());
        //设置icon
        Bitmap bitmap = com.lakala.library.util.ImageUtil.getBitmapInAssets(context,
                "bank_icon/" + quickCardBinBean.getBankCode()+ "" +
                        ".png");
        ivBankIcon.setImageBitmap(bitmap);
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.credit_card_info);

        includeValidityPeriod = findViewById(R.id.include_validity_period);
        includeSafetyCode = findViewById(R.id.include_safety_code);
        includePhoneNumber = findViewById(R.id.include_phone_number);
        includeValidityPeriod = findViewById(R.id.include_validity_period);
        includeName = findViewById(R.id.include_name);
        includeIdCard = findViewById(R.id.include_idcard);

        tvBankName=(TextView)findViewById(R.id.tv_bank_name);
        tvBankType=(TextView)findViewById(R.id.tv_bank_type);
        tvBankNumber=(TextView)findViewById(R.id.tv_bank_number);
        tvAgreement=(TextView)findViewById(R.id.tv_agreement);

        tvPhoneNumber = (TextView) includePhoneNumber.findViewById(R.id.id_combinatiion_text_edit_text);
        etPhoneNumber = (EditText) includePhoneNumber.findViewById(R.id.id_combination_text_edit_edit);
//        etPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        tvName = (TextView) includeName.findViewById(R.id.id_combinatiion_text_edit_text);
        etName = (EditText) includeName.findViewById(R.id.id_combination_text_edit_edit);
        etName.setOnFocusChangeListener(hintListener);
        tvIdCardId = (TextView) includeIdCard.findViewById(R.id.id_combinatiion_text_edit_text);
        etIdCardId = (EditText) includeIdCard.findViewById(R.id.id_combination_text_edit_edit);
        btnNext = (TextView) findViewById(R.id.tv_next_step);
        ivBankIcon = (ImageView) findViewById(R.id.iv_bank_icon);
        tvPhoneNumber.setText(R.string.phone_number);
        tvName.setText(R.string.name);
        tvIdCardId.setText(R.string.wallet_ID_card);
        etName.setHint(R.string.user_name_error1);
        etPhoneNumber.setHint(R.string.bank_mobile_phone);
        etIdCardId.setHint(R.string.user_IDcard_error1);
        cboxAgreement = (CheckBox) findViewById(R.id.cbox_agreement);
        CompoundButton.OnCheckedChangeListener CheckListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnNext.setEnabled(true);
//                btnNext.setBtnBackgroundDrawable(getResources().getDrawable(R.drawable.btn_topline_selector));
                } else {
                    btnNext.setEnabled(false);
//                btnNext.setBtnBackgroundDrawable(getResources().getDrawable(R.drawable.gray_bg));
                }
            }
        };
        View.OnClickListener ClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValid()){

                }else{
                    if(isCreditCard){
                        quickCardBinBean.setCVN2(etSafetyCode.getText().toString());
                        quickCardBinBean.setCardExp(etValidityPeriod.getText().toString());
                    }
                    quickCardBinBean.setCustomerName(etName.getText().toString());
                    quickCardBinBean.setIdentifier(etIdCardId.getText().toString());
                    quickCardBinBean.setMobileInBank(etPhoneNumber.getText().toString());
                    Intent intent=new Intent(context, SMSBankVerificationActivity.class);
                    intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN,quickCardBinBean);
                    intent.putExtra(Constants.IntentKey.SIGN_OR_UNSIGN, "sign");
                    startActivityForResult(intent, ConstKey.REQUEST_ADD);
                }

            }
        };
        cboxAgreement.setOnCheckedChangeListener(CheckListener);
        btnNext.setOnClickListener(ClickListener);
        //键盘
        initNumberKeyboard();

        tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocalActivity.open(context, ProtocalType.QUICK_PAY_SERVICE);
            }
        });
    }
    public boolean isValid(){
        if(isCreditCard){
            if(TextUtils.isEmpty(etValidityPeriod.getText())){
                ToastUtil.toast(context,"有效期不能为空");
                return false;
            }
            if(TextUtils.isEmpty(etSafetyCode.getText())){
                ToastUtil.toast(context,"安全码不能为空");
                return false;
            }
        }
        if(TextUtils.isEmpty(etName.getText())){
            ToastUtil.toast(context,"姓名不能为空");
            return false;
        }
        if(etPhoneNumber.getText().length()!=11){
            ToastUtil.toast(context,"手机号码长度为11位");
            return false;
        }
        if(!Util.checkIdCard(etIdCardId.getText().toString())){
            ToastUtil.toast(context,R.string.id_no_input_error);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ConstKey.REQUEST_ADD &&resultCode == ConstKey.RESULT_ADD_BACK){
            setResult(ConstKey.RESULT_ADD_BACK,data);
            finish();
        }
    }
}

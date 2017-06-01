package com.lakala.shoudan.activity.shoudan.loan;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.BankInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.OpenBankInfoCallback;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.IMEUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.CardEditFocusChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 还款和还贷信息
 * 
 * @author ldm
 * 
 */
public class LoanMainActivity extends BasePwdAndNumberKeyboardActivity implements
		OnClickListener {

	private EditText creditCardNo;// 信用卡卡号
	private TextView openBank;// 开户行
	private OpenBankInfo mOpenBankInfo;
	private EditText savingCard;// 绑定储蓄卡
	private TextView savingCard_OpenBank;// 签约卡开户行
	private OpenBankInfo mSavingCard_OpenCard;
	private TextView btnSure;

	/** 用户协议 */
	private CheckBox checkProtocol;
	private TextView agreetex;

	private final int NEXT_STEP = 0x01;
	private static final int REQUEST_OPENBANK_LIST = 0x99;
	private int banktype = 0;// 记录开户行选择类型
	private SharedPreferences sp;
	private String orderno;
	private BankInfo bankInfo;
	private LoanBackShowInfo backShowInfo;

	private String creditCardFromRemittaqnce = "";
    private CardEditFocusChangeListener savingCardListener;
    private CardEditFocusChangeListener creditListener;
    private View viewGroup;
    private final String CREDIT_CARD = "CREDIT_CARD_NUMBER";
    private final String SAVING_CARD_NUMBER = "SAVING_CARD_NUMBER";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_main);
		initUI();
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	protected void initUI() {

		navigationBar.setTitle(getString(R.string.payforyou));
		// 信用卡卡号
		creditCardNo = (EditText) findViewById(R.id.credit_cardno);
        creditListener = new CardEditFocusChangeListener();
        creditListener.setBankBusid(BankBusid.LOAN_FOR_YOU);
        creditListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if (openBankInfo == null || !TextUtils.equals(openBankInfo.acccountType, "C")) {
                    return;
                }
                mOpenBankInfo = openBankInfo;
                banktype = 0;
                updateBankInfo();
            }
        });
        creditCardNo.setOnFocusChangeListener(null);
        creditCardNo.setTag(CREDIT_CARD);
        creditCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String cardNum = com.lakala.library.util.StringUtil.trim(editable.toString());
                if (cardNum.equals("")){
                    openBank.setText("");
                }
            }
        });

		// 开户行
		openBank = (TextView) findViewById(R.id.openbank);
		findViewById(R.id.support_bank_list).setOnClickListener(this);
        findViewById(R.id.saving_bank_list).setOnClickListener(this);
		// 绑定储蓄卡
		savingCard = (EditText) findViewById(R.id.savingcard);
        savingCardListener = new CardEditFocusChangeListener();
        savingCardListener.setBankBusid(BankBusid.LOAN_FOR_YOU);
        savingCardListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback(){
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if(openBankInfo == null || !TextUtils.equals(openBankInfo.acccountType,"D")){
                    return;
                }
                mSavingCard_OpenCard = openBankInfo;
                banktype = 1;
                updateBankInfo();
            }
        });
        savingCard.setOnFocusChangeListener(null);
        savingCard.setTag(SAVING_CARD_NUMBER);
        savingCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNum = com.lakala.library.util.StringUtil.trim(s.toString());
                if (cardNum.equals("")){
                    savingCard_OpenBank.setText("");
                }
            }
        });

		// 签约卡开户行
		savingCard_OpenBank = (TextView) findViewById(R.id.savingcard_openbank);
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		btnSure.setOnClickListener(this);
		
		agreetex = (TextView) findViewById(R.id.scanagreement);
		agreetex.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		agreetex.getPaint().setAntiAlias(true);//防锯齿
		checkProtocol = (CheckBox) findViewById(R.id.protocol_lakala_payforyou);
		agreetex.setOnClickListener(this);

		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
		backShowInfo=(LoanBackShowInfo) getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO);
		bankInfo=new BankInfo();
		if(backShowInfo != null && backShowInfo.isPass()){
            String credit = backShowInfo.getCreditcard();
            if(!TextUtils.isEmpty(credit)){
                creditListener.checkBinTask(credit);
            }
            String debitCard = backShowInfo.getDebitcard();
            if(!TextUtils.isEmpty(debitCard)){
                savingCardListener.checkBinTask(debitCard);
            }
		}else if(!TextUtils.isEmpty(creditCardFromRemittaqnce)){//来自信用卡还款，需要显示传递过来的卡号
			creditCardNo.setText(creditCardFromRemittaqnce);
            creditListener.checkBinTask(creditCardFromRemittaqnce);
		}
		setCheckListeners();

        viewGroup = findViewById(R.id.viewgroup);
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(creditCardNo, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        initNumberEdit(savingCard,CustomNumberKeyboard.EDIT_TYPE_CARD,30);
	}

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.scroll_keyboard));
    }

    private void checkCreditCard(){
        creditListener.checkBinTask(creditCardNo.getText().toString(), new OpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if(openBankInfo == null){
                    toast("暂不支持此银行，请更换银行卡。");
                    return;
                }
                mOpenBankInfo = openBankInfo;
                checkSavingCard();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
            }
        });
    }
    private void checkSavingCard(){
        savingCardListener.checkBinTask(creditCardNo.getText().toString(), new OpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if(openBankInfo == null){
                    toast("暂不支持此银行，请更换银行卡。");
                    return;
                }
                mSavingCard_OpenCard = openBankInfo;
                bankStorage();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        Object tag = v.getTag();
        if(TextUtils.equals(String.valueOf(tag),CREDIT_CARD)){
            creditListener.onFocusChange(v, hasFocus);
        }else if(TextUtils.equals(String.valueOf(tag),SAVING_CARD_NUMBER)){
            savingCardListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
	public void onClick(View v) {
        super.onClick(v);
		IMEUtil.hideIme(this);
		switch (v.getId()) {
		case R.id.next:
			if (!isInputValid()) {
                return;
			}
            OpenBankInfo info1 = creditListener.getOpenBankInfo(creditCardNo.getText().toString());
            if(info1 == null){
                checkCreditCard();
                return;
            }
            OpenBankInfo info2 = savingCardListener.getOpenBankInfo(savingCard.getText().toString
                    ());
            if(info2 == null){
                checkSavingCard();
                return;
            }
            checkCardBin(info1,info2);
			break;
		case R.id.scanagreement:
            ProtocalActivity.open(context, ProtocalType.LOAN_BANK_STORE);
			break;
		case R.id.saving_bank_list:
			// 签约卡开户行
            BankChooseActivity.openForResult(context, BankBusid.LOAN_FOR_YOU, BankInfoType.DEBIT,
                                             REQUEST_OPENBANK_LIST);
            banktype = 1;
			break;
		case R.id.support_bank_list:
			// 信用卡开户行
            BankChooseActivity.openForResult(context, BankBusid.LOAN_FOR_YOU, BankInfoType.CREDIT,
                                             REQUEST_OPENBANK_LIST);
			banktype = 0;
			break;
		default:
			break;
		}
	}

    private void checkCardBin(final OpenBankInfo creditInfo,final OpenBankInfo savingInfo) {
        if(!creditListener.isBankInfoValid(creditInfo,mOpenBankInfo)){
            //不支持信用卡
            toast("暂不支持此银行，请更换银行卡。");
        }else if(!savingCardListener.isBankInfoValid(savingInfo,mSavingCard_OpenCard)){
            //不支持储蓄卡
            toast("暂不支持此银行，请更换银行卡。");
        }else{
            PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work_Bank_Info_Apllay1, context);
            bankStorage();
        }
    }

	/**
	 * 银行信息入库
	 * @throws Exception
	 */
	private void bankStorage(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    //返回用户可贷额度loanlimit和返回订单号
                    try {
                        JSONObject jsonObject1 = new JSONObject(resultServices.retData);
                        orderno = jsonObject1.getString("orderno");
                        backShowInfo.setOrderno(orderno);
                        Intent intent = new Intent(LoanMainActivity.this,LoanConfirmApplyActivity.class);
                        intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO, backShowInfo);
                        intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                        startActivity(intent);
                        PublicToEvent.LoanEvent( ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work_Bank_Info_Apllay, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if("00000O".equals(resultServices.retCode)){
                    //{"orderno":"S20141202142630505373","loanlimit":800000}
                    try {
                        JSONObject jsonLimit = new JSONObject(resultServices.retData) ;
                        String loanLimit = jsonLimit.optString("loanlimit");
                        backShowInfo.setLoanLimits(Util.fen2Yuan(loanLimit));
                        Intent intent = new Intent(LoanMainActivity.this,LoanNotificationActivity.class);
                        backShowInfo.setPass(false);
                        intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO, backShowInfo);
                        intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                        intent.putExtra(Constants.IntentKey.FAILED_REASON, resultServices.retMsg);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
                ToastUtil.toast(context,R.string.socket_fail);
            }
        };
        ShoudanService.getInstance().bankStorage(backShowInfo.getOrderno(), bankInfo, callback);
	}

	protected boolean isInputValid() {
		String creditcardNo=creditCardNo.getText().toString().trim();
		if (TextUtils.isEmpty(creditcardNo)) {
			ToastUtil.toast(context,"请输入信用卡卡号");
			return false;
		}
		if (TextUtils.isEmpty(creditcardNo)) {
            ToastUtil.toast(context,"请输入正确的卡号");
			return false;
		}
		backShowInfo.setCreditcard(creditcardNo);
		bankInfo.setCreditCard(creditcardNo);
		if (TextUtils.isEmpty(openBank.getText().toString().trim())) {
            ToastUtil.toast(context,"请先选择信用卡开户行");
			return false;
		}
		String savingCardNo=savingCard.getText().toString().trim();
		if (TextUtils.isEmpty(savingCardNo)) {
            ToastUtil.toast(context,"请输入签约银行卡卡号");
			return false;
		}
		if (TextUtils.isEmpty(savingCardNo)) {
            ToastUtil.toast(context,"请输入正确的卡号");
			return false;
		}
		backShowInfo.setDebitcard(savingCardNo);
		bankInfo.setDebitcard(savingCardNo);
		if (TextUtils.isEmpty(savingCard_OpenBank.getText().toString().trim())) {
            ToastUtil.toast(context,"请先选择签约卡开户行");
			return false;
		}
		if(!checkProtocol.isChecked()){
            ToastUtil.toast(context,"请查看并同意协议");
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 开户行 返回结果处理
		if (requestCode == REQUEST_OPENBANK_LIST&& UniqueKey.BANK_LIST_CODE == resultCode) {
			if (banktype == 0)
				mOpenBankInfo = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
			else {
				mSavingCard_OpenCard = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
			}
			updateBankInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取开户行信息，更新UI
	 */
	private void updateBankInfo() {
		switch (banktype) {
		case 0:
			openBank.setText(mOpenBankInfo.bankname);
			// bankInfo.setCreditCard(mOpenBankInfo.cardNo);
			bankInfo.setCreditbank(mOpenBankInfo.bankCode);
			backShowInfo.setCreditbankName(mOpenBankInfo.bankname);
			backShowInfo.setCreditbank(mOpenBankInfo.bankCode);
			break;
		case 1:
			savingCard_OpenBank.setText(mSavingCard_OpenCard.bankname);
			// bankInfo.setDebitcard(mSavingCard_OpenCard.cardNo);
			bankInfo.setDebitbank(mSavingCard_OpenCard.bankCode);
			backShowInfo.setDebitbankName(mSavingCard_OpenCard.bankname);
			backShowInfo.setDebitbank(mSavingCard_OpenCard.bankCode);
			break;
		default:
			break;
		}
	}
	/**
	 * 为checkbox设置监听器
	 */
	private void setCheckListeners(){
		checkProtocol.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {// 是否同意拉卡拉服务协议及是否可以点击下一步
				if (isChecked) {
					btnSure.setEnabled(true);
//					btnSure.setBackgroundResource(R.drawable.orange_btn_bg);
				} else {
					btnSure.setEnabled(false);
//					btnSure.setBackgroundResource(R.drawable.disable_btn_bg);
				}
			}
		});
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	
	public static class CardDetailInfo {
		
		public String cardNo;
		public String cardLen;
		public String cardHeader;
		public String cardName;
		public String bankName;
		public String cardHeaderLen;
		public String productName;
		public String cardType;
		public String bankInfoId;
		public String bankCode;
		
		public CardDetailInfo(){};
		
		public CardDetailInfo(String cardNo){
			this.cardNo = cardNo;
		}
		
		
		public void unpackCardDetailInfo(JSONObject jsonData) throws Exception{
			this.cardLen = jsonData.optString("cardLen");
			this.cardHeader = jsonData.optString("cardHeader");
			this.cardName = jsonData.optString("cardName");
			this.bankName = jsonData.optString("bankName");
			this.cardHeaderLen = jsonData.optString("cardHeaderLen");
			this.productName = jsonData.optString("productName");
			this.cardType = jsonData.optString("cardType");
			this.bankInfoId = jsonData.optString("bankInfoId");
			this.bankCode = jsonData.optString("bankCode");
			
		}
			
	}

}

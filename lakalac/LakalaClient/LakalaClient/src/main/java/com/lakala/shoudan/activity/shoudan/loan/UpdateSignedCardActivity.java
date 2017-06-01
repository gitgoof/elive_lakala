package com.lakala.shoudan.activity.shoudan.loan;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.LoanMainActivity.CardDetailInfo;

/**
 * 更换签约卡
 * @author Zhangmy
 *
 */
public class UpdateSignedCardActivity extends AppBaseActivity implements OnClickListener{
	private static final int REQUEST_OPENBANK_LIST = 0x99;
	private EditText editCardNumber;//储蓄卡号
	private EditText editCardBankName;//开户行
	private TextView btnCommit;//提交
	private CheckBox cbAgreen;
	private TextView tvPro;//协议
	
	private String contractno;
	
	private OpenBankInfo signedCardBankInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_signedcard);
		initUI();
	}
	
	protected void initUI() {
		navigationBar.setTitle("更换签约卡");
		//填写内容
		editCardNumber = (EditText) findViewById(R.id.tv_card_number_value);
		editCardBankName = (EditText) findViewById(R.id.tv_bank_value);
		editCardBankName.setOnClickListener(this);
		findViewById(R.id.img_right).setOnClickListener(this);
		//提交
		btnCommit = (TextView) findViewById(R.id.btn_commit);
		btnCommit.setOnClickListener(this);
		btnCommit.setText("提交申请");
		//协议
		cbAgreen = (CheckBox) findViewById(R.id.cb_agreen);
		tvPro = (TextView) findViewById(R.id.tv_pro);
		tvPro.setOnClickListener(this);
		
		contractno = getIntent().getStringExtra(SignedCardManagerActivity.CONTRACTNO);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit://提交
			if(validate()){
				updateSignedCard(editCardNumber.getText().toString().trim());
			}
			break;
		case R.id.tv_pro://协议s
            ProtocalActivity.open(context, ProtocalType.LOAN_BANK_STORE);
			break;
		case R.id.tv_bank_value:
		case R.id.img_right:
			// 签约卡开户行
            BankChooseActivity.openForResult(context, BankBusid.LOAN_FOR_YOU, BankInfoType.DEBIT,
                                             REQUEST_OPENBANK_LIST);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 开户行 返回结果处理
		if (requestCode == REQUEST_OPENBANK_LIST&& UniqueKey.BANK_LIST_CODE == resultCode) {
			signedCardBankInfo = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
			editCardBankName.setText(signedCardBankInfo.bankname);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//提交后确认框
	private void showSignedCardConfirmDialog(){
		final Dialog dialog = new Dialog(UpdateSignedCardActivity.this, R.style.dianyingpiao_progress_dialog_with_bg);
		View rootView = LayoutInflater.from(UpdateSignedCardActivity.this).inflate(R.layout.layout_update_signed_card_confirm, null);
		Button btnSure = (Button) rootView.findViewById(R.id.btn_sure);
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(UpdateSignedCardActivity.this,SignedCardManagerActivity.class);
				intent.putExtra(SignedCardManagerActivity.CONTRACTNO, contractno);
				startActivity(intent);
			}
		});
		dialog.setContentView(rootView);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
	//校验
	private boolean validate(){
		String cardNumber = editCardNumber.getText().toString().trim();
		if(TextUtils.isEmpty(cardNumber)){
			ToastUtil.toast(context,"请输入卡号");
			return false;
		}
		String cardName = editCardBankName.getText().toString().trim();
		if(TextUtils.isEmpty(cardName)){
			ToastUtil.toast(context,"请选择开户行");
			return false;
		}
		if(!cbAgreen.isChecked()){
			ToastUtil.toast(context,"请选择协议");
			return false;
		}
		return true;
	}
	//更换请求1.校验开户行和银行卡，2发送更换请求
	private void updateSignedCard(final String cardNumber){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonDataBank = new JSONObject(resultServices.retData);
                        CardDetailInfo creditCardDetailInfo = new CardDetailInfo(cardNumber);
                        creditCardDetailInfo.unpackCardDetailInfo(jsonDataBank);
                        //信用卡是否准确 0002 是信用卡 0001 是储蓄卡
                        if("0001".equals(creditCardDetailInfo.cardType) && signedCardBankInfo.bankCode.equals(creditCardDetailInfo.bankCode)){
                            //卡信息正确，更改卡
                            signedAccount(cardNumber);
                        }else {
                            ToastUtil.toast(context,"银行卡信息错误");
                        }
                    } catch (Exception e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }else {
                    hideProgressDialog();
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getBankInfoByCardno(cardNumber, callback);
	}

    private void signedAccount(String cardNumber) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    showSignedCardConfirmDialog();
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().updateSignedAccouont(contractno, cardNumber,
                                                          signedCardBankInfo.bankCode, callback);

    }
}

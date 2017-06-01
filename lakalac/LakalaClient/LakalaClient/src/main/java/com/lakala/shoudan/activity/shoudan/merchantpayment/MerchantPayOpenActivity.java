package com.lakala.shoudan.activity.shoudan.merchantpayment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.component.DialogCreator;

import android.widget.Button;

/**
 * 特约商户签约
 *
 * 开通页面
 *
 * @author zmy
 *
 */
public class MerchantPayOpenActivity extends AppBaseActivity implements OnClickListener,OnCheckedChangeListener{

	private TextView tvMechantPhone,tvMerchantName,tvMerchantAddress,tvMerchantPerson;//负责人
	private CheckBox checkBox;//已经接受服务
	private Button btnCancel,btnSure;
	private  MerchantPayTransInfo mer;
	private TextView tvTeyueShanghuServicePro;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchantpay_signature);
		initUI();
	}
	
	protected void initUI() {
		
		navigationBar.setTitle(R.string.merchant_pay_signatuer);

        View includeMerchantPhone = findViewById(R.id.include_merchant_phone);
        TextView labelMerchantPhone = (TextView) includeMerchantPhone.findViewById(
                R.id.id_combinatiion_text_edit_text
        );
        labelMerchantPhone.setText("商户电话");
        tvMechantPhone = (TextView)includeMerchantPhone.findViewById(
                R.id.id_combination_text_edit_edit
        );

        View includeMerchantName = findViewById(R.id.include_merchant_name);
        TextView labelMerchantName = (TextView)includeMerchantName.findViewById(
                R.id.id_combinatiion_text_edit_text
        );
        labelMerchantName.setText("商户名称");
        tvMerchantName = (TextView) includeMerchantName
                .findViewById(R.id.id_combination_text_edit_edit);

        View includeMerchantAddress = findViewById(R.id.include_merchant_address);
        TextView labelMerchantAddress = (TextView) includeMerchantAddress.findViewById(
                R.id.id_combinatiion_text_edit_text
        );
        labelMerchantAddress.setText("商户地址");
        tvMerchantAddress = (TextView) includeMerchantAddress.findViewById(
                R.id.id_combination_text_edit_edit
        );

        View includeMerchantPerson = findViewById(R.id.include_merchant_person_charge);
        TextView labelMerchantPerson = (TextView) includeMerchantPerson.findViewById(
                R.id.id_combinatiion_text_edit_text
        );
        labelMerchantPerson.setText("商户负责人");
        tvMerchantPerson = (TextView) includeMerchantPerson.findViewById(
                R.id.id_combination_text_edit_edit
        );
        checkBox = (CheckBox) findViewById(R.id.checkbox_receiver);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnSure = (Button) findViewById(R.id.btn_sure);

        mer = (MerchantPayTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        tvMechantPhone.setText(ApplicationEx.getInstance().getUser().getLoginName());
        tvMerchantAddress.setText(mer.getAddress());
        tvMerchantPerson.setText(mer.getMercname());
        tvMerchantName.setText(mer.getOrganizationName());

		checkBox.setOnCheckedChangeListener(this);
		btnCancel.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		tvTeyueShanghuServicePro = (TextView) findViewById(R.id.tv_teyueshanghu_service_pro);
		tvTeyueShanghuServicePro.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel://取消
			finish();
			break;
		case R.id.btn_sure://确认
            onConfirmClick();
			break;
		case R.id.tv_teyueshanghu_service_pro:
            ProtocalActivity.open(MerchantPayOpenActivity.this, ProtocalType.TEYUEJIAOFEI_PRO);
			break;
		default:
			break;
		}
	}

    private void onConfirmClick(){

        if(!checkBox.isChecked()){
            ToastUtil.toast(MerchantPayOpenActivity.this,"请确认已经接受服务协议");
            return;
        }
        showProgressWithNoMsg();

                    ShoudanService.getInstance().openContributePayment(mer.getInstBill(), new ServiceResultCallback() {
                        @Override
                        public void onSuccess(ResultServices rslt) {
                            if (!rslt.isRetCodeSuccess()) {

                                toast(rslt.retMsg);

                            } else {
                                nextStep();
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onEvent(HttpConnectEvent connectEvent) {
                            toastInternetError();
                            hideProgressDialog();
                        }
                    });





    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){//同意服务协议
			
		}else {
			
		}
	}


    protected void nextStep() {
        commitOpenSuccessTag();
        showOpenSuccessDialog();
        ShoudanStatisticManager.getInstance()
                .onEvent(ShoudanStatisticManager.Singn_Success_Merchant_Management, context);
    }

    private void commitOpenSuccessTag(){
        //save open success tag;
        ApplicationEx.getInstance().getUser().getAppConfig().setContributePaymentEnabled(true);

    }

    /**
     * 显示特约商户开通成功的弹框
     */
    private void showOpenSuccessDialog(){
        DialogCreator.createOneConfirmButtonDialog(context, "确定", "特约商户签约成功", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callMainActivity();
            }
        }).show();
    }

}

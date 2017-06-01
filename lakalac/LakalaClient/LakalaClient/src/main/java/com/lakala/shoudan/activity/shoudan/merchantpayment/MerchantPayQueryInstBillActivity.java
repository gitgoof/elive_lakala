package com.lakala.shoudan.activity.shoudan.merchantpayment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 缴款机构选择
 *
 * 机构编号验证查询
 *
 * @author zmy
 *
 */
public class MerchantPayQueryInstBillActivity extends AppBaseActivity implements OnClickListener{
	private TextView tvOrganizationValue;//机构值
	private EditText editMerchantNo;//商户标号值
	private Button btnNext;//下一步
	private TextView tvPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchantpay_organization);
		initUI();
	}
	
	protected void initUI() {
		
		navigationBar.setTitle("特约商户缴费");
		
		tvOrganizationValue = (TextView) findViewById(R.id.tv_organization_value);
		editMerchantNo = (EditText) findViewById(R.id.edit_merchant_no);
		btnNext = (Button) findViewById(R.id.next);
		btnNext.setOnClickListener(this);

        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvPhone.setText(ApplicationEx.getInstance().getUser().getLoginName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next://下一步s
            if(checkInput()){
                queryContributePayment(editMerchantNo.getText().toString());
            }else{
                toast("请输入正确的商户编号");
            }
			break;

		default:
			break;
		}
	}

    private boolean checkInput(){
        if(editMerchantNo.getText() == null || editMerchantNo.getText().toString().length() == 0){
            return false;
        }
        return true;
    }

    private void queryContributePayment(final String instBill){


        final MerchantPayTransInfo merchantPayTransInfo = new MerchantPayTransInfo();

        merchantPayTransInfo.setInstBill(instBill);

        showProgressWithNoMsg();

        ShoudanService.getInstance().contributeQuery(merchantPayTransInfo.getInstBill(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                hideProgressDialog();
                String sid = "";
                String billNo = "";
                if (resultForService.isRetCodeSuccess()) {

                    JSONObject retData = null;
                    try {
                        retData = new JSONObject(resultForService.retData);

                        if (retData.has("sid")) {
                            sid = retData.optString("sid");
                        }
                        merchantPayTransInfo.setQuerySid(sid);
                        if (retData.has("billno")) {
                            billNo = retData.getString("billno");
                        }
                        String isbind = retData.getString("isbind");

                        merchantPayTransInfo.setBind(isbind);
                        if (retData.has("bmercname")) {
                            merchantPayTransInfo.setMercname(retData.getString("bmercname"));
                        }
                        merchantPayTransInfo.setOrganizationName("新疆福彩缴费");
                        if (retData.has("address")) {
                            merchantPayTransInfo.setAddress(retData.getString("address"));
                        }

                        getIntent().putExtra(Constants.IntentKey.TRANS_INFO, merchantPayTransInfo);

                        if (merchantPayTransInfo.isBind()) {

                            toast("您的商户编号已签约成功，无需重复签约");
//                                    ShouDanMainActivity.ifReloadType2 = true;
                            commitOpenSuccessTag();

                        } else {

                            getIntent().setClass(MerchantPayQueryInstBillActivity.this, MerchantPayOpenActivity.class);
                            startActivity(getIntent());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultForService.retMsg);
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

    private void commitOpenSuccessTag(){
        //save open success tag;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ApplicationEx.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(UniqueKey.getMerchantPaymentOpenStatus(), true);
        editor.commit();
//        ShouDanMainActivity.ifReloadType2 = true;
    }

}

package com.lakala.shoudan.activity.myaccount;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;


/**
 * 关于收款宝
 * 
 * @author More
 * 
 */
public class AccountAboutLakalaActivity extends AppBaseActivity implements
		OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoudan_account_about_lakala);
		initUI(); 
	}

	/** 初始化页面 */
	protected void initUI() {
		Button callButton = (Button) findViewById(R.id.call_btn);
		callButton.setOnClickListener(this);
		navigationBar.setTitle(R.string.about_us);
		TextView versionName = (TextView) findViewById(R.id.id_about_us_version_name);
		String prefix = "版本：V";
		String versionNameString = Util.getAppVersionName();
		versionName.setText(prefix + versionNameString);
		TextView serviceInformation = (TextView) findViewById(R.id.service_information);
		TextView shoudanInformation = (TextView) findViewById(R.id.service_shoudan_information);
        TextView serviceWX = (TextView)findViewById(R.id.service_wx);
		shoudanInformation.setOnClickListener(this);
		serviceInformation.setOnClickListener(this);
        serviceWX.setOnClickListener(this);

	}

	/** 点击事件 */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_btn:// 打电话
                getPhone();
                break;
            case R.id.service_information:// 服务协议
                ProtocalActivity.open(AccountAboutLakalaActivity.this, ProtocalType.USER);
                break;
            case R.id.service_shoudan_information:

                ProtocalActivity.open(AccountAboutLakalaActivity.this, ProtocalType.SHOU_KUAN_BAO_SERVICE_URL);
                break;
            case R.id.service_wx: {

                ProtocalActivity.open(AccountAboutLakalaActivity.this, ProtocalType.SERVICE_WX);

                break;
            }
        }
    }

    private void getPhone(){
        showProgressWithNoMsg();
        ShoudanService.getInstance().queryLakalaService(new ShoudanService.PhoneQueryListener() {
            @Override
            public void onSuccess(String phoneStr) {
                hideProgressDialog();
                if(TextUtils.isEmpty(phoneStr)){
                    PhoneNumberUtil.callPhone(AccountAboutLakalaActivity.this, phoneStr);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });


    }
}

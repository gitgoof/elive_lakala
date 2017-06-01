package com.lakala.shoudan.activity.quickArrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.register.UpdateMerchantInfoActivity;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.ui.component.NavigationBar;

/**
 *
 * 商户信息（未使用）
 * Created by WangCheng on 2016/8/19.
 */
public class UserInforActivity extends AppBaseActivity{

    private TextView tv_num,
            tv_m_name,
            tv_address,
            tv_name,
            tv_id_card,
            tv_phone,
            tv_email,
            tv_bank_name,
            tv_bank_num,
            tv_into_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("用户信息");
        navigationBar.setActionBtnText("修改");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    startActivity(new Intent(UserInforActivity.this,UpdateMerchantInfoActivity.class));
                }
            }
        });
    }
    public void initMyData(){
        tv_num= (TextView) findViewById(R.id.tv_num);
        tv_m_name= (TextView) findViewById(R.id.tv_m_name);
        tv_address= (TextView) findViewById(R.id.tv_address);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_id_card= (TextView) findViewById(R.id.tv_id_card);
        tv_phone= (TextView) findViewById(R.id.tv_phone);
        tv_email= (TextView) findViewById(R.id.tv_email);
        tv_bank_name= (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_num= (TextView) findViewById(R.id.tv_bank_num);
        tv_into_name= (TextView) findViewById(R.id.tv_into_name);

        tv_num.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        tv_m_name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName());
        tv_address.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessAddress().getAd());
        tv_name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        tv_id_card.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getMobileNum());
        tv_email.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getEmail());
        tv_bank_name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
        tv_bank_num.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo());
        tv_into_name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountName());
    }
}

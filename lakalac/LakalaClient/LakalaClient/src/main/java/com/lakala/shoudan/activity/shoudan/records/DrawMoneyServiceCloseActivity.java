package com.lakala.shoudan.activity.shoudan.records;


import android.os.Bundle;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我要提款非服务时段页面
 * 
 */
public class DrawMoneyServiceCloseActivity extends AppBaseActivity {

	private TextView openTime,closeTime;
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                ProtocalActivity.open(context, ProtocalType.D0_DESCRIPTION);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoudan_trade_draw_money_close);
		initUI();
		
	}

	protected void initUI() {
		openTime= (TextView)findViewById(R.id.tv_open_time);
		closeTime=(TextView)findViewById(R.id.tv_close_time);
		navigationBar.setTitle("立即提款");
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
	    navigationBar.setActionBtnText("业务说明");

	    initServiceTime();
	  
	}
	

    private void initServiceTime() {

        String jsonStr = getIntent().getStringExtra("jsonStr");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String begin = jsonObject.getString("beginTime");
            String end = jsonObject.getString("endTime");
            StringBuffer sb = new StringBuffer();
            sb.append(begin.substring(0,2)).append(":").append(begin.substring(2,4));
            openTime.setText(sb.toString());
            sb = new StringBuffer();
            sb.append(end.substring(0,2)).append(":").append(end.substring(2,4));
            closeTime.setText(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   
}

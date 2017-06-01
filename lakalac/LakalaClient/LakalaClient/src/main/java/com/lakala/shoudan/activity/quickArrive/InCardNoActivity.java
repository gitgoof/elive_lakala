package com.lakala.shoudan.activity.quickArrive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取银行卡号
 * Created by WangCheng on 2016/8/19.
 */
public class InCardNoActivity extends AppBaseActivity implements View.OnClickListener{

    private TextView tv_card_no;
    private String data;
    private JSONObject json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_card_no);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("获取银行卡号");
        if(!TextUtils.isEmpty(getIntent().getStringExtra("data"))){
            data=getIntent().getStringExtra("data");
            try {
                json=new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tv_card_no= (TextView) findViewById(R.id.tv_card_no);
        findViewById(R.id.tv_re_get).setOnClickListener(this);
        findViewById(R.id.bt_ok).setOnClickListener(this);
    }

    public void initMyData(){
        tv_card_no.setText(json.optString("cardno"));
    }
    Intent intent=new Intent();
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.bt_ok:
                intent.putExtra("isReget",false);
                break;
            case R.id.tv_re_get:
                intent.putExtra("isReget",true);
                break;
        }
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}

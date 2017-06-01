package com.lakala.shoudan.activity.quickArrive;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 划款详情
 * Created by WangCheng on 2016/8/26.
 */
public class ArriveDetileActivity extends AppBaseActivity{
    private TextView status,type,name,no,js_no,time,num,shou,tv_faile;
    private JSONObject jsonObject;
    private String statuss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrive_detile);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("划款详情");
        status= (TextView) findViewById(R.id.tv_status);
        type= (TextView) findViewById(R.id.tv_type);
        name= (TextView) findViewById(R.id.tv_name);
        no= (TextView) findViewById(R.id.tv_no);
        js_no= (TextView) findViewById(R.id.tv_js_no);
        time= (TextView) findViewById(R.id.tv_time);
        num= (TextView) findViewById(R.id.tv_num);
        shou= (TextView) findViewById(R.id.tv_shou);
        tv_faile= (TextView) findViewById(R.id.tv_faile);
    }
    public void initMyData() {
        if(!TextUtils.isEmpty(getIntent().getStringExtra("json"))){
            try {
                jsonObject=new JSONObject(getIntent().getStringExtra("json"));
                statuss=jsonObject.optString("remStatus");
                status.setText(jsonObject.optString("remStatusName"));
                type.setText("立即提款");
                name.setText(jsonObject.optString("merName"));
                no.setText(jsonObject.optString("shopNo"));
                js_no.setText(jsonObject.optString("remAccNo"));
                time.setText(DateUtil.formatDateStr2(jsonObject.optString("remTime")));
                num.setText(jsonObject.optString("remAmt")+"元");
                shou.setText(jsonObject.optString("remFee")+"元");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

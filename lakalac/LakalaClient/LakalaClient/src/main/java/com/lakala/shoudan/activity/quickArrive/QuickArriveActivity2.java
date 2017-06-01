package com.lakala.shoudan.activity.quickArrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.StlmRem2;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.records.DrawMoneyActivity;
import com.lakala.shoudan.activity.shoudan.records.DrawMoneyServiceCloseActivity;
import com.lakala.shoudan.adapter.QuickListErAdapter;
import com.lakala.shoudan.adapter.QuickListErAdapter2;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.ui.component.NavigationBar;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 极速到账
 * Created by WangCheng on 2016/9/1.
 */
public class QuickArriveActivity2 extends AppBaseActivity implements View.OnClickListener{

    private List<StlmRem2> list=new ArrayList<>();
    private ListView listView;
    private QuickListErAdapter2 adapter;
    private TextView tv_no,tv_num,tv_today,tv_have,tv_info,tv_ljtk;
    private double amount;
    private double accountBalance;
    private View one,two;
    private boolean isOne;
    /**
     * 今日上限
     */
    private double dLimitMax;
    /**
     * 单笔下限
     */
    private double pLimitMin;
    /**
     * 单笔上限
     */
    private double pLimitMax;

    String jsonStr;
    boolean drawEnable;
    TextView tv_tag1,tv_tag2,tv_tag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_arrive2);
        DoEnum.Do.setIsHomepage(true);
        ShoudanStatisticManager.getInstance() .onEvent(ShoudanStatisticManager.Do_Homepage, context);
        jsonStr = getIntent().getStringExtra("jsonStr");
        isOne=getIntent().getBooleanExtra("isOne",false);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            accountBalance = jsonObject.optDouble("amount");
            dLimitMax = jsonObject.optDouble("dLimitMax");
            pLimitMin = jsonObject.optDouble("pLimitMin");
            pLimitMax = jsonObject.optDouble("pLimitMax");
            amount = jsonObject.getDouble("amount");
            drawEnable = jsonObject.getBoolean("enable");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("立即提款");
        if(isOne){
            navigationBar.setTitle("一日贷");
        }
        navigationBar.setActionBtnText("业务说明");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    if(isOne){
                        ProtocalActivity.open(QuickArriveActivity2.this, ProtocalType.ONE_DAY_LOAN_NOTE);
                    }else {
                        ProtocalActivity.open(QuickArriveActivity2.this, ProtocalType.D0_DESCRIPTION);
                    }
                    ShoudanStatisticManager.getInstance() .onEvent(ShoudanStatisticManager.Do_Homepage_Explain, context);
                }
            }
        });
        listView= (ListView) findViewById(R.id.lv_content);
        tv_have= (TextView) findViewById(R.id.tv_have);
        tv_today= (TextView) findViewById(R.id.tv_tuday);
        tv_num= (TextView) findViewById(R.id.tv_num);
        tv_no= (TextView) findViewById(R.id.tv_no);
        tv_info= (TextView) findViewById(R.id.tv_info);
        tv_tag1= (TextView) findViewById(R.id.tv_tag1);
        tv_tag2= (TextView) findViewById(R.id.tv_tag2);
        tv_tag3= (TextView) findViewById(R.id.tv_tag3);
        one=findViewById(R.id.iv_one);
        two=findViewById(R.id.iv_two);
        tv_ljtk= (TextView) findViewById(R.id.tv_ljtk);
        tv_ljtk.setOnClickListener(this);
        if(isOne){
            tv_ljtk.setText("申请贷款");
            tv_tag1.setText("今日可贷金额（元）");
            tv_tag2.setText("今日已贷金额（元）");
            tv_tag3.setText("今日贷款");
        }
    }
    public void initMyData(){
        adapter=new QuickListErAdapter2(this,list);
        listView.setAdapter(adapter);
        getQuickList();//今日提款列表
        getQuick();//今日提款信息
//        getQuatInfo();//每张磁条贷记卡额度
//        getAccountBalance();//可提款金额
        tv_today.setText(new DecimalFormat("0.00").format(amount));
        tv_info.setText(String.format("单笔限额%s元,单日限额%s元", Util.formatAmount(pLimitMax),Util.formatAmount(dLimitMax)));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_ljtk:
//                new DrawButtonClickListener((AppBaseActivity) context).onClick(null);
//
                if (drawEnable) {
                    if(!TextUtils.isEmpty(DoEnum.Do.getAdvertId())){
                        String event = String.format(ShoudanStatisticManager.Do_Ad_Input, DoEnum.Do.getAdvertId());
                        ShoudanStatisticManager.getInstance().onEvent(event, context);
                    }else {
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Homepage_Input, context);
                    }
                    Intent intent2 = new Intent(this, DrawMoneyActivity.class);
                    intent2.putExtra("jsonStr",jsonStr);
                    intent2.putExtra("isOne",isOne);
                    startActivity(intent2);
                } else {
                    //非提款时段提款
                    Intent intent2 = new Intent(this, DrawMoneyServiceCloseActivity.class);
                    intent2.putExtra("jsonStr", jsonStr.toString());
                    startActivity(intent2);
                }
                break;
        }
    }

    public void getQuickList(){
        String url="v1.0/business/speedArrivalD0/cash/hisDetailQuery/status/SUCCESS";
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,url, HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        JSONArray js=jsonObject.getJSONArray("datas");
                        list.clear();
                        for (int i=0;i<js.length();i++){
                            JSONObject json=js.getJSONObject(i);
                            list.add(new StlmRem2(json));
                        }
                        adapter.notifyDataSetChanged();
                        LogUtil.print("notifyDataSetChanged");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

    public void getQuick(){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/business/speedArrivalD0/cash/hisQuery", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    LogUtil.print("onSuccess");
                    try {
                        JSONArray js=new JSONArray(resultServices.retData);
                        for (int i=0;i<js.length();i++){
                            LogUtil.print("add:"+i);
                            JSONObject json=js.getJSONObject(i);
                            if("02".equals(json.optString("remStatus"))){
                                tv_have.setText(json.optString("remAmt"));
                                tv_num.setText(json.optString("remAmt")+"元");
                                tv_no.setText(json.optString("count")+"笔");
                                if("0".equals(json.optString("count"))){
                                    one.setVisibility(View.VISIBLE);
                                    two.setVisibility(View.GONE);
                                }else {
                                    one.setVisibility(View.GONE);
                                    two.setVisibility(View.VISIBLE);
                                }
                            }else {
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

    //请求额度信息
    public void getQuatInfo(){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/business/speedArrivalD0/bindCard/getBindQuatInfo", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    LogUtil.print("onSuccess");
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

    private void getAccountBalance(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject retData = new JSONObject(resultServices.retData.toString());
                        amount = retData.getDouble("amount");
                        tv_today.setText(new DecimalFormat("0.00").format(amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                tv_today.setText(new DecimalFormat("0.00").format(amount));
            }
        };
        CommonServiceManager.getInstance().getAccountBalance(callback);
    }
}

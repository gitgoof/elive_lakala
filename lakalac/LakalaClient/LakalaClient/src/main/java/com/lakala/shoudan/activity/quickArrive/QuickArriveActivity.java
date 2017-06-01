package com.lakala.shoudan.activity.quickArrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.StlmRem;
import com.lakala.platform.bean.StlmRem2;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.register.UpdateMerchantInfoActivity;
import com.lakala.shoudan.adapter.QuickListAdapter;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.util.UIUtils;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 极速到账（弃用）
 * Created by WangCheng on 2016/8/21.
 */
public class QuickArriveActivity extends AppBaseActivity{
    private ListView listView;
    private QuickListAdapter adapter;
    private List<StlmRem> list=new ArrayList<>();
    private TextView tv_have,tv_tuday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_arrive);
        initMyView();
        initMyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuickList();
        getCollectionAmount();
        getQuatInfo();
    }

    public void initMyView(){
        navigationBar.setTitle("极速到账");
        navigationBar.setActionBtnText("业务说明");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
//                    startActivity(new Intent(QuickArriveActivity.this,UpdateMerchantInfoActivity.class));
                }
            }
        });

        listView= (ListView) findViewById(R.id.lv_content);
        tv_have= (TextView) findViewById(R.id.tv_have);
        tv_tuday= (TextView) findViewById(R.id.tv_tuday);
    }

    public void initMyData(){
        list.add(new StlmRem("划款成功",0,"0.00"));
        list.add(new StlmRem("划款失败",0,"0"));
        list.add(new StlmRem("金额",0,"0.00"));
        adapter=new QuickListAdapter(this,list);
        listView.setAdapter(adapter);
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
                        list.get(2).setRemAmt(jsonObject.optString("totalQutaCout"));
                        list.get(2).setJsonObject(jsonObject);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

    public void getQuickList(){
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
                                list.get(0).setStlmRem(json);
                                tv_have.setText(json.optString("remAmt"));
                            }else {
                                list.get(1).setStlmRem(json);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

    private void getCollectionAmount() {
        CommonServiceManager.getInstance().queryTodayCollection(new CommonServiceManager.TodayCollectionCallback() {
            @Override
            public void onSuccess(Double amount) {
                updateCollectionAmount(amount);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                updateCollectionAmount(null);
            }
        });
    }

    private void updateCollectionAmount(Double amount) {
        DecimalFormat format = new DecimalFormat("0.00");
        tv_tuday.setText(format.format(amount == null?0:amount));
    }
}

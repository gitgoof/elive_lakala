package com.lakala.shoudan.activity.quickArrive;

import android.os.Bundle;
import android.text.Html;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.BankCard;
import com.lakala.shoudan.adapter.CardListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定银行卡列表
 * Created by WangCheng on 2016/8/19.
 */
public class CardListActivity extends AppBaseActivity{

    private TextView tv_content;
    private ListView lv_content;
    private List<BankCards> list_card=new ArrayList<>();
    private CardListAdapter adapter;
    private String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("极速到账设置");
        tv_content= (TextView) findViewById(R.id.tv_content);
        lv_content= (ListView) findViewById(R.id.lv_content);
    }
    public void initMyData(){
        adapter=new CardListAdapter(this,list_card);

        lv_content.setAdapter(adapter);
        try {
            json=getIntent().getStringExtra("json");
            JSONObject jsonObject=new JSONObject(json);
            String htmlText = "<font color=#8E979D size=20px>"+"每添加一张本人磁条贷记卡，D0额度可提升"+
                    "<font color=#8E979D size=20px>"+jsonObject.optString("baseQutaCout")+".00"+
                    "<font color=#8E979D size=20px>"+"元,额度最多可达"+
                    "<font color=#8E979D size=20px>"+jsonObject.optString("addBaseQutaCout")+".00"+
                    "<font color=#8E979D size=20px>"+"元"+
                    "</font>";
            tv_content.setText(Html.fromHtml(htmlText));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardList();
    }

    public void getCardList(){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(this,"v1.0/business/speedArrivalD0/bindCard/getCardList", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    LogUtil.print("onSuccess");
                    list_card.clear();
                    try {
                        JSONArray js=new JSONArray(resultServices.retData);
                        for (int i=0;i<js.length();i++){
                            LogUtil.print("add:"+i);
                            JSONObject json=js.getJSONObject(i);
                            BankCards bank=new BankCards(json);
                            list_card.add(bank);
                        }
                        LogUtil.print("notifyDataSetChanged");
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(CardListActivity.this,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(CardListActivity.this,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }

}

package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.DateUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.NoRecordsFragment;
import com.lakala.shoudan.activity.wallet.bean.TransDetail;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTransDetailRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fengx on 2015/11/30.
 */
public class WalletTransDetailActivity extends AppBaseActivity{

    private PullToRefreshListView detail;
    private TransRecordAdapter adapter;
    private ArrayList<TransDetail> data = new ArrayList<TransDetail>();
    private String jsonString;
    private boolean isFinished = false;
    public static int page = 1;
    private NoRecordsFragment noRecordsFragment;
    private FrameLayout content;
    private boolean isFist=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_trans_detail);
        jsonString = getIntent().getStringExtra("data");
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("收支明细");
        detail = (PullToRefreshListView) findViewById(R.id.wallet_detail);
        content = (FrameLayout) findViewById(R.id.wallet_detail_content);
        content.setVisibility(View.GONE);
        noRecordsFragment = new NoRecordsFragment();
        Bundle args = new Bundle();
        args.putBoolean("isWallet",true);
        noRecordsFragment.setArguments(args);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.wallet_detail_content,noRecordsFragment);
        transaction.commit();

        adapter = new TransRecordAdapter(data,this);
        detail.setAdapter(adapter);
        detail.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        detail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isFinished) {
                    toast("查询完毕");
                    cancelRefresh();
                } else {
                    getTransDetail();
                }
            }
        });
        detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Balance_of_Payment_Details_Into,WalletTransDetailActivity.this);
                Intent intent = new Intent(WalletTransDetailActivity.this, WalletTransDetailItemActivity.class);
                int i = position - 1;
                intent.putExtra("detail",data.get(i));
                startActivity(intent);
            }
        });

        initData(jsonString);

    }

    private void cancelRefresh(){

        detail.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail.onRefreshComplete();
            }
        }, 1000);
    }
    private void getTransDetail(){
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_DETAIL_QRY);
        WalletTransDetailRequest params = new WalletTransDetailRequest(this);
        params.setPage(page);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                cancelRefresh();
                if (resultServices.isRetCodeSuccess()) {

                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        page++;
                        if (page >= jsonObject.optInt("totalPage")){
                            isFinished = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isFist=false;
                    initData(resultServices.retData);
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                detail.onRefreshComplete();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }


    private void initData(String str){

        try {
            JSONObject json = new JSONObject(str);
            JSONArray jsonArray = json.getJSONArray("datas");
            if (jsonArray.length() == 0&&isFist==true){
                detail.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }else {
                detail.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TransDetail transDetail = new TransDetail();
                    transDetail.setTranAmount(jsonObject.optString("transAmt"));
                    transDetail.setTransDate(DateUtil.formatDate(jsonObject.optString("hostDate")));
                    transDetail.setTransTime(DateUtil.formatTime(jsonObject.optString("hostTime")));
                    transDetail.setTransRes(jsonObject.optString("traceRES"));
                    transDetail.setWalletBalance(jsonObject.optString("transAcbl"));
                    transDetail.setTransName(jsonObject.optString("transName"));
                    transDetail.setTransRetdesc(jsonObject.optString("transRetdesc"));
                    transDetail.setTransType(jsonObject.optString("transType"));
                    data.add(transDetail);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;   //page置1
    }
}

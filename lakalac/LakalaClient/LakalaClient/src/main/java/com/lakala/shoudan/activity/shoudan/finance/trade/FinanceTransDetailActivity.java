package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.FrontPageActivity;
import com.lakala.shoudan.activity.shoudan.finance.adapter.AllProdAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetail;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TransDetailRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public class FinanceTransDetailActivity extends AppBaseActivity {

    public static final int PAGESIZE = 20;
    FrameLayout content;
    public static int PAGECOUNT = 0;

    private NoRecordsFragment noRecordsFragment;
    private TransRecordFragment transRecordFragment;
    private List<TransDetailProInfo> proList = new ArrayList<TransDetailProInfo>();
    private AllProdAdapter adapter;
    private View viewBg;
    private int isDefaultId = 0;
    private JSONObject response;
    private boolean backToFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_trans_detail);
        backToFront = getIntent().getBooleanExtra("backToFront",false);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        content = (FrameLayout) findViewById(R.id.content);
        viewBg = findViewById(R.id.view_bg);
        //默认隐藏遮罩层
        viewBg.setVisibility(View.GONE);
        proList = (List<TransDetailProInfo>) getIntent().getSerializableExtra("proList");
        isDefaultId = getIntent().getIntExtra("position",0);
        try {
            response = new JSONObject(getIntent().getStringExtra("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navigationBar.setTitle(proList.get(isDefaultId).getProName());
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.title) {
                    showPopupWindow(isDefaultId);
                } else if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                }
            }
        });
        changeFragment(response, isDefaultId,proList.get(isDefaultId).getProductId());
//        getAllProd();
    }

    @Override
    public void onBackPressed() {
        if (backToFront) {
            FinanceRequestManager.getInstance().startFinance(
                    FinanceTransDetailActivity.this, new HttpResponseListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
//                            TransactionManager.getInstance().clearActivityStack();
                            FinanceRequestManager.getInstance().isFinanceChange = true;
                            toFrontPage();
                        }

                        @Override
                        public void onErrorResponse() {
                            toFrontPage();
                        }
                    }
            );
        } else {
            super.onBackPressed();
        }
    }

    private void toFrontPage() {
        BusinessLauncher.getInstance().clearTop(FrontPageActivity.class);
    }

    private void getRecord(final String productId,final int position) {

        TransDetailRequest request = new TransDetailRequest();
        request.setPageCount(String.valueOf(PAGECOUNT));
        request.setPageSize(String.valueOf(PAGESIZE));
        request.setProductId(productId);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()){

                    changeFragment(responseData,position,productId);
                }else{
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toast(R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getFinancialRecord(request, listener);
    }

    public void changeFragment(JSONObject responseData,int position,String productId){

        JSONArray jsonArray = null;
        try {
            jsonArray = responseData.getJSONArray("TradeList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray.length() > 0){
            PAGECOUNT++;
        }
        if (responseData.optInt("TotalCount") > 0){


            transRecordFragment = new TransRecordFragment();
            navigationBar.toggleTitleRightDrawable();
            ArrayList<TransDetail> lst = parseJson(responseData);
            Bundle bundle = new Bundle();
            bundle.putSerializable("recordList",lst);
            bundle.putSerializable("productId",productId);
            transRecordFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content,transRecordFragment);
            transaction.commit();
        }else{
            navigationBar.setTitle(proList.get(position).getProName());
            noRecordsFragment = new NoRecordsFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content, noRecordsFragment);
            transaction.commit();
        }
    }

    private void showPopupWindow(final int position){
        proList.get(position).setTick(true);
        navigationBar.toggleTitleRightDrawable();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_popupwindow, null);
        ListView lvPro = (ListView) view.findViewById(R.id.lv_prod);
        adapter = new AllProdAdapter(this,proList);

        lvPro.setAdapter(adapter);
        final PopupWindow popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        viewBg.setVisibility(View.VISIBLE);
        popupWindow.showAsDropDown(navigationBar);

        lvPro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                popupWindow.dismiss();
                proList.get(position).setTick(false);
                adapter.notifyDataSetChanged();

                //点击标题查询，PAGECOUNT置0
                PAGECOUNT = 0;
                isDefaultId = i;
                navigationBar.setTitle(proList.get(isDefaultId).getProName());
                getRecord(proList.get(i).getProductId(), isDefaultId);
                }
//            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                navigationBar.toggleTitleRightDrawable();
                viewBg.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PAGECOUNT = 0;
    }

    public ArrayList<TransDetail> parseJson(JSONObject responseData){

        ArrayList<TransDetail> lst = new ArrayList<TransDetail>();
        try {
            JSONArray jsonArray = responseData.getJSONArray("TradeList");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);

                TransDetail detail = new TransDetail();
                detail.setAmount(object.optDouble("Amount"));
                detail.setDay(object.optString("Day"));
                detail.setMoneyGo(object.optString("MoneyGo"));
                detail.setProdName(object.optString("ProdName"));
                detail.setSid(object.optString("Sid"));
                detail.setTradeName(object.optString("TradeName"));
                detail.setTradeTime(object.optString("TradeTime"));
                detail.setTradeType(object.optString("TradeType"));
                lst.add(detail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.print("理财产品记录解析json错误");
        }

        return lst;
    }
}

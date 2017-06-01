package com.lakala.shoudan.activity.integral.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.integral.IntegralController;
import com.lakala.shoudan.activity.integral.callback.CashRecordListCallback;
import com.lakala.shoudan.adapter.CashRecordListAdapter;
import com.lakala.shoudan.datadefine.CashRecordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linmq on 2016/6/14.
 */
public class IntegralBillActivity extends AppBaseActivity {
    private RadioGroup radioTabs;
    private HashMap<Integer,CashRecordList.Type> tabTypeMap = new HashMap<Integer, CashRecordList.Type>();
    private PullToRefreshListView lvRecord;
    private View emptyView;
    private List<CashRecordList.Detail> mData = new ArrayList<CashRecordList.Detail>();
    private CashRecordListAdapter mAdapter;
    private CashRecordList.Type checkedType;
    private int pageIndex = 1;
    private int totalPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_bill);
        initUI();
    }

    protected void initUI() {
        navigationBar.setTitle("账单明细");
        initTab();
        initList();
    }

    private void initList() {
        lvRecord = (PullToRefreshListView)findViewById(R.id.listview_record);
        emptyView = findViewById(R.id.emptyView);
        ((TextView)emptyView.findViewById(R.id.textView)).setText("暂无明细");
        mAdapter = new CashRecordListAdapter(context,mData);
        lvRecord.setAdapter(mAdapter);
        lvRecord.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lvRecord.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //TODO 上拉加载更多
                if (pageIndex < totalPage) {
                    pageIndex++;
                    getCashRecordList();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void initTab() {
        tabTypeMap.put(R.id.tab1, CashRecordList.Type.TYPE1);
        tabTypeMap.put(R.id.tab2, CashRecordList.Type.TYPE2);
        tabTypeMap.put(R.id.tab3, CashRecordList.Type.TYPE3);
        radioTabs = (RadioGroup)findViewById(R.id.radioTabs);
        for(Integer tabId:tabTypeMap.keySet()){
            CashRecordList.Type type = tabTypeMap.get(tabId);
            RadioButton radioButton = (RadioButton) radioTabs.findViewById(tabId);
            radioButton.setText(type.getDesc());
        }
        radioTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedType = tabTypeMap.get(checkedId);
                pageIndex = 1;
                getCashRecordList();
            }
        });
        showProgressWithNoMsg();
        ((RadioButton)radioTabs.findViewById(R.id.tab1)).setChecked(true);
    }

    private void getCashRecordList() {
        CashRecordListCallback callback = new CashRecordListCallback() {
            @Override
            public void onResult(ResultServices resultServices,
                                 CashRecordList cashRecordList) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    List<CashRecordList.Detail> detailList = cashRecordList.getDetailList();
                    totalPage = cashRecordList.getTotal();
                    if(detailList == null){
                        return;
                    }
                    if(pageIndex == 1){
                        mData.clear();
                    }
                    mData.addAll(detailList);
                    mAdapter.notifyDataSetChanged();
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,getString(R.string.socket_fail));
            }
        };
        IntegralController.getInstance(context)
                          .getCashRecordList(checkedType,pageIndex,callback);
    }
}

package com.lakala.shoudan.activity.shoudan.records;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by LMQ on 2015/5/22.
 * 提款记录
 */
public class DrawMoneyHistoryActivity extends AppBaseActivity {

    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            }
        }
    };
    private static final String[] TABS_TXT = {"今日", "近七日", "近两周"};
    private static final int[] TABS_VIEW_ID = {R.id.tab1, R.id.tab2, R.id.tab3};
    private static final int DEFAULT_CHECKED = R.id.tab1;
    private RadioGroup tabs;
    private PullToRefreshExpandableListView historyList;
    private Map<String, List<CashHistory>> data;
    private HistoryListAdapter mAdapter;
    private View emptyView;

    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_history);
        data = new HashMap();
        initUI();
//        tabs.check(DEFAULT_CHECKED);
        ((RadioButton) findViewById(DEFAULT_CHECKED)).setChecked(true);
    }

    private void updateDebugData() {
        String[] titles = {"D+0", "T+1", "D+1"};
        Random random = new Random();
        CashHistory his = null;
        data.clear();
        for (int i = 0; i < 3; i++) {
            String title = titles[random.nextInt(3)];
            if (i == 0) {
                title = titles[1];
            }
            his = new CashHistory();
            his.setAmount(12346);
            his.setStatusName("划款成功");
            CashHistory.OperatorType type = CashHistory.OperatorType.valueOfByKey(title);
            his.setOperatorType(type);
            his.setTradeDate("2015/05/23");
            String mapKey = type.getChineseDesc();
            if (!data.containsKey(mapKey) || data.get(mapKey) == null) {
                data.put(mapKey, new ArrayList<CashHistory>());
            }
            data.get(mapKey).add(his);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("划款记录");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        tabs = (RadioGroup) findViewById(R.id.radioTabs);

        int length = TABS_VIEW_ID.length;
        for (int i = 0; i < length; i++) {
            RadioButton tab = (RadioButton) tabs.findViewById(TABS_VIEW_ID[i]);
            tab.setText(TABS_TXT[i]);
        }
        tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentPage = 1;
                data.clear();
                tabChanged(checkedId);
            }
        });

        emptyView = findViewById(R.id.layout_empty);
        ((TextView) emptyView.findViewById(R.id.textView)).setText("暂无划款记录");
        historyList = (PullToRefreshExpandableListView) findViewById(R.id.history_list);
        mAdapter = new HistoryListAdapter(this, data, historyList);
        historyList.getRefreshableView().setAdapter(mAdapter);
        historyList.getRefreshableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                //设置为group不可点击
                return true;
            }
        });
        historyList.getRefreshableView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if (tabs != null)
                    getEvent(tabs.getCheckedRadioButtonId());
                Intent intent = new Intent(DrawMoneyHistoryActivity.this,
                        TransferMoneyDetailsActivity.class);
                intent.putExtra(Constants.IntentKey.TRANSFER_INTENT_KEY, mAdapter.getChild(groupPosition, childPosition));
                startActivity(intent);
                return false;
            }
        });
        historyList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        historyList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                isRefresh = true;
                tabChanged(tabs.getCheckedRadioButtonId());

            }
        });
    }

    private boolean isRefresh;

    private void getEvent(int checkedId) {
        String event = "";
        switch (checkedId) {
            case R.id.tab1:
                event = ShoudanStatisticManager.TradeRecord_HuaKuanTodayDetail;
                break;
            case R.id.tab2:
                event = ShoudanStatisticManager.TradeRecord_HuaKuanSevenDayDetail;
                break;
            case R.id.tab3:
                event = ShoudanStatisticManager.TradeRecord_HuaKuanTwoweeksDetail;
                break;
        }
        ShoudanStatisticManager.getInstance().onEvent(event, context);
    }

    private void tabChanged(int checkedId) {
        int searchDays = 1;
        switch (checkedId) {
            case R.id.tab1: {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_HuaKuanToday, this);
                searchDays = 1;
                break;
            }
            case R.id.tab2: {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_HuaKuanSevenDay, this);
                searchDays = 7;
                break;
            }
            case R.id.tab3: {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_HuaKuanTwoweeks, this);
                searchDays = 14;
                break;
            }
        }
        getHistoryData(searchDays);
    }


    private void getHistoryData(final int searchDays) {
        showProgressWithNoMsg();
        Calendar calendar = Calendar.getInstance();
        Calendar endDate = calendar;
        int dayAgo = searchDays - 1;
        Calendar beginDate = Calendar.getInstance();
        beginDate.setTimeInMillis(endDate.getTimeInMillis());
        beginDate.add(Calendar.DAY_OF_MONTH, -dayAgo);
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                historyList.onRefreshComplete();
                anylizeCashHisResult(resultServices);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                historyList.onRefreshComplete();
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getCashHis(beginDate, endDate, currentPage, callback);
    }

    private void anylizeCashHisResult(final ResultServices resultForService) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                if (resultForService.isRetCodeSuccess()) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultForService.retData.toString());
                        int length = jsonArray == null ? 0 : jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String type = jsonObject.getString("operatorType");
                            String date = jsonObject.getString("tradeDate");
                            String tradeTime = jsonObject.getString("tradeTime");
                            String sid = jsonObject.getString("sid");
                            double amount = jsonObject.getDouble("amount");
                            double feeAmount = jsonObject.optDouble("feeAmount", 0);
                            String status = jsonObject.getString("status");
                            String statusName = jsonObject.getString("statusName");
                            String shopNo = jsonObject.getString("shopNo");
                            CashHistory his = new CashHistory();
                            String date1 = Util.formatDateStrToPattern(date, "yyyyMMdd", "yyyy/MM/dd");
                            String date2 = Util.formatDateStrToPattern(tradeTime, "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss");
                            his.setTradeDate(date1);
                            his.setTradeTime(date2);
                            CashHistory.OperatorType operatorType = CashHistory.OperatorType
                                    .valueOfByKey(type);
                            his.setOperatorType(operatorType);
                            his.setStatusName(statusName);
                            his.setStatus(status);
                            his.setAmount(amount);
                            his.setFeeAmount(feeAmount);
                            his.setSid(sid);
                            his.setShopNo(shopNo);

                            if (operatorType == CashHistory.OperatorType.NULL) {
                                continue;
                            }
                            String mapKey = operatorType.getChineseDesc();
                            if (!data.containsKey(mapKey) || data.get(mapKey) == null) {
                                data.put(mapKey, new ArrayList<CashHistory>());
                            }
                            if (isRefresh) {
                                for (int j = 0; j < data.get(mapKey).size(); j++) {
                                    if (!data.get(j).contains(his)) {
                                        data.get(mapKey).add(his);
                                    }
                                }
                                isRefresh = false;
                            } else {
                                data.get(mapKey).add(his);
                            }
                        }
                        currentPage++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (resultForService != null) {
                    ToastUtil.toast(DrawMoneyHistoryActivity.this,resultForService.retMsg);
                }
                historyList.setEmptyView(emptyView);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class HistoryListAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private LayoutInflater mInflater = null;
        private Map<String, List<CashHistory>> mData;
        private PullToRefreshExpandableListView mExpandableListView;

        HistoryListAdapter(Context context, Map<String, List<CashHistory>> data,
                           PullToRefreshExpandableListView expandableListView) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mData = data;
            mExpandableListView = expandableListView;
        }

        @Override
        public int getGroupCount() {
            return mData == null ? 0 : mData.keySet().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<CashHistory> list = mData.get(getGroupKey(groupPosition));
            return list == null ? 0 : list.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return getGroupKey(groupPosition);
        }

        private String getGroupKey(int groupPosition) {
            ArrayList<String> groups = new ArrayList<String>(mData.keySet());
            return groups.get(groupPosition);
        }

        @Override
        public CashHistory getChild(int groupPosition, int childPosition) {
            return mData.get(getGroupKey(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            TextView groupText;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.adapter_drawhistory_group_layout, null);
                groupText = (TextView) convertView.findViewById(R.id.groupText);
                convertView.setTag(groupText);
            } else {
                groupText = (TextView) convertView.getTag();
            }
            groupText.setText(getGroup(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            ChildHolder holder = null;
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = mInflater.inflate(R.layout.adapter_drawhistory_child_layout, null);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            CashHistory his = getChild(groupPosition, childPosition);
            holder.time.setText(his.getTradeDate());
            holder.amount.setText(new StringBuffer().append("￥").append(Util.formatAmount(String
                    .valueOf(his.getAmount()))).append("元"));
            holder.content.setText(his.getStatusName());
            return convertView;
        }

        class ChildHolder {
            TextView time, content, amount;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            expandAllGroup();
        }

        /**
         * 展开所有group
         */
        public void expandAllGroup() {
            int groupCount = getGroupCount();
            for (int i = 0; i < groupCount; i++) {
                mExpandableListView.getRefreshableView().expandGroup(i);
            }
        }
    }
}

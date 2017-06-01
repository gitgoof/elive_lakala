package com.lakala.shoudan.activity.shoudan.records;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengxuan on 2015/12/28.
 */
public class TradeListFragment extends Fragment {

    public static int page = 1;
    private PullToRefreshListView listView;
    private TradeAdapter adapter;
    private boolean isFinished = false;
    private String startTime, endTime, code;
    private TradeQueryActivity tradeQueryActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trade_list, null);
        tradeQueryActivity = (TradeQueryActivity) getActivity();
        Bundle bundle = getArguments();

        if (page == TradeQueryActivity.tradeInfo.getTotalPage()) {
            isFinished = true;
        }
        startTime = bundle.getString("start");
        endTime = bundle.getString("end");
        code = bundle.getString("code");

        initList(view);
        return view;
    }

    private void initList(View view) {

        adapter = new TradeAdapter(getActivity(), TradeQueryActivity.tradeInfo);
        listView = (PullToRefreshListView) view.findViewById(R.id.trade_list);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isFinished) {
                    cancelRefresh();
                    ToastUtil.toast(getActivity(), "查询完毕");

                } else {
                    query();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                intent.putExtra("which", i);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });

    }

    private void cancelRefresh() {

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        }, 1000);
    }

    private void showDialog() {
        if (tradeQueryActivity != null) {
            tradeQueryActivity.showProgressWithNoMsg();
        }
    }

    private void hideDialog() {

        if (tradeQueryActivity != null) {
            tradeQueryActivity.hideProgressDialog();
        }
    }


    private void query() {

        page++;
        if (!startTime.contains("00:00:00")) {//避免重复添加
            startTime += " 00:00:00";
        }
        if (!endTime.contains("23:59:59")) {//避免重复添加
            endTime += " 23:59:59";
        }
        showDialog();
        ShoudanService.getInstance().queryTradeRecords(false, TradeListFragment.page, startTime, endTime, code, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideDialog();
                cancelRefresh();
                if (resultServices.isRetCodeSuccess()) {
                    try {

                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        TradeQueryInfo info = new TradeQueryInfo();
                        info = info.parseObject(jsonObject);
                        if (page >= info.getTotalPage()) {
                            isFinished = true;
                        }
                        TradeQueryActivity.tradeInfo.getDealDetails().addAll(info.getDealDetails());
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(getActivity(), resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideDialog();
                ToastUtil.toast(getActivity(), R.string.socket_fail);
                cancelRefresh();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        page = 1;
    }
}

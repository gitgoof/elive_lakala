package com.lakala.shoudan.activity.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.wallet.RedPackageActivity;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;
import com.lakala.shoudan.activity.wallet.request.RedPackageRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengxuan on 2016/1/18.
 */
public class UnuseFragment extends Fragment{

    private PullToRefreshListView listView;
    private RedPackAdapter adapter;
    private RedPackageInfo redPackageInfo = new RedPackageInfo();
    private boolean isFinished = false;
    private int page = 2;
    private RedPackageActivity redPackageActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_unuse,null);
        redPackageActivity = (RedPackageActivity) getActivity();
        listView = (PullToRefreshListView) view.findViewById(R.id.listview);
        String jsonString = getArguments().getString("jsonString");
        redPackageInfo = json2Info(jsonString);
        if (redPackageInfo.getGiftList().size() == 0){
            redPackageActivity.replace(0);
            return null;
        }
        adapter = new RedPackAdapter(getActivity(),redPackageInfo, RedPackAdapter.Type.UNUSE);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isFinished){
                    cancelRefresh();
                    ToastUtil.toast(getActivity(), "查询完毕");

                }else {
                    queryGift();
                }
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFinished = false;
        page = 2;
    }

    private void cancelRefresh(){

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        }, 1000);
    }

    /**
     * 查询红包信息
     */
    private void queryGift(){

        showDialog();
        BusinessRequest request = RequestFactory.getRequest(getActivity(), RequestFactory.Type.RED_PACKAGE_QRY);
        RedPackageRequest params = new RedPackageRequest(getActivity());
        params.setPage("" + page);//红包分页编号
        params.setBusid(WalletHomeActivity.RED_PACKAGE_BUSID);
        params.setGiftStat("0");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideDialog();
                cancelRefresh();
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    RedPackageInfo info = json2Info(data);
                    if (info.getGiftList().size() == 0){
                        isFinished = true;
                    }else {
                        page += 1;
                    }
                    redPackageInfo.getGiftList().addAll(info.getGiftList());
                    adapter.notifyDataSetChanged();

                } else {
                    ToastUtil.toast(getActivity(), resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                hideDialog();
                ToastUtil.toast(getActivity(),getString(R.string.socket_fail));
                cancelRefresh();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }

    private void showDialog(){
        if (redPackageActivity != null){
            redPackageActivity.showProgressWithNoMsg();
        }
    }

    private void hideDialog(){

        if (redPackageActivity != null){
            redPackageActivity.hideProgressDialog();
        }
    }

    private RedPackageInfo json2Info(String jsonString){
        RedPackageInfo info = new RedPackageInfo();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            info.paseObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return info;
        }
        return info;
    }
}

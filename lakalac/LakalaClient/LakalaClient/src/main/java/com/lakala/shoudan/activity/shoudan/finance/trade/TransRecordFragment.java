package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetail;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TransDetailRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/12.
 */
public class TransRecordFragment extends BaseFragment {

    private ListView lvRecord;
    private TransRecordAdapter adapter;
    private ArrayList<TransDetail> data;
    private String productId;
    private FinanceTransDetailActivity activity;
    private boolean isLast = false;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finance_trans_record,null);
        lvRecord = (ListView) view.findViewById(R.id.lv_record);
        dialog = DialogCreator.createDialogWithMessage(getActivity(),"正在查询更多信息");
        initData(getArguments());
        activity = (FinanceTransDetailActivity) getActivity();
        return view;
    }

    public void initData(Bundle bundle){

        data = (ArrayList<TransDetail>) bundle.getSerializable("recordList");
        productId = bundle.getString("productId");
        adapter = new TransRecordAdapter(data,getActivity());
        lvRecord.setAdapter(adapter);

        lvRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int visibleItemCount;
            int visibleLastIndex;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                int itemsLastIndex = adapter.getCount() - 1;	//数据集最后一项的索引
                int lastIndex = itemsLastIndex;
                //当滚动到最后一行并且停止滚动，这时去加载数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                    if(isLast){
                        //加载完毕
                        ToastUtil.toast(getActivity(),"全部加载完成");
                    }else{
                        getRecord(productId);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//                //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
//                //visibleItemCount：当前能看见的列表项个数（小半个也算）
//                //totalItemCount：列表项共数
//                visibleLastIndex = firstVisibleItem + visibleItemCount;
//                //当滚动到最后一行

                this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount -1;
            }
        });
    }

    private void getRecord(String productId) {

        TransDetailRequest request = new TransDetailRequest();
        request.setPageCount(String.valueOf(FinanceTransDetailActivity.PAGECOUNT));
        request.setPageSize(String.valueOf(FinanceTransDetailActivity.PAGESIZE));
        request.setProductId(productId);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                dialog.dismiss();
                if (returnHeader.isSuccess()){
                    try {
                        JSONArray jsonArray = responseData.getJSONArray("TradeList");
                        if (jsonArray.length() > 0){
                            FinanceTransDetailActivity.PAGECOUNT++;
                        }else{
                            isLast = true;
                        }
                        try {
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

                                data.add(detail);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtil.toast(getActivity(),returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                dialog.dismiss();
                ToastUtil.toast(getActivity(),R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getFinancialRecord(request, listener);
    }
}

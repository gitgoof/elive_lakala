package com.lakala.shoudan.activity.communityservice.creditpayment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.CardInfo;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.OneIconTwoTextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUASHO on 2015/1/19.
 * 信用卡还款—记录的历史还款信用卡卡号
 */
public class PaymentHistoryRecordCardActivity extends AppBaseActivity implements
         AdapterView.OnItemClickListener {


    public String cardno;//信用卡卡号
    private List<CardInfo> dataList = new ArrayList<CardInfo>();
    private ListView listView;
    private InnerListAdapter adapter;
    private LinearLayout listLayout;
    private TextView tipsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history_record_card);
        mContext = this;
        initUI();
        requestData();
    }

    protected void initUI() {
        navigationBar.setTitle(R.string.history_remit_card);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    setResult(RESULT_CANCELED, null);
                    finish();
                }
            }
        });
        tipsText = (TextView) findViewById(R.id.tips_text);
        listView = (ListView) findViewById(R.id.lv_history_list);
//        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        adapter = new InnerListAdapter(this);
        listView.setAdapter(adapter);
        listLayout = (LinearLayout) findViewById(R.id.ll_list_layout);
        listLayout.setVisibility(View.GONE);
    }

    /**
     * 获取信用卡卡号历史记录
     */
    private void requestData() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(this, RequestFactory.Type.BANK_LIST_CREDIT);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        int length = 0;
                        if(jsonArray != null){
                            length = jsonArray.length();
                        }
                        for (int i = 0; i < length; i++) {
                            String json = jsonArray.getString(i);
                            if(TextUtils.isEmpty(json)){
                                continue;
                            }
                            CardInfo info = JSON.parseObject(json, CardInfo.class);
                            dataList.add(info);
                        }
                        if(length != 0){
                            listLayout.setVisibility(View.VISIBLE);
                            tipsText.setVisibility(View.GONE);
                        }else{
                            listLayout.setVisibility(View.GONE);
                            tipsText.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = resultServices.retMsg;
                    if(TextUtils.isEmpty(msg)){
                        msg = getString(R.string.socket_fail);
                    }
                    ToastUtil.toast(PaymentHistoryRecordCardActivity.this,msg);
                    listLayout.setVisibility(View.GONE);
                    tipsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(PaymentHistoryRecordCardActivity.this,getString(R.string.socket_fail));
                listLayout.setVisibility(View.GONE);
                tipsText.setVisibility(View.VISIBLE);
            }
        });
        request.execute();
    }

//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//        new AlertDialog.Builder(this).setItems(new String[]{
//                                                       getString(R.string.jiaoyi_jilu_delete)},
//                                               new DialogInterface.OnClickListener() {
//                                                   @Override
//                                                   public void onClick(DialogInterface dialog,
//                                                                       int which) {
//                                                       delete(position);
//                                                   }
//                                               }).create().show();
//        return false;
//    }
    private void delete(final int position){
        CardInfo data = dataList.get(position);
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.DELETE_CARD_CREDIT);
        request.setProgressMessage("正在删除...");
        request.setAutoShowProgress(true);
        request.setRequestURL(request.getRequestURL().replace("{cardId}",data.getCardId()));
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                }else{
                    String msg = resultServices.retMsg;
                    if(TextUtils.isEmpty(msg)){
                        msg = getString(R.string.socket_fail);
                    }
                    ToastUtil.toast(PaymentHistoryRecordCardActivity.this,msg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(PaymentHistoryRecordCardActivity.this,getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardInfo info = adapter.getItem(position);
        Intent data = new Intent();
        data.putExtra(UniqueKey.CREDIT_CARD_NUMBER,info.getCardNo());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * listview适配器
     */
    class InnerListAdapter extends BaseAdapter {
        private Context context;

        public InnerListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public CardInfo getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CardInfo data = dataList.get(position);
            OneIconTwoTextView itemLayout = null;
            if (convertView == null) {
                convertView = RelativeLayout
                        .inflate(context, R.layout.adapter_item_credit_history, null);
                itemLayout = (OneIconTwoTextView)convertView;
                itemLayout.getIcon().setVisibility(View.GONE);
                convertView.setTag(itemLayout);
            } else {
                itemLayout = (OneIconTwoTextView)convertView.getTag();
            }
            String cardNo = data.getCardNo();
            String cardNumberString = "";
            if(!TextUtils.isEmpty(cardNo)){
                cardNumberString = CardUtil.formatCardNumberWithSpace(cardNo);
            }
            itemLayout.setTopText(cardNumberString);
            itemLayout.setBottomTextVisibility(View.GONE);
            return convertView;
        }
    }
}

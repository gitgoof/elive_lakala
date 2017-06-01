package com.lakala.shoudan.activity.communityservice.phonerecharge;

import android.content.Context;
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
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.UserMobileChargeHis;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HUASHO on 2015/1/22.
 * 手机充值 : 历史充值记录
 */
public class RechargeHistoryRecordDataActivity extends AppBaseActivity implements
        AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener{
    private List<UserMobileChargeHis> dataList = null;
    private TextView tipsText;
    private LinearLayout listLayout;
    private ListView listView;
    private List<Map<String, String>> lists;
    private DataAdapter adapter = null;
    private String info = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_history_record_data);
        initUI();
        getHistory();
    }

    protected void initUI() {
        navigationBar.setTitle(R.string.history_recharge);
        dataList = new ArrayList<UserMobileChargeHis>();
        navigationBar.setTitle(R.string.history_recharge);
        listView = (ListView) findViewById(R.id.data_list);
        listView.setOnItemClickListener(this);
//        listView.setOnItemLongClickListener(this);

        tipsText=(TextView) findViewById(R.id.tips_text);
        listLayout=(LinearLayout) findViewById(R.id.list_layout);
        listLayout.setVisibility(View.GONE);
        adapter = new DataAdapter(this);
        listView.setAdapter(adapter);

        lists = new ArrayList<>();
    }
    private void setHasData(boolean hasData){
        if(hasData){
            tipsText.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
        }else{
            tipsText.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取充值记录
     */
    private void getHistory() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(this, RequestFactory.Type.MOBILE_CHARGE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        hideProgressDialog();
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        int length = 0;
                        if (jsonArray != null) {
                            length = jsonArray.length();
                        }
                        for (int i = 0; i < length; i++) {
                            String json = jsonArray.getString(i);
                            if (TextUtils.isEmpty(json)) {
                                continue;
                            }
                            UserMobileChargeHis userMobileChargeHis = JSON
                                    .parseObject(json, UserMobileChargeHis.class);
                            dataList.add(userMobileChargeHis);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(RechargeHistoryRecordDataActivity.this, getString(R.string.socket_fail));
                        hideProgressDialog();
                    }
                }
                getHistoryOver();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                getHistoryOver();
                ToastUtil.toast(RechargeHistoryRecordDataActivity.this, getString(R.string.socket_fail));
                hideProgressDialog();
            }
        });
        request.execute();
    }
    private void getHistoryOver(){
        listView.setEmptyView(tipsText);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        if(adapter.getCount() != 0){
            setHasData(true);
        }else{
            setHasData(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserMobileChargeHis itemData = adapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("phone",itemData.getMobileNo());
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //长按删除（收款宝无此功能）
//        new AlertDialog.Builder(this).setItems(new String[]{
//                        getString(R.string.jiaoyi_jilu_delete)},
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        delete(position);
//                    }
//                }).create().show();
        return false;
    }

//    private void delete(final int position){
//        UserMobileChargeHis itemData = adapter.getItem(position);
//        BusinessRequest request = RequestFactory.getRequest(this,
//                                                            RequestFactory.Type.MOBILE_CHARGE_DELETE);
//        request.setRequestURL(request.getRequestURL().replace("{mobileId}",String.valueOf(itemData.getMobileId())));
//        request.setProgressMessage("正在删除...");
//        request.setAutoShowProgress(true);
//        request.setResponseHandler(new ServiceResultCallback() {
//            @Override
//            public void onSuccess(ResultServices resultServices) {
//                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
//                    dataList.remove(position);
//                    adapter.notifyDataSetChanged();
//                    ToastUtil.toast(RechargeHistoryRecordDataActivity.this,"删除成功");
//                }else{
//                    String msg = resultServices.retMsg;
//                    if(TextUtils.isEmpty(msg)){
//                        msg = getString(R.string.http_error);
//                    }
//                    ToastUtil.toast(RechargeHistoryRecordDataActivity.this,msg);
//                }
//            }
//
//            @Override
//            public void onEvent(HttpConnectEvent connectEvent) {
//                ToastUtil.toast(RechargeHistoryRecordDataActivity.this,getString(R.string.http_error));
//            }
//        });
//        request.execute();
//    }

    class DataAdapter extends BaseAdapter {

        private Context context;

        public DataAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public UserMobileChargeHis getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserMobileChargeHis itemData = dataList.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = RelativeLayout
                        .inflate(context, R.layout.listitem_remittance_history_recordcard, null);
                holder.leftText = (TextView) convertView
                        .findViewById(R.id.id_combination_left_text);
                holder.rightText = (TextView)convertView.findViewById(R.id.id_combination_middle_text);
                holder.divider = convertView.findViewById(R.id.id_combination_right_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            int cnt = getCount();
            holder.leftText.setText(itemData.getMobileNo());
//            String mark = itemData.getMobileMark();
//            if (!TextUtils.isEmpty(mark)) {
//                holder.leftText.setText(mark);
//            } else {
//
//            }

            return convertView;
        }
        class ViewHolder{
            public TextView leftText;
            public TextView rightText;
            public View divider;
        }

    }
}

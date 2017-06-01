package com.lakala.shoudan.activity.communityservice.transferremittance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.ImageUtil;
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
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.OneIconTwoTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUASHO on 2015/1/21.
 * 转帐汇款 : 历史收款人卡号
 */
public class RemittanceHistoryRecordCardActivity extends AppBaseActivity {
    private TextView tv_tips;
    private LinearLayout ll_list_layout;
    private ListView lv_cardList;
    private CardDataAdapter adapter;
    private List<CardInfo> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_history_record_card);
        initUI();
        getHistoryCard();
    }

    protected void initUI() {
        navigationBar.setTitle("常用收款人");
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        ll_list_layout = (LinearLayout) findViewById(R.id.ll_list_layout);
        ll_list_layout.setVisibility(View.GONE);
        lv_cardList = (ListView) findViewById(R.id.lv_cardList);
        dataList = new ArrayList<CardInfo>();
        adapter = new CardDataAdapter(this);
        lv_cardList.setAdapter(adapter);
    }

    private void delete(final int position){
        CardInfo cardInfo = dataList.get(position);
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.DELETE_CARD_TRANSFER);
        request.setProgressMessage("正在删除...");
        request.setAutoShowProgress(true);
        request.setRequestURL(request.getRequestURL().replace("{cardId}", cardInfo.getCardId()));
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    serviceError(resultServices);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(RemittanceHistoryRecordCardActivity.this,getString(R.string.socket_fail));
            }
        });
        request.execute();
    }
    private void serviceError(ResultServices resultServices){
        String msg = resultServices.retMsg;
        if(TextUtils.isEmpty(msg)){
            ToastUtil.toast(RemittanceHistoryRecordCardActivity.this,
                            String.valueOf(resultServices.retCode));
        }else{
            ToastUtil.toast(RemittanceHistoryRecordCardActivity.this,msg);
        }
    }

    /**
     * 获取历史转账账户
     */
    private void getHistoryCard() {
        BusinessRequest request = RequestFactory
                .getRequest(this, RequestFactory.Type.BANK_LIST_TRANSFER);
        showProgressWithNoMsg();
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        int length = jsonArray.length();
                        if (length <= 0) {
                            tv_tips.setVisibility(View.VISIBLE);
                            return;
                        }
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CardInfo info = JSON.parseObject(jsonObject.toString(), CardInfo.class);
                            dataList.add(info);
                        }
                        adapter.notifyDataSetChanged();
                        ll_list_layout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    serviceError(resultServices);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        request.execute();
    }

    /**
     * 历史收款人卡号适配器
     */
    class CardDataAdapter extends BaseAdapter {
        private Context context;

        public CardDataAdapter(Context context) {
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

        private boolean setIcon(OneIconTwoTextView layout,Bitmap bm){
            if(bm != null && layout != null){
                layout.setIconBitmap(bm);
                return true;
            }
            return false;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CardInfo info = getItem(position);
            OneIconTwoTextView itemLayout;
            if (convertView == null) {
                convertView = LinearLayout.inflate(context, R.layout.adapter_item_remittance_history, null);
                itemLayout = (OneIconTwoTextView) convertView;
                convertView.setTag(itemLayout);
            } else {
                itemLayout = (OneIconTwoTextView) convertView.getTag();
            }
            //设置icon
            String bankCode = info.getBankCode();
            Bitmap bitmap = ImageUtil.getBitmapInAssets(context,
                                                        "bank_icon/"+bankCode + "" +
                                                                ".png");
            if(!setIcon(itemLayout,bitmap)){
                String subCode = info.getSubBankFullNameCode();
                bitmap = ImageUtil.getBitmapInAssets(context,
                                                     "bank_icon/"+subCode + "" +
                                                             ".png");
                setIcon(itemLayout,bitmap);
            }

            itemLayout.setTopText(info.getAccountName());
            itemLayout.setBottomText(Util.formatCardNumberWithStar(info.getCardNo()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardInfo info = adapter.getItem(position);
                    Intent data = new Intent();
                    data.putExtra(UniqueKey.KEY_CARDINFO,info);
                    setResult(RESULT_OK,data);
                    finish();
                }
            });
//            convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    new android.app.AlertDialog.Builder(RemittanceHistoryRecordCardActivity.this)
//                            .setItems(new String[]{
//                                              getString(R.string.jiaoyi_jilu_delete)},
//                                      new DialogInterface.OnClickListener() {
//                                          @Override
//                                          public void onClick(DialogInterface dialog, int which) {
//                                              delete(position);
//                                          }
//                                      }).create().show();
//                    return false;
//                }
//            });
            return convertView;
        }
    }
}

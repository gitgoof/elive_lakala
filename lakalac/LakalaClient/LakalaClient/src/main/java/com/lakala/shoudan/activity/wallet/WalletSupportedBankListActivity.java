package com.lakala.shoudan.activity.wallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.util.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱包添加的银行卡列表
 * Created by HJP on 2015/12/15.
 */
public class WalletSupportedBankListActivity extends AppBaseActivity{

    private WalletSuppoortedListAdapter adapter;
    protected ListView lvBanks;
    protected List<JSONObject> data = new ArrayList<JSONObject>();
    private int mRequestCode;
    public static void open(Activity context, String bankListString, int requestCode){
        Intent intent = new Intent(context,WalletSupportedBankListActivity.class);
        intent.putExtra(Constants.IntentKey.BANKLIST_STRING, bankListString);
        intent.putExtra("requestCode", requestCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_banks);
        initUI();
        initData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("银行卡列表");
        lvBanks = (ListView)findViewById(R.id.lv_banks);
        adapter=new WalletSuppoortedListAdapter(data);
        lvBanks.setAdapter(adapter);
        //需求修改，支持银行列表数据不做点击效果
//        lvBanks.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position,
//                                            long id) {
//                        Intent intent = new Intent();
//                        intent.putExtra("data", adapter.getItem(position).toString());
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//                }
//        );
    }

    protected void initData() {
        String json = getIntent().getStringExtra(Constants.IntentKey.BANKLIST_STRING);
        mRequestCode=getIntent().getIntExtra("requestCode",0);
        if(TextUtils.isEmpty(json)){
            return;
        }
        try {
            JSONObject responseData = new JSONObject(json);
            JSONArray jsonArray = responseData.getJSONArray("List");
            if(jsonArray == null || jsonArray.length() == 0){
                return;
            }
            data.clear();
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                data.add(jsonObject);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class WalletSuppoortedListAdapter extends BaseAdapter{

        private String type;
        private List<JSONObject> data = null;
        public WalletSuppoortedListAdapter(List<JSONObject> data){
            this.data=data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public JSONObject getItem(int position) {
            if(position >= 0 && position < data.size()){
                return data.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_wallet_supported_bank_list,null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_icon);
                holder.tvText = (TextView)convertView.findViewById(R.id.tv_text);
                holder.tvSupportedCardType=(TextView)convertView.findViewById(R.id.tv_supported_card_type);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            JSONObject item = getItem(position);
            holder.tvText.setText(item.optString("bankName", ""));

            //设置icon
            Bitmap bitmap = com.lakala.library.util.ImageUtil.getBitmapInAssets(context,
                    "bank_icon/" + item.optString("bankCode", "")+ "" +
                            ".png");
            holder.ivIcon.setImageBitmap(bitmap);


            String assetsUri = new StringBuilder().append(item.optString("bankCode", "")).append(".png")
                    .toString();
            Drawable iconDrawable = ImageUtil.getDrawbleFromAssets(parent.getContext(), assetsUri);
            if(iconDrawable != null){
                holder.ivIcon.setBackgroundDrawable(iconDrawable);
            }else{
                UILUtils.displayBackground(item.optString("BankImg"), holder.ivIcon);
            }
            //显示支持卡的类型，信用卡、储蓄卡
            type=item.optString("accountType");
            if (TextUtils.isEmpty(type)){
                LogUtil.print("accountType=null");
            }else{
                if(type.equals("1")&&!(mRequestCode==0x2360)){
                    holder.tvSupportedCardType.setVisibility(View.VISIBLE);
                    holder.tvSupportedCardType.setText("*仅支持储蓄卡");
                }else if(type.equals("2")&&!(mRequestCode==0x2360)){
                    holder.tvSupportedCardType.setVisibility(View.VISIBLE);
                    holder.tvSupportedCardType.setText("*仅支持信用卡");
                }else{
                    holder.tvSupportedCardType.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ivIcon;
            TextView tvText;
            TextView tvSupportedCardType;
        }
    }
}

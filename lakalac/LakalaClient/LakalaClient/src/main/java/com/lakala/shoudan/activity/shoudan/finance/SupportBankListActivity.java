package com.lakala.shoudan.activity.shoudan.finance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.adapter.SupportListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/10/19.
 */
public class SupportBankListActivity extends AppBaseActivity {

    protected ListView lvBanks;
    private SupportListAdapter adapter;
    protected List<JSONObject> data = new ArrayList<JSONObject>();

    public static void open(Activity context, String bankListString, int requestCode){
        Intent intent = new Intent(context,SupportBankListActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, bankListString);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_banks);
        initUI();
        initData();
    }

    protected void initData() {
        String json = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
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

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("银行列表");

        lvBanks = (ListView)findViewById(R.id.lv_banks);
        adapter = new SupportListAdapter(data);
        lvBanks.setAdapter(adapter);
        lvBanks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent intent = new Intent();
                        intent.putExtra("data", adapter.getItem(position).toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
    }
}

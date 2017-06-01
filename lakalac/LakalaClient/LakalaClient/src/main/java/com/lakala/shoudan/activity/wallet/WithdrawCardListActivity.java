package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.bean.AccountInfo;
import com.lakala.shoudan.activity.wallet.bean.SimpleWithdrawCardInfo;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.ui.component.NavigationBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/25.
 */
public class WithdrawCardListActivity extends AppBaseActivity{

    private ListView listView;
    private WalletWithdrawAdapter adapter;
    private List<SimpleWithdrawCardInfo> dataList = new ArrayList<SimpleWithdrawCardInfo>();
    private AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_card_list);
        initUI();

    }

    @Override
    protected void initUI() {
        super.initUI();
        if (getIntent().hasExtra("fromLQ"))
            navigationBar.setTitle("选择银行卡");
        else
            navigationBar.setTitle("零钱转出");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });

        listView = (ListView) findViewById(R.id.lv_credit_card);

        accountInfo = WalletServiceManager.getInstance().getAccountInfo();
        parseAccountInfo2Simple(accountInfo.getList());
        adapter = new WalletWithdrawAdapter(this, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //如果点击添加银行卡
                if (i == dataList.size()) {
                    Intent intent = new Intent(WithdrawCardListActivity.this, WalletBindBankCardActivity.class);
                    intent.putExtra(Constants.IntentKey.ADD_CARD_TYPE, WalletTransferActivity.Type.BIND_CARD);
                    startActivityForResult(intent, WalletTransferActivity.ADD_BANK_REQUEST);
                } else {
                    Intent data = new Intent();
                    data.putExtra("position", i);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        addFooterView();
    }

    private void addFooterView(){
        View view = adapter.getView(0, null, listView);
        //view.measure(0,0);
        //int height = view.getMeasuredHeight();
        int height=100;
        if(view!=null){
            view.measure(0, 0);
            height= view.getMeasuredHeight();
        }
        View footView= LayoutInflater.from(context).inflate(R.layout.layout_transfer_add_card, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,height);
        footView.setLayoutParams(params);
        listView.addFooterView(footView);
    }

    private void parseAccountInfo2Simple(List<AccountInfo.ListEntity> entities){
        dataList.clear();
        for (int i=0;i<entities.size();i++){
            SimpleWithdrawCardInfo info = new SimpleWithdrawCardInfo();
            AccountInfo.ListEntity entity = entities.get(i);
            info.setBankCard(entity.getPayeeAcNo());
            info.setBankName(entity.getPayeeBankName());
            info.setBankType(entity.getCardTypeName());
            info.setBankCode(entity.getPayeeCoreBankId());
            dataList.add(info);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WalletTransferActivity.ADD_BANK_REQUEST && resultCode == RESULT_OK){
            accountInfo = WalletServiceManager.getInstance().getAccountInfo();
            parseAccountInfo2Simple(accountInfo.getList());
            adapter.notifyDataSetChanged();
        }
    }
}

package com.lakala.shoudan.activity.merchantmanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.adapter.MyBaseAdapter;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.BaseDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/17 0017.
 */
public class BankCardAttestationActivity extends AppBaseActivity {
    private ListView listview_bca;
    private BankAdapter adapter;
    private TextView tv_explain;
    private List<Map<String,Object>>dataList=new ArrayList<>();
    private static final double  limit  = 5000.00;//额度
    private static final double  limitPromote  = 10000.00;//可提升额度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_attestation);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.bank_card_attestation);
        listview_bca= (ListView) findViewById(R.id.listview_bca);
        tv_explain= (TextView) findViewById(R.id.tv_explain);

        String msg=getString(R.string.bank_card_l)+limit+getString(R.string.bank_card_mid)+limitPromote+getString(R.string.bank_card_r);
        SpannableStringBuilder style=new SpannableStringBuilder(msg);
        style.setSpan(new ForegroundColorSpan(Color.RED),msg.indexOf(String.valueOf(limit)),msg.indexOf(String.valueOf(limit))+String.valueOf(limit).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED),msg.indexOf(String.valueOf(limitPromote)),msg.indexOf(String.valueOf(limitPromote))+String.valueOf(limitPromote).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_explain.setText(style);

        dataList.add(new HashMap<String, Object>());
        adapter=new BankAdapter(context);
        ToastUtil.toast(this,dataList.size()+"");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem==NavigationBar.NavigationBarItem.back){
                    finish();
                }
            }
        });
    }

    private class BankAdapter extends MyBaseAdapter {

        public BankAdapter(Context context) {
           super(context, dataList);
        }

        @Override
        public int getItemResource() {
            return R.layout.adapter_bank_card_item;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            RelativeLayout rl_other= (RelativeLayout) convertView.findViewById(R.id.rl_other);
            LinearLayout rl_last= (LinearLayout) convertView.findViewById(R.id.rl_last);
            TextView tv_bank_add_name= (TextView) convertView.findViewById(R.id.tv_bank_add_name);
            TextView tv_bank_add_type= (TextView) convertView.findViewById(R.id.tv_bank_add_type);
            TextView tv_bank_add_num= (TextView) convertView.findViewById(R.id.tv_bank_add_num);
            String tag="";
            if (dataList.size()-1==position){
                tag="last";
                rl_last.setVisibility(View.VISIBLE);
                rl_other.setVisibility(View.GONE);

            }else {
                tag="nolast";
               /* tv_bank_add_name.setText(dataList.get(position).get("").toString());
                tv_bank_add_type.setText(dataList.get(position).get("").toString());
                tv_bank_add_num.setText(dataList.get(position).get("").toString());*/
                rl_other.setVisibility(View.VISIBLE);
                rl_last.setVisibility(View.GONE);
            }
            final String finalTag = tag;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalTag.equals("last")){
                        showMyDialog();
                    }else {
                        toAddBank();
                    }
                }
            });
            return convertView;
        }
    }
    private void showMyDialog() {
        String msg="删除已绑定的银行卡，您的秒到额度将减少10000.00元，确定删除？";
        BaseDialog dialog = DialogCreator.createAlertDialog(context,"提示",msg,new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index){
                    case 0:
                        ToastUtil.toast(context,""+index);
                    break;

                    case 1:
                        ToastUtil.toast(context,""+index);
                    break;
                }
            }
        },"取消","确定");
    }

    private void toAddBank() {
        Intent view = new Intent(this,BindBankCardActivity.class);
        startActivity(view);
    }

}

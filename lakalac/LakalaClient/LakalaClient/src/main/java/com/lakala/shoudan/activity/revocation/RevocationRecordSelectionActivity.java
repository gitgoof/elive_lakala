package com.lakala.shoudan.activity.revocation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.TimeCounter;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.ScancodeCancelActivity;
import com.lakala.shoudan.activity.shoudan.records.DealDetails;

import java.util.List;

/**
 * Created by More on 15/3/15.
 */
public class RevocationRecordSelectionActivity extends FragmentActivity {

    private TextView revocationComfirm;//Next Step
    private CheckBox preView;//存放上次点击的list的子 v
    private int selection = -1;
    private ImageView ivCancel;
    private List<DealDetails> dealDetailsList;
    public static DealDetails selectedDetais = new DealDetails();
    private String bankName;
    private String cardName;
    private boolean isScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        isScan = getIntent().getBooleanExtra("isScan", false);
        if (isScan){
            dealDetailsList = ScancodeCancelActivity.dealDetails;
        }else{
            dealDetailsList = RevocationPaymentActivity.dealDetails;
            bankName = getIntent().getStringExtra("bankName");
            cardName = getIntent().getStringExtra("cardName");
        }
        setContentView(R.layout.actvity_revocation_selection);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeCounter.getInstance().may2Gesture(this);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TimeCounter.getInstance().save2BackgroundTime(this);
    }

    protected void initUI(){

        ListView revocationListView = (ListView) findViewById(R.id.revocation_list);

        //查询可以撤销的交易记录
        //queryRecords();


//        revocationRecords = (List<RevocationRecord>)getIntent().getSerializableExtra(RevocationPaymentActivity.TRANS_RECORDS);

        revocationListView.setAdapter(new RevocationListAdapter(RevocationRecordSelectionActivity.this, dealDetailsList));

        /** 设置Item点击事件 */
        revocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int itemIndex, long arg3) {

//                if(preView != null&&selection!=itemIndex){
//                    preView.setChecked(false);
//                }
                CheckBox selected = (CheckBox) v.findViewById(R.id.if_selected);
                selected.setChecked(true);
                preView = selected;
//                boolean ischecked=selected.isChecked();
//                selected.setChecked(!ischecked);
//                if(selected.isChecked()){
//                    selection = itemIndex;
//                }else {
//                    selection=-1;
//                }
//                setButtonEnable();

                Intent intent = getIntent();
                selectedDetais = dealDetailsList.get(itemIndex);
                setResult(RESULT_OK);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 200);
            }
        });

        TextView tvBankName = (TextView) findViewById(R.id.tv_card);

        //扫码撤销不显示顶部的卡信息
        if (isScan){

            tvBankName.setVisibility(View.GONE);
        }else {

            tvBankName.setVisibility(View.VISIBLE);
            DealDetails dealDetails = dealDetailsList.get(0);

            String cardType = cardName;
            String bankName = this.bankName;

            String cardNo = "";
            String str = dealDetails.getPaymentAccount();
            char[] cardNos = str.toCharArray();
            int length = cardNos.length;
            if (length > 4){
                cardNo  = str.substring(length - 4,length);
            }

            tvBankName.setText(StringUtil.formatRevocationLastFour(cardNo,bankName,cardType));
        }

        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * 格式化银行的格式  例子：邮政储蓄(023534) 返回邮政储蓄
     * @param bankWithBankCode
     * @return
     */
    public static String formatBankWithBankCode(String bankWithBankCode){

        if(TextUtils.isEmpty(bankWithBankCode)){
            return "";
        }

        if(bankWithBankCode.indexOf("(") == -1){

            return bankWithBankCode;
        }

        return bankWithBankCode.substring(0, bankWithBankCode.indexOf("("));
    }

    private Handler handler = new Handler();


    private void setButtonEnable(){
//        if(selection>-1){
//            revocationComfirm.setEnabled(true);
//        }else {
//            revocationComfirm.setEnabled(false);
//        }
    }

    /**
     *
     * 撤销列表适配器
     *
     * @author More
     *
     */
    private class RevocationListAdapter extends BaseAdapter {

        private List<DealDetails> recordInfoList;

        private Context context;

        private boolean firstInit = true;

        public RevocationListAdapter(Context context,
                                     List<DealDetails> list) {
            this.recordInfoList = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return recordInfoList != null ? recordInfoList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = LinearLayout.inflate(context, R.layout.revocation_list_item, null);

//            holder.cardNo = (TextView) convertView.findViewById(R.id.card_no);//银行卡号
//            holder.mrchName = (TextView) convertView.findViewById(R.id.mrch_name);//商户名
            holder.time = (TextView) convertView.findViewById(R.id.time);//时间日期
            holder.date = (TextView) convertView.findViewById(R.id.date);//时间日期
            holder.amount = (TextView) convertView.findViewById(R.id.trans_amount);//交易金额
            holder.selector = (CheckBox) convertView.findViewById(R.id.if_selected);//是否被选中
            holder.seperator = convertView.findViewById(R.id.seperator);
//            holder.status = (TextView) convertView.findViewById(R.id.trans_status);//交易状态的
            convertView.setTag(holder);

            if(firstInit){
//				if(position == 0&&getCount()==1){
//		    		holder.selector.setChecked(true);
//	                selection = 0;
//
//		    	}
                firstInit = false;
                setButtonEnable();
            }

            DealDetails tempRecord = recordInfoList.get(position);
            /** 获取交易金额 */
            double amount = tempRecord.getDealAmount();
            holder.amount.setText(StringUtil.formatDisplayAmount(String.valueOf(amount)));
            /** 获取卡号 */
            String cardNo = tempRecord.getPaymentAccount();

            /** 获取商户名 */
            String merchName = tempRecord.getMerchantName();
            /** 获取交易时间 */
            String date = DateUtil.formatDateStr(tempRecord.getDealStartDateTime());
            if (TextUtils.isEmpty(date)){
                return convertView;
            }
            String[] times = date.split(" ");
            holder.date.setText(times[0]);
            holder.time.setText(times[1]);
            if (position == (recordInfoList.size() - 1)){
                holder.seperator.setVisibility(View.GONE);
            }else {
                holder.seperator.setVisibility(View.VISIBLE);
            }
//            /** 交易状态**/

//            holder.status.setText(tempRecord.getStatus().getName());

            return convertView;
        }

        private class ViewHolder {
            public TextView amount;
//            public TextView cardNo;
//            public TextView mrchName;
            public TextView date;
            public TextView time;
//            public TextView status;
            public CheckBox selector;
            public View seperator;
        }
    }
}

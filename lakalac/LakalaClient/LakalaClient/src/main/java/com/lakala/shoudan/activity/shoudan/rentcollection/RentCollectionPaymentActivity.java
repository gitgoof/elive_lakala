package com.lakala.shoudan.activity.shoudan.rentcollection;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by More on 14-6-26.
 */
public class RentCollectionPaymentActivity extends NewCommandProtocolPaymentActivity {

    private RentCollectionInfo rentCollectionInfo;

    private EditText editTips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rentCollectionInfo = (RentCollectionInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
//        rentCollectionInfo.isSignatureNeeded();
        navigationBar.setTitle("特约商户缴费");
        secTitle.setVisibility(View.VISIBLE);
        secTitle.setText("平安收银宝");
        addTipsTableRow();
    }

    private void addTipsTableRow(){
        confirmInfo.addSeparator();
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.table_row_edittext, null);
        TextView name = (TextView) view.findViewById(R.id.name);
        view.setBackgroundColor(getResources().getColor(R.color.white));
        name.setText("备注:");
        editTips = (EditText)view.findViewById(R.id.edit_value);
        editTips.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});

        editTips.setHint("请输入备注(32个字以内)");
        editTips.setSingleLine(true);
        confirmInfo.addView(view);
//        confirmInfo.addSeparator();
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid","1F2");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("fee", Utils.yuan2Fen("0"));
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("issms", "1");
        requestParams.put("srcsid", rentCollectionInfo.getSrcSid());
        requestParams.put("inpan", rentCollectionInfo.getInpan());
        requestParams.put("feeflag",rentCollectionInfo.getFeeFlag());
        requestParams.put("notesdesc",rentCollectionInfo.getTips());
        requestParams.put("amount", Utils.yuan2Fen(rentCollectionInfo.getAmount()));
    }

    @Override
    protected void reStartSwipe() {
        finish();
    }
}

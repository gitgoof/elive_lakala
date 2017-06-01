package com.lakala.shoudan.activity.collection;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.ui.common.Dimension;
import com.loopj.lakala.http.RequestParams;


/**
 * Created by More on 15/2/10.
 */
public class CollectionPaymentActivity extends NewCommandProtocolPaymentActivity {

    private CollectionTransInfo collectionTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectionTransInfo = (CollectionTransInfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);

        View seperator = new View(this);
        LinearLayout.LayoutParams seperatorParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, Dimension.dip2px(1, this));
        seperator.setBackgroundResource(R.drawable.lakala_divide_line);
        seperator.setLayoutParams(seperatorParams);
        confirmInfo.addView(seperator);

        TextView textView = new TextView(this);
        textView.setText("您所收款项将结算至您的收款账户");
        textView.setPadding(20,10,10,0);
        textView.setTextColor(getResources().getColor(R.color.gray_666666));
        confirmInfo.addView(textView);
        initPoint();
    }

    @Override
    protected boolean divideNeed() {
        return false;
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {

        requestParams.put("busid","18X");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("fee", Utils.yuan2Fen("0"));
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("issms", "1");
        requestParams.put("mobileno", "");
        requestParams.put("Tips", "");
        requestParams.put("amount", Utils.yuan2Fen(collectionTransInfo.getAmount()));

    }

    public void initPoint(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Collection_Home_Page;
        }else if(PublicEnum.Business.isDirection()){
        }else if(PublicEnum.Business.isAd()){
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Collection_Public_Page;
        }else {
            event=ShoudanStatisticManager.Collection_Succes_Page;
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }
}
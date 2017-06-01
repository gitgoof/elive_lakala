package com.lakala.shoudan.activity.collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.shoudan.activity.base.AmountInputActivity;

import java.math.BigDecimal;

/**
 * 
 * 金额输入
 * 
 * @author More date2014-4-25
 *
 */
public class CollectionsAmountInputActivity extends AmountInputActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayout.setVisibility(View.VISIBLE);//MCC行业选择
        showForceQpbocSwitch();
        initPoint();
    }

    @Override
    protected void onInputComplete(BigDecimal amount) {
        Intent intent = new Intent(context, CollectionPaymentActivity.class);
        CollectionTransInfo collectionTransInfo =  new CollectionTransInfo();
        collectionTransInfo.setAmount(amount.toString());
        intent.putExtra(ConstKey.TRANS_INFO, collectionTransInfo);
        startActivity(intent);
    }

    public void initPoint(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Collection_Home;
        }else if(PublicEnum.Business.isDirection()){
        }else if(PublicEnum.Business.isAd()){
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Collection_Public;
        }else {
            event=ShoudanStatisticManager.Collection_Succes;
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }
}

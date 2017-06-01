package com.lakala.shoudan.activity.shoudan.chargebusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.component.SingleTextWithRightArrowAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 14-6-27.
 */
public class ChargeBusinessSelection extends AppBaseActivity {

    private SingleTextWithRightArrowAdapter singleTextWithRightArrowAdapter;
    private ListView businessList;
    public List<String> getBusiness(){
        List<String> data = new ArrayList<>();
        return data;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_business_selection);
        initUI();
    }

    protected void initUI() {
        navigationBar.setTitle("特约商户缴费");
        businessList = (ListView)findViewById(R.id.business_list);
        singleTextWithRightArrowAdapter=new SingleTextWithRightArrowAdapter(ChargeBusinessSelection.this, getChargeBusinesses());
        businessList.setAdapter(singleTextWithRightArrowAdapter);
        businessList.setOnItemClickListener(businessItemClick);

    }

    private AdapterView.OnItemClickListener businessItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if(i == 0){
                startActivity(new Intent(ChargeBusinessSelection.this,RentCollectionAmountActivity.class));
            }else{
                startActivity(new Intent(ChargeBusinessSelection.this, ContributePaymentAmountInputActivity.class));
            }

        }
    };

    private String[] getChargeBusinesses(){

        return new String[]{
            TransactionType.RentCollection.getValue(), TransactionType.ContributePayment.getValue()};

    }

}

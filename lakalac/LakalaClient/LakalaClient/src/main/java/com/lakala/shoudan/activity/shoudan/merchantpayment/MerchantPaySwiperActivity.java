package com.lakala.shoudan.activity.shoudan.merchantpayment;

import android.os.Bundle;
import android.view.View;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.loopj.lakala.http.RequestParams;

/**
 * 缴费刷卡
 * @author zmy
 *
 */
public class MerchantPaySwiperActivity extends NewCommandProtocolPaymentActivity {

    private MerchantPayTransInfo merchantPayTransInfo;

    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid", "1CH");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("amount", Utils.yuan2Fen(merchantPayTransInfo.getAmount()));
        requestParams.put("fee", Utils.yuan2Fen(merchantPayTransInfo.getFee()));
        requestParams.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("tips", "");
        requestParams.put("secmercd", "");
        requestParams.put("secmername", "");
        requestParams.put("sectermid", "");
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("instbill", merchantPayTransInfo.getInstBill());
        requestParams.put("openarea", "");
        requestParams.put("srcsid", merchantPayTransInfo.getFeeSid());
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        merchantPayTransInfo = (MerchantPayTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        findViewById(R.id.bottom_tips).setVisibility(View.GONE);
	}



    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        super.onProcessEvent(swiperProcessState, swiperInfo);

        if(swiperProcessState == SwiperProcessState.WAITING_FOR_CARD_SWIPE){
            setTipsOnlyMsc();
        }
    }
    private void setTipsOnlyMsc(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prompt.setText("请用刷卡器刷卡……");
                tipsImage.setImageDrawable(getResources().getDrawable(R.drawable.lakala_swipe_only));
            }
        });

    }

}
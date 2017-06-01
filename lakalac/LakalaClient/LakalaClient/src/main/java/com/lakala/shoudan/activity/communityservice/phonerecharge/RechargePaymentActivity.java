package com.lakala.shoudan.activity.communityservice.phonerecharge;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by LMQ on 2015/3/15.
 */
public class RechargePaymentActivity extends NewCommandProtocolPaymentActivity {
    private MobileRechargeInfo mobileRechargeInfo;

    @Override
        protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.telphone_payment);
        mobileRechargeInfo = (MobileRechargeInfo)getIntent().getSerializableExtra(ConstKey
                                                                                        .TRANS_INFO);
    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
        super.startPayment(swiperInfo);
        String event="";
        if(PublicEnum.Business.isHome()){
            event=ShoudanStatisticManager.Phone_Recharge_Swipe_Home;
        }else if(PublicEnum.Business.isDirection()){
            event=ShoudanStatisticManager.Phone_Recharge_Swipe_De;
        }else if(PublicEnum.Business.isAd()){
            event=ShoudanStatisticManager.Phone_Recharge_Swipe_Ad;
        }else if(PublicEnum.Business.isPublic()){
            event=ShoudanStatisticManager.Phone_Recharge_Swipe_Public;
        }else {
            event=ShoudanStatisticManager.Phone_Recharge_Swipe_Success;
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }

    @Override
    protected void addTransParams(RequestParams params) {
        String mobileno = mobileRechargeInfo.getPhoneNumber();
        params.put("busid","M20020");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("mobileno",mobileno);
        params.put("mobileno2",mobileno);
        params.put("chgtype",mobileRechargeInfo.getChargeCard().getCardCode());
        params.put("amount", Utils.yuan2Fen(mobileRechargeInfo.getChargeCard().getCardName()));
        params.put("tdtm", Utils.createTdtm());
    }

}

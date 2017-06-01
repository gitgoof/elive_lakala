package com.lakala.shoudan.activity.shoudan.webmall;

import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 14-7-21.
 */
public class WebMallTransInfo extends WebCallTransInfo {

    @Override
    public boolean isSignatureNeeded() {
        return true;
    }

    @Override
    public boolean ifSupportIC() {
        return true;
    }

    @Override
    public String getTransTitle() {
        return "社区商城";
    }

    @Override
    public String getStatisticTransResult() {
        return null;
    }

    @Override
    public String getStatisticSignPage() {
        return null;
    }

    @Override
    public String getStatisticIsSend() {
        return null;
    }

    @Override
    public String getRepayName() {
        return "付款";
    }

    @Override
    public String getTransTypeName() {
        return "社区商城";
    }

    @Override
    public String getSwipeAmount() {
        return getAmount();
    }


    @Override
    public List<TransferInfoEntity> getResultInfo() {
        return getBillInfo();
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("商品名称:", productName));
        transferInfoEntities.add(new TransferInfoEntity("订单号:", fBillNo));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额:", getAmount(), true));

        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {

        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        if(!"".equals(getSysRef()) && !"null".equals(getSysRef())){
            infoEntities.add(new TransferInfoEntity("交易流水号:", getSysRef()));

        }
        infoEntities.add(new TransferInfoEntity("付款卡:", StringUtil.formatCardNumberN6S4N4(payCardNo)));
        infoEntities.add(new TransferInfoEntity("交易金额:", Util.formatDisplayAmount(getAmount()),true));
        infoEntities.add(new TransferInfoEntity("交易时间:", Util.getNowTime()));

        return infoEntities;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WebMall;
    }

    @Override
    public String getAdditionalMsg() {
        return "萌萌哒:" + "999fuck";
    }
}

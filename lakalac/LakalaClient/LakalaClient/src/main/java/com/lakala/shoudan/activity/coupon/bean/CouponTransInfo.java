package com.lakala.shoudan.activity.coupon.bean;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarCodeCollectionTransInfo;
import com.lakala.shoudan.common.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjp on 2016/6/2.
 */
public class CouponTransInfo extends BarCodeCollectionTransInfo {

    @Override
    public TransactionType getType() {
        return TransactionType.COUPON;
    }

    @Override
    public String getAdditionalMsg() {
        return "";
    }

    @Override
    public boolean isSignatureNeeded() {
        return false;
    }

    @Override
    public boolean ifSupportIC() {
        return false;
    }

    @Override
    public String getTransTitle() {
        return "扫描代金券码";
    }

    @Override
    public String getRepayName() {
        return "重新收款";
    }

    @Override
    public String getTransTypeName() {
        return "扫描代金券码";
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
        return null;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("商户名称", ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName()));
        infoEntities.add(new TransferInfoEntity("付款方式", "代金券支付"));
        infoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(getAmount()), true));
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "收款成功" : (transResult == TransResult.FAILED ? "收款失败" : "收款超时"),true));
        infoEntities.add(new TransferInfoEntity("交易时间", Util.getNowTime()));
        return infoEntities;
    }

    @Override
    public String getStatisticTransResult() {
        return null;
    }
}

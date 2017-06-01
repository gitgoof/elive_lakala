package com.lakala.shoudan.activity.shoudan.barcodecollection.revocation;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarCodeCollectionTransInfo;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.VerticalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/11/20.
 */
public class BarCodeRevocationTransInfo extends BarCodeCollectionTransInfo {

    private VerticalListView.IconType type;

    @Override
    public void setType(VerticalListView.IconType type) {
        this.type = type;
    }

    @Override
    public String getTransTitle() {
        return "扫码商户单号";
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("用户名", ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName()));
        infoEntities.add(new TransferInfoEntity("付款方式", type.getType(), type));
        infoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(getAmount()), true));
        infoEntities.add(new TransferInfoEntity("交易时间", Util.getNowTime()));
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "撤销成功" : (TransResult.FAILED == transResult ? "撤销失败" : "撤销超时"),true));
        return infoEntities;
    }

    @Override
    public String getTransTypeName() {
        return "撤销交易";
    }

    @Override
    public TransactionType getType() {
        return TransactionType.ScanRevocation;
    }
}

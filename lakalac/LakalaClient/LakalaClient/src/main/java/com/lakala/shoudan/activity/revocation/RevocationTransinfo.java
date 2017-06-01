package com.lakala.shoudan.activity.revocation;

import android.text.TextUtils;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.platform.statistic.UndoEnum;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangMY on 2015/3/13.
 */
public class RevocationTransinfo extends BaseTransInfo{

    private String srcAmount;

    public String getSrcAmount() {
        return srcAmount;
    }

    public void setSrcAmount(String srcAmount) {
        this.srcAmount = srcAmount;
    }

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
        return "撤销";
    }

    @Override
    public String getRepayName() {
        return "撤销";
    }

    @Override
    public String getTransTypeName() {
        return "撤销交易";
    }

    @Override
    public String getSwipeAmount() {
        return null;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        return getBillInfo();
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        User user = ApplicationEx.getInstance().getUser();
        TransferInfoEntity tempEntity;
        tempEntity = new TransferInfoEntity("用户名", "", user.getMerchantInfo().getBusinessName());
        infoEntities.add(tempEntity);
        String account;
        if(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountType() == AccountType.PRIVATE){//对私
            account =  CardUtil.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo());
        }else {
            account = CardUtil.formateCardNumberOfCompany(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo());
        }
        infoEntities.add(new TransferInfoEntity("结算账户", "", account));
        return infoEntities;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "撤销成功" : "结果未知"));
        infoEntities.add(new TransferInfoEntity("卡号", CardUtil.formatCardNumberWithStar(payCardNo)));
        infoEntities.add(new TransferInfoEntity("日期/时间", DateUtil.formateDateTransTime(syTm)));
        infoEntities.add(new TransferInfoEntity("交易金额", StringUtil.formatDisplayAmount(srcAmount),true));
        return infoEntities;
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        String event="";
//        if(!TextUtils.isEmpty(LargeAmountEnum.LargeAmount.getAdvertId())){//广告id不为null，从广告进入
//            event = String.format(ShoudanStatisticManager.Advert_Undo_Success, UndoEnum.Undo.getAdvertId());
//        }else{
//            event=ShoudanStatisticManager.Undo_Success;
//        }
        return event;
    }

    @Override
    public String getStatisticSignPage() {
        return ShoudanStatisticManager.Advert_Undo_Sign_Page;
    }

    @Override
    public String getStatisticIsSend() {
        return ShoudanStatisticManager.Advert_Undo_Sign_isSend;
    }


    @Override
    public TransactionType getType() {
        return TransactionType.Revocation;
    }

    @Override
    public String getAdditionalMsg() {
        return null;
    }

}

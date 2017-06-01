package com.lakala.shoudan.activity.payment.base;



import org.json.JSONObject;

import java.util.List;

/**
 * Created by More on 14-4-29.
 */
public interface TransInfoInterface {

    /**
     * 是否需要电子签名,决定跳转结果页面
     * @return
     */
    boolean isSignatureNeeded();

    boolean ifSupportIC();

    /**
     * 交易标题
     * @return
     */
    String getTransTitle();

    /**
     * 重新交易的按钮 例如  重新 + 收款(getRepayName)
     * @return
     */
    String getRepayName();

    /**
     * 交易名称
     * @return
     */
    String getTransTypeName();

    /**
     * 刷卡金额
     * @return
     */
    String getSwipeAmount();

    /**
     * 结果信息显示内容(或异常错误)
     * @return
     */
    List<TransferInfoEntity> getResultInfo();

    /**
     * 确认信息页面展示内容
     * @return
     */
    List<TransferInfoEntity> getConfirmInfo();

    /**
     * 电子签名结果页面展示内容
     * @return
     */
    List<TransferInfoEntity> getBillInfo();

    /**
     * 解析数据验证接口
     * @param jsonObject
     */
    void unpackValidateResult(JSONObject jsonObject);

    String getStatisticTransResult();
    String getStatisticSignPage();
    String getStatisticIsSend();
}

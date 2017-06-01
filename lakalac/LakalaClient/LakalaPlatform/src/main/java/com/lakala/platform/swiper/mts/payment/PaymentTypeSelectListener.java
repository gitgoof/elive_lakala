package com.lakala.platform.swiper.mts.payment;

/**
 * 支付方式选择器，事件监听
 * Created by xyz on 14-1-19.
 */
public interface PaymentTypeSelectListener{

    /**
     * 选择了列表项
     */
    public void onItemSelected(Object object);

    /**
     * 选择了钱包余额支付
     */
    public void onBalancePaySelected();

    /**
     * 选择使用快捷支付
     * @param quickCard
     */
    public void onQuickPaySelected(QuickCard quickCard);

    /**
     * 选择使用刷卡支付
     */
    public void onSwipePaySelected();

    /**
     * 选择添加快捷银行卡
     */
    public void onAddQuickCardSelected();

    /**
     * 取消选择
     */
    public void onCancel();
}


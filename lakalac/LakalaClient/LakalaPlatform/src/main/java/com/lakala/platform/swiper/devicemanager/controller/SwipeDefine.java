package com.lakala.platform.swiper.devicemanager.controller;

/**
 * Created by wangchao on 14-8-1.
 */
public class SwipeDefine {
    /**
     * 刷卡器键盘状态
     */
    public enum SwipeKeyBoard {
        YES,NO;
    }

    /**
     * 收款宝交易类型
     */
    public enum SwipeCollectionType {
        QUERY,      //查询
        CONSUMPTION;//消费
    }

    /**
     * 刷卡类型
     */
    public enum SwipeCardType {
        MAGNETIC_NORMAL,    //不带键盘磁条卡
        IC_NORMAL,          //不带键盘ic卡
        MAGNETIC_KEYBOARD,  //带键盘,磁条卡
        IC_KEYBOARD;        //带键盘,ic卡
    }

}

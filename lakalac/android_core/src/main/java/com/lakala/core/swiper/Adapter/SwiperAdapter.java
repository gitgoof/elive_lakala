package com.lakala.core.swiper.Adapter;

import android.content.Context;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;

/**
 * Created by Vinchaos api on 14-1-3.
 */
public abstract class SwiperAdapter {

    public static SwiperAdapter initWithType(ESwiperType type, Context context) {
        switch (type) {
            case Q201:
                return new SwiperAdapterQ201(context);
            case Q202:
                return new SwiperAdapterQ202(context);
            case Q203:
                return new SwiperAdapterQ203(context);
            case Q206:
                return new SwiperAdapterQ206(context);
            case PayFi:
                return new SwiperAdapterPayFi(context);
            case QV30E:
                return new SwiperAdapterQV30E(context);
            case LKLMobile:
                return new SwiperAdapterLKLMobile(context);
            case Bluetooth:
                return new SwiperAdapterBluetooth(context);
        }
        return null;
    }

    /**
     * 判断当前是否已经插入刷卡设备
     *
     * @return 插入?true:false。
     */
    public abstract boolean isDevicePresent();

    /**
     * 启动刷卡器等待用户刷卡。
     * 启动刷卡程序，将进入刷卡流程如判断设备是否插上，启动是否成功等。
     */
    public abstract void startSwiper();

    /**
     * 结束刷卡等待。
     */
    public abstract void stopSwiper();

    /**
     * 卸载刷卡驱动，释放占用的所有资源。
     */
    public abstract void deleteSwiper();

    /**
     * 获取刷卡器状态
     *
     * @return 状态值
     */
    public abstract SwiperDefine.SwiperControllerState getCSwiperState();

    /**
     * 获取 KSN
     * 刷卡器KSN中的左边14位BCD码，可以判断刷卡器是否合法设备刷卡器 (4位BCD）＋10位 系列号BCD码，共14个字符
     */
    public abstract String getKSN();

    /**
     * 获取刷卡器类型
     *
     * @return 刷卡器类型
     */
    public abstract ESwiperType getSwiperType();

    /**
     * 设置事件侦听器*
     */
    public abstract void setListener(SwiperAdapterListener listener);
}

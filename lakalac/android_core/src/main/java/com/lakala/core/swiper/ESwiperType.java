package com.lakala.core.swiper;

/**
 * Created by Vinchaos api on 14-1-3.
 * 刷卡器类型
 */
public enum ESwiperType {

    Q201("0001", SwiperDefine.SwiperPortType.TYPE_AUDIO),
    Q202("0002", SwiperDefine.SwiperPortType.TYPE_AUDIO),
    Q203("0003", SwiperDefine.SwiperPortType.TYPE_AUDIO),
    Q206("0008", SwiperDefine.SwiperPortType.TYPE_AUDIO),
    PayFi("0007", SwiperDefine.SwiperPortType.TYPE_WIFI),
    QV30E("0009", SwiperDefine.SwiperPortType.TYPE_AUDIO),
    LKLMobile("0010", SwiperDefine.SwiperPortType.TYPE_LKLMOBILE),
    Bluetooth("0011", SwiperDefine.SwiperPortType.TYPE_BLUETOOTH);

    private String id;
    private SwiperDefine.SwiperPortType portType;

    private ESwiperType(String id, SwiperDefine.SwiperPortType portType) {
        this.id = id;
        this.portType = portType;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public SwiperDefine.SwiperPortType getPortType() {
        return this.portType;
    }
}

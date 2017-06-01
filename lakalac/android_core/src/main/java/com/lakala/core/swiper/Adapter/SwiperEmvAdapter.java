package com.lakala.core.swiper.Adapter;

import com.lakala.cswiper6.bluetooth.CommandProtocolVer;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.emv.AIDConfig;
import com.newland.mtype.module.common.emv.CAPublicKey;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import java.util.Map;

/**
 * Created by wangchao on 14-4-21.
 */
public abstract class SwiperEmvAdapter extends SwiperCollectionAdapter{
    /**
     * 设置匹配的蓝牙
     * @param params
     */
    public abstract boolean setConnectParams(String[] params);

    /**
     * 添加AID
     * @param aidConfig
     */
    public abstract void addAID(AIDConfig aidConfig);

    /**
     * 删除AID
     * @param aid
     */
    public abstract void deleteAID(byte[] aid);

    /**
     * 清空AID
     */
    public abstract void clearAllAID();

    /**
     * 添加公钥
     * @param rid
     * @param capk
     */
    public abstract void addCAPublicKey(byte[] rid, CAPublicKey capk);

    /**
     * 删除公钥
     * @param rid
     * @param index
     */
    public abstract void deleteCAPublicKey(byte[] rid, int index);

    /**
     * 清空公钥
     * @param rid
     */
    public abstract void clearAllCAPublicKey(byte[] rid);

    /**
     * 二次授权
     */
    public abstract void doSecondIssuance(SecondIssuanceRequest request);

    /**
     *  应用撤销密码输入调用
     */
    public abstract void cancelPininput();

    /**
     * 应用撤销插卡或刷卡调用
     */
    public abstract void cancelCardRead();

    /**
     *
     * @param amount
     * @param cardReaderModuleTypes
     */
    public abstract void startSwiper(final String amount, final ModuleType[] cardReaderModuleTypes);

    /**
     * @param amount
     * @param cardReaderModuleTypes
     * @param businessCode
     * @param serviceCode
     * @param additionalMsg
     */
    public abstract void startSwiper(String amount, ModuleType[] cardReaderModuleTypes, final byte[] businessCode, final byte[] serviceCode, final byte[] additionalMsg);


    /**
     *
     * @return
     */
    public abstract Map<String, Object> getSwipeCardExtendData();

    /**
     * 获取设备能力
     *
     * @return
     */
    public abstract byte[] fetchProdAllocation();

    /**
     * 结束EMV
     * true : 正常结束
     * false : 中断
     *
     * @param b
     */
    public abstract void cancelEmv(boolean b);

//    public abstract String getDeviceVersion();

    public abstract void setCommandProtocolVer(CommandProtocolVer commandProtocolVer);

    public abstract CommandProtocolVer getDeviceSupportedProtocol();

    public abstract GetDeviceInfo getDeviceInfo();

    public abstract boolean isSupportNCCARD();
}

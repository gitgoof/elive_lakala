package com.lakala.core.swiper;

import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.tlv.TLVPackage;

import org.apache.commons.codec.binary.Base64;

/**
 * IC卡信息类
 * Created by xyz on 14-7-25.
 */
public class ICCardInfo {

    //ic卡磁道数据
    private String track2;

    //55域
    private String icc55;

    //IC卡交易标识 1,接触式交易;2,插卡式交易
    private String posemc;

    //IC卡序列号 23域
    private String cardSn;

    //银行卡号
    private String pan;

    //遮罩的银行卡号
    private String maskedPan;

    private boolean hasScpic55 = true;

    private EmvTransInfo emvTransInfo;

    private String icinfo;

    public String getIcinfo() {
        return icinfo;
    }

    public void setIcinfo(String icinfo) {
        this.icinfo = icinfo;
    }

    public EmvTransInfo getEmvTransInfo() {
        return emvTransInfo;
    }

    public void setEmvTransInfo(EmvTransInfo emvTransInfo) {
        this.emvTransInfo = emvTransInfo;
    }

    public boolean isHasScpic55() {
        return hasScpic55;
    }

    public void setHasScpic55(boolean hasScpic55) {
        this.hasScpic55 = hasScpic55;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getIcc55() {
        return icc55;
    }

    public void setIcc55(String icc55) {
        this.icc55 = icc55;
    }

    public String getPosemc() {
        return posemc;
    }

    public void setPosemc(String posemc) {
        this.posemc = posemc;
    }

    public String getCardSn() {
        return cardSn;
    }

    public void setCardSn(String cardSn) {
        this.cardSn = cardSn;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String createTcicc55() {
        byte[] tempIcc55 = ICFieldConstructor.createTcicc55TLV(emvTransInfo).pack();
        if (null == tempIcc55 || tempIcc55.length == 0) {
            return "";
        }
        return new String(new Base64().encode(tempIcc55));
    }
    public String createScpic55(){

        if(!hasScpic55){
            return "";
        }

        TLVPackage tempTlvPackage = ICFieldConstructor.createScpic55TLV(emvTransInfo);

        if (tempTlvPackage != null) {
            byte[] scpic55 = tempTlvPackage.pack();
            if (scpic55 == null || scpic55.length == 0)
                return "";
            else
                return new String(new Base64().encode(scpic55));
        }



        return "";//ISOUtils.hexString(ICFieldConstructor.createScpic55(emvTransInfo).pack());
    }
}

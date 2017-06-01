package com.lakala.platform.swiper.devicemanager;

import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.security.GetDeviceInfo;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.ISOUtils;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

/**
 * Created by More on 14-2-15.
 */
public class SwiperInfo implements Serializable {

    public GetDeviceInfo deviceInfo;

    /**
     * Swiper Tag
     */
    CardType cardType;
    public enum CardType{
        /**
         * IC卡
         */
        ICCard,
        /**
         * 磁条卡
         * Magnetic Stripe Card
         */
        MSC,
        /**
         * 非接卡
         */
        QPBOC,
    }

    private String typeName;

    private class CardTypeNames{

        public static final String CREDIT_CARD = "信用卡";

        public static final String DEBIT_CARD = "储蓄卡";

        public static final String UNKNOWN = "银行卡";

    }

    private String mac;

    private String macRandom;

    /**
     * 二次授权结果
     */
    private String tcValue;

    /**
     * 随机数
     */
    String rnd = "";
    /**
     * 磁道
     */
    String encTracks = "";
    /**
     * 卡号
     */
    String maskedPan = "";
    /**
     * Pin
     */
    String pin = "";
    /**
     * KSN
     */
    String ksn = "";
    /**
     * 交易类型安全码
     */
    String chntype = "";

    String posemc = "";
    /**
     * card sn 号
     */
    String cardsn = "";
    /**
     * 二磁道
     */
    String track2 = "";
    /**
     * ic 卡交易数据返回
     */
    EmvTransInfo emvTransInfo;
    /**
     * ic 卡55域
     */
    String icc55 = "";

    /**
     * 1磁道
     * @param encTracks
     */
    String track1 = "";

    boolean isNewCommandProtocol = false;

    private String icinfo;

    public String getIcinfo() {
        return icinfo;
    }

    public void setIcinfo(String icinfo) {
        this.icinfo = icinfo;
    }

    public void setEncTracks(String encTracks) {
        this.encTracks = encTracks;
    }

    public String getEncTracks() {
        return encTracks;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan.replace("E", "X").replace("F", "");
    }

    /**
     * *被替换为 X 的 pan
     *
     * @return
     */
    public String getTransTypePan() {
        return (maskedPan.replaceAll("X", "*"));
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin.toUpperCase();
    }

    public String getKsn() {
        return ksn;
    }

    public void setKsn(String ksn) {
        this.ksn = ksn;
    }

    public String getRandomNumber() {
        return rnd;
    }

    public void setRandomNumber(String rnd) {
        this.rnd = rnd;
    }

    public String getChntype() {
        return chntype;
    }

    public void setChntype(String chntype) {
        this.chntype = chntype;
    }

    public String getPosemc() {
        return posemc;
    }

    public void setPosemc(String posemc) {
        this.posemc = posemc;
    }

    public String getCardsn() {
        return cardsn;
    }

    public void setCardsn(String cardsn) {
        this.cardsn = cardsn;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public EmvTransInfo getEmvTransInfo() {
        return emvTransInfo;
    }

    public void setEmvTransInfo(EmvTransInfo emvTransInfo) {
        this.emvTransInfo = emvTransInfo;
    }

    private String icc = "";

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getTypeName() {
        return typeName;
    }

    /**
     * D== Debit Card
     * c== Credit Card
     * @param typeName
     */
    public void setTypeName(String typeName) {
       if("D".equals(typeName)){
           this.typeName = CardTypeNames.DEBIT_CARD;
       }else if("C".equals(typeName)){
           this.typeName = CardTypeNames.CREDIT_CARD;
       }else{
            this.typeName = CardTypeNames.UNKNOWN;
        }
    }

    public String getIcc55() {
        return icc55;
    }

    private String tcIcc55;

    private boolean ifRetScpic55 = false;

    public boolean isIfRetScpic55() {
        return ifRetScpic55;
    }

    public void setIfRetScpic55(boolean ifRetScpic55) {
        this.ifRetScpic55 = ifRetScpic55;
    }

    private String scpicc55;

    public String getTCValue(){
        return tcValue;
    }

    public void setTCValue(boolean b){

        if(b){
           tcValue = "0";
        }else{
            tcValue = "1";
        }

    }


    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public void secIssError(){
        icc55 = "";
        tcValue = "1";
        ifRetScpic55 = false;
    }


    public void setIcc55(String icc55) {
        this.icc55 = icc55;
    }

    public String getTcValue() {
        return tcValue;
    }

    public void setTcValue(String tcValue) {
        this.tcValue = tcValue;
    }

    public String getRnd() {
        return rnd;
    }

    public void setRnd(String rnd) {
        this.rnd = rnd;
    }

    public String getIcc() {
        return icc;
    }

    public void setIcc(String icc) {
        this.icc = icc;
    }

    public String getTcIcc55() {
        return tcIcc55;
    }

    public void setTcIcc55(String tcIcc55) {
        this.tcIcc55 = tcIcc55;
    }

    public String getScpicc55() {
        return scpicc55;
    }

    public void setScpicc55(String scpicc55) {
        this.scpicc55 = scpicc55;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMacRandom() {
        return macRandom;
    }

    public void setMacRandom(String macRandom) {
        this.macRandom = macRandom;
    }

    public boolean isNewCommandProtocol() {
        return isNewCommandProtocol;
    }

    public void setIsNewCommandProtocol(boolean isNewCommandProtocol) {
        this.isNewCommandProtocol = isNewCommandProtocol;
    }

    public GetDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(GetDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public String toString() {
        return "SwiperInfo{" +
                "cardType=" + cardType +
                ", typeName='" + typeName + '\'' +
                ", mac='" + mac + '\'' +
                ", macRandom='" + macRandom + '\'' +
                ", tcValue='" + tcValue + '\'' +
                ", rnd='" + rnd + '\'' +
                ", encTracks='" + encTracks + '\'' +
                ", maskedPan='" + maskedPan + '\'' +
                ", pin='" + pin + '\'' +
                ", ksn='" + ksn + '\'' +
                ", chntype='" + chntype + '\'' +
                ", posemc='" + posemc + '\'' +
                ", cardsn='" + cardsn + '\'' +
                ", track2='" + track2 + '\'' +
                ", emvTransInfo=" + emvTransInfo +
                ", icc55='" + icc55 + '\'' +
                ", track1='" + track1 + '\'' +
                ", isNewCommandProtocol=" + isNewCommandProtocol +
                ", icc='" + icc + '\'' +
                ", tcIcc55='" + tcIcc55 + '\'' +
                ", ifRetScpic55=" + ifRetScpic55 +
                ", scpicc55='" + scpicc55 + '\'' +
                '}';
    }
}

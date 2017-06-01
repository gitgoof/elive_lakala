package com.lakala.platform.swiper.mts;

import com.lakala.core.swiper.ICCardInfo;

/**
 * Created by wangchao on 14-8-4.
 */
public class CardInfo {

    //磁条卡
    public static final String MAGNETIC_CARD = "1";
    //ic卡
    public static final String IC_CARD       = "2";

    /**加密后的磁道数据*/
    public String Otrack;
    /**加密后的 Pin*/
    public String Pinkey;
    /**pin原文长度*/
    public int PinLength;
    /**随机数*/
    public String Random;
    /**卡类型 1 磁条卡 ，2 IC卡*/
    public String CardType;
    /**IC卡磁道数据*/
    public String Track2;
    /**55域数据*/
    public String Icc55;
    /**IC 卡交易标识, 1 接触式交易 2 非接式交易*/
    public String Posemc;
    /**IC 卡序列号*/
    public String CardSn;
    /**银行卡号*/
    public String Pan;
    /**银行卡号（遮罩）*/
    public String MaskPan;
    /**KSN*/
    public String KSN;
    /**渠道标识 收款宝：'02101'，其它可不送,默认为02102*/
    public String ChnType;
    /**是否是磁条卡交易*/
    public boolean isMagneticCard;
    /**刷卡器键盘支持*/
    public SwipeDefine.SwipeKeyBoard swipeKeyBoard;
    /**EmvTransINfo*/
    public Object emvTransInfo;

    /**
     * 空构造
     */
    public CardInfo(){}

    /**
     * 提供磁条卡使用
     *
     * @param encTracks
     * @param randomNumber
     * @param maskedPANString
     */
    public CardInfo(String encTracks,
                    String randomNumber,
                    String maskedPANString,
                    SwipeDefine.SwipeKeyBoard swipeKeyBoard){
        this.Otrack         = encTracks;
        this.Random         = randomNumber;
        this.MaskPan        = maskedPANString;
        this.CardType       = MAGNETIC_CARD;
        this.swipeKeyBoard  = swipeKeyBoard;
        //设置ChnType默认值02102
        this.ChnType        = "02102";
    }

    /**
     * 提供IC卡使用
     *
     * @param icCardInfo
     */
    public CardInfo(ICCardInfo icCardInfo, SwipeDefine.SwipeKeyBoard swipeKeyBoard){
        this.Track2         = icCardInfo.getTrack2();
        this.Icc55          = icCardInfo.getIcc55();
        this.Posemc         = icCardInfo.getPosemc();
        this.CardSn         = icCardInfo.getCardSn();
        this.Pan            = icCardInfo.getPan();
        this.MaskPan        = icCardInfo.getMaskedPan();
        this.CardType       = IC_CARD;
        this.swipeKeyBoard  = swipeKeyBoard;
        this.emvTransInfo   = icCardInfo.getEmvTransInfo();
        //设置ChnType默认值02102
        this.ChnType        = "02102";
    }

    public Object getEmvTransInfo() {
        return emvTransInfo;
    }

    public void setEmvTransInfo(Object emvTransInfo) {
        this.emvTransInfo = emvTransInfo;
    }

    public SwipeDefine.SwipeKeyBoard getSwipeKeyBoard() {
        return swipeKeyBoard;
    }

    public void setSwipeKeyBoard(SwipeDefine.SwipeKeyBoard swipeKeyBoard) {
        this.swipeKeyBoard = swipeKeyBoard;
    }

    public String getOtrack() {
        return Otrack;
    }

    public void setOtrack(String otrack) {
        Otrack = otrack;
    }

    public String getPinkey() {
        return Pinkey;
    }

    public void setPinkey(String pinkey) {
        Pinkey = pinkey;
    }

    public int getPinLength() {
        return PinLength;
    }

    public void setPinLength(int pinLength) {
        PinLength = pinLength;
    }

    public String getRandom() {
        return Random;
    }

    public void setRandom(String random) {
        Random = random;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getTrack2() {
        return Track2;
    }

    public void setTrack2(String track2) {
        Track2 = track2;
    }

    public String getIcc55() {
        return Icc55;
    }

    public void setIcc55(String icc55) {
        Icc55 = icc55;
    }

    public String getPosemc() {
        return Posemc;
    }

    public void setPosemc(String posemc) {
        Posemc = posemc;
    }

    public String getCardSn() {
        return CardSn;
    }

    public void setCardSn(String cardSn) {
        CardSn = cardSn;
    }

    public String getPan() {
        return Pan;
    }

    public void setPan(String pan) {
        Pan = pan;
    }

    public String getMaskPan() {
        return MaskPan;
    }

    public void setMaskPan(String maskPan) {
        MaskPan = maskPan;
    }

    public String getKSN() {
        return KSN;
    }

    public void setKSN(String KSN) {
        this.KSN = KSN;
    }

    public String getChnType() {
        return ChnType;
    }

    public void setChnType(String chnType) {
        ChnType = chnType;
    }

    public boolean isMagneticCard() {
        return isMagneticCard;
    }

    public void setMagneticCard(boolean isMagneticCard) {
        this.isMagneticCard = isMagneticCard;
    }
}

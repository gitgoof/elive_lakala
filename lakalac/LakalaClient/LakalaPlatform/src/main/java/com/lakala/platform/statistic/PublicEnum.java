package com.lakala.platform.statistic;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/11/22.
 */
public enum PublicEnum {
    Business;
    boolean isPublic = false;
    boolean isDirection = false;
    boolean isHome = false;
    String isAd = "";
    boolean isWithdrawal=false;//是否是立即提款的弹框点击

    public boolean isWithdrawal() {

        return isWithdrawal;
    }

    public void setWithdrawal(boolean withdrawal) {
        isWithdrawal = withdrawal;
        isPublic = false;
        isAd = "";
        isDirection = false;
        isHome = false;
        PayForYouEnum.PayForYou.close();
    }

    public boolean isAd() {
        if (!TextUtils.isEmpty(isAd)) {
            return true;
        } else {
            return false;
        }
    }

    public String getIsAd() {
        return isAd;
    }

    public void setIsAd(String isAd) {
        this.isAd = isAd;
        isWithdrawal=false;
        isPublic = false;
        isDirection = false;
        isHome = false;
        PayForYouEnum.PayForYou.close();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean pblic) {
        isPublic = pblic;
        isAd = "";
        isWithdrawal=false;
        isDirection = false;
        isHome = false;
        PayForYouEnum.PayForYou.close();
    }


    public boolean isDirection() {
        return isDirection;
    }

    public void setDirection(boolean direction) {
        isDirection = direction;
        isAd = "";
        isWithdrawal=false;
        isPublic = false;
        isHome = false;
        PayForYouEnum.PayForYou.close();
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
        isAd = "";
        isWithdrawal=false;
        isPublic = false;
        isDirection = false;
        PayForYouEnum.PayForYou.close();
    }

    public void Close() {
        isHome = false;
        isAd = "";
        isWithdrawal=false;
        isPublic = false;
        isDirection = false;
    }
}

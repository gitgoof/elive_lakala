package com.lakala.platform.statistic;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

public class PublicToEvent {
    public static boolean messeage=false;
    public static int tag=0;
    public static void setMesseage(){
        PublicEnum.Business.setHome(false);
        PublicEnum.Business.setPublic(false);
        messeage=true;
    }
    /*
      贷款 买点
     */
    public static void normalEvent( String end, Context context){
        if (TextUtils.isEmpty(end))
            return;
        String[] loan_Top = {"首页公共业务", "首页定向业务", "首页广告"};//从首页公共业务进入贷款
        String event="";
        if (PublicEnum.Business.isPublic()){
            event=loan_Top[0];
        }else  if (PublicEnum.Business.isDirection()){
            event=loan_Top[1];
        }else  if (PublicEnum.Business.isAd()){
            event=loan_Top[2];
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }
    /*
      贷款 买点
     */
    public static void normalEvent1(  Context context){
        String[] loan_Top = {"首页公共业务", "首页定向业务", "首页广告"};//从首页公共业务进入贷款
        String event="";
        if (PublicEnum.Business.isPublic()){
            event=loan_Top[0];
        }else  if (PublicEnum.Business.isDirection()){
            event=loan_Top[1];
        }else  if (PublicEnum.Business.isAd()){
            event=loan_Top[2];
        }
        String end ="";
        if (tag==1){
            end=ShoudanStatisticManager.Loan_List_To_Applay_Write1;
        }else if (tag==2){
            end=ShoudanStatisticManager.Loan_List_To_Applay_Write2;
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }

    /*
       替你还 买点
     */
    public static void LoansEvent(  String end,Context context){
        if (TextUtils.isEmpty(end))
            return;
        String[] loan_Top = {"首页公共业务-贷款", "首页定向业务", "首页广告","信用卡还款"};//从首页公共业务进入贷款
        String event="";
        if (PayForYouEnum.PayForYou.isLoan()){
            event=loan_Top[0];
        }else  if (PublicEnum.Business.isDirection()){
            event=loan_Top[1];
        }else  if (PublicEnum.Business.isAd()){
            event=loan_Top[2];
        }else  if (PayForYouEnum.PayForYou.isPayCredit()){
            event=loan_Top[3];
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }
    /*
         用户信息 买点
     */
    public static void MerchantlEvent( String end, Context context){
        if (TextUtils.isEmpty(end))
            return;
        String[] loan_Top = {"首页", "首页公共业务","消息中心"};
        String event="";
        if (PublicEnum.Business.isHome()){
            event=loan_Top[0];
        }else  if (PublicEnum.Business.isPublic()){
            event=loan_Top[1];
        }else if (PublicToEvent.messeage==true){
            event=loan_Top[2];
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }
    /*
       "  替你还买点
     */
    public static void LoanEvent( String end, Context context){
        if (TextUtils.isEmpty(end))
            return;
        String[] loan_Top = {"首页公共业务-贷款-该页面点击立即申请","信用卡还款页面"};
        String event="";
        if (PayForYouEnum.PayForYou.isLoan()){
            event=loan_Top[0];
        }else  if (PayForYouEnum.PayForYou.isForYou()){
            event=loan_Top[1];
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }
    /*
       理财买点
     */
    public static void FinalEvent( String end, Context context){
        if (TextUtils.isEmpty(end))
            return;
        String[] loan_Top = {"首页公共业务","定向业务","广告"};
        String event="";
        if (PublicEnum.Business.isPublic()){
            event=loan_Top[0];
        }else  if (PublicEnum.Business.isDirection()){
            event=loan_Top[1];
        }else  if (PublicEnum.Business.isAd()){
            event=loan_Top[2];
        }
        ShoudanStatisticManager.getInstance().onEvent(event+end, context);
    }
}

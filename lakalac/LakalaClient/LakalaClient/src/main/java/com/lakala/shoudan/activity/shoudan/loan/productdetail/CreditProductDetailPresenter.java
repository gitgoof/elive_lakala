package com.lakala.shoudan.activity.shoudan.loan.productdetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListModel;
import com.lakala.shoudan.activity.shoudan.loan.personinfo.CreditPersonInfoActivity;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.dialog.BaseDialog;
import com.lidroid.xutils.util.LogUtils;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CreditProductDetailPresenter extends CreditProductDetailContract.Presenter {
    private CreditProductDetailContract.CreditBusinessView creditBusinessView;
    private CreditProductDetailContract.Model mModel;
    private Context mContex;
    private int maxlength = 62;
    private String str_show = "";
    private String status = "up";
    public CreditProductDetailPresenter(CreditProductDetailContract.CreditBusinessView creditBusinessView) {
        this.creditBusinessView = creditBusinessView;
        this.creditBusinessView.setPresenter(this);
        mContex= (Context) creditBusinessView;
        mModel=new CreditProductDetailModel();
    }

    @Override
    public void setIntroduce(TextView tvDec, boolean toUp) {
        if (toUp==true) {
            tvDec.setEllipsize(TextUtils.TruncateAt.END);
            tvDec.setMaxLines(2);
            creditBusinessView.setDown();
        }else {
            tvDec.setEllipsize(null);
            tvDec.setMaxLines(Integer.MAX_VALUE);
            creditBusinessView.setUp();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setShowIntroduce(final LinearLayout onUp, final TextView tvIntroduce) {

        tvIntroduce.post(new Runnable() {
            @Override
            public void run() {
                Layout l = tvIntroduce.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    LogUtils.e( "lines = " + lines);
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) > 0) {
                            onUp.setVisibility(View.VISIBLE);
                            LogUtils.e("Text is ellipsized");
                        }else {
                            onUp.setVisibility(View.GONE);
                        }
                    }
                } else {
                    LogUtils.e("Layout is null");
                }
            }
        });
       /* if (tvIntroduce.length()>maxlength&&tvIntroduce.getText().toString().trim().endsWith("..")){
            onUp.setVisibility(View.VISIBLE);
        }else {
            onUp.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void setWordLabel(TextView tv1, TextView tv2, TextView tv3, TextView tv4, String wordLabel) {
        TextView[] rvs = {tv1, tv2, tv3, tv4};
        String[] label = wordLabel.split(",");
        if (label.length > 0) {
            for (int i = 0; i < label.length; i++) {
                rvs[i].setText(label[i]);
            }
        }
    }

    @Override
    public void applay(CreditListModel loan) {
       /* if (CommonUtil.isMerchantCompleted((FragmentActivity) mContex)){
            mModel.applyCheck(mContex,creditBusinessView, loan);
        }*/
        mModel.applyCheck(mContex,creditBusinessView, loan);
        PublicToEvent.normalEvent(ShoudanStatisticManager.Loan_List_To_Product_Applay, mContex);
    }

    @Override
    public void showApplay(CreditListModel model) {
        if (!CommonUtil.isMerchantValid1((FragmentActivity) mContex)){
            createFullShueDialog("很抱歉，该业务暂不能使用，先去体验一下其他业务吧");
            return;
        }else if (!model.isMerOpenMoreThanOneMonth()){
            createFullShueDialog("很抱歉，您的商户开通未满一个月，暂时不能申请贷款业务");
            return;
        }else if (!model.isSuccSettled()){
            showAhtuDialog("很抱歉，您需要有一笔收款交易成功结算后才能申请，快点击“立即交易”去收款吧","isSuccSettled",model);
            return;
        }else if (!model.isLv1Audit()){
            showAhtuDialog("很抱歉，您需要完成商户身份认证才能使用贷款服务，先去完善您的资料吧","isLv1Audit",model);
            return;
        }else if (model.isBlackAcct()){
            createFullShueDialog("很抱歉，该业务暂不能使用，先去体验一下其他业务吧");
            return;
        }else if (model.isAcctTypeIsPublic()){
            createFullShueDialog("很抱歉，您的结算账户为对公账户，不能使用贷款服务哦！");
            return;
        }else if (!model.isFinishedUserInfo()){
            showAhtuDialog("请完善相关信息，以便进行贷款申请","isFinishedUserInfo", model);
            return;
        }else {//进入申请页面
            mModel.appllySdk(mContex,model.getLoanMerId(),model.getLoanProName());
        }
    }
    private void  createFullShueDialog(String content){
        if (((AppBaseActivity)mContex).getProgressDialog()!=null&&((AppBaseActivity)mContex).isProgressisShowing()) {
            ((AppBaseActivity)mContex).hideProgressDialog();
        }
        DialogCreator.createFullShueDialog(
                (FragmentActivity) mContex, "提示",   "确定", content,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //  startActi(isUpgradeFirst);
                    }
                }
        ).show();
    }
    private void showAhtuDialog(String str, final String tag, final CreditListModel model) {
        if (((AppBaseActivity)mContex).getProgressDialog()!=null&&((AppBaseActivity)mContex).isProgressisShowing()) {
            ((AppBaseActivity)mContex).hideProgressDialog();
        }
        String shure = null;
        if(tag.equals("isFinishedUserInfo")){
            shure="立刻完善";
        }else if(tag.equals("isSuccSettled")) {
            shure="立即交易";
        }else  if (tag.equals("isLv1Audit")){
            shure="立即升级";
        }
        BaseDialog AhtuDialog;  AhtuDialog= DialogCreator.createFullContentDialog((FragmentActivity) mContex, "提示","取消" ,shure ,str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (tag.equals("isFinishedUserInfo")){
                    Intent intent= new Intent(mContex,CreditPersonInfoActivity.class);
                    intent.putExtra("loanMerId",model.getLoanMerId());
                    mContex.startActivity(intent);
                    PublicToEvent.tag=2;
                    PublicToEvent.normalEvent(ShoudanStatisticManager.Loan_List_Product_Applay, mContex);
                }else  if (tag.equals("isLv1Audit")){
                    MerchantUpdateActivity.start(mContex, true);
                }else if(tag.equals("isSuccSettled")) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Collection, mContex);
                    CollectionEnum.Colletcion.setData(null, false);
                    BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                }
            }
        });
        if (!AhtuDialog.isShowing()){
            AhtuDialog.show();
        }
    }
    public void setIntroduce1(TextView tv,String str) {
        if (str.length() < maxlength) {
            tv.setText(str);
        } else {
            changStatus(tv,str);
        }
    }
    private void changStatus(final TextView tv, final String str) {
        int showTag=0;
        tv.setText("");
        Drawable drawable = null;
        if (status.equals("down")) {
            showTag=1;
            drawable = mContex.getResources().getDrawable(R.drawable.btn_arrow_up_blue);
            status = "up";
            str_show = str;
            str_show += " 收起 ";
            tv.setMaxLines(10000);
        } else if (status.equals("up")) {
            showTag=2;
            drawable =mContex.getResources().getDrawable(R.drawable.btn_arrow_down_blue);
            status = "down";
            tv.setMaxLines(2);
            str_show = str.substring(0, maxlength);
            str_show += "...";
            str_show += " 展开 ";
        }
        String spanString = str_show;
        spanString = spanString.substring(0, spanString.length());
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        SpannableString spannable = new SpannableString(spanString.toString()
                + "s");
        VerticalImageSpan span = new VerticalImageSpan(drawable);
        spannable.setSpan(span, spanString.toString().length(), spanString
                        .toString().length() + "s".length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        int start=0;
        int end=0;
        if (showTag==1){
            start=str_show.length()-3;
            end=str_show.length()-1;
        }else {
            start=str_show.length()-5;
            end=str_show.length()-1;
        }
        spannable.setSpan(new ForegroundColorSpan(mContex.getResources().getColor(R.color.main_nav_blue)), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(11,true), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan rightClickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View view) {
                avoidHintColor(view);
                changStatus(tv, str);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //ds.setColor(mContex.getResources().getColor(android.R.color.white));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }

        };
        spannable.setSpan(rightClickableSpan, start,
                spanString.toString().length() + "s".length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannable);
        tv.setMovementMethod(LinkMovementMethod.getInstance() );
    }

    private void avoidHintColor(View view){
        if(view instanceof TextView)
            ((TextView)view).setHighlightColor(mContex.getResources().getColor(android.R.color.white));
    }
}

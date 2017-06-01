package com.lakala.shoudan.activity.shoudan.loan.loanlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mapapi.common.SysOSUtil;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lakala.platform.common.MyAdGallery;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.shoudan.activity.shoudan.loan.personinfo.CreditPersonInfoActivity;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.ScreenUtil;
import com.lakala.ui.dialog.BaseDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CreditListPresenter extends CreditListContract.Presenter {
    private CreditListContract.CreditBusinessView creditBusinessView;
    private Context mContext;
    private CreditListContract.Model mModel;
    public CreditListPresenter(CreditListContract.CreditBusinessView creditBusinessView) {
        this.creditBusinessView = creditBusinessView;
        this.creditBusinessView.setPresenter(this);
        mContext= (Context) creditBusinessView;
        mModel =new CreditListModel();
    }

    @Override
    public void initGallery(RelativeLayout rlGallery, LinearLayout ovalLayout, MyAdGallery gallery) {
        final ArrayList<AdDownloadManager.Advertise> advertises=((Activity)mContext).getIntent().getParcelableArrayListExtra("adverList");
        //屏幕
        ScreenUtil.getScrrenWidthAndHeight((Activity) mContext);
        int width = Parameters.screenWidth;
        int height = (int) (5 * width * 1.0 / 16);
        ViewGroup.LayoutParams params = rlGallery.getLayoutParams();
        params.width = width;
        params.height = height;
        rlGallery.setLayoutParams(params);
        ovalLayout.removeAllViews();// 获取圆点容器
        // 第二和第三参数 2选1 ,参数2为 图片网络路径数组 ,参数3为图片id的数组,本地测试用
        // ,2个参数都有优先采用 参数2
        if (advertises.size()>0){
            gallery.xdstart(mContext, mModel.getImgRes(advertises), null, 3000,
                    ovalLayout, R.drawable.dot_focused,
                    R.drawable.dot_normal);
        }else {
            gallery.xdstart(mContext, null, mModel.getImgResLocal(), 3000,
                    ovalLayout, R.drawable.dot_focused,
                    R.drawable.dot_normal);
        }

        gallery.setMyOnItemClickListener(new MyAdGallery.MyOnItemClickListener() {
            public void onItemClick(int curIndex) {// Gallery组件
                if (advertises.size()>0){
                    String urlClick=advertises.get(curIndex).getClickUrl();
                    if (urlClick.startsWith("lklmpos")){
                            if (urlClick.endsWith("gfd")){//功夫贷广告
                                ActiveNaviUtils.toGFDSDK((AppBaseActivity) mContext);
                            }else  if (urlClick.endsWith("51rpd")){//51贷广告
                                ActiveNaviUtils.to51DSDK((AppBaseActivity) mContext,"CreditListPresenter");
                            }
                    }else {
                        if (!TextUtils.isEmpty(urlClick)&&!"null".equals(urlClick)){

                            Intent intent =new Intent(mContext,AdShareActivity.class);
                            intent.putExtra("title",advertises.get(curIndex).getTitle());
                            intent.putExtra("url",urlClick);
                            mContext.startActivity(intent);
                        }
                       // ProtocalActivity.open(mContext,advertises.get(curIndex).getTitle(), urlClick);
                    }
                }
            }
        });

    }

    @Override
    public  List<CreditListModel> getData() {
        List<CreditListModel>data=new ArrayList<>();
        String dataList=((Activity)mContext).getIntent().getStringExtra("dataList");
        data.addAll(mModel.getDataList(dataList));
        return data;
    }

    @Override
    public void setList(PullToRefreshScrollView mallsScrollView) {
        mallsScrollView.setLoadingDrawable(mContext.getResources().getDrawable(R.drawable.default_ptr_rotate));
        ILoadingLayout loadingLayoutProxy = mallsScrollView.getLoadingLayoutProxy(
                true, false);
        loadingLayoutProxy.setLoadingDrawable(mContext.getResources().getDrawable(R.drawable.default_ptr_rotate));
        // 设置释放时的文字提示
        loadingLayoutProxy.setReleaseLabel("松开刷新数据");
        // 设置下拉时的文字提示
        loadingLayoutProxy.setPullLabel("下拉刷新");
        // 设置最后一次更新的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());
        loadingLayoutProxy.setLastUpdatedLabel("更新时间:" + time);
        // 设置刷新中的文字提示
        loadingLayoutProxy.setRefreshingLabel("正在刷新....");


    }

    @Override
    public void topRight() {
        mModel.getLoanApplyRecords(mContext,creditBusinessView);
    }

    @Override
    public void getLoanListAndDetail() {
        mModel.getLoanListAndDetail(mContext,creditBusinessView);
    }

    @Override
    public void applyCheck(CreditListModel creditListModel) {
        mModel.applyCheck(mContext,creditBusinessView, creditListModel);
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                .Loan_List_Applay, mContext);
    }

    @Override
    public void showApplay(CreditListModel model) {
            if (!CommonUtil.isMerchantValid1((FragmentActivity) mContext)){
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
                showAhtuDialog("请完善相关信息，以便进行贷款申请","isFinishedUserInfo",model);
                return;
            }else {//进入申请页面
                mModel.appllySdk(mContext,model.getLoanMerId(),model.getLoanProName());
            }
    }
    private void  createFullShueDialog(String content){
        if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
            ((AppBaseActivity)mContext).hideProgressDialog();
        }
        DialogCreator.createFullShueDialog(
                (FragmentActivity) mContext, "提示",   "确定", content,
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
        if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
            ((AppBaseActivity)mContext).hideProgressDialog();
        }
        String shure = null;
        if(tag.equals("isFinishedUserInfo")){
            shure="立刻完善";
        }else if(tag.equals("isSuccSettled")) {
            shure="立即交易";
        }else  if (tag.equals("isLv1Audit")){
            shure="立即升级";
        }
         BaseDialog AhtuDialog;  AhtuDialog= DialogCreator.createFullContentDialog((FragmentActivity) mContext, "提示","取消" ,shure ,str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (tag.equals("isFinishedUserInfo")){
                   Intent intent= new Intent(mContext,CreditPersonInfoActivity.class);
                    intent.putExtra("loanMerId",model.getLoanMerId());
                    mContext.startActivity(intent);
                    PublicToEvent.tag=1;
                    PublicToEvent.normalEvent( ShoudanStatisticManager.Loan_List_To_Applay, mContext);
                }else  if (tag.equals("isLv1Audit")){
                    MerchantUpdateActivity.start(mContext, true);
                }else if(tag.equals("isSuccSettled")) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Collection, mContext);
                    CollectionEnum.Colletcion.setData(null, false);
                    BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                }
            }
        });
        if (!AhtuDialog.isShowing()){
            AhtuDialog.show();
        }
    }
}

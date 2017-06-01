package com.lakala.shoudan.activity.shoudan.loan.loanlist;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lakala.platform.bean.BannerBean;
import com.lakala.platform.common.MyAdGallery;
import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.bll.AdDownloadManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public interface CreditListContract {
    interface Model{
        int[] getImgResLocal();
        ArrayList<BannerBean> getImgRes(ArrayList<AdDownloadManager.Advertise> advertises);

        void getLoanApplyRecords(Context mContext, CreditBusinessView creditBusinessView);

        void getLoanListAndDetail(Context mContext, CreditBusinessView creditBusinessView);

        void applyCheck(Context mContext, CreditBusinessView creditBusinessView, CreditListModel creditListModel);

        List<CreditListModel> getDataList(String dataList);

        void appllySdk(Context mContext, String merId, String loanMerId);
    }
    interface CreditBusinessView extends BaseView<Presenter> {

        void cancelRefresh();

        void showApplay(CreditListModel model);
    }
    abstract class Presenter {
        public abstract void initGallery(RelativeLayout rlGallery, LinearLayout ovalLayout, MyAdGallery gallery);
        public abstract  List<CreditListModel> getData();

        public abstract void setList(PullToRefreshScrollView mallsScrollView);

        public abstract void topRight();

        public abstract void getLoanListAndDetail();

        public abstract void applyCheck(CreditListModel creditListModel);

        public abstract  void showApplay(CreditListModel model);

    }
}

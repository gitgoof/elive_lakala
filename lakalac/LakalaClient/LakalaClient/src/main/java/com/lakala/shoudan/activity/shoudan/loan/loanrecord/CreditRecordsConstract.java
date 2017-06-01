package com.lakala.shoudan.activity.shoudan.loan.loanrecord;

import android.content.Context;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.ui.component.NavigationBar;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public interface CreditRecordsConstract {
    interface Model {
        List<TransDetailProInfo> getList();

        List<CreditRecordsModel> getDataList(String dataObj);

        void getLoanApplyRecords(Context context, CreditBusinessView creditBusinessView, String loan_a);

        void appllySdk(Context context, String loanProId, String loanName);
    }
    interface CreditBusinessView extends BaseView<Presenter> {
        void setPop(int pagecount,int isDefaultId,String title);

        void showNewData(List<CreditRecordsModel> dataList);
    }
    abstract class Presenter {
       public abstract View getEmptyView(String str);
       public abstract void showPopupWindow(final int position, final NavigationBar navigationBar);
        public abstract  List<CreditRecordsModel> getData();

        public abstract void setList(PullToRefreshListView recordList);

        public abstract void onItemClick(CreditRecordsModel position);
    }
}

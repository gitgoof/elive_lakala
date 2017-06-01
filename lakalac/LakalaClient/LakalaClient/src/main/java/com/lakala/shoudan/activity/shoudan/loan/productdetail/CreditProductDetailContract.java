package com.lakala.shoudan.activity.shoudan.loan.productdetail;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListModel;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public interface CreditProductDetailContract {
    interface Model{

        void applyCheck(Context mContex, CreditBusinessView creditBusinessView, CreditListModel loan);

        void appllySdk(Context mContex, String loanMerId, String loanProName);
    }
    interface CreditBusinessView extends BaseView<Presenter> {

        void setDown();

        void setUp();

        void showApplay(CreditListModel model);
    }
    abstract class Presenter {
        public abstract void setIntroduce(TextView tv, boolean str);

        public abstract void setShowIntroduce(LinearLayout onUp, TextView tvIntroduce);

        public abstract void setWordLabel(TextView tv1, TextView tv2, TextView tv3, TextView tv4, String wordLabel);

        public abstract void applay(CreditListModel loanMerId);

        public abstract void showApplay(CreditListModel model);
    }
}

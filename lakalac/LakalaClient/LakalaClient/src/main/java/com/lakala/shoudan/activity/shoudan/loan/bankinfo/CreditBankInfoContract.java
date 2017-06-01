package com.lakala.shoudan.activity.shoudan.loan.bankinfo;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.platform.bean.MerchantInfo;
import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.ui.component.LabelEditText;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public interface CreditBankInfoContract {
    interface Model{
        void checkCreditNum(CreditBankInfoView creditBankInfoView, String cardNum);

        boolean check(Context mContex, CreditBankInfoView creditBankInfoView, EditText editText, TextView letCardType, EditText editText1, TextView creditCardType);

        void addUserInfo(Context mContex, CreditBankInfoView creditBankInfoView, CreditBankInfoModel modelInfo);

        void appllySdk(Context mContex, String data);
    }
    interface CreditBankInfoView extends BaseView<Presenter> {
        void setCardData(OpenBankInfo mOpenBankInfo);

        void showLetBankError(String str);

        void showCreditBankError(String str);

        void showLetBank(OpenBankInfo mOpenBankInfo);

        void showCreditBank(OpenBankInfo mOpenBankInfo);

        void setBankInfo(String realName, MerchantInfo info);

        void applayComplet(String data);
    }
    abstract class Presenter {
        public abstract void setFocus(View v, boolean hasFocus, LabelEditText letBankCardNum, LabelEditText creditBankCardNum, String TAG_LETBANK, String TAG_CREDITBANK);

        public abstract void setCardData(OpenBankInfo mOpenBankInfo) ;

        public abstract void clear();

        public abstract void toNext(EditText letCardNum, EditText creditCardNum, EditText editText, TextView letCardType, EditText editText1, TextView creditCardType);

        public abstract void setLetInfo();

        public abstract void applayComplet(String data);

        public abstract void addEditTextChange(String s, EditText editText);
    }
}

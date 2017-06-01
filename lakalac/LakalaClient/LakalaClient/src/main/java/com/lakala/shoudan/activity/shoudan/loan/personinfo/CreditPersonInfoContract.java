package com.lakala.shoudan.activity.shoudan.loan.personinfo;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.lakala.shoudan.activity.BaseView;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public interface CreditPersonInfoContract {
    interface Model{
        String[] getRelatStr();
        void getcitys(CreditBusinessView view);
        String[] getUrgencyRelatStr();
         //boolean checkInval(CreditBusinessView view,EditText city,EditText address,EditText email,EditText unitName,EditText atCity,EditText uintAddress,EditText uintTel1,EditText uintTel2,EditText uintTe3,EditText relatName,EditText relatTel,EditText relatRelation,EditText urgencyContactName,EditText urgencyContactTel,EditText urgencyContactRelation);
         boolean checkInval(CreditBusinessView view,EditText city,EditText address,EditText email,EditText unitName,EditText atCity,EditText uintAddress,EditText uintTel1,EditText relatName,EditText relatTel,EditText relatRelation,EditText urgencyContactName,EditText urgencyContactTel,EditText urgencyContactRelation);
    }
    interface CreditBusinessView extends BaseView<Presenter> {
        void showRelatStr(String str);
        void initDialog();
        void showUrgencyRelatStr(String str);
        void setTex(EditText ec,String str);
        void setLiveCity(String str);
        void setAtCity(String str);
    }
    abstract class Presenter {
       // public abstract void btnNext(EditText city,EditText address,EditText email,EditText unitName,EditText atCity,EditText uintAddress,EditText uintTel1,EditText uintTel2,EditText uintTel3,EditText relatName,EditText relatTel,EditText relatRelation,EditText urgencyContactName,EditText urgencyContactTel,EditText urgencyContactRelation);
        public abstract void btnNext(EditText city,EditText address,EditText email,EditText unitName,EditText atCity,EditText uintAddress,EditText uintTel1,EditText relatName,EditText relatTel,EditText relatRelation,EditText urgencyContactName,EditText urgencyContactTel,EditText urgencyContactRelation);
        public abstract void contactChoose(String tag);
        public abstract void cityiChoose(String tag);
        public abstract void setCityi(View rootView, Spinner proviceSpinner, Spinner citySpinner, Spinner disticSppinner);
        public abstract void actResult(int requestCode, final Intent data, final EditText relatives, final EditText linkman);
        public abstract void clearTags();
        public abstract void showRelatRelation();
        public abstract void showUrgencyRelation();
    }
}

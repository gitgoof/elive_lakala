package com.lakala.shoudan.activity.shoudan.loan.personinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.activity.shoudan.loan.LoanIndividualInfoActivity;
import com.lakala.shoudan.activity.shoudan.loan.RegionInfoAdapter;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.lakala.shoudan.R.id.context;

/**
 * Created by Administrator on 2016/10/27 0027.
 */

public class CreditPersonInfoModel extends BaseProciceModel implements CreditPersonInfoContract.Model {
    private String apcrcodes;//所在住宅省市区编码串
    private String apcrnames; //所在住宅省市区名称

    public String getApcrcodes() {
        return apcrcodes;
    }

    public void setApcrcodes(String apcrcodes) {
        this.apcrcodes = apcrcodes;
    }

    public String getApcrnames() {
        return apcrnames;
    }

    public void setApcrnames(String apcrnames) {
        this.apcrnames = apcrnames;
    }

    @Override
    public void getcitys(CreditPersonInfoContract.CreditBusinessView view) {
        getProvice(view);
    }
    @Override
    public void initDialog( BaseView views){
        final CreditPersonInfoContract.CreditBusinessView view= (CreditPersonInfoContract.CreditBusinessView) views;
        view.initDialog();
    }

    @Override
    public String[] getRelatStr() {
        String levels[]=new String[]{"夫妻","父子","母子"};
        return levels;
    }

    @Override
    public String[] getUrgencyRelatStr() {
        String levels[]=new String[]{"夫妻","父子","母子","同事"};
        return levels;
    }

    @Override
    public boolean checkInval(CreditPersonInfoContract.CreditBusinessView view, EditText city, EditText address, EditText email, EditText unitName, EditText atCity, EditText uintAddress, EditText uintTel1, EditText relatName, EditText relatTel, EditText relatRelation, EditText urgencyContactName, EditText urgencyContactTel, EditText urgencyContactRelation) {
        String phoneNum=uintTel1.getText().toString().trim();
        if (TextUtils.isEmpty(city.getText().toString().trim())) {
            view.showToast("请选择居住城市");
            return false;
        }else if (TextUtils.isEmpty(address.getText().toString().trim())) {
            view.showToast("请填写详细居住地址");
            return false;
        }else if (TextUtils.isEmpty(email.getText().toString().trim())) {
            view.showToast("请输入电子邮箱");
            return false;
        } else if (!Util.checkEmailAddress(email.getText().toString().trim())) {
            view.showToast("电子邮箱格式有误");
            return false;
        }else if (TextUtils.isEmpty(unitName.getText().toString().trim())) {
            view.showToast("请输入单位全称");
            return false;
        }else if (TextUtils.isEmpty(atCity.getText().toString().trim())) {
            view.showToast("请选择单位所在城市");
            return false;
        }else if (TextUtils.isEmpty(uintAddress.getText().toString().trim())) {
            view.showToast("请填写详细单位地址");
            return false;
        }else if (TextUtils.isEmpty(uintTel1.getText().toString().trim())) {
            view.showToast("请输入单位电话");
            return false;
        }else if (TextUtils.isEmpty(relatName.getText().toString().trim())) {
            view.showToast("请输入亲属姓名");
            return false;
        }else if (TextUtils.isEmpty(relatTel.getText().toString().trim())) {
            view.showToast("请输入亲属手机号码");
            return false;
        }if(!Util.checkPhoneNumber(relatTel.getText().toString().trim())){
            view.showToast("亲属手机号码格式错误");
            return false;
        }else if (TextUtils.isEmpty(relatRelation.getText().toString().trim())) {
            view.showToast("请选择亲属关系");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactName.getText().toString().trim())) {
            view.showToast("请输入紧急联系人姓名");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactTel.getText().toString().trim())) {
            view.showToast("请输入紧急联系人手机号码");
            return false;
        }if(!Util.checkPhoneNumber(urgencyContactTel.getText().toString().trim())){
            view.showToast("紧急联系人手机号码格式错误");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactRelation.getText().toString().trim())) {
            view.showToast("请选择紧急联系人关系");
            return false;
        }
        return true;
    }



 /*   @Override
    public boolean checkInval(CreditPersonInfoContract.CreditBusinessView view, EditText city, EditText address, EditText email, EditText unitName, EditText atCity, EditText uintAddress, EditText uintTel1, EditText uintTel2, EditText uintTel3, EditText relatName, EditText relatTel, EditText relatRelation, EditText urgencyContactName, EditText urgencyContactTel, EditText urgencyContactRelation) {
        String phoneNum=uintTel1.getText().toString().trim()+uintTel2.getText().toString().trim()+uintTel3.getText().toString().trim();
        if (TextUtils.isEmpty(city.getText().toString().trim())) {
            view.showToast("请选择居住城市");
            return false;
        }else if (TextUtils.isEmpty(address.getText().toString().trim())) {
            view.showToast("请填写详细居住地址");
            return false;
        }else if (TextUtils.isEmpty(email.getText().toString().trim())) {
            view.showToast("请输入电子邮箱");
            return false;
        } else if (!Util.checkEmailAddress(email.getText().toString().trim())) {
            view.showToast("电子邮箱格式有误");
            return false;
        }else if (TextUtils.isEmpty(unitName.getText().toString().trim())) {
            view.showToast("请输入单位全称");
            return false;
        }else if (TextUtils.isEmpty(atCity.getText().toString().trim())) {
            view.showToast("请选择单位所在城市");
            return false;
        }else if (TextUtils.isEmpty(uintAddress.getText().toString().trim())) {
            view.showToast("请填写详细单位地址");
            return false;
        }else if (TextUtils.isEmpty(uintTel1.getText().toString().trim())) {
            view.showToast("请输入单位电话");
            return false;
        }else if (TextUtils.isEmpty(uintTel2.getText().toString().trim())) {
            view.showToast("请输入单位电话");
            return false;
        }else if (TextUtils.isEmpty(uintTel3.getText().toString().trim())) {
            view.showToast("请输入单位电话");
            return false;
        }if(!Util.checkPhoneNumber(phoneNum)){
            view.showToast("单位电话格式错误");
            return false;
        }else if (TextUtils.isEmpty(relatName.getText().toString().trim())) {
            view.showToast("请输入亲属姓名");
            return false;
        }else if (TextUtils.isEmpty(relatTel.getText().toString().trim())) {
            view.showToast("请输入亲属手机号码");
            return false;
        }if(!Util.checkPhoneNumber(relatTel.getText().toString().trim())){
            view.showToast("亲属手机号码格式错误");
            return false;
        }else if (TextUtils.isEmpty(relatRelation.getText().toString().trim())) {
            view.showToast("请选择亲属关系");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactName.getText().toString().trim())) {
            view.showToast("请输入紧急联系人姓名");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactTel.getText().toString().trim())) {
            view.showToast("请输入紧急联系人手机号码");
            return false;
        }if(!Util.checkPhoneNumber(urgencyContactTel.getText().toString().trim())){
            view.showToast("紧急联系人手机号码格式错误");
            return false;
        }else if (TextUtils.isEmpty(urgencyContactRelation.getText().toString().trim())) {
            view.showToast("请选择紧急联系人关系");
            return false;
        }
        return true;
    }
*/
}

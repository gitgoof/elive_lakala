package com.lakala.shoudan.activity.shoudan.loan.bankinfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.datadefine.OpenBankInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/31 0031.
 */

public class CreditBankInfoModel implements CreditBankInfoContract.Model, Serializable {
    private String email;//电子邮箱
    private String city;//居住城市
    private String address;//住宅地址
    private String companyName;//单位全称
    private String companyCity;//单位城市
    private String companyDetailAddr;//单位详址
    private String companyTelephone;//单位电话
    private String relationShip;//亲属关系
    private String relationName;//亲属姓名
    private String relationNumb;//亲属手机号码
    private String emergContactShip;//紧急联系
    private String emergContactName;//紧急联系人名称
    private String emergContactNumb;//紧急联系人电话
    private String debitUserName;//储蓄卡用户名称
    private String debitBankAcctNo;//银行账号
    private String debitBankName;//银行名称
    private String creditUserName;//信用卡用户名称
    private String creditBankAcctNo;//银行账号
    private String creditBankName;//银行名称
    private String loanMerId;//供应商编号

    public String getLoanMerId() {
        return loanMerId;
    }

    public void setLoanMerId(String loanMerId) {
        this.loanMerId = loanMerId;
    }

    public String getCreditBankName() {
        return creditBankName;
    }

    public void setCreditBankName(String creditBankName) {
        this.creditBankName = creditBankName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyDetailAddr() {
        return companyDetailAddr;
    }

    public void setCompanyDetailAddr(String companyDetailAddr) {
        this.companyDetailAddr = companyDetailAddr;
    }

    public String getCompanyTelephone() {
        return companyTelephone;
    }

    public void setCompanyTelephone(String companyTelephone) {
        this.companyTelephone = companyTelephone;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationNumb() {
        return relationNumb;
    }

    public void setRelationNumb(String relationNumb) {
        this.relationNumb = relationNumb;
    }

    public String getEmergContactShip() {
        return emergContactShip;
    }

    public void setEmergContactShip(String emergContactShip) {
        this.emergContactShip = emergContactShip;
    }

    public String getEmergContactName() {
        return emergContactName;
    }

    public void setEmergContactName(String emergContactName) {
        this.emergContactName = emergContactName;
    }

    public String getEmergContactNumb() {
        return emergContactNumb;
    }

    public void setEmergContactNumb(String emergContactNumb) {
        this.emergContactNumb = emergContactNumb;
    }

    public String getDebitUserName() {
        return debitUserName;
    }

    public void setDebitUserName(String debitUserName) {
        this.debitUserName = debitUserName;
    }

    public String getDebitBankAcctNo() {
        return debitBankAcctNo;
    }

    public void setDebitBankAcctNo(String debitBankAcctNo) {
        this.debitBankAcctNo = debitBankAcctNo;
    }

    public String getDebitBankName() {
        return debitBankName;
    }

    public void setDebitBankName(String debitBankName) {
        this.debitBankName = debitBankName;
    }

    public String getCreditUserName() {
        return creditUserName;
    }

    public void setCreditUserName(String creditUserName) {
        this.creditUserName = creditUserName;
    }

    public String getCreditBankAcctNo() {
        return creditBankAcctNo;
    }

    public void setCreditBankAcctNo(String creditBankAcctNo) {
        this.creditBankAcctNo = creditBankAcctNo;
    }

    public void checkCreditNum(final CreditBankInfoContract.CreditBankInfoView creditBankInfoView, final String cardNum) {
        creditBankInfoView.showLoading();

        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    creditBankInfoView.hideLoading();
                    try {
                        jsonObject = new JSONObject(resultServices.retData);

                        if (jsonObject != null) {
                            OpenBankInfo mOpenBankInfo = OpenBankInfo.construct(jsonObject);
                            mOpenBankInfo.cardNo=cardNum;
                            creditBankInfoView.setCardData(mOpenBankInfo);
                        }
                    } catch (JSONException e) {
                        creditBankInfoView.hideLoading();
                        e.printStackTrace();
                    }
                } else {
                    creditBankInfoView.hideLoading();
                    creditBankInfoView.showToast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                creditBankInfoView.hideLoading();
                creditBankInfoView.showToast(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getCardBIN(BankBusid.MPOS_ACCT, cardNum, callback);
    }

    @Override
    public boolean check(Context mContex, CreditBankInfoContract.CreditBankInfoView view, EditText editText, TextView letCardType, EditText editText1, TextView creditCardType) {
        SharedPreferences mySharedPreferences=mContex .getSharedPreferences("bankInfo", Activity.MODE_PRIVATE);
        String letBankNo = mySharedPreferences.getString("letBankNo","");
        boolean letBankNoEdit= mySharedPreferences.getBoolean("letBankNoEdit",false);
        String creditBankNo = mySharedPreferences.getString("creditBankNo","");
        String accountNo=ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo();

        String letNum=editText.getText().toString().trim();
        String creditNum=editText1.getText().toString().trim();
        if(TextUtils.isEmpty(letNum) ){
            view.showToast("请输入储蓄卡卡号");
            return false;
        }else if (!letBankNo.equals(accountNo)&&letBankNoEdit){
            if (letBankNo.equals("")){
                view.showToast("请重新输入储蓄卡卡号");
            }else if (!letBankNo.equals("")&&letBankNo!=letNum){
                view.showToast("请输入正确的储蓄卡卡号");
            }
            return false;
        }else if (TextUtils.isEmpty(creditNum)){
            view.showToast("请输入信用卡卡号");
            return false;
        }else if (creditBankNo.equals("")){
            view.showToast("请重新输入信用卡卡号");
            return false;
        }else if (!creditBankNo.equals("")&&!creditBankNo.equals(creditNum)){
            view.showToast("请输入正确的信用卡卡号");
            return false;
        }
        return true;
    }

    @Override
    public void addUserInfo(final Context mContex, final CreditBankInfoContract.CreditBankInfoView creditBusinessView, final CreditBankInfoModel modelInfo) {
        creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContex, RequestFactory.Type.ADD_USER_INFO);
        HttpRequestParams params =businessRequest.getRequestParams();
        params.put("email",modelInfo.getEmail());
        params.put("city",modelInfo.getCity());
        params.put("address",modelInfo.getAddress());
        params.put("companyName",modelInfo.getCompanyName());
        params.put("companyCity",modelInfo.getCompanyCity());
        params.put("companyDetailAddr",modelInfo.getCompanyDetailAddr());
        params.put("companyTelephone",modelInfo.getCompanyTelephone());
        params.put("relationShip",modelInfo.getRelationShip());
        params.put("relationName",modelInfo.getRelationName());
        params.put("relationNumb",modelInfo.getRelationNumb());
        params.put("emergContactShip",modelInfo.getEmergContactShip());
        params.put("emergContactName",modelInfo.getEmergContactName());
        params.put("emergContactNumb",modelInfo.getEmergContactNumb());
        params.put("debitUserName",modelInfo.getDebitUserName());
        params.put("debitBankAcctNo",modelInfo.getDebitBankAcctNo());
        params.put("debitBankName",modelInfo.getDebitBankName());
        params.put("creditUserName",modelInfo.getCreditUserName());
        params.put("creditBankAcctNo",modelInfo.getCreditBankAcctNo());
        params.put("creditBankName",modelInfo.getCreditBankName());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        String data = resultServices.retData;
                        creditBusinessView.applayComplet(modelInfo.getLoanMerId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    creditBusinessView.showToast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                creditBusinessView.hideLoading();
                creditBusinessView.showToast(mContex.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(mContex, businessRequest);
    }

    @Override
    public void appllySdk(Context mContext, String loanMerId) {
        if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
            ((AppBaseActivity)mContext).hideProgressDialog();
        }
        ActiveNaviUtils.stopBankInfo();
        /*if ("DK_DS".equals(loanMerId)){
            ActiveNaviUtils.toGFDSDK1((AppBaseActivity) mContext);

        }else if ("DK_51".equals(loanMerId)){
            ActiveNaviUtils.to51DSDK((AppBaseActivity) mContext,"CreditBankInfoModel");
        }else {
            if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
        }*/
    }
}

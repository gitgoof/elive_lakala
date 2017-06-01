package com.lakala.shoudan.common;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/11/26.
 */
public abstract class LoanInfoCallback implements ServiceResultCallback {
    @Override
    public void onSuccess(ResultServices resultServices) {
        LoanBackShowInfo loanBackShowInfo = new LoanBackShowInfo();
        if(resultServices.isRetCodeSuccess()){
            try {
                JSONObject jsonData = new JSONObject(resultServices.retData);
                loanBackShowInfo = unPackBackShowInfo(jsonData);
                loanBackShowInfo.setPass(true);
            } catch (JSONException e) {
                e.printStackTrace();
                loanBackShowInfo.setPass(false);
            }
        }else{
            loanBackShowInfo.setPass(false);
            loanBackShowInfo.setErrMsg(resultServices.retMsg);
        }
        onSuccess(loanBackShowInfo);
    }
    public abstract void onSuccess(LoanBackShowInfo loanBackShowInfo);
    private LoanBackShowInfo unPackBackShowInfo(JSONObject retData) throws JSONException {
        LoanBackShowInfo backShowInfo=new LoanBackShowInfo();
        backShowInfo.setApplyamt(retData.optInt("applyamt"));
        backShowInfo.setPeriod(retData.optInt("period"));
        backShowInfo.setRate(retData.optInt("rate"));
        backShowInfo.setCurcode(retData.optString("curcode"));
        backShowInfo.setTermid(retData.optString("termid"));
        backShowInfo.setTelecode(retData.optString("telecode"));
        backShowInfo.setApplicant(retData.optString("applicant"));
        backShowInfo.setCertno(retData.optString("certno"));
        backShowInfo.setApcrcodes(retData.optString("apcrcodes"));
        backShowInfo.setApcrnames(retData.optString("apcrnames"));
        backShowInfo.setAddress(retData.optString("address"));
        backShowInfo.setHighestlevel(retData.optString("highestlevel"));
        backShowInfo.setEmail(retData.optString("email"));
        backShowInfo.setCertfirstimg(retData.optString("certfirstimg"));
        backShowInfo.setCertsecondimg(retData.optString("certsecondimg"));
        backShowInfo.setNowphoto(retData.optString("nowphoto"));
        backShowInfo.setCompanyname(retData.optString("companyname"));
        backShowInfo.setPosition(retData.optString("position"));
        backShowInfo.setCpcrcodes(retData.optString("cpcrcodes"));
        backShowInfo.setCpcrnames(retData.optString("cpcrnames"));
        backShowInfo.setCompanyaddress(retData.optString("companyaddress"));
        backShowInfo.setCompanytel(retData.optString("companytel"));
        backShowInfo.setIncome(retData.optString("income"));
        backShowInfo.setContactname(retData.optString("contactname"));
        backShowInfo.setContactmobile(retData.optString("contactmobile"));
        backShowInfo.setCreditcard(retData.optString("creditcard"));
        backShowInfo.setCreditbank(retData.optString("creditbank"));
        backShowInfo.setDebitcard(retData.optString("debitcard"));
        backShowInfo.setDebitbank(retData.optString("debitbank"));

        return backShowInfo;
    }

}

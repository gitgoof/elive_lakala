package com.lakala.shoudan.activity.shoudan.loan.loanrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.loan.CameraActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.shoudan.activity.shoudan.loan.kepler.KeplerUtil;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class CreditRecordsModel implements CreditRecordsConstract.Model {
    private String loanLogo;//产品
    private String loanName;//产品名称
    private String loanProId;//产品编号
    private String applyAmount;//申请金额
    private String loanPeriod;//贷款周期
    private String loanRate;//手续费率
    private String applyDate;//申请日期
    private String loanStatus;//申请状态
    private String loanMerId;//供应商编号

    public String getLoanMerId() {
        return loanMerId;
    }

    public void setLoanMerId(String loanMerId) {
        this.loanMerId = loanMerId;
    }

    public String getLoanLogo() {
        return loanLogo;
    }

    public void setLoanLogo(String loanLogo) {
        this.loanLogo = loanLogo;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public String getLoanProId() {
        return loanProId;
    }

    public void setLoanProId(String loanProId) {
        this.loanProId = loanProId;
    }

    public String getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(String applyAmount) {
        this.applyAmount = applyAmount;
    }

    public String getLoanPeriod() {
        return loanPeriod;
    }

    public void setLoanPeriod(String loanPeriod) {
        this.loanPeriod = loanPeriod;
    }

    public String getLoanRate() {
        return loanRate;
    }

    public void setLoanRate(String loanRate) {
        this.loanRate = loanRate;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
    @Override
    public void getLoanApplyRecords(final Context mContext, final CreditRecordsConstract.CreditBusinessView creditBusinessView, final String loan_a) {
        creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContext, RequestFactory.Type.LOAN_APPLY_RECORDS);
        RequestParams params= businessRequest.getRequestParams();
        params.put("flag",loan_a);
        final String []loan={"LOAN_A","LOAN_B","LOAN_C","LOAN_D"};
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        String event="";
                        for (int i=0;i<loan.length;i++){
                            if (loan_a.equals(loan[i])){
                                event= ShoudanStatisticManager.Loan_Apply_Records_end[i]+
                                        ShoudanStatisticManager.Loan_Apply_Records_All_Click+"-记录详情";
                            }
                        }
                        ShoudanStatisticManager.getInstance().onEvent(event, mContext);
                        String data = resultServices.retData;
                        creditBusinessView.showNewData(getDataList(data));
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
                creditBusinessView.showToast(mContext.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(mContext, businessRequest);
    }

    @Override
    public void appllySdk(Context mContext, String loanMerId, String loanName) {
        if ("DK_DS".equals(loanMerId)){
            ActiveNaviUtils.toGFDSDK((AppBaseActivity) mContext);
        }else if ("DK_51".equals(loanMerId)){
            ActiveNaviUtils.to51DSDK((AppBaseActivity) mContext,"CreditRecordsModel");
        } else if ("DK_PA".equals(loanMerId)){
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            new KeplerUtil().enterKepler((Activity) mContext,loanName);
        }else if("DK_YFQ".equals(loanMerId)){
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            getYFQurl(mContext);
        }else if("DK_TNH".equals(loanMerId)){//替你还
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            getTNHurl(mContext);
        }else {
            if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
        }
    }

    @Override
    public List<TransDetailProInfo> getList() {
         List<TransDetailProInfo> proList = new ArrayList<TransDetailProInfo>();
        String[]str={"全部记录","浏览记录","申请中","已放贷"};
        for (int i=0;i<str.length;i++){
            TransDetailProInfo info=new TransDetailProInfo();
            info.setProName(str[i]);
            proList.add(info);
        }
        return proList;
    }

    public List<CreditRecordsModel> getDataList(String dataObj) {
        try {
            List<CreditRecordsModel> data = new ArrayList<>();
            org.json.JSONArray objArray=new org.json.JSONArray(dataObj);
            for (int i=0;i<objArray.length();i++){
                CreditRecordsModel model=new CreditRecordsModel();
                JSONObject obj=objArray.optJSONObject(i);
                model.setLoanLogo(setTV(obj.optString("loanLogo")));
                model.setLoanName(setTV(obj.optString("loanName")));
                model.setLoanProId(setTV(obj.optString("loanProId")));
                model.setApplyAmount(setTV(obj.optString("applyAmount")));
                model.setLoanPeriod(setTV(obj.optString("loanPeriod")));
                model.setLoanRate(setTV(obj.optString("loanRate")));
                model.setApplyDate(setTV(obj.optString("applyDate")));
                model.setLoanStatus(setTV(obj.optString("loanStatus")));
                model.setLoanMerId(setTV(obj.optString("loanMerId")));
                data.add(model);
            }
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String setTV(String tv){
        if (tv==null){
            return "";
        }else {
            return tv;
        }

    }

    public void getYFQurl(final Context context){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/easyStageH5/createUrl", HttpRequest.RequestMethod.GET,true);
        businessRequest.getRequestParams().put("realName", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        businessRequest.getRequestParams().put("idCard",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        Intent intent1 = new Intent(context,CameraActivity.class);
                        intent1.putExtra("litan",jsonObject.optString("entryptURL"));
                        context.startActivity(intent1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, "网络请求失败");
            }
        }));
        businessRequest.execute();
    }
    //替你还
    public void getTNHurl(final Context context){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"/v1.0/easyStageH5/createTNHUrl", HttpRequest.RequestMethod.GET,true);
        businessRequest.getRequestParams().put("realName", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        businessRequest.getRequestParams().put("idCard",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        Intent intent1 = new Intent(context,CameraActivity.class);
                        intent1.putExtra("litan",jsonObject.optString("entryptURL"));
                        intent1.putExtra("source","TNH");
                        context.startActivity(intent1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, "网络请求失败");
            }
        }));
        businessRequest.execute();
    }
}

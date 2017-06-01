package com.lakala.shoudan.activity.shoudan.loan.loanlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.BannerBean;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.loan.CameraActivity;
import com.lakala.shoudan.activity.shoudan.loan.kepler.KeplerUtil;
import com.lakala.shoudan.activity.shoudan.loan.loanrecord.CreditRecordsActivity;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27 0027.
 */

public class CreditListModel implements CreditListContract.Model,Serializable {
    private boolean isFinishedUserInfo;//是否完成信息
    private boolean merOpenMoreThanOneMonth;//商户开通一个月以上
    private boolean isSuccSettled;//有结算成功过
    private boolean lv1Audit;//商户通过一类审核
    private boolean isBlackAcct;//分控黑名单账户
    private boolean acctTypeIsPublic;//是否对公账户
    private String loanLogo;//贷款产品logo
    private String loanProName;//贷款产品名称
    private String loanProId;//贷款产品编号
    private String loanMerName;//供应商名称
    private String loanMerId;//供应商编号
    private String loanRule;//校验规则
    private String wordLabel;//话术标签
    private String applyUpLimit;//申请额度上限
    private String applyDownLimit;//申请额度下限
    private String applyConditions;//申请条件
    private String auditDescription;//审核说明
    private String proIntroduction;//产品简介
    private String rateLimit;//利率
    private String loanPeriod;//贷款周期
    private String applyProcess;//申请流程

    public boolean isFinishedUserInfo() {
        return isFinishedUserInfo;
    }

    public void setFinishedUserInfo(boolean finishedUserInfo) {
        isFinishedUserInfo = finishedUserInfo;
    }

    public boolean isMerOpenMoreThanOneMonth() {
        return merOpenMoreThanOneMonth;
    }

    public void setMerOpenMoreThanOneMonth(boolean merOpenMoreThanOneMonth) {
        this.merOpenMoreThanOneMonth = merOpenMoreThanOneMonth;
    }

    public boolean isSuccSettled() {
        return isSuccSettled;
    }

    public void setSuccSettled(boolean succSettled) {
        isSuccSettled = succSettled;
    }

    public boolean isLv1Audit() {
        return lv1Audit;
    }

    public void setLv1Audit(boolean lv1Audit) {
        this.lv1Audit = lv1Audit;
    }

    public boolean isBlackAcct() {
        return isBlackAcct;
    }

    public void setBlackAcct(boolean blackAcct) {
        isBlackAcct = blackAcct;
    }

    public boolean isAcctTypeIsPublic() {
        return acctTypeIsPublic;
    }

    public void setAcctTypeIsPublic(boolean acctTypeIsPublic) {
        this.acctTypeIsPublic = acctTypeIsPublic;
    }

    public String getLoanLogo() {
        return loanLogo;
    }

    public void setLoanLogo(String loanLogo) {
        this.loanLogo = loanLogo;
    }

    public String getLoanProName() {
        return loanProName;
    }

    public void setLoanProName(String loanProName) {
        this.loanProName = loanProName;
    }

    public String getLoanProId() {
        return loanProId;
    }

    public void setLoanProId(String loanProId) {
        this.loanProId = loanProId;
    }

    public String getLoanMerName() {
        return loanMerName;
    }

    public void setLoanMerName(String oanMerName) {
        this.loanMerName = oanMerName;
    }

    public String getLoanMerId() {
        return loanMerId;
    }

    public void setLoanMerId(String loanMerId) {
        this.loanMerId = loanMerId;
    }

    public String getLoanRule() {
        return loanRule;
    }

    public void setLoanRule(String loanRule) {
        this.loanRule = loanRule;
    }

    public String getWordLabel() {
        return wordLabel;
    }

    public void setWordLabel(String wordLabel) {
        this.wordLabel = wordLabel;
    }

    public String getApplyUpLimit() {
        return applyUpLimit;
    }

    public void setApplyUpLimit(String applyUpLimit) {
        this.applyUpLimit = applyUpLimit;
    }

    public String getApplyDownLimit() {
        return applyDownLimit;
    }

    public void setApplyDownLimit(String applyDownLimit) {
        this.applyDownLimit = applyDownLimit;
    }

    public String getApplyConditions() {
        return applyConditions;
    }

    public void setApplyConditions(String applyConditions) {
        this.applyConditions = applyConditions;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription;
    }

    public String getProIntroduction() {
        return proIntroduction;
    }

    public void setProIntroduction(String proIntroduction) {
        this.proIntroduction = proIntroduction;
    }

    public String getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(String rateLimit) {
        this.rateLimit = rateLimit;
    }

    public String getLoanPeriod() {
        return loanPeriod;
    }

    public void setLoanPeriod(String loanPeriod) {
        this.loanPeriod = loanPeriod;
    }

    public String getApplyProcess() {
        return applyProcess;
    }

    public void setApplyProcess(String applyProcess) {
        this.applyProcess = applyProcess;
    }

    @Override
    public  ArrayList<BannerBean> getImgRes(ArrayList<AdDownloadManager.Advertise> advertises) {
        ArrayList<BannerBean> data=new ArrayList<>();
        for (int i=0;i<advertises.size();i++){
            BannerBean b=new BannerBean();
            b.setImg(advertises.get(i).getContent());
            b.setPicture_id(advertises.get(i).getClickUrl());
            data.add(b);
        }
        return data;
    }
    @Override
    public  int[] getImgResLocal() {
        int[] idRse= {R.drawable.credit_banner};
        return idRse;
    }

    @Override
    public void getLoanApplyRecords(final Context mContext, final CreditListContract.CreditBusinessView creditBusinessView) {
        creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContext, RequestFactory.Type.LOAN_APPLY_RECORDS);
        RequestParams params= businessRequest.getRequestParams();
        params.put("flag","LOAN_A");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        String data = resultServices.retData;
                        Intent intent = new Intent(mContext, CreditRecordsActivity.class);
                        intent.putExtra("dataObj",data);
                        PublicToEvent.normalEvent( ShoudanStatisticManager.Loan_Apply_Records, mContext);
                        mContext.startActivity(intent);
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
    public void getLoanListAndDetail(final Context mContext, final CreditListContract.CreditBusinessView creditBusinessView) {
        creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContext, RequestFactory.Type.Loan_List_Homepage);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                creditBusinessView.cancelRefresh();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        String data = resultServices.retData;
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

    public void applyCheck(final Context mContext, final CreditListContract.CreditBusinessView creditBusinessView, final CreditListModel item) {
       creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContext, RequestFactory.Type.Loan_Apply_Check);
        HttpRequestParams params =businessRequest.getRequestParams();
        params.put("loanLogo",item.getLoanLogo());
        params.put("loanName",item.getLoanProName());
        params.put("loanProId",item.getLoanProId());
        params.put("loanMerId",item.getLoanMerId());
        params.put("loanRule", item.getLoanRule());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject data = new JSONObject(resultServices.retData);
                        CreditListModel model=optData(data,item);
                        creditBusinessView.showApplay(model);
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

    public static CreditListModel optData(JSONObject data, CreditListModel model) {
        model.setFinishedUserInfo(setBooean(data,"isFinishedUserInfo"));
        model.setMerOpenMoreThanOneMonth(setBooean(data,"isOpenMoreThanOneMonth"));
        model.setSuccSettled(setBooean(data,"isSuccSettled"));
        model.setLv1Audit(setBooean(data,"lv1Audit"));
        model.setBlackAcct(setBooean(data,"isBlackAcct"));
        model.setAcctTypeIsPublic(setBooean(data,"acctTypeIsPublic"));
        return model;
    }

    @Override
    public List<CreditListModel> getDataList(String dataList) {
        try {
            List<CreditListModel>data=new ArrayList<>();
            org.json.JSONArray objArray=new org.json.JSONArray(dataList);
            for (int i=0;i<objArray.length();i++){
                CreditListModel model=new CreditListModel();
                JSONObject obj=objArray.optJSONObject(i);
                model.setLoanLogo(setTV(obj.optString("loanLogo")));
                model.setLoanProName(setTV(obj.optString("loanProName")));
                model.setLoanProId(setTV(obj.optString("loanProId")));
                model.setLoanMerName(setTV(obj.optString("loanMerName")));
                model.setLoanMerId(setTV(obj.optString("loanMerId")));
                model.setLoanRule(setTV(obj.optString("loanRule")));
                model.setWordLabel(setTV(obj.optString("wordLabel")));
                model.setApplyUpLimit(setTV(obj.optString("applyUpLimit")));
                model.setApplyDownLimit(setTV(obj.optString("applyDownLimit")));
                model.setApplyConditions(setTV(obj.optString("applyConditions")));
                model.setAuditDescription(setTV(obj.optString("auditDescription")));
                model.setProIntroduction(setTV(obj.optString("proIntroduction")));
                model.setRateLimit(setTV(obj.optString("rateLimit")));
                model.setLoanPeriod(setTV(obj.optString("loanPeriod")));
                model.setApplyProcess(setTV(obj.optString("applyProcess")));
                data.add(model);
            }
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void appllySdk(final Context mContext, String loanMerId, final String merName) {
        if ("DK_DS".equals(loanMerId)){//功夫贷
            ActiveNaviUtils.toGFDSDK((AppBaseActivity) mContext);
        }else if ("DK_51".equals(loanMerId)){
            ActiveNaviUtils.to51DSDK1((AppBaseActivity) mContext);

        } else if ("DK_PA".equals(loanMerId)){//平安
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            new KeplerUtil().enterKepler((AppBaseActivity) mContext,merName);
        }else if("DK_YFQ".equals(loanMerId)){//易分期
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            getYFQurl(mContext);
        }else if("DK_TNH".equals(loanMerId)){//易分期
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

    public String setTV(String tv){
        if (tv==null){
            return "";
        }else {
            return tv;
        }
    };
    public static boolean setBooean(JSONObject data,String tv){
        if (data.has(tv)){
            return data.optBoolean(tv);
        }else {
            if ("isBlackAcct".equals(tv))
                 return false;
            else if ("acctTypeIsPublic".equals(tv))
                return false;
            else
                return true;
        }
    };
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
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
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
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
}

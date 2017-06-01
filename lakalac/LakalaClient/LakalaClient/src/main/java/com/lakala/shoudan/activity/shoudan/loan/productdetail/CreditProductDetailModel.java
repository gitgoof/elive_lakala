package com.lakala.shoudan.activity.shoudan.loan.productdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.net.HttpRequestParams;
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
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.loan.CameraActivity;
import com.lakala.shoudan.activity.shoudan.loan.kepler.KeplerUtil;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListModel;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class CreditProductDetailModel implements CreditProductDetailContract.Model{
    @Override
    public void applyCheck(final Context mContext, final CreditProductDetailContract.CreditBusinessView creditBusinessView, final CreditListModel item) {
        creditBusinessView.showLoading();
        BusinessRequest businessRequest = RequestFactory.getRequest((Activity) mContext, RequestFactory.Type.Loan_Apply_Check);
        HttpRequestParams params =businessRequest.getRequestParams();
        params.put("loanLogo",item.getLoanLogo());
        params.put("loanName",item.getLoanProName());
        params.put("loanMerId",item.getLoanMerId());
        params.put("loanProId",item.getLoanProId());
        params.put("loanRule", item.getLoanRule());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                creditBusinessView.hideLoading();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject data = new JSONObject(resultServices.retData);
                        CreditListModel model=CreditListModel.optData(data, item);
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

    @Override
    public void appllySdk(Context mContext, String loanMerId, String loanProName) {
        if ("DK_DS".equals(loanMerId)){
            ActiveNaviUtils.toGFDSDK((AppBaseActivity) mContext);
        }else if ("DK_51".equals(loanMerId)){
            ActiveNaviUtils.to51DSDK1((AppBaseActivity) mContext);
        } else if ("DK_PA".equals(loanMerId)){
            if (((AppBaseActivity)mContext).getProgressDialog() != null && ((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
            new KeplerUtil().enterKepler((Activity) mContext,loanProName);
        } else if("DK_YFQ".equals(loanMerId)){
            getYFQurl(mContext);
        } else if("DK_TNH".equals(loanMerId)){
            getTNHurl(mContext);
        }else {
            if (((AppBaseActivity)mContext).getProgressDialog()!=null&&((AppBaseActivity)mContext).isProgressisShowing()) {
                ((AppBaseActivity)mContext).hideProgressDialog();
            }
        }
    }

    public void getYFQurl(final Context context){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/easyStageH5/createUrl", HttpRequest.RequestMethod.GET,false);
        businessRequest.getRequestParams().put("realName", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        businessRequest.getRequestParams().put("idCard",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (((AppBaseActivity)context).getProgressDialog()!=null&&((AppBaseActivity)context).isProgressisShowing()) {
                    ((AppBaseActivity)context).hideProgressDialog();
                }
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
                if (((AppBaseActivity)context).getProgressDialog()!=null&&((AppBaseActivity)context).isProgressisShowing()) {
                    ((AppBaseActivity)context).hideProgressDialog();
                }
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
    public void getTNHurl(final Context context){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"/v1.0/easyStageH5/createTNHUrl", HttpRequest.RequestMethod.GET,false);
        businessRequest.getRequestParams().put("realName", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        businessRequest.getRequestParams().put("idCard",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (((AppBaseActivity)context).getProgressDialog()!=null&&((AppBaseActivity)context).isProgressisShowing()) {
                    ((AppBaseActivity)context).hideProgressDialog();
                }
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
                if (((AppBaseActivity)context).getProgressDialog()!=null&&((AppBaseActivity)context).isProgressisShowing()) {
                    ((AppBaseActivity)context).hideProgressDialog();
                }
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
}

package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.wallet.bean.AccountInfo;
import com.lakala.shoudan.common.CommonBaseRequest;
import com.lakala.shoudan.common.util.ServiceManagerUtil;

/**
 * Created by LMQ on 2015/12/17.
 */
public class WalletServiceManager<T extends CommonBaseRequest> {
    private String noPwdFlag;//免密标识
    private String trsPasswordFlag;//支付密码标识
    private String questionFlag;//密保标识
    private String noPwdAmount;//免密金额

    private AccountInfo accountInfo;
    private static final WalletServiceManager instance = new WalletServiceManager();

    public static WalletServiceManager getInstance(){
        return instance;
    }

    private WalletServiceManager() {
    }
    public void start(Context context,BusinessRequest request){
        CommonBaseRequest params = new CommonBaseRequest(context);
        start((T)params, request, null);
    }
    public void start(T params,BusinessRequest request){
        start(params, request, null);
    }
    /**
     *
     * @param params 请求参数
     * @param request BusinessRequest,需要设置好url后，作为参数传入
     * @param callback 请求回调，如果request已经设置回调，可传入null
     */
    public void start(T params, final BusinessRequest request, final ServiceResultCallback callback){
        ServiceManagerUtil serviceManagerUtil =new ServiceManagerUtil();
        serviceManagerUtil.start(params,request,callback);

    }

    public String getNoPwdFlag() {
        return noPwdFlag;
    }

    public void setNoPwdFlag(String noPwdFlag) {
        this.noPwdFlag = noPwdFlag;
    }

    public String getTrsPasswordFlag() {
        return trsPasswordFlag;
    }

    public void setTrsPasswordFlag(String trsPasswordFlag) {
        this.trsPasswordFlag = trsPasswordFlag;
    }

    public String getQuestionFlag() {
        return questionFlag;
    }

    public void setQuestionFlag(String questionFlag) {
        this.questionFlag = questionFlag;
    }

    public String getNoPwdAmount() {
        return noPwdAmount;
    }

    public void setNoPwdAmount(String noPwdAmount) {
        this.noPwdAmount = noPwdAmount;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }
}

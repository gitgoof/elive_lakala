package com.lakala.shoudan.bll.service.shoudan;

import com.lakala.shoudan.bll.service.shoudan.BaseServiceShoudanResponse;
import com.lakala.shoudan.datadefine.SerializableRecords;

/**
 * 交易记录查询返回抽象类
 * Created by More on 14-1-14.
 */
public class QueryRecordsResponse extends BaseServiceShoudanResponse {

    private SerializableRecords cancelableRecords = new SerializableRecords();

    private String successCount ;//成功笔数
    private String successAmount;//成功金额
    private String cancelCount;//撤销笔数
    private String cancelAmount;//撤销金额

    public void setCancelableRecords(SerializableRecords cancelableRecords) {
        this.cancelableRecords = cancelableRecords;
    }

    public SerializableRecords getCancelableRecords() {
        return cancelableRecords;
    }

    @Override
    public void setErrMsg(String errMsg) {
        super.setErrMsg(errMsg);
    }

    public void setCancelAmount(String cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    @Override
    public boolean isPass() {
        return super.isPass();
    }

    @Override
    public String getErrMsg() {
        return super.getErrMsg();
    }

    public void setCancelCount(String cancelCount) {
        this.cancelCount = cancelCount;
    }

    @Override
    public void setPass(boolean pass) {
        super.setPass(pass);
    }

    public void setSuccessAmount(String successAmount) {
        this.successAmount = successAmount;
    }

    public void setSuccessCount(String successCount) {
        this.successCount = successCount;
    }

    public String getSuccessCount() {
        return successCount;
    }

    public String getSuccessAmount() {
        return successAmount;
    }

    public String getCancelCount() {
        return cancelCount;
    }

    public String getCancelAmount() {
        return cancelAmount;
    }

}


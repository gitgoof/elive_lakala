package com.lakala.shoudan.common;

import android.support.annotation.Nullable;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.bll.service.shoudan.QueryRecordsResponse;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.RecordDetail;
import com.lakala.shoudan.datadefine.SerializableRecords;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/11/24.
 */
public abstract class RecordsResultCallback implements ServiceResultCallback{
    /**
     * 交易查询
     */

    private static final String DEAL_DATE_TIME = "dealStartDateTime";// 交易时间
    private static final String DEAL_TYPE_NAME = "dealTypeName";// 交易类型
    private static final String DEAL_TYPE_CODE = "dealTypeCode"; // 交易类型码
    private static final String DEAL_AMOUNT = "dealAmount";// 交易金额
    private static final String HANDLING_CHARGE = "handlingCharge";// 手续费
    private static final String PAYMENT_ACCOUNT = "paymentAccount";// 付款卡号
    private static final String COLLECTION_ACCOUNT = "collectionAccount";// 收款卡号
    private static final String STATUS = "status";// 交易状态
    private static final String SYS_SEQ = "sysSeq";// 检索号
    private static final String SID = "sid";// 交易Sid
    private static final String PASM = "pasm";// 刷卡器序列号
    private static final String PAYMENT_MOBILE = "paymentMobile";// 支付手机号
    private static final String SERIES = "series";// 流水号
    private static final String WITH_DRAW = "isWithdrawInfo";// 是否被撤销
    private static final String AUTHCODE = "authCode";// 授权码
    private static final String POSSTATUS = "posStatus"; // X0 收款成功,X1 收款失败,Y0
    // 撤销成功,Y1 撤销失败
    private static final String MERCHANTCODE = "merchantCode"; // 商户号
    private static final String POSOGNAME = "posOgName";// 收单机构
    private static final String BATCHNO = "batchNo";// 批次号
    private static final String VOUCHER_CODE = "voucherCode";// 凭证
    private static final String NEED_CANCLE_PWD = "needCancelPasswd";// 是否需要输入pin
    private static final String BUS_NAME = "busName";// 业务名
    private final String SUCCESS_COUNT = "successCount"; // 成功笔数
    private final String SUCCESS_AMOUNT = "successAmount"; // 成功金额
    private final String CANCEL_COUNT = "cancelCount"; // 撤销笔数
    private final String CANCEL_AMOUNT = "cancelAmount"; // 撤销金额
    private final String MERCHANT_NAME = "merchantName";// 商户名
    public abstract void onSuccess(@Nullable QueryRecordsResponse response);

    @Override
    public void onSuccess(ResultServices resultServices) {
        QueryRecordsResponse rsp = null;
        try {
            rsp = getRecordsResponse(resultServices);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onSuccess(rsp);
    }
    private QueryRecordsResponse getRecordsResponse(ResultServices result) throws JSONException {
        QueryRecordsResponse rsp = new QueryRecordsResponse();
        SerializableRecords cancelableRecords = new SerializableRecords();
        if (result.isRetCodeSuccess()) {
            // 成功
            rsp.setPass(true);
            JSONObject jsonObject = new JSONObject(result.retData);
            JSONObject pageResult = jsonObject.getJSONObject("pageResult");
            cancelableRecords.setTotalPage(
                    Integer.parseInt(
                            pageResult.getString("totalPage")
                    )
            );// 总页数
            cancelableRecords.setPrePage(Integer.parseInt(pageResult.getString("pageNo"))
            );// 当前页
            int totalCount = Integer.parseInt(pageResult.getString("totalCount"));
            cancelableRecords.setTotalCount(totalCount);
            // if (totalCount == 0) {
            // // 获取交易记录为空
            // rsp.setCancelableRecords(cancelableRecords);
            // return rsp;
            // }

            rsp.setSuccessCount(
                    Util.trim(
                            jsonObject.optString(SUCCESS_COUNT)
                    )
            );// 成功笔数
            rsp.setSuccessAmount(
                    Util.trim(
                            jsonObject.optString(SUCCESS_AMOUNT)
                    )
            );// 成功金额


//            rsp.setCancelCount(Util.trim(jsonObject.optString(CANCEL_COUNT)));// 撤销笔数
//            rsp.setCancelAmount(
//                    Util.trim(
//                            jsonObject.optString(CANCEL_AMOUNT)
//                    )
//            );// 撤销金额


            // cancelTotalCount 撤销总笔数
            if (jsonObject.get("dealDetails") instanceof JSONArray) {
                JSONArray recordArray = jsonObject.getJSONArray("dealDetails");

//                JSONArray totalCountArray = jsonObject.getJSONArray("dealCount");
                List<SerializableRecords.TransTotal> transTotalList = new ArrayList<SerializableRecords.TransTotal>();
//                for (int i = 0; i < totalCountArray.length(); i++) {
//                    JSONObject tempJb = totalCountArray.getJSONObject(i);
//                    SerializableRecords.TransTotal tempTransTotal = new SerializableRecords.TransTotal();
//                    tempTransTotal.setDealTypeName(
//                            tempJb.getString("dealTypeName")
//                    );
//                    tempTransTotal.setSuccessCount(
//                            tempJb.getString("successCount")
//                    );
//                    tempTransTotal.setSuccessAmount(
//                            tempJb.getString("successAmount")
//                    );
//                    tempTransTotal.setDealTypeCode(tempJb.optString("dealTypeCode"));
//                    transTotalList.add(tempTransTotal);
//                }

                boolean hasCollection = false;
                String presentDealTypeName = "";
                for (int i = 0; i < transTotalList.size(); i++) {
                    if (transTotalList.get(i).getDealTypeName().equals("收款") || "扫码收款"
                            .equals(transTotalList.get(i).getDealTypeName())) {
                        hasCollection = true;
                        presentDealTypeName = transTotalList.get(i).getDealTypeName();
                    }
                }

                if (!hasCollection && totalCount != 0) {
                    SerializableRecords.TransTotal tempTransTotal = new SerializableRecords.TransTotal();
                    tempTransTotal.setDealTypeName(presentDealTypeName);
                    tempTransTotal.setSuccessCount(rsp.getSuccessCount());
                    tempTransTotal.setSuccessAmount(rsp.getSuccessAmount());
                    transTotalList.add(tempTransTotal);
                }

                cancelableRecords.setTransTotalList(transTotalList);

                List<RecordDetail> recordDetailList = new ArrayList<RecordDetail>();

                recordDetailList.addAll(unpackRecordsJsonArray(recordArray));
                cancelableRecords.setRecordDetailList(recordDetailList);
                rsp.setCancelableRecords(cancelableRecords);
            }

            return rsp;
        } else {
            // 获取失败失败
            rsp.setErrMsg(result.retMsg);
            rsp.setPass(false);
            return rsp;
        }
    }


    /**
     * 解析 格式为 JSONArry 的全部交易记录
     *
     * @param recordArray
     * @return
     * @throws JSONException
     */
    private List<RecordDetail> unpackRecordsJsonArray(JSONArray recordArray)
            throws JSONException {
        List<RecordDetail> recordDetailList = new ArrayList<RecordDetail>();
        for (int i = 0; i < recordArray.length(); i++) {
            JSONObject detailJsonObject = recordArray.getJSONObject(i);
            RecordDetail record = unpackRecordJson(detailJsonObject);
            // 只显示成功的便民业务
            if (record.getPosStatus() != RecordDetail.EPosStatus.RECEIVE_SUCCESS
                    && (!"收款".equals(record.getDealTypeName()) && !"P08".equals(record.getDealTypeCode()) && !"P09".equals(record.getDealTypeCode())) )
                continue;
            recordDetailList.add(record);
        }
        return recordDetailList;
    }

    /**
     * 解析单条交易记录
     *
     * @param detailJsonObject
     * @return
     * @throws JSONException
     */
    private RecordDetail unpackRecordJson(JSONObject detailJsonObject)
            throws JSONException {
        RecordDetail record = new RecordDetail();
        String dealStartDateTime = Util.trim(detailJsonObject
                                                     .getString(DEAL_DATE_TIME));// 交易时间
        record.setDealDateTime(dealStartDateTime);
        String dealTypeName = Util.trim(detailJsonObject
                                                .getString(DEAL_TYPE_NAME));// 交易类型
        record.setDealTypeName(dealTypeName);
        String dealTypeCode = Util.trim(detailJsonObject
                                                .getString(DEAL_TYPE_CODE));// 交易类型码
        record.setDealTyepCode(dealTypeCode);
        String dealAmount = Util.trim(detailJsonObject.getString(DEAL_AMOUNT));// 交易金额
        // dealAmount=Util.formatAmount(Double.parseDouble(dealAmount)/100+"");
        dealAmount = Double.parseDouble(dealAmount) / 100 + "";
        record.setDealAmount(dealAmount);
//        String handlingCharge = Util.trim(detailJsonObject
//                                                  .getString(HANDLING_CHARGE));// 手续费
//        if (handlingCharge != null && !handlingCharge.equals("")) {
//            handlingCharge = Util.formatAmount(Double
//                                                       .parseDouble(handlingCharge) / 100 + "");
//        }
//        record.setHandlingCharge(handlingCharge);
        String paymentAccount = Util.trim(detailJsonObject
                                                  .getString(PAYMENT_ACCOUNT));// 付款卡号
        record.setPaymentAccount(paymentAccount);
//        String collectionAccount = Util.trim(detailJsonObject
//                                                     .getString(COLLECTION_ACCOUNT));// 收款卡号
//        record.setCollectionAccount(collectionAccount);
        String status = Util.trim(detailJsonObject.getString(STATUS));// 交易状态
        record.setStatus(status);
        String sysSeq = Util.trim(detailJsonObject.getString(SYS_SEQ));// 检索参考号
        record.setSysSeq(sysSeq);
        String sid = Util.trim(detailJsonObject.getString(SID));// 交易的 Sid
        record.setSid(sid);
        String pasm = Util.trim(detailJsonObject.getString(PASM));// 刷卡器序列号
        record.setPasm(pasm);
//        String paymentMobile = Util.trim(detailJsonObject
//                                                 .getString(PAYMENT_MOBILE));// 支付手机号
//        record.setPaymentMobile(paymentMobile);
//        String series = Util.trim(detailJsonObject.getString(SERIES));
//        record.setSeries(series);
        String isWithdraw = Util.trim(String.valueOf(detailJsonObject.getString(WITH_DRAW)));// 是否被撤销
        record.setIsWithDraw(isWithdraw);
        String authCode = Util.trim(detailJsonObject.getString(AUTHCODE));// 授权码
        record.setAuthCode(authCode);
//        String posStatus = Util.trim(detailJsonObject.getString(POSSTATUS));// 收款状态
        // X0
        // 收款成功,X1
        // 收款失败,Y0
        // 撤销成功,Y1
        // 撤销失败
//        record.setPosStatus(posStatus);
        String merChantCode = Util.trim(detailJsonObject
                                                .getString(MERCHANTCODE));// 商户号
        record.setMerChantCode(merChantCode);
        String merchantName = Util.trim(detailJsonObject
                                                .getString(MERCHANT_NAME));// 商户名
        record.setMechantName(merchantName);
        String posOGName = Util.trim(detailJsonObject.getString(POSOGNAME));// 收单机构
        record.setPosOGName(posOGName);
        String batchNo = Util.trim(detailJsonObject.getString(BATCHNO)); // 批次号
        record.setBatchNo(batchNo);
        String voucherCode = Util
                .trim(detailJsonObject.getString(VOUCHER_CODE));// 凭证号
        record.setVoucherCode(voucherCode);
//        String needCancelpwd = Util.trim(detailJsonObject
//                                                 .getString(NEED_CANCLE_PWD));// 是否需要输入 pin
//        record.setNeedCancelPwd(needCancelpwd);
        String busName = Util.trim(detailJsonObject.getString(BUS_NAME));// 业务名
        record.setBusname(busName);
        if (!detailJsonObject.isNull("notesdesc")) {
            record.setTips(detailJsonObject.getString("notesdesc"));
        }
        record.setSign(detailJsonObject.optBoolean("sign",false));
        return record;
    }

}

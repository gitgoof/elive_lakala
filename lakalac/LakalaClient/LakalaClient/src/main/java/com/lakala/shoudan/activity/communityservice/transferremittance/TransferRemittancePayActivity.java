package com.lakala.shoudan.activity.communityservice.transferremittance;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.loopj.lakala.http.RequestParams;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/3/13.
 */
public class TransferRemittancePayActivity extends NewCommandProtocolPaymentActivity {
    private RemittanceTransInfo info;

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.transfer_remittance);
        info = (RemittanceTransInfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
//        super.startPayment(swiperInfo);

        checkAmountLimitInfo(swiperInfo);
    }

    /**
     * 检查大额开通金额及普通转账单笔最大金额
     */
    private void checkAmountLimitInfo(SwiperInfo swiperInfo){
        //大额转账
//        double maxAmount = 100000000;
//        if(Double.parseDouble(info.getAmount()) > maxAmount){
//        }else{
            queryRemittanceData(swiperInfo);
//        }
    }
    /**
     * query order info Before Remittance
     * 查询交易订单信息
     */
    private void queryRemittanceData(final SwiperInfo swiperInfo){
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.TRADE);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid","M50010");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("billno",info.getCardNumber());
        params.put("blname",info.getName());
        params.put("bankname", info.getOpenBank());
        params.put("ebkcode",info.getBankCode());
        params.put("qscode",info.getBankCode());
        params.put("ebkname",info.getOpenBank());
        params.put("amount", Utils.yuan2Fen(info.getAmount()));
        params.put("mobileno", StringUtil.formatString(info.getPhone()));
        params.put("tranType",info.getTransType());
        params.put("remark",info.getRemark());
        params.put("bkcustid",ApplicationEx.getInstance().getUser().getUserId());
        params.put("bkcustlev","");
        params.put("termid",ApplicationEx.getInstance().getSession().getCurrentKSN());
        params.put("chntype", BusinessRequest.CHN_TYPE);
        params.put("chncode", BusinessRequest.CHN_CODE);


        if(swiperInfo.getDeviceInfo() != null){

            GetDeviceInfo temp = swiperInfo.getDeviceInfo();
            params.put("mver", temp.getFirmwareVersion());
            params.put("aver", temp.getAppVersion());
            params.put("pver", temp.getCommandVersion());

        }

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String fee = jsonObject.getString("fee");
                        String price = jsonObject.getString("price");
                        String billno = jsonObject.getString("billno");
                        String orderId = jsonObject.getString("orderId");
                        String sid = jsonObject.getString("sid");
                        info.setOrderId(orderId);
                        info.setTransFee(Utils.fen2Yuan(fee));
                        info.setBillno(billno);
                        info.setPrice(Utils.fen2Yuan(price));
                        info.setSrcsid(sid);
                        TransferRemittancePayActivity.super.startPayment(swiperInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    swiperManagerHandler.cancelEmv(false);
                    baseTransInfo.setMsg(resultServices.retMsg);
                    onPaymentFailed();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                baseTransInfo.setMsg(getString(R.string.http_error));
                onPaymentFailed();
            }
        });
        request.execute();
    }


    @Override
    protected void addTransParams(RequestParams params) {
        params.put("busid", "M80001");
        params.put("mobile",ApplicationEx.getInstance().getUser().getLoginName());
        params.put("srcsid",info.getSrcsid());
        params.put("billno",Utils.formatString(info.getBillno()));
        params.put("amount",Utils.yuan2Fen(info.getAmount()));
        params.put("savMark","1");
        params.put("itemId","");
        params.put("orderId",info.getOrderId());
        params.put("fee",Utils.yuan2Fen(info.getTransFee()));
        params.put("bkcustid", ApplicationEx.getInstance().getUser().getUserId());
        params.put("bkcustlev","");
    }
}

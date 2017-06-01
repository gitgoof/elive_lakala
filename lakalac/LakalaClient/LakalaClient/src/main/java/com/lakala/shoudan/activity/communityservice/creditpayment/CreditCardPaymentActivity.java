package com.lakala.shoudan.activity.communityservice.creditpayment;

import android.os.Bundle;
import android.view.View;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CreditCardEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.ui.dialog.AlertDialog;
import com.loopj.lakala.http.RequestParams;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/2/13.
 */
public class CreditCardPaymentActivity extends NewCommandProtocolPaymentActivity {

    private CreditCardPaymentInfo creditCardPaymentInfo;
    private String srcsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoudanStatisticManager.getInstance().onEvent(getEvent(), this);
    }

    public String getEvent() {
        String smsEvent = "";
        String payEvent = "";
        boolean payOpen = CreditCardEnum.PayBack.isPayOpen();
        boolean smsOpen = CreditCardEnum.PayBack.isSmsOpen();
        payEvent = payOpen ? "是" : "否";
        smsEvent = smsOpen ? "是" : "否";
        return String.format(ShoudanStatisticManager.Credit_Card_paySuccess_public_swipe_public, payEvent, smsEvent);
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(getString(R.string.credit_payment));
        creditCardPaymentInfo = (CreditCardPaymentInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {

        queryHandlingCharge(swiperInfo);

    }

    private void showConfirmFeeDialog(String fee, final SwiperInfo swiperInfo) {
        AlertDialog dialog = new AlertDialog();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("本次还款手续费：");
        stringBuilder.append(fee);
        stringBuilder.append("元。点击\"确定\"完成信用卡还款操作");
        dialog.setMessage(stringBuilder.toString());
        dialog.setButtons(new String[]{getString(R.string.button_cancel), getString(R.string.button_ok)});
        dialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                switch (index) {
                    case 0: {
                        finish();
                        break;
                    }
                    case 1: {
                        commitTransaction(swiperInfo);
                        break;
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager());
    }

    /**
     * 手续费查询
     *
     * @param swiperInfo
     */
    private void queryHandlingCharge(final SwiperInfo swiperInfo) {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.TRADE);

        HttpRequestParams params = request.getRequestParams();
        params.put("busid", "M50001");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("mobileno", creditCardPaymentInfo.getMobileno());
        params.put("inpan", creditCardPaymentInfo.getNumber());
        params.put("amount", Utils.yuan2Fen(creditCardPaymentInfo.getAmount()));
        params.put("issms", creditCardPaymentInfo.getIssms());

        params.put("otrack", swiperInfo.getEncTracks());
        params.put("rnd", swiperInfo.getRandomNumber());
        params.put("pinkey", swiperInfo.getPin());
        params.put("posemc", swiperInfo.getPosemc());
        params.put("icc55", swiperInfo.getIcc55());
        params.put("cardsn", swiperInfo.getCardsn());
        params.put("track2", swiperInfo.getTrack2());
        if (swiperInfo.getCardType() != SwiperInfo.CardType.MSC)
            params.put("pan", swiperInfo.getMaskedPan());
        params.put("track1", swiperInfo.getTrack1());
        params.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
        params.put("chntype", BusinessRequest.CHN_TYPE);
        params.put("chncode", BusinessRequest.CHN_CODE);

        if (swiperInfo.getDeviceInfo() != null) {

            GetDeviceInfo temp = swiperInfo.getDeviceInfo();
            params.put("mver", temp.getFirmwareVersion());
            params.put("aver", temp.getAppVersion());
            params.put("pver", temp.getCommandVersion());

        }

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String price = jsonObject.getString("price");
                        creditCardPaymentInfo.setPrice(Utils.fen2Yuan(price));
                        String fee = Utils.fen2Yuan(jsonObject.getString("fee"));
                        creditCardPaymentInfo.setFee(fee);
                        srcsid = jsonObject.getString("sid");

                        CreditCardPaymentActivity.super.startPayment(swiperInfo);
//                        if(!"0.00".equals(fee)){//手续费不为0
//                            showConfirmFeeDialog(fee,swiperInfo);
//                        }else{
//
//                        }
                    } catch (JSONException e) {
                        LogUtil.print(e);
                        swiperManagerHandler.cancelEmv(false);
                        baseTransInfo.setMsg(getString(R.string.plat_http_004));
                        onPaymentFailed();
                    }
                } else {
                    //获取手续费失败 判断如果是ic交易， 需要取消当前交易 swiperManagerHandler.cancelEmv();  展示错误
                    //要set errmsg onPaymentFailed();
                    swiperManagerHandler.cancelEmv(false);
                    baseTransInfo.setMsg(resultServices.retMsg);
                    onPaymentFailed();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                //获取手续费失败
                //要set 网络异常的msg onPaymentFailed();
                baseTransInfo.setMsg(getString(R.string.http_error));
                onPaymentFailed();
            }
        });
        request.execute();
    }

    /**
     * 完成信用卡还款
     *
     * @param swiperInfo
     */
    private void commitTransaction(SwiperInfo swiperInfo) {
        boolean showProgress = true;//是否显示正在加载提示框
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.TRADE);
        request.setAutoShowProgress(showProgress);
        request.obtainTransRequestParams(swiperInfo);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid", "M90000");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("srcsid", srcsid);
        params.put("tdtm", Utils.createTdtm());
        params.put("inpan", creditCardPaymentInfo.getNumber());
        params.put("amount", Utils.yuan2Fen(creditCardPaymentInfo.getAmount()));
        params.put("issms", creditCardPaymentInfo.getIssms());
        params.put("mobileno", creditCardPaymentInfo.getMobileno());
        request.setResponseHandler(this);
        request.execute();
    }

    @Override
    protected void addTransParams(RequestParams params) {
        params.put("busid", "M90000");
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("srcsid", srcsid);
        params.put("tdtm", Utils.createTdtm());
        params.put("inpan", creditCardPaymentInfo.getNumber());
        params.put("amount", Utils.yuan2Fen(creditCardPaymentInfo.getAmount()));
        params.put("issms", creditCardPaymentInfo.getIssms());
        params.put("mobileno", creditCardPaymentInfo.getMobileno());
    }

    @Override
    protected void reStartSwipe() {
        srcsid = null;
        startSwipe();
    }
}

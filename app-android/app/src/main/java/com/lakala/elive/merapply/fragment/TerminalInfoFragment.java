package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class TerminalInfoFragment extends LazyFragment {

    private TextView tvTerminal, tvModels, tvDaiJiKaFeeRate, tvDaiJiKaFeeMax, tvJieJiKaFeeRate,
            tvJieJiKaFeeMax, tvYinLianKaFeeRate, tvYinLianKaFeeMax, tvALiPayFeeRate, tvALiPayFeeMax,
            tvWeiXinFeeRate, tvWeiXinFeeMax, tvBaiDuFeeRate, tvBaiDuFeeMax, tvLaKaLaFeeRate,
            tvLaKaLaFeeMax, tvJianHangFeeRate, tvJianHangFeeMax, tvSalesWay, tvDeposit, tvRent, tvAmount, tvSettlementCycle,tvCommFee;
    private RelativeLayout rlDeposit, rlRent, rlAmount;

    private MerApplyDetailsResp.ContentBean contentBean;

    private static final String DEBIT_CARD = "DEBIT_CARD";//借记卡
    private static final String CREDIT_CARD = "CREDIT_CARD";//贷记卡
    private static final String WECHAT_PAY_FEE = "WECHAT_PAY_FEE";//微信支付
    private static final String ALIPAY_WALLET_FEE = "ALIPAY_WALLET_FEE";//支付宝
    private static final String BAIDU_WALLET_FEE = "BAIDU_WALLET_FEE";//百度钱包
    private static final String LAKALA_WALLET_FEE = "LAKALA_WALLET_FEE";//拉卡拉钱包
    private static final String UNIONPAY_WALLET_FEE = "UNIONPAY_WALLET_FEE";//银联境外卡
    private static final String JIANHANG_WALLET_FEE = "JIANHANG_WALLET_FEE";//建行钱包

    public static TerminalInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        TerminalInfoFragment fragment = new TerminalInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_terminal_info;
    }

    @Override
    protected void bindView() {
        tvTerminal = findView(R.id.tv_terminal);
        tvModels = findView(R.id.tv_models);
        tvSettlementCycle = findView(R.id.tv_settlement_cycle);//结算周期
        tvSalesWay = findView(R.id.tv_sales_way);//终端领取方式
        rlDeposit = findView(R.id.rl_deposit);//押金
        tvDeposit = findView(R.id.tv_deposit);
        rlRent = findView(R.id.rl_rent);//租金
        tvRent = findView(R.id.tv_rent);
        rlAmount = findView(R.id.rl_amount);//售价
        tvAmount = findView(R.id.tv_amount);

        //贷记卡
        tvDaiJiKaFeeRate = findView(R.id.tv_daijika_feerate);
        tvDaiJiKaFeeMax = findView(R.id.tv_daijika_feemax);
        //借记卡
        tvJieJiKaFeeRate = findView(R.id.tv_jiejika_feerate);
        tvJieJiKaFeeMax = findView(R.id.tv_jiejika_feemax);
        //银联
        tvYinLianKaFeeRate = findView(R.id.tv_yinlianka_feerate);
        tvYinLianKaFeeMax = findView(R.id.tv_yinlianka_feemax);
        //支付宝
        tvALiPayFeeRate = findView(R.id.tv_ali_pay_feerate);
        tvALiPayFeeMax = findView(R.id.tv_ali_pay_feemax);
        //微信
        tvWeiXinFeeRate = findView(R.id.tv_weixin_feerate);
        tvWeiXinFeeMax = findView(R.id.tv_weixin_feemax);
        //百度钱包
        tvBaiDuFeeRate = findView(R.id.tv_baidu_feerate);
        tvBaiDuFeeMax = findView(R.id.tv_baidu_feemax);
        //拉卡拉钱包
        tvLaKaLaFeeRate = findView(R.id.tv_lakala_feerate);
        tvLaKaLaFeeMax = findView(R.id.tv_lakala_feemax);
        //建行钱包
        tvJianHangFeeRate = findView(R.id.tv_jianhang_feerate);
        tvJianHangFeeMax = findView(R.id.tv_jianhang_feemax);
        tvCommFee = findView(R.id.tv_comm_fee);
    }

    @Override
    protected void bindEvent() {

    }


    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            MerApplyDetailsResp.ContentBean.TerminalInfo terminalInfo = contentBean.getTerminalInfo();
            if (null != terminalInfo) {
                setTextContent(terminalInfo);
            }
            List<MerApplyDetailsResp.ContentBean.CardAppRateInfoListBean> cardAppRateInfoList = contentBean.getCardAppRateInfoList();
            if (null != cardAppRateInfoList && cardAppRateInfoList.size() > 0) {
                setTextContent2(cardAppRateInfoList);
            }
            if (null != contentBean.getMerOpenInfo()) {
                tvSettlementCycle.setText(getNoEmptyString(contentBean.getMerOpenInfo().getSettlePeriodStr()));
            }
        }
    }


    private void setTextContent(MerApplyDetailsResp.ContentBean.TerminalInfo terminalInfo) {
        tvTerminal.setText(terminalInfo.getDeviceCnt());//"POS", "POSPLUS", "MPOS"
        tvCommFee.setText(terminalInfo.getCommFee() + "");

        switch (terminalInfo.getDeviceType()) {
            case "POS":
                tvModels.setText("传统POS机");
                break;
            case "POSPLUS":
                tvModels.setText("智能POS机");
                break;
            case "MPOS":
                tvModels.setText("手收POS机");
                break;
        }

        switch (getNoEmptyString(terminalInfo.getDeviceDrawMethod())) {
            case "44"://租赁
                tvSalesWay.setText("租赁");
                rlDeposit.setVisibility(View.VISIBLE);
                rlRent.setVisibility(View.VISIBLE);
                rlAmount.setVisibility(View.GONE);
                tvDeposit.setText(terminalInfo.getDeviceDeposit() + "");
                tvRent.setText(terminalInfo.getDeviceRent() + "");
                break;
            case "45"://出售
                tvSalesWay.setText("出售");
                rlDeposit.setVisibility(View.GONE);
                rlRent.setVisibility(View.GONE);
                rlAmount.setVisibility(View.VISIBLE);
                tvAmount.setText(terminalInfo.getDeviceSaleAmount() + "");
                break;
            case "46"://赠送
                tvSalesWay.setText("赠送");
                rlDeposit.setVisibility(View.GONE);
                rlRent.setVisibility(View.GONE);
                rlAmount.setVisibility(View.GONE);
                break;
        }
    }

    private void setTextContent2(List<MerApplyDetailsResp.ContentBean.CardAppRateInfoListBean> cardAppRateInfoList) {

        NumberFormat nf = NumberFormat.getInstance();
        if (null != cardAppRateInfoList && cardAppRateInfoList.size() > 0) {
            for (MerApplyDetailsResp.ContentBean.CardAppRateInfoListBean cardAppRateInfoBean : cardAppRateInfoList) {
                switch (cardAppRateInfoBean.getCardType()) {
                    case CREDIT_CARD:
                        tvDaiJiKaFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
//                        tvDaiJiKaFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case DEBIT_CARD:
                        tvJieJiKaFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
                        tvJieJiKaFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case UNIONPAY_WALLET_FEE:
                        tvYinLianKaFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
//                        tvYinLianKaFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case ALIPAY_WALLET_FEE:
                        tvALiPayFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
                        tvALiPayFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case WECHAT_PAY_FEE:
                        tvWeiXinFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
                        tvWeiXinFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case BAIDU_WALLET_FEE:
                        tvBaiDuFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
//                        tvBaiDuFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case LAKALA_WALLET_FEE:
                        tvLaKaLaFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
//                        tvLaKaLaFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                    case JIANHANG_WALLET_FEE:
                        tvJianHangFeeRate.setText(nf.format(mul(cardAppRateInfoBean.getBaseFeeRate(), 100)));
//                        tvJianHangFeeMax.setText(nf.format(cardAppRateInfoBean.getBaseFeeMax()));
                        break;
                }
            }
        }
    }


    /**
     * 乘,BigDecimal用
     *
     * @param v1
     * @param v2
     * @return result
     */
    public static BigDecimal mul(Object v1, Object v2) {
        BigDecimal result = null;
        // if(v1!=null && v2!=null){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        result = b1.multiply(b2);
        // }
        return result;
    }
}

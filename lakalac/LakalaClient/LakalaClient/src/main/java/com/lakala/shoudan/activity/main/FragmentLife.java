package com.lakala.shoudan.activity.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceTransInfo;
import com.lakala.shoudan.activity.more.MoreActivity;
import com.lakala.shoudan.activity.shoudan.merchantpayment.MerchantPayQueryInstBillActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeQueryActivity;
import com.lakala.shoudan.component.IconTextView;
import com.lakala.ui.component.NavigationBar;

/**
 * 生活
 * Created by Administrator on 2017/2/23.
 */
public class FragmentLife extends BaseFragment implements NavigationBar.OnNavBarClickListener, View.OnClickListener {
    private View view;
    private NavigationBar navigationBar;
    private IconTextView creditCardRefund,transferAcounter,blanceEnquiry,phoneRecharge,specialPay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_life, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        navigationBar= (NavigationBar) view.findViewById(R.id.navigation_bar);
        navigationBar.setTitle("生活");
        navigationBar.setActionBtnText("交易记录");
        navigationBar.setOnNavBarClickListener(this);
        creditCardRefund= (IconTextView) view.findViewById(R.id.credit_card_refund);//信用卡还款
        transferAcounter= (IconTextView) view.findViewById(R.id.transfer_acounter);//转账
        blanceEnquiry= (IconTextView) view.findViewById(R.id.blance_enquiry);//余额查询
        phoneRecharge= (IconTextView) view.findViewById(R.id.phone_recharge);//手机充值
        specialPay= (IconTextView) view.findViewById(R.id.special_pay);//特约商户续费
        creditCardRefund.setOnClickListener(this);
        transferAcounter.setOnClickListener(this);
        blanceEnquiry.setOnClickListener(this);
        phoneRecharge.setOnClickListener(this);
        specialPay.setOnClickListener(this);



    }

    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        if (navBarItem==NavigationBar.NavigationBarItem.action){
            queryDealType(context, RecordsQuerySelectionActivity.Type.LIFE_RECORD,false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.credit_card_refund://信用卡还款
                BusinessLauncher.getInstance().start("creditcard_payment");
                break;
            case R.id.transfer_acounter://转账
                BusinessLauncher.getInstance().start("remittance");
                break;
            case R.id.blance_enquiry://余额查询
                Intent intent = new Intent();
                intent.putExtra(ConstKey.TRANS_INFO, new BalanceTransInfo());
                BusinessLauncher.getInstance().start("balance_query", intent);
                break;
            case R.id.phone_recharge://手机充值
                BusinessLauncher.getInstance().start("mobile_recharge");
                break;
            case R.id.special_pay://特约商户缴费
             //   ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.More_MerchantSign, this);
                showProgressWithNoMsg();
                TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onError(String msg) {
                        hideProgressDialog();
                        ToastUtil.toast(context, getString(R.string.socket_fail));
                        LogUtil.print(msg);
                    }

                    @Override
                    public void onSuccess() {
                        hideProgressDialog();
                        Intent intent = new Intent(context, MerchantPayQueryInstBillActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
    }
    public  void queryDealType(final FragmentActivity context, final
    RecordsQuerySelectionActivity.Type type, final boolean fromResultPage) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    Intent intent = new Intent();
                    intent.putExtra("retData", resultServices.retData);
                    intent.setClass(context, TradeQueryActivity.class);
                    if (type != null) {
                        intent.putExtra("type", type.getValue());
                    }
                    if (fromResultPage) {
                        context.startActivityForResult(intent, 0);
                    } else {
                        context.startActivity(intent);
                    }

                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, getString(R.string.socket_fail));
               hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        if (type == RecordsQuerySelectionActivity.Type.COLLECTION_RECORD) {
            BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.COLLECTION_DETAIL_QUERY);
            request.setResponseHandler(callback);
            request.execute();
        } else if (type == RecordsQuerySelectionActivity.Type.LIFE_RECORD) {
            BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.LIFE_DETAIL_QUERY);
            request.setResponseHandler(callback);
            request.execute();
        }
    }
}

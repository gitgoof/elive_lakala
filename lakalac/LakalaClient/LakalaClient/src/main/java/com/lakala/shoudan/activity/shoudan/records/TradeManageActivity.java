package com.lakala.shoudan.activity.shoudan.records;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.wallet.WalletTransDetailActivity;
import com.lakala.shoudan.activity.wallet.request.GetComPrensiveInfoRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTransDetailRequest;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 交易记录
 */
public class TradeManageActivity extends AppBaseActivity implements
        OnClickListener {

    private RelativeLayout tradeRL, transferRL;
    private RelativeLayout drawMoneyRL;// 立即提款
    private RelativeLayout lifeTrade;
    private RelativeLayout walletTrade;
    private String walletBalance;


    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            }
        }
    };
    private TextView tv_current_amount;
    private TextView tv_account_balance;
    /**
     * 可提款金额
     */
    private double amount;
    /**
     * 单日提款上限
     */
    private double dLimitMax;

    /**
     * 单笔提款下限
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_trade_manage);
        initUI();
    }

    protected void initUI() {

        tradeRL = (RelativeLayout) findViewById(R.id.rl_trade_record);
        transferRL = (RelativeLayout) findViewById(R.id.rl_transfer_record);
        navigationBar.setTitle(R.string.trans_manage);
        drawMoneyRL = (RelativeLayout) findViewById(R.id.rl_draw_money);
        lifeTrade = (RelativeLayout) findViewById(R.id.life_trade_query);
        walletTrade = (RelativeLayout) findViewById(R.id.wallet_trade_detail);

        lifeTrade.setOnClickListener(this);
        walletTrade.setOnClickListener(this);
        tradeRL.setOnClickListener(this);
        transferRL.setOnClickListener(this);
        DrawButtonClickListener listener = new DrawButtonClickListener(this);
        listener.setStatistic(ShoudanStatisticManager.Do_Public_Record_Input);
        DoEnum.Do.setData(null, false, false);
        drawMoneyRL.setOnClickListener(listener);

        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        tv_current_amount = (TextView) findViewById(R.id.tv_current_amount);
        tv_account_balance = (TextView) findViewById(R.id.tv_account_balance);
        Double today_amount = (Double) getIntent().getSerializableExtra("today_amount");
        if (today_amount != null) {
            updateCurrentDateView(today_amount);
        } else {
            getCurrentAmount();
        }

        initDrawView();
        initDate();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApplicationEx.getInstance().getUser().getMerchantInfo().getT0() == T0Status.COMPLETED) {//如果已开通D+0提款业务，则获取可提款金额并显示
            getAccountBalance();
        }
    }

    private void getAccountBalance() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject retData = new JSONObject(resultServices.retData.toString());
                        amount = retData.getDouble("amount");
                        dLimitMax = retData.getDouble("dLimitMax");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tv_account_balance.setText(new DecimalFormat("0.00").format(amount));
                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                tv_account_balance.setText(new DecimalFormat("0.00").format(amount));
            }
        };
        CommonServiceManager.getInstance().getAccountBalance(callback);
    }

    /**
     * 根据是否开通D0业务和是否白名单初始化部分UI
     */
    private void initDrawView() {
        View tag = findViewById(R.id.draw_tag);
        View line = findViewById(R.id.draw_line);
        if (T0Status.COMPLETED == ApplicationEx.getInstance().getUser().getMerchantInfo().getT0()) {//白名单用户且已开通D0业务，显示可提款金额
            tag.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else {
            tag.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
    }

    /**
     * 获取今日收款
     */
    private void getCurrentAmount() {
        showProgressWithNoMsg();
        CommonServiceManager.TodayCollectionCallback callback = new CommonServiceManager.TodayCollectionCallback() {
            @Override
            public void onSuccess(Double amount) {
                hideProgressDialog();
                updateCurrentDateView(amount);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().queryTodayCollection(callback);
    }

    private void updateCurrentDateView(Double amount) {
        double mAmount = 0;
        if (amount != null) {
            mAmount = amount;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_current_amount.setText(df.format(mAmount));
    }

    /**
     * 初始化日期
     */
    private void initDate() {
        TextView tvDate = (TextView) findViewById(R.id.today_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        tvDate.setText(format.format(Calendar.getInstance().getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_trade_record:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .TradeRecord_CollectionRecord, context);
                queryDealType(this, RecordsQuerySelectionActivity.Type.COLLECTION_RECORD);
                break;
            case R.id.rl_transfer_record:
                Intent intent0 = new Intent(this, DrawMoneyHistoryActivity.class);
                startActivity(intent0);
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .Drawing_Recode, context);
                break;

            case R.id.life_trade_query:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .TradeRecord_FeesOfLife, context);
                queryDealType(this, RecordsQuerySelectionActivity.Type.LIFE_RECORD);

                break;

            case R.id.wallet_trade_detail:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .TradeRecord_Incomedetails, context);
                checkWalletTerminal();

                break;
            default:
                break;
        }
    }

    public void checkWalletTerminal() {

        showProgressWithNoMsg();
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onError(String msg) {
                hideProgressDialog();
            }

            @Override
            public void onSuccess() {
                getComprensiveInfo();
            }
        });

    }

    private void getComprensiveInfo() {

        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.COMPREHENSIVE_INFORMATION);
        GetComPrensiveInfoRequest params = new GetComPrensiveInfoRequest(context);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);

                        walletBalance = jsonObject.optString("walletBalance");
                        getTransDetail();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    toast(resultServices.retMsg);
                    hideProgressDialog();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }

    private void getTransDetail() {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_DETAIL_QRY);
        WalletTransDetailRequest params = new WalletTransDetailRequest(this);
        params.setPage(WalletTransDetailActivity.page);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    WalletTransDetailActivity.page++;
                    String data = resultServices.retData;
                    Intent intent = new Intent(TradeManageActivity.this, WalletTransDetailActivity.class);
                    intent.putExtra("data", data);
                    intent.putExtra("balance", walletBalance);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(TradeManageActivity.this, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }

    public static void queryDealType(final AppBaseActivity context, final
    RecordsQuerySelectionActivity.Type type) {
        queryDealType(context, type, false);
    }

    public static void queryDealType(final AppBaseActivity context, final
    RecordsQuerySelectionActivity.Type type, final boolean fromResultPage) {
        context.showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
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
                context.toastInternetError();
                context.hideProgressDialog();
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

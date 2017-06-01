package com.lakala.shoudan.activity.collection;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.UndoEnum;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.SingleLineTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by More on 15/12/18.
 */
public class CollectionEntranceActivity extends AppBaseActivity {


    @Bind(R.id.swipe_collection)
    SingleLineTextView swipeCollection;
    @Bind(R.id.scan_colleciton)
    SingleLineTextView scanColleciton;
    @Bind(R.id.large_amount_collection)
    SingleLineTextView largeAmountCollection;
    @Bind(R.id.revocation)
    SingleLineTextView revocation;
    private TextView tv_current_amount;
    private RelativeLayout drawMoneyRL;// 立即提款
    private TextView tv_account_balance;
    /**
     * 可提款金额
     */
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_entrance);
        navigationBar.setTitle("收款");
        navigationBar.setActionBtnText("交易查询");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type.COLLECTION_RECORD);
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Collection_Recode, context);
                }else if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });
        initUI();
        initDate();
        initDrawView();
    }

    @Override
    protected void initUI() {
        super.initUI();

        tv_current_amount = (TextView)findViewById(R.id.tv_current_amount);

        Double today_amount = (Double) getIntent().getSerializableExtra("today_amount");
        if(today_amount != null){
            updateCurrentDateView(today_amount);
        }else{
            getCurrentAmount();
        }

        drawMoneyRL = (RelativeLayout) findViewById(R.id.rl_draw_money);
        DrawButtonClickListener listener = new DrawButtonClickListener(this);
        listener.setStatistic(ShoudanStatisticManager
                .Do);
        DoEnum.Do.setData(null, false, true);
        drawMoneyRL.setOnClickListener(listener);

        tv_account_balance = (TextView)findViewById(R.id.tv_account_balance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ApplicationEx.getInstance().getUser().getMerchantInfo().getT0() == T0Status.COMPLETED){//如果已开通D+0提款业务，则获取可提款金额并显示
            getAccountBalance();
        }
    }

    private void getAccountBalance(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject retData = new JSONObject(resultServices.retData.toString());
                        amount = retData.getDouble("amount");
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
    private void updateCurrentDateView(Double amount){
        double mAmount = 0;
        if(amount != null){
            mAmount = amount;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_current_amount.setText(df.format(mAmount));
    }

    /**
     * 获取今日收款
     */
    private void getCurrentAmount(){
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

    private void initDate() {

        TextView textView = (TextView) findViewById(R.id.today_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        textView.setText(format.format(Calendar.getInstance().getTime()));
    }

    /**
     * 根据是否开通D0业务和是否白名单初始化部分UI
     */
    private void initDrawView(){
        View tag = findViewById(R.id.draw_tag);
        View line = findViewById(R.id.draw_line);
        if(T0Status.COMPLETED == ApplicationEx.getInstance().getUser().getMerchantInfo().getT0())
        {//白名单用户且已开通D0业务，显示可提款金额
            tag.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }else{
            tag.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.swipe_collection)
    public void swipeCollection(){

        if(CommonUtil.isMerchantValid(context)){
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                    .Collection, context);
            CollectionEnum.Colletcion.setData(null,false);
            BusinessLauncher.getInstance().start("collection_transaction");
        }

    }

    @OnClick(R.id.scan_colleciton)
    public void scanCollection(){
        if(CommonUtil.isMerchantCompleted(context)){
            ScanCodeCollectionEnum.ScanCodeCollection.setData(null, false);
            BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager(context);
            barcodeAccessManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection);
            barcodeAccessManager.check(true);
        }
    }

    @OnClick(R.id.large_amount_collection)
    public void largeAmountCollection(){
        if(CommonUtil.isMerchantValid(context)){
            LargeAmountAccessManager largeAmountAccessManager= new LargeAmountAccessManager(context);
            largeAmountAccessManager.setStatistic(ShoudanStatisticManager.LargeAmount_Collection);
            largeAmountAccessManager.check();
            LargeAmountEnum.LargeAmount.setAdvertId("");
        }
    }

    @OnClick(R.id.revocation)
    public void revocation(){
        if(CommonUtil.isMerchantValid(context)){
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo, context);
            UndoEnum.Undo.setAdvertId("");
            BusinessLauncher.getInstance().start("revocation");
        }
    }

}

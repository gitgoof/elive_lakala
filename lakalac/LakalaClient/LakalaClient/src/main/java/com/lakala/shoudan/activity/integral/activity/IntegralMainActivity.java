package com.lakala.shoudan.activity.integral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.coupon.activity.CouponHomeActivity;
import com.lakala.shoudan.activity.integral.IntegralController;
import com.lakala.shoudan.activity.integral.callback.VoucherExplainCallback;
import com.lakala.shoudan.activity.integral.callback.VoucherListCallback;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.adapter.IntegralListItemAdapter;
import com.lakala.shoudan.common.util.PhoneUtils;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.component.CustomTextView;
import com.lakala.shoudan.datadefine.Voucher;
import com.lakala.shoudan.datadefine.VoucherExplain;
import com.lakala.shoudan.datadefine.VoucherList;
import com.lakala.ui.component.CustomTableListView;
import com.lakala.ui.component.NavigationBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fengxuan on 2016/5/17.
 */
public class IntegralMainActivity extends AppBaseActivity{
    private CustomTextView tvIntegralAmount;
    private CustomTableListView lvIntegralList;
    private TextView tvBottomNote;
    private IntegralListItemAdapter mAdapter;
    private IntegralController controller;
    private CountDownLatch controllerLatcher = new CountDownLatch(2);
    private ArrayList<VoucherExplain> mData = new ArrayList<VoucherExplain>();
    private View integralView;
    private List<Voucher> voucherData = new ArrayList<Voucher>();
    private TextView tvMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_main);
        controller = IntegralController.getInstance(context);

        initUI();
        showProgressWithNoMsg();
        getVoucherExplain();
        getVoucherList();
    }

    @Override
    protected void initUI() {
        super.initUI();
        initBottomNote();
        navigationBar.setTitle("积分兑换");
        navigationBar.setActionBtnText("账单明细");
        navigationBar.setBottomImageVisibility(View.VISIBLE);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    Intent intent = new Intent(IntegralMainActivity.this, IntegralBillActivity.class);
                    startActivity(intent);
                }
            }
        });

        tvMobile = (TextView)findViewById(R.id.mobile_server);
        tvMobile.setText(getUserMobile());
        tvIntegralAmount = (CustomTextView)findViewById(R.id.integral_total);
        lvIntegralList = (CustomTableListView)findViewById(R.id.lv_integral_list);
        mAdapter = new IntegralListItemAdapter(context, mData);
        lvIntegralList.setAdapter(mAdapter);
        mAdapter.setOnExchangeClickeListener(new IntegralListItemAdapter.OnExchangeClickeListener(){
            @Override
            public void onExchangeClick(VoucherExplain voucherExplain) {
                String phone = voucherExplain.getExchange_address();
                String content = voucherExplain.getExchange_content();
                PhoneUtils.sendSMS(context, phone, content);
            }
        });

        integralView = findViewById(R.id.integral_view);
        integralView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CouponHomeActivity.start(context,voucherData, CouponHomeActivity.IsFirst.TWO);
            }
        });
        findViewById(R.id.query_integral).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mAdapter.getCount() <= 0){
                    ToastUtil.toast(context,"查询失败，请联系运营商查询");
                    return;
                }
                VoucherExplain data = mAdapter.getItem(0);
                String phone = data.getQuery_address();
                String content = data.getQuery_content();
                PhoneUtils.sendSMS(context, phone, content);
            }
        });
    }

    private void initBottomNote() {
        tvBottomNote = (TextView)findViewById(R.id.integral_bottom_note);
        int start = 0;
        int end = 0;
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("详情请咨询客服95016789或");
        start = textBuilder.length();
        textBuilder.append("查看详情>>");
        end = textBuilder.length();
        SpannableString s = new SpannableString(textBuilder.toString());
        LinkClickedListener linkClickedListener = new LinkClickedListener() {
            @Override
            public void onClick(View widget) {
                //TODO 查看详情
            }
        };
        s.setSpan(linkClickedListener, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBottomNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvBottomNote.setText(s);
        tvBottomNote.setHighlightColor(getResources().getColor(android.R.color.transparent));
    }

    private String getUserMobile(){
        return StringUtil.formatPhoneN3S4N4(ApplicationEx.getInstance().getUser().getLoginName()).replace(" ","");
    }

    private void getVoucherExplain(){
        VoucherExplainCallback callback = new VoucherExplainCallback() {
            @Override
            public void onResult(ResultServices resultServices, List<VoucherExplain> explainList) {
                onResultCallback(resultServices);
                if(resultServices.isRetCodeSuccess() && explainList != null && explainList.size() != 0){
                    VoucherExplain explain = explainList.get(0);
                    StringBuilder mobileService = new StringBuilder();
                    mobileService.append(explain.getName()).append("&nbsp;&nbsp;&nbsp;&nbsp;")
                            .append(getUserMobile());
                    tvMobile.setText(Html.fromHtml(mobileService.toString()));
                    mData.clear();
                    mData.addAll(explainList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                onEventCallback();
            }
        };
        controller.getVoucherExplain(callback);
    }

    private void getVoucherList(){
        VoucherListCallback callback = new VoucherListCallback() {
            @Override
            public void onResult(ResultServices resultServices, VoucherList voucherList) {
                onResultCallback(resultServices);
                if(resultServices.isRetCodeSuccess()){
                    String sumPrice = voucherList.getSum_price();
                    List<Voucher> vouchers = voucherList.getVouchers();
                    if(TextUtils.isEmpty(sumPrice)){
                        sumPrice = "——";
                        integralView.setVisibility(View.GONE);
                    }else {
                        integralView.setVisibility(View.VISIBLE);
                        if(vouchers != null){
                            voucherData.clear();
                            voucherData.addAll(vouchers);
                        }
                    }
                    tvIntegralAmount.setVisibility(View.VISIBLE);
                    tvIntegralAmount.setText(sumPrice);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                onEventCallback();
            }
        };
        controller.getVoucherList(callback);
    }

    private void onEventCallback(){
        controllerLatcher.countDown();
        if(controllerLatcher.getCount() == 0){
            hideProgressDialog();
            ToastUtil.toast(context,getString(R.string.socket_fail));
        }
    }

    private void onResultCallback(ResultServices resultServices){
        controllerLatcher.countDown();
        if(controllerLatcher.getCount() == 0){
            hideProgressDialog();
            if(!resultServices.isRetCodeSuccess()){
                ToastUtil.toast(context,resultServices.retMsg);
            }
        }
    }

    @Override
    public void onBackPressed() {
        BusinessLauncher.getInstance().clearTop(MainActivity.class);
    }

    abstract class LinkClickedListener extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.common_blue_color));
            ds.setUnderlineText(true);
        }
    }
}

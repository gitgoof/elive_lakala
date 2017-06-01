package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.component.DiagramPopupWindow;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.HintFocusChangeListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HJP on 2015/10/15.
 */
public class CurrentProductFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Bundle bundle;
    private ProductInfo newProductInfo;
    private ValidprodApplyprodInfo newValidprodApplyprodInfo;

    TextView tvYesterdayIncome;
    TextView tvAssets;
    TextView tvSevenDaysAnnualYield;
    TextView tvTotalIncome;
    TextView tvIncomeW;
    ImageView ivSevenDaysAnnualYield;
    ImageView ivIncomeW;
    TextView tvTxtContent;
    TextView tvPurchase2;
    TextView tvBusinessDescription;
    TextView tvProductName;
    RelativeLayout rlProductCurrent;
    /**
     * 购买金额
     */
    EditText etPrice;
    DiagramPopupWindow diagramPopupWindow;
    LinearLayout llWithdrawPurchase2;

    //    private Boolean isFirstTime;
    private static String SEVENDAYS_DAY = "4.1";//七日年化收益 当天
    private static String INCOMEW_DAY = "3.1";//万份收益 当天
    private static String NUMBER_DAY = "7";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_details, null);
        init();
        return view;
    }

    public void init() {
        tvYesterdayIncome = (TextView) view.findViewById(R.id.tv_yesterday_income);
        tvAssets = (TextView) view.findViewById(R.id.tv_assets);
        tvSevenDaysAnnualYield = (TextView) view.findViewById(R.id.tv_seven_days_of_annual_yield);
        tvTotalIncome = (TextView) view.findViewById(R.id.tv_total_income);
        tvIncomeW = (TextView) view.findViewById(R.id.tv_income_w);
        ivSevenDaysAnnualYield = (ImageView) view.findViewById(R.id.iv_seven_days_of_annual_yield);
        ivIncomeW = (ImageView) view.findViewById(R.id.iv_income_w);
        tvTxtContent = (TextView) view.findViewById(R.id.tv_txt_content);
        tvPurchase2 = (TextView) view.findViewById(R.id.tv_purchase2);
        llWithdrawPurchase2 = (LinearLayout) view.findViewById(R.id.ll_withdraw_purchase2);
        tvBusinessDescription = (TextView) view.findViewById(R.id.tv_business_description);
        tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
        etPrice = (EditText) view.findViewById(R.id.et_price);
        etPrice.setOnFocusChangeListener(new HintFocusChangeListener());
//        etPrice.clearFocus();

        rlProductCurrent = (RelativeLayout) view.findViewById(R.id.rl_product_current);
        rlProductCurrent.requestFocus();


        tvPurchase2.setOnClickListener(this);
        ivIncomeW.setOnClickListener(this);
        ivSevenDaysAnnualYield.setOnClickListener(this);
        llWithdrawPurchase2.setOnClickListener(this);
        tvBusinessDescription.setOnClickListener(this);
        initData();
//        if(isFirstTime != null && isFirstTime){
//            llWithdrawPurchase2.setVisibility(View.VISIBLE);
////            llWithdrawPurchase.setVisibility(View.GONE);
//            viewSperator.setVisibility(View.GONE);
//        }
    }

    public void initData() {
        countEarnDate();
        bundle = getArguments();
        if (bundle != null) {
            newProductInfo = (ProductInfo) getArguments().getSerializable("newProductInfo");
            newValidprodApplyprodInfo = (ValidprodApplyprodInfo) getArguments().getSerializable("newValidprodApplyprodInfo");
//            isFirstTime=getArguments().getBoolean("firstTime");
            if (newProductInfo != null) {
                /**
                 * 七日年化收益率、万份收益
                 */
                tvSevenDaysAnnualYield.setText(String.valueOf(newProductInfo.getGrowthRate()));
                tvIncomeW.setText(String.valueOf(newProductInfo.getIncomeW()));
                tvProductName.setText(newProductInfo.getProdName());
            }
            if (newValidprodApplyprodInfo != null) {
                /**
                 * 昨日收益、资产、累计收益
                 */
                tvYesterdayIncome.setText(String.valueOf(Util.formatTwo(newValidprodApplyprodInfo.getDayIncome())));
                tvAssets.setText(String.valueOf(Util.formatTwo(newValidprodApplyprodInfo.getTotalAsset())));
                tvTotalIncome.setText(String.valueOf(Util.formatTwo(newValidprodApplyprodInfo.getTotalIncome())));
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //买入操作
            case R.id.tv_purchase2:
                FinanceRequestManager.getInstance().buyFinance((AppBaseActivity) getActivity(),
                        newProductInfo, etPrice.getText()
                                .toString());
                break;
            case R.id.iv_seven_days_of_annual_yield:
                showDiagram(SEVENDAYS_DAY, NUMBER_DAY, true, true);
                break;
            case R.id.iv_income_w:
                showDiagram(INCOMEW_DAY, NUMBER_DAY, false, true);
                break;
            case R.id.tv_business_description:
                if (newProductInfo.getProductId().equals("000686")) {
                    ProtocalActivity.open(getActivity(), ProtocalType.LC_LC2NOTE);
                }
                break;

        }
    }

    public void showDiagram(String TradeType, String PageSize, boolean isSevenDays, boolean isSeven) {
        if (diagramPopupWindow == null) {
            diagramPopupWindow = new DiagramPopupWindow((AppBaseActivity) getActivity(), tvYesterdayIncome,
                    TradeType, PageSize, isSevenDays, isSeven);
        } else {
            diagramPopupWindow.requestXYData(TradeType, PageSize, isSevenDays, isSeven);
        }
        diagramPopupWindow.showDiagramPopupWindow();
        diagramPopupWindow.setBackgroundAlpha(getActivity(), 0.5f);
        diagramPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                diagramPopupWindow.setBackgroundAlpha(getActivity(), 1.0f);
            }
        });
    }

    private void countEarnDate() {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    String countDate = responseData.optString("CountDate", "");
                    String earnDate = responseData.optString("EarnDate", "");
                    SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
                    try {
                        Date date1 = parse.parse(countDate);
                        countDate = format.format(date1);
                        Date date2 = parse.parse(earnDate);
                        earnDate = format.format(date2);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("预计")
                                .append(countDate).append("产生收益，")
                                .append(earnDate).append("可查看收益");
                        tvTxtContent.setText(stringBuilder.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(getActivity(), returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {

            }
        };
        FinanceRequestManager.getInstance().countEarnDate(listener);
    }

}

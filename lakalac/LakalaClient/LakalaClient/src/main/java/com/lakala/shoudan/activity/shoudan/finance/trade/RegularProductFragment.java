package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.HintFocusChangeListener;

import java.text.DecimalFormat;

/**
 * Created by HJP on 2015/10/15.
 */
public class RegularProductFragment extends BaseFragment implements View.OnClickListener{
    private View view;
    private TextView tvPredictIncome;
    private TextView tvPeriod;
    private TextView tvExpectedAnnualYield;
    private TextView tvStartTime;
//    private TextView tvIncomeType;
    private TextView tvLimitTime;
    private TextView tvStartAmount;
    private TextView tvEndTime;
    private TextView tvTxtContent;
    private TextView tvPurchase;
    private TextView tvBusinessDescription;
    private TextView tvProductName;
    private String ProductId;
    /**
     * 买入金额
     */
    private EditText etPrice;
    /**
     * 买入预期收益
     */
    private TextView tvPredict;
    private LinearLayout llPredict;

    private Bundle bundle;
    private ProductInfo newProductInfo;
    private ValidprodApplyprodInfo newValidprodApplyprodInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_regular_details,null);
        view.requestFocus();
        init();
        return view;
    }
    public void init(){
        tvPredictIncome=(TextView)view.findViewById(R.id.tv_predict_income);
        tvPeriod=(TextView)view.findViewById(R.id.tv_period);
        tvExpectedAnnualYield=(TextView)view.findViewById(R.id.tv_expected_annual_yield);
        tvStartTime=(TextView)view.findViewById(R.id.tv_starttime);
//        tvIncomeType=(TextView)view.findViewById(R.id.tv_incometype);
        tvLimitTime=(TextView)view.findViewById(R.id.tv_limittime);
        tvStartAmount=(TextView)view.findViewById(R.id.tv_startamount);
        tvEndTime=(TextView)view.findViewById(R.id.tv_endtime);
        tvTxtContent=(TextView)view.findViewById(R.id.tv_txt_content);
        tvTxtContent=(TextView)view.findViewById(R.id.tv_txt_content);
        tvPurchase=(TextView)view.findViewById(R.id.tv_purchase);
        tvBusinessDescription=(TextView)view.findViewById(R.id.tv_business_description);
        tvProductName=(TextView)view.findViewById(R.id.tv_product_name);
        etPrice=(EditText)view.findViewById(R.id.et_price);
        tvPredict=(TextView)view.findViewById(R.id.tv_predict);
        llPredict=(LinearLayout)view.findViewById(R.id.ll_predict);
        tvPurchase.setOnClickListener(this);
        tvBusinessDescription.setOnClickListener(this);
        initData();

        etPrice.setOnFocusChangeListener(new HintFocusChangeListener());
        etPrice.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        llPredict.setVisibility(View.VISIBLE);
                        String amount = s.toString();
                        if(TextUtils.isEmpty(amount)){
                            tvPredict.setText("0");
                            return;
                        }
                        double YearOrofit=newProductInfo.getGrowthRate();
                        String day = newProductInfo.getLimitTime();
                        if (!TextUtils.isEmpty(day)) {
                            //每百元预期收益
                            double data = Double.parseDouble(amount) * (YearOrofit/100) * Integer
                                    .parseInt(day) /
                                    365;
                            tvPredict.setText(Util.formatTwo(data));//保留两位小数
                        }
                    }
                }
        );
    }
    public void initData(){
        bundle=getArguments();
        if (bundle!=null){
            newProductInfo= (ProductInfo) getArguments().getSerializable("newProductInfo");
            newValidprodApplyprodInfo= (ValidprodApplyprodInfo) getArguments().getSerializable("newValidprodApplyprodInfo");
            if(newProductInfo!=null){
                ProductId=newProductInfo.getProductId();
                tvPeriod.setText(newProductInfo.getPeriod());
                tvStartTime.setText(newProductInfo.getStartTime());
//                String incomeType=newProductInfo.getIncomeType();
//
//                if(incomeType!=null && incomeType=="0"){
//                    incomeType="一次性还本付息";
//                }else{
//                    incomeType="周期返还";
//                }
//                tvIncomeType.setText(incomeType);
                tvLimitTime.setText(newProductInfo.getLimitTime());
                tvStartAmount.setText(newProductInfo.getStartAmout());
                tvEndTime.setText(newProductInfo.getEndTime());
                tvProductName.setText(newProductInfo.getProdName());
                double YearOrofit=newProductInfo.getGrowthRate();
                tvExpectedAnnualYield.setText(String.valueOf(YearOrofit));
                String day = newProductInfo.getLimitTime();
                if (!TextUtils.isEmpty(day)) {
                    //每百元预期收益
                    double data = 100 * (YearOrofit/100) * Integer.parseInt(day) / 365;
                    DecimalFormat decimalFormat = new DecimalFormat("######0.00");//保留两位小数
                    tvPredictIncome.setText(String.valueOf(decimalFormat.format(data)));
                }
                if(ProductId.equals("KL1H00001")){
                    tvTxtContent.setText("温馨提示：该产品为不可变现产品，封闭期间不可提前取出，到期后一次性还本付息，本金和收益自动取出至赎回卡内");
                }else if(ProductId.equals("KL1H00002")){
                    tvTxtContent.setText(newProductInfo.getRemark());
                }
//
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //买入操作
            case R.id.tv_purchase:
                FinanceRequestManager.getInstance().buyFinance(
                        (AppBaseActivity) getActivity(), newProductInfo,
                        etPrice.getText().toString()
                );
                break;
            case R.id.tv_business_description:
                if(ProductId.equals("KL1H00001")){
                    ProtocalActivity.open(getActivity(), ProtocalType.LC_LC1NOTE);
                } else if(ProductId.equals("KL1H00002")){
                    ProtocalActivity.open(getActivity(),ProtocalType.LC_LC3NOTE);
                }
                break;
        }
    }
}

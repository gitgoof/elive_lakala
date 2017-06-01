package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.common.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HJP on 2015/10/15.
 */
public class RegularPossessProductFragment extends BaseFragment implements View.OnClickListener{
    private View view;
    private TextView tvPredictIncome;
    private TextView tvBusinessDescription;
    private TextView tvUrl;
    TextView tvProductName;
    /**
     * 产品期次
     */
    private TextView tvPeriod;
    private TextView tvExpectedAnnualYield;
    private int ProdState;

    /**
     * 本期剩余天数
     */
    private TextView tvRemainingDays;
    /**
     * 持有资产
     */
    private TextView tvAssets;
    private TextView tvEndTime;
    private TextView tvNotice;
    private TextView tvTxt;
    private TextView tvProdState;
    private Bundle bundle;
    private ProductInfo newProductInfo;
    private ValidprodApplyprodInfo newValidprodApplyprodInfo;
    private boolean isShowUrl=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_regulart_possess_details,null);
        init();
        return view;
    }
    public void init(){
        tvPredictIncome=(TextView)view.findViewById(R.id.tv_predict_income);
        tvPeriod=(TextView)view.findViewById(R.id.tv_period);
        tvExpectedAnnualYield=(TextView)view.findViewById(R.id.tv_expected_annual_yield);
        tvRemainingDays=(TextView)view.findViewById(R.id.tv_remaining_days);
        tvAssets=(TextView)view.findViewById(R.id.tv_assets);
        tvEndTime=(TextView)view.findViewById(R.id.tv_endtime);
        tvNotice=(TextView)view.findViewById(R.id.tv_notice);
        tvTxt=(TextView)view.findViewById(R.id.tv_txt);
        tvProductName=(TextView)view.findViewById(R.id.tv_product_name);
        tvBusinessDescription = (TextView) view.findViewById(R.id.tv_business_description);
        tvUrl = (TextView) view.findViewById(R.id.tv_url);
        tvProdState=(TextView)view.findViewById(R.id.tv_prod_state);
        tvBusinessDescription.setOnClickListener(this);
        tvUrl.setOnClickListener(this);
        initData();

    }
    public void initData(){
        bundle=getArguments();
        if (bundle!=null){
            newProductInfo= (ProductInfo) getArguments().getSerializable("newProductInfo");
            newValidprodApplyprodInfo= (ValidprodApplyprodInfo) getArguments().getSerializable("newValidprodApplyprodInfo");
            if(newProductInfo!=null){
                tvPeriod.setText(newProductInfo.getPeriod());
                tvRemainingDays.setText(newProductInfo.getLimitTime());
                tvProductName.setText(newProductInfo.getProdName());

                if(newProductInfo.getProductId().equals("KL1H00001")){
                    tvTxt.setVisibility(View.GONE);
                    tvNotice.setText("温馨提示：该产品为不可变现产品，封闭期间不可提前取出，到期后一次性还本付息，本金和收益自动取出至赎回卡内");
                }else {
                    String LiquiStart=newProductInfo.getLiquiStart();
                    String LiquiEnd=newProductInfo.getLiquiEnd();
                    tvNotice.setText("可变现日期:"+LiquiStart+"至"+LiquiEnd);
                }
                ProdState=newProductInfo.getProdState();
                if(ProdState==3){
                    tvProdState.setText("产品状态");
                    tvRemainingDays.setText("暂未成立");
                    tvUrl.setVisibility(View.INVISIBLE);
                    isShowUrl=false;
                }

            }
            if (newValidprodApplyprodInfo!=null){
                /**
                 *预期收益、持有资产、本期到期时间
                 */
                 tvPredictIncome.setText(String.valueOf(newValidprodApplyprodInfo.getPredictIncome()));
                 tvAssets.setText(String.valueOf(Util.formatTwo(newValidprodApplyprodInfo.getPrincipal())));
                 tvExpectedAnnualYield.setText(String.valueOf(newValidprodApplyprodInfo.getYearOrofit()));

                String[] array = newValidprodApplyprodInfo.getEndDate().split(" ");
                tvEndTime.setText(array[0]);
                if(ProdState!=3){
                    /**
                     * 本期剩余天数
                     */
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                    Date date=new Date();
                    String today=simpleDateFormat.format(date);//今天的日期
                    String EndDate=(newValidprodApplyprodInfo.getEndDate()).substring(0,10);
                    try {
                        Date todayDate=simpleDateFormat.parse(today);
                        Date LiquiEndDate=simpleDateFormat.parse(EndDate);
                        int leftDay=Util.getGapCount(todayDate, LiquiEndDate);
                        if(leftDay<=0){
                            tvRemainingDays.setText("已到期，收益计算中");
                        }else {
                            tvRemainingDays.setText(String .valueOf(leftDay));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_business_description:
                if(newProductInfo.getProductId().equals("KL1H00001")){
                    ProtocalActivity.open(getActivity(), ProtocalType.LC_LC1NOTE);
                }else if(newProductInfo.getProductId().equals("KL1H00002")){
                    ProtocalActivity.open(getActivity(),ProtocalType.LC_LC3NOTE);
                }
                break;
            case R.id.tv_url:
                if(isShowUrl){
                    String url=newValidprodApplyprodInfo.getUrl();
                    if(!TextUtils.isEmpty(url)){
                        ProtocalActivity.open(getActivity(),"信贷合同披露",url);
                    }
                }


                break;
        }
    }
}

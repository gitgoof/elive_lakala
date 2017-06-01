package com.lakala.shoudan.activity.shoudan.finance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.bean.Income;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalAssets;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundValidateProdListRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TransDetailRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.trade.FinanceTransDetailActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.FragmentFactory;
import com.lakala.shoudan.activity.shoudan.finance.trade.ProductListActivity;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 理财
 * Created by More on 15/8/25.
 */
public class FrontPageActivity extends AppBaseActivity implements View.OnClickListener {
    /**
     * 用于判断显示的fragment
     */
    int index = 0;
    /**
     * 总资产
     */
    double totalAsset = 0.0;
    /**
     * 累计收益
     */
    double totalIncome = 0.0;
    /**
     * 理财产品数据
     */
    private String totalAssetsString;
    private Income income;
    private Fragment fragment;

    private List<ValidprodApplyprodInfo> totalAssetsData;
    private List<ValidprodApplyprodInfo> validprodApplyprodInfosList;
    private List<ValidprodApplyprodInfo> applyprodListInfosList;
    private TotalAssets totalAssets;

    TextView tvFinancialAssets;
    View btnArrowGoGray;
    LinearLayout llManagemontyBottom;
    LinearLayout llManagemontyBottom2;
    TextView tvAccumulatedIncome;
    ImageView ivRise;
    LinearLayout linearArrowGoGray;//理财资产详情
    TextView tvManageMoney;//我要理财
    TextView tvTansactionDetailsy;//交易明细
    TextView tvManageMoney2;
    TextView tvYuanTxt;
    private Bundle bundleFragment;

    private List<TransDetailProInfo> proList = new ArrayList<TransDetailProInfo>();
    private int isDefaultId = 0;

    private List<ProductInfo> regularProducts;
    private List<ProductInfo> currentProducts;
    private TotalProductInfo totalProductInfo;
    private List<ProductInfo> productListInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wealth_management_column);
        initUI();
    }

    public void init() {
        bundleFragment = new Bundle();
        totalAssetsData = new ArrayList<>();
        income = Income.parserStringToIncome(FinanceRequestManager.getInstance().getIncomeString());
        totalAssetsString = FinanceRequestManager.getInstance().getTotalAssetsString();
        totalAssets = TotalAssets.parserStringToTotalAssets(totalAssetsString);
        if (totalAssets != null) {
            applyprodListInfosList = totalAssets.getApplyprodList();
            validprodApplyprodInfosList = totalAssets.getValidprodList();
            if (applyprodListInfosList != null) {
                for (int i = 0; i < applyprodListInfosList.size(); i++) {
                    totalAssetsData.add(applyprodListInfosList.get(i));
                }
            }
            if (validprodApplyprodInfosList != null) {
                for (int i = 0; i < validprodApplyprodInfosList.size(); i++) {
                    totalAssetsData.add(validprodApplyprodInfosList.get(i));
                }
            }
            bundleFragment.putSerializable("validprodApplyprodInfosListTotal", (Serializable) totalAssetsData);
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("理财");
        navigationBar.setActionBtnText("业务说明");
        navigationBar.setOnNavBarClickListener(
                new NavigationBar.OnNavBarClickListener() {
                    @Override
                    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                        switch (navBarItem) {
                            case back:
                                finish();
                                break;
                            case action:
                                PublicToEvent.FinalEvent( ShoudanStatisticManager.Finance_Introduction, context);
                                ProtocalActivity.open(context, ProtocalType.LC_ALLNOTE);
                                break;
                        }
                    }
                }
        );
        navigationBar.setBottomImageVisibility(View.GONE);
        tvFinancialAssets = (TextView) findViewById(R.id.tv_financial_assets);
        btnArrowGoGray = findViewById(R.id.btn_arrow_go_gray);
        llManagemontyBottom = (LinearLayout) findViewById(R.id.ll_managemonty_bottom);
        llManagemontyBottom2 = (LinearLayout) findViewById(R.id.ll_managemonty_bottom2);
        tvAccumulatedIncome = (TextView) findViewById(R.id.tv_accumulated_income);
        ivRise = (ImageView) findViewById(R.id.iv_rise);
        linearArrowGoGray = (LinearLayout) findViewById(R.id.linear_assets);
        tvManageMoney = (TextView) findViewById(R.id.tv_manage_money);
        tvTansactionDetailsy = (TextView) findViewById(R.id.tv_transaction_details);
        tvManageMoney2 = (TextView) findViewById(R.id.tv_manage_money2);
        tvYuanTxt = (TextView) findViewById(R.id.tv_yuan_txt);
        tvTansactionDetailsy.setOnClickListener(this);
        tvManageMoney.setOnClickListener(this);
        linearArrowGoGray.setOnClickListener(this);
        tvManageMoney2.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        proList = new ArrayList<>();
        init();
        if (income != null) {
            totalAsset = income.getTotalAsset();
            totalIncome = income.getTotalIncome();
        }
        //未理过财
        if (totalIncome == 0.0 && totalAsset == 0.0) {
            index = 2;
            tvFinancialAssets.setText("暂无资产");
            linearArrowGoGray.setEnabled(false);
            tvAccumulatedIncome.setText("暂无收益");
            btnArrowGoGray.setVisibility(View.INVISIBLE);
            tvYuanTxt.setVisibility(View.INVISIBLE);
        } else if (totalAsset == 0 && totalIncome != 0) {//理过财，但现在未理财
            tvFinancialAssets.setText("暂无资产");
            linearArrowGoGray.setEnabled(false);
            btnArrowGoGray.setVisibility(View.INVISIBLE);
            tvYuanTxt.setVisibility(View.INVISIBLE);
            index = 0;
            tvAccumulatedIncome.setText(String.valueOf(Util.formatTwo(totalIncome)));//保留两位小数
        } else {
            index = 1;
            ivRise.setVisibility(View.VISIBLE);
            tvFinancialAssets.setText(String.valueOf(Util.formatTwo(totalAsset)));
            tvAccumulatedIncome.setText(String.valueOf(Util.formatTwo(totalIncome)));//保留两位小数
            btnArrowGoGray.setVisibility(View.VISIBLE);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (FinanceRequestManager.getInstance().isFinanceChange || fragment == null) {
            fragment = FragmentFactory.getInstanceByIndex(index);
            FinanceRequestManager.getInstance().isFinanceChange = false;
            fragment.setArguments(bundleFragment);
            transaction.replace(R.id.linear_content, fragment);
            transaction.commit();
        }

        if (index == 2) {
            llManagemontyBottom.setVisibility(View.GONE);
            llManagemontyBottom2.setVisibility(View.VISIBLE);
        } else {
            llManagemontyBottom.setVisibility(View.VISIBLE);
            llManagemontyBottom2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_transaction_details:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .Finance_Transaction_Details, context);
                getAllProd();
                break;
            case R.id.tv_manage_money2:
            case R.id.tv_manage_money:
                PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product, context);
                getProductInfo();
                break;
            case R.id.linear_assets://查看贷款详情
                PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance, context);
                Intent intent1 = new Intent(FrontPageActivity.this, FinancialAssetsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("totalAssetsData", (Serializable) totalAssetsData);
                double assets = totalAssets.getTotalAsset();
                bundle1.putDouble("totalAsset", assets);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;

        }
    }

    public void getProductInfo() {
        FinanceRequestManager.getInstance().getProductList(
                new HttpResponseListener() {
                    @Override
                    public void onStart() {
                        showProgressWithNoMsg();
                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                        hideProgressDialog();
                        if (returnHeader.isSuccess() && responseData != null) {
                            regularProducts = new ArrayList<>();
                            currentProducts = new ArrayList<>();
                            totalProductInfo = TotalProductInfo
                                    .parserStringToTotalProductInfo(responseData.toString());
                            productListInfoList = totalProductInfo.getProdList();
                            for (int i = 0; i < productListInfoList.size(); i++) {
                                ProductInfo tempFinanceProduct = productListInfoList.get(i);
                                //0：活期  1：定期
                                if (1 == tempFinanceProduct.getProdType()) {
                                    regularProducts.add(tempFinanceProduct);
                                } else {
                                    currentProducts.add(tempFinanceProduct);
                                }
                            }
                            Intent intent = new Intent(
                                    FrontPageActivity.this, ProductListActivity.class
                            );
                            intent.putExtra("regularProducts", (Serializable) regularProducts);
                            intent.putExtra("currentProducts", (Serializable) currentProducts);
                            startActivity(intent);
                        } else {
                            ToastUtil.toast(context, returnHeader.getErrMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        hideProgressDialog();
                        ToastUtil.toast(context, R.string.socket_fail);

                    }
                }
        );
    }


    private void getAllProd() {
        FundValidateProdListRequest request = new FundValidateProdListRequest();
        request.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    try {
                        JSONArray jsonArray = responseData.getJSONArray("ProdList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TransDetailProInfo info = new TransDetailProInfo();
                            info.setProName(jsonObject.optString("ProdName"));
                            info.setIsDefault(jsonObject.optInt("IsDefault"));
                            if (jsonObject.optInt("IsDefault") == 1) {
                                isDefaultId = i;
                            }
                            info.setProductId(jsonObject.optString("ProductId"));
                            proList.add(info);
                        }
                        getRecord(proList.get(isDefaultId).getProductId(), isDefaultId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                    hideProgressDialog();
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getProdList(request, listener);
    }

    private void getRecord(String productId, final int position) {
        TransDetailRequest request = new TransDetailRequest();
        request.setPageCount(String.valueOf(FinanceTransDetailActivity.PAGECOUNT));
        request.setPageSize(String.valueOf(FinanceTransDetailActivity.PAGESIZE));
        request.setProductId(productId);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {

                    Intent intent = new Intent(FrontPageActivity.this, FinanceTransDetailActivity.class);
                    String data = responseData.toString();
                    intent.putExtra("data", data);
                    intent.putExtra("position", position);
                    intent.putExtra("proList", (Serializable) proList);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getFinancialRecord(request, listener);
    }
}

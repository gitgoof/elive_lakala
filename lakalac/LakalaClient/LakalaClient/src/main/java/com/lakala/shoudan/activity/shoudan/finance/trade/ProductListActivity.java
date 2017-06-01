package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.adapter.ProductListCurrentAdapter;
import com.lakala.shoudan.activity.shoudan.finance.adapter.ProductListRegularAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalAssets;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.ui.component.NavigationBar;

import java.util.List;

/**
 * 理财产品
 * 产品列表
 * Created by More on 15/8/25.
 */
public class ProductListActivity extends AppBaseActivity {
    private TotalProductInfo totalProductInfo;
    private List<ProductInfo> productListInfoList;
    private ProductInfo newProductInfo;
    private ListView lvCurrentProducts;
    private ListView lvRegularProducts;
    private TextView tvRegularTxt;
    private TextView tvCurrentTxt;
    private List<ProductInfo> regularProducts;
    private List<ProductInfo> currentProducts;
    private ScrollView scrollView;
    private String totalAssetsString;
    private TotalAssets totalAssets;
    private List<ValidprodApplyprodInfo> validprodApplyprodInfosList;
    private ValidprodApplyprodInfo validprodApplyprodInfo;
    private ValidprodApplyprodInfo newValidprodApplyprodInfo;
    int recodeInt = 0;
    private Bundle bundle;
    private String productId;
    private int count = 0;
    private int prodState;
    private double tempAssets;
    private double tempTotalIncome;
    private double tempDayIncome;
    private double tempPredictIncome;
    private double dayIncome = 0;
    private double assets = 0;
    private double totalIncom = 0;
    private double predictIncome = 0;
    private boolean isRegular = false;
    private int productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_product_list);
        initUI();
        init();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("理财产品");
        navigationBar.setActionBtnText("业务说明");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                    case action:
                        ProtocalActivity.open(context, ProtocalType.LC_ALLNOTE);
                        break;
                }
            }
        });


    }

    public void init() {
        bundle = new Bundle();
        lvCurrentProducts = (ListView) findViewById(R.id.lv_current_products);
        lvRegularProducts = (ListView) findViewById(R.id.lv_regular_products);
        tvRegularTxt = (TextView) findViewById(R.id.tv_regual_txt);
        tvCurrentTxt = (TextView) findViewById(R.id.tv_current_txt);
        scrollView = (ScrollView) findViewById(R.id.scrollview);

        currentProducts = (List<ProductInfo>) getIntent().getSerializableExtra("currentProducts");
        regularProducts = (List<ProductInfo>) getIntent().getSerializableExtra("regularProducts");

        if (currentProducts.size() != 0) {
            tvCurrentTxt.setVisibility(View.VISIBLE);
            lvCurrentProducts.setVisibility(View.VISIBLE);
        }
        if (regularProducts.size() != 0) {
            tvRegularTxt.setVisibility(View.VISIBLE);
        }
        lvCurrentProducts.setAdapter(new ProductListCurrentAdapter(ProductListActivity.this, currentProducts));
        lvRegularProducts.setAdapter(
                new ProductListRegularAdapter(ProductListActivity.this, regularProducts)
        );
        scrollView.fullScroll(ScrollView.FOCUS_UP);


        lvCurrentProducts.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        newProductInfo = currentProducts.get(i);
                        productId = newProductInfo.getProductId();
                        getValidprodApplyprodInfo();
                    }
                }
        );


        lvRegularProducts.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        LinearLayout linearLayout = (LinearLayout) lvRegularProducts.getChildAt(i);
                        TextView tvProdState = (TextView) linearLayout
                                .findViewById(R.id.tv_prod_state);
                        if (!(tvProdState.getText()).equals("3")) {
                            newProductInfo = regularProducts.get(i);
                            productId = newProductInfo.getProductId();
                            getValidprodApplyprodInfo();
                        }
                    }
                }
        );

    }

    public void getValidprodApplyprodInfo() {
        totalAssetsString = FinanceRequestManager.getInstance().getTotalAssetsString();
        totalAssets = TotalAssets.parserStringToTotalAssets(totalAssetsString);
        boolean hasValidProd = false;//用户是否有理财产品
        if (totalAssets != null) {
            validprodApplyprodInfosList = totalAssets.getValidprodList();
            if (validprodApplyprodInfosList != null && validprodApplyprodInfosList.size() != 0) {
                mergeValidprodApplyprodInfo();
                newValidprodApplyprodInfo = validprodApplyprodInfosList.get(recodeInt);
                hasValidProd = true;
            }
        }
        if (!hasValidProd) {
            newValidprodApplyprodInfo = new ValidprodApplyprodInfo();
            bundle.putBoolean("firstTime", true);
        }
        setValidprodApplyprodInfo();
        deliverInfo();
        Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
        intent.putExtra("productInfo", newProductInfo);
        intent.putExtra("validprodApplyprodInfo", newValidprodApplyprodInfo);
        startActivity(intent);
        PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product_Details, context);
    }

    public void mergeValidprodApplyprodInfo() {
        boolean isProductListActivityJumpTo = true;
        /**
         * productId相同，合并活期的昨日收益、资产、累计收益，定期的预期收益、资产
         */
        for (int i = 0; i < validprodApplyprodInfosList.size(); i++) {
            validprodApplyprodInfo = validprodApplyprodInfosList.get(i);
            if (validprodApplyprodInfo.getProductId().equals(productId)) {
                count++;
                isProductListActivityJumpTo = false;
                if (count == 1) {
                    recodeInt = i;
                }
                prodState = validprodApplyprodInfo.getProdState();
                if (validprodApplyprodInfo.getProdType() == 0) {
                    tempAssets = validprodApplyprodInfo.getTotalAsset();
                    tempTotalIncome = validprodApplyprodInfo.getTotalIncome();
                    totalIncom += tempTotalIncome;
                    tempDayIncome = validprodApplyprodInfo.getDayIncome();
                    dayIncome += tempDayIncome;
//                    fragment = new CurrentProductFragment();
                } else {
                    isRegular = true;
                    tempAssets = validprodApplyprodInfo.getPrincipal();
                    tempPredictIncome = validprodApplyprodInfo.getPredictIncome();
                    predictIncome += tempPredictIncome;
                }
                assets += tempAssets;
            }
            /**
             * 理财产品列表ProductListActivity点击非用户拥有产品所跳到的逻辑
             */
            else {
                if (isProductListActivityJumpTo) {
                    if (productType == 1) {
                        isRegular = true;
                    }
                }
            }

        }
    }

    public void setValidprodApplyprodInfo() {
        //定期产品的话，设置合并的期收益、资产
        if (productType == 1) {
            newValidprodApplyprodInfo.setPrincipal(assets);
            newValidprodApplyprodInfo.setPredictIncome(predictIncome);
        }
        //活期的话，设置合并的昨日收益、资产、累计收益
        else {
            newValidprodApplyprodInfo.setDayIncome(Double.valueOf(dayIncome));
            newValidprodApplyprodInfo.setTotalAsset(assets);
            newValidprodApplyprodInfo.setTotalIncome(totalIncom);
        }
    }

    public void deliverInfo() {
        bundle.putSerializable("newValidprodApplyprodInfo", newValidprodApplyprodInfo);
        bundle.putSerializable("newProductInfo", newProductInfo);
    }
}

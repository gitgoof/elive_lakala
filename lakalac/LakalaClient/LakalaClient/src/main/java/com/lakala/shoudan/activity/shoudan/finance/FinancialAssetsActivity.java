package com.lakala.shoudan.activity.shoudan.finance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.adapter.FinancialAssetsAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalAssets;
import com.lakala.shoudan.activity.shoudan.finance.bean.TotalProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundContListRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.trade.ProductDetailsActivity;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by fengx on 2015/10/8.
 * 理财资产
 */
public class FinancialAssetsActivity extends AppBaseActivity {

    TextView tvFinance;
    ListView lvFinancialProduct;
    private TotalAssets totalAssets;
    private FinancialAssetsAdapter adapter;
    private List<ValidprodApplyprodInfo> newData;
    private TotalProductInfo totalProductInfo;
    private ProductInfo newProductInfo;
    private List<ProductInfo> productListInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_assets);
        ShoudanStatisticManager.getInstance();
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();

        tvFinance = (TextView) findViewById(R.id.tv_finance);
        lvFinancialProduct = (ListView) findViewById(R.id.lv_financial_product);
        navigationBar.setTitle("理财资产");
        navigationBar.setActionBtnText("交易明细");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Assets_Transaction_Details, context);
                    FinanceRequestManager.getInstance().toTransDetail(FinancialAssetsActivity.this);
                }
            }
        });

        //获取首页传来的理财产品数据
        double totalAsset = getIntent().getExtras().getDouble("totalAsset",0);

        tvFinance.setText(String.valueOf(Util.formatTwo(totalAsset)));
        newData = (List<ValidprodApplyprodInfo>) getIntent().getExtras().getSerializable("totalAssetsData");
        if (newData == null){
            LogUtil.print("fundTotalQry检查这个接口是否获取到数据");
            return;
        }
        adapter = new FinancialAssetsAdapter(this,newData);
        lvFinancialProduct.setAdapter(adapter);
        lvFinancialProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productId = newData.get(position).getProductId();
                int prodState = newData.get(position).getProdState();
                //如果是定期，并且不可申购即已生效
                if (newData.get(position).getProdType() == 1 && prodState != 1) {
                        getUnValidProductInfo(productId, position);
                } else {
                    getProductInfo(productId, position);
                }
            }}
            
            );
    }

    public void getUnValidProductInfo(final String productId, final int position){

        FundContListRequest request = new FundContListRequest();
        request.setContractState("2");
        request.setContractId(newData.get(position).getContractId());
        request.setProductId(productId);
        request.setPeriod(newData.get(position).getPeriod());
        FinanceRequestManager.getInstance().getUnValidProductList(request, new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess() && responseData != null){
                    newProductInfo = JSON.parseObject(responseData.toString(),ProductInfo.class);
                    ValidprodApplyprodInfo info = newData.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("productInfo", newProductInfo);
                    intent.putExtra("validprodApplyprodInfo", info);
                    intent.setClass(FinancialAssetsActivity.this, ProductDetailsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);

            }
        });
    }

    public void getProductInfo(final String productId, final int position) {
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
                            totalProductInfo = TotalProductInfo
                                    .parserStringToTotalProductInfo(responseData.toString());
                            productListInfoList = totalProductInfo.getProdList();
                            //如果是活期只判断产品id，如果是定期判断id和期数
                            if (newData.get(position).getProdType() == 0) {
                                for (int i=0;i<productListInfoList.size();i++){
                                    if (productId.equals(productListInfoList.get(i).getProductId())){
                                        newProductInfo = productListInfoList.get(0);
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < productListInfoList.size(); i++) {
                                    if (productId.equals(productListInfoList.get(i).getProductId()) &&
                                            productListInfoList.get(i).getPeriod().equals(newData.get(position).getPeriod())) {
                                        newProductInfo = productListInfoList.get(i);
                                        break;
                                    }
                                }
                            }
                            if (newProductInfo == null){
                                LogUtil.print("newProductInfo为空");
                                return;
                            }
                            ValidprodApplyprodInfo info = newData.get(position);
                            Intent intent = new Intent();
                            intent.putExtra("productInfo", newProductInfo);
                            intent.putExtra("validprodApplyprodInfo", info);
                            intent.setClass(FinancialAssetsActivity.this, ProductDetailsActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.toast(context,returnHeader.getErrMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        hideProgressDialog();
                        ToastUtil.toast(context,R.string.socket_fail);

                    }
                }
        );
    }
}

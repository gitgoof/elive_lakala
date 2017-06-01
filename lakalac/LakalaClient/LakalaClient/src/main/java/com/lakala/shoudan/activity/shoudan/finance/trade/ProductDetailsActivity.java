package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryCurrentCardInfoRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryRegularCardInfoRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HJP on 2015/10/15.
 */
public class ProductDetailsActivity extends AppBaseActivity {
    private Fragment fragment;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    /**
     * 可申购
     */
    private static int CANPURCHASE = 1;
    /**
     * 已封闭
     */
    private static int CLOSED = 2;
    private Bundle bundle;
    private ProductInfo newProductInfo;
    private ValidprodApplyprodInfo newValidprodApplyprodInfo;
    private int productType;
    private String productId;
    private boolean isCurrentWithdraw;
    private String productName;
    private String contractId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        init();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("理财产品详情");
        if (productId.equals("KL1H00001") || (productId.equals("KL1H00002") && !isRegularCanWithdraw())) {
            //理财1号，或者未满锁定期的理财3号
            navigationBar.setActionBtnEnabled(false);
        } else if (productId.equals("KL1H00002") && isRegularCanWithdraw()) {
            //满锁定期的理财3号
            navigationBar.setActionBtnEnabled(true);
            navigationBar.setActionBtnVisibility(View.VISIBLE);
            navigationBar.setActionBtnText("提前取出");
            isCurrentWithdraw = false;
        } else if (productId.equals("000686") && newValidprodApplyprodInfo.getTotalAsset() != 0) {
            //理财2号
            navigationBar.setActionBtnEnabled(true);
            navigationBar.setActionBtnVisibility(View.VISIBLE);
            navigationBar.setActionBtnText("取出");
            isCurrentWithdraw = true;
        }
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                    case action:
                        if (isCurrentWithdraw) {
                            queryCardInfo();
                        } else {
                            PromptDialog();
                        }
                        break;
                }
            }
        });
    }

    public void init() {
        bundle = new Bundle();
        newProductInfo = (ProductInfo) getIntent().getSerializableExtra("productInfo");
        newValidprodApplyprodInfo = (ValidprodApplyprodInfo) getIntent().getSerializableExtra("validprodApplyprodInfo");
        productId = newValidprodApplyprodInfo.getProductId();
        contractId = newValidprodApplyprodInfo.getContractId();
        if (newProductInfo == null) {
            LogUtil.print("产品信息ProductInfo为null");
            return;
        }
        if (newValidprodApplyprodInfo == null) {
            LogUtil.print("产品信息ValidprodApplyprodInfo为null");
            return;
        }
        productId = newProductInfo.getProductId();
        productType = newProductInfo.getProdType();

        if (productType == 0) {
            fragment = new CurrentProductFragment();
        } else {
            //还未到起息日，可再继续购买定期产品
            if (newProductInfo.getProdState() == CANPURCHASE) {
                fragment = new RegularProductFragment();
            } else {
                fragment = new RegularPossessProductFragment();
            }
        }
        bundle.putSerializable("newValidprodApplyprodInfo", newValidprodApplyprodInfo);
        bundle.putSerializable("newProductInfo", newProductInfo);
        fragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ll_product_details_content, fragment);
        fragmentTransaction.commit();
        initUI();
    }

    public boolean isRegularCanWithdraw() {
        /**
         *理财3号锁定期剩余天数
         */
        int overEndDays = 0;
        int overStartDate = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = simpleDateFormat.format(date);//今天的日期
        String Startate = (newProductInfo.getLiquiStart()).substring(0, 10);
        String Endtate = (newProductInfo.getLiquiEnd()).substring(0, 10);
        try {
            Date todayDate = simpleDateFormat.parse(today);
            Date LiquiStartDate = simpleDateFormat.parse(Startate);
            Date LiquiEndDate = simpleDateFormat.parse(Endtate);

            overStartDate = Util.getGapCount(LiquiStartDate, todayDate);
            overEndDays = Util.getGapCount(LiquiEndDate, todayDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (overStartDate < 0 || overEndDays > 0) {
            return false;
        }
        return true;
    }

    /**
     * 活期取现查询签约卡
     */
    private void queryCardInfo() {

        QueryCurrentCardInfoRequest request = new QueryCurrentCardInfoRequest();
        FinanceRequestManager.getInstance().queryCardMsg(request, new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    Intent intent = new Intent(ProductDetailsActivity.this, WithdrawalActivity.class);
                    String cardMsg = responseData.toString();
                    intent.putExtra("cardMsg", cardMsg);
                    intent.putExtra("prodName", newValidprodApplyprodInfo.getProdName());
                    intent.putExtra("productId", newValidprodApplyprodInfo.getProductId());
                    startActivity(intent);
                } else {
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toastInternetError();

            }
        });
    }

    /**
     * 提前取现定期产品用这个接口查询签约卡
     *
     * @param ProductId
     * @param ContractId
     */
    private void queryRegularCardInfo(String ProductId, String ContractId) {

        QueryRegularCardInfoRequest request = new QueryRegularCardInfoRequest();
        request.setContractId(ContractId);
        request.setProductId(ProductId);
        LogUtil.print("ContractId is " + ContractId + " ProductId is " + ProductId);
        FinanceRequestManager.getInstance().queryCardMsg(request, new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    PublicToEvent.FinalEvent( ShoudanStatisticManager.Finance_Product_takeOut, context);
                    Intent intent = new Intent(ProductDetailsActivity.this, WithdrawalActivity.class);
                    String cardMsg = responseData.toString();
                    intent.putExtra("cardMsg", cardMsg);
                    intent.putExtra("prodName", newValidprodApplyprodInfo.getProdName());
                    intent.putExtra("productId", newValidprodApplyprodInfo.getProductId());
                    intent.putExtra("contractId", contractId);
                    intent.putExtra("productType", productType);
                    startActivity(intent);
                } else {
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toastInternetError();

            }
        });
    }

    public void PromptDialog() {
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                queryRegularCardInfo(productId, contractId);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        String content1 = "您正在执行提前取出操作，年化收益率将降低为" + newProductInfo.getRyRate() + "%请点击确定后再继续操作！";

        DialogCreator.createFullContentDialog(context, "确定", "取消", content1, positiveListener,
                negativeListener).show();

    }
}

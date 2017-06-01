package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
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
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeQueryActivity;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.shoudan.component.IconTextView;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.component.NavigationBar;

/**
 * 收银
 * Created by Administrator on 2017/2/23.
 */
public class FragmentCheckstand extends BaseFragment implements NavigationBar.OnNavBarClickListener, View.OnClickListener {
    private View view;
    private NavigationBar navigationBar;
    private IconTextView skSk, smSk, deSk, cxJy, d0Tk, syZj;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_checkstand, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
       navigationBar= (NavigationBar) view.findViewById(R.id.navigation_bar);
        navigationBar.setTitle("收银");
        navigationBar.setActionBtnText("交易记录");
        navigationBar.setOnNavBarClickListener(this);
        skSk = (IconTextView) view.findViewById(R.id.sk_sk);//刷卡收款
        smSk = (IconTextView) view.findViewById(R.id.sm_sk);//扫码收款
        deSk = (IconTextView) view.findViewById(R.id.de_sk);//大额收款
        cxJy = (IconTextView) view.findViewById(R.id.cx_jy);//撤销交易
        d0Tk = (IconTextView) view.findViewById(R.id.d0_tk);//D0提款
        syZj = (IconTextView) view.findViewById(R.id.sy_zj);//生意专家
        skSk.setOnClickListener(this);
        smSk.setOnClickListener(this);
        deSk.setOnClickListener(this);
        cxJy.setOnClickListener(this);
        d0Tk.setOnClickListener(this);
        syZj.setOnClickListener(this);

    }

    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        if (navBarItem== NavigationBar.NavigationBarItem.action){
            queryDealType(context, RecordsQuerySelectionActivity.Type.COLLECTION_RECORD,false);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.sk_sk://刷卡收款
                    if (CommonUtil.isMerchantValid(context)) {
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
//                            .Collection, context);
//                    Intent intent3 = new Intent(context, CollectionsAmountInputActivity.class);
//                    startActivity(intent3);
                        CollectionEnum.Colletcion.setData(null, false);
                        BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                    }
                    break;
                case R.id.sm_sk://扫码收款
                    if (CommonUtil.isMerchantCompleted(context)) {
                        ScanCodeCollectionEnum.ScanCodeCollection.setData(null, false);
                        BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                        barcodeAccessManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection_Homepage_Public);
                        barcodeAccessManager.check(true, true);
                    }
                    break;
                case R.id.de_sk://大额收款
                    if (CommonUtil.isMerchantValid(context)) {
                        LargeAmountAccessManager largeAmountAccessManager = new LargeAmountAccessManager((AppBaseActivity) context);
                        largeAmountAccessManager.setStatistic(ShoudanStatisticManager.LargeAmount_Collection);
                        largeAmountAccessManager.check();
                        LargeAmountEnum.LargeAmount.setAdvertId("");
                    }
                    break;
                case R.id.cx_jy://撤销交易
                    if (CommonUtil.isMerchantValid(context)) {
                  //      ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo, context);
                        UndoEnum.Undo.setAdvertId("");
                        BusinessLauncher.getInstance().start("revocation");
                    }
                    break;
                case R.id.d0_tk://D0提款
                    new DrawButtonClickListener((AppBaseActivity) context)
                            .setStatistic(ShoudanStatisticManager.Do_Public_Input)
                            .onClick(null);
                    DoEnum.Do.setIsCollectionPage(true);
                    break;
                case R.id.sy_zj://生意专家
                    break;
            }
    }
    public  void queryDealType(final FragmentActivity context, final
    RecordsQuerySelectionActivity.Type type, final boolean fromResultPage) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
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
                ToastUtil.toast(context, getString(R.string.socket_fail));
                hideProgressDialog();
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

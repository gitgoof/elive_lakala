package com.lakala.shoudan.bll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.LargeAmountAccess;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.largeamountcollection.LargeAmountInputActivity;
import com.lakala.shoudan.activity.shoudan.largeamountcollection.apply.LargeAmountInstructionActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.SingleLineTextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 15/6/17.
 */
public class LargeAmountAccessManager {

    private AppBaseActivity context;
    private String statistic;
    private AlertDialog progressWithNoMsgDialog;

    public LargeAmountAccessManager(AppBaseActivity context) {
        this.context = context;
    }

    /**
     * 查询大额转账开通状态
     */
    public SingleLineTextView check() {

        isProcessing();
        return null;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    private void handleAccessState() {

        LargeAmountAccess largeAmountAccess = ApplicationEx.getInstance().getUser().getLargeAmountAccess();
        //大额收款权限
        switch (largeAmountAccess) {

            case NOTSUPPORT:
                showNotSupportDialog("您目前还不符合开通条件，更多信息请点击帮助");
                break;
            case SUPPORT:
            case FAILURE:
                startApply();
                break;
            case PROCESSING:
                DialogCreator.createConfirmDialog(context,
                        context.getResources().getString(R.string.ok),
                        context.getResources().getString(R.string.large_amount_processing)).show();
                break;
            case SUCCESS:
                startLargeAmountCollection();
                break;
            case CLOSED:
                DialogCreator.createConfirmDialog(context,
                        context.getResources().getString(R.string.ok),
                        context.getResources().getString(R.string.large_amount_closed)).show();
                break;
            case UNKNOWN:
                LogUtil.e("handleAccessState", "LargeAmountAcdessManager line 83");
                break;
        }
    }


    private void isProcessing() {

        showProgressDialog();
        getLargeAmountAccess();
    }

    private void getLargeAmountAccess() {
        ShoudanService.getInstance().getLargeAmountAccess(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                if (resultForService.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(resultForService.retData);

                        ApplicationEx.getInstance().getUser().setLargeAmountAccess(LargeAmountAccess.valueOf(jsonObject.optString("status", "UNKNOWN")));

                        handleAccessState();
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {

                    context.showMessage(resultForService.retMsg);

                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
            }
        });
    }

    private void hideProgressDialog() {
        context.hideProgressDialog();
    }


    private void showProgressDialog() {
        // zhangmingy
        context.showProgressWithNoMsg();
    }


    private void runOnUiThread(Runnable runnable) {
        if (context != null)
            ((Activity) context).runOnUiThread(runnable);
    }


    private void startLargeAmountCollection() {
        context.startActivity(new Intent(context, LargeAmountInputActivity.class));
        ShoudanStatisticManager.getInstance().onEvent(statistic, context);
        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_deType_Open, context);
    }


    private void startApply() {

        context.startActivity(new Intent(context, LargeAmountInstructionActivity.class));

    }

    private void toast(final String str) {
        ToastUtil.toast(context, str);
    }

    private void showNotSupportDialog(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("帮助", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ProtocalActivity.open(context, ProtocalType.LARGE_AMOUNT_RULES);
                PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_deType, context);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void showMessage(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                showNotSupportDialog(msg);
            }
        });

    }

}

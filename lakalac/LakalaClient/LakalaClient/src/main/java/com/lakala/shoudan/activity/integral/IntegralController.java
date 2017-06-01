package com.lakala.shoudan.activity.integral;

import android.content.Intent;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.integral.activity.IntegralMainActivity;
import com.lakala.shoudan.activity.integral.activity.IntegralSmsVertifyActivity;
import com.lakala.shoudan.activity.integral.callback.CashRecordListCallback;
import com.lakala.shoudan.activity.integral.callback.FirstEnterCallback;
import com.lakala.shoudan.activity.integral.callback.VoucherExplainCallback;
import com.lakala.shoudan.activity.integral.callback.VoucherListCallback;
import com.lakala.shoudan.datadefine.CashRecordList;

/**
 * Created by linmq on 2016/6/8.
 */
public class IntegralController {
    private static IntegralController instance;
    private AppBaseActivity context;
    public static IntegralController getInstance(AppBaseActivity context){
        if(instance == null){
            instance = new IntegralController();
        }
        instance.context = context;
        return instance;
    }

    private IntegralController() {
    }

    public void check2Enter(){
        context.showProgressWithNoMsg();
        isFirstEnter(new FirstEnterCallback(context) {
            @Override
            public void firstEnterCallback(@Nullable Boolean firstEnter) {
                context.hideProgressDialog();
                if(firstEnter == null){
                    return;
                }
                Class enterClazz = null;
                if(firstEnter){
                    enterClazz = IntegralSmsVertifyActivity.class;
                }else {
                    enterClazz = IntegralMainActivity.class;
                }
                Intent intent = new Intent(context, enterClazz);
                context.startActivity(intent);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context,context.getString(R.string.socket_fail));
            }
        });
    }

    public void isFirstEnter(FirstEnterCallback callback){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.POINTS_FIRST);
        request.setResponseHandler(callback);
        request.execute();
    }
    public void getVoucherExplain(VoucherExplainCallback callback){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.VOUCHER_EXPLAIN);
        request.setResponseHandler(callback);
        request.execute();
    }
    public void getVoucherList(VoucherListCallback callback){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.COUPON_LIST);
        request.setResponseHandler(callback);
        request.execute();
    }

    /**
     *
     * @param type 明细类型
     * @param page 分页页数，从1开始
     */
    public void getCashRecordList(CashRecordList.Type type, int page, CashRecordListCallback callback){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.CASH_RECORDLIST);
        HttpRequestParams params = request.getRequestParams();
        params.put("page",page);
        params.put("type",type.getValue());
        request.setResponseHandler(callback);
        request.execute();
    }
}

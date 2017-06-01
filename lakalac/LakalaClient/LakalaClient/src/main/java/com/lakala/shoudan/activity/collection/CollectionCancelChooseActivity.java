package com.lakala.shoudan.activity.collection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.revocation.RevocationPwdVerifyActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fengx on 2015/9/18.
 */
public class CollectionCancelChooseActivity extends AppBaseActivity {

    @Bind(R.id.rl_swingcard)
    RelativeLayout rlSwingcard;
    @Bind(R.id.rl_scancard)
    RelativeLayout rlScancard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_cancel_choose);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("撤销交易");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });
    }

    @OnClick(R.id.rl_swingcard)
    public void swing() {
        Intent intent = new Intent(this, RevocationPwdVerifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentKey.TRANS_STATE, TransactionType.Revocation);
        intent.putExtras(bundle);
        startActivity(intent);
//        getT0Status();
    }

    @OnClick(R.id.rl_scancard)
    public void scan() {
        new BarcodeAccessManager(this).check(false);
    }

    private void getT0Status() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                T0Status status = T0Status.NOTSUPPORT;
                hideProgressDialog();
                LogUtil.print("<S>", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData.toString());
                        int length = 0;
                        if (jsonArray != null) {
                            length = jsonArray.length();
                        }
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.isNull("inpan")) {
                                continue;
                            }
                            String s = jsonObject.getString("inpan");
                            LogUtil.print("<><><>", s);
                            status = T0Status.valueOf(s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (TextUtils.equals(resultServices.retCode, "005020")) {
                    status = T0Status.NOTSUPPORT;
                } else if (TextUtils.equals(resultServices.retCode, "001023")) {
                    status = T0Status.UNKNOWN;
                } else if (TextUtils.equals(resultServices.retCode, "005043")
                        || TextUtils.equals(resultServices.retCode, "005044")) {
                    status = T0Status.UNKNOWN;
                } else if (TextUtils.equals(resultServices.retCode, "005045")) {
                    status = T0Status.UNKNOWN;
                }
                status.setErrMsg(resultServices.retMsg);
                dealResult(status);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().isOpenD02(callback);
    }

    private void dealResult(final T0Status status) {
        {
            switch (status) {
                case COMPLETED: {
                    DialogCreator.createOneConfirmButtonDialog(
                            CollectionCancelChooseActivity.this, "确定", "由于您已开通拉卡拉极速到账服务，系统不再处理您的撤销交易",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                }
                case SUPPORT:
                case FAILURE:
                case NOTSUPPORT:
                case PROCESSING:
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo_Pwd, context);
                    Intent intent = new Intent(this, RevocationPwdVerifyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.IntentKey.TRANS_STATE, TransactionType.Revocation);
                    intent.putExtras(bundle);
                    startActivity(intent);
                default: {
                    break;
                }
            }
        }
    }
}

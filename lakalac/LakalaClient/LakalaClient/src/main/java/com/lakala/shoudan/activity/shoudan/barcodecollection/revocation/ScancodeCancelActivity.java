package com.lakala.shoudan.activity.shoudan.barcodecollection.revocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.revocation.RevocationRecordSelectionActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarCodeCollectionTransInfo;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarcodeResultActivity;
import com.lakala.shoudan.activity.shoudan.barcodecollection.CaptureActivity;
import com.lakala.shoudan.activity.shoudan.records.DealDetails;
import com.lakala.shoudan.activity.shoudan.records.TradeQueryInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fengx on 2015/9/18.
 */
public class ScancodeCancelActivity extends AppBaseActivity implements View.OnClickListener {

    EditText etScancode;
    TextView btnScancodeRevocationOk;
    private static final int SCAN_REQUEST = 100;
    private static final int SHOWLIST_REQUEST = 101;
    private int totalpage = 1;
    public static List<DealDetails> dealDetails = new ArrayList<DealDetails>();
    RelativeLayout llScancode;
    private String srcsid = null;
    private BarCodeRevocationTransInfo barcodeInfo;
    private String series = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode_revocation);
        barcodeInfo = new BarCodeRevocationTransInfo();
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("撤销交易");
        navigationBar.setActionBtnText("使用帮助");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    ProtocalActivity.open(context, ProtocalType.SCAN_REVOCATION_PROTOCOL);
                } else if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });

        etScancode = (EditText) findViewById(R.id.et_scancode);
        btnScancodeRevocationOk = (TextView) findViewById(R.id.btn_scancode_revocation_ok);
        llScancode = (RelativeLayout) findViewById(R.id.ll_scancode);

        btnScancodeRevocationOk.setOnClickListener(this);
        llScancode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scancode_revocation_ok:
                clickQuery();
                break;
            case R.id.ll_scancode:
                scan();
                break;
        }

    }

    private void scan() {
        startActivityForResult(new Intent(this, CaptureActivity.class).putExtra(Constants.IntentKey.TRANS_INFO, barcodeInfo), SCAN_REQUEST);
    }

    private void clickQuery() {

        String code = etScancode.getText().toString();
        if (checkCode(code)) {
            checkWalletTerminal(this, code);
        }
    }

    public boolean checkCode(String code) {
        if (code == null || code.equals(""))
            return false;
        return true;
    }

    //根据输入或扫描得到的商户单号code调用1.6接口去查询srcsid原交易id，再根据这个id去做撤销
    public void querySrcsid(final String parmNo, final String code) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String startTime = sdf.format(new Date());
        sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String endTime = sdf.format(new Date());

        ShoudanService.getInstance().queryRevocationRecords(true, 1, startTime, endTime, "P09", "", "W00001", "", code, parmNo, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {

                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        TradeQueryInfo tradeQueryInfo = new TradeQueryInfo();
                        tradeQueryInfo = tradeQueryInfo.parseObject(jsonObject);
                        //微信撤销只有一条记录
                        totalpage = tradeQueryInfo.getTotalPage();
                        if (tradeQueryInfo.getDealDetails().size() == 0 || totalpage == 0) {
                            toast("暂时没有可撤销的交易");
                            return;
                        }
                        series = tradeQueryInfo.getDealDetails().get(0).getSeries();

                        dealDetails.add(tradeQueryInfo.getDealDetails().get(0));
                        srcsid = tradeQueryInfo.getDealDetails().get(0).getSid();
                        Intent intent = new Intent(ScancodeCancelActivity.this, RevocationRecordSelectionActivity.class);
                        intent.putExtra("isScan", true);
                        startActivityForResult(intent, SHOWLIST_REQUEST);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });

    }

    private void forwardConfirmResultActivity() {
        startActivity(new Intent(this, BarcodeResultActivity.class).putExtra(Constants.IntentKey.TRANS_INFO, barcodeInfo));
    }

    private void forwardSuccessActivity() {
        startActivity(new Intent(this, BarcodeResultActivity.class).putExtra(Constants.IntentKey.TRANS_INFO, barcodeInfo));
    }

    public void revocation(final String srcsid) {

        dealDetails.clear();
        showProgressWithNoMsg();
        //设置超时时间，默认60S，每次查询扫码开通情况时调用通用接口去请求超时时间、重发次数、重发间隔三个参数设置到Paramters
        ShoudanService.getInstance().scancodeRevocation(srcsid, Parameters.timeout * 1000, series, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {

                try {
                    JSONObject jsonObject = new JSONObject(resultForService.retData);
                    String type = jsonObject.optString("scanCodeType");
                    if (type.equals(BarCodeCollectionTransInfo.WECHAT)) {
                        barcodeInfo.setType(VerticalListView.IconType.WECHAT);
                    } else if (type.equals(BarCodeCollectionTransInfo.ALIPAY)) {
                        barcodeInfo.setType(VerticalListView.IconType.ALIPAY);
                    } else if (type.equals(BarCodeCollectionTransInfo.BAIDUPAY)) {
                        barcodeInfo.setType(VerticalListView.IconType.BAIDUPAY);
                    } else if (type.equals(BarCodeCollectionTransInfo.UNIONPAY)) {
                        barcodeInfo.setType(VerticalListView.IconType.UNIONPAY);
                    } else {
                        barcodeInfo.setType(VerticalListView.IconType.UNKNOWN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (resultForService.isRetCodeSuccess()) {
                    barcodeInfo.setTransResult(TransResult.SUCCESS);
                } else {
                    barcodeInfo.setTransResult(TransResult.FAILED);
                    barcodeInfo.setMsg(resultForService.retMsg);
                }
                hideProgressDialog();
                handleRevocationResult();

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                barcodeInfo.setTransResult(TransResult.TIMEOUT);
                barcodeInfo.setType(VerticalListView.IconType.UNKNOWN);
                handleRevocationResult();

            }
        });

    }

    public void checkWalletTerminal(final AppBaseActivity activity, final String code) {

        showProgressWithNoMsg();
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onError(String msg) {
                hideProgressDialog();
                toast(msg);
            }

            @Override
            public void onSuccess() {
                querySrcsid(ApplicationEx.getInstance().getUser().getTerminalId(), code);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_REQUEST:
                if (data != null) {
                    String code = data.getStringExtra("code");
                    if (code == null || code.equals(""))
                        return;
                    etScancode.setText(code);
                    checkWalletTerminal(ScancodeCancelActivity.this, code);
                }
                break;
            case SHOWLIST_REQUEST:
                //开始撤销
                if (resultCode == RESULT_OK) {
                    DealDetails record = RevocationRecordSelectionActivity.selectedDetais;
                    barcodeInfo.setAmount(String.valueOf(record.getDealAmount()));
                    if (srcsid != null && (!(srcsid.equals("")))) {
                        revocation(srcsid);
                    }
                    break;
                } else {
                    dealDetails.clear();
                    break;
                }

        }
    }

    private void handleRevocationResult() {

        switch (barcodeInfo.getTransResult()) {

            case SUCCESS:
                forwardSuccessActivity();
                break;
            case TIMEOUT:
            case FAILED:
                forwardConfirmResultActivity();
                break;
        }
    }
}

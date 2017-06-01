package com.lakala.shoudan.activity.coupon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.encryption.Mac;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.coupon.bean.CouponTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.BarcodeResultActivity;
import com.lakala.shoudan.activity.shoudan.barcodecollection.CaptureActivity;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 代金券收款信息页
 * Created by huangjp on 2016/6/1.
 */
public class CouponCollectionsActivity extends AppBaseActivity{
    private TextView tvAmount;
    private TextView tvFee;
    private TextView tvTotalAmount;
    private RelativeLayout llScancode;
    private EditText etScancode;
    private Button btnSure;
    private  double amount;
    private double fee;
    private static final int SCAN_REQUEST = 200;
    private CouponTransInfo couponTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_collectons);
        init();
    }

    private void init(){
        initView();
        initUI();
        initData();
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("收款信息");
        navigationBar.setActionBtnText(R.string.description_of_business);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem== NavigationBar.NavigationBarItem.back){
                    finish();
                }else if (navBarItem== NavigationBar.NavigationBarItem.action){
                    ProtocalActivity.open(context, ProtocalType.COUPON_PROTOCAL);
                }
            }
        });
    }
    private void initView(){
        tvAmount=(TextView)findViewById(R.id.tv_amount);
        tvFee=(TextView)findViewById(R.id.tv_fee);
        tvTotalAmount=(TextView)findViewById(R.id.tv_total_amount);
        llScancode=(RelativeLayout) findViewById(R.id.ll_scancode);
        etScancode=(EditText)findViewById(R.id.et_scancode);
        btnSure=(Button) findViewById(R.id.btn_sure);
        llScancode.setOnClickListener(this);
        btnSure.setOnClickListener(this);

        etScancode.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }
    private void initData(){
        couponTransInfo=new CouponTransInfo();
        amount=getIntent().getDoubleExtra("amount",0.00);
        fee=getIntent().getDoubleExtra("fee",0.00);
        tvAmount.setText(String.valueOf(Util.formatTwo(amount)));
        tvFee.setText(String.valueOf(Util.formatTwo(fee)));
        tvTotalAmount.setText(String.valueOf(Util.formatTwo(amount+fee)));
    }
    //扫代金券二维码，4.2暂无此功能
    private void scan() {
        startActivityForResult(new Intent(this, CaptureActivity.class).putExtra(Constants.IntentKey.TRANS_INFO,couponTransInfo), SCAN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SCAN_REQUEST:
                if (null!=data){
                    String code = data.getStringExtra("code");
                    if (code == null || code.equals(""))
                        return;
                    etScancode.setText(code);
                    LogUtil.print("code="+code);
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_sure:
                if (!TextUtils.isEmpty(etScancode.getText())){
                    couponTransInfo.setAmount(tvTotalAmount.getText().toString());
                    trade();
                }else{
                    toast("请输入串码");
                }
                break;
//            case R.id.ll_scancode:
//                scan();
//                break;

        }
    }

    /**
     * 代金券收款
     */
    private void trade(){
        String shopNo=ApplicationEx.getInstance().getUser().getMerchantInfo().getShopNo();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.COUPON_TRADE);
        HttpRequestParams params = request.getRequestParams();
        params.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        params.put("chncode",BusinessRequest.CHN_CODE);
        params.put("series",Utils.createSeries());
        params.put("lmercid",shopNo);
        params.put("amount",String.valueOf(amount));
        params.put("fee", String.valueOf(fee));
        params.put("vouchernum",etScancode.getText());
        params.put("tdtm",Util.dateForWallet());
        params.put("rnd", Mac.getRnd());
        params.put("busid","Z00001");
        params.put("mac","");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jsonObject=null;
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        jsonObject=new JSONObject(resultServices.retData);
                        couponTransInfo.setTransResult(TransResult.SUCCESS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    toast(resultServices.retMsg);
                    couponTransInfo.setTransResult(TransResult.FAILED);
                }
                startActi();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toast(getString(R.string.socket_fail));
                couponTransInfo.setTransResult(TransResult.TIMEOUT);
                startActi();
            }
        });
        request.execute();
    }

    private void startActi(){
        Intent intent=new Intent(CouponCollectionsActivity.this, BarcodeResultActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO,couponTransInfo);
        startActivity(intent);
    }

}

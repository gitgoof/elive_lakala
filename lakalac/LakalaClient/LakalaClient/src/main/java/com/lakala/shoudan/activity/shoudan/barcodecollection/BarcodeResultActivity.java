package com.lakala.shoudan.activity.shoudan.barcodecollection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.coupon.activity.CouponInputActivity;
import com.lakala.shoudan.activity.coupon.bean.CouponTransInfo;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.BarCodeRevocationTransInfo;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.ScancodeCancelActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.shoudan.util.CommonUtil;

/**
 * Created by fengx on 2015/9/14.
 */
public class BarcodeResultActivity extends AppBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_receipt);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();

        BaseTransInfo transInfo = (BaseTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);

        navigationBar.setTitle(transInfo.getTransTypeName());
        navigationBar.setBackBtnVisibility(View.GONE);

        VerticalListView resultList = (VerticalListView)findViewById(R.id.result_list);
        resultList.addList(this, transInfo.getBillInfo(), false, getResources().getColor(R.color.gray));



        View errView = findViewById(R.id.error);
        TextView btnBottom = (TextView)findViewById(R.id.button_bottom);
        View btnTop = findViewById(R.id.button_top);
        TextView tvLeft = (TextView) findViewById(R.id.button_left);
        TextView tvCenter = (TextView)findViewById(R.id.button_center);
        TextView tvRight = (TextView)findViewById(R.id.button_right);
        btnBottom.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvCenter.setOnClickListener(this);
        TransResult transResult = transInfo
                .getTransResult();
        if(transResult == TransResult.SUCCESS){
            errView.setVisibility(View.GONE);
            btnBottom.setVisibility(View.GONE);
            btnTop.setVisibility(View.VISIBLE);
            tvLeft.setVisibility(View.GONE);
            tvCenter.setVisibility(View.GONE);
            tvRight.setText("返回首页");
            tvRight.setTag("back_to_main");
            if(transInfo instanceof BarCodeCollectionTransInfo){
                BarCodeCollectionTransInfo info = (BarCodeCollectionTransInfo) transInfo;
                VerticalListView.IconType iconType = info.getIconType();
                if(iconType == VerticalListView.IconType.BAIDUPAY){
                    errView.setVisibility(View.VISIBLE);
                    TextView tvMsg = (TextView) errView.findViewById(R.id.err_detail);
                    tvMsg.setText("温馨提示：该笔收款不支持撤销");
                }
            }
        }else {
            if(transResult == TransResult.FAILED){
                errView.setVisibility(View.VISIBLE);
                btnTop.setVisibility(View.VISIBLE);
                TextView tvMsg = (TextView) errView.findViewById(R.id.err_detail);
                tvMsg.setText(transInfo.getMsg());
                btnBottom.setVisibility(View.GONE);
                tvLeft.setText("返回首页");
                tvLeft.setTag("back_to_main");
                if(transInfo instanceof BarCodeRevocationTransInfo){
                    tvRight.setText("重新撤销");
                    tvRight.setTag("re_revocation");
                }else if(transInfo instanceof BarCodeCollectionTransInfo){
                    tvRight.setText("重新收款");
                    tvRight.setTag("re_collection");
                }else if (transInfo instanceof CouponTransInfo){
                    tvRight.setText("重新收款");
                    tvRight.setTag("re_coupon_collection");
                }
            }else {
                errView.setVisibility(View.GONE);
                btnBottom.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.VISIBLE);
                tvCenter.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.GONE);
                btnBottom.setText("返回首页");
                btnBottom.setTag("back_to_main");
                tvLeft.setTag("query_collection");
                tvLeft.setText("交易查询");
                tvCenter.setTag("call_lakala");
                tvCenter.setText("电话查询");
                if (transInfo instanceof CouponTransInfo){
                    btnBottom.setVisibility(View.GONE);
                    tvCenter.setText("联系客服");
                }
            }
        }

        findViewById(R.id.context).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CommonUtil.initBackground(context);
            }
        });
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        String tag = String.valueOf(view.getTag());
        if(TextUtils.equals(tag,"back_to_main")){
            backToMain();
        }else if (TextUtils.equals("query_collection",tag)){
            TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type
                    .COLLECTION_RECORD);
        }else if (tag.equals("call_lakala")) {
            getPhone();
        }else if(TextUtils.equals("re_collection",tag)){
            //重新收款
            Intent intent = new Intent(context,BarcodeAmountInputActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.IntentKey.TRANS_STATE, TransactionType.BARCODE_COLLECTION);
            intent.putExtras(bundle);
            context.startActivity(intent);
            ScanCodeCollectionEnum.ScanCodeCollection.setData(null, false);
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Scan_Code_Collection,context);
        }else if(TextUtils.equals("re_revocation",tag)){
            //重新撤销
            BusinessLauncher.getInstance().clearTop(ScancodeCancelActivity.class);
        }else if (TextUtils.equals("re_coupon_collection",tag)){
            BusinessLauncher.getInstance().clearTop(CouponInputActivity.class);
        }
    }
    private void getPhone(){
        showProgressWithNoMsg();
        ShoudanService.getInstance().queryLakalaService(new ShoudanService.PhoneQueryListener() {
            @Override
            public void onSuccess(String phoneStr) {
                hideProgressDialog();
                if(!TextUtils.isEmpty(phoneStr)){
                    PhoneNumberUtil.callPhone(BarcodeResultActivity.this, phoneStr);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
    }

    private void backToMain(){
        //返回首页
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onBackPressed() {
        return;
    }
}

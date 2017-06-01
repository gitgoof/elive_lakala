package com.lakala.shoudan.activity.shoudan.barcodecollection.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.ScancodeAccess;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.dialog.AlertDialog;

/**
 * Created by fengx on 2015/9/11.
 */
public class BarcodeApplyActivity extends AppBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_apply);
        initUI();
    }

    private TextView tvMerchantName, tvMerchantAddress, tvRealName, tvIdCardNo, tvBank, tvCardNo;
    private TextView btnApply;
    private CheckBox agreement;
    private TextView protocol;
    private boolean barcode_status;

    protected void initUI() {
        if (getIntent() != null)
            barcode_status = getIntent().getBooleanExtra(Constants.BARCODE_STATUS, false);
        initBar();
        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();
        tvMerchantName = (TextView) findViewById(R.id.tv_merchant_name);
        tvMerchantName.setText(info.getBusinessName());
        tvMerchantAddress = (TextView) findViewById(R.id.tv_merchant_address);

        tvMerchantAddress.setText(info.getBusinessAddress().getFullDisplayAddress());
        tvRealName = (TextView) findViewById(R.id.tv_real_name);
        tvRealName.setText(info.getUser().getRealName());
        tvIdCardNo = (TextView) findViewById(R.id.tv_id_card_number);
        tvIdCardNo.setText(info.getUser().getIdCardInfo().getIdCardId());
        tvBank = (TextView) findViewById(R.id.tv_collection_bank);
        tvBank.setText(info.getBankName());
        tvCardNo = (TextView) findViewById(R.id.tv_collection_card_number);
        if (info.getAccountType() == AccountType.PUBLIC) {
            tvCardNo.setText(Util.formatCompanyAccount(info.getAccountNo()));
        } else {
            tvCardNo.setText(Util.formatCardNumberWithStar(info.getAccountNo()));
        }
        btnApply = (TextView) findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);
        agreement = (CheckBox) findViewById(R.id.agreement);
        agreement.setChecked(true);
        checkedChangeListener.onCheckedChanged(null, true);
        agreement.setOnCheckedChangeListener(checkedChangeListener);
        protocol = (TextView) findViewById(R.id.barcode_collection_protocol);
        protocol.setOnClickListener(this);
    }

    private void initBar() {
//        if (barcode_status)
//            navigationBar.setTitle("二维码收款申请");
//        else
//            navigationBar.setTitle("扫码收款申请");
        navigationBar.setTitle("扫码收款");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_apply:
                if (barcode_status)
                    apply_c_b();
                else
                    apply_b_c();
                break;
            case R.id.barcode_collection_protocol:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Scan_Code_Collection_BarCodeRule, this);
                ProtocalActivity.open(context, ProtocalType.BARCODE_PROTOCAL);
                break;
        }
    }

    private void apply_b_c() {
        showProgressWithNoMsg();
        ShoudanService.getInstance().openScancode(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    ApplicationEx.getInstance().getUser().setScancodeAccess(ScancodeAccess.PROCESSING);
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Scan_Code_Collection_DoApply, context);
                    showMessageAndBackToMain("扫码收款申请成功，审核大约需要2个小时，请等待审核结果！");
                } else {
                    showMessage(resultServices.retMsg);
                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(BarcodeApplyActivity.this, R.string.socket_fail);
                hideProgressDialog();
            }
        }, false);


    }

    /**
     * 二维码收款申请成功
     */
    private void showDialogAndBackToMain() {
        AlertDialog customDialog = new AlertDialog();
        customDialog.setMessage("恭喜！扫码收款申请成功，您的申请结果，将以消息的形式通知您！");
        customDialog.setTitle("提示");
        customDialog.setButtons(new String[]{"确定"});
        customDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        startActivity(new Intent(BarcodeApplyActivity.this, MainActivity.class));
                        break;
                }
                dialog.dismiss();
            }
        });
        customDialog.show((this.getSupportFragmentManager()));
    }

    /**
     * C扫B流程
     */
    private String getEvent() {
        boolean isHomepage = ScanCodeCollectionEnum.ScanCodeCollection.isHomePage();
        if (isHomepage)
            return ShoudanStatisticManager.Scan_Code_Collection_DoApply_Homepage;
        return ShoudanStatisticManager.Scan_Code_Collection_DoApply_HomepagePublic;
    }

    private void apply_c_b() {
        showProgressWithNoMsg();
        ShoudanService.getInstance().openScancode(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                ShoudanStatisticManager.getInstance().onEvent(getEvent(), context);
                if (resultServices.isRetCodeSuccess()) {
                    ApplicationEx.getInstance().getUser().setScancodeAccess(ScancodeAccess.COMPLETED);
                    showDialogAndBackToMain();
                } else {
//                    showMessage(resultServices.retMsg);
                    ToastUtil.toast(BarcodeApplyActivity.this, resultServices.retMsg);
//                    startActivity(new Intent(BarcodeApplyActivity.this, MainActivity.class));
                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(BarcodeApplyActivity.this, R.string.socket_fail);
                hideProgressDialog();
            }
        }, true);
    }


    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            btnApply.setEnabled(b);
        }
    };
}

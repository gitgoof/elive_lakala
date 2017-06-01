package com.lakala.shoudan.activity.shoudan.barcodecollection;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.shoudan.activity.base.AmountInputActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.QRCodeCollectionsActivity;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by LMQ on 2015/12/3.
 */
public class BarcodeAmountInputActivity extends AmountInputActivity {
    private final static int REQUEST_CODE = 0x1242;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);navigationBar.setTitle("扫码收款");
        navigationBar.setActionBtnText("业务说明");
//        navigationBar.setActionBtnText("我的二维码");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.action){
                    ProtocalActivity.open(context, ProtocalType.SCAN_COLLECTION_PROTOCOL);
//                    startActivity(new Intent(BarcodeAmountInputActivity.this, QRCodeCollectionsActivity.class));
                }else if(navBarItem == NavigationBar.NavigationBarItem.back){
                    finish();
                }
            }
        });
    }

    @Override
    protected void onInputComplete(BigDecimal amount) {
        BarCodeCollectionTransInfo barcodeTransInfo = new BarCodeCollectionTransInfo();
        barcodeTransInfo.setAmount(new DecimalFormat("#0.00").format(amount));
        Intent intent = new Intent();
        intent.setClass(context, CaptureActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, barcodeTransInfo);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == CaptureActivity.RESULT_CODE){
            DialogCreator.createConfirmDialog(
                    context,
                    "确定",
                    "无法启用摄像头，请在手机应用权限管理中打开收款宝的摄像头权限"
            ).show();
        }
    }
}

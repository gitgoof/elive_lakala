package com.lakala.shoudan.activity.shoudan.barcodecollection.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalUrl;
import com.lakala.shoudan.activity.shoudan.Constants;

/**
 * 申请,业务说明
 * Created by fengx on 2015/9/11.
 */
public class BarcodeInstructionActivity extends AppBaseActivity implements View.OnClickListener {


    private WebView wvBarcode;
    private TextView btnApply;
    private static String URL1 = "https://download.lakala.com/lklmbl/html/skb_scancodenote.html";
    public static final String URL2 = ProtocalUrl.SCAN_COLLECTION_PROTOCOL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_unopen);
        initUI();
    }

    private boolean barcode_status;

    @Override
    protected void initUI() {
        super.initUI();
        if (getIntent() != null)
            barcode_status = getIntent().getBooleanExtra(Constants.BARCODE_STATUS, false);
        if (barcode_status)
            URL1 = URL2;
        navigationBar.setTitle("扫码收款");
        wvBarcode = (WebView) findViewById(R.id.wv_barcode);
        wvBarcode.loadUrl(URL1);
        btnApply = (TextView) findViewById(R.id.btn_barcode_apply);
        btnApply.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        startNextStepActivity();
    }

    /**
     * 埋点统计区分
     *
     * @return
     */
    private String getEvent() {
        boolean isHomepage = ScanCodeCollectionEnum.ScanCodeCollection.isHomePage();
        if (isHomepage)
            return ShoudanStatisticManager.Scan_Code_Collection_ConfirmInfo;
        return ShoudanStatisticManager.Scan_Code_Collection_ConfirmInfo_Public;
    }

    protected void startNextStepActivity() {
        ShoudanStatisticManager.getInstance().onEvent(getEvent(), this);
        Intent intent = getIntent();
        intent.setClass(this, BarcodeApplyActivity.class);
        intent.putExtra(Constants.BARCODE_STATUS, barcode_status);
        startActivity(intent);
    }
}

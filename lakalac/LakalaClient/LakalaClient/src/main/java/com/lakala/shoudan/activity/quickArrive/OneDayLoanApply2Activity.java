package com.lakala.shoudan.activity.quickArrive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.T0Status;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.shoudan.records.SecondOpenActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 立即提款申请说明页面
 * Created by fengx on 2015/9/11.
 */
public class OneDayLoanApply2Activity extends AppBaseActivity {

    private static final String TAG = "OneDayLoanApply2Activity";
    private WebView wvBarcode;
    private TextView btnApply;
    private static String URL1 = "https://download.lakala.com/lklmbl/html/skb_D0note.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_unopen);
        initUI();
    }


    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("立即提款业务说明");
        wvBarcode = (WebView) findViewById(R.id.wv_barcode);
        wvBarcode.loadUrl(URL1);
        btnApply = (TextView) findViewById(R.id.btn_barcode_apply);
        btnApply.setOnClickListener(listener);
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OneDayLoanApply2Activity.this.startActivity(
                    new Intent( OneDayLoanApply2Activity.this, SecondOpenActivity.class));
            finish();
        }
    };

}

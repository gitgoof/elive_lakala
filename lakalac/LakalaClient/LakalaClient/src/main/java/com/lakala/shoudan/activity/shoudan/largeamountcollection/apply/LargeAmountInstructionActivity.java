package com.lakala.shoudan.activity.shoudan.largeamountcollection.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalUrl;

/**
 * Created by More on 15/6/11.
 *
 * 大额收款业务说明
 */
public class LargeAmountInstructionActivity extends AppBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_amount_instruction);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("大额收款业务说明");
        WebView webView = (WebView)findViewById(R.id.web_view);
        webView.loadUrl(ProtocalUrl.LARGE_AMOUNT_RULES);

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                personalInfoAuth();
            }
        });
    }

    private void personalInfoAuth(){
        //进行实名认证查询

//        new QueryRealNameAuth(){
//
//            @Override
//            protected void onPostExecute(ResultForService resultForService) {
//                if(resultForService.success()){
//                    startNextStepActivity();
//                }else{
//                    Util.toast(resultForService.errMsg);
//                }
//            }
//        }.execute(0);
        startNextStepActivity();

    }

    protected void startNextStepActivity() {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.LargeAmount_Collection_InfoApply, context);
        Intent intent = getIntent();
        intent.setClass(this, LargeAmountCollectionApplyActivity.class);
        startActivity(intent);

    }
}

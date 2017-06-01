package com.lakala.shoudan.activity.shoudan.webmall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.webkit.WebView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 14-7-21.
 */
public class WebMallMainActivity extends WebMallMouldActivity {

    private WebMallTransInfo webMallTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webMallTransInfo = new WebMallTransInfo();
    }

    private void startValidate(){

        showProgressWithNoMsg();

        ShoudanService.getInstance().mallPaymentValidate(webMallTransInfo.getParam(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                hideProgressDialog();
                if (resultForService.isRetCodeSuccess()) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultForService.retData);


                        webMallTransInfo.unpackValidateResult(jsonObject);
                        Intent intent = getIntent();
                        intent.putExtra(ConstKey.TRANS_INFO, webMallTransInfo);
                        intent.setClass(WebMallMainActivity.this, WebMallPaymentActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast("数据验证未通过");
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgressDialog();
            }
        });

    }

	@Override
	protected void initTitle() {
		navigationBar.setTitle("社区商城");		
	}

	@Override
	protected void loadUrl() {
		
		String cttm = String.valueOf(SystemClock.currentThreadTimeMillis());
	    String v = cttm.substring(cttm.length()-2, cttm.length());
		String webMallURL;
	    if(!PropertiesUtil.getUrl().contains("mpos.lakala.com")){
	         webMallURL = "http://27.115.105.167:81/skbshop/index.html?userName=";
	    }else{
	         webMallURL = "http://skb.lakalaec.com/?userName=";
	    }
	    
        webView.loadUrl(webMallURL+  ApplicationEx.getInstance().getUser().getLoginName() + "&v=" + v);
	}

	@Override
	protected void dealShouldOverrideUrlLoading(WebView view, String url) {
      Uri uri= Uri.parse(url);
          if(uri.getScheme().equals("lklmpos")){
              //启动验证
              webMallTransInfo.unpackCallUri(uri);
              startValidate();

          }else{
              //view.loadUrl(url);
              //解决回退不能完全返回的bug
//          if(url.contains("customer/index.html") && !isFirstLoad){
//              finish();
//          }else{
//              isFirstLoad = false;
//
//          }
          webView.loadUrl(url);
      }
	}
}

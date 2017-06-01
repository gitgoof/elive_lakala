package com.lakala.shoudan.activity.webbusiness;

import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lakala.library.util.LogUtil;

import org.java_websocket.util.Base64;
import org.json.JSONObject;


/**
 * Created by More on 15/12/18.
 */
public class BusWebClient extends WebViewClient {

    protected final String PARAMS = "p";
    protected final String SCHEME = "lklmpos";
    protected final String BUSTYPE = "busstype";
    private BusCallerBack busCallerBack;

    public BusWebClient(BusCallerBack busCallerBack) {
        this.busCallerBack = busCallerBack;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
        //handler.cancel(); // Android默认的处理方式
        handler.proceed();  // 接受所有网站的证书
        //handleMessage(Message msg); // 进行其他处理
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //

        LogUtil.print(url);
        Uri uri = Uri.parse(url);

        if(!SCHEME.equals(uri.getScheme())){
            view.loadUrl(url);
            return false;
        }

        String param = uri.getQueryParameter(PARAMS);
        String busstype = uri.getQueryParameter(BUSTYPE);
        if(TextUtils.isEmpty(busstype)){
            return false;
        }

        if(busstype == null){
            return false;
        }
        int value = Integer.parseInt(busstype);
        BusType busType = BusType.values()[value];

        switch (busType) {

            case FINISH:
                busCallerBack.onParamsBack(BusType.FINISH, null);
                break;
            case ALERT_DIALOG:
                try {
                    JSONObject json = new JSONObject(new String(Base64.decode(param), "utf-8"));
                    if (json.has("params")) {
                        String p = new String(Base64.decode(json.optString("params")), "utf-8");
                        busCallerBack.onParamsBack(busType, p);
                    }
                } catch (Exception e){
                    LogUtil.print(e);
                }

                break;
            case REQUEST_TRADE:

                onParamsBack(busType, param);
//                try {
//                    byte[] output = Base64.decode(param);
//                    String pa = new String(output, "utf-8");
//                    JSONObject json = new JSONObject(pa);
//                    if (json.has("params")) {
////                        String busParam = new String(Base64.decode(json.optString("params")), "utf-8");
//                        onParamsBack(busType, json.optString("params"));
//                    }
//
//                } catch (Exception e) {
//                    LogUtil.print(e);
//                }

                break;
        }

        return true;
    }

    private void onParamsBack(BusType busType, String  p){
        if(busCallerBack != null){
            busCallerBack.onParamsBack(busType, p);
        }

    }
}

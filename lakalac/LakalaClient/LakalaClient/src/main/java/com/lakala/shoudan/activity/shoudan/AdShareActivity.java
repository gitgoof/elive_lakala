package com.lakala.shoudan.activity.shoudan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.loan.CameraActivity;
import com.lakala.shoudan.activity.shoudan.records.LoanMoneyActivity;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchaseTransInfo2;
import com.lakala.shoudan.activity.treasure.TreasureBuyActivity2;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.component.NavigationBar;

import org.java_websocket.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by HJP on 2015/9/23.
 */
public class AdShareActivity extends UrlLoaderActivity {
    private String advertUrl;
    private String advertTitle;
    private SharePopupWindow sharePopupWindow;
    protected final String PARAMS = "p";
    protected final String SCHEME = "lklmpos";
    protected final String TEL = "tel";
    protected final String BUSTYPE = "busstype";
    private PrivilegePurchaseTransInfo2 mPrivilegePurchaseTransInfo = new PrivilegePurchaseTransInfo2();
    String url = "lklmpos://action=native?launch=market_zone&p=eyJyZXRDb2RlIjoiMDAwMDAwIiwicmV0TXNnIjoiU1VDQ0VTUyIsInJldERhdGEiOlt7ImFtb3VudCI6IjAuMDEiLCJidXNpZCI6IjFDTiIsIm9yZGVySWQiOiJUMjAxNjA3MjgxMDUyNTQxNjUiLCJwYXlUeXBlIjoiQ0FSRCIsIm9yZGVySW5mbyI6IuWVhuWTgei0reS5sOaUr+S7mCJ9XX0=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {

        super.initUI();
        advertTitle = getIntent().getStringExtra("title");
        advertUrl = getIntent().getStringExtra("url");

        navigationBar.setTitle(advertTitle);
        navigationBar.setActionBtnText("分享");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {

//                try {
//                    Uri uri = Uri.parse(url);
//                    mPrivilegePurchaseTransInfo.unpackCallUri(uri);
//                    String param = mPrivilegePurchaseTransInfo.getParam();
//                    byte[] output = Base64.decode(getParams(param));
//                    String pa = new String(output, "utf-8");
//                    LogUtil.print("------>","pa:"+pa);
//                    JSONObject json = new JSONObject(pa);
//                    mPrivilegePurchaseTransInfo.unpackValidateResult((JSONObject) json.getJSONArray("retData").get(0));
//
//                    Intent intent = new Intent(
//                            AdShareActivity.this, TreasureBuyActivity2.class);
//                    intent.putExtra(Constants.IntentKey.TRANS_INFO, mPrivilegePurchaseTransInfo);
//
////                                        transInfo.setTransTitle("刷卡交易");
////                intent.putExtra("data", "{\"retCode\":\"000000\",\"retMsg\":\"SUCCESS\",\"retData\":[{\"amount\":\"1\",\"orderId\":\"f5a39389-87da-404a-84e1-a47e72e2cec1\",\"payType\":\"PAYTYPE_CARD\",\"orderInfo\":\"商品购买支付\"}]}");
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
//                    startActivity(new Intent(AdShareActivity.this, WalletProcessAuthActivity.class));
//                    getChina();
//                    ActiveNaviUtils.start(context, "shgl", null);
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {//显示分享功能
                    if (sharePopupWindow == null) {
                        sharePopupWindow = new SharePopupWindow(AdShareActivity.this, webView, advertUrl, advertTitle);
                    }
                    sharePopupWindow.showSharePopupWindow();
                    setBackgroundAlpha(0.5f);
                    sharePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            setBackgroundAlpha(1.0f);
                        }
                    });
                }
//                ActiveNaviUtils.start(context, "shgl", null);


            }
        });

//        ApplicationEx.getInstance().getUser().getAccessToken()
        webView.initBus();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.print("------>", "url:" + url);

                Uri uri = Uri.parse(url);

                if (!TextUtils.equals(uri.getScheme(), SCHEME)) {
                    if (TextUtils.equals(uri.getScheme(), TEL)) {
                        Intent it = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(it);
                        return true;
                    }
                    view.loadUrl(url);
                    return false;
                } else {
                    mPrivilegePurchaseTransInfo.unpackCallUri(uri);
//                    String param = uri.getQueryParameter(PARAMS);
                    String param = mPrivilegePurchaseTransInfo.getParam();
                    LogUtil.print("------>", "param:" + param);
                    String key = uri.getQueryParameter("launch");
                    LogUtil.print("------>", "key:" + key);
                    if (!TextUtils.isEmpty(key)) {
                        ActiveNaviUtils.Type type = ActiveNaviUtils.Type.keyValueOf(key);
                        LogUtil.print("------>", type.toString());
                        switch (type) {
                            case MARKET_ZONE:
                                LogUtil.print("------>", "活动广告");
                                try {
                                    byte[] output = Base64.decode(getParams(param));
                                    String pa = new String(output, "utf-8");
                                    LogUtil.print("------>", "pa:" + pa);
                                    JSONObject json = new JSONObject(pa);
                                    if (json.has("retData")) {
                                        String reDat = json.optString("retData");
                                        LogUtil.print("------>", "reDat:" + reDat);
                                        JSONObject json2 = new JSONObject(reDat);
                                        LogUtil.print("------>", "json2:" + json2);
                                        mPrivilegePurchaseTransInfo.unpackValidateResult(json2);
//                                        String params = new String(Base64.decode(json.optString("retData")), "utf-8");
//                                        LogUtil.print("------>","retData:"+params);
//                                        JSONObject jsonObject=new JSONObject(params);
//////                                        transInfo.setOrder(new JSONObject(params));
                                        Intent intent = new Intent(
                                                context, TreasureBuyActivity2.class);
//                                        transInfo.setTransTitle("刷卡交易");
                                        intent.putExtra(Constants.IntentKey.TRANS_INFO, mPrivilegePurchaseTransInfo);
                                        startActivity(intent);

//                                        Intent intent2 = new Intent(context, CollectionPaymentActivity.class);
//                                        CollectionTransInfo collectionTransInfo =  new CollectionTransInfo();
//                                        collectionTransInfo.setAmount("2");
//                                        intent2.putExtra(ConstKey.TRANS_INFO, collectionTransInfo);
//                                        startActivity(intent2);
//                                        Intent intent2 = new Intent(
//                                                context, PrivilegePurchasePaymentActivity.class);
//                                        transInfo.setTransTitle("刷卡交易");
//                                        intent.putExtra(Constants.IntentKey.TRANS_INFO, transInfo);
//                                        startActivity(intent);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                ActiveNaviUtils.start(context, key, null);
                                break;
                        }

                    }
                }
                return true;
            }
        });
    }

    public void setBackgroundAlpha(float al) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = al;
        getWindow().setAttributes(params);
    }

    private void showDialog(String msg, final String finish) {
        //token 失效
        if ("1".equals(finish)) {
            toLoginDialog();
        } else {
            DialogCreator.createConfirmDialog(context, "确定", msg).show();
        }
    }

    public static void toLoginDialog() {
        final Context context = ApplicationEx.getInstance();
        com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();
        alertDialog.setMessage("您的登录状态异常,请重新登陆");
        alertDialog.setButtons(new String[]{context.getString(com.lakala.platform.R.string.ui_certain)});
        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                try {
                    dialog.dismiss();
                    LoginUtil.loginOut(context);

                } catch (Exception e) {
                    LogUtil.print(e);
                    CrashlyticsUtil.logException(e);
                }

            }
        });
        ErrorDialogActivity.startSelf(alertDialog, "toLoginDialog");
    }

    public String getParams(String params) {
        char[] chars = params.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                stringBuilder.append('+');
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }

}

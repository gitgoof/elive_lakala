package com.lakala.shoudan.activity.webbusiness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchasePaymentActivity;
import com.lakala.shoudan.activity.wallet.request.PartnerPayParams;
import com.lakala.shoudan.activity.treasure.TreasureBuyActivity;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.component.NavigationBar;

import org.java_websocket.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by More on 15/12/18.
 */
public class WebMallContainerActivity extends AppBaseActivity {

    private BusWebView webView;
    protected SharePopupWindow sharePopupWindow;
    private WebTransInfo webTransInfo;
    private String title;
    private String url;
    private ActiveNaviUtils.Type type;
    private String advertUrl;
    private String advertTitle;
    public static String onResumeUrl = null;
    public static void open(Context context, String title, String url, ActiveNaviUtils.Type type){
        Intent intent = new Intent(context,WebMallContainerActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("url",url);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }
    public static void open(Context context, String title, String url,String shareUrl, ActiveNaviUtils.Type type){
        Intent intent = new Intent(context,WebMallContainerActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("url",url);
        intent.putExtra("type",type);
        intent.putExtra("share",shareUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_container);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        type = (ActiveNaviUtils.Type) intent.getSerializableExtra("type");
        switch (type){
            case TREASURE:
                oneKuaiPoint();
                break;
        }
        if(!TextUtils.isEmpty(intent.getStringExtra("share"))){
            advertUrl=intent.getStringExtra("share");
        }
        webTransInfo = new WebTransInfo();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(onResumeUrl)){
            webView.loadUrl(onResumeUrl);
            onResumeUrl = null;
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        webView = (BusWebView)findViewById(R.id.web_view_container);

        webView.initBus();
        webView.setBusCallerBack(new BusCallerBack() {
            @Override
            public void onParamsBack(BusType type, String param) {

                switch (type) {
                    case FINISH:
                        finish();
                        break;
                    case ALERT_DIALOG:
                        JSONObject info = null;
                        try {
                            info = new JSONObject(param);
                            //{"finish":"1","tqinfo":"手机号或密码错误,请重新登录！"}
                            if (info.has("tqinfo")) {
                                showDialog(info.optString("tqinfo"), info.optString("finish", "0"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case REQUEST_TRADE:
                        switch (WebMallContainerActivity.this.type) {
                            case TREASURE:
                                yikuai(param);
                                break;
                            case TEQUAN:
                                try {
                                    byte[] output = Base64.decode(param);
                                    String pa = new String(output, "utf-8");
                                    JSONObject json = new JSONObject(pa);
                                    if (json.has("params")) {
                                        String params = new String(Base64.decode(json.optString("params")), "utf-8");
                                        webTransInfo.setOrder(new JSONObject(params));
                                        Intent intent = new Intent(
                                                context, PrivilegePurchasePaymentActivity.class);
                                        webTransInfo.setTransTitle(title);
                                        intent.putExtra(Constants.IntentKey.TRANS_INFO, webTransInfo);
                                        startActivity(intent);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case ONE_YUAN:
                                //TODO 一元购交易
                                break;
                        }
                        break;
                }

            }
        });

        if(type == ActiveNaviUtils.Type.TEQUAN){
            navigationBar.setActionBtnText(R.string.share);
            navigationBar.setActionBtnVisibility(View.VISIBLE);
            navigationBar.setActionBtnEnabled(true);
//            advertUrl=url;
            advertTitle=title;
        }else if(type == ActiveNaviUtils.Type.TREASURE){
            TextView backButton = navigationBar.getBackButton();
            backButton.setCompoundDrawablePadding(
                    (int)context.getResources().getDimension(R.dimen.dimen_10));
            backButton.setText("返回");
            backButton.setTextColor(
                    getResources().getColorStateList(R.color.action_text_color_selector));
            navigationBar.setActionBtnEnabled(false);
            navigationBar.setActionBtnVisibility(View.GONE);
            navigationBar.getNavClose().setTextColor(getResources().getColorStateList(R.color.action_text_color_selector));
            navigationBar.getNavClose().setVisibility(View.VISIBLE);
            navigationBar.getNavClose().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.finish();
                }
            });
        }else{
            navigationBar.setActionBtnEnabled(false);
            navigationBar.setActionBtnVisibility(View.GONE);
        }
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {//显示分享功能
                    if (sharePopupWindow == null) {
                        sharePopupWindow = new SharePopupWindow(context, webView, advertUrl, advertTitle);
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
            }
        });
        navigationBar.setTitle(String.valueOf(title));
        webView.loadUrl(String.valueOf(url));

    }

    private void yikuai(String param) {
        webTransInfo.setParam((param));
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onError(String msg) {
                hideProgressDialog();
                context.toast(msg);
            }

            @Override
            public void onSuccess() {
                startValidate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
        }
    }

    private void showDialog(String msg,final String finish){
        //token 失效
        if("1".equals(finish)){
            toLoginDialog();
        }else {
            DialogCreator.createConfirmDialog(context,"确定",msg).show();
        }
    }

    public static void toLoginDialog(){
        final Context context = ApplicationEx.getInstance();
        com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();
        alertDialog.setMessage("您的登录状态异常,请重新登陆");
        alertDialog.setButtons(new String[]{context.getString(com.lakala.platform.R.string.ui_certain)});
        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                try {
                    dialog.dismiss();
                    LoginUtil.loginOut(context);

                }catch (Exception e){
                    LogUtil.print(e);
                    CrashlyticsUtil.logException(e);
                }

            }
        });
        ErrorDialogActivity.startSelf(alertDialog, "toLoginDialog");
    }

    private void startValidate(){

        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.PARTNER_PAY);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    String data = resultServices.retData;
                    TreasureBuyActivity.open(context,data);
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Purchase_Product_Treasure_Buy,context);
                }else{
                    context.toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                context.toastInternetError();
            }
        });
        PartnerPayParams params = new PartnerPayParams(context);
        params.setParams(webTransInfo.getParam());
        WalletServiceManager.getInstance().start(params, request);
    }

    /**
     * 一块夺宝打点
     */
    public void oneKuaiPoint(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Treasure_Buy_Home;
        }else if(PublicEnum.Business.isDirection()){
            event= ShoudanStatisticManager.Treasure_Buy_De;
        }else if(PublicEnum.Business.isAd()){
            event= ShoudanStatisticManager.Treasure_Buy_Ad;
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Treasure_Buy_Public;
        }else {
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }
}

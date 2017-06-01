package com.lakala.shoudan.activity.shoudan.webmall.privilege;

import org.java_websocket.util.Base64;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.Parameters;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.webmall.WebMallMouldActivity;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.ui.component.NavigationBar;


/**
 * 特权购买
 * @author ZhangMY
 *
 */
public class PrivilegePurchaseWebMallActivity extends WebMallMouldActivity {

	private PrivilegePurchaseTransInfo mPrivilegePurchaseTransInfo;
	private String advertUrl;
	private String advertTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	public void initData(){
		mPrivilegePurchaseTransInfo = new PrivilegePurchaseTransInfo();
        navigationBar.setTitle(advertTitle);
	}
	@Override
	protected void initTitle() {
		navigationBar.setTitle("专享购买");
		navigationBar.setActionBtnText(R.string.share);
		navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
			@Override
			public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
				if (navBarItem == NavigationBar.NavigationBarItem.back) {
					onBackPressed();
				} else if (navBarItem == NavigationBar.NavigationBarItem.action) {//显示分享功能
					if (sharePopupWindow == null) {
						sharePopupWindow = new SharePopupWindow(PrivilegePurchaseWebMallActivity.this, webView, advertUrl, advertTitle);
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
	}

	@Override
	protected void loadUrl() {

        StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL);
        url.append("business/tqpage/").append(ApplicationEx.getInstance().getUser().getLoginName()).append(".html");
        url.append("?userToken=").append(ApplicationEx.getInstance().getUser().getAccessToken());//TODO 确认使用哪一种token
        url.append("&verifyType=").append("1");
        url.append("&ver=").append(Util.getVersionCode());
		url.append("&p_v=").append(Util.getAppVersionCode());
//		url.append("&v="+v);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


		advertUrl=getIntent().getStringExtra("url");
		advertTitle = getIntent().getStringExtra("title");
//		StringBuffer newUrl=new StringBuffer();
//		newUrl.append("<a href=\"");
//		newUrl.append(advertUrl);
//		newUrl.append("\">专享购买</a>");

		StringBuffer jumpToUrl;
		jumpToUrl=new StringBuffer();
		jumpToUrl.append(advertUrl);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存，只从网络获取数据
		if("专享购买".equals(advertTitle)){
            webView.loadUrl(url.toString());
        }else{
            webView.loadUrl(jumpToUrl.toString());
        }

	}


	@Override
	protected void dealShouldOverrideUrlLoading(WebView view, String url) {
		try{
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equals("lklmpos")) {
                //启动验证
                mPrivilegePurchaseTransInfo.unpackCallUri(uri);
                if (mPrivilegePurchaseTransInfo.getBusstype().equals("0")) {//返回首页
                    finish();
                } else if (mPrivilegePurchaseTransInfo.getBusstype().equals("1")) {//显示对话框
                    String params = mPrivilegePurchaseTransInfo.getParam();

                    JSONObject json = new JSONObject(new String(Base64.decode(params), "utf-8"));
                    if (json.has("params")) {
                        String p = new String(Base64.decode(json.optString("params")), "utf-8");
                        JSONObject info = new JSONObject(p);
                        //{"finish":"1","tqinfo":"手机号或密码错误,请重新登录！"}
                        if (info.has("tqinfo")) {
                            showDialog(info.optString("tqinfo"), info.optString("finish", "0"));
                        }
                    }
                } else if (mPrivilegePurchaseTransInfo.getBusstype().equals("2")) {//支付
                    String p = mPrivilegePurchaseTransInfo.getParam();//参数p，p中的parms才是所需的订单信息
                    byte[] output = Base64.decode(p);
                    String pa = new String(output, "utf-8");
                    JSONObject json = new JSONObject(pa);
                    if (json.has("params")) {
                        String params = new String(Base64.decode(json.optString("params")), "utf-8");
                        mPrivilegePurchaseTransInfo.unpackValidateResult(new JSONObject(params));
                        startActivity();
                    }
                }

            } else {
//		          view.loadUrl(url);
            }
        }catch(Exception e){
			e.printStackTrace();
		}
	      
	}

    private void  startActivity(){

          Intent intent = getIntent();
          intent.putExtra(Constants.IntentKey.TRANS_INFO, mPrivilegePurchaseTransInfo);
          intent.setClass(PrivilegePurchaseWebMallActivity.this, PrivilegePurchasePaymentActivity.class);
          startActivity(intent);
    }
    
    private void showDialog(String msg,final String finish){
    	//token 失效
    	if("1".equals(finish)){
            //TODO  token失效对话框展示
//    		Intent intent=new Intent(context, TokenOutOfDateActivity.class);
//    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    		context.startActivity(intent);
    	}else {
    		AlertDialog.Builder builder=new AlertDialog.Builder(PrivilegePurchaseWebMallActivity.this);
    		builder.setMessage(msg);
    		builder.setCancelable(false);
    		builder.setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.cancel();
    			}
    		});
    		AlertDialog dialog=builder.create();
    		dialog.show();
    	}
    }
}

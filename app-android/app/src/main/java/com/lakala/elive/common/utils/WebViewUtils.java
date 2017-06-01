package com.lakala.elive.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lakala.elive.Constants;
import com.lakala.elive.Session;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * 
 * WebView常用设置工具类
 * 
 * @author hongzhiliang
 *
 */
public class WebViewUtils {

	private static WebViewUtils mInstance;
	private Context mContext;
	
	private WebViewUtils(Context context){
		super();
		this.mContext = context;
	} 
	
	public static WebViewUtils getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new WebViewUtils(context);
		}
		return mInstance;
	}
	
	/**
	 * WebView常规属性设置
	 * @param webView
	 */
	public void initSetting(WebView webView) {
        //设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript,则webview必须设置支持Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
	}
	
	/**
	 * 设置客户端
	 * @param webView
	 * @param progressBar
	 */
	public void setNormalClient(WebView webView, ProgressBar progressBar){
	    //集成WebView进度
        webView.setWebChromeClient(new ExWebChromeClient(progressBar));
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new ExWebViewClient());
	}
	
	/**
	 * 
	 * 设置SSL客户端
	 * @param webView
	 * @param progressBar
	 * 
	 */
	public void setSSLClient(Context mContext, WebView webView, ProgressBar progressBar){
		try {
			webView.setWebChromeClient(new ExWebChromeClient(progressBar));
			webView.setWebViewClient(new SSLWebViewClient(mContext));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 设置WebView Cookie
	 * @param context
	 * @param url
	 */
	public void syncCookie(Context context, String url) {
		try {
		    CookieSyncManager.createInstance(context);
		    CookieManager cookieManager = CookieManager.getInstance();
		    cookieManager.setAcceptCookie(true);  
		    cookieManager.removeSessionCookie();//移除
            String authTokenStr = Session.get(context).getUserLoginInfo().getAuthToken();
		    if( authTokenStr !=null && !"".equals(authTokenStr) ){

		    	cookieManager.setCookie(url, "authKey="+ Utils.urlEncode(authTokenStr));
		    }
		    CookieSyncManager.getInstance().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载页面
	 * @param context
	 * @param webView
	 * @param url
	 */
	public void loadUrl(Context context, WebView webView, String url) {
		syncCookie(context, url);
		webView.loadUrl(url);
	}
	
	
	/**
	 * 实现ChromClient
	 * @author hongzhiliang
	 *
	 */
	class ExWebChromeClient extends WebChromeClient {
		private ProgressBar mProgressBar;
		
		public ExWebChromeClient(ProgressBar progressBar){
			this.mProgressBar = progressBar;
		}
		
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        	if(mProgressBar != null){
        		if( mProgressBar.getVisibility() == View.GONE ){
        			mProgressBar.setVisibility(View.VISIBLE);
        		}
        		
        		mProgressBar.setProgress(newProgress);
        		if(newProgress >= 100){
        			mProgressBar.setVisibility(View.GONE);
        		}
        	}
        }
        
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);  
        }
        
    }
	
	class ExWebViewClient extends WebViewClient {
		
		// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
        
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadUrl(failingUrl);
        }
	}
	
	/**
	 * 
	 * 支持SSL的WebViewClient
	 * 
	 * Android 版本必选支持 5.0（API 21） 以上的
	 *
	 */
	class SSLWebViewClient extends WebViewClient {
		
		private X509Certificate[] certificatesChain;
	    private PrivateKey clientCertPrivateKey;
	    
	    private String certfile_password = "lakala123";
	    private Context mContext;
	    
		public SSLWebViewClient(Context mContext) throws Exception {
			super();
			this.mContext = mContext;
			initPrivateKeyAndX509Certificate();
		}  
	    
		private void initPrivateKeyAndX509Certificate() throws Exception {
	        KeyStore keyStore = KeyStore.getInstance("PKCS12","BC");
			InputStream inputStream = mContext.getResources().getAssets().open("client.p12");
			keyStore.load(inputStream, certfile_password.toCharArray());
			printKeystoreInfo(keyStore); //for debug
		    Enumeration<?> localEnumeration = keyStore.aliases();
		    while (localEnumeration.hasMoreElements()) {  
	            String aliasName = (String) localEnumeration.nextElement();
	            clientCertPrivateKey = (PrivateKey) keyStore.getKey(aliasName, certfile_password.toCharArray());
	            if (clientCertPrivateKey == null) {
	                continue;
	            } else {  
	                Certificate[] arrayOfCertificate = keyStore.getCertificateChain(aliasName);
	                certificatesChain = new X509Certificate[arrayOfCertificate.length];
	                for (int j = 0; j < certificatesChain.length; j++) {  
	                	certificatesChain[j] = ((X509Certificate) arrayOfCertificate[j]);
	                }  
	            }  
	        }  
	    }
		
		public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
	        if((null != clientCertPrivateKey) && ((null!=certificatesChain) 
	        		&& (certificatesChain.length !=0))){
	        	request.proceed(this.clientCertPrivateKey, this.certificatesChain);   
	        }else{  
	        	request.cancel();  
	        }
		}
		

	          
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
	    @Override
	    public void onReceivedSslError(final WebView view, SslErrorHandler handler, SslError error) {
	        handler.proceed();     // 信任所有的证书   默认是handler.cancle(),即不做处理
	    } 
	    
	    @Override
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        super.onReceivedError(view, errorCode, description, failingUrl);
	        //加载出错的自定义界面  
	        view.loadUrl(Constants.WEBVIEW_LOAD_ERR_PAGE);
	    }
	    
	    /**
	     * 证书打印测试
	     * @param keystore
	     * @throws KeyStoreException
	     */
		private  void printKeystoreInfo(KeyStore keystore)
				throws KeyStoreException {
			System.out.println("Provider : " +keystore.getProvider().getName());
			System.out.println("Type : " +keystore.getType());
			System.out.println("Size : " +keystore.size());
			Enumeration en = keystore.aliases();
			while (en.hasMoreElements()) {
				System.out.println("Alias: " +en.nextElement());
			}
		}
		
	}

}

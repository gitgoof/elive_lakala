package com.lakala.elive.report;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.lakala.elive.common.net.NetAPI;

/**
 * 
 * 暴露给WebView的 Html JS 调用的方法
 * 
 * @author hongzhiliang
 *
 */
public class WebViewJsInterface {
	private final static String TAG = WebViewJsInterface.class.getSimpleName();
	private ReportFragListener reportListener;//外部接口
	private Context mContext;
	
	public WebViewJsInterface(Context mContext, ReportFragListener reportListener){
		this.reportListener = reportListener;
		this.mContext = mContext;
	}
	
	/**
	 * 页面加载错误暴露的接口方法
	 */
    @JavascriptInterface
    public void errorReload() {
    	new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
	        public void run() {
				reportListener.updateContentUI(reportListener.getCurContentUrl());  
	        }  
		});
    }
    
    /**
     * 
     * 提供给报表查询界面的方法
     * @param resultUrl
     * 
     */
    @JavascriptInterface
    public void startQuery(final String resultUrl) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
				if (resultUrl != null) {  
					Toast.makeText(mContext, "查询条件：" + resultUrl, Toast.LENGTH_SHORT).show();
					reportListener.updateContentUI(reportListener.getShortResutlUrl() + "?" + resultUrl);
					reportListener.closeDrawer(Gravity.END);
                }  
            }  
        });  
    } 
    
}

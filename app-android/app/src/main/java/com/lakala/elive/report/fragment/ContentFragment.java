package com.lakala.elive.report.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lakala.elive.R;
import com.lakala.elive.common.utils.WebViewUtils;
import com.lakala.elive.report.ReportFragListener;


/**
 * 
 * 报表内容显示 WebView
 * 
 * @author hongzhiliang
 *
 */
public class ContentFragment extends Fragment {
	
	private final String TAG = ContentFragment.class.getSimpleName();
	private WebView webView; //内容显示webview
	private ProgressBar progressBar; //页面加载进度调
	private WebViewUtils wvUtils;
	private ReportFragListener reportListener;//外部接口
	private View view;
	
    public ContentFragment() {
    	
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.report_content_fragment, container, false);
		initView();
		return view;
	}

	private void initView() {
		webView = (WebView) view.findViewById(R.id.report_show_content_webview);
		progressBar = (ProgressBar) view.findViewById(R.id.index_progressBar);
		wvUtils = WebViewUtils.getInstance(getActivity());
		wvUtils.initSetting(webView);
		wvUtils.setSSLClient(getActivity(), webView, progressBar);
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState); 
    } 
	
    /**
     * 
     * 外部更新内容页面的接口
     * @param url
     * 
     */
	public void updateContent(String url) {
	    wvUtils.loadUrl(getActivity(),webView,url);
	}
	
    /**
     * 
     * 提供给MainActivity设置回调接口方法
     * @param reportListener
     * 
     */
    public void setReportListener(ReportFragListener reportListener){
        this.reportListener = reportListener;
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	    	webView.goBack();
	        return true;
	    }
	    return getActivity().onKeyDown(keyCode, event);
	}
    
}

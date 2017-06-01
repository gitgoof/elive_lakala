package com.lakala.elive.report.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lakala.elive.R;
import com.lakala.elive.common.utils.WebViewUtils;
import com.lakala.elive.report.ReportFragListener;
import com.lakala.elive.report.WebViewJsInterface;


/**
 * 查询条件页面
 * 
 * @author hongzhiliang
 * 
 */
public class RightQueryFragment extends Fragment {
	private final String TAG = RightQueryFragment.class.getSimpleName();
	private WebView webView;
	private ProgressBar progressBar;
	private View view;
	private WebViewUtils wvUtils;
	private ReportFragListener reportListener;// 外部接口

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.report_right_query_fragment,container, false);
		return view;
	}

	private void initView() {
		webView = (WebView) view.findViewById(R.id.report_show_query_webview);
		progressBar = (ProgressBar) view.findViewById(R.id.index_progressBar);
		wvUtils = WebViewUtils.getInstance(getActivity());
		wvUtils.initSetting(webView);
		wvUtils.setSSLClient(getActivity(), webView, progressBar);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 提供给MainActivity设置回调接口方法
	 * 
	 * @param url
	 */
	public void updateContent(String url) {
		Log.i("updateContent", "query:" + url);
		//设置提供给页面调用的方法
		webView.addJavascriptInterface(new WebViewJsInterface(getActivity(),reportListener), "jsinterface");
		wvUtils.loadUrl(getActivity(), webView, url);
	}

	/**
	 * 
	 * 提供给MainActivity设置回调接口方法
	 * 
	 * @param reportListener
	 * 
	 */
	public void setReportListener(ReportFragListener reportListener) {
		this.reportListener = reportListener;
	}
	
}

package com.lakala.elive.market.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.WebViewUtils;
import com.lakala.elive.market.base.BaseFragment;
import com.lakala.elive.report.ReportFragListener;


/**
 * 
 * 报表内容显示 WebView
 * 
 * @author hongzhiliang
 *
 */
public class ShopReportFragment extends BaseFragment {

	private WebView webView; //内容显示webview
	private ProgressBar progressBar; //页面加载进度调
	private WebViewUtils wvUtils;
	private View view;
	MerShopInfo merchantInfo = null; //商户详情信息


    public ShopReportFragment() {
    	
    }


	@SuppressLint("ValidFragment")
	public ShopReportFragment(MerShopInfo merchantInfo) {
		this.merchantInfo = merchantInfo;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.report_content_fragment, container, false);
		initView();
		return view;
	}

	@Override
	protected void setViewLayoutId() {

	}

	@Override
	protected void bindViewIds() {

	}

	@Override
	protected void initData() {
		String url = Constants.ELIVE_DATA_RESULT_API + "?reportId=WF_SHOP_TRANS_AMT_001&shopid=" + merchantInfo.getShopNo();
		//	String url = Constants.ELIVE_DATA_RESULT_API + "?reportId=WF_SHOP_TRANS_AMT_001&shopid=5839193&beginDate=2017-03-30";
//		Utils.showToast(getActivity(), url);
		updateContent(url);
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


	public void updateContent(String url) {
	    wvUtils.loadUrl(getActivity(),webView,url);
	}

	@Override
	public void refershUi() {

	}
}

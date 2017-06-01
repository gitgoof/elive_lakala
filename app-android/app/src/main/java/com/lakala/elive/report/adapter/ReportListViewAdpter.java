package com.lakala.elive.report.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.GsonJsonUtils;

import java.util.List;


/**
 * 关注报表 ListView
 * @author hongzhiliang
 *
 */
public class ReportListViewAdpter extends BaseAdapter implements ApiRequestListener {
	
	private List<ReportInfo> reportInfoList;
	private LayoutInflater inflater;
	private Context mContext;
	
	public ReportListViewAdpter(Context context, List<ReportInfo> reportInfoList) {
		this.mContext = context;
		this.reportInfoList = reportInfoList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return reportInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return reportInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder = null;
		if (view == null) {
			holder = new Holder();
			view = inflater.inflate(R.layout.report_attention_item, null);
			holder.childName = (TextView) view.findViewById(R.id.tv_report_name);
			holder.ivBtnAttention = (ImageButton) view.findViewById(R.id.iv_btn_attention);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
        final ReportInfo reportInfo = reportInfoList.get(position);
		holder.childName.setText(reportInfo.getReportName());
		final int index = position;
		holder.ivBtnAttention.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                UserReqInfo reqInfo = new UserReqInfo();
                reqInfo.setAuthToken(Session.get(mContext).getUserLoginInfo().getAuthToken());
                reqInfo.setReportId(reportInfo.getReportId());

                try {
                    Log.e("UserReqInfo", "removeAttention   " + GsonJsonUtils.parseObj2Json(reqInfo));
                } catch (Exception e) {
                    e.printStackTrace();
                }

				NetAPI.removeAttention(mContext,ReportListViewAdpter.this,reqInfo);
				reportInfoList.remove(index);
				notifyDataSetChanged();
			}
		});
		return view ;
	}
	
	
	public static class Holder{
		TextView childName;
		ImageButton ivBtnAttention;
	}

	public List<ReportInfo> getReportInfoList() {
		return reportInfoList;
	}

	public void setReportInfoList(List<ReportInfo> reportInfoList) {
		this.reportInfoList = reportInfoList;
	}
	
	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_REMOVE_ATTENTION: //添加关注
                Log.e("ACTION_REMOVE_ATTENTION", " onSuccess " +obj);
//			Utils.makeEventToast(mContext, "取消关注成功", true);
			//刷新数据
			Session.get(mContext).updateReportAttention();
			break;
		}
	}

    @Override
	public void onError(int method, String statusCode) {
		switch (method) {
			case NetAPI.ACTION_REMOVE_ATTENTION: //添加关注
                Log.e("ACTION_REMOVE_ATTENTION", " onError: " +statusCode);
//			Utils.makeEventToast(mContext, "取消关注失败", true);
//			//刷新数据
			Session.get(mContext).updateReportAttention();
			break;
		}
	}
	
}

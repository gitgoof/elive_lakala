package com.lakala.elive.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.NoticeInfo;

import java.util.List;


/**
 * 
 * 公告ListView
 * 
 * @author hongzhiliang
 *
 */
public class NoticeListViewAdpter extends BaseAdapter {
	
	private List<NoticeInfo> noticeInfoList;
	private LayoutInflater inflater;
	
	private Context mContext;
	
	public NoticeListViewAdpter(Context context, List<NoticeInfo> noticeInfoList) {
		this.mContext = context;
		this.noticeInfoList = noticeInfoList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return noticeInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return noticeInfoList.get(position);
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
			view = inflater.inflate(R.layout.list_item_home_notice, null);
			holder.tvNoticeTitle = (TextView) view.findViewById(R.id.tv_notice_title);
			holder.tvNoticeTime = (TextView) view.findViewById(R.id.tv_notice_time);
			holder.ivNoticeType = (ImageView) view.findViewById(R.id.iv_notice_type);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
		
		NoticeInfo noticeInfo = noticeInfoList.get(position);
		holder.tvNoticeTitle.setText(noticeInfo.getNoticeSubject());
		holder.tvNoticeTime.setText(noticeInfo.getOperateDateStr());
		
		if( "1".equals(noticeInfo.getNoticeType()) ){
			holder.ivNoticeType.setImageResource(R.drawable.p_gg_01);
		}else if( "2".equals(noticeInfo.getNoticeType()) ){
			holder.ivNoticeType.setImageResource(R.drawable.p_gg_02);
		}else if( "3".equals(noticeInfo.getNoticeType()) ){
			holder.ivNoticeType.setImageResource(R.drawable.p_gg_03);
		}else if( "4".equals(noticeInfo.getNoticeType()) ){
			holder.ivNoticeType.setImageResource(R.drawable.p_gg_04);
		}else if( "5".equals(noticeInfo.getNoticeType()) ){
			holder.ivNoticeType.setImageResource(R.drawable.p_gg_05);
		}
		
		return view ;
	}
	
	public static class Holder{
		TextView tvNoticeTitle;
		TextView tvNoticeTime;
		ImageView ivNoticeType;
	}

	public List<NoticeInfo> getNoticeInfoList() {
		return noticeInfoList;
	}

	public void setNoticeInfoList(List<NoticeInfo> noticeInfoList) {
		this.noticeInfoList = noticeInfoList;
	}
	
}

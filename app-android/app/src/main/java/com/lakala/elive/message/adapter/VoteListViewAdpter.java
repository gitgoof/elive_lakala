package com.lakala.elive.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.VoteTaskInfo;

import java.util.List;


/**
 * 
 * 公告ListView
 * 
 * @author hongzhiliang
 *
 */
public class VoteListViewAdpter extends BaseAdapter {

	private List<VoteTaskInfo> voteInfoList;
	private LayoutInflater inflater;

	private Context mContext;

	public VoteListViewAdpter(Context context, List<VoteTaskInfo> noticeInfoList) {
		this.mContext = context;
		this.voteInfoList = noticeInfoList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return voteInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return voteInfoList.get(position);
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
			view = inflater.inflate(R.layout.list_item_msg_vote, null);
			holder.tvVoteTitle = (TextView) view.findViewById(R.id.tv_vote_title);
			holder.tvVoteTime = (TextView) view.findViewById(R.id.tv_vote_time);
			holder.tvVotePulisher = (TextView) view.findViewById(R.id.tv_vote_pulisher);
			holder.ivVoteType = (ImageView) view.findViewById(R.id.iv_vote_type);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}


        VoteTaskInfo voteInfo = voteInfoList.get(position);
        holder.tvVoteTitle.setText(voteInfo.getName());
        holder.tvVoteTime.setText(voteInfo.getCreateTimeStr());
        holder.tvVotePulisher.setText(voteInfo.getCreateBy());

		
		return view ;
	}
	
	public static class Holder{
		TextView tvVoteTitle;
		TextView tvVoteTime;
		TextView tvVotePulisher;
		ImageView ivVoteType;
	}


    public List<VoteTaskInfo> getVoteInfoList() {
        return voteInfoList;
    }

    public void setVoteInfoList(List<VoteTaskInfo> voteInfoList) {
        this.voteInfoList = voteInfoList;
    }
}

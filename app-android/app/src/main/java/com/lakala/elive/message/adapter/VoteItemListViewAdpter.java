package com.lakala.elive.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.VoteItemInfo;
import com.lakala.elive.beans.VoteTaskInfo;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * 商户日常维护列表
 * @author hongzhiliang
 *
 */
public class VoteItemListViewAdpter extends BaseAdapter{

	private List<VoteItemInfo> voteItemList = new ArrayList<VoteItemInfo>();
	private LayoutInflater inflater;
	private Context mContext;
    private VoteTaskInfo voteTaskInfo;
    private Session mSession;

	public VoteItemListViewAdpter(Context context, List<VoteItemInfo> voteItemList,VoteTaskInfo voteTaskInfo) {
		this.mContext = context;
        mSession = Session.get(mContext);
        if(voteItemList!=null){
            this.voteItemList = voteItemList;
        }
		inflater = LayoutInflater.from(context);

        this.voteTaskInfo = voteTaskInfo;
	}


    @Override
	public int getCount() {
		return voteItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return voteItemList.get(position);
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
			view = inflater.inflate(R.layout.list_item_vote_info, null);
            holder.cbVoteItem  = (CheckBox)view.findViewById(R.id.cb_vote_item);
            holder.rbVoteItem  = (RadioButton)view.findViewById(R.id.rb_vote_item);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
        //初始化条目信息
        final VoteItemInfo voteItemInfo = voteItemList.get(position);
        if(voteTaskInfo.getIsMulti()==0){
            holder.cbVoteItem.setVisibility(View.GONE);
            holder.rbVoteItem.setVisibility(View.VISIBLE);
            holder.rbVoteItem.setText(voteItemInfo.getItemName());

        }else{
            holder.cbVoteItem.setVisibility(View.VISIBLE);
            holder.rbVoteItem.setVisibility(View.GONE);
            holder.cbVoteItem.setText(voteItemInfo.getItemName());
            holder.cbVoteItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        voteItemInfo.setIsVote(1);
                    }else{
                        voteItemInfo.setIsVote(0);
                    }
                }
            });
        }


        return view ;
	}
	
	
	public static class Holder{
		CheckBox cbVoteItem;
        RadioButton rbVoteItem;
	}

    public List<VoteItemInfo> getVoteItemList() {
        return voteItemList;
    }

    public void setVoteItemList(List<VoteItemInfo> voteItemList) {
        this.voteItemList = voteItemList;
    }
}

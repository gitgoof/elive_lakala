package com.lakala.elive.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.TermInfo;

import java.util.List;


/**
 *
 * 商户日常维护列表
 * @author hongzhiliang
 *
 */
public class TermListViewAdpter extends BaseAdapter{

	private List<TermInfo> termInfoList;
	private LayoutInflater inflater;
	private Context mContext;

    private Session mSession;

	public TermListViewAdpter(Context context, List<TermInfo> merInfoList) {
		this.mContext = context;
        mSession = Session.get(mContext);
        if(merInfoList!=null){
            this.termInfoList = merInfoList;
        }
		inflater = LayoutInflater.from(context);
	}


    @Override
	public int getCount() {
		return termInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return termInfoList.get(position);
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
			view = inflater.inflate(R.layout.list_item_term_info, null);
            holder.tvProductClass = (TextView) view.findViewById(R.id.tv_product_class);
            holder.tvTermCode = (TextView) view.findViewById(R.id.tv_term_code);
            holder.tvDeviceSn = (TextView) view.findViewById(R.id.tv_device_sn);
            holder.tvOpenTime = (TextView) view.findViewById(R.id.tv_open_time);
            holder.tvTermStatus = (TextView) view.findViewById(R.id.tv_term_status);
            holder.tvTermCheckIcon = (TextView) view.findViewById(R.id.tv_term_check_icon);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
        //初始化条目信息
        TermInfo termInfo = termInfoList.get(position);

        holder.tvProductClass.setText(termInfo.getProductClass());

        if(mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS)!=null
                &&  mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS).get(termInfo.getProductClass())!=null){
            holder.tvProductClass.setText( mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS).get(termInfo.getProductClass()));
        }else{
            holder.tvProductClass.setText("未知");
        }

        holder.tvTermCode.setText(termInfo.getTerminalCode());
        holder.tvDeviceSn.setText(termInfo.getDeviceSn());

        if(termInfo.getCreateTimeStr()!=null){
            holder.tvOpenTime.setText(termInfo.getCreateTimeStr().substring(0,10));
        }

        String termStatus = "";
        if("VALID".equals(termInfo.getTermStatus())){
            termStatus = "有效";
        }else if("INVALID".equals(termInfo.getTermStatus())){
            termStatus = "无效";
        }else{
            termStatus = "未知";
        }
        holder.tvTermStatus.setText(termStatus);

        //终端检查状态
        if(termInfo.getCheckStatus()==null){
            holder.tvTermCheckIcon.setVisibility(View.GONE);
        }else{
            if(termInfo.getCheckStatus()){
                holder.tvTermCheckIcon.setText("已核查");
            }else{
                holder.tvTermCheckIcon.setText("待核查");
            }
        }


        return view ;
	}
	
	
	public static class Holder{
		TextView tvTermCode;
		TextView tvDeviceSn;
		TextView tvProductClass;
		TextView tvOpenTime;
        TextView tvTermStatus; //终端状态
        TextView tvTermCheckIcon; //终端核查状态
	}

    public List<TermInfo> getTermInfoList() {
        return termInfoList;
    }

    public void setTermInfoList(List<TermInfo> termInfoList) {
        this.termInfoList = termInfoList;
    }
}

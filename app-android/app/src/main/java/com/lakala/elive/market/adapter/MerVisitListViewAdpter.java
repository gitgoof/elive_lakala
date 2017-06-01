package com.lakala.elive.market.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.common.utils.DateUtil;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.common.utils.Utils;

import java.util.List;
import java.util.Map;


/**
 *
 * 工单处理
 * @author hongzhiliang
 *
 */
public class MerVisitListViewAdpter extends BaseAdapter{

	private List<ShopVisitInfo> visitInfoList;
	private LayoutInflater inflater;
	private Context mContext;
    private Session mSession;

    private Map<String,String> vistDictList;

	public MerVisitListViewAdpter(Context context, List<ShopVisitInfo> visitInfoList) {
		this.mContext = context;
		this.visitInfoList = visitInfoList;
		inflater = LayoutInflater.from(context);
        mSession = Session.get(mContext);
        vistDictList = mSession.getSysDictMap().get(Constants.VISIT_TYPE);
	}

	@Override
	public int getCount() {
		return visitInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return visitInfoList.get(position);
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

//			view = inflater.inflate(R.layout.list_item_mer_visit, null);
			view = inflater.inflate(R.layout.list_item_visit_time_list, null);
            holder.llTimeHeader = (LinearLayout) view.findViewById(R.id.ll_time_header);
            holder.tvVisitDate = (TextView) view.findViewById(R.id.tv_visit_date);
            holder.tvVisitWeek = (TextView) view.findViewById(R.id.tv_visit_week);
            holder.tvVisitTime = (TextView) view.findViewById(R.id.tv_visit_time);
            holder.tvVisitType = (TextView) view.findViewById(R.id.tv_visit_type);
            holder.tvVisitDesc = (TextView) view.findViewById(R.id.tv_visit_desc);

			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
        //初始化条目信息
        ShopVisitInfo visitInfo = visitInfoList.get(position);

        if(position == 0){
            holder.llTimeHeader.setVisibility(View.VISIBLE);
        }else{
            Log.e("position" ,":" + position);
            //初始化条目信息
            ShopVisitInfo preInfo = visitInfoList.get(position-1);
            if(preInfo.getVisitDateStr().equals(visitInfo.getVisitDateStr())){
                holder.llTimeHeader.setVisibility(View.GONE);
            }else{
                holder.llTimeHeader.setVisibility(View.VISIBLE);
            }
        }

        holder.tvVisitDate.setText(visitInfo.getVisitDateStr());
        holder.tvVisitWeek.setText(DateUtil.getDayOfWeekChinaName(visitInfo.getVisitDateStr()));
        holder.tvVisitTime.setText(visitInfo.getEndTimeStr().substring(10));
//        if(vistDictList != null)
//        holder.tvVisitType.setText(vistDictList.get(visitInfo.getVisitType()));
        if(StringUtil.isNotNullAndBlank(visitInfo.getVisitTypeName())){
            holder.tvVisitType.setText(visitInfo.getVisitTypeName());
        }else {
            holder.tvVisitType.setText("");
        }
        holder.tvVisitDesc.setText(visitInfo.getComments());
		return view ;
	}
	
	
	public static class Holder{
        LinearLayout llTimeHeader;
        TextView tvVisitDate;
        TextView tvVisitWeek;
        TextView tvVisitTime;
        TextView tvVisitType;
        TextView tvVisitDesc;
	}


    public List<ShopVisitInfo> getVisitInfoList() {
        return visitInfoList;
    }

    public void setVisitInfoList(List<ShopVisitInfo> visitInfoList) {
        this.visitInfoList = visitInfoList;
    }
}

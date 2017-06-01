package com.lakala.elive.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.DictDetailInfo;

import java.util.List;


/**
 *
 * 数据字典 ListView
 * @author hongzhiliang
 *
 */
public class DictDetailListAdpter extends BaseAdapter{

	private List<DictDetailInfo> dictDetailList;
	private LayoutInflater inflater;
	private Context mContext;


	public DictDetailListAdpter(Context context, List<DictDetailInfo> dictDetailList) {
		this.mContext = context;
		this.dictDetailList = dictDetailList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return dictDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		return dictDetailList.get(position);
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
			view = inflater.inflate(R.layout.item_dict_detail, null);
			holder.dataName = (TextView) view.findViewById(R.id.tv_dict_name);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
		holder.dataName.setText(dictDetailList.get(position).getDictName());
		return view ;
	}
	
	
	public static class Holder{
		TextView dataName;
	}


    public int getValueByKey(String key) {
        int ret = 0;
        for(int i = 0 ; i < dictDetailList.size() ; i++){
            if(dictDetailList.get(i).getDictKey().equals(key) ){
                ret = i;
                break;
            }
        }
        return ret;
    }
}

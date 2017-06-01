package com.lakala.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.ui.R;

import java.util.List;

/**
 * Created by LMQ on 2015/12/28.
 */
public class SingleChoiseListDialog extends AlertListDialog {
    private MyAdapter mAdapter;

    @Override
    View middleView() {
        if(middle != null){
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.ui_alert_list_dialog, null);
        middle_layout = (FrameLayout) middle_root.findViewById(R.id.middle_layout);
        alertList = (ListView) middle_root.findViewById(R.id.alert_list);
        if(listNames != null){
            mAdapter = new MyAdapter(listNames);
            alertList.setAdapter(mAdapter);
        }
        alertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapter == null){
                    return;
                }
                if(position == mAdapter.getSelectedPosition()){
                    return;
                }
                mAdapter.setSelected(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        return middle_root;
    }
    public int getSelectedPosition(){
        return mAdapter == null?0:mAdapter.getSelectedPosition();
    }

    private class MyAdapter extends BaseAdapter{
        private int selectedPosition = 0;
        private List<String> mData;

        public MyAdapter(List<String> mData) {
            this.mData = mData;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelected(int position){
            selectedPosition = position;
        }
        public int getSelectedPosition(){
            return selectedPosition;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                                                                          .my_simple_choise_list_item,null);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.icon);
                holder.text = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            String name = getItem(position);
            holder.text.setText(name);
            if(position == selectedPosition){
                holder.icon.setBackgroundResource(R.drawable.home_icon_xzan_sel);
            }else{
                holder.icon.setBackgroundResource(R.drawable.home_icon_xzan_nol);
            }
            return convertView;
        }
        private class ViewHolder{
            private ImageView icon;
            private TextView text;
        }
    }
}

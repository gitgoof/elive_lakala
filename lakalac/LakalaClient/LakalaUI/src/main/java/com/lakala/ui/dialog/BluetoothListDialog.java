package com.lakala.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.List;

/**
 * Created by linmq on 2016/6/24.
 */
public class BluetoothListDialog extends AlertListDialog {
    List<String> mData;
    private BluetoothListAdapter mAdapter;

    public BluetoothListDialog() {
        super();
        setDividerVisibility(View.VISIBLE);
    }

    public BluetoothListDialog setData(
            List<String> mData) {
        this.mData = mData;
        return this;
    }

    public int getSelectedPosition(){
        if(mAdapter != null){
            return mAdapter.getSelectedPosition();
        }
        return -1;
    }

    @Override
    View middleView() {

        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(com.lakala.ui.R.layout.ui_alert_list_dialog, null);
        middle_layout = (FrameLayout) middle_root.findViewById(com.lakala.ui.R.id.middle_layout);
        alertList = (ListView) middle_root.findViewById(com.lakala.ui.R.id.alert_list);
        if(mData != null){
            mAdapter = new BluetoothListAdapter(getActivity(),mData);
            alertList.setAdapter(mAdapter);
        }
        alertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        return middle_root;
    }
}

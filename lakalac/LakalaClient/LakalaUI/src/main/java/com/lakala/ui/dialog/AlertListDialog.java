package com.lakala.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.lakala.ui.R;

import java.util.List;

/**
 * Created by More on 15/3/6.
 */
public class AlertListDialog extends AlertDialog {

    protected ListView alertList;

    protected ArrayAdapter<String> adapter;

    protected List<String> listNames;

    protected ItemClickListener listItemCLickListener;

    public AlertListDialog(){
        super();
        setDividerVisibility(View.VISIBLE);
    };

    public interface ItemClickListener{
        void onItemClick(AlertDialog dialog, int index);
    }

    public ItemClickListener getListItemCLickListener() {
        return listItemCLickListener;
    }

    public void setListItemCLickListener(ItemClickListener listItemCLickListener) {
        this.listItemCLickListener = listItemCLickListener;
    }

    public List<String> getListNames() {
        return listNames;
    }

    public void setListNames(List<String> listNames) {
        this.listNames = listNames;
    }

    public ListView getAlertList() {
        return alertList;
    }

    public void setAlertList(ListView alertList) {
        this.alertList = alertList;
    }

    @Override
    View middleView() {

        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.ui_alert_list_dialog, null);
        middle_layout = (FrameLayout) middle_root.findViewById(R.id.middle_layout);
        alertList = (ListView) middle_root.findViewById(R.id.alert_list);
        if(listNames != null){
            adapter = new ArrayAdapter<String>(getActivity(),
                                               R.layout.my_simple_list_item,listNames);
//            {
//                @Override
//                public View getView(int position, View convertView, ViewGroup parent) {
//                    TextView textView = null;
//                    if(convertView == null){
//                        textView = new TextView(getActivity());
//                        convertView = textView;
//                        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,
//                                                               getActivity().getResources()
//                                                                       .getDisplayMetrics());
//                        textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup
//                                                                                      .LayoutParams.MATCH_PARENT,height));
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setTextSize(getResources().getDimension(R.dimen.font_size_small));
//                    }else{
//                        textView = (TextView)convertView;
//                    }
//                    textView.setText((CharSequence)getItem(position));
//                    textView.setTextColor(getResources().getColor(R.color.blue_00458A));
//                    return convertView;
//                }
//            };
            alertList.setAdapter(adapter);
        }
        alertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                if(listItemCLickListener != null){
                    listItemCLickListener.onItemClick(AlertListDialog.this, i);
                }
            }
        });

        return middle_root;

    }

    public void setItems(List<String> list, ItemClickListener itemClickListener){
        this.listNames = list;
        this.listItemCLickListener = itemClickListener;
    }
    public void setItems(List<String> list){
        this.listNames = list;
    }

    public static class ItemValue{
        public String code;
        public String name;
    }

}

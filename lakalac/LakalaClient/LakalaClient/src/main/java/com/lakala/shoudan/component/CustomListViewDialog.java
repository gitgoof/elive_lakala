package com.lakala.shoudan.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * Created by huangjp on 2016/3/16.
 */
public class CustomListViewDialog {
    private Context context;

    private String[] lists;
    private String title = "提示";

    private String confirmMsg = "确定";

    private String cancelMsg = "取消";

    private DialogInterface.OnClickListener positiveClickListener;

    private DialogInterface.OnClickListener negativeClickListener;

    private AdapterView.OnItemClickListener listviewItemClickListener;

    //    private int theme=0;
//    private boolean hastitle=false;//自定义view情况下，是否存在title，默认为false
    private boolean iscanable=true;
    private boolean isCanceledOnTouchOutside=true;

    public CustomListViewDialog(Context context) {
        this.context = context;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setCanable(boolean iscanable){
        this.iscanable=iscanable;
    }

    public void setCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.isCanceledOnTouchOutside=isCanceledOnTouchOutside;
    }

    public void setPositiveButton(DialogInterface.OnClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listviewItemClickListener, String[] lists) {
        this.listviewItemClickListener = listviewItemClickListener;
        this.lists=lists;
    }

    public void setNegativeButton(DialogInterface.OnClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public void setPositiveButton(String confirmMsg, DialogInterface.OnClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
        this.confirmMsg = confirmMsg;
    }

    public void setNegativeButton(String cancelMsg, DialogInterface.OnClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
        this.cancelMsg = cancelMsg;
    }
    Dialog mobileNoinputDialog;

    public void show(){
        View contentView;
        contentView = LayoutInflater.from(context).inflate(R.layout.common_dialog_listview,null);
        TextView titleTv = (TextView)contentView.findViewById(R.id.dialog_title);
        titleTv.setText(title);
        ListView listView=(ListView) contentView.findViewById(R.id.dialog_listview);
        listView.setBackgroundColor(Color.WHITE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.common_dialog_listview_item, lists);
        listView.setAdapter(arrayAdapter);
        if(listviewItemClickListener!=null){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    mobileNoinputDialog.dismiss();
                    listviewItemClickListener.onItemClick(parent, view, position, id);
                }
            });
        }
        mobileNoinputDialog = new Dialog(context);
        TextView cancel = (TextView)contentView.findViewById(R.id.dialog_cancel);
        TextView confirm = (TextView)contentView.findViewById(R.id.dialog_confirm);

        mobileNoinputDialog.setCancelable(iscanable);
        mobileNoinputDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        if(positiveClickListener != null){
            confirm.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mobileNoinputDialog.dismiss();
                    positiveClickListener.onClick(mobileNoinputDialog, 0);
                }
            });
        }else{
            confirm.setVisibility(View.GONE);
        }

        if(negativeClickListener != null){
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mobileNoinputDialog.dismiss();
                    negativeClickListener.onClick(mobileNoinputDialog, 1);
                }
            });
        }else{
            cancel.setVisibility(View.GONE);
        }
        mobileNoinputDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mobileNoinputDialog.setContentView(contentView);
        mobileNoinputDialog.setCancelable(false);
        mobileNoinputDialog.show();

    }

    public boolean isShowing(){
        if(mobileNoinputDialog == null){
            return false;
        }
        return mobileNoinputDialog.isShowing();
    }

    public void dismiss(){
        if(mobileNoinputDialog!=null && mobileNoinputDialog.isShowing()){
            mobileNoinputDialog.dismiss();
        }
    }

    public void cancel(){
        if(mobileNoinputDialog!=null && mobileNoinputDialog.isShowing()){
            mobileNoinputDialog.cancel();
        }
    }
}

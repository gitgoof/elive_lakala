package com.lakala.shoudan.component;

import android.content.Context;
import android.view.View;

import com.lakala.shoudan.R;
import com.lakala.ui.module.IPhoneStylePopupWindow;

/**
 * Created by More on 15/12/2.
 */
public class CallerCameraPopWindow {

    private IPhoneStylePopupWindow iPhoneStylePopupWindow;

    private Context context;

    private IPhoneStylePopupWindow.ItemClickListener itemClickListener;

    public IPhoneStylePopupWindow.ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(IPhoneStylePopupWindow.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CallerCameraPopWindow(Context context, IPhoneStylePopupWindow.ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public void show(View parent){
        getCallCameraPopWindow().showPop(parent);
    }

    private IPhoneStylePopupWindow getCallCameraPopWindow(){

        if(iPhoneStylePopupWindow == null){

            iPhoneStylePopupWindow = new IPhoneStylePopupWindow(context, new String[]{context.getString(R.string.photo_album), context.getString(R.string.take_photo)});
            iPhoneStylePopupWindow.setItemClickListener(itemClickListener);

        }

        return iPhoneStylePopupWindow;

    }

    public void dismiss(){
        iPhoneStylePopupWindow.dismiss();
    }
}

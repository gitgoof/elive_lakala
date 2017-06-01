package com.lakala.ui.dialog.mts;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by andy_lv on 13-12-20.
 *
 *  加载框dialog，用于数据请求。
 */
public class ProgressDialog extends BaseDialog {
   //根 rootView
    private View rootView;
    //加载dialog的文字信息
    private TextView dialog_message_textView;

    //加载框提示文字
    private String loading_message;

    private DialogInterface.OnDismissListener onDismissListener;

    private OnDialogKeyListener onDialogKeyListener;

    /**
     * 对话框弹出时，按键回调
     */
    public interface OnDialogKeyListener {
        public void onDialogKeyevent(int keyCode, KeyEvent keyEvent);
    }

    /**
     * 构造方法
     */
    public ProgressDialog(){
      super();
    }


    /**
     * 默认构造器
     */
    public ProgressDialog(FragmentManager fragmentManager) {
        super(fragmentManager);
        setStyle(DialogFragment.STYLE_NO_FRAME,0);
        setCancelable(false);
    }

    /**
     * 设置 ProgressDialog 的标题样式，及主题样式
     * @param style  style的值只能是 DialogFragement.STYLE_NORMAL,
     *               DialogFragement.STYLE_NO_FRAME,DialogFragement.STYLE_NO_INPUT,DialogFragement.STYLE_NO_TITLE
     * @param theme  样式id
     */
    public ProgressDialog(FragmentManager fragmentManager,int style,int theme){
        super(fragmentManager,style, theme);
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if(onDialogKeyListener != null){
                    onDialogKeyListener.onDialogKeyevent(keyCode, keyEvent);
                }
                return false;
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         if(null == rootView){
             rootView = inflater.inflate(R.layout.ui_progress_dialog_layout,container,false);
             initView();
         }else{
             ((ViewGroup)rootView.getParent()).removeView(rootView);
         }
         return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener != null){
            onDismissListener.onDismiss(dialog);
        }
    }

    /**
     * 初始化布局
     *
     */
    private void initView(){
        dialog_message_textView = (TextView)rootView.findViewById(R.id.progress_dialog_layout_message);
        if(!isTitleNull(loading_message)){
            dialog_message_textView.setText(loading_message);
        }
    }

    /**
     * 设置加载框的文字提示
     * @param message
     */
    public void setMessageText(String message){
        this.loading_message = message;
       if(null != dialog_message_textView && !isTitleNull(loading_message)){
           dialog_message_textView.setText(loading_message);
       }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
    }

    public void setOnDialogKeyListener(OnDialogKeyListener onDialogKeyListener) {
        this.onDialogKeyListener = onDialogKeyListener;
    }
}

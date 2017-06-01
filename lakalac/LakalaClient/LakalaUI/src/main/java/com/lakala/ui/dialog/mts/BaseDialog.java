package com.lakala.ui.dialog.mts;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * Created by andy_lv on 13-12-20.
 * <p/>
 * 基础dialog类，定义dialog的基本类，包括dialog的背景，显示方式.
 */
public class BaseDialog extends DialogFragment {
    //FragmentManager
    FragmentManager fragmentManager;
    DialogInterface.OnCancelListener cancelListener;

    //设置是否可以自动取消对话框，
    //App升级对话框弹出后，被其他请求结束后自动取消掉，解决此类问题
    private boolean isCouldAutoDismiss = true;

    /**
     * 构造方法
     */
    public BaseDialog(){
        super();
    }

    /**
     * 构造方法
     */
    public BaseDialog(FragmentManager fragmentManager){
       this.fragmentManager = fragmentManager;
       setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    /**
     * 构造方法
     * @param style  style的值只能是 DialogFragement.STYLE_NORMAL,
     *               DialogFragement.STYLE_NO_FRAME,DialogFragement.STYLE_NO_INPUT,DialogFragement.STYLE_NO_TITLE
     * @param theme  样式id
     */
    public BaseDialog(FragmentManager fragmentManager,int style,int theme){
        this.fragmentManager = fragmentManager;
        setStyle(style, theme);
    }

    public boolean isCouldAutoDismiss() {
        return isCouldAutoDismiss;
    }

    public void setCouldAutoDismiss(boolean isCouldAutoDismiss) {
        this.isCouldAutoDismiss = isCouldAutoDismiss;
    }

    /**
     * 显示dialog,使用FragmentTransaction 控制提交
     */
    public void show(){
       try{
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(this,"");
        fragmentTransaction.commitAllowingStateLoss();
       }catch (Exception e){

       }
    }

    /**
     * 设置是否取消
     * @param isCancle
     */
     public void setCancelable(boolean isCancle){
      super.setCancelable(isCancle);
    }

    /**
     * 设置点击dialog外部区域是否让其消失
     * @param canceledOnTouchOutside
     */
    protected void setCanceledOnTouchOutside(boolean canceledOnTouchOutside){
        if(getDialog() != null){
            getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        }
    }

    /**
     * 设置Dialog Cancel Listener
     * @param cancelListener  回调接口
     */
    public void setCancelListener(DialogInterface.OnCancelListener cancelListener){
       if(getDialog() != null){
           getDialog().setOnCancelListener(cancelListener);
       }
       this.cancelListener = cancelListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try{
        super.onActivityCreated(savedInstanceState);
        }catch (Exception e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCancelListener(cancelListener);
    }


    /**
     *  判断title 是否为空
     * @param title  title
     * @return
     */
    protected boolean  isTitleNull(String title){
        return (title ==null || title.equals("") || title.equals("null"));
    }
}

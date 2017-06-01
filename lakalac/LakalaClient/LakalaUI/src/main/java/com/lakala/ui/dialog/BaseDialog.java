package com.lakala.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.library.util.LogUtil;
import com.lakala.ui.R;

/**
 * Created by wangchao on 14/11/29.
 */
public class BaseDialog extends DialogFragment {
    //Base dialog tag
    public static final String BASE_DIALOG = "base_dialog";
    //根视图
    private View rootView;
    //代理
    BaseDialogDelegate dialogDelegate;
    private FragmentActivity context;

    public FragmentActivity getContext() {
        return context;
    }

    public BaseDialog setContext(FragmentActivity context) {
        this.context = context;
        return this;
    }

    public BaseDialog(){
        super();
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(style(), theme());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnKeyListener(
                new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return dialogDelegate != null && dialogDelegate
                                .onKeyEvent(dialog, keyCode, event);
                    }
                }
        );
        dialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (dialogDelegate != null) {
                            dialogDelegate.onShow(dialog);
                        }
                    }
                }
        );
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.ui_base_dialog, container, false);
            if (customView() != null){
                if(((ViewGroup)rootView).getChildCount() != 0){
                    ((ViewGroup)rootView).removeAllViews();
                }
                ((ViewGroup)rootView).addView(customView());
            }
        }else {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (dialogDelegate != null){
            dialogDelegate.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * dialog style
     *
     * @return  style
     */
    int style() {
        return STYLE_NO_TITLE;
    }

    /**
     * dialog theme
     *
     * @return  theme
     */
    int theme() {
        return 0;
    }

    /**
     * custom view
     *
     * @return           view
     */
    View customView(){
        return null;
    }

    /**
     * show dialog
     *
     * @param manager   FragmentManager
     * @return          me
     */
    public BaseDialog show(final FragmentManager manager){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        //FragmentTransaction transaction = manager.beginTransaction();
                        //show(transaction, BASE_DIALOG);
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.add(BaseDialog.this, BASE_DIALOG);
                        ft.commitAllowingStateLoss();
                    }
                }
        );
        return this;
    }
    public BaseDialog show(){
        if(context != null && !context.isFinishing()){
            return show(context.getSupportFragmentManager());
        }else{
            return null;
        }
    }

    public boolean isShowing(){
        Dialog dialog = getDialog();
        return dialog == null?false:dialog.isShowing();
    }

    /**
     * set dialog delegate
     *
     * @param dialogDelegate    BaseDialogDelegate
     * @return                  me
     */
    public BaseDialog setDialogDelegate(BaseDialogDelegate dialogDelegate){
        this.dialogDelegate = dialogDelegate;
        return this;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener != null){
            onDismissListener.onDismiss(dialog);
        }
    }
    private DialogInterface.OnDismissListener onDismissListener;

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    //Base Dialog Delegate
    public static class BaseDialogDelegate {

        /**
         * Called when a key is dispatched to a dialog. This allows listeners to
         * get a chance to respond before the dialog.
         *
         * @param dialog The dialog the key has been dispatched to.
         * @param keyCode The code for the physical key that was pressed
         * @param keyEvent The KeyEvent object containing full information about
         *            the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        public boolean onKeyEvent(DialogInterface dialog, int keyCode, KeyEvent keyEvent){
            if(dialog instanceof BaseDialog && keyCode == KeyEvent.KEYCODE_BACK){
                BaseDialog baseDialog = (BaseDialog)dialog;
                if(baseDialog.isCancelable()){
                    baseDialog.dismiss();
                }
            }
            return false;
        }

        /**
         * This method will be invoked when the dialog is shown.
         *
         * @param dialog The dialog that was shown will be passed into the
         *               method.
         */
        public void onShow(DialogInterface dialog) {}

        /**
         * This method will be invoked when the dialog is destroy
         */
        public void onDestroy (){}
    }
}

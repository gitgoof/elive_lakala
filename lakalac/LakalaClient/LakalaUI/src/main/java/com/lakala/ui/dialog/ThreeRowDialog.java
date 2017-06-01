package com.lakala.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lakala.ui.R;

/**
 * Created by wangchao on 14/11/30.
 */
public class ThreeRowDialog extends BaseDialog {

    private View me;

    private FrameLayout top,
                        middle,
                        bottom;
    private int topVisibility = -1;

    public void setTopVisibility(int topVisibility) {
        this.topVisibility = topVisibility;
        if(top != null){
            top.setVisibility(topVisibility);
        }
    }

    @Override
    View customView() {

        if (me != null){
            return me;
        }
        me      = View.inflate(getActivity(), R.layout.ui_threerow_dialog, null);

        top     = (FrameLayout) me.findViewById(R.id.top_container);
        middle  = (FrameLayout) me.findViewById(R.id.middle_container);
        bottom  = (FrameLayout) me.findViewById(R.id.bottom_container);

        if (background() != 0){
            me.setBackgroundResource(background());
        }

        if (topView() != null){
            top.setVisibility(View.VISIBLE);
            if (top.getChildCount() != 0){
                top.removeAllViews();
            }
            top.addView(topView());
        } else {
            top.setVisibility(View.VISIBLE);
        }

        if(topVisibility != -1){
            top.setVisibility(topVisibility);
        }

        if (middleView() != null){
            middle.setVisibility(View.VISIBLE);
            if (middle.getChildCount() != 0){
                middle.removeAllViews();
            }
            middle.addView(middleView());
        } else {
            middle.setVisibility(View.GONE);
        }

        if (bottomView() != null){
            bottom.setVisibility(View.VISIBLE);
            if (bottom.getChildCount() != 0){
                bottom.removeAllViews();
            }
            bottom.addView(bottomView());
            bottom.addView(divider());
        } else {
            bottom.setVisibility(View.GONE);
        }

        return me;
    }
    private View divider(){
        View view = new View(getActivity());
        view.setBackgroundColor(getResources().getColor(R.color.color_blue_00458a));
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),getTheme()){
            @Override
            public void setContentView(View view) {
                double width = getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.95;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int)width,
                                                                           ViewGroup.LayoutParams
                                                                                   .WRAP_CONTENT);
                super.setContentView(view,params);
            }
        };
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return dialogDelegate != null && dialogDelegate.onKeyEvent(dialog, keyCode, event);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (dialogDelegate != null){
                    dialogDelegate.onShow(dialog);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogDelegate != null && dialogDelegate instanceof ThreeRowDialogDelegate){
                    ((ThreeRowDialogDelegate) dialogDelegate).onTopClick(v);
                }
            }
        });

        middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogDelegate != null && dialogDelegate instanceof ThreeRowDialogDelegate){
                    ((ThreeRowDialogDelegate) dialogDelegate).onMiddleClick(v);
                }
            }
        });

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogDelegate != null && dialogDelegate instanceof ThreeRowDialogDelegate){
                    ((ThreeRowDialogDelegate) dialogDelegate).onBottomClick(v);
                }
            }
        });
    }

    /**
     * background
     *
     * @return  image id
     */
    int background(){
        return 0;
    }

    /**
     * top view
     *
     * @return view
     */
    View topView(){
        return null;
    }

    /**
     * middle view
     *
     * @return view
     */
    View middleView(){
        return null;
    }

    /**
     * bottom view
     *
     * @return view
     */
    View bottomView(){
        return null;
    }

    //Three row dialog delegate
    public static class ThreeRowDialogDelegate extends BaseDialogDelegate {

        /**
         * This method will be invoked when the top view clicked
         *
         * @param view click view
         */
        public void onTopClick(View view){}

        /**
         * This method will be invoked when the middle view clicked
         *
         * @param view click view
         */
        public void onMiddleClick(View view){}

        /**
         * This method will be invoked when the bottom view clicked
         *
         * @param view click view
         */
        public void onBottomClick(View view){}
    }
}

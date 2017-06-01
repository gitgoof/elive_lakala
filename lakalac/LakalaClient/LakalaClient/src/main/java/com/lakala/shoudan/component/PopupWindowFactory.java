package com.lakala.shoudan.component;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lakala.shoudan.R;
import com.lakala.ui.component.BubbleTextView;

/**
 * Created by ZhangMY on 2015/1/27.
 * PopupWindow 工厂类
 */
public class PopupWindowFactory {

    /**
     * 创建editText 右侧问号所需的pop提示
     *
     * @param view
     * @param context
     * @param msg
     */
    public static PopupWindow createEditPopupWindowTips(final ImageView view, int width, final Context context, String msg) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.layout_edit_popupwindow_tips, null);
        final BubbleTextView tvEditTips = (BubbleTextView) rootView.findViewById(R.id.tv_edit_popupwindow_tips);
        tvEditTips.setText(msg);
        final int[] location = new int[2];
        view.getLocationOnScreen(location);

        final PopupWindow mPopupWindow = new PopupWindow(rootView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent));
        mPopupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, -400);

        tvEditTips.setTag(true);
        tvEditTips.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if ((Boolean) tvEditTips.getTag()) {
                    tvEditTips.setTag(false);
                    final int mPopupWindowHeight = rootView.getHeight();
                    mPopupWindow.dismiss();
                    mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            view.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_help_login));
                        }
                    });
                    mPopupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, location[1] - mPopupWindowHeight);
                    view.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_help_login_on));
                }
            }
        });
        return mPopupWindow;
    }


}

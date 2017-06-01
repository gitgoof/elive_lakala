package com.lakala.ui.dialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by wangchao on 14/11/30.
 */
public class AlertDialog extends ThreeRowDialog {


    protected View top,
            middle,
            bottom;
    protected FrameLayout middle_layout;//中间布局
    protected View middle_root;//中间布局根布局
    protected ViewGroup.LayoutParams middleParams;//中间的布局参数
    protected ImageView imageView;
    protected TextView titleView,
            messageView;
    protected Button[] buttons;

    protected String title,
            message;
    protected int topIcon;
    protected String[] buttonsText;
    protected View divider;
    private int dividerVisibility = View.GONE;


    private boolean versionMode;

    public AlertDialog setDividerVisibility(int dividerVisibility) {
        this.dividerVisibility = dividerVisibility;
        return this;
    }

    public String getMessage() {
        if (messageView != null) {
            return messageView.getText().toString();
        }
        return message;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;
    }

    @Override
    View topView() {
        if (TextUtils.isEmpty(title) && topIcon == 0) {
            return null;
        }
        if (top != null) {
            return top;
        }
        top = View.inflate(getActivity(), R.layout.ui_alert_top_dialog, null);
        divider = top.findViewById(R.id.divider);
        divider.setVisibility(dividerVisibility);

        imageView = (ImageView) top.findViewById(R.id.dialog_top_image);
        titleView = (TextView) top.findViewById(R.id.dialog_top_text);
        if (versionMode) {
            titleView.setTextSize(22);
            titleView.setTextColor(getResources().getColor(R.color.black));
        }

        if (topIcon != 0) {
            imageView.setBackgroundResource(topIcon);
            imageView.setVisibility(View.VISIBLE);
        }

        if (title != null && !title.equals("")) {
            titleView.setText(title);
        }
        return top;
    }

    @Override
    int theme() {
        return R.style.dialog_normal;
    }

    @Override
    View middleView() {
        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (versionMode)
            middle_root = inflater.inflate(R.layout.ui_alert_middle_dialog_version, null);
        else
            middle_root = inflater.inflate(R.layout.ui_alert_middle_dialog, null);
        middle_layout = (FrameLayout) middle_root.findViewById(R.id.middle_layout);
        messageView = (TextView) middle_root.findViewById(R.id.dialog_middle_text);
        if (linkMark != null) {
            messageView.setAutoLinkMask(linkMark);
        }
        if (message != null && !message.equals("")) {
            messageView.setText(message);
        }
        return middle_root;
    }

    private Integer linkMark;

    public void setMessageAutoLinkMask(int mark) {
        linkMark = mark;
    }

    @Override
    View bottomView() {
        if (bottom != null) {
            return bottom;
        }
        bottom = View.inflate(getActivity(), R.layout.ui_alert_bottom_dialog, null);
        if (buttons != null && buttons.length != 0) {
            if (((ViewGroup) bottom).getChildCount() == buttons.length) {
                return bottom;
            }
            int btnLength = buttons.length;
            if (btnLength == 1) {
                View btn = getOnlyOneButton(buttonsText[0], 0);
                addButton2Bottom(btn);
                return bottom;
            }
            for (int i = 0; i < btnLength; i++) {
                if (i > 0) {
                    View divider = new View(getActivity());
                    divider.setBackgroundColor(getResources().getColor(R.color.color_blue_00458a));
                    ((ViewGroup) bottom).addView(divider);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    divider.setLayoutParams(params);
                }
                View btn = null;
                if (i == 0) {
                    btn = getLeftButton(buttonsText[i], i);
                } else if (i == btnLength - 1) {
                    btn = getRightButton(buttonsText[i], i);
                } else {
                    btn = getButton(buttonsText[i], i);
                }
                addButton2Bottom(btn);
            }
        }
        return bottom;
    }

    public void setMode(boolean mode) {
        this.versionMode = mode;
    }

    private void addButton2Bottom(View btn) {
        ((ViewGroup) bottom).addView(btn);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn.getLayoutParams();
        params.width = 0;
        params.weight = 1;
        btn.setLayoutParams(params);
    }

    private View getOnlyOneButton(String text, int index) {
        View button = getButton(text, index);
        button.setBackgroundResource(R.drawable.ui_selector_dialog_common_onebtn);
        return button;
    }

    private View getRightButton(String text, int index) {
        View button = getButton(text, index);
        button.setBackgroundResource(R.drawable.ui_selector_dialog_common_rightbtn);
        return button;
    }

    private View getLeftButton(String text, final int index) {
        View button = getButton(text, index);
        button.setBackgroundResource(R.drawable.ui_selector_dialog_common_leftbtn);
        return button;
    }

    /**
     * get button with text
     *
     * @param text  button text
     * @param index index
     * @return me
     */
    private View getButton(String text, final int index) {
        View view = View.inflate(getActivity(), R.layout.ui_alert_bottom_button_dialog, null);

        final AlertDialog dialog = this;
        buttons[index] = (Button) view.findViewById(R.id.dialog_bottom_button);
        buttons[index].setText(text);
        buttons[index].setTag(text);
        if (versionMode) {
            buttons[index].setTextSize(20);
        }
        buttons[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogDelegate != null && dialogDelegate instanceof AlertDialogDelegate) {
                    ((AlertDialogDelegate) dialogDelegate).onButtonClick(dialog, v, index);
                }
            }
        });
        return view;
    }

    /**
     * set title
     *
     * @param title title
     * @return me
     */
    public AlertDialog setTitle(String title) {
        if (titleView != null) {
            titleView.setText(title);
        } else {
            this.title = title;
        }
        return this;
    }

    /**
     * set icon
     *
     * @param resourceId id
     * @return me
     */
    public AlertDialog setIcon(int resourceId) {
        if (imageView != null) {
            imageView.setBackgroundResource(resourceId);
        } else {
            this.topIcon = resourceId;
        }
        return this;
    }

    /**
     * set middle message
     *
     * @param message message
     * @return me
     */
    public AlertDialog setMessage(String message) {
        if (messageView != null) {
            messageView.setText(message);
        } else {
            this.message = message;
        }
        return this;
    }

    public void setMessageViewSize() {
        if (messageView != null) {
            messageView.setTextSize(getResources().getDimension(R.dimen.font_size_normal));
        }
    }

    /**
     * set buttons
     *
     * @param buttonsText button array
     * @return me
     */
    public AlertDialog setButtons(String[] buttonsText) {
        this.buttonsText = buttonsText;
        this.buttons = new Button[buttonsText.length];
        return this;
    }

    /**
     * 获取 Button
     *
     * @param index Button index
     * @return View
     */
    public Button getButton(int index) {
        if (index < buttons.length && index > -1) {
            return buttons[index];
        }
        return null;
    }

    /**
     * 设置中间的布局
     *
     * @param view
     * @return
     */
    public AlertDialog setMiddleView(View view) {
        this.middle = view;
        this.middleParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (null != middle_root) {
            removeMiddleView();
            initMiddleView();
        }
        return this;
    }

    /**
     * 设置AlertDialog中间view
     *
     * @param view
     */
    public void setMiddleView(View view, ViewGroup.LayoutParams params) {
        this.middle = view;
        this.middleParams = params;
        if (null != middle_root) {
            removeMiddleView();
            initMiddleView();
        }
    }

    /**
     * 初始化中间布局
     */
    public void initMiddleView() {
        if (middle != null) {
            removeMiddleView();
            middle_layout.addView(middle, middleParams);
            if (title == null || title.equals("") || title.equals("null")) {
                middle_layout.setPadding(5, (int) this.getActivity().getResources().getDimension(R.dimen.dimen_20), 5, 0);
            }
            middle_layout.bringChildToFront(middle);
            middle_layout.postInvalidate();

        }
    }

    /**
     * 删除中间的布局
     */
    public void removeMiddleView() {
        if (null != middle_root && middle_layout.getChildCount() > 1) {
            if (null != middle.getParent()) {
                ((ViewGroup) middle.getParent()).removeView(middle);
            }
            middle_layout.removeView(middle);
        }
    }

    /**
     * 设置索引为 index 的 button 的文本
     *
     * @param text  文本
     * @param index 索引
     * @return me
     */
    public AlertDialog setButtonText(String text, int index) {
        if (buttons != null && buttons.length > index) {
            buttons[index].setText(text);
        } else if (dialogDelegate != null && dialogDelegate instanceof AlertDialogDelegate) {
            ((AlertDialogDelegate) dialogDelegate).onError(index + "", "您设置的按钮文本，索引越界!");
        }
        return this;
    }

    //alert dialog delegate
    public static class AlertDialogDelegate extends ThreeRowDialogDelegate {
        /**
         * button click
         *
         * @param dialog dialog
         * @param view   button
         * @param index  index
         */
        public void onButtonClick(AlertDialog dialog, View view, int index) {
        }

        /**
         * 发生异常时候会调用这个方法
         *
         * @param id   异常 id
         * @param data 异常数据
         */
        public void onError(String id, Object data) {
        }
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }
}

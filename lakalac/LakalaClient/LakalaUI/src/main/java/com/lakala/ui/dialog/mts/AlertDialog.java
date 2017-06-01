package com.lakala.ui.dialog.mts;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by andy_lv on 13-12-20.
 * <p/>
 * 警告dialog
 */
public class AlertDialog extends BaseDialog {
    //Dialog的根view对象
    private View rootView;
    //标题图标
    private ImageView titleIconImageview;
    //标题文字
    private TextView titleTextview;
    //正文内容
    private TextView messageTextview;
    //AlertDialog中所有button
    private Button leftButton, rightButton, middleButton;
    //AlertDialog中所有button的RelativeLayout
    private ViewGroup left_button_lienar, right_button_linear, middle_button_linear;
    //AlertDialog中顶端title布局
    private LinearLayout title_linear;
    //AlertDialog中的底部button布局
    private LinearLayout bottom_linear;
    //AlertDialog中间布局
    private FrameLayout middle_layout;
    //Buidler
    private Builder builder;
    //中间view
    private View middleView;
    //中间View 的布局参数
    private ViewGroup.LayoutParams middleParams;
    //中间布局横向分割线
    private View middleLayoutDividerView;
    //button布局中间分割线
    private View middleDividerLine;
    //左边分割线
    private View leftDividerLine;
    //顶部分割线
    private View topDividerLine;

    //Dialog标题
    private static String title;
    private static int titleIconId = 0;
    //Dialog Message;
    private static String message;
    //3个button的文字 序号0数据表示左边button的text,序号1数据表示中间的button的text,序号2数据表示右边button的text
    private static String[] buttonTextString = new String[3];
    //设置某个button不可点击状态, 序号0数据表示左边button,序号1数据表示中间的button,序号2数据表示右边button
    private static boolean[] discardButtonType = new boolean[]{true, true, true};
    //记录3个button的显示状态,序号0数据表示左边button,序号1数据表示中间的button,序号2数据表示右边button
    private static int[] buttonVisiblity = new int[]{-1, -1, -1};

    /**
     * 添加空的构造方法
     * 尝试解决crash #263的问题 com.lakala.ui.a.a; no empty constructor
     */
    public AlertDialog() {
        super();
    }

    /**
     * 默认构造器
     */
    public AlertDialog(FragmentManager fragmentManager) {
        super(fragmentManager, DialogFragment.STYLE_NO_TITLE, R.style.dialog_normal);
    }

    /**
     * 设置AlertDialog的标题样式，及主题样式
     *
     * @param style style的值只能是 DialogFragement.STYLE_NORMAL,
     *              DialogFragement.STYLE_NO_FRAME,DialogFragement.STYLE_NO_INPUT,DialogFragement.STYLE_NO_TITLE
     * @param theme 样式id
     */
    public AlertDialog(FragmentManager fragmentManager, int style, int theme) {
        super(fragmentManager, style, theme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //红米手机上，弹出Dialog时，容易报空指针异常，v4包的bug，故在这里try catch
        try {
            super.onActivityCreated(savedInstanceState);
        } catch (Exception e) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.ui_alert_dialog_layout, container, false);
            initView();
            useBuilder();
            initMiddleView();
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        return rootView;
    }


    /**
     * 初始化UI
     */
    private void initView() {
        titleIconImageview = (ImageView) rootView.findViewById(R.id.alert_dialog_title_imageview);
        titleTextview = (TextView) rootView.findViewById(R.id.altert_dialog_title_textview);
        messageTextview = (TextView) rootView.findViewById(R.id.alert_dialog_message_textview);
        leftButton = (Button) rootView.findViewById(R.id.alert_dialog_left_button);
        rightButton = (Button) rootView.findViewById(R.id.alert_dialog_right_button);
        middleButton = (Button) rootView.findViewById(R.id.alert_dialog_middle_button);
        leftButton.getPaint().setFakeBoldText(true);
        rightButton.getPaint().setFakeBoldText(true);
        middleButton.getPaint().setFakeBoldText(true);

        topDividerLine = (View) rootView.findViewById(R.id.ui_alert_dialog_top_title_divider);
        middleDividerLine = (View) rootView.findViewById(R.id.alert_dialog_middle_divider_line);
        leftDividerLine = (View) rootView.findViewById(R.id.alert_dialog_left_divider_view);

        middleLayoutDividerView = (View) rootView.findViewById(R.id.alter_dialog_bottom_horizontal_line);
        middle_layout = (FrameLayout) rootView.findViewById(R.id.alert_dialog_middle_layout);

        //底部button布局Relativelayout
        left_button_lienar = (ViewGroup) rootView.findViewById(R.id.alter_dialog_left_button_linear);
        middle_button_linear = (ViewGroup) rootView.findViewById(R.id.alert_dialog_middle_button_linear);
        right_button_linear = (ViewGroup) rootView.findViewById(R.id.alert_dialog_right_button_linear);

        //顶部title条的布局
        title_linear = (LinearLayout) rootView.findViewById(R.id.alert_dialog_title_linear);

        //底部button布局
        bottom_linear = (LinearLayout) rootView.findViewById(R.id.alert_dialog_bottom_linear);


        //判断当所显示的message文字为一行时，居中显示；多行时，左对齐
        final ViewTreeObserver viewTreeObserver = messageTextview.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                messageTextview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = messageTextview.getWidth();

                DisplayMetrics dm = messageTextview.getResources().getDisplayMetrics();
                int textSize = (int) (15 * dm.scaledDensity);

                if (width - textSize * messageTextview.getText().length() - messageTextview.getPaddingLeft() * 2 > 5) {
                    messageTextview.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    messageTextview.setGravity(Gravity.LEFT);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //设置对话框显示时候的位置默认居中
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.y = 0;
            getDialog().getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onPause() {
        if (getDialog() != null) {
            //设置当安全键盘显示时候，对话框上移
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenHeight = dm.heightPixels;
            lp.y = lp.y - screenHeight / 10;
            getDialog().getWindow().setAttributes(lp);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //触发对话框销毁代理
        if (null != builder && builder.getAlertDialogClickListener() != null) {
            builder.getAlertDialogClickListener().onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 调用此方法之前，需调用Builder类相关方法，待设置完全以后，
     * 才可以调用此方法，否则不起作用。
     *
     * @param builder
     */
    public void setBuilder(final Builder builder) {
        this.builder = builder;
        if (null != rootView) {
            useBuilder();
        }
    }

    /**
     * 设置AlertDialog中间view
     *
     * @param view
     */
    public void setMiddleView(View view) {
        this.middleView = view;
        this.middleParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (null != rootView) {
            initMiddleView();
        }
    }

    /**
     * 设置AlertDialog中间view
     *
     * @param view
     */
    public void setMiddleView(View view, ViewGroup.LayoutParams params) {
        this.middleView = view;
        this.middleParams = params;
        if (null != rootView) {
            removeMiddleView();
            initMiddleView();
        }
    }

    /**
     * remove通过setMiddleView（）设置的view
     */
    private void removeMiddleView() {
        if (null != rootView && middle_layout.getChildCount() > 1) {
            if (null != middleView.getParent()) {
                ((ViewGroup) middleView.getParent()).removeView(middleView);
            }
            middle_layout.removeView(middleView);
        }
    }

    /**
     * 使用builder;
     */
    private void useBuilder() {
        //设置AlertDialog顶部icon
        if (titleIconId == 0) {
            titleIconImageview.setVisibility(View.GONE);
        } else {
            titleIconImageview.setImageResource(builder.getTitleIconId());
        }

        //设置AlertDialog顶部标题
        if (!isTitleNull(title)) {
            titleTextview.setText(title);
            title_linear.setVisibility(View.VISIBLE);
        } else {
            title_linear.setVisibility(View.GONE);
            topDividerLine.setVisibility(View.GONE);
        }

        //设置AlertDialog正文文本
        if (!TextUtils.isEmpty(message)) {
            setMessage(message);
            messageTextview.setVisibility(View.VISIBLE);
        } else {
            messageTextview.setVisibility(View.GONE);
        }

        int visibleButtonCount = 3;

        //设置AlertDialog左边button文本标签，如果文本内容为null,则隐藏左边button
        if (!TextUtils.isEmpty(buttonTextString[0])) {
            if (buttonVisiblity[0] != -1) {
                if (buttonVisiblity[0] == View.GONE) {
                    left_button_lienar.setVisibility(View.GONE);
                    visibleButtonCount--;
                } else if (buttonVisiblity[0] == View.INVISIBLE) {
                    leftButton.setVisibility(View.INVISIBLE);
                } else {
                    leftButton.setVisibility(View.VISIBLE);
                }
            }
            leftButton.setText(buttonTextString[0]);
        } else {
            left_button_lienar.setVisibility(View.GONE);
            visibleButtonCount--;
        }

        //设置AlertDialog右边button文本标签，如果文本内容为null,则隐藏右边button
        if (!TextUtils.isEmpty(buttonTextString[1])) {
            if (buttonVisiblity[1] != -1) {
                if (buttonVisiblity[1] == View.GONE) {
                    middle_button_linear.setVisibility(View.GONE);
                    visibleButtonCount--;
                } else if (buttonVisiblity[1] == View.INVISIBLE) {
                    middleButton.setVisibility(View.INVISIBLE);
                } else {
                    middleButton.setVisibility(View.VISIBLE);
                }
            }
            middleButton.setText(buttonTextString[1]);
        } else {
            middle_button_linear.setVisibility(View.GONE);
            visibleButtonCount--;
        }

        //设置AlertDialog中间button文本标签，如果文本内容为null,则隐藏中间button
        if (!TextUtils.isEmpty(buttonTextString[2])) {
            if (buttonVisiblity[2] != -1) {
                if (buttonVisiblity[2] == View.GONE) {
                    right_button_linear.setVisibility(View.GONE);
                    visibleButtonCount--;
                } else if (buttonVisiblity[2] == View.INVISIBLE) {
                    rightButton.setVisibility(View.INVISIBLE);
                } else {
                    rightButton.setVisibility(View.VISIBLE);
                }
            }
            rightButton.setText(buttonTextString[2]);
        } else {
            right_button_linear.setVisibility(View.GONE);
            visibleButtonCount--;
        }
        computeButtonVisiblity(visibleButtonCount);
        //设置button的不可点击状态,和显示状态
        for (int i = 0; i < 3; i++) {
            Builder.ButtonTypeEnum buttonTypeEnum = null;
            switch (i) {
                case 0:
                    buttonTypeEnum = Builder.ButtonTypeEnum.LEFT_BUTTON;
                    break;
                case 1:
                    buttonTypeEnum = Builder.ButtonTypeEnum.MIDDLE_BUTTON;
                    break;
                case 2:
                    buttonTypeEnum = Builder.ButtonTypeEnum.RIGHT_BUTTON;
                    break;
            }
            setButtonEnable(buttonTypeEnum, discardButtonType[i]);
        }

        final int count = visibleButtonCount;
        //设置左边button点击事件回调
        if (bottom_linear.getVisibility() != View.GONE) {
            if (leftButton.getVisibility() == View.VISIBLE) {

                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (count == 1) {
                            AlertDialog.this.dismiss();
                        }
                        if (builder != null && null != builder.getAlertDialogClickListener()) {
                            builder.getAlertDialogClickListener().clickCallBack(Builder.ButtonTypeEnum.LEFT_BUTTON, AlertDialog.this);
                        }
                    }
                });
            }
            if (rightButton.getVisibility() == View.VISIBLE) {
                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (count == 1) {
                            AlertDialog.this.dismiss();
                        }
                        if (builder != null && null != builder.getAlertDialogClickListener()) {
                            builder.getAlertDialogClickListener().clickCallBack(Builder.ButtonTypeEnum.RIGHT_BUTTON, AlertDialog.this);
                        }
                    }
                });
            }
            if (middleButton.getVisibility() == View.VISIBLE) {
                middleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (count == 1) {
                            AlertDialog.this.dismiss();
                        }
                        if (builder != null && null != builder.getAlertDialogClickListener()) {
                            builder.getAlertDialogClickListener().clickCallBack(Builder.ButtonTypeEnum.MIDDLE_BUTTON, AlertDialog.this);
                        }
                    }
                });
            }
        }
    }

    /**
     * button显示个数
     *
     * @param visibleButtonCount
     */
    private void computeButtonVisiblity(final int visibleButtonCount) {
        switch (visibleButtonCount) {
            case 0:
                bottom_linear.setVisibility(View.GONE);
                middleLayoutDividerView.setVisibility(View.GONE);
                break;
            case 1:
                if (left_button_lienar.getVisibility() == View.VISIBLE) {
                    leftButton.setBackgroundResource(R.drawable.ui_dialog_single_button_bg_drawable);
                    leftDividerLine.setVisibility(View.INVISIBLE);
                } else if (middle_button_linear.getVisibility() == View.VISIBLE) {
                    middleButton.setBackgroundResource(R.drawable.ui_dialog_single_button_bg_drawable);
                    middleDividerLine.setVisibility(View.INVISIBLE);
                } else if (right_button_linear.getVisibility() == View.VISIBLE) {
                    rightButton.setBackgroundResource(R.drawable.ui_dialog_single_button_bg_drawable);
                }
                middleLayoutDividerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (left_button_lienar.getVisibility() == View.VISIBLE &&
                        middle_button_linear.getVisibility() == View.VISIBLE) {
                    leftButton.setBackgroundResource(R.drawable.ui_dialog_left_button_bg_drawable);
                    middleButton.setBackgroundResource(R.drawable.ui_dialog_right_button_bg_drawable);
                    middleDividerLine.setVisibility(View.INVISIBLE);
                    leftDividerLine.setVisibility(View.VISIBLE);
                } else if (middle_button_linear.getVisibility() == View.VISIBLE
                        && right_button_linear.getVisibility() == View.VISIBLE) {
                    middleButton.setBackgroundResource(R.drawable.ui_dialog_left_button_bg_drawable);
                    rightButton.setBackgroundResource(R.drawable.ui_dialog_right_button_bg_drawable);
                    middleDividerLine.setVisibility(View.VISIBLE);
                } else if (left_button_lienar.getVisibility() == View.VISIBLE
                        && right_button_linear.getVisibility() == View.VISIBLE) {
                    leftButton.setBackgroundResource(R.drawable.ui_dialog_left_button_bg_drawable);
                    rightButton.setBackgroundResource(R.drawable.ui_dialog_right_button_bg_drawable);
                    leftDividerLine.setVisibility(View.VISIBLE);
                }
                middleLayoutDividerView.setVisibility(View.VISIBLE);
                break;
            case 3:
                left_button_lienar.setVisibility(View.VISIBLE);
                middle_button_linear.setVisibility(View.VISIBLE);
                right_button_linear.setVisibility(View.VISIBLE);
                leftDividerLine.setVisibility(View.VISIBLE);
                middleDividerLine.setVisibility(View.VISIBLE);
                middleLayoutDividerView.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 初始化AlertDialog中间view
     */
    private void initMiddleView() {
        if (middleView != null) {
            removeMiddleView();
            middle_layout.addView(middleView, middleParams);
            if (title == null || title.equals("") || title.equals("null")) {
                middle_layout.setPadding(5, (int) this.getActivity().getResources().getDimension(R.dimen.dimen_20), 5, 0);
            }
            middle_layout.bringChildToFront(middleView);
            middle_layout.postInvalidate();
        }
    }

    /**
     * 设置Dialog标题
     *
     * @param title 标题文本
     */
    public void setTitle(String title) {
        this.title = title;
        if (null != titleTextview && !isTitleNull(title)) {
            titleTextview.setText(title);
        }
    }

    /**
     * 设置Dialog标题
     *
     * @param titleId Dialog标题id
     */
    public void setTitle(final int titleId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    title = getActivity().getResources().getString(titleId);
                }
                if (null != titleTextview && !isTitleNull(title)) {
                    titleTextview.setText(title);
                }
            }
        }, 200);
    }


    /**
     * 设置AlertDialog中某个button呈不可点状态
     *
     * @param typeEnum
     */
    public void setButtonEnable(Builder.ButtonTypeEnum typeEnum, boolean isEnable) {
        Button button = null;
        switch (typeEnum) {
            case LEFT_BUTTON:
                button = leftButton;
                discardButtonType[0] = isEnable;
                break;
            case MIDDLE_BUTTON:
                button = middleButton;
                discardButtonType[1] = isEnable;
                break;
            case RIGHT_BUTTON:
                button = rightButton;
                discardButtonType[2] = isEnable;
                break;
        }
        if (null != button) {
            button.setEnabled(isEnable);
            button.setClickable(isEnable);
        }
    }

    /**
     * 设置button的显示状态
     *
     * @param typeEnum
     * @param visibility
     */
    public void setButtonVisiblity(Builder.ButtonTypeEnum typeEnum, int visibility) {
        Button button = null;
        ViewGroup viewGroup = null;
        switch (typeEnum) {
            case LEFT_BUTTON:
                button = leftButton;
                buttonVisiblity[0] = visibility;
                viewGroup = left_button_lienar;

                break;
            case MIDDLE_BUTTON:
                button = middleButton;
                buttonVisiblity[1] = visibility;
                viewGroup = middle_button_linear;
                break;
            case RIGHT_BUTTON:
                button = rightButton;
                buttonVisiblity[2] = visibility;
                viewGroup = right_button_linear;
                break;
        }
        if (null != button) {
            button.setVisibility(visibility);
            if (null != viewGroup) {
                if (visibility == View.GONE) {
                    viewGroup.setVisibility(View.GONE);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                }
            }
            int visibleCount = 3;
            if (left_button_lienar.getVisibility() == View.GONE) {
                visibleCount--;
            }
            if (middle_button_linear.getVisibility() == View.GONE) {
                visibleCount--;
            }
            if (right_button_linear.getVisibility() == View.GONE) {
                visibleCount--;
            }
            computeButtonVisiblity(visibleCount);
            rootView.postInvalidate();
        }
    }


    /**
     * 设置button的文本
     *
     * @param buttonTypeEnum button类别
     * @param buttonText     button文字
     */
    public void setButtonText(Builder.ButtonTypeEnum buttonTypeEnum, String buttonText) {
        Button button = null;
        int index = -1;
        switch (buttonTypeEnum) {
            case LEFT_BUTTON:
                button = leftButton;
                index = 0;
                break;
            case MIDDLE_BUTTON:
                button = middleButton;
                index = 1;
                break;
            case RIGHT_BUTTON:
                button = rightButton;
                index = 2;
                break;
        }
        buttonTextString[index] = buttonText;
        if (null != button) {
            button.setText(buttonText);
        }
    }

    /**
     * 设置AlertDialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
        if (null != messageTextview) {
            message = toDBCString(message);
            //简单 判断字符串中是否含有html标签，如果有字符串需要格式化html格式
            if (message.matches("\\S*<[^>]*>[^<]*</[^>]*>\\S*")) {
                messageTextview.setText(Html.fromHtml(message));
            } else {
                messageTextview.setText(message);
            }
        }
    }

    /**
     * 含半角数字字符串转换为含全角数字字符串
     * (主要处理数字自动换行错位问题)
     *
     * @param input
     * @return
     */
    public static String toSBCCase(String input) {
        input = input.replaceAll("0", "０")
                .replaceAll("1", "１")
                .replaceAll("2", "２")
                .replaceAll("3", "３")
                .replaceAll("4", "４")
                .replaceAll("5", "５")
                .replaceAll("6", "６")
                .replaceAll("7", "７")
                .replaceAll("8", "８")
                .replaceAll("9", "９");
        return input;
    }

    /**
     * 所有的数字、字母及标点全部转为全角字符
     *
     * @param input
     * @return
     */
    public static String toDBCString(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 得到button
     *
     * @param buttonTypeEnum
     * @return
     */
    public Button getButton(Builder.ButtonTypeEnum buttonTypeEnum) {
        Button button = null;
        switch (buttonTypeEnum) {
            case LEFT_BUTTON:
                button = leftButton;
                break;
            case MIDDLE_BUTTON:
                button = middleButton;
                break;
            case RIGHT_BUTTON:
                button = rightButton;
                break;
        }
        return button;
    }


    /**
     * 设置标题图标id
     *
     * @param titleIconId 图标id
     */
    public void setTitleIconId(int titleIconId) {
        this.titleIconId = titleIconId;
    }

    /**
     * AlertDialog 构建类
     */
    public static class Builder {

        public static enum ButtonTypeEnum {
            LEFT_BUTTON,
            RIGHT_BUTTON,
            MIDDLE_BUTTON
        }

        //button点击事件回调
        private AlertDialogClickListener alertDialogClickListener;

        /**
         * button 点击事件回调
         */
        public static class AlertDialogClickListener {

            //在此方法内，实现相应的事件逻辑
            public void clickCallBack(ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
            }

            public void onDestroy() {
            }

            ;
        }

        /**
         * 默认构造器
         */
        public Builder() {

        }

        /**
         * 默认设置标题的构造器
         *
         * @param titleText 标题
         */
        public Builder(String titleText) {
            title = titleText;
        }

        /**
         * 默认设置标题，和Message内容的构造器
         *
         * @param titleText
         * @param messageText
         */
        public Builder(String titleText, String messageText) {
            title = titleText;
            message = messageText;
        }

        /**
         * 设置所有button点击事件回调
         */
        public void setButtonClickListener(AlertDialogClickListener alertDialogClickListener) {
            this.alertDialogClickListener = alertDialogClickListener;
        }

        /**
         * 设置标题图标id
         *
         * @param titleIcon 图标id
         */
        public void setTitleIconId(int titleIcon) {
            titleIconId = titleIcon;
        }

        /**
         * 设置正文内容
         *
         * @param messageText
         */
        public void setMessage(String messageText) {
            message = messageText;
        }

        /**
         * 设置左边button的文字内容
         *
         * @param leftButtonText button文字内容
         */
        public void setLeftButtonText(String leftButtonText) {
            buttonTextString[0] = leftButtonText;
        }

        /**
         * 设置右边button文字内容
         *
         * @param rightButtonText
         */
        public void setRightButtonText(String rightButtonText) {
            buttonTextString[2] = rightButtonText;
        }

        /**
         * 设置中间button文字内容
         *
         * @param middleButtonText
         */
        public void setMiddleButtonText(String middleButtonText) {
            buttonTextString[1] = middleButtonText;
        }

        /**
         * 得到标题图标id
         *
         * @return 图标id
         */
        public int getTitleIconId() {
            return titleIconId;
        }

        /**
         * 得到AlertDialog标题文本
         *
         * @return 标题文本
         */
        public String getTitle() {
            return title;
        }

        /**
         * 得到AlertDialog正文内容
         *
         * @return 正文文本内容
         */
        public String getMessage() {
            return message;
        }

        /**
         * 得到AlertDialog左边button文本标签
         *
         * @return 左边button文本标签
         */
        public String getLeftButtonText() {
            return buttonTextString[0];
        }

        /**
         * 得到AlertDialog右边button文本标签
         *
         * @return 右边button文本标签
         */
        public String getRightButtonText() {
            return buttonTextString[2];
        }

        /**
         * 得到AlertDialog中间button文本标签
         *
         * @return 中间button文本标签
         */
        public String getMiddleButtonText() {
            return buttonTextString[0];
        }

        /**
         * 得到AlertDialog button点击事件回调
         *
         * @return button点击事件回调
         */
        public AlertDialogClickListener getAlertDialogClickListener() {
            return alertDialogClickListener;
        }
    }
}

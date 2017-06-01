package com.lakala.elive.common.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.elive.R;


/**
 * Title: CustomDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * 作者：huangc on 2015/9/8 14:23
 * <p>
 * 邮箱：huangc@Bluemobi.cn
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    int layoutRes;// 布局文件
    Context context;
    /**
     * 确定按钮
     **/
    private Button confirmBtn;
    /**
     * 取消按钮
     **/
    private Button cancelBtn;
    /**
     * 对话框标题
     **/
    private TextView titletv;

    private String title, confirmTitle, cancelTitle;
    private TextView hint_tv;//提示标题
    boolean isShowHint=false;//是否显示提示标题

    public CustomDialog(Context context) {
        super(context);
        this.context = context;

    }

    /**
     * 自定义布局的构造方法
     *
     * @param context
     * @param resLayout
     */
    public CustomDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;

    }

    /**
     * 自定义主题及布局的构造方法
     *
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomDialog(Context context, int theme, int resLayout, String title,
                        String confirmTitle, String cancelTitle, boolean isShowHint) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.title = title;
        this.confirmTitle = confirmTitle;
        this.cancelTitle = cancelTitle;
        this.isShowHint=isShowHint;
    }
    public CustomDialog(Context context, int theme, int resLayout, String title,
                        String confirmTitle, String cancelTitle) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.title = title;
        this.confirmTitle = confirmTitle;
        this.cancelTitle = cancelTitle;
    }
    public CustomDialog(Context context, int theme, int resLayout, String title,
                        String cancelTitle) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.title = title;
        this.cancelTitle = cancelTitle;
    }
    public CustomDialog(Context context, int theme, int resLayout, String title
    ) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.title = title;
    }

    /**
     * 自定义主题及布局的构造方法
     *
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);

        // 根据id在布局中找到控件对象
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        titletv = (TextView) findViewById(R.id.title_tv);



        //设置按钮的文字
        titletv.setText(title);
        confirmBtn.setText(confirmTitle);
        cancelBtn.setText(cancelTitle);
//        if (isShowHint){
//            hint_tv = (TextView) findViewById(R.id.tv_hint);
//            hint_tv.setVisibility(View.VISIBLE);
//        }else {
//            hint_tv = (TextView) findViewById(R.id.tv_hint);
//            hint_tv.setVisibility(View.GONE);
//        }
        // 设置按钮的文本颜色
        //confirmBtn.setTextColor(0xff1E90FF);
        //cancelBtn.setTextColor(0xff1E90FF);

        // 为按钮绑定点击事件监听器
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                this.dialogListener.confirmOnclick();
                break;
            case R.id.cancel_btn:
                this.dialogListener.cancelOnclick();
                break;
        }
    }

    public DialogListener dialogListener;

    public void setBtnListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }


    public interface DialogListener {

        public void confirmOnclick();//用户点击了确定按钮

        public void cancelOnclick();//用户点击了取消按钮
    }

}


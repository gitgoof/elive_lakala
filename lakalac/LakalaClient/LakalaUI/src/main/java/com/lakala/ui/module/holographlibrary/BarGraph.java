package com.lakala.ui.module.holographlibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

import java.util.ArrayList;

/**
 * 绘制柱状图
 * <p/>
 * Created by jerry on 14-2-8.
 */
public class BarGraph extends LinearLayout {

    private Context mContext;
    private LinearLayout mRootLayout;
    private boolean mIsShowBarText = true;
    private int mScreenHeight;
    private int mdp10;
    private TextView mTitleText;
    private TextView mTitleRightText;

    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mdp10 = Dimension.dip2px(10, mContext);
        initUI(context);
    }

    private void initUI(Context context) {
        mScreenHeight = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();

        RelativeLayout mParentRootLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.ui_bar_graph, null);
        mTitleText = (TextView) mParentRootLayout.findViewById(R.id.ui_id_bar_graph_title_center);
        mTitleRightText = (TextView) mParentRootLayout.findViewById(R.id.ui_id_bar_graph_title_right);
        mRootLayout = (LinearLayout) mParentRootLayout.findViewById(R.id.ui_id_bar_graph_content_view);
        addView(mParentRootLayout);

    }

    public void setTitle(int title){
        mTitleText.setText(title);
    }

    public void setTitle(CharSequence title){
        mTitleText.setText(title);
    }

    public void setRightTitle(int title){
        mTitleRightText.setText(title);
    }

    public void setRightTitle(CharSequence title){
        mTitleRightText.setText(title);
    }

    public void setShowBarText(boolean isShowBarText) {
        this.mIsShowBarText = isShowBarText;
    }

    public void setBars(final ArrayList<Bar> bars) {
        //View绘制完成监听
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mRootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                // get width and height of the view
                if (mRootLayout.getHeight() != 0){
                    setBarsView(bars);
                }
            }
        });
    }

    private void setBarsView(ArrayList<Bar> bars) {

        if (bars == null || bars.size() == 0) return;

        final float maxValue = getMax(bars);

        for (int i = 0; i < bars.size(); i++) {
            LinearLayout layout =
                    (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ui_rect_item_view, mRootLayout, false);
            final TextView topText = (TextView) layout.findViewById(R.id.ui_id_item_top_text);
            final View rectView = layout.findViewById(R.id.ui_id_item_center_rect);
            final TextView bottomText = (TextView) layout.findViewById(R.id.ui_id_item_bottom_text);

            final Bar bar = bars.get(i);
            if (mIsShowBarText){
                topText.setText(String.valueOf(bar.getValue()));
            }
            bottomText.setText(bar.getName());
            rectView.setBackgroundColor(bar.getColor());
            //计算柱状图的高度
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    Dimension.dip2px(20, mContext),
                    (int) (bar.getValue() / maxValue *
                            (mRootLayout.getHeight() -
                                    Dimension.dip2px(mScreenHeight * 40 / mScreenHeight,mContext))));
            params.setMargins(mdp10, 0, mdp10, 0);
            rectView.setLayoutParams(params);

            mRootLayout.addView(layout);
        }

    }

    /**
     * 获取最大数
     *
     * @param values 一组数据
     * @return
     */
    private float getMax(ArrayList<Bar> values) {
        float tmp = Float.MIN_VALUE;

        if (null != values) {
            tmp = values.get(0).getValue();
            for (int i = 0; i < values.size(); i++) {
                if (tmp < values.get(i).getValue()) {
                    tmp = values.get(i).getValue();
                }
            }
        }

        return tmp;
    }

}

package com.lakala.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/12/14.
 */
public class SpreadMenuView extends RelativeLayout {

    public SpreadMenuView(Context context) {
        super(context);
    }

    public SpreadMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpreadMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    /**
     * 菜单组成
     */
    public static class MenuItem {

        /**
         * 图片int id
         */
        private int drawable;

        /**
         * 文字描述
         */
        private String text;

        public MenuItem(int drawable, String text) {
            this.drawable = drawable;
            this.text = text;
        }

        public int getDrawable() {
            return drawable;
        }

        public void setDrawable(int drawable) {
            this.drawable = drawable;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private BaseAdapter spreadMenuAdapter;

    public void setSpreadMenuAdapter(BaseAdapter spreadMenuAdapter){

        this.spreadMenuAdapter = spreadMenuAdapter;

    }

    /**
     * 添加菜单
     * @param items
     * @param mainButtonDrawableId
     */
    public void setMenu(List<MenuItem> items, int mainButtonDrawableId) {

        totalMenuItemCount = items.size();

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                parentHeight = getMeasuredHeight();
                parentWidth = getMeasuredWidth();

            }
        });


        textViewList = new ArrayList<TextView>();


        for (int i = 0; i < items.size(); i++) {

            MenuItem menuItem = items.get(i);
            final TextView textView = new TextView(getContext());
            textView.setTag(i);
            textView.setText(menuItem.getText());
            textView.setOnClickListener(itemClickListener);
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, menuItem.getDrawable(), 0, 0);
            addView(textView, getMenuItemLayoutParams());

            textViewList.add(textView);
            if (i == 0) {
                textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        sonHeight = textView.getMeasuredHeight();
                        sonWidth = textView.getMeasuredWidth();
                        print("height = " + sonHeight);
                        print("sonWidth = " + sonWidth);

                    }
                });
            }
        }

        final ImageView imageView = new ImageView(getContext());
        imageView.setTag(-1);
        imageView.setImageDrawable(getResources().getDrawable(mainButtonDrawableId));
        imageView.setOnClickListener(itemClickListener);
        addView(imageView, getMenuItemLayoutParams());



    }

    private void print(String msg){

        LogUtil.e(getClass().getName(), "Msg = " + msg);

    }

    private RelativeLayout.LayoutParams menuItemLayoutParams;

    private RelativeLayout.LayoutParams getMenuItemLayoutParams() {

        if (menuItemLayoutParams == null) {
            menuItemLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            menuItemLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            menuItemLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        }

        return menuItemLayoutParams;

    }


    private List<TextView> textViewList;

    private boolean spread = true;

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int clickIndex = (Integer) view.getTag();

            if (clickIndex < 0) {
                if (spread) {
                    spread = false;
                    spread();

                } else {
                    spread = true;
                    gather();
                }
            } else {
                dispatchItemClick(clickIndex, view);
            }

        }
    };


    private final int MAX_ICON_IN_LINE = 4;

    private int totalMenuItemCount = 0;

    /**
     * 展开
     */
    private void spread() {

        int totalSize = totalMenuItemCount;

        int lineCount = totalSize / MAX_ICON_IN_LINE;

        if (totalSize % MAX_ICON_IN_LINE != 0) {
            lineCount++;
        }
        int lineSpace = (int) (0.73 * sonWidth);
        int blankHeight = parentHeight - lineCount * sonHeight - lineSpace * lineCount - 40;
        int iconSpace = (parentWidth - MAX_ICON_IN_LINE * sonWidth) / (MAX_ICON_IN_LINE + 1);


        print("lineSpace = " + lineSpace);
        print("blankHeight = " + blankHeight);
        print("iconSpace = " + iconSpace);
        print("lineCount = " + lineCount);

        TextView textView = textViewList.get(0);

        originalX = textView.getLeft();
        originalY = textView.getTop();

        for (int index = 0; index < textViewList.size(); index++) {

            int lineIndex = (index) / MAX_ICON_IN_LINE;
            int row = index % MAX_ICON_IN_LINE + 1;

            int x = row * iconSpace + (row - 1) * sonWidth;
            int y = blankHeight + (sonHeight + iconSpace) * lineIndex;

            ViewPropertyAnimator.animate(textViewList.get(index)).x(x).y(y).setDuration(382);

        }

    }


    private int originalX = 0;
    private int originalY = 0;


    /**
     * 聚拢
     */
    private void gather() {
        for (int index = 0; index < textViewList.size(); index++) {

            ViewPropertyAnimator.animate(textViewList.get(index)).x(originalX).y(originalY).setDuration(382);

        }
    }


    private int parentHeight;
    private int parentWidth;
    private int sonHeight;
    private int sonWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public interface MenuItemClickListener {

        void onItemClick(int index, View view);

    }

    private MenuItemClickListener menuItemClickListener;

    private void dispatchItemClick(int index, View view) {

        if (menuItemClickListener != null) {
            menuItemClickListener.onItemClick(index, view);
        }

    }

    public MenuItemClickListener getMenuItemClickListener() {
        return menuItemClickListener;
    }

    /**
     * 设置菜单点击事件监听
     * @param menuItemClickListener
     */
    public void setMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }


}

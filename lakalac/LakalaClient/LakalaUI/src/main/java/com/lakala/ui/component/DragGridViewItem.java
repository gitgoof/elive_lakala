package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

public class DragGridViewItem extends FrameLayout implements View.OnClickListener {
    //是否充许删除，如不充许则始终不显示删除图标
    private boolean isAllowDelete = true;
    //是否充许抖动，如不充许则始终不会抖动
    private boolean isAllowShake = true;
    //是否充许重排该项目的位置。
    private boolean isAllowRearrange = true;
    //“删除”图标
    private ImageView deleteIcon;
    //删除侦听器
    private OnDeleteClickListener deleteListener;

    public DragGridViewItem(Context context) {
        super(context);
        init(null);
    }

    public DragGridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DragGridViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    public void onClick(View view) {
        if (view == deleteIcon){
            //单击了删除图标
            if (deleteListener != null)
                deleteListener.onDeleteClick(this);
        }
    }

    /**
     * 是否充许删除，如不充许则始终不显示删除图标
     * @return  充许删除返回 true。
     */
    public boolean isAllowDelete() {
        return isAllowDelete;
    }

    /**
     * 设置是否充许删除，如不充许则始终不显示删除图标
     * @param allowDelete true 充许删除。
     */
    public void setAllowDelete(boolean allowDelete) {
        isAllowDelete = allowDelete;
    }

    /**
     * 是否充许抖动，如不充许则始终不会抖动
     * @return 充许抖动返回 true。
     */
    public boolean isAllowShake() {
        return isAllowShake;
    }

    /**
     * 设置是否充许抖动，如不充许则始终不会抖动
     * @param allowShake true 充许抖动。
     */
    public void setAllowShake(boolean allowShake) {
        isAllowShake = allowShake;
    }

    /**
     * 是否充许重排该项目的位置。
     * @return 充许返回 true
     */
    public boolean isAllowRearrange() {
        return isAllowRearrange;
    }

    /**
     * 是否充许重排该项目的位置。
     * @param allowRearrange  充许为 true
     */
    public void setAllowRearrange(boolean allowRearrange) {
        isAllowRearrange = allowRearrange;
    }

    /**
     * 显示删除图标，如果设置为不充许删除则不显示图标。
     * 点击删除图标将触发 {@link OnDeleteClickListener#onDeleteClick} 事件
     * @see DragGridViewItem#setAllowDelete(boolean)
     */
    public void showDeleteIcon(){
        if (!isAllowDelete)
            return;

        //如果删除图标不是在最上层，则将它移到最上层。
        if (indexOfChild(deleteIcon) < getChildCount() - 1){
            removeView(deleteIcon);
            addView(deleteIcon);
        }
        deleteIcon.setVisibility(VISIBLE);
    }

    /**
     * 隐藏删除图标
     */
    public void hideDeleteIcon(){
        deleteIcon.setVisibility(INVISIBLE);
    }

    /**
     * 开始抖动
     */
    public void startShake(){
        if (!isAllowShake)
            return;

        //暂未实现，预留方法。
    }

    /**
     * 结束抖动
     */
    public void stopShake(){
        //暂未实现，预留方法。
    }

    /**
     * 设置删除单击事件侦听器
     * @param deleteListener
     */
    public void setDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    private void init(AttributeSet attrs){
        int dp5 = Dimension.dip2px(5,getContext());

        //初始化左上角删除图标
        deleteIcon = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        deleteIcon.setLayoutParams(params);
        deleteIcon.setOnClickListener(this);
        deleteIcon.setImageResource(R.drawable.l_delete);
        deleteIcon.setPadding(0,0,dp5,dp5);
        deleteIcon.setVisibility(INVISIBLE);

        addView(deleteIcon);

        if (attrs == null)
            return;

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DragGridViewItem);

        boolean bValue;

        //属性 isAllowDelete
        bValue = ta.getBoolean(R.styleable.DragGridViewItem_isAllowDelete, true);
        setAllowDelete(bValue);

        //属性 isAllowShake
        bValue = ta.getBoolean(R.styleable.DragGridViewItem_isAllowShake, true);
        setAllowShake(bValue);

        ta.recycle();
    }

    public interface OnDeleteClickListener {
        public void onDeleteClick(DragGridViewItem item);
    }
}

package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lakala.ui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DraggableGridView extends ViewGroup implements
        View.OnTouchListener, View.OnClickListener, View.OnLongClickListener,
        DragGridViewItem.OnDeleteClickListener,Animation.AnimationListener,Handler.Callback {

    private final static int MSG_DELETE_ITEM = 100;

    //是否充许进入编辑模式
    private boolean isLongClickEditMode = false;
    //是否充过量卷动
    private boolean isOverreach = true;
    //列数
    private int numColumns = -1;
    //水平间距
    private int horizontalSpacing = 0;
    //垂直间距
    private int verticalSpacing   = 0;
    //列宽
    private int columnWidth  = 0;
    //行高，由用户设置得来
    private int rowHeight = 0;
    //单元格宽,由 onMeasure 事件计算得出。
    private int childWidth = 0;
    //单元格高,由 onMeasure 事件计算得出。
    private int childHeight = 0;
    //列数，由 onMeasure 事件计算得出。
    private int columnCount = 0;
    //用于执行周步延时任务
    private Handler handler = new Handler(this);
    //拖拽的项目的索引, -1表示没有托拽任何项目。
    private int dragged = -1;
    //向上卷动的距离，可改变scroll 的值请使用set、inc、dec 方法。
    private int scroll = 0;
    //水平方向两端的空白宽度,由 onMeasure 事件计算得出。
    private int horizontalMargin = 0;
    //卷动的位移量，用松手后的减速卷动。
    private float lastDelta = 0;
    //上一个 Touch 事件的坐标
    private int lastX = -1, lastY = -1;
    //最后一个拖放的目标位置，-1表示没有。
    private int lastTarget = -1;
    //是否处理长按、单击事件。
    private boolean enabled  = true;
    //手指托动中
    private boolean touching = false;
    //是否是编辑模式
    private boolean isEditMode = false;
    //是否充许重排
    private boolean isAllowRearrange = true;
    //动画时长
    private static int animT = 200;
    //用于在托拽过程中保存子视图的新位置
    private ArrayList<Integer> newPositions = new ArrayList<Integer>();
    //当前Grid的宽度、高度，由 onMeasure 计算得出。
    private int layoutWidth = 0, layoutHeight = 0;
    //是否更新布局，当发生卷动时会设置此值。
    private boolean updateLayout = false;
    //是否拖拽并卷动
    private boolean isDragAndScroll = false;
    //将要删除的视图列表：<子视图,是否已经从布局中移除>
    private Map<View,Boolean> deletedViews= new HashMap<View,Boolean>();

    //listeners
    private OnRearrangeListener onRearrangeListener;
    private OnEditModeListener  onEditModeListener;
    private OnClickListener secondaryOnClickListener;
    private OnItemClickListener onItemClickListener;

    /**
     * 允许外部设置onTouchListener
     */
    private OnTouchListener externalTouchListener;

    public void setExternalTouchListener(OnTouchListener externalTouchListener) {
        this.externalTouchListener = externalTouchListener;
    }

    //在空闲时执行卷动任务
    private Runnable updateTask = new Runnable() {
        public void run()
        {
            //清除拖拽并卷动标志
            isDragAndScroll = false;

            if (dragged != -1)
            {
                if (lastY < childHeight / 2 && scroll > 0){
                    //当有拖动对象时并且上一次Touch 事件的坐标小于半个单元格，则向下卷动ViewGroup内容。
                    decScroll(20);
                    //拖拽并卷动标志置位，该状态下不进行重排。
                    isDragAndScroll = true;
                }
                else if (lastY > getBottom() - getTop() - childHeight / 2 && scroll < getMaxScroll()){
                    //当有拖动对象时并且上一次Touch 事件的坐标`位于 ViewGroup底部半个单元格内时，则向上卷动ViewGroup内容。
                    incScroll(20);
                    //拖拽并卷动标志置位，该状态下不进行重排。
                    isDragAndScroll = true;
                }
            }
            else if (lastDelta != 0 && !touching)
            {
                //手指抬起之后继续卷动内容，但距离递减直到为0，为的是造成一个减速效果。
                incScroll((int)lastDelta);
                lastDelta *= .9;

                if (Math.abs(lastDelta) < .25)
                    lastDelta = 0;

                updateLayout = true;
            }

            //松手后的回弹或惯性卷动效果
            clampScroll(0);

            if  (updateLayout){
                onLayout(false, getLeft(), getTop(), getRight(), getBottom());
            }

            //再次在延时执行 updateTask
            handler.postDelayed(this, 25);
        }
    };

    public DraggableGridView(Context context){
        super(context);
        init(null);
    }

    public DraggableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case MSG_DELETE_ITEM:
            {
                View v = (View)message.obj;

                //删除子视图
                int index = indexOfChild(v);
                if (index > -1){
                    newPositions.remove(index);
                    super.removeView(v);
                }

                //删除完成，从deletedViews 列表中移除子视图
                deletedViews.remove(v);

                break;
            }
        }

        return false;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
    	secondaryOnClickListener = listener;
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        if (index <0)
            newPositions.add(-1);
        else
            newPositions.add(index,-1);

        if (child instanceof DragGridViewItem){
            ((DragGridViewItem)child).setDeleteClickListener(this);
        }
    }

    @Override
    public void removeView(View view) {
        animateDelete(view);
    }

    @Override
    public void removeViewAt(int index) {
        View view = getChildAt(index);
        removeView(view);
    }

    //LAYOUT
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        updateLayout = false;

        int count = getChildCount();
        for (int i = 0; i < count; i++){
        	if (i != dragged){
	            Point xy = getPointFromIndex(i);
                childLayout(i,xy.x, xy.y, xy.x + childWidth, xy.y + childHeight);
        	}
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        boolean weightWarpContent = false;
        boolean heightWarpContent = false;
        LayoutParams lp = getLayoutParams();
        if (lp != null){
            heightWarpContent = (lp.height == LayoutParams.WRAP_CONTENT);
            weightWarpContent = (lp.width  == LayoutParams.WRAP_CONTENT);
        }

        //计算单元大小、列数
        layoutWidth  = MeasureSpec.getSize(widthMeasureSpec);
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (columnWidth <= 0 && numColumns <= 0){
            //单元格宽度和列数都没有指定，则默认为 4 列。
            columnCount = 4;

            //计算列宽
            childWidth = (layoutWidth - (columnCount - 1) * horizontalSpacing) / columnCount;
        }
        else if (columnWidth > 0 && numColumns <= 0){
            //指定了单元格宽度，没有指定列数则根据当前宽计算出列数
            childWidth = columnWidth;

            int tw  = 0;
            columnCount = 0;

            while((tw += childWidth) < layoutWidth){
                columnCount ++;
                tw += horizontalSpacing;
            }
        }
        else if (columnWidth <= 0 && numColumns >0){
            //没有指定单元格宽度，指定了列数。
            columnCount = numColumns;

            //计算列宽
            childWidth = (layoutWidth - (columnCount - 1) * horizontalSpacing) / columnCount;
        }
        else if (columnWidth >= 0 && numColumns >0){
            //指定了单元格宽度，指定了列数。
            columnCount = numColumns;
            childWidth = columnWidth;
            if (childWidth * columnCount + (columnCount - 1) * horizontalSpacing > layoutWidth){
                //设定的列数与列宽计算后超过最大宽度，重新计数每列宽度
                childWidth = (layoutWidth - (columnCount - 1) * horizontalSpacing) / columnCount;
            }
        }

        if (columnCount < 1)
            columnCount = 1;

        if (childWidth < 0)
            childWidth = 0;

        if (rowHeight <= 0){
            //没有设置行高，则单元格的高度与宽度相同
            childHeight = childWidth;
        }
        else{
            //设置了行高，则使用用户设置的高度做为单元格高度。
            childHeight = rowHeight;
            if (childHeight < 0) childHeight = 0;
        }

        //计算所有列与间距加在一起的总宽度
        int ww = childWidth * columnCount + (columnCount -1) * horizontalSpacing;

        if (columnCount > 1 && layoutWidth < ww){
            //所有列加一起的宽度超过总宽度则重新计算水平间距
            horizontalSpacing = (layoutWidth - childWidth * columnCount) / (columnCount -1);
            horizontalMargin  = 0;
        }
        else{
            horizontalMargin = (layoutWidth - ww) / 2;
        }

        //计算所有子项目加到一起的总高度
        int rowCount = (int) Math.ceil((double) getChildCount() / columnCount);
        int contentHeight = rowCount * (childHeight + verticalSpacing) - verticalSpacing;
        if (contentHeight < 0) contentHeight = 0;

        //如果高度值设置的是根据内容大小，则在些尝试调整Grid的高度。
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightWarpContent){
            switch (hMode){
                case MeasureSpec.UNSPECIFIED: //未指定大小
                    layoutHeight = contentHeight;
                    break;
                case MeasureSpec.AT_MOST: //最多达到的大小
                    if (contentHeight < layoutHeight){
                        layoutHeight = contentHeight;
                    }
                    break;
            }
        }

        setMeasuredDimension(layoutWidth,layoutHeight);

        //通知子视图测量大小，如果子视图是ViewGroup 必须有执行此过程，
        //否则ViewGroup内部的视图无法显示。
        int size = getChildCount();

        for (int index = 0;index < size;index++)
        {
            View child = getChildAt(index);
            //强制子视图的大小
            int wMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int hMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            child.measure(wMeasureSpec, hMeasureSpec);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i)
    {
    	if (dragged == -1)
    		return i;
    	else if (i == childCount - 1)
    		return dragged;
    	else if (i >= dragged)
    		return i + 1;
    	return i;
    }

    //EVENT HANDLERS
    @Override
    public void onClick(View view) {
        //enabled == false 表示用户是想要卷动内容而不是单击
        if (!enabled)
            return;

        if (secondaryOnClickListener != null)
            secondaryOnClickListener.onClick(view);

        int lastIndex = getLastIndex();

        if (onItemClickListener != null && lastIndex != -1)
            onItemClickListener.onItemClick(this,getChildAt(lastIndex), lastIndex, lastIndex / columnCount);
    }

    @Override
    public boolean onLongClick(View view){
        //enabled == false 表示用户是想要卷动内容而不是想要长按。编辑模式时不充许长按。
        if (!enabled || isEditMode)
            return false;

        int index = getLastIndex();

        //长按了某个单元格,如果单元格充许被拖动,则启动托拽。
        if (index != -1 && isDragOrRearrange(index)){
            //如果充许开启编辑模式，则长按后进入编辑模式。
            if (isLongClickEditMode){
                entryEditMode();

                if (onEditModeListener != null)
                    onEditModeListener.onEntryEditMode(this);
            }

            if (isAllowRearrange()){
                dragged = index;
                animateDragged();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (externalTouchListener != null && externalTouchListener.onTouch(view, event)){
            return true;
        }

        if (deletedViews.size() > 0){
            //删除过程中，忽略触碰事件并不响应长安、单击子视图事件。
            enabled = false;
            return false;
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //标记Touch Down
                enabled = true;

                lastX = (int) event.getX();
                lastY = (int) event.getY();

                touching = true;

                if (isEditMode && isAllowRearrange()){
                    //如果处在编模式，则Touch down 时检查是否按住了某个子视图，如果单元格充许被拖动,则启动托拽。
                    int index = getIndexFromPoint(lastX, lastY);
                    if (index >=0 && isDragOrRearrange(index)){
                        dragged = index;
                        animateDragged();
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                //y 轴距上次 Touch 事件的移动距离
                int delta = lastY - (int)event.getY();

                if (dragged != -1){
                    //有托拽目标，移动拖拽目标到手指位置。
                    int x = (int)event.getX();
                    int y = (int)event.getY();
                    int l = x - childWidth / 2;
                    int t = y - childHeight / 2;

                    childLayout(dragged,l, t, l + childWidth, t + childHeight);

                    //获取拖拽目标下方单元格索引（即要放入的位置索引）
                    int target = getTargetFromPoint(x, y);

                    //1.最后一次放入的位置与现在要放入的位置相同时不重排。
                    //2.拖拽并卷动状态下不进行重排。
                    //3.删除中不能重排
                    //4.不充许重排的项目不能重排
                    if (lastTarget != target && !isDragAndScroll){
                        if (target != -1 && isDragOrRearrange(target)){
                            animateGap(target);
                            lastTarget = target;
                        }
                    }
                }
                else{
                    //没有托拽子视图，则认为当前的滑动是要卷动 ViewGroup 内容。
                    clampScroll(delta);

                    //如果在长按事件触发前手指移动距离超过2个单位，则忽略本次的长按事件及单击事件，
                    //认为用户是想托动视图内容。
                    if (Math.abs(delta) > 2)
                        enabled = false;

                    onLayout(false, getLeft(), getTop(), getRight(), getBottom());
                }

                lastX = (int) event.getX();
                lastY = (int) event.getY();
                lastDelta = delta;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (dragged != -1)
                {
                    //得到拖拽目标视图
                    View v = getChildAt(dragged);

                    if (lastTarget != -1){
                        //如果拖拽目标准备重新放置一个位置时，得新排序子视图。
                        reorderChildren();
                    }
                    else {
                        //拖拽目标没有改变位置，恢复它的大小，并放回原位。
                        Point xy = getPointFromIndex(dragged);
                        childLayout(dragged,xy.x, xy.y, xy.x + childWidth, xy.y + childHeight);
                    }

                    //清除拖拽目标上的动画
                    if (v != null)
                        v.clearAnimation();

                    lastTarget = -1;
                    dragged = -1;
                }

                touching = false;
                break;
        }

        //正在拖拽状态，结束 onTouch 事件处理。
        if (dragged != -1)
            return true;

        return false;
    }

    @Override
    public void onDeleteClick(DragGridViewItem item) {
        //触碰过程中禁止删除
        if (touching)
            return;

        //正在删除这个图标，不能再次删除它。
        if (deletedViews.containsKey(item))
            return;

        //处理子视图的“删除图标”单击事件，询问是否充许删除这个图标。
        //当 onDelete 返回 true 时删除这个图标。
        int index = indexOfChild(item);
        if (onEditModeListener == null || onEditModeListener.onDelete(this,index)){
            removeView(item);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        //动画结事事件，目前用于删除子视图时播放的动画，当动画播放完时从布局中移出子视图。
        if (animation instanceof  DeleteAnimation){
            View v = ((DeleteAnimation) animation).getView();

            Message msg = new Message();
            msg.what = MSG_DELETE_ITEM;
            msg.obj  = v;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    /**
     * 根子视图对象获取它的索引值。
     * @param child  子视图对象
     * @return  子视图的索引（单元格索引）
     */
    public int getIndexOf(View child){
        int count = getChildCount();
        for (int i = 0; i < count; i++){
            if (getChildAt(i) == child)
                return i;
        }

        return -1;
    }

    /**
     * 卷动到顶部
     */
    public void scrollToTop()
    {
        setScroll(0);
    }

    /**
     * 卷动到最底部
     */
    public void scrollToBottom()
    {
        setScroll(Integer.MAX_VALUE);
        clampScroll(0);
    }

    /**
     * 设置视图重排侦听器
     * @param listener  侦听器
     */
    public void setOnRearrangeListener(OnRearrangeListener listener)
    {
        this.onRearrangeListener = listener;
    }

    /**
     * 设置子视图单击侦听器。
     * @param listener  侦听器
     */
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.onItemClickListener = listener;
    }

    /**
     * 设置编辑模式侦听器
     */
    public void setOnEditModeListener(OnEditModeListener listener){
        this.onEditModeListener = listener;
    }

    /**
     * 设置列数
     * @param num 列数，必须大于 1。
     */
    public void setNumColumns(int num){
        this.numColumns = num;
        requestLayout();
    }

    /**
     * 设置垂直间距
     * @param verticalSpacing 间距值
     */
    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
        requestLayout();
    }

    /**
     * 水平间距
     * @param horizontalSpacing 间距值
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        requestLayout();
    }

    /**
     * 设置列宽
     * @param columnWidth 列宽
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
        requestLayout();
    }

    /**
     * 设置行高
     * @param rowHeight 行高
     */
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        requestLayout();
    }

    public boolean isLongClickEditMode() {
        return isLongClickEditMode;
    }

    /**
     * 是否充许长按后进入编辑模式
     * @param longClickEditMode true 长按后进入编辑模式
     */
    public void setLongClickEditMode(boolean longClickEditMode) {
        isLongClickEditMode = longClickEditMode;
    }

    /**
     * 是否充许过量卷动
     * @param overreach true 是否充许过量卷动
     */
    public void setOverreach(boolean overreach) {
        isOverreach = overreach;
    }

    public boolean isOverreach(){
        return isOverreach;
    }

    public boolean isAllowRearrange() {
        return isAllowRearrange;
    }

    /**
     * 设置是否充许重排
     * @param allowRearrange true 充许重排
     */
    public void setAllowRearrange(boolean allowRearrange) {
        isAllowRearrange = allowRearrange;
    }

    /**
     * 进入编辑模式
     */
    public void entryEditMode(){
        isEditMode = true;

        showHideDeleteIcon();

        if (onEditModeListener != null)
            onEditModeListener.onEntryEditMode(this);
    }

    /**
     * 退出编辑模式
     */
    public void exitEditMode(){
        isEditMode = false;

        showHideDeleteIcon();

        if (onEditModeListener != null)
            onEditModeListener.onExitEditMode(this);
    }

    /**
     * 判断当前是否处在编辑模式。
     * @return
     */
    public boolean isEditMode(){
        return this.isEditMode;
    }

    private void setListeners(){
        setOnTouchListener(this);
        super.setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * 根据 x,y 位置获单元格索引，如没不在任何单元格的范围内返回 -1。
     * @param x  x 坐标
     * @param y  y 坐标
     * @return  单元格索引，如没不在任何单元格的范围内返回 -1。
     */
    private int getIndexFromPoint(int x, int y)
    {
        int col = getColFromX(x), row = getRowFromY(y + scroll);

        //坐标位于两行或两列之间
        if (col == -1 || row == -1)
            return -1;

        int index = row * columnCount + col;

        //索引值超过子视图总数，这是不正确的。
        if (index >= getChildCount())
            return -1;

        return index;
    }

    /**
     * 根据 x 位置取所在的列索引
     * @param x x 坐标
     * @return  列索引,如果不匹配返回 -1。
     */
    private int getColFromX(int x)
    {
        //x减去左空白值
        x -= horizontalMargin;

        //x 坐标在左侧空白区域内
        if (x <= 0)
            return -1;

        //在 x 加上一个horizontalSpacing,因为最后一列是没有水平间距，加上的目地是为了方便进行除法计算。
        x += horizontalSpacing;

        int colWidth = (childWidth + horizontalSpacing);
        int col      = x / colWidth;
        int offset   = x % colWidth;

        //计算出的列数大于等总列数，则表示x坐标没在任何列范围内。
        if (col >= columnCount)
            return -1;

        //x 坐标位于水平间距内。
        if (offset > childWidth)
            return -1;

        return col;
    }

    /**
     * 根据 y 位置取所在的行索引
     * @param y  y 坐标
     * @return 行索引,如果不匹配返回 -1。
     */
    private int getRowFromY(int y)
    {
        int rowCount = (int) Math.ceil((double) getChildCount() / columnCount);

        //在 y verticalSpacing,因为最后一行是没有垂直间距，加上的目地是为了方便进行除法计算。
        y += verticalSpacing;

        int rowHeight = (childHeight + verticalSpacing);
        int row      = y / rowHeight;
        int offset   = y % rowHeight;

        //计算出的行数大于等总行数，则表示y坐标没在任何列范围内。
        if (row >= rowCount)
            return -1;

        //x 坐标位于水平间距内。
        if (offset > childHeight)
            return -1;

        return row;
    }

    /**
     * 根据 x,y 位置计算托放位置的单元格索引，如没不在任何单元格的范围内返回 -1。
     * @param x  x 坐标
     * @param y  y 坐标
     * @return
     */
    private int getTargetFromPoint(int x, int y)
    {
        //坐标点位于两行之间
        if (getRowFromY(y + scroll) == -1)
            return -1;

        //if (getIndexFromCoor(x, y) != -1) //touch on top of another visual
            //return -1;
        
        int leftPos  = getIndexFromPoint(x - (childWidth / 4), y);
        int rightPos = getIndexFromPoint(x + (childWidth / 4), y);

        //坐标位于空白区域
        if (leftPos == -1 && rightPos == -1)
            return -1;

        //坐标位于单元格中间，因此无法判定是将托动的单元格放在它在左边还是右边，所以返回 -1。
        if (leftPos == rightPos)
        	return -1;
        
        int target = -1;

        //返回托放位置单元格索引
        if (rightPos > -1){
            target = rightPos;
        }
        else if (leftPos > -1){
            target = leftPos + 1;
        }

        //拖动的目标与放置目标相同
        if (dragged < target)
            return target - 1;

        return target;
    }

    /**
     * 根所单元格的索引值计算出它的左上角坐标。
     * @param index  单元格索引
     * @return  坐标
     */
    private Point getPointFromIndex(int index)
    {
        int col = index % columnCount;
        int row = index / columnCount;
        int x,y;

        if (col >= 1){
           x = horizontalMargin + (childWidth + horizontalSpacing) * col;
        }
        else{
           x = horizontalMargin;
        }

        if (row >= 1){
            y = (childHeight + verticalSpacing) * row - scroll ;
        }
        else{
            y = -scroll;
        }

        return new Point(x,y);
    }

    /**
     * 开始拖动一个子视图单元格
     */
    private void animateDragged()
    {
        //获取拖拽目标视图
    	View v = getChildAt(dragged);

        //计算拖拽目标的中心点
    	int x = getPointFromIndex(dragged).x + childWidth / 2;
        int y = getPointFromIndex(dragged).y + childHeight / 2;

        //计算拖拽目标的左上角坐标
        int l = x - childWidth / 2;
        int t = y - childHeight / 2;

    	childLayout(dragged,l, t, l + childWidth, t + childHeight);

        //播放一个动画，产生一个跳动感。
    	AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, childWidth, childHeight);
		scale.setDuration(animT);
		AlphaAnimation alpha = new AlphaAnimation(1, .8f);
		alpha.setDuration(animT);

		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		
		v.clearAnimation();
		v.startAnimation(animSet);
    }

    /**
     * 动画移动目地位置两侧的子视图（单元格）
     * @param target  目地位置索引
     */
    private void animateGap(int target)
    {
        int count = getChildCount();

    	for (int i = 0; i < count; i++)
    	{
    		View v = getChildAt(i);

            //跳过被拖动的对象
            if (i == dragged)
	    		continue;

            //子视图新位置
    		int newPos = i;
            //目标位置索引大于被拖拽的对象，并且子视图位于它们之间，子视图索引减1。
    		if (dragged < target && i >= dragged + 1 && i <= target)
    			newPos--;
            //目标位置索引小于被拖拽的对象，并且子视图位于它们之间，子视图索引加1。
    		else if (target < dragged && i >= target && i < dragged)
    			newPos++;
    		
    		//旧位置
    		int oldPos = i;

            //之前缓存了新位置，因为在拖拽过程中ViewGroup内的视图顺序并没有真正改变，是要等到
            //拖拽完成后才重排，所以在拖拽过程中要缓存子视图的新位置。
    		if (newPositions.get(i) != -1)
    			oldPos = newPositions.get(i);

            //新旧位置相同跳过动画
    		if (oldPos == newPos)
    			continue;

            //计算新旧位置的坐标。
    		Point oldXY = getPointFromIndex(oldPos);
    		Point newXY = getPointFromIndex(newPos);

            //计算位移量
            int l = v.getLeft();
            int t = v.getTop();

    		Point oldOffset = new Point(oldXY.x - l, oldXY.y - t);
    		Point newOffset = new Point(newXY.x - l, newXY.y - t);

    		TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
																  Animation.ABSOLUTE, newOffset.x,
																  Animation.ABSOLUTE, oldOffset.y,
																  Animation.ABSOLUTE, newOffset.y);

            //设置加速度与减速度。
            Interpolator accelerate = new AccelerateDecelerateInterpolator();
            translate.setInterpolator(accelerate);
            //动画时长
			translate.setDuration(animT);

			translate.setFillEnabled(true);
			translate.setFillAfter(true);

			v.clearAnimation();
			v.startAnimation(translate);

            //缓存子视图的新位置。
			newPositions.set(i, newPos);
    	}
    }

    /**
     * 动画删除一个子视图
     * @param view  子视图索引
     */
    private void animateDelete(View view){
        //删除标识置位，在此期间不充许拖动视图。
        deletedViews.put(view,false);
        DeleteAnimation da = new DeleteAnimation(view);

        //设置侦听器，等动画播放完后从布局中移队这个视图
        da.setAnimationListener(this);
        view.startAnimation(da);
    }

    /**
     * 重新排序子视图（单元格）
     */
    private void reorderChildren()
    {
    	if (onRearrangeListener != null)
    		onRearrangeListener.onRearrange(this,dragged, lastTarget);

        ArrayList<View> children = new ArrayList<View>();

        //清除所有子视图的动画，并将所有视图缓到 children 列表中。
        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
        	getChildAt(i).clearAnimation();
            children.add(getChildAt(i));
        }

        //移除所有子视图
        removeAllViews();

        //重新排序子视图
        while (dragged != lastTarget){
            if (lastTarget == children.size()) //目标位置是最后
            {
                children.add(children.remove(dragged));
                dragged = lastTarget;
            }
            else if (dragged < lastTarget) // 拖拽对象位置在目标置左边
            {
                Collections.swap(children, dragged, dragged + 1);
                dragged++;
            }
            else if (dragged > lastTarget) // 拖拽对象位置在目标置右边
            {
                Collections.swap(children, dragged, dragged - 1);
                dragged--;
            }
        }

        //将子视图得新添加到 VeiwGroup 中
        int size = children.size();
        for (int i = 0; i < size; i++)
        {
        	newPositions.set(i, -1); //清除临时位置缓存。
            addView(children.get(i));
        }

        onLayout(false, getLeft(), getTop(), getRight(), getBottom());
    }

    /**
     * 当过量卷动时进行限制、松手后回弹、惯性卷动效果。
     * @param offsetY  Y 方向的偏移量，如果设置该值将可能会修改 scroll 值。（off = Y1-Y2)
     */
    private void clampScroll(int offsetY)
    {
        //弹性值，当向下或向下卷动超过范围但在卷动溢出量内后松开手指后，用这个值计算回弹速度。
    	int stretch = 3;

        //卷动溢出量
        int overreach = getHeight() / 2;

        //向下最大卷动量
    	int max = getMaxScroll();
    	max = Math.max(max, 0);

        //不允许溢出卷动，将溢出量清0。
        if (!isOverreach()){
            overreach = 0;
        }

        incScroll(offsetY);

        //提示:　scroll < 0  表示手指向下滑，scorll > 0 表示手指向上滑。
    	if (scroll < - overreach)//向上卷动达到最大溢出量
    	{
            setScroll(-overreach);
    		lastDelta = 0;
    	}
    	else if (scroll > max + overreach) //向下卷动达到最大溢出量
    	{
            setScroll(max + overreach);
    		lastDelta = 0;
    	}
    	else if (scroll < 0) //已经卷动到最顶部但没有超过溢出量
    	{
	    	if (scroll >= -stretch){
                //向下过量卷动量小于弹性值，直接回到原位。
	    		setScroll(0);
            }
	    	else if (!touching){
                //向下过量卷动量大于弹性值，松手后进行回弹。
                decScroll(scroll / stretch);
            }
            else {
                //向下过量卷动量大于弹性值并且没有松手，则根据溢出量计算出一个助力值。
                float resistance = 1.0f - ((float)-scroll / (float)overreach);
                decScroll(offsetY);
                offsetY = (int)(offsetY * resistance);
                incScroll(offsetY);
            }
    	}
    	else if (scroll > max)  //已经卷动到最底部但没有超过溢出量
    	{
    		if (scroll <= max + stretch){
                //向上过量卷动量小于弹性值，直接回到原位。
                setScroll(max);
            }
    		else if (!touching){
                //向上过量卷动量大于弹性值，松手后进行回弹。
                incScroll((max - scroll) / stretch);
            }
            else {
                //向上过量卷动量大于弹性值并且没有松手，则根据溢出量计算出一个助力值。
                float resistance = 1.0f - (float)(scroll - max) / (float)overreach;
                decScroll(offsetY);
                offsetY = (int)(offsetY * resistance);
                incScroll(offsetY);
            }
    	}
    }

    /**
     * 计算最大的可卷动距离
     * @return  最大距离
     */
    private int getMaxScroll()
    {
    	int rowCount = (int) Math.ceil((double) getChildCount() / columnCount);
        int max = rowCount * childHeight + (rowCount - 1) * horizontalSpacing - getHeight();
    	return max;
    }

    /**
     * 获取最后一次触碰的单元格索引
     * @return 索引值
     */
    private int getLastIndex()
    {
        return getIndexFromPoint(lastX, lastY);
    }

    /**
     * 初始化
     * @param attrs
     */
    private void init(AttributeSet attrs){
        setListeners();
        handler.removeCallbacks(updateTask);
        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);

        if (attrs == null)
            return;

        //默认只有一列
        setNumColumns(1);

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DraggableGridView);

        int     iValue = 0;
        float   fValue = 0.0f;
        boolean bValue = false;

        //属性 horizontalSpacing
        fValue = ta.getDimension(R.styleable.DraggableGridView_horizontalSpacing,0);
        setHorizontalSpacing((int)fValue);

        //属性 verticalSpacing
        fValue = ta.getDimension(R.styleable.DraggableGridView_verticalSpacing,0);
        setVerticalSpacing((int)fValue);

        //属性 numColumns
        iValue = ta.getInt(R.styleable.DraggableGridView_numColumns,-1);
        setNumColumns(iValue);

        //属性 columnWidth
        fValue = ta.getDimension(R.styleable.DraggableGridView_columnWidth, 0);
        setColumnWidth((int)fValue);

        //属性 rowHeight
        fValue = ta.getDimension(R.styleable.DraggableGridView_rowHeight, 0);
        setRowHeight((int) fValue);

        //属性 isLongClickEditMode
        bValue = ta.getBoolean(R.styleable.DraggableGridView_isLongClickEditMode, false);
        setLongClickEditMode(bValue);

        //属性 isAllowRearrange
        bValue = ta.getBoolean(R.styleable.DraggableGridView_isAllowRearrange, true);
        setAllowRearrange(bValue);

        //属性 isOverreach
        bValue = ta.getBoolean(R.styleable.DraggableGridView_isOverreach, true);
        setOverreach(bValue);

        ta.recycle();
    }

    /**
     * 新调整子视图的位置、大小，如果子视图的目示位置与当前位置均不在可视范围则忽略布局
     * @param index  子视图的索引
     * @param l      左值
     * @param t      上值
     * @param r      右值
     * @param b      下值
     */
    private void childLayout(int index,int l, int t, int r, int b){
        View v = getChildAt(index);
        if (v == null)
            return;

        if (isInVisualArea(l,t,r,b) || isInVisualArea(v.getLeft(),v.getTop(),v.getRight(),v.getBottom())){
            v.layout(l,t,r,b);

            //显示隐藏删除图标
            if (v instanceof DragGridViewItem){
                if (isEditMode()){
                    ((DragGridViewItem)v).showDeleteIcon();
                }
                else {
                    ((DragGridViewItem)v).hideDeleteIcon();
                }
            }
        }
    }

    /**
     * 判定给定的区域是否在可见范围
     * @param l      左值
     * @param t      上值
     * @param r      右值
     * @param b      下值
     * @return  可见返回 true
     */
    private boolean isInVisualArea(int l, int t, int r, int b){
        if (l > layoutWidth){
            return false;
        }

        if (r < 0){
            return false;
        }

        if (t > layoutHeight){
            return false;
        }

        if (b < 0){
            return false;
        }

        return true;
    }

    private void setScroll(int value) {
        scroll = value;
        updateLayout = true;
    }

    private void incScroll(int value){
        scroll += value;
        if (value != 0)
            updateLayout = true;
    }

    private void decScroll(int value){
        scroll -= value;
        if (value != 0)
            updateLayout = true;
    }

    /**
     * 判断目标是否可以被拖动或重排。
     * @param index  子视图索引
     */
    private boolean isDragOrRearrange(int index){
        //如果子视图是 DragGridViewItem，则判断它是否可以被拖动重排。
        View v = getChildAt(index);
        if (v != null && v instanceof DragGridViewItem){
            if (!((DragGridViewItem) v).isAllowRearrange()){
                return false;
            }
        }

        return true;
    }

    /**
     * 如果子视图是 DragGridViewItem 类型，则在编辑模式时在该子视图上显示一个删除图标，
     * 非编辑模式时隐藏删除图标。
     */
    private void showHideDeleteIcon(){
        int count = getChildCount();
        for (int i = 0; i < count; i++){
            View view = getChildAt(i);

            //如果子视图不可见则不显示图标
            if (!isInVisualArea(view.getLeft(),view.getTop(),view.getRight(),view.getBottom()))
                continue;

            if (view instanceof DragGridViewItem){
                if (isEditMode()){
                    ((DragGridViewItem)view).showDeleteIcon();
                }
                else {
                    ((DragGridViewItem)view).hideDeleteIcon();
                }
            }
        }
    }

    public static interface OnItemClickListener {
        /**
         * 单击Grid 中的子视图事件。
         * @param grid   DraggableGridView 对象
         * @param view   子视图对象
         * @param index  子视图的索引
         * @param row    了视图所有的行索引
         */
        void onItemClick(DraggableGridView grid, android.view.View view, int index, int row);
    }

    public interface  OnRearrangeListener {
        /**
         * 视图位置重排触发此事件。
         * @param oldIndex    旧位置
         * @param newIndex    新位置
         */
        public abstract void onRearrange(DraggableGridView grid, int oldIndex, int newIndex);
    }

    public interface  OnEditModeListener {
        /**
         * 进入编辑模式。
         */
        public abstract void onEntryEditMode(DraggableGridView grid);

        /**
         * 退出编辑模式。
         */
        public abstract void onExitEditMode(DraggableGridView grid);

        /**
         * 删除一个项目时触发此事件，如果该事件返回false 则不执行删除，如果返回 true 则执行删除动作。
         * @param index  被删除项目的索引
         */
        public abstract boolean onDelete(DraggableGridView grid, int index);
    }

    //将要删除的视图缩小
    private class DeleteAnimation extends ScaleAnimation {
        private View view;
        public  DeleteAnimation(View v){
            super(1,.01f, 1, .01f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            view = v;
            this.setDuration(80);
            this.setFillEnabled(true);
            this.setFillAfter(true);
            this.setInterpolator(new AccelerateInterpolator(1.0f));
        }

        public View getView(){
            return view;
        }
    }
}


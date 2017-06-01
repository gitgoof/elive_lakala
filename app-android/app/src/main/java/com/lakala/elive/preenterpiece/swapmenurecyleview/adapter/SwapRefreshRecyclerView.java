package com.lakala.elive.preenterpiece.swapmenurecyleview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwapRecyclerView;

/**
 */
public class SwapRefreshRecyclerView extends PullToRefreshBase<SwapRecyclerView> {
    private Context mContext;
    private SwapRecyclerView recyclerView;

    public SwapRefreshRecyclerView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SwapRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public SwapRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
        this.mContext = context;
    }

    public SwapRefreshRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        this.mContext = context;

    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected SwapRecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        if (null == recyclerView) recyclerView = new SwapRecyclerView(context, attrs);
        return recyclerView;
    }

//    @Override
//    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
//        if (null == recyclerView) recyclerView = new RecyclerView(context, attrs);
//        return recyclerView;
//    }

    @Override
    protected boolean isReadyForPullEnd() {
        RecyclerView.LayoutManager layoutManager = getRefreshableView().getLayoutManager();
        View view = layoutManager.findViewByPosition(recyclerView.getAdapter().getItemCount() - 1);
        if (null != view) {
            return getRefreshableView().getBottom() >= view.getBottom();
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }


    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();
        if (null == adapter || adapter.getItemCount() == 0) {
            return true;
        } else {
            if (getFirstVisiblePosition() == 0) {
                return getRefreshableView().getChildAt(0).getTop() >= getRefreshableView().getTop();
            }
        }
        return false;
    }


    private int getFirstVisiblePosition() {
        View firstVisibleChild = getRefreshableView().getChildAt(0);
        return firstVisibleChild != null ? getRefreshableView().getChildAdapterPosition(firstVisibleChild) : -1;
    }


}

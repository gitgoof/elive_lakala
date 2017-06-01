package com.lakala.elive.common.widget;

import com.lakala.elive.merapply.fragment.BaseFragment;

/**
 * viewpage +fragment 懒加载
 * Created by wenhaogu on 2017/3/9.
 */

public abstract class LazyFragment extends BaseFragment {

    private boolean isVisible;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    protected boolean isFirstVisible;//在initData第一次加载后置为true,防止页面切换重复调用initData

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    private void onVisible() {
        lazyLoad();
    }

    @Override
    protected void bindData() {
        isPrepared = true;
        lazyLoad();
    }


    private void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        //isFirstVisible变量要在initData第一次调用后置为true,防止页面切换重复调用initData
        if (!isFirstVisible) {
            initData();
            isFirstVisible = true;
        }
    }

    /**
     * 实现数据加载
     */
    protected abstract void initData();

    protected void onInvisible() {
    }
}

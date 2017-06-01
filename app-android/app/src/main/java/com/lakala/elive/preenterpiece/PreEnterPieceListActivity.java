package com.lakala.elive.preenterpiece;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lakala.elive.R;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.preenterpiece.adapter.PreFragmentPagerAdapter;
import com.lakala.elive.preenterpiece.swapmenurecyleview.MyViewPager;
import com.lakala.elive.preenterpiece.swapmenurecyleview.SwipeMenuBuilder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenu;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenuItem;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;

/**
 * Created by ousachisan on 2017/3/21.
 * 合作商预进件列表界面
 * 在PreEnterPieceListFragment里加载数据
 */
public class PreEnterPieceListActivity extends com.lakala.elive.merapply.activity.BaseActivity implements SwipeMenuBuilder {

    private PreFragmentPagerAdapter pagerAdapter;
    private ImageView mImgaBack;
    private EditText edtSearch;
    private TextView mTvSearch;
    private LinearLayout mLinerSearch;
    private TextView tvAction;
    private TabLayout tabLayout;

    private MyViewPager myViewPager;

    private String search = "";
    private String[] titles = new String[]{"待录入", "已提交", "处理中", "处理成功", "处理失败"};

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_cooperperenterpiece);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        mLinerSearch = findView(R.id.liner_search);
        mTvSearch = findView(R.id.tv_search);
        mImgaBack = findView(R.id.img_back);
        edtSearch = findView(R.id.edt_search);
        tvAction = findView(R.id.tv_action);
        tabLayout = findView(R.id.tab_layout);
        myViewPager = findView(R.id.view_pager);
        setTabLayoutStyle();
    }

    /**
     * 设置顶部的Tablayout的样式
     */
    private void setTabLayoutStyle() {
        pagerAdapter = new PreFragmentPagerAdapter(getSupportFragmentManager(), titles);
        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        myViewPager.setNoScroll(true);//设置viewPage不能滑动
        myViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void bindEvent() {
        mImgaBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);//点击搜索
        edtSearch.addTextChangedListener(new TextWatcher() {//监听editText的变化
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setSearch(search);
            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setmStatues(tab.getPosition() + "");
                Log.e("%%%%%%%%%%%%", tab.getPosition() + "OnTabSelected");
                pagerAdapter.currentFragment.refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private static String mStatues;

    public static String getmStatues() {
        return mStatues;
    }

    public void setmStatues(String mStatues) {
        PreEnterPieceListActivity.mStatues = mStatues;
    }

    @Override
    protected void bindData() {
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        pagerAdapter.currentFragment.refresh();//重新提交后刷新界面
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_search://点击搜索按钮
                //搜索操作
                search = edtSearch.getText().toString().trim();
                ((InputMethodManager) edtSearch.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        PreEnterPieceListActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                pagerAdapter.currentFragment.setSearch(search);
                break;
            case R.id.tv_action://搜索
                if (View.GONE == mLinerSearch.getVisibility()) {
                    mLinerSearch.setVisibility(View.VISIBLE);
                    tvAction.setVisibility(View.GONE);
                    tvTitleName.setVisibility(View.GONE);
                } else {//搜索操作
                    search = edtSearch.getText().toString().trim();
                    setSearch(search);
                    ((InputMethodManager) edtSearch.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            PreEnterPieceListActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    pagerAdapter.currentFragment.setSearch(search);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setmStatues("0");//界面销毁的时候恢复变量的初始转态
    }

    @Override
    public SwipeMenuView create() {
        SwipeMenu menu = new SwipeMenu(this);
        SwipeMenuItem item = new SwipeMenuItem(this);
        item.setTitle("删除")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(Color.RED));
        menu.addMenuItem(item);
        SwipeMenuView menuView = new SwipeMenuView(menu);
        menuView.setOnMenuItemClickListener(pagerAdapter.currentFragment.mOnSwipeItemClickListener);
        return menuView;
    }


}

package com.lakala.elive.merapply.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.R;
import com.lakala.elive.common.net.req.MerApplyListReq;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.merapply.adapter.MyFragmentPagerAdapter;
import com.lakala.elive.merapply.adapter.MyMerchantsAdapter;

import java.util.HashMap;


/**
 * 我的商户
 * Created by wenhaogu on 2017/1/17.
 */

public class MyMerchantsActivity extends BaseActivity {


    private PullToRefreshListView mPullToRefreshListView;
    private ListView mMerListView;

    private int pageSize = 10;

    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数
    private MerApplyListReq merApplyListReq;

    public static final String APPLYID_ID = "applyId";
    private MyMerchantsAdapter myMerchantsAdapter;

    private EditText edtSearch;
    private TextView tvAction;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //ENTER:待录入（未录入完成）,AUDIT:待审核（未同步到bmcp）,AUDITING:审核中（已同步到bmcp）,REBUT:审核失败,STORAGED:审核成功
    // 待录入、待审核、审核中、审核通过、审核失败
    private String[] titles = new String[]{"审核通过","审核失败","待录入","待审核","审核中"};
    private MyFragmentPagerAdapter pagerAdapter;
    private String search;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_my_merchants);
    }

    @Override
    protected void bindView() {
        edtSearch = findView(R.id.edt_search);
        tvAction = findView(R.id.tv_action);
        tabLayout = findView(R.id.tab_layout);
        viewPager = findView(R.id.view_pager);


        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.layout_divider_vertical));
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titles);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final String string = tab.getText().toString();
                String value = mSearchValueSave.get(string);
                if(TextUtils.isEmpty(value)){
                    value = "";
                }
                search = value;
                edtSearch.setText(value);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private final HashMap<String,String> mSearchValueSave = new HashMap<String,String>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchValueSave.clear();
    }

    @Override
    protected void bindEvent() {

        tvAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (View.GONE == edtSearch.getVisibility()) {//显示搜索框
                    edtSearch.setVisibility(View.VISIBLE);
                    tvAction.setText("搜索");
                    tvAction.setBackground(null);
                    tvTitleName.setVisibility(View.GONE);


                } else {//搜索操作
                    search = edtSearch.getText().toString().trim();
                    ((InputMethodManager) edtSearch.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            MyMerchantsActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    pagerAdapter.currentFragment.setSearch(search);
                    final String tab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();
                    mSearchValueSave.put(tab,search);
                }
            }
        });
        iBtnBack.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        pagerAdapter.currentFragment.refresh();//RejectMerApplyActivity重新提交后刷新界面
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                setResult(RESULT_OK);
                finish();
                break;

        }
    }

    @Override
    protected void bindData() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return false;
    }
}

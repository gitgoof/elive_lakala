package com.lakala.shoudan.activity.shoudan.loan.loanrecord;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.ui.component.NavigationBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 贷款记录
 */
public class CreditRecordsActivity extends AppBaseActivity implements CreditRecordsConstract.CreditBusinessView, PullToRefreshBase.OnRefreshListener2<ListView>,AdapterView.OnItemClickListener{
    @Bind(R.id.record_list)
    PullToRefreshListView recordList;
    @Bind(R.id.activity_loan_records)
    LinearLayout activityLoanRecords;
    private CreditRecordsConstract.Presenter presenter;
    private CreditRecordsAdapter adapter;
    private int isDefaultId = 0;
    public static int PAGECOUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cedit_records);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {

        presenter = new CreditRecordsPresenter(this);
        super.initUI();
        navigationBar.setTitle("全部记录");
        navigationBar.toggleTitleRightDrawable();
        navigationBar.setTitleDrawablePadding(8);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.title) {
                    presenter.showPopupWindow(isDefaultId, navigationBar);
                } else if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
                }
            }
        });
        adapter = new CreditRecordsAdapter(context, presenter, R.layout.item_credit_record);
        recordList.setAdapter(adapter);
        recordList.getRefreshableView().setEmptyView(presenter.getEmptyView("没有数据"));
        recordList.setMode(PullToRefreshBase.Mode.DISABLED);
        recordList.setOnRefreshListener(this);
        presenter.setList(recordList);
        recordList.setOnItemClickListener(this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        recordList.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        recordList.onRefreshComplete();
    }

    @Override
    public void setPresenter(CreditRecordsConstract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showToast(String str) {
        ToastUtil.toast(context,str);
    }

    @Override
    public void showLoading() {
        showProgressWithNoMsg();
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);//解除绑定，官方文档只对fragment做了解绑
        PAGECOUNT = 0;
        super.onDestroy();
    }

    @Override
    public void setPop(int pagecount, int i, String title) {
        PAGECOUNT = pagecount;
        isDefaultId = i;
        navigationBar.setTitle(title);
    }

    @Override
    public void showNewData(List<CreditRecordsModel> dataList) {
            adapter.reflash(dataList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClick(adapter.getItem(position-1));
    }
}

package com.lakala.shoudan.activity.shoudan.loan.loanlist;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.MyAdGallery;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.productdetail.CreditNum;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.module.CustomNestListView;
import com.paem.iloanlib.api.SDKExternalAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 贷款列表
 */
public class CreditListActivity extends AppBaseActivity implements CreditListContract.CreditBusinessView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ScrollView> {
    @Bind(R.id.adgallery)
    MyAdGallery adgallery;
    @Bind(R.id.rl_gallery)
    RelativeLayout rlGallery;
    @Bind(R.id.ovalLayout)
    LinearLayout ovalLayout;
    @Bind(R.id.credit_list)
    CustomNestListView creditList;
    @Bind(R.id.malls_scrollView)
    PullToRefreshScrollView mallsScrollView;
    private CreditListContract.Presenter presenter;
    private CreditListAdapter adapter;
    public static List<String> list_support_loan=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditNum.isCreat.setList(true);
        setContentView(R.layout.activity_cedit_list);
        ButterKnife.bind(this);
        initUI();
        initSupport_Loan_List();
    }

    @Override
    protected void initUI() {
        super.initUI();
        //定位
        LocationManager.getInstance().statLocating();
        presenter = new CreditListPresenter(this);
        navigationBar.setTitle("贷款");
        navigationBar.setActionBtnText("我的贷款");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                    case action:
                        presenter.topRight();
                }
            }
        });
        presenter.initGallery(rlGallery,ovalLayout,adgallery);

        adapter=new CreditListAdapter(this,presenter,this);
        creditList.setAdapter(adapter);
        creditList.setFocusable(false);
        mallsScrollView.setMode(PullToRefreshBase.Mode.DISABLED);
        mallsScrollView.setOnRefreshListener(this);
        presenter.setList(mallsScrollView);
    }

    @Override
    public void setPresenter(CreditListContract.Presenter presenter) {
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
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
       //presenter.getLoanListAndDetail();
        mallsScrollView.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        mallsScrollView.onRefreshComplete();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);//解除绑定，官方文档只对fragment做了解绑
        CreditNum.isCreat.setList(false);
        if(!CreditNum.isCreat.isLisDetail()&&!CreditNum.isCreat.isList()){
            SDKExternalAPI.getInstance().destroy();
        }
        super.onDestroy();
    }

    @Override
    public void cancelRefresh() {
        mallsScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mallsScrollView.onRefreshComplete();
            }
        }, 1000);
    }

    @Override
    public void showApplay(CreditListModel model) {
        presenter.showApplay(model);
    }

    /**
     * 初始化支持的贷款列表
     * 新加贷款业务需要再这个添加相应的业务
     */
    public void initSupport_Loan_List(){
        list_support_loan.add("DK_LKL");
        list_support_loan.add("DK_DS");
        list_support_loan.add("DK_51");//51贷
        list_support_loan.add("DK_PA");//平安普惠
        list_support_loan.add("DK_YFQ");//易分期
        list_support_loan.add("DK_YRD");//一日贷
        list_support_loan.add("DK_TNH");//替你还
    }
}

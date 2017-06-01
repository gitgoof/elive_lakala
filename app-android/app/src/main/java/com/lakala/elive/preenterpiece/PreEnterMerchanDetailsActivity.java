package com.lakala.elive.preenterpiece;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.fragment.BaseFragment;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.adapter.PreFragmentDetailPagerAdapter;
import com.lakala.elive.preenterpiece.fragment.PreDetailApplyInfoFragment;
import com.lakala.elive.preenterpiece.fragment.PreDetailBasicInfoFragment;
import com.lakala.elive.preenterpiece.fragment.PreDetailMerchantsInfoFragment;
import com.lakala.elive.preenterpiece.fragment.PreDetailSettleInfoFragment;

import java.util.ArrayList;


import static com.lakala.elive.merapply.fragment.MyMerchantsFragment.PROCESS;

/**
 * 合作商预进件的商户申请详情
 */
public class PreEnterMerchanDetailsActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String applyId;
    private String[] titles = new String[]{"申请信息", "结算信息", "商户信息", "基本信息"};
    private PreFragmentDetailPagerAdapter pagerAdapter;
    public static final String CONTENT_BEAN = "contentBean";

    private int process;
    private PreEnPieceDetailRequ preEnPieceDetailRequ;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_mer_apply_details);
    }

    @Override
    protected void bindView() {
        tabLayout = findView(R.id.tab_layout);
        viewPager = findView(R.id.view_pager);
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText("编辑");
        tvTitleName.setText("商户详情");
        iBtnBack.setVisibility(View.VISIBLE);
        setTabLayoutStyle();
    }

    /**
     * 设置顶端布局的样式
     */
    private void setTabLayoutStyle() {
        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {//点击编辑
            @Override
            public void onClick(View v) {
                if (process == 1) {//待录入状态
                    startActivity(new Intent(PreEnterMerchanDetailsActivity.this, PreEntContactInfoActivity.class)
                            .putExtra("APPLYID", applyId)
                            .putExtra("CONTENTBEAN", preEnPieceDetailResponse.getContent())//如果是编辑传递数据到联系人信息界面
                    );
                } else {//其他
                    Utils.showToast(PreEnterMerchanDetailsActivity.this, "无法编辑");
                }
            }
        });
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra("APPLYID");
        process = intent.getIntExtra(PROCESS, 1);
        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");
            preEnPieceDetailRequ = new PreEnPieceDetailRequ();
            preEnPieceDetailRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            preEnPieceDetailRequ.setApplyId(applyId);
            NetAPI.preEnterPieDetailRequest(this, this, preEnPieceDetailRequ);
        }
    }

    private PreEnPieceDetailResponse preEnPieceDetailResponse;

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ELIVE_PARTNER_APPLY_002://获取详情数据
                preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null) {
                    PreEnPieceDetailResponse.ContentBean content = preEnPieceDetailResponse.getContent();
                    ArrayList<BaseFragment> fragments = new ArrayList<>();
                    fragments.add(PreDetailApplyInfoFragment.newInstance(content));
                    fragments.add(PreDetailSettleInfoFragment.newInstance(content));
                    fragments.add(PreDetailMerchantsInfoFragment.newInstance(content));
                    fragments.add(PreDetailBasicInfoFragment.newInstance(content));
                    pagerAdapter = new PreFragmentDetailPagerAdapter(getSupportFragmentManager(), fragments, titles);
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    viewPager.setAdapter(pagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    Log.e(TAG, "preEnPieceDetailResponse为空");
                }
                break;
        }
    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ELIVE_PARTNER_APPLY_002) {
            Utils.showToast(this, statusCode);
        }
    }
}
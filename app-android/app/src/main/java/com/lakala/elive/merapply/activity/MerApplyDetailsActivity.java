package com.lakala.elive.merapply.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.adapter.MyFragmentPagerAdapter;
import com.lakala.elive.merapply.fragment.PrivateInfoFragment;
import com.lakala.elive.merapply.fragment.ApplyInfoFragment;
import com.lakala.elive.merapply.fragment.BaseFragment;
import com.lakala.elive.merapply.fragment.BasicInfoFragment;
import com.lakala.elive.merapply.fragment.MerchantsInfoFragment;
import com.lakala.elive.merapply.fragment.PayeeProveInfoFragment;
import com.lakala.elive.merapply.fragment.PublicInfoFragment;
import com.lakala.elive.merapply.fragment.TerminalInfoFragment;
import com.lakala.elive.preenterpiece.swapmenurecyleview.MyViewPager;
import com.lakala.elive.preenterpiece.swapmenurecyleview.SwipeMenuBuilder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenu;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenuItem;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;
import com.lakala.elive.qcodeenter.QCodePrivatePhotoOcrActivity;
import com.lakala.elive.qcodeenter.QCodePublicPhotoOcrActivity;
import com.lakala.elive.qcodeenter.QCodeRejectMerApplyActivity;
import com.lakala.elive.qcodeenter.fragment.QCodeInfoFragment;

import java.util.ArrayList;

import static com.lakala.elive.merapply.activity.EnterPieceActivity.ACCOUNT_KIND;
import static com.lakala.elive.merapply.activity.InformationInputActivity.APPLY_ID;
import static com.lakala.elive.merapply.fragment.MyMerchantsFragment.PROCESS;

/**
 * 商户申请详情
 * Created by wenhaogu on 2017/1/22.
 */

public class MerApplyDetailsActivity extends BaseActivity implements SwipeMenuBuilder {

    private TabLayout tabLayout;
    private MyViewPager viewPager;
    private String applyId;
    private String[] titles = new String[]{"申请信息", "结算信息", "商户信息", "基本信息", "终端信息"
//            ,"Q码信息"
    };//对公
    private String[] titles2 = new String[]{"申请信息", "结算信息", "附件信息", "基本信息", "终端信息"
//            ,"Q码信息"
    };//对私
    private MyFragmentPagerAdapter pagerAdapter;
    private MerApplyDetailsResp.ContentBean content;
    public static final String CONTENT_BEAN = "contentBean";
    private String accountKind;
    private String process;
    private String applyChanl;
    private String id;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_mer_apply_details);
    }

    @Override
    protected void bindView() {

        tabLayout = findView(R.id.tab_layout);
        viewPager = findView(R.id.view_pager);


        btnCancel.setText("编辑");
        tvTitleName.setText("商户详情");
        iBtnBack.setVisibility(View.VISIBLE);


        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.layout_divider_vertical));

    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(applyId) || TextUtils.isEmpty(accountKind)) {
                    return;
                }
                if (Constants.BMCP_APPLY_STATUS.ENTER.equals(process)) {//待提交状态//编辑
                    if ("03".equals(applyChanl)) {//如果是Q码进件
                        startActivity(new Intent(MerApplyDetailsActivity.this, accountKind.equals("58") ? QCodePrivatePhotoOcrActivity.class : QCodePublicPhotoOcrActivity.class)
                                        .putExtra(ACCOUNT_KIND, accountKind)
                                        .putExtra(APPLY_ID, applyId)
                                        .putExtra(Constants.QCODE_COMPILE_STATUE, "1")//不为空，且为1 可以认为是编辑状态
                                //.putExtra("ContentBean", content)
                        );
                    } else  if ("01".equals(applyChanl)) {
                        startActivity(new Intent(MerApplyDetailsActivity.this, accountKind.equals("58") ? PrivatePhotoOcrActivity.class : PublicPhotoOcrActivity.class)
                                        .putExtra(ACCOUNT_KIND, accountKind)
                                        .putExtra(APPLY_ID, applyId)
                                //.putExtra("ContentBean", content)
                        );
                    }
                } else if (Constants.BMCP_APPLY_STATUS.REBUT.equals(process)) {//驳回状态
                    if ("01".equals(applyChanl)) {//
                        startActivity(new Intent(MerApplyDetailsActivity.this, RejectMerApplyActivity.class)
                                .putExtra("ContentBean", content));
                    }else  if ("03".equals(applyChanl)) {//如果是Q码进件
                        startActivity(new Intent(MerApplyDetailsActivity.this, QCodeRejectMerApplyActivity.class)
                                .putExtra("ContentBean", content));
                         }
                } else {//其他
                    Utils.showToast(MerApplyDetailsActivity.this, "无法编辑");
                }


            }
        });

    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra(MyMerchantsActivity.APPLYID_ID);
        process = intent.getStringExtra(PROCESS);
        applyChanl = intent.getStringExtra("APPLYCHANNEL");//区分是Q码进件还是其他进件,如果是Q码进件，显示Q码信息列表
        if (
//                "03".equals(applyChanl)&&
                "STORAGED".equals(process)) {//并且是审核通过显示
            titles = new String[]{"申请信息", "结算信息", "商户信息", "基本信息", "终端信息", "Q码信息"
            };//对公
            titles2 = new String[]{"申请信息", "结算信息", "附件信息", "基本信息", "终端信息", "Q码信息"
            };//对私
        }
        if (Constants.BMCP_APPLY_STATUS.ENTER.equals(process)) {//待提交状态
            btnCancel.setVisibility(View.VISIBLE);
        } else if (Constants.BMCP_APPLY_STATUS.REBUT.equals(process)) {//驳回状态
            btnCancel.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }
    }

    private ArrayList<BaseFragment> fragments;

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            content = ((MerApplyDetailsResp) obj).getContent();
            accountKind = content.getMerOpenInfo().getAccountKind();
            EnterPieceActivity.setAccountKind(accountKind);
            id = content.getMerApplyInfo().getResultId();
            fragments = new ArrayList<>();
            fragments.add(ApplyInfoFragment.newInstance(content));
            if (accountKind.equals("58")) {//对私
                fragments.add(PrivateInfoFragment.newInstance(content));
                fragments.add(PayeeProveInfoFragment.newInstance(content));
            } else {//对公
                fragments.add(PublicInfoFragment.newInstance(content));
                fragments.add(MerchantsInfoFragment.newInstance(content));
            }
            fragments.add(BasicInfoFragment.newInstance(content));
            fragments.add(TerminalInfoFragment.newInstance(content));

            if (
//                    "03".equals(applyChanl)&&
                    "STORAGED".equals(process)) {//审核通过的商户
                fragments.add(QCodeInfoFragment.newInstance(content));
            }
            pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, accountKind.equals("58") ? titles2 : titles);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            viewPager.setNoScroll(true);//设置viewPage不能滑动
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);

        }
    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        }
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
        menuView.setOnMenuItemClickListener(QCodeInfoFragment.mOnSwipeItemClickListener);
        return menuView;
    }
}

package com.lakala.shoudan.activity.myaccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.register.UpdateMerchantInfoActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.NavigationBar;

import java.util.HashMap;
import java.util.Map;


/**
 * 商户个人信息详情
 *
 * @author More
 */
public class AccountInfoActivity extends AppBaseActivity {
    private MerchantInfo merchantInfo;
    private View ivPass;
    private TextView rejectInfoTv;
    private String rejectInfoStr;
    private LinearLayout llUpgradeInfo2;
    private String firstUpgradeStatus;
    private String secondUpgradeStatus;
    private TextView tvMechantUpgrade;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_account_info);
        initUI();
    }

    protected void initUI() {
        navigationBar.setTitle("用户信息");
        navigationBar.setActionBtnText("修改");
        initView();
        initData();
        showMerchantInfo();
        showUpdateInfo();
        showStatusHint();
    }


    public void initData() {
        firstUpgradeStatus = getIntent().getStringExtra("firstUpgradeStatus");
        secondUpgradeStatus = getIntent().getStringExtra("secondUpgradeStatus");
        merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
    }

    private void initView() {
        ivPass = findViewById(R.id.iv_pass);
        llUpgradeInfo2 = (LinearLayout) findViewById(R.id.ll_upgrade_infos);
        rejectInfoTv = (TextView) findViewById(R.id.merchant_info_error);
        tvMechantUpgrade = (TextView) findViewById(R.id.tv_mechant_upgrade);
        tvMechantUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MerchantUpdateActivity.start(context, false);
                ;
            }
        });
        view = (View) findViewById(R.id.view);
    }

    @Override
    protected void onResume() {
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (merchantInfo.getMerchantStatus() == MerchantStatus.FAILURE) {
            //开通审核未通过
            showModifyMrchInfoBtn(null);
        } else if (TextUtils.equals(firstUpgradeStatus, UpgradeStatus.FAILURE.name())
                && TextUtils.equals(secondUpgradeStatus, UpgradeStatus.NONE.name())) {
            //一次升级驳回
            showModifyMrchInfoBtn("1");
        } else if (TextUtils.equals(secondUpgradeStatus, UpgradeStatus.FAILURE.name())) {
            //二次升级驳回
            showModifyMrchInfoBtn("2");
        } else {
            navigationBar.setActionBtnEnabled(false);
            navigationBar.setActionBtnVisibility(View.INVISIBLE);

        }
        super.onResume();

    }

    /**
     * 显示修改资料按钮
     */
    private void showModifyMrchInfoBtn(final String upgrade) {
        navigationBar.setActionBtnEnabled(true);
        navigationBar.setActionBtnVisibility(View.VISIBLE);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {

            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    //修改资料
                    if (TextUtils.isEmpty(upgrade)) {
                        Intent intent = new Intent(AccountInfoActivity.this, UpdateMerchantInfoActivity.class);
                        intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
                        startActivity(intent);
                        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_Merchant_Info_Change, context);
                    } else {
                        MerchantUpdateActivity.start(context, TextUtils.equals(upgrade, "1"));
                        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_Merchant_Info_Upgtate, context);
                    }
                }
                if (navBarItem == NavigationBar.NavigationBarItem.back) {//退出登录
                    finish();
                }
            }
        });
    }

    /**
     * 显示商户信息
     */
    Map<String, String> errField;

    private void showMerchantInfo() {
        //插入需要显示的内容
        final LabelEditText businessName = (LabelEditText) findViewById(R.id.merchantAddress);

        LabelEditText businessNo = (LabelEditText) findViewById(R.id.merchant_no);
        LabelEditText merchantName = (LabelEditText) findViewById(R.id.merchantName);
        LabelEditText realName = (LabelEditText) findViewById(R.id.personInCharge);
        LabelEditText idCardNo = (LabelEditText) findViewById(R.id.idCardNo);
        LabelEditText mobilephone = (LabelEditText) findViewById(R.id.telephone);
        LabelEditText email = (LabelEditText) findViewById(R.id.email);
        LabelEditText bankName = (LabelEditText) findViewById(R.id.collectionBank);
        LabelEditText accountNo = (LabelEditText) findViewById(R.id.collectionAccount);
        LabelEditText accountName = (LabelEditText) findViewById(R.id.collectionName);
        setLabelTextMore(businessNo);
        setLabelTextMore(merchantName);
        setLabelTextMore(businessName);
        setLabelTextMore(realName);
        setLabelTextMore(idCardNo);
        setLabelTextMore(mobilephone);
        setLabelTextMore(email);
        setLabelTextMore(bankName);
        setLabelTextMore(accountNo);
        setLabelTextMore(accountName);

        errField = new HashMap<String, String>();
        //2.5.2修改显示
        merchantName.setTag(new String[]{"10000"});
        businessName.setTag(new String[]{"10001"});
        email.setTag(new String[]{"10002"});
        bankName.setTag(new String[]{"10003"});
        accountNo.setTag(new String[]{"10003"});
        accountName.setTag(new String[]{"10003"});
        if (!merchantInfo.getUser().isIdInfoValid()) {
            realName.setTag(new String[]{"10003"});
        }
        idCardNo.setTag(new String[]{"10004"});
//        mobilephone.setTag("10005");
        //申请人资料
        setLabelText(merchantName, merchantInfo.getBusinessName());
        setLabelText(businessNo, merchantInfo.getMerNo());
        setLabelText(businessName, merchantInfo.getBusinessAddress().getFullDisplayAddress());

        setLabelText(realName, Util.formatUserName(merchantInfo.getUser().getRealName()));
        setLabelText(idCardNo, Util.formatIDCardNum(merchantInfo.getUser().getIdCardInfo().getIdCardId()));
        setLabelText(mobilephone, Util.formatPhoneStart3End4(merchantInfo.getUser().getMobileNum()));
        setLabelText(email, Util.formatEmailN1(merchantInfo.getEmail()));
        //收款账户
        setLabelText(bankName, merchantInfo.getBankName());
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (merchantInfoData.getAccountType() == AccountType.PUBLIC) {
            setLabelText(accountNo, Util.formatCompanyAccount(merchantInfo.getAccountNo()));
        } else {
            setLabelText(accountNo, StringUtil.formatCardNumberN6S4N4(merchantInfo.getAccountNo()));
        }
        setLabelText(accountName, Util.formatUserName(merchantInfo.getAccountName()));
    }

    /**
     * 显示升级信息
     */
    public void showUpdateInfo() {
        boolean showUpgradeInfo = false;

        if (merchantInfo.getMerchantStatus() == MerchantStatus.COMPLETED &&
                !TextUtils.equals(firstUpgradeStatus, UpgradeStatus.NONE.name())) {
            showUpgradeInfo = true;
        }

        if (showUpgradeInfo) {
            llUpgradeInfo2.setVisibility(View.VISIBLE);
            LabelEditText letBranch = (LabelEditText) findViewById(R.id.let_branch);
            LabelEditText letPhoto = (LabelEditText) findViewById(R.id.let_photo);
            LabelEditText letLicenseNumber = (LabelEditText) findViewById(R.id.let_license_number);
            setLabelTextMore(letBranch);
            setLabelTextMore(letPhoto);
            setLabelTextMore(letLicenseNumber);

            String bankName = getIntent().getStringExtra("openBankBranchName");
            String busLicenceCode = getIntent().getStringExtra("busLicenceCode");

            setLabelText(letPhoto, "已上传");

            if (secondUpgradeStatus.equals(UpgradeStatus.NONE.name())) {
                letLicenseNumber.setVisibility(View.GONE);
                letBranch.setVisibility(View.GONE);
            } else {
                setLabelText(letBranch, bankName);
                setLabelText(letLicenseNumber, Util.formatLicense1End1(busLicenceCode));
            }
        }


    }

    /**
     * 显示商户状态提示语
     */
    public void showStatusHint() {
        rejectInfoStr = getString(R.string.acc_data_not_passed);
        ivPass.setVisibility(View.INVISIBLE);

        if (merchantInfo.getMerchantStatus() != MerchantStatus.COMPLETED) {
            ShoudanStatisticManager.getInstance()
                    .onEvent(ShoudanStatisticManager.ACCOUNT_INFO_MERCHANT, context);

            switch (merchantInfo.getMerchantStatus()) {

                case NONE:
                    break;

                case PROCESSING:
                    rejectInfoStr = "您已开通商户，请等待审核";
                    break;

                case FROZEN:
                    rejectInfoStr = "账户被冻结。";
                    break;

                case FAILURE:
                    rejectInfoStr = "很抱歉，您提交的资料有误，未能通过审核。请将标成橘色内容部分修改后重新提交审核。";
                  /*  tvChangeInfo.setVisibility(View.VISIBLE);
                    tvChangeInfo.setText("银行卡非本人银行卡");
                    //((ImageView)ivPass).setImageResource(R.drawable.pic_pass_through);
                    ivPass.setVisibility(View.VISIBLE);*/
                    break;
                case COMPLETED:
                    rejectInfoStr = "您的商户审核已通过";
                    ((ImageView) ivPass).setImageResource(R.drawable.pic_pass_through);
                    ivPass.setVisibility(View.VISIBLE);
                    break;
            }

        } else {
            ShoudanStatisticManager.getInstance()
                    .onEvent(ShoudanStatisticManager.ACCOUNT_INFO_UPGRADE, context);
            view.setVisibility(View.GONE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_linearLayout);
            int padding = (int) getDP(context, 15);
            linearLayout.setPadding(padding, 0, padding, padding);
            boolean flag = true;
            //一次升级提示语
            if (TextUtils.equals(secondUpgradeStatus, UpgradeStatus.NONE.name())) {
                flag = false;
            }
            initFlag(flag);
        }
        rejectInfoTv.setText(rejectInfoStr);
    }

    private void initFlag(boolean flag) {
        String status;
        if (!flag) {
            status = firstUpgradeStatus;
        } else {
            status = secondUpgradeStatus;
        }
        //一级升级
        switch (UpgradeStatus.valueOf(status)) {
            case PROCESSING:
                rejectInfoStr = "您正在申请商户升级，请耐心等待审核";
                break;
            case FAILURE:
                rejectInfoStr = "很抱歉，您提交的商户升级资料有误，未能通过审核，请修改资料后重新提交审核";
                break;
            case COMPLETED:
                if (!flag) {
                    rejectInfoStr = "恭喜！您的商户升级成功，提交更多资料，还可以继续升级，享受更高额度哦";
                    tvMechantUpgrade.setVisibility(View.VISIBLE);
                } else {
                    rejectInfoStr = "恭喜！您的商户已升级成功";
                }
                ((ImageView) ivPass).setImageResource(R.drawable.already_upgrade);
                ivPass.setVisibility(View.VISIBLE);
                break;
            case NONE://不可能是二次升级
                rejectInfoStr = "您的商户审核已通过";
                ((ImageView) ivPass).setImageResource(R.drawable.pic_pass_through);
                ivPass.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * 在label添加一个：
     *
     * @param labelText
     */
    private void setLabelTextMore(LabelEditText labelText) {
        if (labelText == null) {
            return;
        }
        labelText.getLabelView().append("：");
    }

    private void setLabelText(LabelEditText labelEditText, final String value) {
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        labelEditText.setLabelGravity(Gravity.RIGHT);
        final EditText editText = labelEditText.getEditText();
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setText(value);
        editText.setSingleLine(false);
        String[] tags = (String[]) labelEditText.getTag();
        boolean isRight = true;
        if (tags != null) {
            for (String tag : tags) {
                if (merchantInfo.has(tag)) {
                    isRight = false;
                    break;
                } else {
                    isRight = true;
                }
            }
        } else {
            isRight = true;
        }
        if (!isRight) {
            editText.setTextColor(getResources().getColor(R.color.orange));
        } else {
            editText.setTextColor(getResources().getColor(R.color.black));
        }

    }

    public static float getDP(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }

}

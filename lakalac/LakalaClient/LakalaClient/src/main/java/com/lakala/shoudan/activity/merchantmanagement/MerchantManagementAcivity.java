package com.lakala.shoudan.activity.merchantmanagement;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerLevelInfo;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HJP on 2015/12/1.
 */
public class MerchantManagementAcivity extends AppBaseActivity {
    /**
     * 升档
     */
    private String statusValue;
    private String statusBigmoneyValue;
    private String statusMer_Lv;//商户级别
    private String statusMer_Chn_Self;//是否自主进件
    private boolean isShowUpgrade;
    private MerLevelInfo levelInfo = null;
    private boolean thirdUpgrade;//是否跑分
    private String level;
    private boolean showHint;
    private ImageView ivLevel;
    private TextView tvLevel;
    private TextView tv_succuss;
    private TextView jbsm;
    private LinearLayout tvMechantUpgrade;
    private RelativeLayout llMsgType;
    private RelativeLayout llSmType;
    private RelativeLayout llJsType;
    private RelativeLayout llDType;
    private RelativeLayout lLcType;
    private String rejectInfoStr;
    private MerchantInfo merchantInfo;
    private String firstUpgradeStatus;
    private String secondUpgradeStatus;
    private String lcStas;
    private String tag;
    private String deskState;
    private String jsdzState;
    private UpgradeStatus secondStatus;
    private UpgradeStatus firstStatus;
    private String smType;
    private int[] llHints = {R.id.hint0, R.id.hint1, R.id.hint2, R.id.hint3};
    private int[] imgHints = {R.id.iv_arrow_go, R.id.iv_arrow_go1, R.id.iv_arrow_go2, R.id.iv_arrow_go3};
    private String[] typesShoudan = {ShoudanStatisticManager.Merchant_ll_msgType, ShoudanStatisticManager.Merchant_ll_jsType, ShoudanStatisticManager.Merchant_ll_smType, ShoudanStatisticManager.Merchant_ll_deType};
    private boolean[] isHint = {false, false, false, false};
    public boolean upDate = false;
    public boolean thirdUpdate = false;
    private MerchantFragment fragment;
    private String smTag = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_management);
        dealMerchState();// 处理商户状态
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (smTag.equals("1"))
            checkProgress();
    }

    private void checkProgress() {
        if (context.getProgressDialog() != null && context.isProgressisShowing()) {
            context.hideProgressDialog();
            smTag = "0";
        }
    }

    public void initView(String data) {
        try {
            if (TextUtils.isEmpty(data)) {
                return;
            }
            JSONObject jsonObject = new JSONObject(data);
            levelInfo = MerLevelInfo.newInstance(jsonObject);
            String levelMy = jsonObject.optString("merLevel", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView tvDebitPer = (TextView) findViewById(R.id.tv_single_account_bankcard);
        TextView tvDebitDay = (TextView) findViewById(R.id.tv_single_day_bankcard);
        TextView tvDebitMonth = (TextView) findViewById(R.id.tv_single_month_bankcard);
        TextView tvCreditPer = (TextView) findViewById(R.id.tv_single_account_creditcard);
        TextView tvCreditDay = (TextView) findViewById(R.id.tv_single_day_creditcard);
        TextView tvCreditMonth = (TextView) findViewById(R.id.tv_single_month_creditcard);

        //立即提款
        TextView tv_single_account_creditcard2 = (TextView) findViewById(R.id.tv_single_account_creditcard2);
        TextView tv_single_day_creditcard2 = (TextView) findViewById(R.id.tv_single_day_creditcard2);

        //扫码收款
        TextView tv_single_account_creditcard1 = (TextView) findViewById(R.id.tv_single_account_creditcard1);
        TextView tv_single_day_creditcard1 = (TextView) findViewById(R.id.tv_single_day_creditcard1);
        TextView tv_single_month_creditcard1 = (TextView) findViewById(R.id.tv_single_month_creditcard1);

        //大额收款
        TextView tv_single_account_creditcard31 = (TextView) findViewById(R.id.tv_single_account_creditcard31);
        TextView tv_single_day_creditcard31 = (TextView) findViewById(R.id.tv_single_day_creditcard31);
        TextView tv_single_day_bankcard31 = (TextView) findViewById(R.id.tv_single_day_bankcard31);
        TextView tv_single_month_bankcard31 = (TextView) findViewById(R.id.tv_single_month_bankcard31);
        TextView tv_single_account_32 = (TextView) findViewById(R.id.tv_single_account_32);
        TextView tv_account_32 = (TextView) findViewById(R.id.tv_account_32);
        TextView tv_single_day_creditcard32 = (TextView) findViewById(R.id.tv_single_day_creditcard32);
        TextView tv_single_month_creditcard32 = (TextView) findViewById(R.id.tv_single_month_creditcard32);

        ivLevel = (ImageView) findViewById(R.id.xqq);
        tvLevel = (TextView) findViewById(R.id.tv_lv);
        if (levelInfo == null) {
            return;
        }

        tvDebitPer.setText(getShowAmount(levelInfo.getDebitPerLimit(), ""));
        tvDebitDay.setText(getShowAmount(levelInfo.getDebitDayLimit(), ""));
        tvDebitMonth.setText(getShowAmount(levelInfo.getDebitMonthLimit(), ""));
        tvCreditPer.setText(getShowAmount(levelInfo.getCreditPerLimit(), ""));
        tvCreditDay.setText(getShowAmount(levelInfo.getCreditDayLimit(), ""));
        tvCreditMonth.setText(getShowAmount(levelInfo.getCreditMonthLimit(), ""));


        //立即提款
        tv_single_account_creditcard2.setText(getShowAmount(levelInfo.getdPerMaxLimit(), ""));
        tv_single_day_creditcard2.setText(getShowAmount(levelInfo.getdDayMaxLimit(), ""));

        //扫码收款
        tv_single_account_creditcard1.setText(getShowAmount(levelInfo.getWechatPerLimit(), ""));
        tv_single_day_creditcard1.setText(getShowAmount(levelInfo.getWechatDayLimit(), ""));
        tv_single_month_creditcard1.setText(getShowAmount(levelInfo.getWechatMonthLimit(), ""));

        //大额收款
        tv_single_account_creditcard31.setText(getShowAmount(levelInfo.getBigDebitPerLimitMin(), "BigDebitPerLimitMin"));
        tv_single_day_creditcard31.setText(getShowAmount(levelInfo.getBigDebitPerLimit(), ""));
        tv_single_account_32.setText(getShowAmount(levelInfo.getBigCreditPerLimitMin(), "BigCreditPerLimitMin"));
        tv_account_32.setText(getShowAmount(levelInfo.getBigCreditPerLimit(), ""));
        tv_single_day_bankcard31.setText(getShowAmount(levelInfo.getBigDebitDayLimit(), ""));
        tv_single_month_bankcard31.setText(getShowAmount(levelInfo.getBigDebitMonthLimit(), ""));
        tv_single_day_creditcard32.setText(getShowAmount(levelInfo.getBigCreditDayLimit(), ""));
        tv_single_month_creditcard32.setText(getShowAmount(levelInfo.getBigCreditMonthLimit(), ""));


        level = levelInfo.getMerLevel();
        setLevel(level);
        if (thirdUpdate)
            fragment.setLevel(level);
        tv_succuss = (TextView) findViewById(R.id.tv_succuss);
    }

    private void setLevel(String levelName) {
        if (levelName == null || levelName.equals("") || !levelName.startsWith("V")) {
            levelName = "MV";
        }
        Level lv = Level.valueOf(levelName + "HAT");
        for (Level v : Level.values()) {
            if (lv == v) {
                setLv(lv);
            }
        }
    }

    View.OnTouchListener settingOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View singleLineTextView, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (singleLineTextView.getId() == R.id.ll_cuccuss_change)
                        findViewById(R.id.merchant_up).setBackgroundColor(getResources().getColor(R.color.gray));
                    else
                        singleLineTextView.setBackgroundColor(getResources().getColor(R.color.gray));
                    break;
                case MotionEvent.ACTION_UP:
                    if (singleLineTextView.getId() == R.id.ll_cuccuss_change)
                        findViewById(R.id.merchant_up).setBackgroundColor(getResources().getColor(R.color.white));
                    else
                        singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    settingItemClick(singleLineTextView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (singleLineTextView.getId() == R.id.ll_cuccuss_change)
                        findViewById(R.id.merchant_up).setBackgroundColor(getResources().getColor(R.color.white));
                    else
                        singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    private void setLv(Level lv) {
        ivLevel.setImageResource(lv.getLvImg());
        tvLevel.setText(lv.getLvName());
    }

    private String getShowAmount(String str1, String tag) {
        if (TextUtils.isEmpty(str1) || "null".equals(str1)) {
            if (!tag.equals(""))
                return "";
            else
                return "";
        } else if (str1.equals("0")) {
            if (!tag.equals(""))
                return "0元";
            else
                return "不限";
        } else {
            if (str1.contains("."))
                str1 = str1.substring(0, str1.indexOf("."));
            if (str1.length() > 2) {
                str1 = new StringBuilder(str1).reverse().toString();     //先将字符串颠倒顺序
                String str2 = "";
                for (int i = 0; i < str1.length(); i++) {
                    if (i * 3 + 3 > str1.length()) {
                        str2 += str1.substring(i * 3, str1.length());
                        break;
                    }
                    str2 += str1.substring(i * 3, i * 3 + 3) + ",";
                }
                if (str2.endsWith(",")) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                //最后再将顺序反转过来
                str1 = new StringBuilder(str2).reverse().toString();
            }
            return str1;
        }
    }

    /**
     * 针对商户状态处理UI merchantStatus
     */
    public void dealMerchState() {
        tvMechantUpgrade = (LinearLayout) findViewById(R.id.ll_cuccuss_change);
        // 商户状态,0:未开通;1:已开通;2:冻结;3:审核未通过;4:修改资料并且审核通过
        fragment = new MerchantFragment();
        merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        switch (merchantInfo.getMerchantStatus()) {
            case FAILURE:
            case PROCESSING:
            case FROZEN:
            case COMPLETED: {
                initData();
                fragment.setIsNeedUpdate(isShowUpgrade);
                fragment.setThirdUpgrade(thirdUpgrade);
                Bundle bundle = new Bundle();
                bundle.putString("level", level);
                bundle.putString("firstUpgradeStatus", firstUpgradeStatus);
                bundle.putString("secondUpgradeStatus", secondUpgradeStatus);
                bundle.putString("busLicenceCode", getIntent().getStringExtra("busLicenceCode"));
                bundle.putString("openBankBranchName", getIntent().getStringExtra("openBankBranchName"));
                bundle.putString("secondUpgradeStatus", secondUpgradeStatus);
                bundle.putBoolean("showHint", showHint);
                fragment.setArguments(bundle);
                findViewById(R.id.fl_merchant_container).setBackgroundColor(getResources().getColor(R.color.white));
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_merchant_container, fragment).commit();

                showStatusHint();
                break;
            }

            case NONE: {
                NoneMerchantFragment noneMerchantFragment = new NoneMerchantFragment();
                Bundle bundle = new Bundle();
                bundle.putString("mobileNum", merchantInfo.getUser().getMobileNum());
                noneMerchantFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_merchant_container, noneMerchantFragment).commit();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_merchant_level);
                linearLayout.setVisibility(View.GONE);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 显示商户状态提示语
     */
    public void showStatusHint() {
        //rejectInfoStr= getString(R.string.acc_data_not_passed);
        rejectInfoStr = "";

        if (merchantInfo.getMerchantStatus() != MerchantStatus.COMPLETED) {
            ShoudanStatisticManager.getInstance()
                    .onEvent(ShoudanStatisticManager.ACCOUNT_INFO_MERCHANT, context);

            switch (merchantInfo.getMerchantStatus()) {

                case NONE:
                    rejectInfoStr = "您要先开通拉卡拉收款服务才能使用该功能哦，快去开通吧！";
                    tv_succuss.setVisibility(View.GONE);
                    break;

                case PROCESSING:
                    rejectInfoStr = "您已开通商户，请等待审核";
                    break;

                case FROZEN:
                    rejectInfoStr = "账户被冻结。";
                    break;

                case FAILURE:
                    rejectInfoStr = "很抱歉，您提交的资料有误，未能通过审核。请将标成橘色内容部分修改后重新提交审核。";
                    tv_succuss.setVisibility(View.GONE);
                  /*  tvChangeInfo.setVisibility(View.VISIBLE);
                    tvChangeInfo.setText("银行卡非本人银行卡");
                    //((ImageView)ivPass).setImageResource(R.drawable.pic_pass_through);
                    ivPass.setVisibility(View.VISIBLE);*/
                    break;
                case COMPLETED:
                    rejectInfoStr = "您的商户审核已通过";
                    break;
            }

        } else {
            if (level == null || level.equals("") || !level.startsWith("V")) {
                level = "MV";
            }
            Level lv = Level.valueOf(level);
            if (lv == Level.V1 || lv == Level.V2 || lv == Level.MV) {
                tvMechantUpgrade.setVisibility(View.VISIBLE);
                if (firstStatus == UpgradeStatus.NONE) {
                    tag = "1";
                } else if (firstStatus == UpgradeStatus.PROCESSING || secondStatus == UpgradeStatus.PROCESSING) {
                    tag = "2";
                } else if (firstStatus == UpgradeStatus.FAILURE || secondStatus == UpgradeStatus.FAILURE) {
                    tv_succuss.setVisibility(View.VISIBLE);
                    if (firstStatus == UpgradeStatus.FAILURE) {
                        tag = "3";
                        tv_succuss.setText("您的资料审核未通过，请重新提交");
                    } else {
                        tag = "6";
                        tv_succuss.setText("您的资料审核未通过，请重新提交");
                    }
                    tv_succuss.setTextColor(getResources().getColor(R.color.orange_FF8308));
                } else if (TextUtils.equals(firstUpgradeStatus, UpgradeStatus.COMPLETED.name())
                        && TextUtils.equals(secondUpgradeStatus, UpgradeStatus.NONE.name())) {
                    tag = "5";
                } else if (TextUtils.equals(secondUpgradeStatus, UpgradeStatus.COMPLETED.name())) {
                    tvMechantUpgrade.setVisibility(View.GONE);
                }
            } else {
                if (thirdUpgrade && level != "MV") {
                    tag = "4";
                    tvMechantUpgrade.setVisibility(View.VISIBLE);
                }

            }
        }

        //刷卡收款
        llMsgType = (RelativeLayout) findViewById(R.id.ll_msgType);
        TextView iv_msgType = (TextView) findViewById(R.id.iv_msgType);
        llMsgType.setOnClickListener(this);
        if ((merchantInfo.getMerchantStatus() == MerchantStatus.FROZEN) || (merchantInfo.getMerchantStatus() == MerchantStatus.NONE)) {
            iv_msgType.setTextColor(getResources().getColor(R.color.orange_FF8308));
            iv_msgType.setText("未开通");
        } else {
            // findViewById(R.id.iv_arrow_go).setVisibility(View.INVISIBLE);
        }
        //扫码收款
        smType = getIntent().getStringExtra("c2bStatus");
        llSmType = (RelativeLayout) findViewById(R.id.ll_smType);
        TextView iv_smType = (TextView) findViewById(R.id.iv_smType);
        llSmType.setOnClickListener(this);
        if (!smType.equals("COMPLETED")) {
            iv_smType.setTextColor(getResources().getColor(R.color.orange_FF8308));
            iv_smType.setText("未开通");
        } else if (merchantInfo.getMerchantStatus() == MerchantStatus.COMPLETED) {
            // findViewById(R.id.iv_arrow_go1).setVisibility(View.INVISIBLE);
        }
        //急速到账
        jsdzState = getIntent().getStringExtra("speedStatus");
        llJsType = (RelativeLayout) findViewById(R.id.ll_jsType);
        TextView iv_jsType = (TextView) findViewById(R.id.iv_jsType);
        if (!jsdzState.equals("COMPLETED")) {
            DrawButtonClickListener2 listener = new DrawButtonClickListener2(context);
            DoEnum.Do.setData(null, true, false);
            llJsType.setOnClickListener(listener);
            listener.setStatistic(ShoudanStatisticManager.Merchant_jsType);
            iv_jsType.setTextColor(getResources().getColor(R.color.orange_FF8308));
            iv_jsType.setText("未开通");
        } else {
            llJsType.setOnClickListener(this);
            //  findViewById(R.id.iv_arrow_go2).setVisibility(View.INVISIBLE);
        }

        //大额收款
        deskState = getIntent().getStringExtra("bStatus");
        llDType = (RelativeLayout) findViewById(R.id.ll_deType);
        TextView iv_deType = (TextView) findViewById(R.id.iv_deType);
        llDType.setOnClickListener(this);
        if (!deskState.equals("SUCCESS")) {
            iv_deType.setTextColor(getResources().getColor(R.color.orange_FF8308));
            iv_deType.setText("未开通");
        } else {
            // findViewById(R.id.iv_arrow_go3).setVisibility(View.INVISIBLE);
        }

        jbsm = (TextView) findViewById(R.id.jbsm);
        jbsm.setOnClickListener(this);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.merchant_management);
        int[] settingIds = new int[]{
                R.id.merchant_up, R.id.ll_msgType, R.id.ll_smType, R.id.ll_cuccuss_change,
                R.id.ll_deType, R.id.hint31, R.id.hint32, R.id.hint2, R.id.hint1, R.id.hint02, R.id.hint01};

        for (int i : settingIds) {
            findViewById(i).setOnTouchListener(settingOnTouchListener);
        }
        setHint(0);
    }

    private void initData() {
        firstUpgradeStatus = getIntent().getStringExtra("firstUpgradeStatus");
        secondUpgradeStatus = getIntent().getStringExtra("secondUpgradeStatus");
        isShowUpgrade = getIntent().getBooleanExtra("isShowUpgrade", false);
        thirdUpgrade = getIntent().getBooleanExtra("thirdUpgrade", false);
        String data = getIntent().getStringExtra(UniqueKey.MER_LEVEL_INFO);
        showHint = getIntent().getBooleanExtra("showHint", false);
        firstStatus = UpgradeStatus.valueOf(firstUpgradeStatus);
        secondStatus = UpgradeStatus.valueOf(secondUpgradeStatus);
        initView(data);
    }

    private void settingItemClick(View view) {
        switch (view.getId()) {
          /*  case R.id.jbsm:
                ProtocalActivity.open(context, ProtocalType.LEVEL_RULES);
                ShoudanStatisticManager.getInstance() .onEvent(ShoudanStatisticManager.Level_Rules, context);
                break;*/
            case R.id.ll_msgType:
                if ((merchantInfo.getMerchantStatus() == MerchantStatus.FROZEN) || (merchantInfo.getMerchantStatus() == MerchantStatus.NONE)) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Collection, context);
                    CollectionEnum.Colletcion.setData(null, false);
                    BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                } else {
                    setHint(0);
                }

                break;
            case R.id.ll_smType:
                if (!smType.equals("COMPLETED")) {
                    smTag = "1";
                    if (CommonUtil.isMerchantCompleted(context)) {
                        BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                        barcodeAccessManager.check(true, true);
                        PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_smType, context);
                    }
                } else if (merchantInfo.getMerchantStatus() == MerchantStatus.COMPLETED) {
                    setHint(1);
                }

                break;
            case R.id.ll_jsType:
                if (!jsdzState.equals("COMPLETED")) {

                } else {
                    setHint(2);
                }

                break;
            case R.id.ll_deType:
                if (!deskState.equals("SUCCESS")) {
                    //大额收款deskState;//大额收款1不支持 2支持/未申请/失败 3处理中 4成功 5关闭 6未知，接口没有获取到
                    if (CommonUtil.isMerchantCompleted(context)) {
                        LargeAmountAccessManager largeAmountAccessManager = new LargeAmountAccessManager((AppBaseActivity) context);
                        largeAmountAccessManager.check();

                    }
                } else {
                    setHint(3);
                }

                break;
            case R.id.ll_cuccuss_change:
                Intent intent2 = new Intent(context, MerchantUpdateActivity.class);
                switch (tag) {
                    case "1":
                        upDate = true;
                        intent2.putExtra("isUpgradeFirst", true);
                        intent2.putExtra("fromMerchant", "fromMerchant");
                        startActivity(intent2);
//                        ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.UPDATE_STATUS);
                        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.MENU_MERCHANT_UPdate, context);
                        break;
                    case "2":
                        String msg = "您的资料正在审核中，请耐心等待";
                        DialogCreator.createOneConfirmButtonDialog(context, "确定", msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        break;
                    case "3":
                        upDate = true;
                        intent2.putExtra("isUpgradeFirst", true);
                        intent2.putExtra("fromMerchant", "fromMerchant");
                        startActivity(intent2);
                        PublicToEvent.MerchantlEvent(ShoudanStatisticManager.MENU_MERCHANT_UPdate, context);
                        // MerchantUpdateActivity.start(context,true);
                        break;
                    case "4":
                        thirdUpgrade();
                        break;
                    case "5":
                        upDate = true;
                        intent2.putExtra("isUpgradeFirst", false);
                        intent2.putExtra("fromMerchant", "fromMerchant");
                        startActivity(intent2);
                        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.MENU_MERCHANT_UPdate, context);
                        break;
                    case "6":
                        upDate = true;
                        intent2.putExtra("isUpgradeFirst", false);
                        intent2.putExtra("fromMerchant", "fromMerchant");
                        startActivity(intent2);
                        PublicToEvent.MerchantlEvent(ShoudanStatisticManager.MENU_MERCHANT_UPdate, context);
                        // MerchantUpdateActivity.start(context,false);
                        break;
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.jbsm:
                ProtocalActivity.open(context, ProtocalType.LEVEL_RULES);
                PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_Level_Rules, context);
                break;
           /*    case R.id.ll_msgType:
                if ((merchantInfo.getMerchantStatus()==MerchantStatus.FROZEN)||(merchantInfo.getMerchantStatus()==MerchantStatus.NONE)){
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Collection, context);
                    CollectionEnum.Colletcion.setData(null, false);
                    BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                }else  {
                    setHint(0);
                }

                break;
            case R.id.ll_smType:
                if (!smType.equals("COMPLETED")){
                    if (CommonUtil.isMerchantCompleted(context)) {
                        BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                        barcodeAccessManager.check(true, true);
                    }
                }else  if (merchantInfo.getMerchantStatus() == MerchantStatus.COMPLETED){
                    setHint(1);
                }

                break;

            case R.id.ll_deType:
                if (!deskState.equals("SUCCESS")){
                    //大额收款deskState;//大额收款1不支持 2支持/未申请/失败 3处理中 4成功 5关闭 6未知，接口没有获取到
                    if (CommonUtil.isMerchantValid(context)) {
                        LargeAmountAccessManager largeAmountAccessManager = new LargeAmountAccessManager((AppBaseActivity) context);
                        largeAmountAccessManager.check();
                    }
                }else  {
                    setHint(3);
                }

                break;*/
            case R.id.ll_jsType:
                if (!jsdzState.equals("COMPLETED")) {
                } else {
                    setHint(2);
                }

                break;
        }
    }

    private void setHint(int t) {
        for (int i = 0; i < 4; i++) {
            if (i == t) {
                if (isHint[i]) {
                    findViewById(llHints[i]).setVisibility(View.GONE);
                    ((ImageView) findViewById(imgHints[i])).setImageResource(R.drawable.btn_arrow_go_gray);
                    isHint[i] = false;
                } else {
                    findViewById(llHints[i]).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(imgHints[i])).setImageResource(R.drawable.btn_arrow_disable_up_grey);
                    isHint[i] = true;
                    PublicToEvent.MerchantlEvent( typesShoudan[i], context);
                }
            } else {
                if (isHint[i]) {
                    findViewById(llHints[i]).setVisibility(View.GONE);
                    ((ImageView) findViewById(imgHints[i])).setImageResource(R.drawable.btn_arrow_go_gray);
                    isHint[i] = false;
                }
            }

        }
    }

    private void showNotSupportDialog(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("帮助", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ProtocalActivity.open(context, ProtocalType.LARGE_AMOUNT_RULES);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void thirdUpgrade() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPGRADE_THIRD);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {
                    hideProgressDialog();
                    ShoudanStatisticManager.getInstance()
                            .onEvent(ShoudanStatisticManager.THIRD_UPGRADE_SUCCESS, context);
                    DialogCreator.createOneConfirmButtonDialog(context, "确定", "您的等级信息更新成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            refreshThird();
                        }
                    }).show();

                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
            }
        });
        businessRequest.execute();
    }

    private void refreshThird() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.QUERY_MerLevelAndLimit);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    thirdUpdate = true;
                    thirdUpgrade = true;
                    try {
                        jsonObject = new JSONObject(data);
                        //tvLevel.setText(jsonObject.optString("merLevel"));
                        //tv_succuss.setTextColor(getResources().getColor(R.color.gray_999999));
                        tvMechantUpgrade.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    initView(data);
                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
            }
        });
        request.execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
    }
}

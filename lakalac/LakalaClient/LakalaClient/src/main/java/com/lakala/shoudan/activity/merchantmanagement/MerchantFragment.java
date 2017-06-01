package com.lakala.shoudan.activity.merchantmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.UpgradeStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.merchant.upgrade.MerchantUpdateActivity;
import com.lakala.shoudan.activity.myaccount.AccountInfoActivity;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linmq on 2016/3/22.
 */
public class MerchantFragment extends BaseFragment {
    private TextView tvCard;
    private TextView tv_lev;
    private TextView tv_tel;
    private ImageView img2;
    private ImageView img1;
    private View llUpgrade;
    private boolean isShowUpgrade;//是否需要升级
    private boolean thirdUpgrade;//是否跑分
    private TextView tvHint;
    private CircleImageView ivLevel;
    private boolean showHint;
    private CircleImageView ivUser;
    private String rejectInfoStr;
    private MerchantInfo merchantInfo;
    private String firstUpgradeStatus;
    private String openBankBranchName;
    private String secondUpgradeStatus;
    private String busLicenceCode;
    private String tag;//1审核中2审核通过
    private String title="1";
    private Intent intents=null;
    private TextView tvPhone;
    private TextView tvMerchatHint;
    private Intent getInt;
    private String accountStatus;
    private String tagResume="0";
    private TextView tvName;
    private TextView tvChange;
    private MerchantInfo merchantInfoData;
    private LinearLayout l1;
    private LinearLayout l2;
    private String tagName="0";
    private String acounts="0";
    private MerchantManagementAcivity mer;
    private String accountLimit="-1";
    private String businessNameLimit="-1";

    public void setIsNeedUpdate(boolean isShowUpgrade) {
        this.isShowUpgrade = isShowUpgrade;
    }

    public void setThirdUpgrade(boolean thirdUpgrade) {
        this.thirdUpgrade = thirdUpgrade;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_merchant, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        tvName = (TextView) view.findViewById(R.id.tv_merchant_name);
        tvChange = (TextView) view.findViewById(R.id.tv_merchant_more);
        img2 = (ImageView) view.findViewById(R.id.img2);
        img1 = (ImageView) view.findViewById(R.id.img1);
       ivLevel = (CircleImageView) view.findViewById(R.id.iv_user);
        tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        tv_lev = (TextView) view.findViewById(R.id.tv_lev);
        tv_tel = (TextView) view.findViewById(R.id.tv_tel);
        ivUser = (CircleImageView) view.findViewById(R.id.iv_user);
        tvHint = (TextView) view.findViewById(R.id.tv_hint);
        l1 = (LinearLayout) view.findViewById(R.id.ll_phone_change);
        l2 = (LinearLayout) view.findViewById(R.id.ll_name_change);
        tvMerchatHint = (TextView) view.findViewById(R.id.tv_merchat_hint);
        tvCard = (TextView) view.findViewById(R.id.tv_bank_card);
        llUpgrade = view.findViewById(R.id.ll_merchant_upgrade);
        merchantInfo= ApplicationEx.getInstance().getUser().getMerchantInfo();
        tvName.setText(merchantInfoData.getUser().getRealName());
        String mobileNumss=merchantInfo.getBusinessName();
        if (merchantInfo.getBusinessName().length()>8){

            mobileNumss= mobileNumss.substring(0,6)+"****"+mobileNumss.substring(mobileNumss.length()-2,mobileNumss.length());
            tvPhone.setText(mobileNumss);
            // setPhone(merchantInfo.getBusinessName());
        }else {
            tvPhone.setText(mobileNumss);
        }
        mer = (MerchantManagementAcivity) getActivity();
        String mobileNum=merchantInfo.getUser().getMobileNum();
        mobileNum= mobileNum.substring(0,3)+"****"+mobileNum.substring(7,11);
        tv_tel.setText(mobileNum);
        tv_lev.setVisibility(View.GONE);
        getInt= getActivity().getIntent();
        showHint = getArguments().getBoolean("showHint");
        secondUpgradeStatus = getArguments().getString("secondUpgradeStatus");
        firstUpgradeStatus = getArguments().getString("firstUpgradeStatus");
        busLicenceCode = getArguments().getString("busLicenceCode");
        openBankBranchName = getArguments().getString("openBankBranchName");
        showStatusHint();
        showMerchantCardNo();
        showUpgrade();
        initListener(view);
        String levelName = getArguments().getString("level");
        setLevel(levelName);
        //tvLevel.setText(levelName);

        accountStatus = getInt.getStringExtra("accountStatus");
        if (accountStatus .equals("FAILURE")||accountStatus .equals("PROCESSING")){
            if (accountStatus .equals("FAILURE")){
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getInt.getStringExtra("accountStatusName"));
            }
            if (getInt.hasExtra("newValue"))
            acounts=getInt.getStringExtra("newValue");
            if (getInt.getStringExtra("accountType") == AccountType.PUBLIC.getValue()) {// 企业
                tvCard.setText(Util.formatCompanyAccount(getInt.getStringExtra("newValue")));
            } else {
                tvCard.setText(StringUtil.formatCardNumberN6S4N4(getInt.getStringExtra("newValue")));
            }
        }
        if (getInt.hasExtra("accountLimit")){
            accountLimit = getInt.getStringExtra("accountLimit");
        }
        if (getInt.hasExtra("businessNameLimit")){
            businessNameLimit = getInt.getStringExtra("businessNameLimit");
        }

        int[] settingIds = new int[]{
                R.id.mer_frag_top, R.id.mer_frag_change_name, R.id.ss};

        for (int i : settingIds) {
            view.findViewById(i).setOnTouchListener(settingOnTouchListener);
        }
    }
    private void settingItemClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.mer_frag_top:
                queryUsrInfo();
                break;
            case R.id.mer_frag_change_name:
                changeName();
                break;
            case R.id.ss:
                changeAccount();
                break;
        }
    }
    View.OnTouchListener settingOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View singleLineTextView, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.gray));
                    break;
                case MotionEvent.ACTION_UP:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    settingItemClick(singleLineTextView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    singleLineTextView.setBackgroundColor(getResources().getColor(R.color.white));
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    private void setPhone(String businessName) {
        String mobileNum=businessName;
        if (mobileNum.length()>4)
        mobileNum= mobileNum.substring(0,2)+"****"+mobileNum.substring(businessName.length()-2,businessName.length());
        tvPhone.setText(mobileNum);
    }

    public void setLevel(String levelName) {
        if (levelName==null||levelName.equals("")||!levelName.startsWith("V")){
            levelName="MV";
            img1.setVisibility(View.VISIBLE);
        }
        Level lv=Level.valueOf(levelName);
        for (Level v:Level.values()) {
            if (lv==v){
                ivLevel.setImageResource(lv.getLvImg());
                tv_lev.setText(lv.getLvName());
            }
        }
    }

    private void initListener(View view) {
        if (!getInt.getStringExtra("cardAppType").equals("PERSON")){
            img1.setVisibility(View.GONE);
            tag="6";
        }
        if (tag.equals("2")){
            l2.setClickable(false);
            l2.setFocusable(false);
            l2.setFocusableInTouchMode(false);
        }
    }

    private void changeName() {
        if (tag.equals("1")){
           // createFullContentDialog();//商户审核未通过
        }else  if (tag.equals("2")){
           // createFullShueDialog("您的商户资料正在审核中，暂时不可修改");//商户资料正在审核中
        }else if (TextUtils.equals(secondUpgradeStatus,UpgradeStatus.PROCESSING.name())){
            createFullShueDialog("您的商户资料正在审核中，暂时不可修改。");//二 类升级审核中
        }else if (TextUtils.equals(secondUpgradeStatus,UpgradeStatus.COMPLETED.name())){
            createFullShueDialog("您的商户资料已认证，不可修改店铺名称");//二 类升级通过
        }else  if (getInt.hasExtra("businessNameLimit")||tagName.equals("1")){
            createFullShueDialog(businessNameLimit);//超出次数限制
            //createFullShueDialog("每个商户只能修改1次店铺名称，您已超过次数限制");//超出次数限制
        }else{
            intents=new Intent(getActivity(),UpdateMechatInfoActivity.class);
            intents.putExtra("shopName",merchantInfo.getBusinessName());
            intents.putExtra("secondUpgradeStatus",secondUpgradeStatus);
            intents.putExtra("accountNo",merchantInfoData.getAccountNo());//收款银行卡卡号
            intents.putExtra("accountType",merchantInfo.getAccountType().getValue());//商户类型:  对公(public,企业)对私(个人,private)
            intents.putExtra("bankName",merchantInfo.getBankName());// 收款银行卡开户行
            intents.putExtra("userName",merchantInfo.getAccountName());// 用户名
            intents.putExtra("bankNo",merchantInfo.getBankNo());// 用户名
            startActivityForResult(intents,101);
            PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_Change_Name, context);
        }
    }

    private  void getMenrchattuStas() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_MERCHAT_BUSINESSNAME);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());

                        JSONObject ACCOUNTNO=jsonObject.optJSONObject("ACCOUNTNO");
                        JSONObject accountNoLimt=ACCOUNTNO.optJSONObject("accountNoLimt");
                        JSONObject accountNoStatus=ACCOUNTNO.optJSONObject("accountNoStatus");
                        accountStatus = accountNoStatus.optString("status");
                        String newValue = accountNoStatus.optString("newValue");
                       // intent.putExtra("newValue", newValue);
                       // intent.putExtra("accountStatus", accountStatus);
                        String accountStatusName = accountNoStatus.optString("statusName");
                        //intent.putExtra("accountStatusName", accountStatusName);
                        if (accountNoLimt!=null&&accountNoLimt.has("msg")){
                            accountLimit = accountNoLimt.optString("msg");
                         //   intent.putExtra("accountLimit", accountLimit);
                        }

                        JSONObject BUSINESSNAME=jsonObject.optJSONObject("BUSINESSNAME");
                        JSONObject businessNameLimt=BUSINESSNAME.optJSONObject("businessNameLimt");
                        if (businessNameLimt!=null&&businessNameLimt.has("msg")){
                            businessNameLimit = businessNameLimt.optString("msg");
                            //intent.putExtra("businessNameLimit", businessNameLimit);
                        }

                        String cardAppType = jsonObject.optString("cardAppType");
                       // intent.putExtra("cardAppType", cardAppType);

                        JSONObject lv1Status=jsonObject.optJSONObject("lv1Status");
                        String lv1StatusMsg = lv1Status.optString("msg");
                        //intent.putExtra("lv1StatusMsg", lv1StatusMsg);

                        JSONObject lv2Status=jsonObject.optJSONObject("lv2Status");
                        String lv2StatusMsg = lv2Status.optString("msg");
                       // intent.putExtra("lv2StatusMsg", lv2StatusMsg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==getActivity().RESULT_OK){
            if (data.hasExtra("upDate")){
                if (data.getStringExtra("upDate").equals("upDateName")){
                    tagName = "1";
                    tvPhone.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName());
                } else  if (data.getStringExtra("upDate").equals("upDateAccount")){
                   // tvMerchatHint.setVisibility(View.GONE);
                    tvHint.setVisibility(View.GONE);
                    accountStatus="PROCESSING";
                    tagResume="1";
               if (data.getStringExtra("type").equals("1")) {// 企业
                     tvCard.setText(Util.formatCompanyAccount(data.getStringExtra("upDateAccount")));
                } else {
                    tvCard.setText(StringUtil.formatCardNumberN6S4N4(data.getStringExtra("upDateAccount")));
                }
                }
                getMenrchattuStas();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mer.upDate){
            queryUsrInfo();

        }
    }

    private void changeAccount() {
       if (tag.equals("1")){
           // createFullContentDialog();//商户审核未通过
        }else  if (tag.equals("2")){
            //createFullShueDialog("您的商户资料正在审核中，暂时不可修改");//商户资料正在审核中
        }else if (tag.equals("6")){
           createFullShueDialog("您当前的商户类型不支持更改收款账户");//卡应用为商户的，不允许改结算账号，
       }else  if (getInt.hasExtra("accountLimit")||(tagResume.equals("1")&&accountLimit!="-1")){
           createFullShueDialog(accountLimit);//超出次数限制
           //createFullShueDialog("每个商户只能修改3次收款账户，您已超过次数限制");//超出次数限制
       }else if (accountStatus.equals("PROCESSING")){
          if (tagResume.equals("1")){
              createFullShueDialog("您的收款账户正在审核中，请耐心等待我们的审核");
           } else{
               createFullShueDialog("您的收款账户正在审核中，请耐心等待我们的审核");
               //createFullShueDialog(getInt.getStringExtra("accountStatusName"));
           }

       } else {
           intents=new Intent(getActivity(),UpdateAccountActivity.class);
           intents.putExtra("secondUpgradeStatus",secondUpgradeStatus);
           intents.putExtra("accountNo1",acounts);//收款银行卡卡号
           intents.putExtra("accountNo",tvCard.getText().toString().trim());//收款银行卡卡号
           intents.putExtra("userName",merchantInfo.getAccountName());// 用户名

           if (getInt.hasExtra("newValue")){
               intents.putExtra("accountType",getInt.getStringExtra("accountType"));//商户类型:  对公(public,企业)对私(个人,private)
               intents.putExtra("bankNo",getInt.getStringExtra("bankNo"));// 用户名
               intents.putExtra("bankName",getInt.getStringExtra("bankName"));// 收款银行卡开户行
           }else {
               intents.putExtra("accountType",merchantInfo.getAccountType().getValue());//商户类型:  对公(public,企业)对私(个人,private)
               intents.putExtra("bankNo",merchantInfo.getBankNo());// 用户名
               intents.putExtra("bankName",merchantInfo.getBankName());// 收款银行卡开户行
           }
           startActivityForResult(intents,100);
           PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_Change_Accout, context);
       }
    }
    /**
     * 商户个人信息
     */
    private void queryUsrInfo() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_STATUS);
        businessRequest.setResponseHandler(serviceResultCallback);
        businessRequest.execute();

    }

    private void showMerchantCardNo() {
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        acounts=merchantInfoData.getAccountNo();
        if (merchantInfoData.getAccountType() == AccountType.PUBLIC) {// 企业
            tvCard.setText(Util.formatCompanyAccount(merchantInfoData
                    .getAccountNo()));
        } else {
            tvCard.setText(StringUtil.formatCardNumberN6S4N4(merchantInfoData
                    .getAccountNo()));
        }
    }

    ServiceResultCallback serviceResultCallback = new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            hideProgressDialog();
            JSONObject jsonObject = null;
            if (resultServices.isRetCodeSuccess()) {
                try {
                    jsonObject = new JSONObject(resultServices.retData);
                    String firstUpgradeStatus1 = jsonObject.optString("firstUpgradeStatus");
                    String secondUpgradeStatus1 = jsonObject.optString("secondUpgradeStatus");
                    String openBankBranchName1 = jsonObject.optString("openBankBranchName");
                    String busLicenceCode1 = jsonObject.optString("busLicenceCode");
                    if (mer.upDate){
                        firstUpgradeStatus=firstUpgradeStatus1;
                        secondUpgradeStatus=secondUpgradeStatus1;
                        busLicenceCode=busLicenceCode1;
                        openBankBranchName=openBankBranchName1;
                        mer.upDate=false;
                        update();
                    }else {
                        Intent intent = new Intent(context, AccountInfoActivity.class);
                        intent.putExtra("firstUpgradeStatus", firstUpgradeStatus1);
                        intent.putExtra("secondUpgradeStatus", secondUpgradeStatus1);
                        intent.putExtra("openBankBranchName", openBankBranchName1);
                        intent.putExtra("busLicenceCode", busLicenceCode1);
                        context.startActivity(intent);
                        PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_Merchant_Info, context);
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            hideProgressDialog();
            ToastUtil.toast(context, context.getString(R.string.socket_fail));
            LogUtil.print(connectEvent.getDescribe());
        }
    };
    private void update() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.QUERY_MerLevelAndLimit);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    try {
                        jsonObject = new JSONObject(data);
                        setLevel(jsonObject.optString("merLevel"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
            }
        });
        request.execute();
    }
    private void showUpgrade() {
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();

        if (merchantInfoData.getMerchantStatus() == MerchantStatus.FAILURE) {
           tvMerchatHint.setText("商户资料审核失败，请修改资料后重新提交");
            //tvChange.setText("修改");
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
            tvMerchatHint.setVisibility(View.VISIBLE);
            tvChange.setVisibility(View.VISIBLE);
        }else if (merchantInfoData.getMerchantStatus() == MerchantStatus.PROCESSING) {
            tvMerchatHint.setText("您已开通商户，请等待审核");
            tvMerchatHint.setVisibility(View.VISIBLE);
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
          l1.setClickable(false);
            l2.setClickable(false);
            l1.setFocusable(false);
            l1.setFocusableInTouchMode(false);
            l2.setFocusable(false);
            l2.setFocusableInTouchMode(false);
        }
        if (isShowUpgrade || thirdUpgrade
                || merchantInfoData.getMerchantStatus() == MerchantStatus.FROZEN) {
            llUpgrade.setVisibility(View.VISIBLE);
        }    }
    /**
     * 显示商户状态提示语
     */
    public void showStatusHint(){

        rejectInfoStr= getString(R.string.acc_data_not_passed);

        if (merchantInfo.getMerchantStatus()!=MerchantStatus.COMPLETED){

            switch (merchantInfo.getMerchantStatus()) {

                case NONE:
                    break;

                case PROCESSING:
                    rejectInfoStr = "您已开通商户，请等待审核";
                    tag="2";
                    break;

                case FROZEN:
                    rejectInfoStr = "账户被冻结。";
                    break;

                case FAILURE:
                    rejectInfoStr = "很抱歉，您提交的资料有误，未能通过审核。请将标成橘色内容部分修改后重新提交审核。";
                    tag="1";
                  /*  tvChangeInfo.setVisibility(View.VISIBLE);
                    tvChangeInfo.setText("银行卡非本人银行卡");
                    //((ImageView)ivPass).setImageResource(R.drawable.pic_pass_through);
                    ivPass.setVisibility(View.VISIBLE);*/
                    break;
                case COMPLETED:
                    rejectInfoStr = "您的商户审核已通过";
                    break;
            }

        }else{
            boolean flag=true;
            tag="3";
            //一次升级提示语
            if (TextUtils.equals(secondUpgradeStatus, com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus.NONE.name())){
                flag=false;
            }
          //  initFlag(flag);
        }

    }

    private void initFlag(boolean flag){
        String status;
        if (!flag){
            status=firstUpgradeStatus;
        }else{
            status=secondUpgradeStatus;
        }
        //一级升级
        switch (com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus.valueOf(status)){
            case PROCESSING:
                rejectInfoStr="您正在申请商户升级，请耐心等待审核";
                break;
            case FAILURE:
                rejectInfoStr="很抱歉，您提交的商户升级资料有误，未能通过审核，请修改资料后重新提交审核";
                break;
            case COMPLETED:
                if (!flag){
                    rejectInfoStr="恭喜！您的商户升级成功，提交更多资料，还可以继续升级，享受更高额度哦";
                }else{
                    rejectInfoStr="恭喜！您的商户已升级成功";
                }
                break;
            case NONE://不可能是二次升级
                rejectInfoStr="您的商户审核已通过";
                break;

        }
    }
    private void thirdUpgrade() {
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPGRADE_THIRD);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
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
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
            }
        });
        businessRequest.execute();
    }

    private void showDialog(final boolean isUpgradeFirst) {
        DialogCreator.createFullContentDialog(context, "取消",
                "确定", getString(R.string.firstupgrade_failure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActi(isUpgradeFirst);
                    }
                }).show();
    }

    private void  createFullContentDialog(){
        DialogCreator.createFullContentDialog(
                context, "提示", "取消",
                "确定", getString(R.string.mercht_failure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActi();
                    }
                }
        ).show();
    }

    private void  createFullShueDialog(String content){
        DialogCreator.createFullShueDialog(
                context, "提示",   "确定", content,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                      //  startActi(isUpgradeFirst);
                    }
                }
        ).show();
    }
    private void startActi(boolean isUpgradeFirst) {
        Intent intent = new Intent(getActivity(), MerchantUpdateActivity.class);
        intent.putExtra("isUpgradeFirst", isUpgradeFirst);
        startActivity(intent);
    }
    private void startActi() {
        Intent intent = new Intent(context, AccountInfoActivity.class);
        intent.putExtra("firstUpgradeStatus", firstUpgradeStatus);
        intent.putExtra("secondUpgradeStatus", secondUpgradeStatus);
        intent.putExtra("openBankBranchName", openBankBranchName);
        intent.putExtra("busLicenceCode", busLicenceCode);
        context.startActivity(intent);
    }

    private void refreshThird() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.QUERY_MerLevelAndLimit);
        final MerchantManagementAcivity act = (MerchantManagementAcivity) getActivity();
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    try {
                        jsonObject = new JSONObject(data);
                        //tvLevel.setText(jsonObject.optString("merLevel"));
                        llUpgrade.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    act.initView(data);
                } else {
                    hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

}

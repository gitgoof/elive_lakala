package com.lakala.elive.market.activity;


import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.CardAppInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;


/**
 *
 *
 *  网点终端详情信息
 *
 * @author hongzhiliang
 */
public class TermDetailActivity extends BaseActivity {

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求
    private TermInfo termInfo = null;

    TextView tvTerminalCode; //终端物理号
    TextView tvDeviceSn; //终端序列号
    TextView tvProductClass; //网点地址
    TextView tvOpenTime; //网点地址
    TextView tvDeviceDrawMethod; //终端领用方式
    TextView tvDeviceCheckStatus; //终端核查状态
    TextView tvTermNo; //业务终端号
    TextView tvSignOrg; //终端领用方式
    TextView tvBranchOrg; //终端领用方式
    TextView tvTermType; //终端领用方式
    TextView tvTermStatus; //终端状态
    TextView tvNoTranDesc; //其它无交易原因备注

    protected TextView spinnerNoTranReason;
    protected TextView spinnerNoTranAnalyseResult;
    protected TextView spinnerNonConnChangeResult;
    protected TextView spinnerSwingUpgradeResult;
    protected TextView spinnerTermType;
    private LinearLayout llNoTranDesc;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.fragment_mer_term_detail);
    }

    @Override
    protected void bindView() {
        tvTerminalCode = (TextView) findViewById(R.id.tv_terminal_code);

        tvDeviceSn = (TextView) findViewById(R.id.tv_device_sn);
        tvProductClass = (TextView) findViewById(R.id.tv_product_class);
        tvOpenTime = (TextView) findViewById(R.id.tv_open_time);
        tvDeviceDrawMethod = (TextView) findViewById(R.id.tv_device_draw_method);
        tvDeviceCheckStatus = (TextView) findViewById(R.id.tv_device_check_status);
        tvTermType = (TextView) findViewById(R.id.tv_term_type);

        tvTermNo = (TextView) findViewById(R.id.tv_term_no);
        tvTermStatus = (TextView) findViewById(R.id.tv_term_status);

        tvNoTranDesc = (TextView) findViewById(R.id.tv_no_tran_desc);
        llNoTranDesc = (LinearLayout) findViewById(R.id.ll_no_tran_desc);

        tvSignOrg = (TextView) findViewById(R.id.tv_sign_org);
        tvBranchOrg = (TextView) findViewById(R.id.tv_branch_org);

        spinnerNoTranAnalyseResult = (TextView) findViewById(R.id.tv_no_tran_analyse_result);
        spinnerNonConnChangeResult = (TextView) findViewById(R.id.tv_non_conn_change_result);
        spinnerSwingUpgradeResult = (TextView) findViewById(R.id.tv_swing_upgrade_result);
        spinnerNoTranReason = (TextView) findViewById(R.id.tv_no_tran_reason);
        spinnerTermType = (TextView) findViewById(R.id.tv_term_type);

    }


    @Override
    protected void bindEvent() {

        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);



    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点终端详情");
        termInfo = (TermInfo) getIntent().getExtras().get(Constants.EXTRAS_TERM_INFO); //获取页面传递的对象

        handleTermInfo();

        queryMerTermDetail(); //查询网点终端列表
    }

    private void handleTermInfo() {
        tvDeviceSn.setText(termInfo.getDeviceSn());
        tvProductClass.setText(termInfo.getProductClass());
        tvOpenTime.setText(termInfo.getCreateTimeStr());
        tvDeviceDrawMethod.setText(termInfo.getDeviceDrawMethod());


    }

    private void queryMerTermDetail() {
        showProgressDialog("正在加载终端详情...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(termInfo.getShopNo());
        merShopReqInfo.setTerminalCode(termInfo.getTerminalCode());

        NetAPI.queryTermDetail(this, this, merShopReqInfo);

    }


    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_DETAIL:
                closeProgressDialog();
                handleTermInfo( (TermInfo) obj );
                break;
        }
    }

    private void handleTermInfo(TermInfo mTermInfo) {
        termInfo = mTermInfo;

        tvTerminalCode.setText(termInfo.getTerminalCode());
        tvDeviceSn.setText(termInfo.getDeviceSn());
        tvNoTranDesc.setText(termInfo.getNoTranDesc());

        String termStatus = "";

        if("VALID".equals(termInfo.getTermStatus())){
            termStatus = "有效";
        }else if("INVALID".equals(termInfo.getTermStatus())){
            termStatus = "无效";
        }else{
            termStatus = "未知";
        }
        tvTermStatus.setText(termStatus);

        if(mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS)!=null
                &&  mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS).get(termInfo.getProductClass())!=null){
            tvProductClass.setText( mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS).get(termInfo.getProductClass()));
        }else{
            tvProductClass.setText("未知");
        }

        if(mSession.getSysDictMap().get(Constants.DEVICE_DRAW_METHOD)!=null
                &&  mSession.getSysDictMap().get(Constants.DEVICE_DRAW_METHOD).get(termInfo.getDeviceDrawMethod())!=null){
            tvDeviceDrawMethod.setText( mSession.getSysDictMap().get(Constants.DEVICE_DRAW_METHOD).get(termInfo.getDeviceDrawMethod()));
        }else{
            tvDeviceDrawMethod.setText("未知");
        }

        if(mSession.getSysDictMap().get(Constants.DEVICE_CHECK_STATUS)!=null
                &&  mSession.getSysDictMap().get(Constants.DEVICE_CHECK_STATUS).get(termInfo.getDeviceCheckStatus())!=null){
            tvDeviceCheckStatus.setText(mSession.getSysDictMap().get(Constants.DEVICE_CHECK_STATUS).get(termInfo.getDeviceCheckStatus()));
        }else{
            tvDeviceCheckStatus.setText("未知");
        }

        tvOpenTime.setText(termInfo.getCreateTimeStr());
        tvSignOrg.setText(termInfo.getSignName());
        tvBranchOrg.setText(termInfo.getBranchOrgName());

        if(termInfo.getCardAppVOs() != null){
            String termNos = "";

            for(int i=0; i<termInfo.getCardAppVOs().size();i++ ){
                CardAppInfo tmp = termInfo.getCardAppVOs().get(i);

                String termNoStatus = "";
                if("VALID".equals(tmp.getTermStatus())){
                    termNoStatus = "有效";
                }else if("INVALID".equals(tmp.getTermStatus())){
                    termNoStatus = "无效";
                }else{
                    termNoStatus = "未知";
                }

                if(i == termInfo.getCardAppVOs().size() -1){
                    termNos = termNos + tmp.getTermNo() +" | " + tmp.getCardAppTypeName() +" | " + termNoStatus;
                }else{
                    termNos = termNos + tmp.getTermNo() +" | " + tmp.getCardAppTypeName() +" | " + termNoStatus + "\n";
                }

            }
            tvTermNo.setText(termNos);
        }

        // 终端类型
        if(termInfo.getTerminalType()!=null
                && !"".equals(termInfo.getTerminalType()) && !"-1".equals(termInfo.getTerminalType()) ){
            spinnerTermType.setText(mSession.getSysDictMap().get(Constants.TERM_TYPE).get(termInfo.getTerminalType()));
        }else{
            spinnerTermType.setText("");
        }

        // 无交易原因	noTranReason
        if(termInfo.getNoTranReason()!=null
                && !"".equals(termInfo.getNoTranReason()) && !"-1".equals(termInfo.getNoTranReason()) ){
            spinnerNoTranReason.setText(mSession.getSysDictMap().get(Constants.NO_TRAN_REASON).get(termInfo.getNoTranReason()));

            //无交易原因描述控制
            if("99".equals(termInfo.getNoTranReason())){
                llNoTranDesc.setVisibility(View.VISIBLE);
            }else{
                llNoTranDesc.setVisibility(View.GONE);
            }

        }else{
            spinnerNoTranReason.setText("");
            llNoTranDesc.setVisibility(View.GONE);
        }


        //  无交易梳理结果	noTranAnalyseResult
        if(termInfo.getNoTranAnalyseResult()!=null
                && !"".equals(termInfo.getNoTranAnalyseResult()) && !"-1".equals(termInfo.getNoTranAnalyseResult()) ){
            spinnerNoTranAnalyseResult.setText(mSession.getSysDictMap().get(Constants.NO_TRAN_ANALYSE_RESULT).get(termInfo.getNoTranAnalyseResult()));
        }else{
            spinnerNoTranAnalyseResult.setText("");;
        }

//        非接改造结果	nonConnChangeResult
        if(termInfo.getNonConnChangeResult()!=null
                && !"".equals(termInfo.getNonConnChangeResult()) && !"-1".equals(termInfo.getNonConnChangeResult()) ){
            spinnerNonConnChangeResult.setText(mSession.getSysDictMap().get(Constants.NONCONN_CHANGE_RESULT).get(termInfo.getNonConnChangeResult()));
        }else{
            spinnerNonConnChangeResult.setText("");;
        }

//        强挥升级结果	swingUpgradeResult
        if(termInfo.getSwingUpgradeResult()!=null
                && !"".equals(termInfo.getSwingUpgradeResult()) && !"-1".equals(termInfo.getSwingUpgradeResult()) ){
            spinnerSwingUpgradeResult.setText(mSession.getSysDictMap().get(Constants.SWING_UPGRADE_RESULT).get(termInfo.getSwingUpgradeResult()));
        }else{
            spinnerSwingUpgradeResult.setText("");
        }


    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_DETAIL:
                closeProgressDialog();
                Utils.showToast(this, "加载失败:" + statusCode + "!");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}

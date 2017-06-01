package com.lakala.elive.market.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.CardAppInfo;
import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.camera.activity.MipcaCaptureActivity;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.net.req.VisitOrEditReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.adapter.DictDetailListAdpter;
import com.lakala.elive.user.base.BaseActivity;


/**
 *
 * 终端详情编辑页面
 *
 * @author hongzhiliang
 *
 */
public class StepEditTermInfoActivity extends BaseActivity {

    private TermInfo termInfo = null;
    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求
    private VisitOrEditReqInfo visitReqInfo = new VisitOrEditReqInfo();

    TextView tvTerminalCode; //终端物理号
    TextView etDeviceSn; //终端序列号

    TextView tvOpenTime; //网点地址

    TextView tvSignOrg;
    TextView tvBranchOrg;
    TextView tvTermNo;

    private EditText etNoTranDesc; //其他无交易原因描述

    TextView tvTermStatus; //终端状态

    private Button btnSubmitEdit; //提交信息
    private Button btnScanOpen; //提交信息

    DictDetailListAdpter mTermTypeAdapter;
    DictDetailListAdpter mNoTranReasonAdapter;
    DictDetailListAdpter mNoTranAnalyseResultAdapter;
    DictDetailListAdpter mNonConnChangeResultAdapter;
    DictDetailListAdpter mSwingUpgradeResultAdapter;
    DictDetailListAdpter mProductClassAdapter;
    DictDetailListAdpter mDeviceDrawMethodAdapter;
    DictDetailListAdpter mDeviceCheckStatusAdapter;

    protected Spinner spinnerNoTranReason;
    protected Spinner spinnerNoTranAnalyseResult;
    protected Spinner spinnerNonConnChangeResult;
    protected Spinner spinnerSwingUpgradeResult;
    protected Spinner spinnerTermType;
    protected Spinner spinnerProductClass; //终端产品类型
    protected Spinner spinnerDeviceDrawMethod; //机具领用类型
    protected Spinner spinnerDeviceCheckStatus; //机具状态核查

    private InputMethodManager mInputMethodManager;

    private LinearLayout llNoTranDesc;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_step_edit_term_info);
    }

    @Override
    protected void bindView() {

        etDeviceSn = (TextView) findViewById(R.id.et_device_sn);
        tvTerminalCode = (TextView) findViewById(R.id.tv_terminal_code);
        tvOpenTime = (TextView) findViewById(R.id.tv_open_time);
        tvSignOrg = (TextView) findViewById(R.id.tv_sign_org);
        tvBranchOrg = (TextView) findViewById(R.id.tv_branch_org);
        tvTermNo = (TextView) findViewById(R.id.tv_term_no);

        llNoTranDesc = (LinearLayout) findViewById(R.id.ll_no_tran_desc);
        etNoTranDesc = (EditText) findViewById(R.id.et_no_tran_desc);
        tvTermStatus = (TextView) findViewById(R.id.tv_term_status);

        btnSubmitEdit = (Button) findViewById(R.id.btn_submit_edit);
        btnScanOpen = (Button) findViewById(R.id.btn_scan_open); //扫码

        spinnerNoTranAnalyseResult = (Spinner) findViewById(R.id.spinner_no_tran_analyse_result);
        spinnerNonConnChangeResult = (Spinner) findViewById(R.id.spinner_non_conn_change_result);
        spinnerSwingUpgradeResult = (Spinner) findViewById(R.id.spinner_swing_upgrade_result);
        spinnerNoTranReason = (Spinner) findViewById(R.id.spinner_no_tran_reason);
        spinnerTermType = (Spinner) findViewById(R.id.spinner_term_type);

        spinnerProductClass = (Spinner) findViewById(R.id.spinner_product_class);
        spinnerDeviceDrawMethod = (Spinner) findViewById(R.id.spinner_device_draw_method);
        spinnerDeviceCheckStatus = (Spinner) findViewById(R.id.spinner_device_check_status);



    }


    @Override
    protected void bindEvent() {
        btnSubmitEdit.setOnClickListener(this);
        btnScanOpen.setOnClickListener(this);

        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

        etDeviceSn.setOnClickListener(this);//序列号可修改
        etNoTranDesc.setOnClickListener(this);
        //终端类型
        mTermTypeAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.TERM_TYPE));
        spinnerTermType.setAdapter(mTermTypeAdapter);

        spinnerTermType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mTermTypeAdapter.getItem(position);
                termInfo.setTerminalType(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //无交易原因
        mNoTranReasonAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.NO_TRAN_REASON));
        spinnerNoTranReason.setAdapter(mNoTranReasonAdapter);

        spinnerNoTranReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mNoTranReasonAdapter.getItem(position);
                termInfo.setNoTranReason(dictDetailInfo.getDictKey());

                //无交易原因描述控制
                if("99".equals(termInfo.getNoTranReason())){
                    llNoTranDesc.setVisibility(View.VISIBLE);
                }else{
                    llNoTranDesc.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //无交易梳理结果
        mNoTranAnalyseResultAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.NO_TRAN_ANALYSE_RESULT));
        spinnerNoTranAnalyseResult.setAdapter(mNoTranAnalyseResultAdapter);
        spinnerNoTranAnalyseResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mNoTranAnalyseResultAdapter.getItem(position);
                termInfo.setNoTranAnalyseResult(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //非接改造结果
        mNonConnChangeResultAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.NONCONN_CHANGE_RESULT));
        spinnerNonConnChangeResult.setAdapter(mNonConnChangeResultAdapter);
        spinnerNonConnChangeResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mNonConnChangeResultAdapter.getItem(position);
                termInfo.setNonConnChangeResult(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //强挥升级结果
        mSwingUpgradeResultAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.SWING_UPGRADE_RESULT));
        spinnerSwingUpgradeResult.setAdapter(mSwingUpgradeResultAdapter);
        spinnerSwingUpgradeResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mSwingUpgradeResultAdapter.getItem(position);
                termInfo.setSwingUpgradeResult(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        //终端产品类型
        mProductClassAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.SYS_PRODUCT_CLASS));
        spinnerProductClass.setAdapter(mProductClassAdapter);
        spinnerProductClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mProductClassAdapter.getItem(position);
                termInfo.setProductClass(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        //机具领用类型
        mDeviceDrawMethodAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.DEVICE_DRAW_METHOD));
        spinnerDeviceDrawMethod.setAdapter(mDeviceDrawMethodAdapter);
        spinnerDeviceDrawMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mDeviceDrawMethodAdapter.getItem(position);
                termInfo.setDeviceDrawMethod(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        //机具状态核查
        mDeviceCheckStatusAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.DEVICE_CHECK_STATUS));
        spinnerDeviceCheckStatus.setAdapter(mDeviceCheckStatusAdapter);
        spinnerDeviceCheckStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mDeviceCheckStatusAdapter.getItem(position);
                termInfo.setDeviceCheckStatus(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });



    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点基本信息核实编辑");
        termInfo = (TermInfo) getIntent().getExtras().get(Constants.EXTRAS_TERM_INFO); //获取页面传递的对象

        queryMerTermDetail(); //查询网点终端详情

        //初始化输入法
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void queryMerTermDetail() {
        showProgressDialog("正在加载终端详情...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(termInfo.getShopNo());
        merShopReqInfo.setTerminalCode(termInfo.getTerminalCode());

        NetAPI.queryTermDetail(this, this, merShopReqInfo);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_edit:
                doNext();//回到终端列表页面
                break;
            case R.id.et_device_sn:
                etDeviceSn.setFocusable(true);//设置输入框可聚集
                etDeviceSn.setFocusableInTouchMode(true);//设置触摸聚焦
                etDeviceSn.requestFocus();//请求焦点
                etDeviceSn.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etDeviceSn, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_no_tran_desc:
                etNoTranDesc.setFocusable(true);//设置输入框可聚集
                etNoTranDesc.setFocusableInTouchMode(true);//设置触摸聚焦
                etNoTranDesc.requestFocus();//请求焦点
                etNoTranDesc.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etNoTranDesc, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.btn_scan_open:
                Intent intent = new Intent();
                intent.setClass(StepEditTermInfoActivity.this, MipcaCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, Constants.TAKE_SCAN);
                break;
            case R.id.btn_iv_back:
                doNext();//回到终端列表页面
                break;
            default:
                break;
        }
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
        etDeviceSn.setText(termInfo.getDeviceSn());
        etNoTranDesc.setText(termInfo.getNoTranDesc());

        String termStatus = "";

        if("VALID".equals(termInfo.getTermStatus())){
            termStatus = "有效";
        }else if("INVALID".equals(termInfo.getTermStatus())){
            termStatus = "无效";
        }else{
            termStatus = "未知";
        }
        tvTermStatus.setText(termStatus);


        tvOpenTime.setText(termInfo.getCreateTimeStr());
        tvSignOrg.setText(termInfo.getSignName());
        tvBranchOrg.setText(termInfo.getBranchOrgName());

        if(termInfo.getCardAppVOs() != null){
//          String termNos = termInfo.getCardAppVOs().toString();
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
            spinnerTermType.setSelection(mNoTranReasonAdapter.getValueByKey(termInfo.getTerminalType()));
        }else{
            spinnerTermType.setSelection(0);
        }

        // 无交易原因	noTranReason
        if(termInfo.getNoTranReason()!=null
                && !"".equals(termInfo.getNoTranReason()) && !"-1".equals(termInfo.getNoTranReason()) ){
            spinnerNoTranReason.setSelection(mNoTranReasonAdapter.getValueByKey(termInfo.getNoTranReason()));

            //无交易原因描述
            if("99".equals(termInfo.getNoTranReason())){
                llNoTranDesc.setVisibility(View.VISIBLE);
            }else{
                llNoTranDesc.setVisibility(View.GONE);
            }

        }else{
            spinnerNoTranReason.setSelection(0);
        }

        //  无交易梳理结果	noTranAnalyseResult
        if(termInfo.getNoTranAnalyseResult()!=null
                && !"".equals(termInfo.getNoTranAnalyseResult()) && !"-1".equals(termInfo.getNoTranAnalyseResult()) ){
            spinnerNoTranAnalyseResult.setSelection(mNoTranAnalyseResultAdapter.getValueByKey(termInfo.getNoTranAnalyseResult()));
        }else{
            spinnerNoTranAnalyseResult.setSelection(0);
        }

//        非接改造结果	nonConnChangeResult
        if(termInfo.getNonConnChangeResult()!=null
                && !"".equals(termInfo.getNonConnChangeResult()) && !"-1".equals(termInfo.getNonConnChangeResult()) ){
            spinnerNonConnChangeResult.setSelection(mNonConnChangeResultAdapter.getValueByKey(termInfo.getNonConnChangeResult()));
        }else{
            spinnerNonConnChangeResult.setSelection(0);
        }

//        强挥升级结果	swingUpgradeResult
        if(termInfo.getSwingUpgradeResult()!=null
                && !"".equals(termInfo.getSwingUpgradeResult()) && !"-1".equals(termInfo.getSwingUpgradeResult()) ){
            spinnerSwingUpgradeResult.setSelection(mSwingUpgradeResultAdapter.getValueByKey(termInfo.getSwingUpgradeResult()));
        }else{
            spinnerSwingUpgradeResult.setSelection(0);
        }

        if(mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS)!=null
                &&  mSession.getSysDictMap().get(Constants.SYS_PRODUCT_CLASS).get(termInfo.getProductClass())!=null){
            spinnerProductClass.setSelection(mProductClassAdapter.getValueByKey(termInfo.getProductClass()));
        }else{
            spinnerProductClass.setSelection(0);
        }

        if(mSession.getSysDictMap().get(Constants.DEVICE_DRAW_METHOD)!=null
                &&  mSession.getSysDictMap().get(Constants.DEVICE_DRAW_METHOD).get(termInfo.getDeviceDrawMethod())!=null){
            spinnerDeviceDrawMethod.setSelection(mDeviceDrawMethodAdapter.getValueByKey(termInfo.getDeviceDrawMethod()));
        }else{
            spinnerDeviceDrawMethod.setSelection(0);
        }

        //机具状态核查
        if(mSession.getSysDictMap().get(Constants.DEVICE_CHECK_STATUS)!=null
                &&  mSession.getSysDictMap().get(Constants.DEVICE_CHECK_STATUS).get(termInfo.getDeviceCheckStatus())!=null){
            spinnerDeviceCheckStatus.setSelection(mDeviceCheckStatusAdapter.getValueByKey(termInfo.getDeviceCheckStatus()));
        }else{
            spinnerDeviceCheckStatus.setSelection(0);
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

    //序列号
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_SCAN:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    etDeviceSn.setText(scanResult);
                }
                break;
        }
    }

    /**
     * 下一步
     */
    private void doNext(){
        //终端SN号
        if(TextUtils.isEmpty(etDeviceSn.getText())){
            Utils.showToast(this,"终端SN号不能为空！");
            return;
        }else{
            termInfo.setDeviceSn(etDeviceSn.getText().toString());
        }

        //终端类型
        if(termInfo.getTerminalType()!=null
                && !"".equals(termInfo.getTerminalType()) && !"-1".equals(termInfo.getTerminalType()) ){
        }else{
            Utils.showToast(this,"终端类型核查不能为空！");
            return;
        }

        // 无交易原因	noTranReason

//        if(termInfo.getNoTranReason()!=null
//                && !"".equals(termInfo.getNoTranReason()) && !"-1".equals(termInfo.getNoTranReason()) ){
//        }else{
//            Utils.showToast(this,"终端无交易原因核查不能为空！");
//            return;
//        }

        //  无交易梳理结果	noTranAnalyseResult

//        if(termInfo.getNoTranAnalyseResult()!=null
//                && !"".equals(termInfo.getNoTranAnalyseResult()) && !"-1".equals(termInfo.getNoTranAnalyseResult()) ){
//        }else{
//            Utils.showToast(this,"终端无交易梳理结果核查不能为空！");
//            return;
//        }

        //其它无交易原因描述
        if(termInfo.getNoTranReason()!=null
                && !"".equals(termInfo.getNoTranReason()) && !"-1".equals(termInfo.getNoTranReason()) ){

            if( "99".equals(termInfo.getNoTranReason()) && TextUtils.isEmpty(etNoTranDesc.getText()) ){
                Utils.showToast(this,"其它无交易原因备注不能为空！");
                return;
            }else{
                termInfo.setNoTranDesc(etNoTranDesc.getText().toString());
            }

        }

//        非接改造结果	nonConnChangeResult
        if(termInfo.getNonConnChangeResult()!=null
                && !"".equals(termInfo.getNonConnChangeResult()) && !"-1".equals(termInfo.getNonConnChangeResult()) ){

        }else{
            Utils.showToast(this,"终端非接改造结果核查不能为空！");
            return;
        }

//        强挥升级结果	swingUpgradeResult
        if(termInfo.getSwingUpgradeResult()!=null
                && !"".equals(termInfo.getSwingUpgradeResult()) && !"-1".equals(termInfo.getSwingUpgradeResult()) ){

        }else{
            Utils.showToast(this,"终端强挥升级结果核查不能为空！");
            return;
        }

        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "已核实该完终端信息？",
                "取消",
                "确定",
                mListener
        ).show();

    }

    /**
     * 上一步
     *
     */
    private void doPrevious(){

    }

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();

                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra(Constants.EXTRAS_TERM_INFO,  termInfo);
                    //设置返回数据
                    StepEditTermInfoActivity.this.setResult(RESULT_OK, intent);
                    //关闭Activity
                    StepEditTermInfoActivity.this.finish();


                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


}

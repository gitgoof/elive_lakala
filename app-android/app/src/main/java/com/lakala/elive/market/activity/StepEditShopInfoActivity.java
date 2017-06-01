package com.lakala.elive.market.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.net.req.VisitOrEditReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;


/**
 *
 * Step1  网点基本信息修改
 *
 * @author hongzhiliang
 *
 */
public class StepEditShopInfoActivity extends BaseActivity {

    private MerShopReqInfo   merShopReqInfo = new MerShopReqInfo(); //请求查询接口
    private VisitOrEditReqInfo visitReqInfo ; //提交签到请求信息体
    private MerShopInfo      merShopInfo = null; //商户网点信息

    private TextView tvMerchantCode; //网点商户号
    private TextView tvMerchantName; //网点商户名称
    private TextView tvShopNo; //网点号
    private TextView tvShopStatus; //网点状态


    private TextView etShopName; //网点名称
    private TextView etShopAddr; //网点地址
    private TextView etContactor; //网点联系人
    private TextView etMobileNo; //网点联系人手机
    private TextView etTelNo; //网点联系人手机
    private TextView etEmail; //

    private Button btnSubmitEdit; //提交信息

    private InputMethodManager mInputMethodManager;
    private ImageButton btnShopLocation;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_step_edit_mer_info);
    }

    @Override
    protected void bindView() {
        tvMerchantCode = (TextView) findViewById(R.id.tv_merchant_code);
        tvMerchantName = (TextView) findViewById(R.id.tv_merchant_name);
        tvShopNo = (TextView) findViewById(R.id.tv_shop_no);
        tvShopStatus = (TextView) findViewById(R.id.tv_shop_status);

        etShopName = (EditText) findViewById(R.id.et_shop_name);
        etShopAddr = (EditText) findViewById(R.id.et_shop_addr);
        etContactor = (EditText) findViewById(R.id.et_contactor);
        etMobileNo = (EditText) findViewById(R.id.et_mobile_no);
        etTelNo = (EditText) findViewById(R.id.et_tel_no);
        etEmail = (EditText) findViewById(R.id.et_email);

        //提交按钮
        btnSubmitEdit = (Button) findViewById(R.id.btn_submit_edit);
        btnShopLocation = (ImageButton) findViewById(R.id.btn_shop_location);

    }


    @Override
    protected void bindEvent() {
        btnSubmitEdit.setOnClickListener(this);

        //初始化输入法
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //打开定位
        btnShopLocation.setOnClickListener(this);
        etShopName.setOnClickListener(this);
        etShopAddr.setOnClickListener(this);
        etContactor.setOnClickListener(this);
        etMobileNo.setOnClickListener(this);
        etTelNo.setOnClickListener(this);
        etEmail.setOnClickListener(this);

    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点基本信息核实");

        //1 获取上个Activity传过来的对象
        visitReqInfo = (VisitOrEditReqInfo) getIntent().getExtras().get(Constants.EXTRAS_MER_VISIT_INFO);

        merShopInfo= visitReqInfo.getShopChangeRecordVO();

        if(merShopInfo == null){
            //请求获取商户详情界面 (如果页面信息详细就不要请求了)
            queryMerShopInfoDetail();
        }else{
            handleMerShopInfo(merShopInfo);
        }


        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

    }

    private void queryMerShopInfoDetail() {
        showProgressDialog("正在加载网点基本信息...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merShopInfo.getShopNo());
        NetAPI.queryEliveMerShopInfoDetail(this, this, merShopReqInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_edit: //进入标签页面
                doNext();
                break;
            case R.id.btn_shop_location: //进入标签页面
              //  startActivityForResult(new Intent(this,LocationActivity.class),Constants.TAKE_MER_LOCATION);
                break;
            case R.id.et_shop_addr://输入框
//              etShopAddr = (EditText) findViewById(R.id.et_shop_addr);
                etShopAddr.setFocusable(true);//设置输入框可聚集
                etShopAddr.setFocusableInTouchMode(true);//设置触摸聚焦
                etShopAddr.requestFocus();//请求焦点
                etShopAddr.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etShopAddr, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_shop_name://输入框
//              etShopName = (EditText) findViewById(R.id.et_shop_name);
                etShopName.setFocusable(true);//设置输入框可聚集
                etShopName.setFocusableInTouchMode(true);//设置触摸聚焦
                etShopName.requestFocus();//请求焦点
                etShopName.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etShopName, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_contactor://输入框
//              etContactor = (EditText) findViewById(R.id.et_contactor);
                etContactor.setFocusable(true);//设置输入框可聚集
                etContactor.setFocusableInTouchMode(true);//设置触摸聚焦
                etContactor.requestFocus();//请求焦点
                etContactor.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etContactor, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_mobile_no://输入框
//              etMobileNo = (EditText) findViewById(R.id.et_mobile_no);
                etMobileNo.setFocusable(true);//设置输入框可聚集
                etMobileNo.setFocusableInTouchMode(true);//设置触摸聚焦
                etMobileNo.requestFocus();//请求焦点
                etMobileNo.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etMobileNo, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_tel_no://输入框
//              etTelNo = (EditText) findViewById(R.id.et_tel_no);
                etTelNo.setFocusable(true);//设置输入框可聚集
                etTelNo.setFocusableInTouchMode(true);//设置触摸聚焦
                etTelNo.requestFocus();//请求焦点
                etTelNo.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etTelNo, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.et_email://输入框
//              etEmail = (EditText) findViewById(R.id.et_email);
                etEmail.setFocusable(true);//设置输入框可聚集
                etEmail.setFocusableInTouchMode(true);//设置触摸聚焦
                etEmail.requestFocus();//请求焦点
                etEmail.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etEmail, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.btn_iv_back:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                closeProgressDialog();
                handleMerShopInfo((MerShopInfo) obj);
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                closeProgressDialog();
                Utils.showToast(this, "加载失败:" + statusCode + "!");
                break;
        }
    }


    private void handleMerShopInfo(MerShopInfo tmp) {
        merShopInfo = tmp;
        tvMerchantCode.setText(tmp.getMerchantCode());
        tvMerchantName.setText(tmp.getMerchantName());
        tvShopNo.setText(tmp.getShopNo());
        etShopName.setText(tmp.getShopName());
        etShopAddr.setText(tmp.getShopAddress());
        etContactor.setText(tmp.getContactor());
        etTelNo.setText(tmp.getTelNo());
        etMobileNo.setText(tmp.getTelNo1());
        etEmail.setText(tmp.getEmail());

        if("VALID".equals(tmp.getMerchantStatus())){
            tvShopStatus.setText("有效");
        }else if("INVALID".equals(tmp.getMerchantStatus())){
            tvShopStatus.setText("无效");
        }else{
            tvShopStatus.setText("未知");
        }

    }

    /**
     * 下一步
     */
    private void doNext(){
        //网点名称
        if(TextUtils.isEmpty(etShopName.getText())){
            Utils.showToast(this,"网点名称不能为空！");
            return;
        }else{
            merShopInfo.setShopName(etShopName.getText().toString());
        }
        //网点地址
        if(TextUtils.isEmpty(etShopAddr.getText())){
            Utils.showToast(this,"网点地址不能为空！");
            return;
        }else{
            merShopInfo.setShopAddress(etShopAddr.getText().toString());
        }

        //网点联系人
        if(TextUtils.isEmpty(etContactor.getText())){
            Utils.showToast(this,"网点联系人不能为空！");
            return;
        }else{
            merShopInfo.setContactor(etContactor.getText().toString());
        }

        //网点人手机
        if(TextUtils.isEmpty(etShopAddr.getText())){
            Utils.showToast(this,"联系人手机不能为空！");
            return;
        }else{
            merShopInfo.setTelNo(etTelNo.getText().toString());
        }

        //固定电话
        if(TextUtils.isEmpty(etTelNo.getText())){
//            Utils.showToast(this,"固定电话不能为空！");
//            return;
        }else{
            merShopInfo.setTelNo1(etMobileNo.getText().toString());
        }

        //邮箱地址
        if(TextUtils.isEmpty(etEmail.getText())){
//            Utils.showToast(this,"邮箱地址不能为空！");
//            return;
        }else{
            merShopInfo.setEmail(etEmail.getText().toString());
        }


        visitReqInfo.setShopChangeRecordVO(merShopInfo);


        //信息编辑验证 这里没有必选条件 提示用户是否确认信息提交
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "已核实完网点基本信息？",
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
                    UiUtils.startActivityWithExObj(StepEditShopInfoActivity.this,StepMarkShopInfoActivity.class,
                            Constants.EXTRAS_MER_VISIT_INFO,visitReqInfo);
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

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     *
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == Constants.TAKE_MER_LOCATION ){
            String result = data.getExtras().getString((Constants.ADDR_STR));//得到新Activity 关闭后返回的数据
            etShopAddr.setText(result);
        }
    }

}

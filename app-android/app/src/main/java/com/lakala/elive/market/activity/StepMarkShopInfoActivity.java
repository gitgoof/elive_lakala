package com.lakala.elive.market.activity;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.common.net.req.VisitOrEditReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.Logger;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.adapter.DictDetailListAdpter;
import com.lakala.elive.user.base.BaseActivity;


/**
 *
 * Step2
 *
 * 给商户网点打标签
 *
 * @author hongzhiliang
 *
 */
public class StepMarkShopInfoActivity extends BaseActivity {

    private MerShopInfo merShopInfo = null;
    private VisitOrEditReqInfo visitReqInfo = new VisitOrEditReqInfo();

    DictDetailListAdpter mMerInfoTruthAdapter;
    DictDetailListAdpter mMerMatureAdapter;
    DictDetailListAdpter mMerLevelAdapter;
    DictDetailListAdpter mMerMainSaleAdapter;
    DictDetailListAdpter mMerClassAdapter;
    DictDetailListAdpter mMerShopAreaAdapter;
//    DictDetailListAdpter mProductClassAdapter;

    DictDetailListAdpter mCardAppTypeAdapter;
    DictDetailListAdpter mOtherPosAdvanceAdapter;

    DictDetailListAdpter mBindElePlatformResultAdapter;
    DictDetailListAdpter mIsExchangeSignAdapter;
    DictDetailListAdpter mNoBindElePlatformReasonAdapter;

    protected Spinner spinnerMerInfoTruth;
    protected Spinner spinnerMerMature;
    protected Spinner spinnerMerLevel;
    protected Spinner spinnerMerMainSale;
    protected Spinner spinnerMerClass;
    protected Spinner spinnerMerShopArea;
//    protected Spinner spinnerProductClass;
    protected Spinner spinnerCardAppType;
    protected Spinner spinnerOtherPosAdvance;
    protected Spinner spinnerBindElePlatformResult;
    protected Spinner spinnerNoBindElePlatformReason;
    protected Spinner spinnerIsExchangeSign;

    private Button btnSubmitEdit; //提交信息
    private Button btnPreEdit; //上一步信息

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_step_mark_mer_info);
    }

    @Override
    protected void bindView() {
        //提交按钮
        btnSubmitEdit = (Button) findViewById(R.id.btn_submit_edit);
        btnPreEdit = (Button) findViewById(R.id.btn_pre_edit);

        spinnerMerInfoTruth = (Spinner) findViewById(R.id.spinner_mer_info_truth);
        spinnerMerMature = (Spinner) findViewById(R.id.spinner_mer_mature);
        spinnerMerLevel = (Spinner) findViewById(R.id.spinner_mer_level);
        spinnerMerMainSale = (Spinner) findViewById(R.id.spinner_mer_main_sale);
        spinnerMerClass = (Spinner) findViewById(R.id.spinner_mer_class);
        spinnerMerShopArea = (Spinner) findViewById(R.id.spinner_mer_shop_area);

        spinnerCardAppType = (Spinner) findViewById(R.id.spinner_card_app_type);
        spinnerOtherPosAdvance = (Spinner) findViewById(R.id.spinner_other_pos_advance);

        spinnerBindElePlatformResult = (Spinner) findViewById(R.id.spinner_bind_ele_platform_result);
        spinnerNoBindElePlatformReason = (Spinner) findViewById(R.id.spinner_no_bind_ele_platform_reason);
        spinnerIsExchangeSign = (Spinner) findViewById(R.id.spinner_is_exchange_sign);

    }


    @Override
    protected void bindEvent() {
        btnSubmitEdit.setOnClickListener(this);
        btnPreEdit.setOnClickListener(this);

        //商户真实性核查
        mMerInfoTruthAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_INFO_TRUTH));
        spinnerMerInfoTruth.setAdapter(mMerInfoTruthAdapter);
        spinnerMerInfoTruth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo)mMerInfoTruthAdapter.getItem(position);
                merShopInfo.setMerInfoTruth(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });

        //商户成熟度
        mMerMatureAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_MATURE));
        spinnerMerMature.setAdapter(mMerMatureAdapter);
        spinnerMerMature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mMerMatureAdapter.getItem(position);
                merShopInfo.setMerMature(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //商户等级
        mMerLevelAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_LEVEL));
        spinnerMerLevel.setAdapter(mMerLevelAdapter);
        spinnerMerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mMerLevelAdapter.getItem(position);
                merShopInfo.setMerLevel(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //商户主营
        mMerMainSaleAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_MAIN_SALE));
        spinnerMerMainSale.setAdapter(mMerMainSaleAdapter);
        spinnerMerMainSale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mMerMainSaleAdapter.getItem(position);
                merShopInfo.setMerMainSale(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //商户分类
        mMerClassAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_CLASS));
        spinnerMerClass.setAdapter(mMerClassAdapter);
        spinnerMerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mMerClassAdapter.getItem(position);
                merShopInfo.setMerClass(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //商铺位置
        mMerShopAreaAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.MER_SHOP_AREA));

        spinnerMerShopArea.setAdapter(mMerShopAreaAdapter);
        spinnerMerShopArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mMerShopAreaAdapter.getItem(position);
                merShopInfo.setMerShopArea(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //商户已有产品
        mCardAppTypeAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.CARD_APP_TYPE));
        spinnerCardAppType.setAdapter(mCardAppTypeAdapter);
        spinnerCardAppType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mCardAppTypeAdapter.getItem(position);
                merShopInfo.setCardAppType(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //其他POS优势
        mOtherPosAdvanceAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.OTHER_POS_ADVANCE));
        spinnerOtherPosAdvance.setAdapter(mOtherPosAdvanceAdapter);
        spinnerOtherPosAdvance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mOtherPosAdvanceAdapter.getItem(position);
                merShopInfo.setOtherPosAdvance(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //电子平台绑定结果
        mBindElePlatformResultAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.BIND_ELE_PLATFORM_RESULT));
        spinnerBindElePlatformResult.setAdapter(mBindElePlatformResultAdapter);
        spinnerBindElePlatformResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mBindElePlatformResultAdapter.getItem(position);
                merShopInfo.setBindElePlatformResult(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //电子平方台未绑定原因
        mNoBindElePlatformReasonAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.NO_BIND_ELE_PLATFORM_REASON));
        spinnerNoBindElePlatformReason.setAdapter(mNoBindElePlatformReasonAdapter);
        spinnerNoBindElePlatformReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mNoBindElePlatformReasonAdapter.getItem(position);
                merShopInfo.setNoBindElePlatformReason(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //是否换签
        mIsExchangeSignAdapter = new DictDetailListAdpter(this, mSession.getSysDictMapByKey(Constants.IS_EXCHANGE_SIGN));
        spinnerIsExchangeSign.setAdapter(mIsExchangeSignAdapter);
        spinnerIsExchangeSign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DictDetailInfo dictDetailInfo = (DictDetailInfo) mIsExchangeSignAdapter.getItem(position);
                merShopInfo.setIsExchangeSign(dictDetailInfo.getDictKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点标签核实编辑");
        visitReqInfo = (VisitOrEditReqInfo) getIntent().getExtras().get(Constants.EXTRAS_MER_VISIT_INFO);
        merShopInfo= visitReqInfo.getShopChangeRecordVO();

//        商户真实性核查	merInfoTruth
        if(merShopInfo.getMerInfoTruth()!=null
                && !"".equals(merShopInfo.getMerInfoTruth()) && !"-1".equals(merShopInfo.getMerInfoTruth()) ){
            spinnerMerInfoTruth.setSelection(mMerInfoTruthAdapter.getValueByKey(merShopInfo.getMerInfoTruth()));
        }else{
            spinnerMerInfoTruth.setSelection(0);
        }

//      商户成熟度	merMature
        if(merShopInfo.getMerMature()!=null
                && !"".equals(merShopInfo.getMerMature()) && !"-1".equals(merShopInfo.getMerMature()) ){
            spinnerMerMature.setSelection(mMerMatureAdapter.getValueByKey(merShopInfo.getMerMature()));
        }else {
            spinnerMerMature.setSelection(0);
        }

//        商户等级	merLevel
        if(merShopInfo.getMerLevel()!=null
                && !"".equals(merShopInfo.getMerLevel()) && !"-1".equals(merShopInfo.getMerLevel()) ){
            spinnerMerLevel.setSelection(mMerLevelAdapter.getValueByKey(merShopInfo.getMerLevel()));
        }else{
            spinnerMerLevel.setSelection(0);
        }

//        商户主营	merMainSale
        if(merShopInfo.getMerMainSale()!=null
                && !"".equals(merShopInfo.getMerMainSale()) && !"-1".equals(merShopInfo.getMerMainSale()) ){
            spinnerMerMainSale.setSelection(mMerMainSaleAdapter.getValueByKey(merShopInfo.getMerMainSale()));
        }else{
            spinnerMerMainSale.setSelection(0);
        }


//        商户分类	merClass
        if(merShopInfo.getMerClass()!=null
                && !"".equals(merShopInfo.getMerClass()) && !"-1".equals(merShopInfo.getMerClass()) ){
            spinnerMerClass.setSelection(mMerClassAdapter.getValueByKey(merShopInfo.getMerClass()));
        }else{
            spinnerMerClass.setSelection(0);
        }

//        商铺位置	merShopArea
        if(merShopInfo.getMerShopArea()!=null
                && !"".equals(merShopInfo.getMerShopArea()) && !"-1".equals(merShopInfo.getMerShopArea()) ){
            spinnerMerShopArea.setSelection(mMerShopAreaAdapter.getValueByKey(merShopInfo.getMerShopArea()));
        }else{
            spinnerMerShopArea.setSelection(0);
        }

//        商户已有产品	cardAppType
        if(merShopInfo.getCardAppType()!=null
                && !"".equals(merShopInfo.getCardAppType()) && !"-1".equals(merShopInfo.getCardAppType()) ){
            spinnerCardAppType.setSelection(mCardAppTypeAdapter.getValueByKey(merShopInfo.getCardAppType()));
        }else{
            spinnerCardAppType.setSelection(0);
        }

//        其他POS优势	otherPosAdvance
        if(merShopInfo.getOtherPosAdvance()!=null
                && !"".equals(merShopInfo.getOtherPosAdvance()) && !"-1".equals(merShopInfo.getOtherPosAdvance()) ){
            spinnerOtherPosAdvance.setSelection(mOtherPosAdvanceAdapter.getValueByKey(merShopInfo.getOtherPosAdvance()));
        }else{
            spinnerOtherPosAdvance.setSelection(0);
        }


//        电子平台绑定结果	bindElePlatformResult
        if(merShopInfo.getBindElePlatformResult()!=null
                && !"".equals(merShopInfo.getBindElePlatformResult()) && !"-1".equals(merShopInfo.getBindElePlatformResult()) ){
            spinnerBindElePlatformResult.setSelection(mBindElePlatformResultAdapter.getValueByKey(merShopInfo.getBindElePlatformResult()));
        }else{
            spinnerBindElePlatformResult.setSelection(0);
        }

//        电子平方台未绑定原因	noBindElePlatformReason
        if(merShopInfo.getNoBindElePlatformReason()!=null
                && !"".equals(merShopInfo.getNoBindElePlatformReason()) && !"-1".equals(merShopInfo.getNoBindElePlatformReason()) ){
            spinnerNoBindElePlatformReason.setSelection(mNoBindElePlatformReasonAdapter.getValueByKey(merShopInfo.getNoBindElePlatformReason()));
        }else {
            spinnerNoBindElePlatformReason.setSelection(0);
        }

//      是否换签	isExchangeSign
        if(merShopInfo.getIsExchangeSign()!=null
                && !"".equals(merShopInfo.getIsExchangeSign()) && !"-1".equals(merShopInfo.getIsExchangeSign()) ){
            spinnerIsExchangeSign.setSelection(mIsExchangeSignAdapter.getValueByKey(merShopInfo.getIsExchangeSign()));
        }else{
            spinnerIsExchangeSign.setSelection(0);
        }


//        iBtnBack = (ImageButton) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

//        btnCancel = (Button) findViewById(R.id.btn_action);
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_edit:
                doNext(); //进入终端列表页面
                break;
            case R.id.btn_pre_edit:
                doPrevious(); //返回上一步
                break;
            case R.id.btn_iv_back:
                doPrevious(); //返回上一步
                break;
            case R.id.btn_action:
                doCancel();
                break;
            default:
                break;
        }
    }

    private void doCancel() {
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "取消编辑和签到？",
                "取消",
                "确定",
                mCancleListener
        ).show();
    }

    /**
     * 下一步
     */
    private void doNext(){
        //信息编辑验证 这里没有必选条件 提示用户是否确认信息提交
//        商户真实性核查	merInfoTruth
        if(merShopInfo.getMerInfoTruth()!=null
                && !"".equals(merShopInfo.getMerInfoTruth()) && !"-1".equals(merShopInfo.getMerInfoTruth()) ){
        }else{
            Utils.showToast(this,"商户真实性核查不能为空！");
            return;
        }

//      商户成熟度	merMature
        if(merShopInfo.getMerMature()!=null
                && !"".equals(merShopInfo.getMerMature()) && !"-1".equals(merShopInfo.getMerMature()) ){

        }else {
            Utils.showToast(this,"商户成熟度核查不能为空！");
            return;
        }

//        商户等级	merLevel
        if(merShopInfo.getMerLevel()!=null
                && !"".equals(merShopInfo.getMerLevel()) && !"-1".equals(merShopInfo.getMerLevel()) ){

        }else{
            Utils.showToast(this,"商户等级核查不能为空！");
            return;
        }

//        商户主营	merMainSale
        if(merShopInfo.getMerMainSale()!=null
                && !"".equals(merShopInfo.getMerMainSale()) && !"-1".equals(merShopInfo.getMerMainSale()) ){

        }else{
            Utils.showToast(this,"商户主营核查不能为空！");
            return;
        }


//        商户分类	merClass
        if(merShopInfo.getMerClass()!=null
                && !"".equals(merShopInfo.getMerClass()) && !"-1".equals(merShopInfo.getMerClass()) ){

        }else{
            Utils.showToast(this,"商户分类核查不能为空！");
            return;
        }

//        商铺位置	merShopArea
        if(merShopInfo.getMerShopArea()!=null
                && !"".equals(merShopInfo.getMerShopArea()) && !"-1".equals(merShopInfo.getMerShopArea()) ){

        }else{
            Utils.showToast(this,"商铺位置核查不能为空！");
            return;
        }


//        商户已有产品	cardAppType
//        if(merShopInfo.getCardAppType()!=null
//                && !"".equals(merShopInfo.getCardAppType()) && !"-1".equals(merShopInfo.getCardAppType()) ){
//
//        }else{
//            Utils.showToast(this,"商户已有产品核查不能为空！");
//            return;
//        }

//        其他POS优势	otherPosAdvance
//        if(merShopInfo.getOtherPosAdvance()!=null
//                && !"".equals(merShopInfo.getOtherPosAdvance()) && !"-1".equals(merShopInfo.getOtherPosAdvance()) ){
//
//        }else{
//            Utils.showToast(this,"其他POS优势核查不能为空！");
//            return;
//        }


//        电子平台绑定结果	bindElePlatformResult
        if(merShopInfo.getBindElePlatformResult()!=null
                && !"".equals(merShopInfo.getBindElePlatformResult()) && !"-1".equals(merShopInfo.getBindElePlatformResult()) ){

        }else{
            Utils.showToast(this,"电子平台绑定结果核查不能为空！");
            return;
        }


//      是否换签	isExchangeSign
        if(merShopInfo.getIsExchangeSign()!=null
                && !"".equals(merShopInfo.getIsExchangeSign()) && !"-1".equals(merShopInfo.getIsExchangeSign()) ){

        }else{
            Utils.showToast(this,"是否换签核查不能为空！");
            return;
        }

        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "已核实完网点标签信息？",
                "取消",
                "确定",
                mNextListener
        ).show();
    }

    /**
     * 上一步
     *
     */
    private void doPrevious(){
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "返回上一步？",
                "取消",
                "确定",
                mPreListener
        ).show();

    }

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mNextListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    UiUtils.startActivityWithExObj(StepMarkShopInfoActivity.this,StepTermListActivity.class,
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

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mPreListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    UiUtils.startActivityWithExObj(StepMarkShopInfoActivity.this,StepEditShopInfoActivity.class,
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

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mCancleListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            doPrevious();
        }
        return false;
    }



}

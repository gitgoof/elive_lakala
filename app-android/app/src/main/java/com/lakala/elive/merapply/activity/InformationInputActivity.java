package com.lakala.elive.merapply.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.BankCardVerificationReq;
import com.lakala.elive.beans.BankCardVerificationResp;
import com.lakala.elive.beans.MerApplyInfo;
import com.lakala.elive.beans.MerAttachFile;
import com.lakala.elive.beans.MerOpenInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.req.PhotoDiscernReq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerApplyInfoRes;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.net.resp.PhotoDiscernResp;
import com.lakala.elive.common.utils.DateUtil;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.EditUtil;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.common.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import exocr.bankcard.BankManager;
import exocr.bankcard.DataCallBack;
import exocr.bankcard.EXBankCardInfo;


/**
 * 进件
 * Created by wenhaogu on 2016/12/29.
 */

public class InformationInputActivity extends BaseActivity implements OptionsPickerView.OnOptionsSelectListener, DataCallBack {

    private TextView tvScanCardNumber, tvSelectBankName, tvIdDate;
    private EditText edtCardNumber, edtName, edtIdNumber;
    private TextView tvCardNum;
    private Button btnNext;
    private ImageView imgProgress;

    private OptionsPickerView pickerView;
    private TimePickerView pvTime;

    private MerApplyInfoReq merApplyInfoReq;
    private String name, cardNumber, IDNumber, IDDate, bankName;
    private ArrayList<String> bankList;
    private List<MerDictionaryResp.ContentBean.ItemsBean> items;

    public static final String APPLY_ID = "applyId"; //进件id

    public static String ID = "id"; //bmcp唯一编号

    private String accountKind;

    private String merchantId;

    private MerApplyDetailsResp.ContentBean contentBean;

    private InputMethodManager imm;//键盘服务

    //    private Uri imageUri;
    private PhotoDiscernResp.ContentBean photoDiscernContent;
    private String applyId;

    private RelativeLayout mRelativePeopleName;
    private View mView21;
    private EditText mEditPeopleName;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_settleaccounts_input);
    }

    private TextView btnIdPerpetual;
    private TextView mTvNameFirst;
    @Override
    protected void bindView() {

        mTvNameFirst = findView(R.id.name);
        //名字
        edtName = findView(R.id.edt_settle_name);

        mRelativePeopleName = findView(R.id.relative_people_name);
        mView21 = findView(R.id.v21);
        mEditPeopleName = findView(R.id.edit_people_name);

        //身份证号码
        edtIdNumber = findView(R.id.edt_id_number);
        //手机号码
        //edtPhone = findView(R.id.edt_phone);

        //身份证有效期
        tvIdDate = findView(R.id.tv_id_date);
        //身份证永久按钮
        btnIdPerpetual = findView(R.id.btn_id_perpetual);
        //扫码卡号
        tvScanCardNumber = findView(R.id.tv_scan_card_number);
        tvCardNum = findView(R.id.card_number);
        //填写卡号
        edtCardNumber = findView(R.id.edt_card_number);
        //开户银行
        tvSelectBankName = findView(R.id.tv_select_bank_name);

        //下一步
        btnNext = findView(R.id.btn_next);

        timePickerView();
        pickerView = new OptionsPickerView(this);//选择器

        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("结算信息");

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        imgProgress = findView(R.id.img_progress);
        EnterPieceActivity.setImageProgress(imgProgress, 2);
    }
//    private Map<String,String> mSaveCache = null;
    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        tvScanCardNumber.setOnClickListener(this);
        tvSelectBankName.setOnClickListener(this);
        pickerView.setOnoptionsSelectListener(this);
        tvIdDate.setOnClickListener(this);
        btnIdPerpetual.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        edtCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String  cardNum =  edtCardNumber.getText().toString();
                    if(StringUtil.isNotNullAndBlank(cardNum) && cardNum.length() > 8){
                        reqBmcpCardBankInfo(cardNum);
                    }
                }
            }

        });

        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String  cardNum =  edtCardNumber.getText().toString();
                if(StringUtil.isNotNullAndBlank(cardNum) && cardNum.length() > 8){
                    reqBmcpCardBankInfo(cardNum);
                }
            }
        });

        EditUtil.setEditInputType(mEditPeopleName,26,true);
    }

    @Override
    protected void bindData() {

        Intent intent = getIntent();
        accountKind = intent.getStringExtra(EnterPieceActivity.ACCOUNT_KIND);
        photoDiscernContent = (PhotoDiscernResp.ContentBean) intent.getSerializableExtra(PrivatePhotoOcrActivity.PHOTO_DISCERN_RESP);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        applyId = intent.getStringExtra("applyId");
        // TODO 对公  对私  判断
        if(!TextUtils.isEmpty(accountKind)){
            tvCardNum.setText(accountKind.equals("58")?"卡号":"入款账号");
            tvScanCardNumber.setVisibility(accountKind.equals("58")?View.VISIBLE:View.INVISIBLE);
            if(accountKind.equals("58")?false:true){
                edtName.setFilters(new InputFilter[]{EditUtil.getChinaInputFilter(100,false)});
                mRelativePeopleName.setVisibility(View.VISIBLE);
                mView21.setVisibility(View.VISIBLE);
                mTvNameFirst.setText("账户名称");
            } else {
                edtName.setFilters(new InputFilter[]{EditUtil.getChinaInputFilter(26,true)});
                mRelativePeopleName.setVisibility(View.GONE);
                mView21.setVisibility(View.GONE);
                mTvNameFirst.setText("入账户名");
            }
        }

        showProgressDialog("加载中...");//银行字典查询
        NetAPI.merDictionaryReq(this, this, new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "BANK"), NetAPI.BANK);
        bankList = new ArrayList<>();

        //初始化请求bean
        merApplyInfoReq = new MerApplyInfoReq(mSession.getUserLoginInfo().getAuthToken());
        MerApplyInfo merApplyInfo = new MerApplyInfo();
        MerOpenInfo merOpenInfo = new MerOpenInfo();
        merApplyInfoReq.setMerApplyInfo(merApplyInfo);
        merApplyInfoReq.setMerOpenInfo(merOpenInfo);

        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this,
                    new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }
        /*
        if(DictionaryUtil.getInstance().getmApplication() == null){
            DictionaryUtil.getInstance().init(getApplication());
        }
        if(!TextUtils.isEmpty(applyId)){
            mDataForSave = DictionaryUtil.getInstance().getValue(applyId,DictionaryUtil.DataForSave.class);
        } else {
            mDataForSave = DictionaryUtil.getInstance().getValue(DictionaryUtil.NEW_DEFAULT_KEY,DictionaryUtil.DataForSave.class);
        }
        if(mDataForSave == null){
            mDataForSave = new DictionaryUtil.DataForSave();
        }
        mSaveCache = mDataForSave.getMaps();
        */
    }
    /*
    @Override
    protected void onPause() {
        super.onPause();
        saveHasDataToCache(edtName,"edit_name");
        saveHasDataToCache(edtIdNumber,"edit_id_number");
        saveHasDataToCache(tvIdDate,"edit_id_date");

        saveHasDataToCache(edtCardNumber,"edit_card_number");
        saveHasDataToCache(tvSelectBankName,"edit_bank_name");

        if(!TextUtils.isEmpty(applyId)){
            DictionaryUtil.getInstance().saveData(applyId,mDataForSave);
        } else {
            DictionaryUtil.getInstance().saveData(DictionaryUtil.NEW_DEFAULT_KEY,mDataForSave);
        }
    }

    private void saveHasDataToCache(TextView textView,String key){
        String name = textView.getText().toString();
        if(TextUtils.isEmpty(name)){
            mSaveCache.put(key,"");
        } else {
            mSaveCache.put(key,name);
        }
    }
    private DictionaryUtil.DataForSave mDataForSave;
    */

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {

            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //如果有数据,就回显到控件上
            if (null != contentBean && null != contentBean.getMerOpenInfo()) {//编辑回显
                MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo1 = contentBean.getMerOpenInfo();
                if (!TextUtils.isEmpty(merOpenInfo1.getAccountName())) {
                    edtName.setText(merOpenInfo1.getAccountName());
                }

                if (!TextUtils.isEmpty(merOpenInfo1.getIdCard())) {
                    edtIdNumber.setText(merOpenInfo1.getIdCard());
                }
                if (!TextUtils.isEmpty(merOpenInfo1.getAccountNo())) {
                    edtCardNumber.setText(merOpenInfo1.getAccountNo());
                }
                if(accountKind.equals("58")?false:true){
                    if(!TextUtils.isEmpty(merOpenInfo1.getIdName())){
                        mEditPeopleName.setText(merOpenInfo1.getIdName());
                    }
                }
                if (merOpenInfo1.getIdCardExpire()!=null&&merOpenInfo1.getIdCardExpire().length() > 0) {
//                    tvIdDate.setText(getTime(new Date(Long.parseLong(merOpenInfo1.getIdCardExpire()))));
                    tvIdDate.setText(merOpenInfo1.getIdCardExpire());
                }
                if (!TextUtils.isEmpty(merOpenInfo1.getOpenningBankName())) {
                    tvSelectBankName.setText(merOpenInfo1.getOpenningBankName());
                }
                merApplyInfoReq.getMerOpenInfo().setOpenningBank(merOpenInfo1.getOpenningBank());
            }
            // TODO 初始化信息
            /*
            initTextViewData(edtName,"edit_name");
            initTextViewData(edtIdNumber,"edit_id_number");
            initTextViewData(tvIdDate,"edit_id_date");

            initTextViewData(edtCardNumber,"edit_card_number");
            initTextViewData(tvSelectBankName,"edit_bank_name");
            */
            if (null != photoDiscernContent) {
                setTextContent();
            }
        }
        switch (method) {
            case NetAPI.BANK:
                MerDictionaryResp.ContentBean content = ((MerDictionaryResp) obj).getContent();
                items = content.getItems();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : items) {
                    bankList.add(itemsBean.getValue());
                }
                break;
            case NetAPI.ACTION_MER_APPLY://对公
                final MerApplyInfoRes merApplyInfoRes = (MerApplyInfoRes) obj;
                if (merApplyInfoRes.getContent().getId() < 1) {
                    Utils.showToast(this, "您的身份证或银行卡已经进过件");
                    break;
                }
                String peopleName = name;
                if(accountKind.equals("58")?false:true){
                    peopleName = mEditPeopleName.getText().toString().trim();
                }
                Map<String,String> maps = merApplyInfoRes.getContent().getValidInfoMap();
                if(maps != null && maps.size() == 1){
                    final String intentPoepName = peopleName;
                    String string = maps.get(merApplyInfoRes.resultTypeList[0]);
                    if(!TextUtils.isEmpty(string)) {
                        DialogUtil.createAlertDialog(
                                this,
                                "注意",
                                string,
                                "取消",
                                "继续",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        switch (which) {
                                            case AlertDialog.BUTTON_POSITIVE:// 确定
                                                startActivity(new Intent(InformationInputActivity.this, FaceRecognitionActivity.class)
                                                        .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
                                                        .putExtra(ID, merApplyInfoRes.getContent().getId())
                                                        .putExtra(EnterPieceActivity.ACCOUNT_KIND, accountKind)
                                                        .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                                                        .putExtra("name", intentPoepName)
                                                        .putExtra("IDNumber", IDNumber));

                                                break;
                                            case AlertDialog.BUTTON_NEGATIVE:// 取消
                                                break;
                                        }
                                    }
                                }
                        ).show();
                        return;
                    }
                }

                startActivity(new Intent(InformationInputActivity.this, FaceRecognitionActivity.class)
                        .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
                        .putExtra(ID, merApplyInfoRes.getContent().getId())
                        .putExtra(EnterPieceActivity.ACCOUNT_KIND, accountKind)
                        .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                        .putExtra("name", peopleName)
                        .putExtra("IDNumber", IDNumber));

//                if (accountKind.equals(EnterPieceActivity.PUBLIC_ACCOUNT)) {//对公
//                    startActivityForResult(new Intent(this, BusinessLicenseDiscernActivity.class)
//                                    .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
//                                    .putExtra(ID, merApplyInfoRes.getContent().getId())
//                                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
//                                    .putExtra("ContentBean", contentBean)
//                            , EnterPieceActivity.REQUEST_FINISH_CODE);
//                } else {//对私
//                    startActivityForResult(new Intent(this, PayeeProveInfoActivity.class)
//                                    .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
//                                    .putExtra(ID, merApplyInfoRes.getContent().getId())
//                                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
//                                    .putExtra("ContentBean", contentBean)
//                            , EnterPieceActivity.REQUEST_FINISH_CODE);
//                }

                break;
            case NetAPI.ACTION_BANK_VERIFICATION:
                BankCardVerificationResp bankCardVerificationResp = (BankCardVerificationResp) obj;
                if(StringUtil.isNullOrBlank(bankCardVerificationResp.getContent().getBankName())){
                    tvSelectBankName.setText("其他非常见银行");
                }else {
                    tvSelectBankName.setText(bankCardVerificationResp.getContent().getBankName());
                }
                break;
            case NetAPI.ACTION_PHOTO_DISCERN:
//                Utils.showToast(this, "上传成功");

        }

    }
    /*
    private void initTextViewData(TextView textView,String key){
        String name = textView.getText().toString();
        if(TextUtils.isEmpty(name)){
            String value = mSaveCache.get(key);
            if(!TextUtils.isEmpty(value)){
                textView.setText(value);
            }
        } else {
            mSaveCache.put(key,name);
        }
    }
    */
    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_BANK_VERIFICATION) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        }  else if( method == NetAPI.ACTION_PHOTO_DISCERN){
            Utils.showToast(this, "上传失败");
        }
    }

    /**
     * 设置显示数据
     */
    private void setTextContent() {
        if (null != photoDiscernContent.getIdCardInfo()) {
            if(accountKind.equals("58")?false:true){
                if (!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getName())) {
                    mEditPeopleName.setText(photoDiscernContent.getIdCardInfo().getName());
                }
            } else {
                if (!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getName())) {
                    edtName.setText(photoDiscernContent.getIdCardInfo().getName());
                }
            }
            /*
            if(!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getName())){
                edtName.setText(photoDiscernContent.getIdCardInfo().getName());
            }
            */
            if(!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getId_number())){
                edtIdNumber.setText(photoDiscernContent.getIdCardInfo().getId_number());
            }
        }

        if (null != photoDiscernContent.getIdCardInfoBack()) {
            String validity = photoDiscernContent.getIdCardInfoBack().getValidity();
            if (!TextUtils.isEmpty(validity)) {
                String substring = validity.substring(validity.indexOf("-") + 1, validity.length());
                tvIdDate.setText(substring.replace(".", "-"));
            }
        }

        if (null != photoDiscernContent.getBankCardInfo()) {//银行卡识别出来直接显示
            edtCardNumber.setText(photoDiscernContent.getBankCardInfo().getCard_number().replaceAll(" ", ""));
            tvSelectBankName.setText(photoDiscernContent.getBankCardInfo().getIssuer());
        }

        merApplyInfoReq.getMerApplyInfo().setApplyId(photoDiscernContent.getApplyId());
        merApplyInfoReq.getMerOpenInfo().setApplyId(photoDiscernContent.getApplyId());
        merApplyInfoReq.getMerOpenInfo().setMerchantId(merchantId);
    }

    private void reqBmcpCardBankInfo(String cardNo) {
        BankCardVerificationReq bankCardVerificationReq = new BankCardVerificationReq();
        bankCardVerificationReq.setCardNo(cardNo);
        NetAPI.bankCardVerification(InformationInputActivity.this, InformationInputActivity.this, bankCardVerificationReq);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_scan_card_number://扫码卡号
                //  Utils.showToast(this, "该功能在开发中");
                BankManager.getInstance().showLogo(false);
                BankManager.getInstance().recognize(this,this);
                break;
            case R.id.tv_select_bank_name://开户银行
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
                }
                selectBank();
                break;
            case R.id.tv_id_date://身份证有效期
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
                }
                pvTime.show();
                break;
            case R.id.btn_id_perpetual:
                tvIdDate.setText("9999-12-31");
                break;
            case R.id.btn_next:
                if (getTextContext()) {
                    next();
                }
                break;
        }
    }

    private void next() {

        if(accountKind.equals("58")?false:true){
            String peopleName = mEditPeopleName.getText().toString().trim();
            merApplyInfoReq.getMerOpenInfo().setIdName(peopleName);
        }

        merApplyInfoReq.getMerApplyInfo().setApplyChannel("01");
        merApplyInfoReq.getMerApplyInfo().setStatus("1");
        merApplyInfoReq.getMerApplyInfo().setApplyType("1");

        merApplyInfoReq.getMerOpenInfo().setIdCard(IDNumber);
        merApplyInfoReq.getMerOpenInfo().setIdCardExpire(IDDate);
        merApplyInfoReq.getMerOpenInfo().setAccountKind(accountKind);//对公对私
        merApplyInfoReq.getMerOpenInfo().setAccountName(name);
        merApplyInfoReq.getMerOpenInfo().setAccountNo(cardNumber);
        merApplyInfoReq.getMerOpenInfo().setOpenningBankName(bankName);

        if (!TextUtils.isEmpty(bankName)&&items!=null) {
            for (int x = 0; x < items.size(); x++) {//根据银行名字查找items里面对应的银行id
                if (bankName.equals(items.get(x).getValue()) || bankName.contains(items.get(x).getValue())) {
                    merApplyInfoReq.getMerOpenInfo().setOpenningBank(items.get(x).getId());
                    break;
                }
            }
            //如果没有找到匹配的银行,就用"其他非常见银行"的key
            if (TextUtils.isEmpty(merApplyInfoReq.getMerOpenInfo().getOpenningBank())) {
                for (int x = 0; x < items.size(); x++) {//根据银行名字查找items里面对应的银行id
                    if ("其他非常见银行".equals(items.get(x).getValue()) || "其他非常见银行".contains(items.get(x).getValue())) {
                        merApplyInfoReq.getMerOpenInfo().setOpenningBank(items.get(x).getId());
                    }
                }
            }
        }

        showProgressDialog("提交中...");
        NetAPI.merApply(this, this, merApplyInfoReq);
//
    }
    private String mPeopleName = "";
    private boolean getTextContext() {
        name = edtName.getText().toString().trim();//开户姓名
        cardNumber = edtCardNumber.getText().toString().trim();//卡号
        IDNumber = edtIdNumber.getText().toString().trim();//身份证号码
        // phone = edtPhone.getText().toString().trim();//银行预留手机号码
        IDDate = tvIdDate.getText().toString().trim();//身份证有效期
        bankName = tvSelectBankName.getText().toString().trim();//开户行

        if (TextUtils.isEmpty(name)) {
            Utils.showToast(this, "请输入账户名称");
            return false;
        }
        if(!TextUtils.isEmpty(accountKind)){
            if(accountKind.equals("58")?false:true){
                if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 5||cardNumber.length()>32) {
                    Utils.showToast(this, "请输入正确的银行卡号");
                    return false;
                }
            } else {
                if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 14||cardNumber.length()>32) {
                    Utils.showToast(this, "请输入正确的银行卡号");
                    return false;
                }
            }
            if(accountKind.equals("58")?false:true){
                mPeopleName = mEditPeopleName.getText().toString().trim();
                if(TextUtils.isEmpty(mPeopleName)){
                    Utils.showToast(this, "请输入法人姓名");
                    return false;
                }
                if(name.length() < 2){
                    Utils.showToast(this, "账户名称不能少于2位");
                    return false;
                }
                if(checkStrLength(name,100)){
                    Utils.showToast(this, "账户名称长度不能超过100字符");
                    return false;
                }
                if(checkStrLength(mPeopleName,26)){
                    Utils.showToast(this, "法人姓名长度不能超过52字符");
                    return false;
                }
            } else {
                if(name.length() < 2){
                    Utils.showToast(this, "入账户名不能少于2位");
                    return false;
                }
                if(checkStrLength(name,26)){
                    Utils.showToast(this, "入账户名长度不能超过52字符");
                    return false;
                }
            }
        }
        if(name.length() < 2){
            Utils.showToast(this, "法人姓名不能少于2位");
            return false;
        }
        if (TextUtils.isEmpty(IDNumber)) {
            Utils.showToast(this, "请输入身份证号码");
        } else if (IDNumber.length() <= 14) {
            Utils.showToast(this, "请输入正确的身份证号码");
        } else if (TextUtils.isEmpty(IDDate)) {
            Utils.showToast(this, "请选择身份证有效期");
        } else if (TextUtils.isEmpty(bankName)) {
            Utils.showToast(this, "请选择开户行");
        } else if (!checkValiDate(IDDate)){
            Utils.showToast(this, "身份证有效期格式不对");
        }else if (isOverdue(IDDate)) {
            Utils.showToast(this, "身份证已过期");
        } else if(checkPeopName()){
            Utils.showToast(this, "请输入法人姓名");
        } else {
            return true;
        }
        return false;

    }
    private boolean checkStrLength(String string,final int maxLength){
        if(TextUtils.isEmpty(string))return false;
        if(string.length()>maxLength)return true;

        int count = 0;
        final char[] chars = string.toCharArray();
        for(int i = 0 ;i < chars.length;i++){
            final char c = chars[i];
            if(c<128){
                count+=1;
            } else {
                count+=2;
            }
            if(count > maxLength){
                return true;
            }
        }
        return false;
    }
    private boolean checkPeopName(){
        if(TextUtils.isEmpty(accountKind)){
            return false;
        }
        if(accountKind.equals("58")?false:true){
            String string = mEditPeopleName.getText().toString().trim();
            if(TextUtils.isEmpty(string)){
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean checkValiDate(String string){
        if(!TextUtils.isEmpty(string)&&string.equals("9999-12-31")){
            return true;
        }
        return DateUtil.isValidDate(IDDate);
    }

    /**
     * 选择身份证有效期
     */
    private void timePickerView() {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();

        pvTime.setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 20);//今年到以后100年
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(onTimeSelectListener);
    }

    /**
     * 时间选择监听
     */
    TimePickerView.OnTimeSelectListener onTimeSelectListener = new TimePickerView.OnTimeSelectListener() {
        @Override
        public void onTimeSelect(Date date) {
            tvIdDate.setText(getTime(date));
        }
    };

    /**
     * 选择银行
     */
    private void selectBank() {
        pickerView.setPicker(bankList);
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);
        pickerView.show();
    }

    /**
     * 银行卡选择监听
     */
    @Override
    public void onOptionsSelect(int options1, int option2, int options3) {
        tvSelectBankName.setText(bankList.get(options1));
    }


    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 判断身份证时间是否过去
     *
     * @param time
     * @return
     */
    private boolean isOverdue(String time) {
        if(!TextUtils.isEmpty(time)&&time.equals("9999-12-31")){
            return false;
        }
        try {
            if (time.length() == 10) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(time);
                long timeStemp = date.getTime();
                String nowTime = simpleDateFormat.format(new Date());
                long currentTimeMillis = simpleDateFormat.parse(nowTime).getTime();
                if (timeStemp < currentTimeMillis) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Utils.showToast(InformationInputActivity.this, "时间格式不对");
            return true;
        }

        return false;
    }

    /**
     * 扫码银行卡回调
     *
     * @param success
     */
    @Override
    public void onBankCardDetected(boolean success) {
        if (success) {
            EXBankCardInfo exBankCardInfo = BankManager.getInstance().getCardInfo();
            edtCardNumber.setText(exBankCardInfo.strNumbers);
            if(!TextUtils.isEmpty(edtCardNumber.getText())){
                reqBmcpCardBankInfo(edtCardNumber.getText().toString());
            }
            MerAttachFile merAttachFile = new MerAttachFile();
            PhotoDiscernReq photoDiscernReq = new PhotoDiscernReq();
            photoDiscernReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            photoDiscernReq.setMerApplyInfo(new MerApplyInfo());
            photoDiscernReq.setMerAttachFileList(new ArrayList<MerAttachFile>());
            photoDiscernReq.getMerApplyInfo().setApplyChannel("01");
            photoDiscernReq.getMerApplyInfo().setApplyType("1");
            photoDiscernReq.getMerApplyInfo().setApplyId(applyId);
            photoDiscernReq.getMerApplyInfo().setAccountKind(accountKind);
            merAttachFile.setApplyId(applyId);
            merAttachFile.photoUploadType = Constants.MER_IMAGE_STEP_TYPE.TYPE_BANK_CARD;
            merAttachFile.setIsOcr(Constants.OCR_NO);
            merAttachFile.setFileType(Constants.MER_IMAGE_TYPE.BANK_CARD);
            merAttachFile.setSegment(Constants.MER_IMAGE_BIG_TYPE.APPLY_ACCOUNT);
            merAttachFile.setFileContent(ImageTools.bitmapToBase64(BankManager.getInstance().getFullCardImage()));
            photoDiscernReq.getMerAttachFileList().add(merAttachFile);
            NetAPI.photoDiscernReq(this, this, photoDiscernReq, merAttachFile.photoUploadType);
        }
    }

    @Override
    public void onCameraDenied() {

    }


}

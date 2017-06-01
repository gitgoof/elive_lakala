package com.lakala.elive.qcodeenter;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.lakala.elive.Constants;
import com.lakala.elive.EliveApplication;
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
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import exocr.bankcard.BankManager;
import exocr.bankcard.DataCallBack;
import exocr.bankcard.EXBankCardInfo;


/**
 * Q码进件的结算信息
 */

public class QCodeSettleInfoActivity extends BaseActivity implements OptionsPickerView.OnOptionsSelectListener, DataCallBack {

    private TextView tvScanCardNumber, tvSelectBankName, tvIdDate, btnIdPerpetual;
    private EditText edtCardNumber, edtName, edtIdNumber;
    private TextView mCardNum;
    private TextView mName;
    private Button btnNext;
    private ImageView imgProgress;

    private View view;
    private LinearLayout mLeagePersNameLin;
    private TextView mLeagePerNameTxt;
    private EditText mLeagePerNameEdt;

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
    private String qcodeComStatue;//不为空，且为1 可以认为是编辑状态


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_qcode_settleaccounts_input);
    }

    @Override
    protected void bindView() {
        view = findView(R.id.liner_legalperson_name_view);
        mLeagePersNameLin = findView(R.id.liner_legalperson_name);
        mLeagePerNameTxt = findView(R.id.tv_legalperson_name);
        mLeagePerNameEdt = findView(R.id.edt_legalperson_name);

        //名字
        edtName = findView(R.id.edt_settle_name);
        //身份证号码
        edtIdNumber = findView(R.id.edt_id_number);
        //手机号码
        //edtPhone = findView(R.id.edt_phone);

        //身份证有效期
        tvIdDate = findView(R.id.tv_id_date);
        //身份证永久按钮
        btnIdPerpetual = findView(R.id.btn_id_perpetual);

        mCardNum = findView(R.id.card_number);
        mName = findView(R.id.name);

        //扫码卡号
        tvScanCardNumber = findView(R.id.tv_scan_card_number);
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

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        imgProgress = findView(R.id.img_progress);
        EliveApplication.setImageQCodeProgress(imgProgress, 2, EliveApplication.PUBLICQCODE);
    }

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
                    String cardNum = edtCardNumber.getText().toString();
                    if (StringUtil.isNotNullAndBlank(cardNum) && cardNum.length() > 8) {
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

            }
        });
//        edtName.setFilters(new InputFilter[]{getChinaInputFilter(50)});
        setEditInputType(mLeagePerNameEdt,26,true);
//        mLeagePerNameEdt.setFilters(new InputFilter[]{getChinaInputFilter(50)});
    }

    private void setEditInputType(EditText editText,final int maxLength,boolean onlyChina){
        if(editText == null)return;
        editText.setFilters(new InputFilter[]{getChinaInputFilter(maxLength,onlyChina)});
    }

    private InputFilter getChinaInputFilter(final int maxLength, final boolean onlyChina){
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for(int i = start ;i < end;i++){
                    if(!isChinese(source.charAt(i),onlyChina)){
                        return "";
                    }
                }

                int dindex = 0;
                int count = 0;
                while (count <= maxLength && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    return dest.subSequence(0, dindex - 1);
                }
                int sindex = 0;
                while (count <= maxLength && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        };
        return inputFilter;
    }


    private boolean isChinese(char c,boolean onlyChina) {
        Character.UnicodeBlock unicode = Character.UnicodeBlock.of(c);
        if(onlyChina){
            if(unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || unicode == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || unicode == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || unicode == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ){

                return true;
            }
        } else {
            if(unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || unicode == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || unicode == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || unicode == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || String.valueOf(c).matches("^[a-zA-Z]*")){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void bindData() {

        Intent intent = getIntent();
        accountKind = intent.getStringExtra(EnterPieceActivity.ACCOUNT_KIND);
        photoDiscernContent = (PhotoDiscernResp.ContentBean) intent.getSerializableExtra(QCodePrivatePhotoOcrActivity.PHOTO_DISCERN_RESP);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        applyId = intent.getStringExtra("applyId");
        qcodeComStatue = intent.getStringExtra(Constants.QCODE_COMPILE_STATUE);

        if ("57".equals(accountKind)) {//对公
            mName.setText("账户名称");
            mCardNum.setText("入款帐号");
            tvScanCardNumber.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            mLeagePersNameLin.setVisibility(View.VISIBLE);
            edtName.setFilters(new InputFilter[]{getChinaInputFilter(100,false)});
        }else{
            edtName.setFilters(new InputFilter[]{getChinaInputFilter(26,true)});
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

        if (!TextUtils.isEmpty(applyId)
//                &&qcodeComStatue!=null&&"1".equals(qcodeComStatue)
                ) {//编辑转态
            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }

    }


    private MerApplyInfoRes merApplyInfoRes;

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
                if (merOpenInfo1.getIdCardExpire() != null && merOpenInfo1.getIdCardExpire().length() > 0) {
                    tvIdDate.setText(merOpenInfo1.getIdCardExpire() + "");
                }
                if (!TextUtils.isEmpty(merOpenInfo1.getOpenningBankName())) {
                    tvSelectBankName.setText(merOpenInfo1.getOpenningBankName());
                }

                if ("57".equals(accountKind)) {//对公
                    if (!TextUtils.isEmpty(merOpenInfo1.getIdName())) {
                        mLeagePerNameEdt.setText(merOpenInfo1.getIdName());
                    }
                }

                merApplyInfoReq.getMerOpenInfo().setOpenningBank(merOpenInfo1.getOpenningBank());
            }

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
                merApplyInfoRes = (MerApplyInfoRes) obj;
//                if (merApplyInfoRes.getContent().getId() < 1) {
//                    Utils.showToast(this, "您的身份证或银行卡已经进过件");
//                    break;
//                }
                //判断身份证银行卡三要素
                if (merApplyInfoRes.getContent().getValidInfoMap() != null && merApplyInfoRes.getContent().getValidInfoMap().get("accountResult") != null) {
                    String tip = merApplyInfoRes.getContent().getValidInfoMap().get("accountResult");
                    showAlertDialog(tip);
                } else {
                    if ("57".equals(accountKind)) {//对公
                        name = mLeagePerNameEdt.getText().toString().trim();
                    }
                    startActivity(new Intent(QCodeSettleInfoActivity.this, QCodeFaceRecognitionActivity.class)
                            .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
                            .putExtra(ID, merApplyInfoRes.getContent().getId())
                            .putExtra(EnterPieceActivity.ACCOUNT_KIND, accountKind)
                            .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                            .putExtra("name", name)
                            .putExtra("IDNumber", IDNumber));
                }
                break;
            case NetAPI.ACTION_BANK_VERIFICATION:
                BankCardVerificationResp bankCardVerificationResp = (BankCardVerificationResp) obj;
                tvSelectBankName.setText(bankCardVerificationResp.getContent().getBankName());
                break;
            case NetAPI.ACTION_PHOTO_DISCERN:
                Utils.showToast(this, "上传成功");
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_BANK_VERIFICATION) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_PHOTO_DISCERN) {
            Utils.showToast(this, "上传失败");
        }
    }

    /**
     * 设置显示数据
     */
    private void setTextContent() {
        if ((qcodeComStatue != null && "1".equals(qcodeComStatue))) {//编辑
            if (null != photoDiscernContent.getIdCardInfo()) {
                if (!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getName())) {
                    if ("57".equals(accountKind)) {//对公
//                        mLeagePerNameEdt.setText(photoDiscernContent.getIdCardInf);
                        edtName.setText(photoDiscernContent.getIdCardInfo().getName());
                    } else {
                        edtName.setText(photoDiscernContent.getIdCardInfo().getName());
                    }
                }
                if (!TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getId_number())) {
                    edtIdNumber.setText(photoDiscernContent.getIdCardInfo().getId_number());
                }
            }

            if (null != photoDiscernContent.getIdCardInfoBack() && photoDiscernContent.getIdCardInfoBack().getValidity() != null) {
                String validity = photoDiscernContent.getIdCardInfoBack().getValidity();
                if (!TextUtils.isEmpty(validity)) {
                    String substring = validity.substring(validity.indexOf("-") + 1, validity.length());
                    tvIdDate.setText(substring.replace(".", "-"));
                }
            }

            if (null != photoDiscernContent.getBankCardInfo()) {//银行卡识别出来直接显示
                if (photoDiscernContent.getBankCardInfo().getCard_number() != null) {
                    edtCardNumber.setText(photoDiscernContent.getBankCardInfo().getCard_number().replaceAll(" ", ""));
                }
                if (photoDiscernContent.getBankCardInfo().getIssuer() != null) {
                    tvSelectBankName.setText(photoDiscernContent.getBankCardInfo().getIssuer());
                }
            }

        } else {//新建状态
            if ("57".equals(accountKind)) {//对公
                edtName.setText("");
                edtCardNumber.setText("");
                if (null != photoDiscernContent.getIdCardInfo() && photoDiscernContent.getIdCardInfo().getName() != null) {
                    mLeagePerNameEdt.setText(photoDiscernContent.getIdCardInfo().getName());
                }
            } else {
                if (photoDiscernContent != null) {
                    if (photoDiscernContent.getIdCardInfo() != null) {
                        if (photoDiscernContent.getIdCardInfo().getName() != null) {
                            edtName.setText(photoDiscernContent.getIdCardInfo().getName());
                        }
                    }
                    if (null != photoDiscernContent.getBankCardInfo() && photoDiscernContent.getBankCardInfo().getCard_number() != null) {//银行卡识别出来直接显示
                        edtCardNumber.setText(photoDiscernContent.getBankCardInfo().getCard_number().replaceAll(" ", ""));
                    }
                }
            }
            if (photoDiscernContent != null) {
                if (photoDiscernContent.getIdCardInfo() != null && !TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getId_number())) {
                    edtIdNumber.setText(photoDiscernContent.getIdCardInfo().getId_number());
                }
                if (null != photoDiscernContent.getIdCardInfoBack() && photoDiscernContent.getIdCardInfoBack().getValidity() != null) {
                    String validity = photoDiscernContent.getIdCardInfoBack().getValidity();
                    if (!TextUtils.isEmpty(validity)) {
                        String substring = validity.substring(validity.indexOf("-") + 1, validity.length());
                        tvIdDate.setText(substring.replace(".", "-"));
                    }
                }
                if (null != photoDiscernContent.getBankCardInfo() && photoDiscernContent.getBankCardInfo().getIssuer() != null) {//银行卡识别出来直接显示
//                edtCardNumber.setText(photoDiscernContent.getBankCardInfo().getCard_number().replaceAll(" ", ""));
                    tvSelectBankName.setText(photoDiscernContent.getBankCardInfo().getIssuer());
                }
            }
        }

        merApplyInfoReq.getMerApplyInfo().setApplyId(photoDiscernContent.getApplyId());
        merApplyInfoReq.getMerOpenInfo().setApplyId(photoDiscernContent.getApplyId());
        merApplyInfoReq.getMerOpenInfo().setMerchantId(merchantId);
    }

    private void reqBmcpCardBankInfo(String cardNo) {
        BankCardVerificationReq bankCardVerificationReq = new BankCardVerificationReq();
        bankCardVerificationReq.setCardNo(cardNo);
        NetAPI.bankCardVerification(QCodeSettleInfoActivity.this, QCodeSettleInfoActivity.this, bankCardVerificationReq);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_scan_card_number://扫码卡号
                BankManager.getInstance().showLogo(false);
                BankManager.getInstance().recognize(this, this);
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
            case R.id.btn_next:
                if (getTextContext()) {
                    next();
                }
                break;
            case R.id.btn_id_perpetual://永久
                tvIdDate.setText("9999-12-31");
                break;
        }
    }

    private void next() {
        merApplyInfoReq.getMerApplyInfo().setApplyChannel("03");
        merApplyInfoReq.getMerApplyInfo().setStatus("1");
        merApplyInfoReq.getMerApplyInfo().setApplyType("1");

        merApplyInfoReq.getMerOpenInfo().setIdCard(IDNumber);
        merApplyInfoReq.getMerOpenInfo().setIdCardExpire(IDDate);
        merApplyInfoReq.getMerOpenInfo().setAccountKind(accountKind);//对公对私
        merApplyInfoReq.getMerOpenInfo().setAccountName(name);
        merApplyInfoReq.getMerOpenInfo().setAccountNo(cardNumber);
        merApplyInfoReq.getMerOpenInfo().setOpenningBankName(bankName);

        if ("57".equals(accountKind)) {//对公，传法人姓名
            merApplyInfoReq.getMerOpenInfo().setIdName(mLeagePerNameEdt.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(bankName)) {
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
    private boolean getTextContext() {
        name = edtName.getText().toString().trim();//开户姓名
        cardNumber = edtCardNumber.getText().toString().trim();//卡号
        IDNumber = edtIdNumber.getText().toString().trim();//身份证号码
        // phone = edtPhone.getText().toString().trim();//银行预留手机号码
        IDDate = tvIdDate.getText().toString().trim();//身份证有效期
        bankName = tvSelectBankName.getText().toString().trim();//开户行


        if ("57".equals(accountKind)) {//对公
            if (TextUtils.isEmpty(name)) {
                Utils.showToast(this, "请输入开户姓名");
            } else if (name.length() < 2) {
                Utils.showToast(this, "账户名不能少于2个字符");
            } else if(checkStrLength(name,100)){
                Utils.showToast(this, "账户名称长度不能超过100字符");
            }else if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 5||cardNumber.length()>32) {
                Utils.showToast(this, "请输入正确的银行卡号");
            } else if (TextUtils.isEmpty(IDNumber)) {
                Utils.showToast(this, "请输入身份证号码");
            } else if (IDNumber.length() < 14||IDNumber.length()>18) {
                Utils.showToast(this, "请输入正确的身份证号码");
            } else if (TextUtils.isEmpty(IDDate)) {
                Utils.showToast(this, "请选择身份证有效期");
            } else if (TextUtils.isEmpty(bankName)) {
                Utils.showToast(this, "请选择开户行");
            } else if (!(DateUtil.isValidDate(IDDate) || "9999-12-31".equals(IDDate))) {
                Utils.showToast(this, "身份证有效期格式不对");
            } else if (isOverdue(IDDate)) {
                Utils.showToast(this, "身份证已过期");
            } else if (isOverdue(mLeagePerNameEdt.getText().toString().trim())) {
                Utils.showToast(this, "请输入法人姓名");
            } else if (mLeagePerNameEdt.getText().toString().trim().length() < 2) {
                Utils.showToast(this, "法人姓名不能少于2个字符");
            } else if(checkStrLength(mLeagePerNameEdt.getText().toString().trim(),26)){
                Utils.showToast(this, "法人姓名长度不能超过50字符");
            }else{
                return true;
            }
        } else {//对私
            if (TextUtils.isEmpty(name)) {
                Utils.showToast(this, "请输入开户姓名");
            } else if (name.length() < 2) {
                Utils.showToast(this, "开户姓名不能少于2个字符");
            } else if(checkStrLength(name,26)){
                Utils.showToast(this, "入账户名长度不能超过50字符");
            }else if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 14||cardNumber.length()>32) {
                Utils.showToast(this, "请输入正确的银行卡号");
            } else if (TextUtils.isEmpty(IDNumber)) {
                Utils.showToast(this, "请输入身份证号码");
            } else if (IDNumber.length() < 14||IDNumber.length()>18) {
                Utils.showToast(this, "请输入正确的身份证号码");
            } else if (TextUtils.isEmpty(IDDate)) {
                Utils.showToast(this, "请选择身份证有效期");
            } else if (TextUtils.isEmpty(bankName)) {
                Utils.showToast(this, "请选择开户行");
            } else if (!(DateUtil.isValidDate(IDDate) || "9999-12-31".equals(IDDate))) {
                Utils.showToast(this, "身份证有效期格式不对");
            } else if (isOverdue(IDDate)) {
                Utils.showToast(this, "身份证已过期");
            } else {
                return true;
            }
        }
        return false;
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

        try {
            if (time.length() == 10) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(time);
                long timeStemp = date.getTime();
                long currentTimeMillis = System.currentTimeMillis();
                if (timeStemp < currentTimeMillis) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Utils.showToast(QCodeSettleInfoActivity.this, "时间格式不对");
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
            if (!TextUtils.isEmpty(edtCardNumber.getText())) {
                reqBmcpCardBankInfo(edtCardNumber.getText().toString());
            }
            MerAttachFile merAttachFile = new MerAttachFile();
            PhotoDiscernReq photoDiscernReq = new PhotoDiscernReq();
            photoDiscernReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            photoDiscernReq.setMerApplyInfo(new MerApplyInfo());
            photoDiscernReq.setMerAttachFileList(new ArrayList<MerAttachFile>());
            photoDiscernReq.getMerApplyInfo().setApplyChannel("03");
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


    private void showAlertDialog(String tip) {
        DialogUtil.createAlertDialog(
                this,
                "注意",
                tip + "",
                "取消",
                "继续",
                mListener
        ).show();
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    if ("57".equals(accountKind)) {//对公
                        name = mLeagePerNameEdt.getText().toString().trim();
                    }
                    startActivity(new Intent(QCodeSettleInfoActivity.this, QCodeFaceRecognitionActivity.class)
                            .putExtra(APPLY_ID, merApplyInfoRes.getContent().getApplyId())
                            .putExtra(ID, merApplyInfoRes.getContent().getId())
                            .putExtra(EnterPieceActivity.ACCOUNT_KIND, accountKind)
                            .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                            .putExtra("name", name)
                            .putExtra("IDNumber", IDNumber));
                    dialog.dismiss();
//                    finish();
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

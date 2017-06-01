package com.lakala.elive.preenterpiece;

import android.content.Intent;
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
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.beans.BankCardVerificationReq;
import com.lakala.elive.beans.BankCardVerificationResp;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.utils.DateUtil;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceSubmitInfoRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceSubmitInfoResponse;

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
 * 预进件结算商户界面
 */

public class PreEnterSettleAccountsActivity extends BaseActivity implements OptionsPickerView.OnOptionsSelectListener, DataCallBack {

    private TextView tvScanCardNumber, tvSelectBankName, tvIdDate,btnIdPerpetual;
    private EditText edtCardNumber, edtName, edtIdNumber;
    private Button btnNext;
    private ImageView imgProgress;

    private OptionsPickerView pickerView;
    private TimePickerView pvTime;
    private ArrayList<String> bankList;
    private List<MerDictionaryResp.ContentBean.ItemsBean> items;

    public static final String APPLY_ID = "applyId"; //进件id
    private InputMethodManager imm;//键盘服务

    private String applyId;
    private String idNumber;
    private String vailidityData;
    private String idName;
    private String openningBank;
    private String cardNumber, bankName;

    private PreEnPieceDetailRequ preEnPieceDetailRequ;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_presettleaccounts_input);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        //身份证永久按钮
        btnIdPerpetual = findView(R.id.btn_id_perpetual);

        //名字
        edtName = findView(R.id.edt_settle_name);
        //身份证号码
        edtIdNumber = findView(R.id.edt_id_number);
        //手机号码
        //edtPhone = findView(R.id.edt_phone);
        //身份证有效期
        tvIdDate = findView(R.id.tv_id_date);
        //扫码卡号
        tvScanCardNumber = findView(R.id.tv_scan_card_number);
        //填写卡号
        edtCardNumber = findView(R.id.edt_card_number);
        //开户银行
        tvSelectBankName = findView(R.id.tv_select_bank_name);
        //下一步
        btnNext = findView(R.id.btn_next);
        imgProgress = findView(R.id.img_progress);

        timePickerView();
        pickerView = new OptionsPickerView(this);//选择器
        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("结算信息");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        EliveApplication.setImageProgress(imgProgress, 3);
    }

    @Override
    protected void bindEvent() {

        btnIdPerpetual.setOnClickListener(this);
        iBtnBack.setOnClickListener(this);
        tvScanCardNumber.setOnClickListener(this);
        tvSelectBankName.setOnClickListener(this);
        pickerView.setOnoptionsSelectListener(this);
        tvIdDate.setOnClickListener(this);
        btnNext.setOnClickListener(this);
//        edtCardNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                BankCardVerificationReq bankCardVerificationReq = new BankCardVerificationReq();
//                if (start == 15 || start == 16 || start == 18 || start == 19) {
//                    showProgressDialog("验证中");
//                    bankCardVerificationReq.setCardNo(s + "");
//                    NetAPI.bankCardVerification(PreEnterSettleAccountsActivity.this, PreEnterSettleAccountsActivity.this, bankCardVerificationReq);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
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


    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        if (intent != null) {
            idNumber = intent.getStringExtra("IDNUMBER");
            vailidityData = intent.getStringExtra("YAILIDITYDATAData");
            applyId = intent.getStringExtra("APPLYID");
            idName = intent.getStringExtra("IDNAME");
        }

        if (!TextUtils.isEmpty(applyId)) {//如果是编辑进来的，则调用详情接口取数据
            showProgressDialog("加载中...");
            preEnPieceDetailRequ = new PreEnPieceDetailRequ();
            preEnPieceDetailRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            preEnPieceDetailRequ.setApplyId(applyId);
            NetAPI.preEnterPieDetailRequest(this, this, preEnPieceDetailRequ);
        }
        edtName.setFilters(new InputFilter[]{getChinaInputFilter(26,true)});
        edtName.setText(idName);
        edtIdNumber.setText(idNumber);
        if (vailidityData != null) {
            if (!TextUtils.isEmpty(vailidityData) && vailidityData.length() > 12) {//设置截止日期
                String str = vailidityData.substring(vailidityData.length() - 10, vailidityData.length());
                tvIdDate.setText(str.replace(".", "-"));
            } else {
                tvIdDate.setText(vailidityData.replace(".", "-"));
            }
        }

        preEnPieceSubmitInfoRequ = new PreEnPieceSubmitInfoRequ();
        preEnPieceSubmitInfoRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        PreEnPieceSubmitInfoRequ.PartnerApplyInfo partnerApplyInfo = new PreEnPieceSubmitInfoRequ.PartnerApplyInfo();
        partnerApplyInfo.setProcess("3");
        partnerApplyInfo.setApplyType("1");
        partnerApplyInfo.setApplyChannel("01");
        partnerApplyInfo.setApplyId(applyId);
        preEnPieceSubmitInfoRequ.setMerApplyInfo(partnerApplyInfo);
        merOpenInfo = new PreEnPieceSubmitInfoRequ.MerOpenInfo();
        showProgressDialog("加载中...");//银行字典查询
        NetAPI.merDictionaryReq(this, this, new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "BANK"), NetAPI.BANK);
        bankList = new ArrayList<>();
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


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.BANK://获取银行信息数据
                MerDictionaryResp.ContentBean content = ((MerDictionaryResp) obj).getContent();
                items = content.getItems();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : items) {
                    bankList.add(itemsBean.getValue());
                }
                break;
            case NetAPI.ACTION_BANK_VERIFICATION://获取银行卡信息
                BankCardVerificationResp bankCardVerificationResp = (BankCardVerificationResp) obj;
                tvSelectBankName.setText(bankCardVerificationResp.getContent().getBankName());
                openningBank = bankCardVerificationResp.getContent().getOpenningBank();
                merOpenInfo.setOpenningBank(openningBank);
                if (isNext) {
                    submitRequest();
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_003://提交商户信息成功
                PreEnPieceSubmitInfoResponse preEnPieceSubmitInfoResponse = (PreEnPieceSubmitInfoResponse) obj;
                applyId = preEnPieceSubmitInfoResponse.getContent().getApplyId();
                Intent intent = new Intent(this, PreFaceRecognitionActivity.class);//跳转到人像识别界面
                intent.putExtra("APPLYID", applyId + "");
                intent.putExtra("IDNAME", idName + "");
                intent.putExtra("IDNUMBER", idNumber + "");
                startActivity(intent);
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_002: //如果是编辑转态进来的，设置值
                PreEnPieceDetailResponse preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null && preEnPieceDetailResponse.getContent() != null && preEnPieceDetailResponse.getContent().getMerAttachFileList() != null) {//不为空，设置数据
                    setContext(preEnPieceDetailResponse.getContent().getMerOpenInfo());
                }
                break;
        }
    }

    public void setContext(PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo) {//设置编辑状态下的数据
        if (merOpenInfo != null) {
            edtName.setText(getNoEmptyString(merOpenInfo.getIdName()));
            edtCardNumber.setText(getNoEmptyString(merOpenInfo.getAccountNo()));
            edtIdNumber.setText(getNoEmptyString(merOpenInfo.getIdCard()));
            if (merOpenInfo.getIdCardExpire() != null) {
                tvIdDate.setText(getNoEmptyString(getTime(Long.parseLong(merOpenInfo.getIdCardExpire() + ""))));
            }
            tvSelectBankName.setText(getNoEmptyString(merOpenInfo.getOpenningBankName()));
            if (merOpenInfo.getOpenningBank() != null) {
                openningBank = merOpenInfo.getOpenningBank();
            }
        }
    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_scan_card_number://扫码卡号
                BankManager.getInstance().showLogo(true);
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
                idName = edtName.getText().toString().trim();//开户姓名
                cardNumber = edtCardNumber.getText().toString().trim();//卡号
                idNumber = edtIdNumber.getText().toString().trim();//身份证号码
                vailidityData = tvIdDate.getText().toString().trim();//身份证有效期
                bankName = tvSelectBankName.getText().toString().trim();//开户行
//                if (getTextContext()) {
                next();
//                }
                break;
            case R.id.btn_id_perpetual://永久
                tvIdDate.setText("9999-12-31");
                break;
        }
    }

    private PreEnPieceSubmitInfoRequ preEnPieceSubmitInfoRequ;
    private PreEnPieceSubmitInfoRequ.MerOpenInfo merOpenInfo;

    private void next() {
        merOpenInfo.setContact(idName);//姓名
        merOpenInfo.setApplyId(applyId);
        merOpenInfo.setIdName(idName);
        merOpenInfo.setIdCard(idNumber);
        merOpenInfo.setAccountNo(cardNumber);
        merOpenInfo.setIdCardExpire(vailidityData);
        merOpenInfo.setOpenningBankName(bankName);//银行名称

        if (!TextUtils.isEmpty(bankName)) {
            for (int x = 0; x < items.size(); x++) {//根据银行名字查找items里面对应的银行id
                if (bankName.equals(items.get(x).getValue()) || bankName.contains(items.get(x).getValue())) {
                    openningBank = items.get(x).getId();
                    merOpenInfo.setOpenningBank(openningBank);
                    break;
                }
            }
            showProgressDialog("提交中...");
            if (!TextUtils.isEmpty(merOpenInfo.getOpenningBank())) {//如果直接从本地已获取的数据取到值了，直接调用提交商户信息接口，否则调用查询接口
                submitRequest();
            } else {//如果没有找到匹配的银行,调用查询接口，查到后再提交接口
                reqBmcpCardBankInfo(cardNumber);
                isNext = true;
                //如果没有找到，调用接口
//                for (int x = 0; x < items.size(); x++) {//根据银行名字查找items里面对应的银行id
//                    if ("其他非常见银行".equals(items.get(x).getValue()) || "其他非常见银行".contains(items.get(x).getValue())) {
//                        merOpenInfo.setOpenningBank(items.get(x).getId());
//                    }
//                }
            }
        } else {//如果银行名称为空，直接跳转到下一页
            Intent intent = new Intent(this, PreFaceRecognitionActivity.class);//跳转到人像识别界面
            intent.putExtra("APPLYID", applyId + "");
            intent.putExtra("IDNAME", idName + "");
            intent.putExtra("IDNUMBER", idNumber + "");
            startActivity(intent);
        }
    }

    private boolean isNext = false;

    private boolean getTextContext() {
//        idName = edtName.getText().toString().trim();//开户姓名
//        cardNumber = edtCardNumber.getText().toString().trim();//卡号
//        idNumber = edtIdNumber.getText().toString().trim();//身份证号码
//        vailidityData = tvIdDate.getText().toString().trim();//身份证有效期
//        bankName = tvSelectBankName.getText().toString().trim();//开户行

        if (TextUtils.isEmpty(idName)) {
            Utils.showToast(this, "请输入开户姓名");
        } else if (idName.length() < 2) {
            Utils.showToast(this, "开户姓名不能少于2个字符");
        } else if(checkStrLength(idName,26)){
            Utils.showToast(this, "入账户名长度不能超过50字符");
        }else if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 5||cardNumber.length()>32) {
            Utils.showToast(this, "请输入正确的银行卡号");
        } else if (TextUtils.isEmpty(idNumber)) {
            Utils.showToast(this, "请输入身份证号码");
        } else if (idNumber.length() <= 17) {
            Utils.showToast(this, "请输入正确的身份证号码");
        } else if (TextUtils.isEmpty(vailidityData)) {
            Utils.showToast(this, "请选择身份证有效期");
        } else if (TextUtils.isEmpty(bankName)) {
            Utils.showToast(this, "请选择开户行");
        } else if (!(DateUtil.isValidDate(vailidityData) || "9999-12-31".equals(vailidityData))) {
            Utils.showToast(this, "身份证有效期格式不对");
        }if (isOverdue(vailidityData)) {
            Utils.showToast(this, "身份证已过期");
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

    private String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        return format.format(d1);
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
            Utils.showToast(PreEnterSettleAccountsActivity.this, "时间格式不对");
            return true;
        }
        return false;
    }


    private void reqBmcpCardBankInfo(String cardNo) {//根据银行卡号查询对于的编码
        BankCardVerificationReq bankCardVerificationReq = new BankCardVerificationReq();
        bankCardVerificationReq.setCardNo(cardNo);
        NetAPI.bankCardVerification(this, this, bankCardVerificationReq);
    }


    private void submitRequest() {
        preEnPieceSubmitInfoRequ.setMerOpenInfo(merOpenInfo);
        NetAPI.preEnterPieSubmitInfoRequest(this, this, preEnPieceSubmitInfoRequ);
    }


//    /**
//     * 扫码银行卡回调
//     * @param success
//     */
//    @Override
//    public void onCardDetected(boolean success) {
//        if (success) {
//            EXBankCardInfo exBankCardInfo = BankManager.getInstance().getCardInfo();
//            edtCardNumber.setText(exBankCardInfo.strNumbers);
//            //银行卡扫描成功后，设置对应的银行
//            String str = exBankCardInfo.strBankName;
//            str = str.replace("(", "").replace(")", "").replace("0", "").replace("1", "").replace("2", "")
//                    .replace("3", "").replace("4", "").replace("5", "").replace("6", "").replace("7", "").replace("8", "").replace("9", "");
//            if (str.length() > 4 && str.contains("中国")) {
//                str = str.replace("中国", "");
//            }
//            tvSelectBankName.setText(str); //获取对于的银行
////            reqBmcpCardBankInfo(exBankCardInfo.strNumbers);//调用接口获取对于银行卡的对于银行编码
//        }
//    }

    @Override
    public void onBankCardDetected(boolean b) {
        if (b) {
            EXBankCardInfo exBankCardInfo = BankManager.getInstance().getCardInfo();
            edtCardNumber.setText(exBankCardInfo.strNumbers);
            //银行卡扫描成功后，设置对应的银行
            String str = exBankCardInfo.strBankName;
            str = str.replace("(", "").replace(")", "").replace("0", "").replace("1", "").replace("2", "")
                    .replace("3", "").replace("4", "").replace("5", "").replace("6", "").replace("7", "").replace("8", "").replace("9", "");
            if (str.length() > 4 && str.contains("中国")) {
                str = str.replace("中国", "");
            }
            tvSelectBankName.setText(str); //获取对于的银行
//            reqBmcpCardBankInfo(exBankCardInfo.strNumbers);//调用接口获取对于银行卡的对于银行编码
        }
    }

    @Override
    public void onCameraDenied() {
    }
}

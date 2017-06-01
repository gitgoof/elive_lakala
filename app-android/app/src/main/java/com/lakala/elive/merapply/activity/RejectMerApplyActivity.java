package com.lakala.elive.merapply.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerOpenInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyInfoReq15;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.utils.DictionaryUtil;
import com.lakala.elive.common.utils.EditUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.VerifyUtil;

import org.greenrobot.eventbus.EventBus;

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
 * Created by wenhaogu on 2017/3/8.
 */

public class RejectMerApplyActivity extends BaseActivity implements DataCallBack {

    private TextView tvIdDate, tvProvince, tvCity, tvCounty, tvArea, tvBigClass, tvCentreClass, tvSmallClass, tvBizContent, tvScanCardNumber;
    private EditText edtCardNumber, edtName, edtIdNumber, edtSignOrderName, edtContact, edtPhone, edtMerchantsAddress, edtEmail;
    private Button btnNext;
    //=========================地址选择用到的变量============================

    private OptionsPickerView provincePickerView, cityPickerView, countyPickerView;

    private String addressLevelType = PROVINCE;

    private static final String PROVINCE = "PROVINCE";

    private static final String CITY = "CITY";

    private static final String COUNTY = "COUNTY";

    private ArrayList<String> provinceList = new ArrayList<>();

    private ArrayList<String> cityList = new ArrayList<>();

    private ArrayList<String> countyList = new ArrayList<>();

    private MerDictionaryResp.ContentBean bmcpRegionProvinceResp, bmcpRegionCityResp, bmcpRegionCountyResp;

    //==============================mcc选择用到的变量===================================

    private OptionsPickerView bigClassPickerView, centreClassPickerView, smallClassPickerView;

    private String mccLevelType = BIG_CLASS;

    private static final String BIG_CLASS = "0";

    private static final String CENTRE_CLASS = "1";

    private static final String SMALL_CLASS = "2";

    private ArrayList<String> bigClassList = new ArrayList<>();

    private ArrayList<String> centreClassList = new ArrayList<>();

    private ArrayList<String> smallClassList = new ArrayList<>();

    private MerDictionaryResp.ContentBean bmcpMccBigClassResp, bmcpMccCentreClassResp, bmcpMccSmallClassResp;

    //===============================================================================
    private OptionsPickerView shopAreaPickerView, merBizContentPickerView;
    private MerDictionaryResp.ContentBean bmcpSelectorShopAreaRes;
    private ArrayList<String> shopAreaList = new ArrayList<>();//使用面积

    private MerDictionaryResp.ContentBean bmcpSelectorMerBizContentRes;
    private ArrayList<String> merBizContentList = new ArrayList<>();//经营内容


    private String applyId;//进件id

    private long id;

    private String mccID, mccSmallID, mccBigID;//小/中/大

    private String provinceID, cityID, countyID;//省//市//县

    private String businessArea;//营业面积

    private String merAddr;//商户地址

    private String email;//邮箱

    private String Contact;//联系方式

    private String phone;//

    private String merchantName;//商户名称

    private String businessContent;//经营内容

    private String name, cardNumber, IDNumber, IDDate;


    private InputMethodManager imm;//键盘服务
    private TimePickerView pvTime;
    private boolean isDisPlay = false;//是否显示界面
    private MerDictionaryReq regionReq;
    private MerDictionaryReq mccReq;
    private MerApplyInfoReq15 merApplyInfoReq15;
    private MerApplyDetailsResp.ContentBean contentBean;
    private TextView btnIdPerpetual;

    private TextView mTvName;
    private TextView mTvCardNum;
    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_reject_merapply);
    }

    @Override
    protected void bindView() {
        // 入账户名 or 账户名称
        mTvName = findView(R.id.name);
        //名字
        edtName = findView(R.id.edt_name);
        mTvCardNum = findView(R.id.card_number);
        //身份证号码
        edtIdNumber = findView(R.id.edt_id_number);
        //身份证有效期
        tvIdDate = findView(R.id.tv_id_date);
        //身份证永久按钮
        btnIdPerpetual = findView(R.id.btn_id_perpetual);
        //扫码卡号
        tvScanCardNumber = findView(R.id.tv_scan_card_number);
        //填写卡号
        edtCardNumber = findView(R.id.edt_card_number);
        //开户银行
        // tvSelectBankName = findView(R.id.tv_select_bank_name);

        //签购单名称
        edtSignOrderName = findView(R.id.edt_sign_order_name);
        //联系人
        edtContact = findView(R.id.edt_contact);
        //手机号码
        edtPhone = findView(R.id.edt_phone);
        //邮箱地址
        edtEmail = findView(R.id.edt_email);

        //省市
        tvProvince = findView(R.id.tv_province);
        //市区
        tvCity = findView(R.id.tv_city);
        //县
        tvCounty = findView(R.id.tv_county);

        //大类别
        tvBigClass = findView(R.id.tv_big_class);
        //中类别
        tvCentreClass = findView(R.id.tv_centre_class);
        //小类别
        tvSmallClass = findView(R.id.tv_small_class);

        //商户地址 详细地址
        edtMerchantsAddress = findView(R.id.edt_merchants_address);
        //经营内容
        tvBizContent = findView(R.id.tv_biz_content);

        //面积
        tvArea = findView(R.id.tv_area);
        //下一步
        btnNext = findView(R.id.btn_next);


        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        timePickerView();
        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("驳回重新提交");
        EditUtil.setEditInputType(edtContact,26,true);
        EditUtil.setAddressInputFileter(edtMerchantsAddress,128);

        EditUtil.setEditInputType(edtSignOrderName,64,false);
        EditUtil.setEditInputType(edtName,100,false);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        tvScanCardNumber.setOnClickListener(this);
        tvIdDate.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvBizContent.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvCounty.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        tvBigClass.setOnClickListener(this);
        tvCentreClass.setOnClickListener(this);
        tvSmallClass.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnIdPerpetual.setOnClickListener(this);
    }

    @Override
    protected void bindData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDisPlay) {

            //地址请求
            regionReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "region");
            regionReq.setId("1000");
            regionReq.setLevel(addressLevelType);
//            NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);

            //mcc请求
            mccReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "mcc");
            mccReq.setLevel("0");
//            NetAPI.merDictionaryReq(this, this, mccReq, NetAPI.BMCP_MCC_REQUEST);

            //营业面积查询
//            NetAPI.merDictionaryReq(this, this,
//                    new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SHOP_AREA"), NetAPI.SHOP_AREA);

            //经营内容
//            NetAPI.merDictionaryReq(this, this,
//                    new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "MER_BIZ_CONTENT"), NetAPI.MER_BIZ_CONTENT);

            merApplyInfoReq15 = new MerApplyInfoReq15();
            merApplyInfoReq15.authToken = mSession.getUserLoginInfo().getAuthToken();
            merApplyInfoReq15.setMerOpenInfo(new MerOpenInfo());

            contentBean = (MerApplyDetailsResp.ContentBean) getIntent().getSerializableExtra("ContentBean");
            if (null != contentBean && null != contentBean.getMerOpenInfo()) {
                setTextContent(contentBean.getMerOpenInfo());
                accountKind = contentBean.getMerOpenInfo().getAccountKind();
                if(TextUtils.isEmpty(accountKind)){
                    accountKind = "58";
                }
                mTvCardNum.setText(accountKind.equals("58")?"卡号":"入款账号");
                edtCardNumber.setHint(accountKind.equals("58")?"请输入卡号":"请输入入款账号");
                if(accountKind.equals("58")?false:true){
                    mTvName.setText("账户名称");
                    edtName.setHint("请输入账户名称");
                    EditUtil.setEditInputType(edtName,100,false);
                } else {
                    mTvName.setText("入账户名");
                    edtName.setHint("请输入入账户名");
                    EditUtil.setEditInputType(edtName,26,false);
                }
            }
        }
        isDisPlay = true;
    }
    private String accountKind = "58";

    private void setTextContent(MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo) {
        edtSignOrderName.setText(getNoEmptyString(merOpenInfo.getMerchantName()));
        edtName.setText(getNoEmptyString(merOpenInfo.getAccountName()));
        edtIdNumber.setText(getNoEmptyString(merOpenInfo.getIdCard()));
        edtCardNumber.setText(getNoEmptyString(merOpenInfo.getAccountNo()));
        tvIdDate.setText( merOpenInfo.getIdCardExpire() );
        edtContact.setText(getNoEmptyString(merOpenInfo.getContact()));
        edtPhone.setText(getNoEmptyString(merOpenInfo.getMobileNo()));
        edtEmail.setText(getNoEmptyString(merOpenInfo.getEmailAddr()));
        tvBigClass.setText(getNoEmptyString(merOpenInfo.getMccBigTypeStr()));
        tvCentreClass.setText(getNoEmptyString(merOpenInfo.getMccSmallTypeStr()));
        tvSmallClass.setText(getNoEmptyString(merOpenInfo.getMccCodeStr()));
        edtMerchantsAddress.setText(getNoEmptyString(merOpenInfo.getMerAddr()));
        tvArea.setText(getNoEmptyString(merOpenInfo.getBusinessAreaStr()));
        tvProvince.setText(getNoEmptyString(merOpenInfo.getProvince()));
        tvCity.setText(getNoEmptyString(merOpenInfo.getCity()));
        tvCounty.setText(getNoEmptyString(merOpenInfo.getDistrict()));
        tvBizContent.setText(getNoEmptyString(merOpenInfo.getBussinessContentStr()));

        //回显赋值
        applyId = merOpenInfo.getApplyId();
        provinceID = merOpenInfo.getProvinceCode();
        cityID = merOpenInfo.getCityCode();
        countyID = merOpenInfo.getDistrictCode();
        mccBigID = merOpenInfo.getMccBigType();
        mccSmallID = merOpenInfo.getMccSmallType();
        mccID = merOpenInfo.getMccCode();
        businessArea = merOpenInfo.getBusinessArea();
        businessContent = merOpenInfo.getBusinessContent();

        List<String> bigStrL = DictionaryUtil.getInstance().getMccBigStrList();
        if(bigStrL != null && bigStrL.size()>0){
            bigClassList.clear();
            bigClassList.addAll(bigStrL);
        }
        List<String> provinceStr = DictionaryUtil.getInstance().getProvinceStrList();
        if(provinceStr != null && provinceStr.size()>0){
            provinceList.clear();
            provinceList.addAll(provinceStr);
        }
        List<String> list1 = DictionaryUtil.getInstance().getCityStrList(provinceID);
        if(list1 != null && list1.size()>0){
            cityList.clear();
            cityList.addAll(list1);
        }
        List<String> list2 = DictionaryUtil.getInstance().getAreaStrList(cityID);
        if(list2 != null && list2.size()>0){
            countyList.clear();
            countyList.addAll(list2);
        }

        List<String> list3 = DictionaryUtil.getInstance().getMccCenterStrList(mccBigID);
        if(list3 != null && list3.size()>0){
            centreClassList.clear();
            centreClassList.addAll(list3);
        }
        List<String> list4 = DictionaryUtil.getInstance().getMccSmallStrList(mccSmallID);
        if(list4 != null && list4.size()>0){
            smallClassList.clear();
            smallClassList.addAll(list4);
        }
        //merApplyInfoReq15.getMerOpenInfo().setOpenningBank(merOpenInfo.getOpenningBank());

//        //加载市列表
//        if (!provinceID.isEmpty()){
//            showProgressDialog("加载中...");
//            regionReq.setId(provinceID);
//            regionReq.setLevel(addressLevelType = CITY);
//            NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
//        }
//
//        //加载区列表
//        if (!cityID.isEmpty()){
//            regionReq.setId(cityID);
//            regionReq.setLevel(addressLevelType = COUNTY);
//            NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
//        }
//
//        //加载中类别列表
//        if (!TextUtils.isEmpty(mccBigID)){
//            mccReq.setParentMcc(mccBigID);
//            mccReq.setLevel(mccLevelType = CENTRE_CLASS);
//            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
//        }
//
//        //加载小类别列表
//        if (!TextUtils.isEmpty(mccSmallID)){
//            mccReq.setParentMcc(mccSmallID);
//            mccReq.setLevel(mccLevelType = SMALL_CLASS);
//            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
//        }

    }

    @Override
    public void onClick(View v) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
        }
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_id_date:
                pvTime.show();
                break;
            case R.id.tv_scan_card_number:
                BankManager.getInstance().showLogo(true);
                BankManager.getInstance().recognize(this,this);
                break;
            case R.id.tv_big_class://商户类别
                List<String> bigStrL = DictionaryUtil.getInstance().getMccBigStrList();
                if(bigStrL != null && bigStrL.size()>0){
                    bigClassList.clear();
                    bigClassList.addAll(bigStrL);
                } else {
                    bigClassList = new ArrayList<String>();
                }
                if(bigClassList == null || bigClassList.size() == 0){
                    showProgressDialog("请求中...");
                    mccReq.setLevel("0");
                    NetAPI.merDictionaryReq(this, this, mccReq, NetAPI.BMCP_MCC_REQUEST);
                    break;
                }
                selectAddress(bigClassPickerView, bigClassList, bigClassSelectListener);
                break;
            case R.id.tv_centre_class://商户类别
                if (centreClassList == null || centreClassList.size() == 0) {
                    centreClassList = new ArrayList<String>();
                    Toast.makeText(RejectMerApplyActivity.this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(centreClassPickerView, centreClassList, centreClassSelectListener);
                break;
            case R.id.tv_small_class://商户类别

                if (smallClassList == null || smallClassList.size() == 0) {
                    smallClassList = new ArrayList<String>();
                    Toast.makeText(RejectMerApplyActivity.this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(smallClassPickerView, smallClassList, smallClassSelectListener);
                break;
            case R.id.tv_province:// 省市
                List<String> provinceStr = DictionaryUtil.getInstance().getProvinceStrList();
                if(provinceStr != null && provinceStr.size()>0){
                    provinceList.clear();
                    provinceList.addAll(provinceStr);
                } else {
                    provinceList = new ArrayList<String>();
                }
                if(provinceList == null || provinceList.size() == 0){
                    regionReq.setId("1000");
                    regionReq.setLevel(PROVINCE);
                    showProgressDialog("请求中...");
                    NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
                    break;
                }
                selectAddress(provincePickerView, provinceList, provinceSelectListener);
                break;
            case R.id.tv_city:// 市
                if(TextUtils.isEmpty(tvProvince.getText().toString())){
                    Toast.makeText(RejectMerApplyActivity.this, "请先选择省！", Toast.LENGTH_SHORT).show();
                    cityList.clear();
                    break;
                }
                if (cityList == null || cityList.size() == 0) {
                    cityList = new ArrayList<String>();
                    Toast.makeText(RejectMerApplyActivity.this, "请选择省！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(cityPickerView, cityList, citySelectListener);
                break;
            case R.id.tv_county:// 县
                if(TextUtils.isEmpty(tvProvince.getText().toString())){
                    Toast.makeText(RejectMerApplyActivity.this, "请先选择省！", Toast.LENGTH_SHORT).show();
                    cityList.clear();
                    break;
                }
                if(TextUtils.isEmpty(tvCity.getText().toString())){
                    Toast.makeText(RejectMerApplyActivity.this, "请先选择市！", Toast.LENGTH_SHORT).show();
                    countyList.clear();
                    break;
                }
                if (countyList == null || countyList.size() == 0) {
                    countyList = new ArrayList<String>();
                    Toast.makeText(RejectMerApplyActivity.this, "请重新选择省！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(countyPickerView, countyList, countySelectListener);
                break;
            case R.id.tv_area://营业面积
                shopAreaList = DictionaryUtil.getInstance().getOtherDictList("SHOP_AREA");
                if(shopAreaList == null || shopAreaList.size() == 0){
                    shopAreaList = new ArrayList<String>();
                    showProgressDialog("请求中...");
                    NetAPI.merDictionaryReq(this, this,
                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SHOP_AREA"), NetAPI.SHOP_AREA);
                    break;
                }
                selectAddress(shopAreaPickerView, shopAreaList, shopAreaSelectListener);
                break;
            case R.id.tv_biz_content://经营内容
                merBizContentList = DictionaryUtil.getInstance().getOtherDictList("MER_BIZ_CONTENT");
                if (merBizContentList == null || merBizContentList.size() == 0) {
                    merBizContentList = new ArrayList<String>();
                    showProgressDialog("请求中...");
                    NetAPI.merDictionaryReq(this, this,
                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "MER_BIZ_CONTENT"), NetAPI.MER_BIZ_CONTENT);
                    break;
                }
                selectAddress(merBizContentPickerView, merBizContentList, merBizContentSelectListener);
                break;
            case R.id.btn_next:
                if (getTextContext()) {
                    next();
                }
                break;
            case R.id.btn_id_perpetual:
                tvIdDate.setText("9999-12-31");
                break;

        }
    }

    private boolean getTextContext() {
        name = edtName.getText().toString().trim();//开户姓名
        cardNumber = edtCardNumber.getText().toString().trim();//卡号
        IDNumber = edtIdNumber.getText().toString().trim();//身份证号码
        IDDate = tvIdDate.getText().toString().trim();//身份证有效期
        merchantName = edtSignOrderName.getText().toString().trim();
        merAddr = edtMerchantsAddress.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        Contact = edtContact.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();


        if(accountKind.equals("58")?false:true){
            if(TextUtils.isEmpty(name)){
                Utils.showToast(this, "请输入开户姓名");
                return false;
            }
            if(name.length() < 2){
                Utils.showToast(this, "入账户名不能少于2位");
                return false;
            }
            if(checkStrLength(name,100)){
                Utils.showToast(this, "入账户名长度不能超过100字符");
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

        if (TextUtils.isEmpty(name)) {
            Utils.showToast(this, "请输入开户姓名");
        } else if (TextUtils.isEmpty(cardNumber) || cardNumber.length() <= 14) {
            Utils.showToast(this, "请输入正确的银行卡号");
        } else if (TextUtils.isEmpty(IDNumber)) {
            Utils.showToast(this, "请输入身份证号码");
        } else if (IDNumber.length() <= 14) {
            Utils.showToast(this, "请输入正确的身份证号码");
        } else if (TextUtils.isEmpty(IDDate)) {
            Utils.showToast(this, "请选择身份证有效期");
        } else if (isOverdue(IDDate)) {
            Utils.showToast(this, "身份证已过期");
        } else if (TextUtils.isEmpty(merchantName)) {
            Utils.showToast(this, "请输入签购单名称");
        } else if (merchantName.length() <= 4) {
            Utils.showToast(this, "请输入5-64位签购单名称");
        } else if (TextUtils.isEmpty(Contact)) {
            Utils.showToast(this, "请输入联系人");
        } else if (EditUtil.getTextCharLength(Contact)<4) {
            Utils.showToast(this, "联系人应该是4-26个字符");
        } else if (TextUtils.isEmpty(phone)) {
            Utils.showToast(this, "请输入手机号");
        } /*else if (!VerifyUtil.isMobilehone(phone)) {
            Utils.showToast(this, "请输入正确的手机号");
        } */else if (TextUtils.isEmpty(email)) {
            Utils.showToast(this, "请输入邮箱");
        } else if (email.trim().length()>64) {
            Utils.showToast(this, "邮箱长度不能大于64");
        } else if (TextUtils.isEmpty(tvBigClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户大类别");
        } else if (TextUtils.isEmpty(tvCentreClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户中类别");
        } else if (TextUtils.isEmpty(tvSmallClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户小类别");
        } else if (TextUtils.isEmpty(tvProvince.getText().toString().trim())) {
            Utils.showToast(this, "请选择省市");
        } else if (TextUtils.isEmpty(tvCity.getText().toString().trim())) {
            Utils.showToast(this, "请选择城市");
        } else if (TextUtils.isEmpty(tvCounty.getText().toString().trim())) {
            Utils.showToast(this, "请选择县区");
        } else if (TextUtils.isEmpty(merAddr)) {
            Utils.showToast(this, "请输入地址");
        } else if (!VerifyUtil.isAddress(merAddr)) {
            Utils.showToast(this, "请输入正确地址");
        } else if (!VerifyUtil.isEmail(email)) {
            Utils.showToast(this, "请输入正确的邮箱");
        } else if (TextUtils.isEmpty(tvArea.getText().toString().trim())) {
            Utils.showToast(this, "请选择营业面积");
        } else if (TextUtils.isEmpty(tvBizContent.getText().toString().trim())) {
            Utils.showToast(this, "请选择经营内容");
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

    private void next() {
        merApplyInfoReq15.getMerOpenInfo().setIdCard(IDNumber);
        merApplyInfoReq15.getMerOpenInfo().setIdCardExpireStr(IDDate);
        merApplyInfoReq15.getMerOpenInfo().setAccountName(name);
        merApplyInfoReq15.getMerOpenInfo().setAccountNo(cardNumber);
        merApplyInfoReq15.getMerOpenInfo().setApplyId(applyId);
        merApplyInfoReq15.getMerOpenInfo().setMerchantName(merchantName);
        merApplyInfoReq15.getMerOpenInfo().setMerAddr(merAddr);
        merApplyInfoReq15.getMerOpenInfo().setProvinceCode(provinceID);
        merApplyInfoReq15.getMerOpenInfo().setCityCode(cityID);
        merApplyInfoReq15.getMerOpenInfo().setDistrictCode(countyID);
        merApplyInfoReq15.getMerOpenInfo().setMccCode(mccID);
        merApplyInfoReq15.getMerOpenInfo().setBusinessArea(businessArea);
        merApplyInfoReq15.getMerOpenInfo().setBusinessContent(businessContent);
        merApplyInfoReq15.getMerOpenInfo().setEmailAddr(email);
        merApplyInfoReq15.getMerOpenInfo().setContact(Contact);
        merApplyInfoReq15.getMerOpenInfo().setMobileNo(phone);
        merApplyInfoReq15.getMerOpenInfo().setMccBigType(mccBigID);
        merApplyInfoReq15.getMerOpenInfo().setMccSmallType(mccSmallID);

        if(!checkParams()){
            return;
        }
        showProgressDialog("提交中...");
        NetAPI.merApply15(this, this, merApplyInfoReq15);
    }

    private boolean checkParams(){
        // TODO 对参数进行判断
        /*
        if(TextUtils.isEmpty(provinceID)){
            Utils.showToast(RejectMerApplyActivity.this, "省地址ID为空！请重新选择。");
            return false;
        }
        if(TextUtils.isEmpty(cityID)){
            Utils.showToast(RejectMerApplyActivity.this, "市地址ID为空！请重新选择。");
            return false;
        }
        if(TextUtils.isEmpty(countyID)){
            Utils.showToast(RejectMerApplyActivity.this, "县地址ID为空！请重新选择。");
            return false;
        }
        */
        if(TextUtils.isEmpty(email)){
            Utils.showToast(RejectMerApplyActivity.this, "请输入邮箱地址");
            return false;
        }
        if(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
            Utils.showToast(RejectMerApplyActivity.this, "请输入正确邮箱地址");
            return false;
        }
        return true;
    }

    /**
     * 选择
     */

    private void selectAddress(OptionsPickerView pickerView, ArrayList<String> list, OptionsPickerView.OnOptionsSelectListener listener) {
        pickerView = new OptionsPickerView(this);
        pickerView.setPicker(list);
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);
        pickerView.setOnoptionsSelectListener(listener);
        pickerView.show();
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.BMCP_REGION_REQUEST://地址选择器数据
                if (addressLevelType.equals(PROVINCE)) {//省
                    bmcpRegionProvinceResp = ((MerDictionaryResp) obj).getContent();
                    provinceList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionProvinceResp.getItems()) {
                        provinceList.add(regionsBean.getValue());
                    }
                    if(provinceList != null && provinceList.size() !=0)
                        selectAddress(provincePickerView, provinceList, provinceSelectListener);
                } else if (addressLevelType.equals(CITY)) {//市
                    bmcpRegionCityResp = ((MerDictionaryResp) obj).getContent();
                    cityList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCityResp.getItems()) {
                        cityList.add(regionsBean.getValue());
                    }
                    EventBus.getDefault().post("city");
                } else {//区
                    bmcpRegionCountyResp = ((MerDictionaryResp) obj).getContent();
                    countyList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCountyResp.getItems()) {
                        countyList.add(regionsBean.getValue());
                    }
                    EventBus.getDefault().post("country");
                }
                break;

            case NetAPI.BMCP_MCC_REQUEST://mcc选择器数据
                if (mccLevelType.equals(BIG_CLASS)) {
                    bmcpMccBigClassResp = ((MerDictionaryResp) obj).getContent();
                    bigClassList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean mccsBean : bmcpMccBigClassResp.getItems()) {
                        bigClassList.add(mccsBean.getValue());
                    }


                } else if (mccLevelType.equals(CENTRE_CLASS)) {
                    bmcpMccCentreClassResp = ((MerDictionaryResp) obj).getContent();
                    centreClassList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean mccsBean : bmcpMccCentreClassResp.getItems()) {
                        centreClassList.add(mccsBean.getValue());
                    }

                } else {
                    bmcpMccSmallClassResp = ((MerDictionaryResp) obj).getContent();
                    smallClassList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean mccsBean : bmcpMccSmallClassResp.getItems()) {
                        smallClassList.add(mccsBean.getValue());
                    }
                }
                break;
            case NetAPI.SHOP_AREA://营业面积
                bmcpSelectorShopAreaRes = ((MerDictionaryResp) obj).getContent();
                shopAreaList.clear();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorShopAreaRes.getItems()) {
                    shopAreaList.add(itemsBean.getValue());
                }
                if(shopAreaList != null && shopAreaList.size()!=0)
                    selectAddress(shopAreaPickerView, shopAreaList, shopAreaSelectListener);
                break;
            case NetAPI.MER_BIZ_CONTENT://经营内容
                bmcpSelectorMerBizContentRes = ((MerDictionaryResp) obj).getContent();
                merBizContentList.clear();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorMerBizContentRes.getItems()) {
                    merBizContentList.add(itemsBean.getValue());
                }
                if(merBizContentList != null && merBizContentList.size() !=0)
                    selectAddress(merBizContentPickerView, merBizContentList, merBizContentSelectListener);
                break;
            case NetAPI.ACTION_MER_APPLY_015:
                Utils.showToast(this, "提交成功");
                startActivity(new Intent(this, MyMerchantsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.BMCP_REGION_REQUEST || method == NetAPI.BMCP_MCC_REQUEST
                || method == NetAPI.SHOP_AREA || method == NetAPI.MER_BIZ_CONTENT || method == NetAPI.SETTLE_PERIOD) {
            Utils.showToast(this, "获取数据失败");
        } else if (method == NetAPI.ACTION_MER_APPLY_015) {
            Utils.showToast(this, statusCode);
        }
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
            Utils.showToast(RejectMerApplyActivity.this, "时间格式不对");
            return true;
        }

        return false;
    }

    OptionsPickerView.OnOptionsSelectListener provinceSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (provinceList.size() < 1) {
                return;
            }
            tvProvince.setText(provinceList.get(options1));
            tvCity.setText("");
            tvCounty.setText("");
            edtMerchantsAddress.setText("");

            List<String> list = DictionaryUtil.getInstance().getProvinceStrList();
            if(list != null && list.size()>0){
                provinceID = DictionaryUtil.getInstance().getProvinceCode(options1);
            } else {
                List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionProvinceResp.getItems();
                provinceID = regions.get(options1).getId() + "";
            }
            regionReq.setId(provinceID + "");
            regionReq.setLevel(addressLevelType = CITY);

            List<String> list1 = DictionaryUtil.getInstance().getCityStrList(provinceID);
            if(list1 != null && list1.size()>0){
                cityList.clear();
                cityList.addAll(list1);
                return;
            }

            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);

        }
    };

    OptionsPickerView.OnOptionsSelectListener citySelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (cityList.size() < 1) {
                return;
            }
            tvCity.setText(cityList.get(options1));
            tvCounty.setText("");
            edtMerchantsAddress.setText("");

            List<String> list = DictionaryUtil.getInstance().getCityStrList(provinceID);
            if(list != null && list.size()>0){
                cityID = DictionaryUtil.getInstance().getCityCode(options1);
            } else {
                List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCityResp.getItems();
                cityID = regions.get(options1).getId() + "";
            }

            regionReq.setId(cityID + "");
            regionReq.setLevel(addressLevelType = COUNTY);

            List<String> list1 = DictionaryUtil.getInstance().getAreaStrList(cityID);
            if(list1 != null && list1.size()>0){
                countyList.clear();
                countyList.addAll(list1);
                return;
            }

            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
        }
    };

    OptionsPickerView.OnOptionsSelectListener countySelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (countyList.size() < 1) {
                return;
            }

            edtMerchantsAddress.setText("");
            tvCounty.setText(countyList.get(options1));

            List<String> list = DictionaryUtil.getInstance().getAreaStrList(cityID);
            if(list != null && list.size()>0){
                countyID = DictionaryUtil.getInstance().getAreaCode(options1);
            } else {
                List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCountyResp.getItems();
                countyID = regions.get(options1).getId() + "";
            }

        }
    };
    //---------------------------------------
    OptionsPickerView.OnOptionsSelectListener bigClassSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (bigClassList.size() < 1) {
                return;
            }

            tvBigClass.setText(bigClassList.get(options1));
            List<String> list = DictionaryUtil.getInstance().getMccBigStrList();
            if(list != null && list.size() > 0){
                mccBigID = DictionaryUtil.getInstance().getMccBigCode(options1);
            } else {
                mccBigID = bmcpMccBigClassResp.getItems().get(options1).getId();
            }
            tvCentreClass.setText("");
            tvSmallClass.setText("");

//            List<MerDictionaryResp.ContentBean.ItemsBean> mccs = bmcpMccBigClassResp.getItems();
            mccReq.setParentMcc(mccBigID);
            mccReq.setLevel(mccLevelType = CENTRE_CLASS);

            List<String> list1 = DictionaryUtil.getInstance().getMccCenterStrList(mccBigID);
            if(list1 != null && list1.size()>0){
                centreClassList.clear();
                centreClassList.addAll(list1);
                return;
            }

            showProgressDialog("获取信息中...");
            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
        }
    };

    OptionsPickerView.OnOptionsSelectListener centreClassSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (centreClassList.size() < 1) {
                return;
            }

            tvCentreClass.setText(centreClassList.get(options1));
            tvSmallClass.setText("");

            List<String> list = DictionaryUtil.getInstance().getMccCenterStrList(mccBigID);
            if(list != null && list.size() > 0){
                mccSmallID = DictionaryUtil.getInstance().getMccCenterCode(options1);
            } else {
                mccSmallID = bmcpMccCentreClassResp.getItems().get(options1).getId();
            }

//            List<MerDictionaryResp.ContentBean.ItemsBean> mccs = bmcpMccCentreClassResp.getItems();
            mccReq.setParentMcc(mccSmallID);
            mccReq.setLevel(mccLevelType = SMALL_CLASS);

            List<String> list1 = DictionaryUtil.getInstance().getMccSmallStrList(mccSmallID);
            if(list1 != null && list1.size()>0){
                smallClassList.clear();
                smallClassList.addAll(list1);
                return;
            }

            showProgressDialog("获取信息中...");
            NetAPI.merDictionaryReq(RejectMerApplyActivity.this, RejectMerApplyActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
        }
    };

    OptionsPickerView.OnOptionsSelectListener smallClassSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (smallClassList.size() < 1) {
                return;
            }

            tvSmallClass.setText(smallClassList.get(options1));

            List<String> list = DictionaryUtil.getInstance().getMccSmallStrList(mccSmallID);
            if(list != null && list.size() > 0){
                mccID = DictionaryUtil.getInstance().getMccSmallCode(options1);
            } else {
                mccID = bmcpMccSmallClassResp.getItems().get(options1).getId();
            }
        }
    };

    //营业面积
    OptionsPickerView.OnOptionsSelectListener shopAreaSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (shopAreaList.size() < 1) {
                return;
            }

            String string = shopAreaList.get(options1);
            tvArea.setText(string);
            List<String> list = DictionaryUtil.getInstance().getOtherDictList("SHOP_AREA");
            if(list != null && list.size() != 0){
                businessArea = DictionaryUtil.getInstance().getMapValueByType("SHOP_AREA").get(string);
                return;
            }
            businessArea = bmcpSelectorShopAreaRes.getItems().get(options1).getId();
        }
    };

    //经营内容
    OptionsPickerView.OnOptionsSelectListener merBizContentSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (merBizContentList.size() < 1) {
                return;
            }

            String string = merBizContentList.get(options1);
            tvBizContent.setText(string);

            List<String> list = DictionaryUtil.getInstance().getOtherDictList("MER_BIZ_CONTENT");
            if(list != null && list.size() != 0){
                businessContent = DictionaryUtil.getInstance().getMapValueByType("MER_BIZ_CONTENT").get(string);
                return;
            }
            businessContent = bmcpSelectorMerBizContentRes.getItems().get(options1).getId();
        }
    };


//    /**
//     * 扫码银行卡回调
//     *
//     * @param success
//     */
//    @Override
//    public void onCardDetected(boolean success) {
//        if (success) {
//            EXBankCardInfo exBankCardInfo = BankManager.getInstance().getCardInfo();
//            edtCardNumber.setText(exBankCardInfo.strNumbers);
//        }
//    }

    @Override
    public void onBankCardDetected(boolean b) {
        if (b) {
            EXBankCardInfo exBankCardInfo = BankManager.getInstance().getCardInfo();
            edtCardNumber.setText(exBankCardInfo.strNumbers);
        }
    }

    @Override
    public void onCameraDenied() {

    }
}

package com.lakala.elive.merapply.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerOpenInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq3;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.utils.BaiduMapUtils;
import com.lakala.elive.common.utils.DictionaryUtil;
import com.lakala.elive.common.utils.EditUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.VerifyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本信息
 * Created by wenhaogu on 2017/1/9.
 */

public class BasicInfoActivity extends BaseActivity implements BaiduMapUtils.LocationGpsListener ,
        OnGetGeoCoderResultListener {

    //百度地图工具类
    private BaiduMapUtils mBaiduMapUtils;
    private ImageView back;
    private EditText edtSignOrderName, edtContact, edtPhone, edtMerchantsAddress, edtEmail, edtMccCode;
    private TextView tvProvince, tvCity, tvCounty, tvArea, tvBusinessHours;
    private TextView tvBigClass, tvCentreClass, tvSmallClass, tvBizContent;
    private Button btnNext;
    private ImageView imgProgress;


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

    //==============================其他选择器======================================
    private ArrayList<String> shopAreaList = new ArrayList<>();//使用面积

    private ArrayList<String> merBizContentList = new ArrayList<>();//经营内容

    private OptionsPickerView shopAreaPickerView, merBizContentPickerView;

    private MerDictionaryResp.ContentBean bmcpSelectorMerBizContentRes;
    private MerDictionaryResp.ContentBean bmcpSelectorShopAreaRes;


    private String applyId;//进件id

    private long id;

    private String merchantId;

    private String mccID, mccSmallID, mccBigID;//小/中/大

    private String provinceID, cityID, countyID;//省//市//县

    private String businessArea;//营业面积

    private String businessContent;//经营内容

    private String businessTime;//营业时间

    private String merchantName;//商户名称

    private String merAddr;//商户地址

    private String email;//邮箱

    private String Contact;//联系方式

    private String phone;//

    private String accountKind;

    private String[] times = {"0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};

    private ArrayList<String> startTimeList = new ArrayList<>();//营业时间 开始

    private ArrayList<ArrayList<String>> stopTimeList = new ArrayList<>();//营业时间 结束

    private OptionsPickerView timePickerView;
    private MerApplyInfoReq3 merApplyInfoReq3;

    private LocationClient locationClient;


    private double latitude;
    private double longitude;
    private String city;
    private String childCity;

    private InputMethodManager imm;//键盘服务
    private MerDictionaryReq regionReq;
    private MerDictionaryReq mccReq;
    private String province;
    private ProgressDialog progressDialog;

    private boolean isDisPlay = false;//是否显示界面
    private MerApplyDetailsResp.ContentBean contentBean;
    private String merName;

    @Override
    protected void setContentViewId() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        setContentView(R.layout.activity_basic_info);
    }

    protected void bindView() {
        back = findView(R.id.btn_iv_back);
        EventBus.getDefault().register(this);
        TextView tvTitleName = findView(R.id.tv_title_name);
        //签购单名称
        edtSignOrderName = findView(R.id.edt_sign_order_name);
        //联系人
        edtContact = findView(R.id.edt_contact);
        //手机号码
        edtPhone = findView(R.id.edt_phone);

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
        edtMccCode = findView(R.id.edt_mcc_code);

        //商户地址 详细地址
        edtMerchantsAddress = findView(R.id.edt_merchants_address);
        //邮箱地址
        edtEmail = findView(R.id.edt_email);

        //面积
        tvArea = findView(R.id.tv_area);
        //营业时间
        tvBusinessHours = findView(R.id.tv_business_hours);
        //经营内容
        tvBizContent = findView(R.id.tv_biz_content);

        //下一步
        btnNext = findView(R.id.btn_next);

        back.setVisibility(View.VISIBLE);

        timePickerView = new OptionsPickerView(this);


        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        tvTitleName.setText("基本信息");

        imgProgress = findView(R.id.img_progress);
        EnterPieceActivity.setImageProgress(imgProgress, 6);
    }

    protected void bindEvent() {
        back.setOnClickListener(this);

        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvCounty.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        tvBusinessHours.setOnClickListener(this);
        tvBizContent.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        tvBigClass.setOnClickListener(this);
        tvCentreClass.setOnClickListener(this);
        tvSmallClass.setOnClickListener(this);
//
        timePickerView.setOnoptionsSelectListener(timeSelectListener);

        EditUtil.setEditInputType(edtContact,26,true);
        EditUtil.setAddressInputFileter(edtMerchantsAddress,128);
        EditUtil.setEditInputType(edtSignOrderName,64,false);
    }

    protected void bindData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDisPlay) {
//            showDialog("加载中...");
            beginLocation();
            Intent intent = getIntent();
            applyId = intent.getStringExtra(InformationInputActivity.APPLY_ID);
            id = intent.getLongExtra(InformationInputActivity.ID, 0);
            merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
            merName = intent.getStringExtra("MerchantName");
            if (!TextUtils.isEmpty(merName)) {
                edtSignOrderName.setText(merName);
            }
            //地址请求
            regionReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "region");
            regionReq.setId("1000");
            regionReq.setLevel(addressLevelType);
//            NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);

            //mcc请求
            mccReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "mcc");
            //mccReq.setParentMcc("0");
            mccReq.setLevel("0");
//            NetAPI.merDictionaryReq(this, this, mccReq, NetAPI.BMCP_MCC_REQUEST);

            //营业面积查询
//            NetAPI.merDictionaryReq(this, this,
//                    new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SHOP_AREA"), NetAPI.SHOP_AREA);
            //经营内容
//            NetAPI.merDictionaryReq(this, this,
//                    new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "MER_BIZ_CONTENT"), NetAPI.MER_BIZ_CONTENT);
            //营业时间
            for (String s : times) {
                startTimeList.add(s);
            }
            for (String s : startTimeList) {
                ArrayList<String> strings = new ArrayList<>();
                for (int x = 0; x < startTimeList.size(); x++) {
                    strings.add(startTimeList.get(x));
                }
                stopTimeList.add(strings);
            }

            //初始化请求数据
            merApplyInfoReq3 = new MerApplyInfoReq3();
            merApplyInfoReq3.setMerOpenInfo(new MerOpenInfo());
            merApplyInfoReq3.setSubmitInfoType("BASE_INFO_SAVE");//BASE_INFO_SAVE.商户基本信息 POS_INFO_SAVE.机具信息
            merApplyInfoReq3.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
//            merApplyInfoReq3.setTerminalInfo(new TerminalInfo());
            closeDialog();

            if (!TextUtils.isEmpty(applyId)) {
                showProgressDialog("加载中...");//获取进件回显数据
                NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
            }
        }

        isDisPlay = true;

    }

    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void getCityId() {
        if (!TextUtils.isEmpty(province)) {
            List<String> list = DictionaryUtil.getInstance().getProvinceStrList();
            if(list != null && list.size()>0){
                for(int i = 0 ;i < list.size();i++){
                    String string = list.get(i);
                    if(!TextUtils.isEmpty(string)){
                        if(string.contains(province) || province.contains(string)){
                            provinceID = DictionaryUtil.getInstance().getProvinceCode(i);
                            break;
                        }
                    }
                }
                if(!TextUtils.isEmpty(provinceID)){
                    List<String> cList = DictionaryUtil.getInstance().getCityStrList(provinceID);
                    cityList.clear();
                    if(cList != null && cList.size() != 0){
                        cityList.addAll(cList);
                        EventBus.getDefault().post("city");
                    } else {
                        getId();
                    }
                }
            } else {
                List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionProvinceResp.getItems();
                for (int i = 0; i < regions.size(); i++) {
                    if (regions.get(i).getValue().equals(province)) {
                        provinceID = regions.get(i).getId();
                        getId();
                        break;
                    }
                }
            }
        }
    }

    private void getId() {
        regionReq.setId(provinceID);
        regionReq.setLevel(addressLevelType = CITY);
        showProgressDialog("获取地址中...");
        NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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
                    if(bigClassList != null && bigClassList.size() !=0)
                    selectAddress(bigClassPickerView, bigClassList, bigClassSelectListener);
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
            case NetAPI.ACTION_MER_APPLY3:
                Intent intent = new Intent(this, MachinesToolsInfoActivity.class);
                if(TextUtils.isEmpty(accountKind)){
                    accountKind = "";
                }
                startActivity(intent
                        .putExtra(InformationInputActivity.APPLY_ID, applyId)
                        .putExtra(InformationInputActivity.ID, id)
                        .putExtra("AccountKind",accountKind));
                break;
            case NetAPI.ACTION_MER_APPLY_DETAILS:
                contentBean = ((MerApplyDetailsResp) obj).getContent();
                //如果有数据,就回显到控件上
                //编辑回显
                if (null != contentBean && null != contentBean.getMerOpenInfo() && !TextUtils.isEmpty(contentBean.getMerOpenInfo().getMerchantName())) {
                    MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
                    accountKind = merOpenInfo.getAccountKind();

                    edtContact.setText(getNoEmptyString(merOpenInfo.getContact()));
                    edtPhone.setText(getNoEmptyString(merOpenInfo.getMobileNo()));
                    edtEmail.setText(getNoEmptyString(merOpenInfo.getEmailAddr()));
                    tvBigClass.setText(getNoEmptyString(merOpenInfo.getMccBigTypeStr()));
                    tvCentreClass.setText(getNoEmptyString(merOpenInfo.getMccSmallTypeStr()));
                    tvSmallClass.setText(getNoEmptyString(merOpenInfo.getMccCodeStr()));
                    edtMccCode.setText(getNoEmptyString(merOpenInfo.getMccCode()));
                    edtMerchantsAddress.setText(getNoEmptyString(merOpenInfo.getMerAddr()));
                    tvArea.setText(getNoEmptyString(merOpenInfo.getBusinessAreaStr()));
                    tvBusinessHours.setText(getNoEmptyString(merOpenInfo.getBusinessTime()));
                    tvBizContent.setText(getNoEmptyString(merOpenInfo.getBussinessContentStr()));
                    tvProvince.setText(getNoEmptyString(merOpenInfo.getProvince()));
                    tvCity.setText(getNoEmptyString(merOpenInfo.getCity()));
                    tvCounty.setText(getNoEmptyString(merOpenInfo.getDistrict()));
                    final String mer = merOpenInfo.getMerchantName();
                    if(!TextUtils.isEmpty(mer)){
                        edtSignOrderName.setText(mer);
                    }
//                    MerApplyDetailsResp.ContentBean.TerminalInfo terminalInfo = contentBean.getTerminalInfo();
//                    if (null != terminalInfo) {
//                        salesWay = terminalInfo.getDeviceDrawMethod();
//                        switch (getNoEmptyString(terminalInfo.getDeviceDrawMethod())) {
//                            case "44"://租赁
//                                tvSalesWay.setText("租赁");
//                                rlDeposit.setVisibility(View.VISIBLE);
//                                rlRent.setVisibility(View.VISIBLE);
//                                rlAmount.setVisibility(View.GONE);
//                                edtDeposit.setText(((int) terminalInfo.getDeviceDeposit()) + "");
//                                edtRent.setText(((int) terminalInfo.getDeviceRent()) + "");
//                                break;
//                            case "45"://出售
//                                tvSalesWay.setText("出售");
//                                rlDeposit.setVisibility(View.GONE);
//                                rlRent.setVisibility(View.GONE);
//                                rlAmount.setVisibility(View.VISIBLE);
//                                edtAmount.setText(((int) terminalInfo.getDeviceSaleAmount()) + "");
//                                break;
//                            case "46"://赠送
//                                tvSalesWay.setText("赠送");
//                                rlDeposit.setVisibility(View.GONE);
//                                rlRent.setVisibility(View.GONE);
//                                rlAmount.setVisibility(View.GONE);
//                                break;
//                        }
//                    }
                    //回显赋值
                    merchantId = merOpenInfo.getMerchantId();
                    provinceID = merOpenInfo.getProvinceCode();
                    cityID = merOpenInfo.getCityCode();
                    countyID = merOpenInfo.getDistrictCode();
                    mccBigID = merOpenInfo.getMccBigType();
                    mccSmallID = merOpenInfo.getMccSmallType();
                    mccID = merOpenInfo.getMccCode();
                    businessArea = merOpenInfo.getBusinessArea();
                    businessTime = merOpenInfo.getBusinessTime();
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
                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.BMCP_REGION_REQUEST || method == NetAPI.BMCP_MCC_REQUEST
                || method == NetAPI.SHOP_AREA || method == NetAPI.MER_BIZ_CONTENT || method == NetAPI.SETTLE_PERIOD) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_MER_APPLY3) {
            Utils.showToast(this, statusCode);
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        }
    }

    @Override
    public void onClick(View view) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
        }

        switch (view.getId()) {
            case R.id.btn_iv_back:
                finish();
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
                    Toast.makeText(BasicInfoActivity.this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(centreClassPickerView, centreClassList, centreClassSelectListener);
                break;
            case R.id.tv_small_class://商户类别
                if (smallClassList == null || smallClassList.size() == 0) {
                    smallClassList = new ArrayList<String>();
                    Toast.makeText(BasicInfoActivity.this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BasicInfoActivity.this, "请先选择省！", Toast.LENGTH_SHORT).show();
                    cityList.clear();
                    break;
                }
                if (cityList == null || cityList.size() == 0) {
                    cityList = new ArrayList<String>();
                    Toast.makeText(BasicInfoActivity.this, "请选择省！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(cityPickerView, cityList, citySelectListener);
                break;
            case R.id.tv_county:// 县
                if(TextUtils.isEmpty(tvProvince.getText().toString())){
                    Toast.makeText(BasicInfoActivity.this, "请先选择省！", Toast.LENGTH_SHORT).show();
                    cityList.clear();
                    break;
                }
                if(TextUtils.isEmpty(tvCity.getText().toString())){
                    Toast.makeText(BasicInfoActivity.this, "请先选择市！", Toast.LENGTH_SHORT).show();
                    countyList.clear();
                    break;
                }
                if (countyList == null || countyList.size() == 0) {
                    countyList = new ArrayList<String>();
                    Toast.makeText(BasicInfoActivity.this, "请重新选择省！", Toast.LENGTH_SHORT).show();
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
            case R.id.tv_business_hours://营业时间
                selectTime();
                break;
            case R.id.btn_next://下一步
                if (getTextContext()) {
//                    if(!TextUtils.isEmpty(merAddr)&&!TextUtils.isEmpty(city)){
//                        showProgressDialog("获取经纬度中...");
//                        searchButtonProcess(city,merAddr);
//                    }
                    next();
                }
                break;

        }
    }

    private boolean getTextContext() {
        merchantName = edtSignOrderName.getText().toString().trim();
        merAddr = edtMerchantsAddress.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        Contact = edtContact.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(merchantName)) {
            Utils.showToast(this, "请输入签购单名称");
        } else if (merchantName.length() <= 4) {
            Utils.showToast(this, "请输入5-40位签购单名称");
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
        } else if (TextUtils.isEmpty(edtMccCode.getText())) {
            Utils.showToast(this, "请确认MCC编码");
        } else if (edtMccCode.getText().toString().trim().length()!=4) {
            Utils.showToast(this, "请确认MCC编码");
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
        } else if (TextUtils.isEmpty(tvBusinessHours.getText().toString().trim())) {
            Utils.showToast(this, "请选择营业时间");
        } else if (TextUtils.isEmpty(tvBizContent.getText().toString().trim())) {
            Utils.showToast(this, "请选择经营内容");
        } else {
            return true;
        }
        return false;
    }

    private void next() {
        merApplyInfoReq3.getMerOpenInfo().setApplyId(applyId);
        merApplyInfoReq3.getMerOpenInfo().setMerchantId(merchantId);
        merApplyInfoReq3.getMerOpenInfo().setMerchantName(merchantName);
//        merApplyInfoReq3.getMerOpenInfo().setMerAddr(tvProvince.getText().toString().trim() + tvCity.getText().toString().trim()
//                + tvCounty.getText().toString().trim()+merAddr);
        merApplyInfoReq3.getMerOpenInfo().setMerAddr(merAddr);
        merApplyInfoReq3.getMerOpenInfo().setProvinceCode(provinceID);
        merApplyInfoReq3.getMerOpenInfo().setCityCode(cityID);
        merApplyInfoReq3.getMerOpenInfo().setDistrictCode(countyID);
        if (!mccID.equals(edtMccCode.getText().toString())) {
            mccID = edtMccCode.getText().toString();
        }
        merApplyInfoReq3.getMerOpenInfo().setMccCode(mccID);
        merApplyInfoReq3.getMerOpenInfo().setBusinessArea(businessArea);
        merApplyInfoReq3.getMerOpenInfo().setBusinessTime(businessTime);
        merApplyInfoReq3.getMerOpenInfo().setBusinessContent(businessContent);
        merApplyInfoReq3.getMerOpenInfo().setEmailAddr(email);
        merApplyInfoReq3.getMerOpenInfo().setContact(Contact);
        merApplyInfoReq3.getMerOpenInfo().setMobileNo(phone);
        merApplyInfoReq3.getMerOpenInfo().setLongitude(longitude + "");
        merApplyInfoReq3.getMerOpenInfo().setLatitude(latitude + "");
        merApplyInfoReq3.getMerOpenInfo().setMccBigType(mccBigID);
        merApplyInfoReq3.getMerOpenInfo().setMccSmallType(mccSmallID);

        if(!checkParams()){
            return;
        }
        showProgressDialog("提交中...");
        NetAPI.merApply3(this, this, merApplyInfoReq3);
    }

    private boolean checkParams(){
        // TODO 对参数进行判断
        if(TextUtils.isEmpty(provinceID)){
            Utils.showToast(BasicInfoActivity.this, "省地址ID为空！请重新选择。");
            return false;
        }
        if(TextUtils.isEmpty(cityID)){
            Utils.showToast(BasicInfoActivity.this, "市地址ID为空！请重新选择。");
            return false;
        }
        if(TextUtils.isEmpty(countyID)){
            Utils.showToast(BasicInfoActivity.this, "县地址ID为空！请重新选择。");
            return false;
        }

        if(TextUtils.isEmpty(email)){
            Utils.showToast(BasicInfoActivity.this, "请输入邮箱地址");
            return false;
        }
        if(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
            Utils.showToast(BasicInfoActivity.this, "请输入正确邮箱地址");
            return false;
        }
        return true;
    }
    /**
     * 选择
     */
    private void selectAddress(OptionsPickerView pickerView, ArrayList<String> list, OptionsPickerView.OnOptionsSelectListener listener) {
        if(pickerView != null && pickerView.isShowing()){
            pickerView.dismiss();
        }
        pickerView = new OptionsPickerView(this);
        pickerView.setPicker(list);
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);
        pickerView.setOnoptionsSelectListener(listener);
        pickerView.show();
    }

    private void selectTime() {
        if(timePickerView.isShowing()){
            timePickerView.dismiss();
        }
        timePickerView.setPicker(startTimeList, stopTimeList, false);
        timePickerView.setLabels("~");
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        timePickerView.show();
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
            NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);

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
            NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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
            NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
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
            NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
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
            edtMccCode.setText(mccID);

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


    //营业时间
    OptionsPickerView.OnOptionsSelectListener timeSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (!startTimeList.get(options1).equals(stopTimeList.get(options1).get(option2))) {
                tvBusinessHours.setText(startTimeList.get(options1) + "~" + stopTimeList.get(options1).get(option2));
                businessTime = startTimeList.get(options1) + "~" + stopTimeList.get(options1).get(option2);
            } else {
                Utils.showToast(BasicInfoActivity.this, "不能选择同一个时间");
            }

        }
    };

    private void beginLocation() {
//        //开始定位
//        if (locationClient == null) {
//            locationClient = EliveApplication.getInstance().mLocationClient;
//        }
//        locationClient.registerLocationListener(bdLocationListener);
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);
//        option.setIsNeedAddress(true);
//        option.setCoorType("bd09ll");
//        option.setAddrType("all");
//        option.setScanSpan(1000);
//        option.setPriority(LocationClientOption.GpsFirst);
//        locationClient.setLocOption(option);
//        locationClient.start();

        //BaiduMapUtils
        mBaiduMapUtils = new BaiduMapUtils();
        mBaiduMapUtils.setLocationListener(this);
        mBaiduMapUtils.startGps(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.destroy();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBaiduReceiver(String receiver) {
        if (receiver.equals("ok")) {
            getCityId();
        } else if (receiver.equals("city")) {
            if(!TextUtils.isEmpty(provinceID)){
                List<String> ciList = DictionaryUtil.getInstance().getCityStrList(provinceID);
                if(ciList != null && ciList.size() != 0){
                    for(int i = 0;i < ciList.size();i++){
                        String cS = ciList.get(i);
                        if(!TextUtils.isEmpty(cS)){
                            if(cS.contains(city)||city.contains(cS)){
                                cityID = DictionaryUtil.getInstance().getCityCode(i);
                            }
                        }
                    }
                    if(!TextUtils.isEmpty(cityID)){
                        List<String> areaStrList = DictionaryUtil.getInstance().getAreaStrList(cityID);
                        countyList.clear();
                        if(areaStrList != null && areaStrList.size() != 0){
                            countyList.addAll(areaStrList);
                            EventBus.getDefault().post("country");
                        } else {
                            getCountryId();
                        }
                    }
                } else {
                    List<MerDictionaryResp.ContentBean.ItemsBean> items = bmcpRegionCityResp.getItems();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getValue().equals(city)) {
                            cityID = items.get(i).getId();
                            getCountryId();
                            break;
                        }
                    }
                }
            }
        } else if (receiver.equals("country")) {
            List<String> areaStrList = DictionaryUtil.getInstance().getAreaStrList(cityID);
            if(areaStrList != null && areaStrList.size() != 0){
                for(int i = 0;i < areaStrList.size();i++){
                    String aS = areaStrList.get(i);
                    if(!TextUtils.isEmpty(aS)){
                        if(aS.contains(childCity)||childCity.contains(aS)){
                            countyID = DictionaryUtil.getInstance().getAreaCode(i);
                        }
                    }
                }
            } else {
                List<MerDictionaryResp.ContentBean.ItemsBean> items = bmcpRegionCountyResp.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getValue().equals(childCity)) {
                        countyID = items.get(i).getId();
                        break;
                    }
                }
            }
        }
    }

    private void getCountryId() {
        regionReq.setId(cityID);
        regionReq.setLevel(addressLevelType = COUNTY);
        showProgressDialog("获取地址中...");
        NetAPI.merDictionaryReq(BasicInfoActivity.this, BasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
    }

    @Override
    public void locationSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mBaiduMapUtils.mLocation != null) {
                    BDLocation bdLocation = mBaiduMapUtils.mLocation;
                    if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                        latitude = bdLocation.getLatitude(); //纬度坐标
                        longitude = bdLocation.getLongitude();
                    }
                    city = bdLocation.getCity();
                    if (!TextUtils.isEmpty(bdLocation.getCity())) {
                        String addrStr = bdLocation.getAddrStr();
                        province = bdLocation.getProvince();
                        city = bdLocation.getCity();
                        childCity = bdLocation.getDistrict();
                        latitude = bdLocation.getLatitude(); //纬度坐标
                        longitude = bdLocation.getLongitude(); //经度坐标
                        if (province.contains("市")) {
                            province = province.substring(0, province.length() - 1);
                        }
                        tvProvince.setText(province);
                        tvCity.setText(city);
                        tvCounty.setText(childCity);
                        edtMerchantsAddress.setText(addrStr);
                        EventBus.getDefault().post("ok");
                        Log.d("BasicInfoActivity", province);
                        Log.d("BasicInfoActivity", city);
                        Log.d("BasicInfoActivity", childCity);
                        Log.d("BasicInfoActivity", "longitude:" + longitude);
                        mBaiduMapUtils.stopGps();
                        if (TextUtils.isEmpty(String.valueOf(latitude)) || TextUtils.isEmpty(String.valueOf(longitude))) {
                            return;
                        }

                    } else {
                        mBaiduMapUtils.stopGps();
//                        Utils.showToast(BasicInfoActivity.this, "定位失败");
                    }
                }
            }
        });

    }

    @Override
    public void locationErr() {
        mBaiduMapUtils.stopGps();
//        Utils.showToast(BasicInfoActivity.this, "定位失败");
    }

    //百度地图位置编码
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private GeoCodeOption option;

    /**
     * 发起搜索
     */
    public void searchButtonProcess(String city, String address) {
        option = new GeoCodeOption().city(city).address(address);
        // Geo搜索
        mSearch.geocode(option);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {//地址获取经纬度
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            closeProgressDialog();
            Toast.makeText(this, "获取所填地址的经纬度失败", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        latitude = result.getLocation().latitude;
        longitude = result.getLocation().longitude;
        next();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {//经纬度获取地址
        closeProgressDialog();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Toast.makeText(this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }
}

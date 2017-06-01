package com.lakala.elive.qcodeenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.lakala.elive.Constants;
import com.lakala.elive.EliveApplication;
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
import com.lakala.elive.common.widget.AmountView;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.activity.BasicInfoActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Q码进件的基本信息
 */
public class QCodeBasicInfoActivity extends BaseActivity implements BaiduMapUtils.LocationGpsListener, OnGetGeoCoderResultListener {

    private static final String WECHAT_PAY_FEE = "WECHAT_PAY_FEE";//微信支付
    private static final String ALIPAY_WALLET_FEE = "ALIPAY_WALLET_FEE";//支付宝

    private RelativeLayout rlAlPay, rlWx;
    private AmountView avALPayRate, avWXRate;
    private CheckBox cbALPay, cbWX,xyCheck;
    private EditText edtALPayRate, edtWXRate;

    //百度地图工具类
    private BaiduMapUtils mBaiduMapUtils;
    private ImageView back;
    private EditText edtSignOrderName, edtContact, edtPhone, edtMerchantsAddress, edtEmail, edtMccCode;
    private TextView tvProvince, tvCity, tvCounty,
//            tvArea, tvBusinessHours,
            tvSettlementCycle,xyTxt;
    private TextView tvBigClass, tvCentreClass, tvSmallClass
    , tvBizContent
                    ;
    private Button btnNext;
    private ImageView imgProgress;

    //=========================地址选择用到的变量============================

    private OptionsPickerView provincePickerView, cityPickerView, countyPickerView, settlePeriodPickerView;

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

    private ArrayList<String> settlePeriodList = new ArrayList<>();//结算周期

    private MerDictionaryResp.ContentBean bmcpSelectorSettlePeriodRes;

    @Override
    protected void setContentViewId() {
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        setContentView(R.layout.activity_qcode_basic_info);
    }

    protected void bindView() {
        xyCheck=findView(R.id.qcode_basicinfo_xy_check);
        xyTxt=findView(R.id.qcode_basicinfo_xy_txt);

        back = findView(R.id.btn_iv_back);
//        EventBus.getDefault().register(this);
        TextView tvTitleName = findView(R.id.tv_title_name);

        //支付宝
        rlAlPay = findView(R.id.rl_al_pay);
        cbALPay = findView(R.id.cb_al_pay);
        avALPayRate = findView(R.id.av_al_pay_rate);
        edtALPayRate = findView(R.id.edt_al_pay_rate);

        //微信
        rlWx = findView(R.id.rl_wx);
        cbWX = findView(R.id.cb_wx);
        avWXRate = findView(R.id.av_wx_rate);
        edtWXRate = findView(R.id.edt_wx_rate);

//        avALPayRate.setMinReat(30);
//        avWXRate.setMinReat(30);
        avALPayRate.setMax(0.60f);
        avWXRate.setMax(0.60f);

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
//        tvArea = findView(R.id.tv_area);
        //营业时间
//        tvBusinessHours = findView(R.id.tv_business_hours);
      //  经营内容
        tvBizContent = findView(R.id.tv_biz_content);
        //结算周期
        tvSettlementCycle = findView(R.id.tv_settlement_cycle);
        //下一步
        btnNext = findView(R.id.btn_next);
        back.setVisibility(View.VISIBLE);
        timePickerView = new OptionsPickerView(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        tvTitleName.setText("基本信息");
        imgProgress = findView(R.id.img_progress);
        Intent intent = getIntent();
        applyId = intent.getStringExtra(QCodeSettleInfoActivity.APPLY_ID);
        id = intent.getLongExtra(QCodeSettleInfoActivity.ID, 0);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        merName = intent.getStringExtra("MerchantName");
        String type = intent.getStringExtra("QCODETYPE");
        if (!TextUtils.isEmpty(merName)) {
            edtSignOrderName.setText(merName);
        }
        if (type != null) {
            if (type.equals(EliveApplication.PUBLICQCODE)) {
                EliveApplication.setImageQCodeProgress(imgProgress, 6, EliveApplication.PUBLICQCODE);//对公
            } else if (type.equals(EliveApplication.PRIVICEQCODE)) {
                EliveApplication.setImageQCodeProgress(imgProgress, 5, EliveApplication.PRIVICEQCODE);//对私
            }
        }
    }

    protected void bindEvent() {
        back.setOnClickListener(this);
        xyTxt.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvCounty.setOnClickListener(this);
//        tvArea.setOnClickListener(this);
//        tvBusinessHours.setOnClickListener(this);
        tvBizContent.setOnClickListener(this);

        tvSettlementCycle.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        tvBigClass.setOnClickListener(this);
        tvCentreClass.setOnClickListener(this);
        tvSmallClass.setOnClickListener(this);

//        timePickerView.setOnoptionsSelectListener(timeSelectListener);

        EditUtil.setEditInputType(edtContact, 26, true);
        EditUtil.setAddressInputFileter(edtMerchantsAddress, 128);
        EditUtil.setEditInputType(edtSignOrderName, 64, false);
    }

    protected void bindData() {
    }

    private boolean flagFirst = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDisPlay) {
            showDialog("加载中...");
//            beginLocation();
            //地址请求
            regionReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "region");
            regionReq.setId("1000");
            regionReq.setLevel(addressLevelType);
            NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);

            //mcc请求
            mccReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "mcc");
            //mccReq.setParentMcc("0");
            mccReq.setLevel("0");

            flagFirst = true;
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
            merApplyInfoReq3.setSubmitInfoType("Q_BASE_INFO_SAVE");//BASE_INFO_SAVE.商户基本信息 POS_INFO_SAVE.机具信息，Q码Q_BASE_INFO_SAVE
            merApplyInfoReq3.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            closeDialog();
            if (!TextUtils.isEmpty(applyId)) {
                showProgressDialog("加载中...");//获取进件回显数据
                NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
            }
        }
        isDisPlay = true;
    }


    @Override
    public void onClick(View view) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
        }
        switch (view.getId()) {
            case R.id.qcode_basicinfo_xy_txt://协议
                 Intent  intent=new Intent(this,QCodeProtocolWebActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_big_class://商户类别
                List<String> bigStrL = DictionaryUtil.getInstance().getMccBigStrList();
                if (bigStrL != null && bigStrL.size() > 0) {
                    bigClassList.clear();
                    bigClassList.addAll(bigStrL);
                } else {
                    bigClassList = new ArrayList<String>();
                }
                if (bigClassList == null || bigClassList.size() == 0) {
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
                    Toast.makeText(this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(centreClassPickerView, centreClassList, centreClassSelectListener);
                break;
            case R.id.tv_small_class://商户类别
                if (smallClassList == null || smallClassList.size() == 0) {
                    smallClassList = new ArrayList<String>();
                    Toast.makeText(this, "请选择商户类型！", Toast.LENGTH_SHORT).show();
                    break;
                }
                selectAddress(smallClassPickerView, smallClassList, smallClassSelectListener);
                break;
            case R.id.tv_province:// 省市
                //地址请求
                regionReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "region");
                regionReq.setId("1000");
                regionReq.setLevel(addressLevelType);
                NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
                break;
            case R.id.tv_city:// 市
                selectAddress(cityPickerView, cityList, citySelectListener);
                break;
            case R.id.tv_county:// 县
                selectAddress(countyPickerView, countyList, countySelectListener);
                break;
//            case R.id.tv_area://营业面积
//                shopAreaList = DictionaryUtil.getInstance().getOtherDictList("SHOP_AREA");
//                if (shopAreaList == null || shopAreaList.size() == 0) {
//                    shopAreaList = new ArrayList<String>();
//                    showProgressDialog("请求中...");
//                    NetAPI.merDictionaryReq(this, this,
//                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SHOP_AREA"), NetAPI.SHOP_AREA);
//                    break;
//                }
//                selectAddress(shopAreaPickerView, shopAreaList, shopAreaSelectListener);
//
//                break;
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
//            case R.id.tv_business_hours://营业时间
//                selectTime();
//                break;
            case R.id.tv_settlement_cycle://结算周期
                settlePeriodList = DictionaryUtil.getInstance().getOtherDictList("SETTLE_PERIOD_Q");
                if (settlePeriodList == null || settlePeriodList.size() == 0) {
                    settlePeriodList = new ArrayList<String>();
                    NetAPI.merDictionaryReq(this, this,
                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SETTLE_PERIOD_Q"), NetAPI.SETTLE_PERIOD_Q);
                    break;
                }
                selectAddress(settlePeriodPickerView, settlePeriodList, settlePeriodSelectListener);
                break;
            case R.id.btn_next://下一步
                if (getTextContext()) {
                    next();
                }
                break;
        }
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

    private void getId() {
        regionReq.setId(provinceID);
        regionReq.setLevel(addressLevelType = CITY);
        showProgressDialog("获取地址中...");
        NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.SETTLE_PERIOD://结算周期
                bmcpSelectorSettlePeriodRes = ((MerDictionaryResp) obj).getContent();
                settlePeriodList.clear();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorSettlePeriodRes.getItems()) {
                    settlePeriodList.add(itemsBean.getValue());
                }
                break;
            case NetAPI.BMCP_REGION_REQUEST://地址选择器数据
                if (addressLevelType.equals(PROVINCE)) {//省
                    bmcpRegionProvinceResp = ((MerDictionaryResp) obj).getContent();
                    if (bmcpRegionProvinceResp != null && bmcpRegionProvinceResp.getItems() != null) {
                        if (!flagFirst) {
                            selectAddress(provincePickerView, provinceList, provinceSelectListener);
                        } else {
                            beginLocation();
                        }
                        provinceList.clear();
                        for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionProvinceResp.getItems()) {
                            provinceList.add(regionsBean.getValue());
                        }
                        flagFirst = false;
                    } else {
                        Utils.showToast(this, "省数据获取失败,请重新点击获取");
                    }
                } else if (addressLevelType.equals(CITY)) {//市
                    bmcpRegionCityResp = ((MerDictionaryResp) obj).getContent();
                    cityList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCityResp.getItems()) {
                        cityList.add(regionsBean.getValue());
                    }
                    getReceiver("city");
//                    EventBus.getDefault().post("city");
                } else {//区
                    bmcpRegionCountyResp = ((MerDictionaryResp) obj).getContent();
                    countyList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCountyResp.getItems()) {
                        countyList.add(regionsBean.getValue());
                    }
                    getReceiver("country");
//                    EventBus.getDefault().post("country");
                }
                break;
            case NetAPI.BMCP_MCC_REQUEST://mcc选择器数据
                if (mccLevelType.equals(BIG_CLASS)) {
                    bmcpMccBigClassResp = ((MerDictionaryResp) obj).getContent();
                    bigClassList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean mccsBean : bmcpMccBigClassResp.getItems()) {
                        bigClassList.add(mccsBean.getValue());
                    }
                    if (bigClassList != null && bigClassList.size() != 0)
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
//            case NetAPI.SHOP_AREA://营业面积
//                bmcpSelectorShopAreaRes = ((MerDictionaryResp) obj).getContent();
//                shopAreaList.clear();
//                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorShopAreaRes.getItems()) {
//                    shopAreaList.add(itemsBean.getValue());
//                }
//                if (shopAreaList != null && shopAreaList.size() != 0) {
//                    selectAddress(shopAreaPickerView, shopAreaList, shopAreaSelectListener);
//                }
//
//                break;
            case NetAPI.MER_BIZ_CONTENT://经营内容
                bmcpSelectorMerBizContentRes = ((MerDictionaryResp) obj).getContent();
                merBizContentList.clear();
                for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorMerBizContentRes.getItems()) {
                    merBizContentList.add(itemsBean.getValue());
                }
                if (merBizContentList != null && merBizContentList.size() != 0)
                    selectAddress(merBizContentPickerView, merBizContentList, merBizContentSelectListener);

                break;
            case NetAPI.ACTION_MER_APPLY3://Q码进件完成
                Intent intent = new Intent(this, QCodeApplyCompleteActivity.class);
                startActivity(intent
                        .putExtra(QCodeSettleInfoActivity.APPLY_ID, applyId)
                        .putExtra(QCodeSettleInfoActivity.ID, id));
                break;
            case NetAPI.ACTION_MER_APPLY_DETAILS:
                contentBean = ((MerApplyDetailsResp) obj).getContent();
                //如果有数据,就回显到控件上
                //编辑回显
                if (null != contentBean && null != contentBean.getMerOpenInfo() && !TextUtils.isEmpty(contentBean.getMerOpenInfo().getMerchantName())) {
                    MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
                    edtContact.setText(getNoEmptyString(merOpenInfo.getContact()));
                    edtPhone.setText(getNoEmptyString(merOpenInfo.getMobileNo()));
                    edtEmail.setText(getNoEmptyString(merOpenInfo.getEmailAddr()));
                    tvBigClass.setText(getNoEmptyString(merOpenInfo.getMccBigTypeStr()));
                    tvCentreClass.setText(getNoEmptyString(merOpenInfo.getMccSmallTypeStr()));
                    tvSmallClass.setText(getNoEmptyString(merOpenInfo.getMccCodeStr()));
                    edtMerchantsAddress.setText(getNoEmptyString(merOpenInfo.getMerAddr()));
//                    tvArea.setText(getNoEmptyString(merOpenInfo.getBusinessAreaStr()));
//                    tvBusinessHours.setText(getNoEmptyString(merOpenInfo.getBusinessTime()));
                    tvBizContent.setText(getNoEmptyString(merOpenInfo.getBussinessContentStr()));
                    tvProvince.setText(getNoEmptyString(merOpenInfo.getProvince()));
                    tvCity.setText(getNoEmptyString(merOpenInfo.getCity()));
                    tvCounty.setText(getNoEmptyString(merOpenInfo.getDistrict()));

                    edtMccCode.setText(getNoEmptyString(merOpenInfo.getMccCode()));
                    //设置结算周期
                    tvSettlementCycle.setText(getNoEmptyString(merOpenInfo.getSettlePeriodStr()));
                    merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(merOpenInfo.getSettlePeriod());
                    if (TextUtils.isEmpty(merName)) {
                        edtSignOrderName.setText(merOpenInfo.getMerchantName());
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
                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.SETTLE_PERIOD) {
            Utils.showToast(this, "获取数据失败");
        } else if (method == NetAPI.MER_BIZ_CONTENT) {
            Utils.showToast(this, "经营内容数据获取失败,请重新点击获取");
        } else if (method == NetAPI.SHOP_AREA) {
            Utils.showToast(this, "营业面积数据获取失败,请重新点击获取");
        } else if (method == NetAPI.BMCP_REGION_REQUEST) {
            Utils.showToast(this, "省数据获取失败,请重新点击获取");
        } else if (method == NetAPI.BMCP_MCC_REQUEST) {
            Utils.showToast(this, "商户数据获取失败,请重新点击获取");
        } else if (method == NetAPI.ACTION_MER_APPLY3) {
            Log.e("ErrorACTION_MER_APPLY3", statusCode);
            Utils.showToast(this, "提交失败:" + statusCode);
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        }
    }


    private boolean checkStrLength(String string, final int maxLength) {
        if (TextUtils.isEmpty(string)) return false;
        if (string.length() > maxLength) return true;

        int count = 0;
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (c < 128) {
                count += 1;
            } else {
                count += 2;
            }
            if (count > maxLength) {
                return true;
            }
        }
        return false;
    }


    private int selectorCount = 0;

    private boolean getTextContext() {
        merchantName = edtSignOrderName.getText().toString().trim();
        merAddr = edtMerchantsAddress.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        Contact = edtContact.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        merApplyInfoReq3.getMerOpenInfo().setApplyId(applyId);
        merApplyInfoReq3.getMerOpenInfo().setMerchantId(merchantId);
        merApplyInfoReq3.getMerOpenInfo().setMerchantName(merchantName);
//        merApplyInfoReq3.getMerOpenInfo().setMerAddr(tvProvince.getText().toString().trim() + tvCity.getText().toString().trim()
//                + tvCounty.getText().toString().trim()+merAddr);
        merApplyInfoReq3.getMerOpenInfo().setMerAddr(merAddr);
        merApplyInfoReq3.getMerOpenInfo().setProvinceCode(provinceID);
        merApplyInfoReq3.getMerOpenInfo().setCityCode(cityID);
        merApplyInfoReq3.getMerOpenInfo().setDistrictCode(countyID);

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

        merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(merApplyInfoReq3.getMerOpenInfo().getSettlePeriod());
        merApplyInfoReq3.setCardAppRateInfoList(new ArrayList<MerApplyInfoReq3.CardAppRateInfo>());

        List<MerApplyInfoReq3.CardAppRateInfo> cardAppRateInfoList = merApplyInfoReq3.getCardAppRateInfoList();
        cardAppRateInfoList.clear();

        if (TextUtils.isEmpty(tvBigClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户大类别");
            return  false;
        }
        if (TextUtils.isEmpty(tvCentreClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户中类别");
            return  false;
        }
        if (TextUtils.isEmpty(tvSmallClass.getText().toString().trim())) {
            Utils.showToast(this, "请选择商户小类别");
            return  false;
        }
        if (TextUtils.isEmpty(edtMccCode.getText())) {
            Utils.showToast(this, "请确认MCC编码");
            return  false;
        }
        if (edtMccCode.getText().toString().trim().length() != 4) {
            Utils.showToast(this, "请确认MCC编码的位数是否是4位");
            return  false;
        }

        if (!edtMccCode.getText().toString().equals(mccID)) {
            mccID = edtMccCode.getText().toString();
        }
        merApplyInfoReq3.getMerOpenInfo().setMccCode(mccID);

        if (TextUtils.isEmpty(merchantName)) {
            Utils.showToast(this, "请输入商户经营名称");
        } else if (merchantName.length() <= 4) {
            Utils.showToast(this, "请输入5-64位商户名称");
        } else if (TextUtils.isEmpty(Contact)) {
            Utils.showToast(this, "请输入联系人");
        } else if (Contact.length() < 2) {
            Utils.showToast(this, "联系人姓名不能少于2个字符");
        } else if (EditUtil.getTextCharLength(Contact) < 4) {
            Utils.showToast(this, "联系人应该是4-26个字符");
        } else if (TextUtils.isEmpty(phone)) {
            Utils.showToast(this, "请输入手机号");
        } else
//        if (!VerifyUtil.isMobilehone(phone)) {
//            Utils.showToast(this, "请输入正确的手机号");
//        } else
            if (TextUtils.isEmpty(email)) {
                Utils.showToast(this, "请输入邮箱");
            } else if (TextUtils.isEmpty(tvBigClass.getText().toString().trim())) {
                Utils.showToast(this, "请选择商户大类别");
            } else if (TextUtils.isEmpty(tvCentreClass.getText().toString().trim())) {
                Utils.showToast(this, "请选择商户中类别");
            } else if (TextUtils.isEmpty(tvSmallClass.getText().toString().trim())) {
                Utils.showToast(this, "请选择商户小类别");
            } else if (TextUtils.isEmpty(edtMccCode.getText())) {
                Utils.showToast(this, "请确认MCC编码");
            } else if (edtMccCode.getText().toString().trim().length() != 4) {
                Utils.showToast(this, "请确认MCC编码的位数是否是4位");
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
            } else if (email.length() > 64) {
                Utils.showToast(this, "邮箱位数不能大于64");
            } else
//            if (TextUtils.isEmpty(tvArea.getText().toString().trim())) {
//                Utils.showToast(this, "请选择营业面积");
//            } else if (TextUtils.isEmpty(tvBusinessHours.getText().toString().trim())) {
//                Utils.showToast(this, "请选择营业时间");
//            } else
 if (TextUtils.isEmpty(tvBizContent.getText().toString().trim())) {
                Utils.showToast(this, "请选择经营内容");
            } else
            if (TextUtils.isEmpty(tvSettlementCycle.getText().toString().trim())) {
                Utils.showToast(this, "请选择结算周期");
            } else if(!xyCheck.isChecked()){
                Utils.showToast(this, "Q码收款服务协议未选择");
            }else {
                if (cbALPay.isChecked()) {//支付宝
                    selectorCount++;
                    String aLPayRate = (new BigDecimal(avALPayRate.getText()).divide(new BigDecimal(100))).toString();
                    MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(ALIPAY_WALLET_FEE, aLPayRate, edtALPayRate.getText().toString().trim());
                    cardAppRateInfo.setApplyId(applyId);
                    //cardAppRateInfo.setTerminalId("");
                    cardAppRateInfoList.add(cardAppRateInfo);
                }
                if (cbWX.isChecked()) {//微信
                    selectorCount++;
                    String wXRate = (new BigDecimal(avWXRate.getText()).divide(new BigDecimal(100))).toString();
                    MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(WECHAT_PAY_FEE, wXRate, edtWXRate.getText().toString().trim());
                    cardAppRateInfo.setApplyId(applyId);
                    cardAppRateInfoList.add(cardAppRateInfo);
                }
                if (selectorCount <= 0) {
                    Utils.showToast(this, "至少选择一种支付方式");
                    return false;
                } else {
                    searchButtonProcess(tvCity.getText().toString().trim(), merAddr);
                    return true;
                }
            }

        return false;
    }

    private void next() {
        if (!checkParams()) {
            return;
        }
        showProgressDialog("提交中...");
        NetAPI.merApply3(this, this, merApplyInfoReq3);
    }

    private boolean checkParams() {
        // TODO 对参数进行判断
        if (TextUtils.isEmpty(provinceID)) {
            Utils.showToast(QCodeBasicInfoActivity.this, "省地址ID为空！请重新选择。");
            return false;
        }
        if (TextUtils.isEmpty(cityID)) {
            Utils.showToast(QCodeBasicInfoActivity.this, "市地址ID为空！请重新选择。");
            return false;
        }
        if (TextUtils.isEmpty(countyID)) {
            Utils.showToast(QCodeBasicInfoActivity.this, "县地址ID为空！请重新选择。");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Utils.showToast(QCodeBasicInfoActivity.this, "请输入邮箱地址");
            return false;
        }
        if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            Utils.showToast(QCodeBasicInfoActivity.this, "请输入正确邮箱地址");
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

    private void selectTime() {
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
            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionProvinceResp.getItems();
            provinceID = regions.get(options1).getId() + "";
            regionReq.setId(regions.get(options1).getId() + "");
            regionReq.setLevel(addressLevelType = CITY);
            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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
            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCityResp.getItems();
            cityID = regions.get(options1).getId() + "";
            regionReq.setId(regions.get(options1).getId() + "");
            regionReq.setLevel(addressLevelType = COUNTY);
            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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
            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCountyResp.getItems();
            countyID = regions.get(options1).getId() + "";
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
            if (list != null && list.size() > 0) {
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
            if (list1 != null && list1.size() > 0) {
                centreClassList.clear();
                centreClassList.addAll(list1);
                return;
            }

            showProgressDialog("获取信息中...");
            NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);
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
            if (list != null && list.size() > 0) {
                mccSmallID = DictionaryUtil.getInstance().getMccCenterCode(options1);
            } else {
                mccSmallID = bmcpMccCentreClassResp.getItems().get(options1).getId();
            }

//            List<MerDictionaryResp.ContentBean.ItemsBean> mccs = bmcpMccCentreClassResp.getItems();
            mccReq.setParentMcc(mccSmallID);
            mccReq.setLevel(mccLevelType = SMALL_CLASS);

            List<String> list1 = DictionaryUtil.getInstance().getMccSmallStrList(mccSmallID);
            if (list1 != null && list1.size() > 0) {
                smallClassList.clear();
                smallClassList.addAll(list1);
                return;
            }
            showProgressDialog("获取信息中...");
            NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, mccReq, NetAPI.BMCP_MCC_REQUEST);

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
            if (list != null && list.size() > 0) {
                mccID = DictionaryUtil.getInstance().getMccSmallCode(options1);
            } else {
                mccID = bmcpMccSmallClassResp.getItems().get(options1).getId();
            }
            edtMccCode.setText(mccID);


        }
    };

//    //营业面积
//    OptionsPickerView.OnOptionsSelectListener shopAreaSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
//        @Override
//        public void onOptionsSelect(int options1, int option2, int options3) {
//            if (shopAreaList.size() < 1) {
//                return;
//            }
//            String string = shopAreaList.get(options1);
//            tvArea.setText(string);
//            List<String> list = DictionaryUtil.getInstance().getOtherDictList("SHOP_AREA");
//            if (list != null && list.size() != 0) {
//                businessArea = DictionaryUtil.getInstance().getMapValueByType("SHOP_AREA").get(string);
//                return;
//            }
//            businessArea = bmcpSelectorShopAreaRes.getItems().get(options1).getId();
//        }
//    };
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
            if (list != null && list.size() != 0) {
                businessContent = DictionaryUtil.getInstance().getMapValueByType("MER_BIZ_CONTENT").get(string);
                return;
            }
            businessContent = bmcpSelectorMerBizContentRes.getItems().get(options1).getId();

        }
    };


//    //营业时间
//    OptionsPickerView.OnOptionsSelectListener timeSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
//        @Override
//        public void onOptionsSelect(int options1, int option2, int options3) {
//            if (!startTimeList.get(options1).equals(stopTimeList.get(options1).get(option2))) {
//                tvBusinessHours.setText(startTimeList.get(options1) + "~" + stopTimeList.get(options1).get(option2));
//                businessTime = startTimeList.get(options1) + "~" + stopTimeList.get(options1).get(option2);
//            } else {
//                Utils.showToast(QCodeBasicInfoActivity.this, "不能选择同一个时间");
//            }
//
//        }
//    };

    private void beginLocation() {
        //BaiduMapUtils
        mBaiduMapUtils = new BaiduMapUtils();
        mBaiduMapUtils.setLocationListener(this);
        mBaiduMapUtils.startGps(this);
    }


    BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
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
                    getReceiver("ok");
                    Log.d("QCodeBasicInfoActivity", province);
                    Log.d("QCodeBasicInfoActivity", city);
                    Log.d("QCodeBasicInfoActivity", childCity);
                    Log.d("QCodeBasicInfoActivity", "longitude:" + longitude);
                    if (TextUtils.isEmpty(String.valueOf(latitude)) || TextUtils.isEmpty(String.valueOf(longitude))) {
                        return;
                    }
                } else {
                    Utils.showToast(QCodeBasicInfoActivity.this, "定位失败");
//                        ToastUtil.showMsg("定位失败，请稍后再试！");
                }
                EliveApplication.getInstance().mLocationClient.unRegisterLocationListener(bdLocationListener);
                EliveApplication.getInstance().mLocationClient.stop();
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除地理编码
        mSearch.destroy();
//        EventBus.getDefault().unregister(this);
    }

    public void getReceiver(String receiver) {
        if (receiver.equals("ok")) {
            getCityId();
        } else if (receiver.equals("city")) {
            List<MerDictionaryResp.ContentBean.ItemsBean> items = bmcpRegionCityResp.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getValue().equals(city)) {
                    cityID = items.get(i).getId();
                    getCountryId();
                    break;
                }
            }
        } else if (receiver.equals("country")) {
            List<MerDictionaryResp.ContentBean.ItemsBean> items = bmcpRegionCountyResp.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getValue().equals(childCity)) {
                    countyID = items.get(i).getId();
                    break;
                }
            }
        }
    }


    private void getCountryId() {
        regionReq.setId(cityID);
        regionReq.setLevel(addressLevelType = COUNTY);
        showProgressDialog("获取地址中...");
        NetAPI.merDictionaryReq(QCodeBasicInfoActivity.this, QCodeBasicInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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

                        getReceiver("ok");
                        Log.d("QCodeBasicInfoActivity", province);
                        Log.d("QCodeBasicInfoActivity", city);
                        Log.d("QCodeBasicInfoActivity", childCity);
                        Log.d("QCodeBasicInfoActivity", "longitude:" + longitude);
                        mBaiduMapUtils.stopGps();
                        if (TextUtils.isEmpty(String.valueOf(latitude)) || TextUtils.isEmpty(String.valueOf(longitude))) {
                            return;
                        }
                    } else {
                        Utils.showToast(QCodeBasicInfoActivity.this, "定位失败");
                        if (mBaiduMapUtils != null) {
                            mBaiduMapUtils.stopGps();
                        }
                    }
                }
            }
        });
    }


    @Override
    public void locationErr() {
        Utils.showToast(QCodeBasicInfoActivity.this, "定位失败");
    }

    //结算周期
    OptionsPickerView.OnOptionsSelectListener settlePeriodSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (settlePeriodList.size() < 1) {
                return;
            }

            String string = settlePeriodList.get(options1);
            tvSettlementCycle.setText(string);
            List<String> list = DictionaryUtil.getInstance().getOtherDictList("SETTLE_PERIOD_Q");
            if (list != null && list.size() != 0) {
                String id = DictionaryUtil.getInstance().getMapValueByType("SETTLE_PERIOD_Q").get(string);
                merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(id);
                return;
            }

            tvSettlementCycle.setText(settlePeriodList.get(options1));
            merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(bmcpSelectorSettlePeriodRes.getItems().get(options1).getId());
            // settlePeriod = bmcpSelectorSettlePeriodRes.getItems().get(options1).getId();
        }
    };


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
//            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
//                    .show();
            return;
        }
        latitude = result.getLocation().latitude;
        longitude = result.getLocation().longitude;

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
//        Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {//经纬度获取地址
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
//                    .show();
            return;
        }
        Toast.makeText(this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }
}

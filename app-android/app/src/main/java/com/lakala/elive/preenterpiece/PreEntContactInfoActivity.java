package com.lakala.elive.preenterpiece;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.utils.EditUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.VerifyUtil;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceSubmitInfoRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceSubmitInfoResponse;


import java.util.ArrayList;
import java.util.List;

/**
 * 合作商预进件的联系方式界面
 */
public class PreEntContactInfoActivity extends BaseActivity implements OnGetGeoCoderResultListener {
    private String TAG = getClass().getSimpleName();

    private EditText mNameEdt;//姓名
    private EditText mContentPhoneEdt;//联系电话
    private EditText edtMerchantsAddress;//详细地址
    private TextView tvProvince;
    private TextView tvCity;
    private TextView tvCounty;
    private ImageView imgProgress;
    private Button btnNext;
    private ImageView iBtnBack;

    private String applyId;
    private String city;
    private double latitude;
    private double longitude;
    private String childCity;
    private LocationClient locationClient;
    //省//市//县
    private String provinceID;
    private String cityID;
    private String countyID;

    private String province;
    //获取省市区的数据
    private MerDictionaryReq regionReq;
    private PreEnPieceSubmitInfoRequ preEnPieceSubmitInfoRequ;
    private PreEnPieceDetailResponse.ContentBean contentBean;//详情传过来的回显数据

    private PreEnPieceDetailRequ preEnPieceDetailRequ;

    @Override
    protected void setContentViewId() {
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        setContentView(R.layout.activity_coopreentcontactinfo);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        btnNext = findView(R.id.btn_next);
        iBtnBack = findView(R.id.btn_iv_back);
        imgProgress = findView(R.id.img_progress);
        tvProvince = findView(R.id.tv_pre_province);//省市
        tvCity = findView(R.id.tv_pre_city);//市区
        tvCounty = findView(R.id.tv_pre_county);//县
        mNameEdt = findView(R.id.edt_preentermerchants_name);
        mContentPhoneEdt = findView(R.id.edt_preentermerchants_phone);
        edtMerchantsAddress = findView(R.id.edt_preentermerchants_address);//商户地址 详细地址
        iBtnBack.setVisibility(View.VISIBLE);
        //开始定位
        beginLocation();
        EliveApplication.setImageProgress(imgProgress, 1);
        EditUtil.setAddressInputFileter(edtMerchantsAddress, 128);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvCounty.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("联系方式");
        if (getIntent() != null) {
            Intent intent = getIntent();
            applyId = intent.getStringExtra("APPLYID");
            if (!TextUtils.isEmpty(applyId)) {//如果是编辑进来的，则调用详情接口取数据
                showProgressDialog("加载中...");
                preEnPieceDetailRequ = new PreEnPieceDetailRequ();
                preEnPieceDetailRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
                preEnPieceDetailRequ.setApplyId(applyId);
                NetAPI.preEnterPieDetailRequest(this, this, preEnPieceDetailRequ);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //地址请求
        regionReq = new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "region");
        regionReq.setId("1000");
        regionReq.setLevel(addressLevelType);
        NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_next://下一步
                if (getTextContext()) {
                    next(nameStr, phoneStr, provinceID, cityID, countyID, latitude + "", longitude + "", merAddrStr);
                }
                break;
            case R.id.tv_pre_province:// 省市
                selectAddress(provincePickerView, provinceList, provinceSelectListener);
                break;
            case R.id.tv_pre_city:// 市
                selectAddress(cityPickerView, cityList, citySelectListener);
                break;
            case R.id.tv_pre_county:// 县
                selectAddress(countyPickerView, countyList, countySelectListener);
                break;
        }
    }

    /**
     * 选择弹出框
     */
    private void selectAddress(OptionsPickerView pickerView, ArrayList<String> list, OptionsPickerView.OnOptionsSelectListener listener) {
        pickerView = new OptionsPickerView(this);
        pickerView.setPicker(list);
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);
        pickerView.setOnoptionsSelectListener(listener);
        pickerView.show();
    }

    private void next(String name, String phone, String privoceCode, String cityCode, String districtCode, String lat, String lon, String address) {
        preEnPieceSubmitInfoRequ = new PreEnPieceSubmitInfoRequ();
        preEnPieceSubmitInfoRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        PreEnPieceSubmitInfoRequ.PartnerApplyInfo partnerApplyInfo = new PreEnPieceSubmitInfoRequ.PartnerApplyInfo();
        if (!TextUtils.isEmpty(applyId)) {//如果applyID不为空，认为是从编辑进来的
            partnerApplyInfo.setApplyId(applyId);
        }
        partnerApplyInfo.setProcess("1");
        partnerApplyInfo.setApplyType("1");
        partnerApplyInfo.setApplyChannel("01");
        preEnPieceSubmitInfoRequ.setMerApplyInfo(partnerApplyInfo);
        PreEnPieceSubmitInfoRequ.MerOpenInfo merOpenInfo = new PreEnPieceSubmitInfoRequ.MerOpenInfo();
        if (!TextUtils.isEmpty(applyId)) {//如果applyID不为空，认为是从编辑进来的
            merOpenInfo.setApplyId(applyId);
        }
        merOpenInfo.setAccountKind("57");//只在第一步传次参数
        merOpenInfo.setContact(name);//姓名
        merOpenInfo.setMobileNo(phone);//电话
        merOpenInfo.setLongitude(lon);
        merOpenInfo.setLatitude(lat);
        merOpenInfo.setProvinceCode(privoceCode);
        merOpenInfo.setCityCode(cityCode);
        merOpenInfo.setDistrictCode(districtCode);
        merOpenInfo.setMerAddr(address);
        preEnPieceSubmitInfoRequ.setMerOpenInfo(merOpenInfo);

        NetAPI.preEnterPieSubmitInfoRequest(this, this, preEnPieceSubmitInfoRequ);
    }


    public void setContent() {//编辑进入的时候定位成功后再设置
        if (contentBean != null && contentBean.merOpenInfo != null) {
            mNameEdt.setText(getNoEmptyString(contentBean.merOpenInfo.getContact()));//姓名
            mContentPhoneEdt.setText(getNoEmptyString(contentBean.merOpenInfo.getMobileNo()));//联系电话
            edtMerchantsAddress.setText(getNoEmptyString(contentBean.merOpenInfo.getMerAddr()));//详细地址

            //根据省市区的ID
            provinceID = getNoEmptyString(contentBean.merOpenInfo.getProvinceCode());
            cityID = getNoEmptyString(contentBean.merOpenInfo.getCityCode());
            countyID = getNoEmptyString(contentBean.merOpenInfo.getDistrictCode());

            tvProvince.setText(getNoEmptyString(contentBean.merOpenInfo.getProvince()));
            tvCity.setText(getNoEmptyString(contentBean.merOpenInfo.getCity()));
            tvCounty.setText(getNoEmptyString(contentBean.merOpenInfo.getDistrict()));

            longitude = Double.parseDouble(getNoEmptyString(contentBean.merOpenInfo.getLongitude()));
            latitude = Double.parseDouble(getNoEmptyString(contentBean.merOpenInfo.getLatitude()));
        }
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
                    getCode("ok");
                } else if (addressLevelType.equals(CITY)) {//市
                    bmcpRegionCityResp = ((MerDictionaryResp) obj).getContent();
                    cityList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCityResp.getItems()) {
                        cityList.add(regionsBean.getValue());
                    }
                    getCode("city");
                } else {//区
                    bmcpRegionCountyResp = ((MerDictionaryResp) obj).getContent();
                    countyList.clear();
                    for (MerDictionaryResp.ContentBean.ItemsBean regionsBean : bmcpRegionCountyResp.getItems()) {
                        countyList.add(regionsBean.getValue());
                    }
                    getCode("country");
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_003://提交商户信息
                PreEnPieceSubmitInfoResponse preEnPieceSubmitInfoResponse = (PreEnPieceSubmitInfoResponse) obj;
                if (preEnPieceSubmitInfoResponse != null) {
                    applyId = preEnPieceSubmitInfoResponse.getContent().getApplyId();
                    Intent intent = new Intent(this, PreEnterPhotoOcrActivity.class);
                    intent.putExtra("APPLYID", applyId + "");
                    startActivity(intent);
                } else {
                    Log.e(TAG, "ELIVE_PARTNER_APPLY_003返回数据为空");
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_002://编辑转态获取珊商户信息
                PreEnPieceDetailResponse preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null && preEnPieceDetailResponse.getContent() != null) {//不为空，设置数据
                    contentBean = preEnPieceDetailResponse.getContent();
                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    private String merAddrStr;
    private String nameStr;
    private String phoneStr;

    private boolean getTextContext() {
        merAddrStr = edtMerchantsAddress.getText().toString().trim();
        nameStr = mNameEdt.getText().toString().trim();
        phoneStr = mContentPhoneEdt.getText().toString().trim();
        if (TextUtils.isEmpty(nameStr)) {
            Utils.showToast(this, "请输入联系人姓名");
        } else if (TextUtils.isEmpty(phoneStr)) {
            Utils.showToast(this, "请输入手机号");
        } else if (!VerifyUtil.isMobilehone(phoneStr)) {
            Utils.showToast(this, "请输入正确的手机号");
        } else if (TextUtils.isEmpty(tvProvince.getText().toString().trim())) {
            Utils.showToast(this, "请选择省市");
        } else if (TextUtils.isEmpty(tvCity.getText().toString().trim())) {
            Utils.showToast(this, "请选择城市");
        } else if (TextUtils.isEmpty(tvCounty.getText().toString().trim())) {
            Utils.showToast(this, "请选择县区");
        } else if (TextUtils.isEmpty(merAddrStr)) {
            Utils.showToast(this, "请输入地址");
        } else if (!VerifyUtil.isAddress(merAddrStr)) {
            Utils.showToast(this, "请输入正确地址");
        } else {
            searchButtonProcess(tvCity.getText().toString().trim(), merAddrStr);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除地理编码
        mSearch.destroy();
        EliveApplication.getHttpQueue().cancelAll(NetAPI.ACTION_PHOTO_DISCERN);
    }

//    @Subscribe
//    public void getBaiduReceiver(String receiver) {
//    }

    public void getCode(String receiver) {//获取省市区
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
        NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
    }

    private void getCityId() {
        if(PreEntContactInfoActivity.this == null)return;// 该activity可能已经被回收掉。
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
        NetAPI.merDictionaryReq(this, this, regionReq, NetAPI.BMCP_REGION_REQUEST);
    }

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

    OptionsPickerView.OnOptionsSelectListener provinceSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (provinceList.size() < 1) {
                return;
            }
            tvProvince.setText(provinceList.get(options1));
            tvCity.setText("");
            tvCounty.setText("");
            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionProvinceResp.getItems();
            provinceID = regions.get(options1).getId() + "";
            regionReq.setId(regions.get(options1).getId() + "");
            regionReq.setLevel(addressLevelType = CITY);
            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(PreEntContactInfoActivity.this, PreEntContactInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
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

            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCityResp.getItems();
            cityID = regions.get(options1).getId() + "";
            regionReq.setId(regions.get(options1).getId() + "");
            regionReq.setLevel(addressLevelType = COUNTY);
            showProgressDialog("获取地址中...");
            NetAPI.merDictionaryReq(PreEntContactInfoActivity.this, PreEntContactInfoActivity.this, regionReq, NetAPI.BMCP_REGION_REQUEST);
        }
    };

    OptionsPickerView.OnOptionsSelectListener countySelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (countyList.size() < 1) {
                return;
            }
            tvCounty.setText(countyList.get(options1));
            List<MerDictionaryResp.ContentBean.ItemsBean> regions = bmcpRegionCountyResp.getItems();
            countyID = regions.get(options1).getId() + "";
        }
    };


    private void beginLocation() {
        //开始定位
        if (locationClient == null) {
            locationClient = EliveApplication.getInstance().mLocationClient;
        }
        locationClient.registerLocationListener(bdLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        option.setAddrType("all");
//        option.setScanSpan(1000);
        option.setPriority(LocationClientOption.GpsFirst);
        locationClient.setLocOption(option);
        locationClient.start();
    }

    private String addrStr;
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
                    addrStr = bdLocation.getAddrStr();
                    province = bdLocation.getProvince();
                    city = bdLocation.getCity();
                    childCity = bdLocation.getDistrict();
                    latitude = bdLocation.getLatitude(); //纬度坐标
                    longitude = bdLocation.getLongitude(); //经度坐标
                    if (province.contains("市")) {
                        province = province.substring(0, province.length() - 1);
                    }
                    handler.sendEmptyMessage(1);//发送handler通知UI更新
                } else {
                    Utils.showToast(PreEntContactInfoActivity.this, "定位失败");
                }
                EliveApplication.getInstance().mLocationClient.unRegisterLocationListener(bdLocationListener);
                EliveApplication.getInstance().mLocationClient.stop();
            }
        }

        android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {//处理更新UI的操作
                super.handleMessage(msg);
                tvProvince.setText(province);
                tvCity.setText(city);
                tvCounty.setText(childCity);
                edtMerchantsAddress.setText(addrStr);

             getCode("ok");

                Log.e(TAG, "province" + province);
                Log.e(TAG, "city" + city);
                Log.e(TAG, "childCity" + childCity);
                Log.e(TAG, "longitude:" + longitude);

                setContent();//设置编辑的转态的参数
                if (TextUtils.isEmpty(String.valueOf(latitude)) || TextUtils.isEmpty(String.valueOf(longitude))) {
                    return;
                }
            }
        };

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
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
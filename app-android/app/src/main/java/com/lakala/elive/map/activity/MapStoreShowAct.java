package com.lakala.elive.map.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.DotAndorderReq;
import com.lakala.elive.common.net.DotAndorderResp;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.SearchMapView;
import com.lakala.elive.common.widget.SearchView;
import com.lakala.elive.map.bean.ShopLatLonAloneReque;
import com.lakala.elive.map.bean.ShopLatLonAloneRespon;
import com.lakala.elive.map.bean.ShopLatLonReque;
import com.lakala.elive.map.bean.ShopLatLonRespon;
import com.lakala.elive.map.common.ImageUtil;
import com.lakala.elive.map.service.LocationService;
import com.lakala.elive.map.view.MapChooseViewGroup;
import com.lakala.elive.market.activity.DailyWorkListActivity;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.task.activity.TaskListActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaofeng on 2017/3/20.
 * 地图网点展示
 */
public class MapStoreShowAct extends Activity implements View.OnClickListener, ApiRequestListener, SearchMapView.SearchViewListener {

    private ImageView mImgBack;
    private ImageView mImgSearch;
    private EditText mEditTopSearch;

    private MapView mMapView;
    private Button mBtnMy;
    private Button mBtnTaskList;
    private LinearLayout mLinearMore;

    private TextView mTvAddPlan;
    private CheckBox mCheckLoction;

    /**
     * 搜索view
     */
    private SearchMapView searchView;

    private int mPageNo = 1; //分页查询当前页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chance_store_layout);
        mSession = Session.get(this);
        initWidget();
    }

    private void initWidget() {
        //初始化查询控件
        searchView = (SearchMapView) findViewById(R.id.main_search_layout);
        //设置监听
        searchView.setSearchViewListener(this);


        mImgBack = (ImageView) findViewById(R.id.img_task_history_back);
        mImgBack.setOnClickListener(this);
        mImgSearch = (ImageView) findViewById(R.id.img_task_history_title_search);
        mImgSearch.setOnClickListener(this);

        mEditTopSearch = (EditText) findViewById(R.id.edit_map_chance_store_search);

        mMapView = (MapView) findViewById(R.id.map_map_chance_store_map);
        mBtnMy = (Button) findViewById(R.id.btn_map_chance_store_my);
        mBtnMy.setOnClickListener(this);
        mBtnTaskList = (Button) findViewById(R.id.btn_map_chance_store_tasklist);
        mBtnTaskList.setOnClickListener(this);

        mLinearMore = (LinearLayout) findViewById(R.id.linear_map_chance_store_more);

        mCheckLoction = (CheckBox) findViewById(R.id.check_map_chance_store_location);
        mCheckLoction.setOnClickListener(this);

        mTvAddPlan = (TextView) findViewById(R.id.tv_map_chance_store_addplan);
        mTvAddPlan.setOnClickListener(this);
        initMapView();
        initStoreMsgWidget();
        setStoreVisible(false);

        mImgShowDialog = (ImageView) findViewById(R.id.img_map_choose_store_showdialog);
        mImgShowDialog.setOnClickListener(this);
        mLinearDialog = (LinearLayout) findViewById(R.id.linear_map_choose_store_dialog);
        mTvDialogReset = (TextView) findViewById(R.id.tv_map_chance_store_center_reset);
        mTvDialogReset.setOnClickListener(this);
        mTvDialogOk = (TextView) findViewById(R.id.tv_map_chance_store_center_ok);
        mTvDialogOk.setOnClickListener(this);

        mGroupScreen = (MapChooseViewGroup) findViewById(R.id.self_viewgroup_map_choose_store_screen);
        mGroupDeal = (MapChooseViewGroup) findViewById(R.id.self_viewgroup_map_choose_store_deal);
        mGroupDistance = (MapChooseViewGroup) findViewById(R.id.self_viewgroup_map_choose_store_distance);
        mGroupType = (MapChooseViewGroup) findViewById(R.id.self_viewgroup_map_choose_store_type);
        initDialogShowView();
    }

    private void initDialogShowView() {
        mGroupScreen.addChildView(
                new String[]{"全部"},
                new MapChooseViewGroup.OnSlefItemClickListener() {
                    @Override
                    public void onClickView(int position, String string) {
                        Toast.makeText(MapStoreShowAct.this, "选择项为:" + string, Toast.LENGTH_SHORT).show();
                    }
                }, -1);
        mGroupDeal.addChildView(
                new String[]{"7天无交易", "30天无交易", "60天无交易", "90天无交易"},
                new MapChooseViewGroup.OnSlefItemClickListener() {
                    @Override
                    public void onClickView(int position, String string) {
                        Toast.makeText(MapStoreShowAct.this, "选择项为:" + string, Toast.LENGTH_SHORT).show();
                    }
                }, 0);
        mGroupDistance.addChildView(
                new String[]{"1km", "3km", "5km", "10km", "12km", "15km"},
                new MapChooseViewGroup.OnSlefItemClickListener() {
                    @Override
                    public void onClickView(int position, String string) {
                        Toast.makeText(MapStoreShowAct.this, "选择项为:" + string, Toast.LENGTH_SHORT).show();
                    }
                }, 3);
        mGroupType.addChildView(
                new String[]{"传统POS", "智能POS"},
                new MapChooseViewGroup.OnSlefItemClickListener() {
                    @Override
                    public void onClickView(int position, String string) {
                        Toast.makeText(MapStoreShowAct.this, "选择项为:" + string, Toast.LENGTH_SHORT).show();
                    }
                }, 0);

    }

    private ImageView mImgShowDialog;
    private LinearLayout mLinearDialog;
    private TextView mTvDialogReset;
    private TextView mTvDialogOk;
    private MapChooseViewGroup mGroupScreen;
    private MapChooseViewGroup mGroupDeal;
    private MapChooseViewGroup mGroupDistance;
    private MapChooseViewGroup mGroupType;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_task_history_back:
                MapStoreShowAct.this.finish();
                break;
            case R.id.check_map_chance_store_location:
                // TODO 进行定位
                if (mCheckLoction.isChecked()) {
                    // TODO 定位
                    mLocationService.startLocate();
                } else {
                    // TODO 停止定位
                    mLocationService.stopLocate();
                }
                break;
            case R.id.img_task_history_title_search:
                Toast.makeText(MapStoreShowAct.this, "该功能还未实现!", Toast.LENGTH_SHORT).show();
                if (mEditTopSearch.getVisibility() == View.VISIBLE) {
                    mEditTopSearch.setVisibility(View.GONE);
                } else {
                    mEditTopSearch.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_map_chance_store_addplan://加入拜访
                addVisit(mMerShopInfo.getShopNo());
//                Toast.makeText(MapStoreShowAct.this, "加入拜访!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_map_chance_store_msg:
                if (mMerShopInfo == null) {
                    Toast.makeText(MapStoreShowAct.this, "没有选择网点!", Toast.LENGTH_SHORT).show();
                    break;
                }
                UiUtils.startActivityWithExObj(MapStoreShowAct.this, MerDetailActivity.class, Constants.EXTRAS_MER_INFO, mMerShopInfo);
                break;
            case R.id.btn_map_chance_store_my://我的网点
                Intent intent = new Intent(MapStoreShowAct.this, DailyWorkListActivity.class);
                startActivity(intent);

//                Toast.makeText(MapStoreShowAct.this, "我的网点--未实现!", Toast.LENGTH_SHORT).show();
                /*
                if(mTvAddPlan.getVisibility() == View.VISIBLE){
                    setStoreVisible(false);
                } else {
                    setStoreVisible(true);
                }
                */
                break;
            case R.id.btn_map_chance_store_tasklist:
                startActivity(new Intent(MapStoreShowAct.this, TaskListActivity.class));
                MapStoreShowAct.this.finish();
                break;
            case R.id.img_map_choose_store_showdialog:
                // TODO 显示或隐藏筛选条件
                if (mLinearDialog.getVisibility() == View.VISIBLE) {
                    animatorOpen(false);
                } else if (mLinearDialog.getVisibility() == View.GONE) {
                    animatorOpen(true);
                }
                break;
            case R.id.tv_map_chance_store_center_reset:
                // TODO 重置选项条件
                animatorOpen(false);
                break;
            case R.id.tv_map_chance_store_center_ok:
                // TODO 确认请求
                animatorOpen(false);

                break;
        }
    }


    /**
     * 加入拜访
     */
    private void addVisit(String shopNo) {
        DotAndorderReq dotAndorderReq = new DotAndorderReq();
        List<DotAndorderReq.UserOrderTask> userOrderTaskList = new ArrayList<>();
        dotAndorderReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        dotAndorderReq.setUserId(mSession.getUserLoginInfo().getUserId());
        dotAndorderReq.setOrderType("1");
        dotAndorderReq.setTaskDateStr("");

        DotAndorderReq.UserOrderTask userOrderTask = dotAndorderReq.new UserOrderTask();
        userOrderTask.setShopNo(shopNo);
        userOrderTask.setOutTaskId("");
        userOrderTaskList.add(userOrderTask);
        dotAndorderReq.setUserOrderTaskList(userOrderTaskList);
        showProgressDialog("添加中");
        NetAPI.addDotAndorder(this, this, dotAndorderReq);
    }


    private void animatorOpen(boolean bl) {
        final AnimationSet animationSet = new AnimationSet(true);
        if (bl) {
            mLinearDialog.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            scaleAnimation.setInterpolator(new OvershootInterpolator());
            scaleAnimation.setDuration(500);
            animationSet.addAnimation(scaleAnimation);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            scaleAnimation.setInterpolator(new AccelerateInterpolator());
            scaleAnimation.setDuration(300);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLinearDialog.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            animationSet.addAnimation(scaleAnimation);
        }
        mLinearDialog.startAnimation(animationSet);
    }

    /**
     * 设置是否显示网点详情
     *
     * @param bl
     */
    private void setStoreVisible(boolean bl) {
        if (bl) {
            mTvAddPlan.setVisibility(View.VISIBLE);
            mLinearMore.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLinearMore.requestLayout();
        } else {
            mTvAddPlan.setVisibility(View.GONE);
            ValueAnimator valueAnimator = ValueAnimator.ofInt(mLinearMore.getHeight(), 1);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mLinearMore.getLayoutParams().height = (int) animation.getAnimatedValue();
                    mLinearMore.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }

    private BaiduMap mBaiduMap;
    private LocationService mLocationService;

    private void initMapView() {
        mBaiduMap = mMapView.getMap();
        // 121.396551,31.238713 该地址是上海天地软件园
        LatLng cenpt = new LatLng(31.2387130022, 121.3965510000);
        MapStatus mapStatus = new MapStatus.Builder().target(cenpt).zoom(15).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);

//        initDataList();
//        initMarker();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO 这里进行点击事件.  保存这个marker。可以用于更新和显示
//                Toast.makeText(MapStoreShowAct.this, "地图点被点击了！", Toast.LENGTH_SHORT).show();
                Object object = marker.getExtraInfo().getSerializable("HOTEL");
                if (object != null && object instanceof MyShop) {
                    MyShop myShop = (MyShop) object;
                    Object object2 = myShop.object;
                    if (object2 != null && object2 instanceof MerShopInfo) {
                        mMerShopInfo = (MerShopInfo) object2;
                        initStoreMsg(mMerShopInfo);
                        setStoreVisible(true);
                        setMarkerUnChecked();
                        setMarkerChecked(marker);
                    }
                }
                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // TODO 地图非marker点被点击出发.可以作为隐藏其他
                setStoreVisible(false);
                setMarkerUnChecked();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(6));
        mLocationService = new LocationService(mBaiduMap, MapStoreShowAct.this);
    }

    private HashMap<Marker, BitmapDescriptor> mCheckMarkerMap = new HashMap<Marker, BitmapDescriptor>();
    private Marker mCheckM;
    private MerShopInfo mMerShopInfo;

    private void setMarkerChecked(Marker marker) {
        mCheckMarkerMap.put(marker, marker.getIcon());
        mCheckM = marker;
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_me_img));
    }

    private void setMarkerUnChecked() {
        if (mCheckM == null) return;
        mCheckM.setIcon(mCheckMarkerMap.get(mCheckM));
        mCheckMarkerMap.remove(mCheckM);
        mCheckM = null;
    }

    private ArrayList<MyShop> mData;

    private void initMarker() {
        if(searchView.getSearchMode()){//如果是搜索查询模式，清空之前的点
            mBaiduMap.clear();
        }
        for (Iterator iterator = mData.iterator(); iterator.hasNext(); ) {
            MyShop shop = (MyShop) iterator.next();
            LatLng ll = new LatLng(shop.latitude, shop.longitude);
            /*
            View view = LayoutInflater.from(MapStoreShowAct.this).inflate(R.layout.map_item_marker_layout, null);

            TextView shopName = (TextView) view.findViewById(R.id.tv_map_item_marker_name);
            shopName.setText(shop.name);
            // 设置坐标点下面的图标
            ImageView imageView = (ImageView) view.findViewById(R.id.img_map_item_position_img);

            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(view));
            */
            BitmapDescriptor markerView = ImageUtil.getPointBitmapDes(MapStoreShowAct.this, shop);

            Bundle bundle = new Bundle();
            bundle.putSerializable("HOTEL", shop);
            MarkerOptions markerOptions = new MarkerOptions().position(ll)
                    .icon(markerView).zIndex(shop.index).draggable(true).extraInfo(bundle);
            mBaiduMap.addOverlay(markerOptions);
        }
    }

    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return bitmap;
    }

    private TextView mTvStoreName;
    private TextView mTvStoreAddress;
    private TextView mTvStoreTel;
    private TextView mTvStoreType;
    private TextView mTvStoreMsg;
    private TextView mTvStoreStatus;

    /**
     * 初始化底层弹出体View
     */
    private void initStoreMsgWidget() {
        mTvStoreName = (TextView) findViewById(R.id.tv_map_chance_store_name);
        mTvStoreAddress = (TextView) findViewById(R.id.tv_map_chance_store_address);
        mTvStoreTel = (TextView) findViewById(R.id.tv_map_chance_store_tel);
        mTvStoreType = (TextView) findViewById(R.id.tv_map_chance_store_type);
        mTvStoreMsg = (TextView) findViewById(R.id.tv_map_chance_store_msg);
        mTvStoreStatus = (TextView) findViewById(R.id.tv_map_chance_store_status);
        mTvStoreMsg.setOnClickListener(this);
    }

    /**
     * 设置弹出框具体文字
     */
    private void initStoreMsg(MerShopInfo merShopInfo) {
        mTvStoreName.setText(merShopInfo.getShopName() + "  (" + merShopInfo.getShopNo() + ")");
        mTvStoreType.setText("" + merShopInfo.getShopCnt());
        mTvStoreAddress.setText("地址：" + merShopInfo.getShopAddress());

        if (!TextUtils.isEmpty(merShopInfo.getShopContactTel())) {
            mTvStoreTel.setText("电话：" + merShopInfo.getShopContactTel());
        } else if (!TextUtils.isEmpty(merShopInfo.getShopMobileNo())) {
            mTvStoreTel.setText("电话：" + merShopInfo.getShopMobileNo());
        }

        mTvStoreStatus.setText("" + merShopInfo.getIsVisitToday());

        setAddButtonClickable(merShopInfo);
    }


    public void setAddButtonClickable(MerShopInfo merShopInfo) {
        final String todayTaskFlag = merShopInfo.getTodayTaskFlag();
        if (!TextUtils.isEmpty(todayTaskFlag) && todayTaskFlag.equals("1")) {
            mTvAddPlan.setBackgroundResource(R.drawable.text_add_plan_cir_bg_gray);
            mTvAddPlan.setClickable(false);
        } else {
            mTvAddPlan.setBackgroundResource(R.drawable.text_add_plan_cir_bg);
            mTvAddPlan.setClickable(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mPageModel == null)
            toRequestStoreList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationService.stopLocate();
        mLocationService.destoryLocation();
        mLocationService = null;
        mPageModel = null;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    private Session mSession;


    PageReqInfo pageReqInfo = new PageReqInfo();
    private boolean flagCenter = false;

    private void toRequestStoreList() {
        showProgressDialog("网点列表加载中.....");

        if (!searchView.getSearchMode()) {
            pageReqInfo.setSearchCode(""); //不是查询模式综合查询条件消除
        }
        // TODO 服务器应该修改为根据经纬度查询网点列表
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(mPageNo);
        // TODO 这里默认为15条
//        pageReqInfo.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        pageReqInfo.setPageSize(400);
        NetAPI.queryMerShopList(this, this, pageReqInfo);
    }

    private PageModel<MerShopInfo> mPageModel;

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_MER_PAGE_LIST:
                if (obj != null && obj instanceof PageModel) {
                    mPageModel = (PageModel<MerShopInfo>) obj;
                    initMapViewMarkers();
                    if (flagCenter && mPageModel.getRows() != null && mPageModel.getRows().size() > 0 && mPageModel.getRows().get(0) != null && mPageModel.getRows().get(0).getLongitude() != null && mPageModel.getRows().get(0).getLongitude() != null) {
                        //设置中心点
                        setCenPoint(mPageModel.getRows().get(0).getLatitude(), mPageModel.getRows().get(0).getLongitude());
                    }
                    if(merShopInfoList!=null&&merShopInfoList.size()>0){//有没有经纬度的网点，去查询
                        getLatLon();
                    }
                } else {
                    Toast.makeText(MapStoreShowAct.this, "返回空数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case NetAPI.getShopLocationList://根据批量网店号查询经纬度
                ShopLatLonRespon latLonRespon = (ShopLatLonRespon) obj;
                if (latLonRespon != null && latLonRespon.getContent() != null && latLonRespon.getContent().size() > 0) {
                    if (mData == null) {
                        mData = new ArrayList<MyShop>();
                    } else if (mData.size() != 0) {
                        mData.clear();
                    }
                    for (int i = 0; i < latLonRespon.getContent().size(); i++) {
                        for (int j = 0; j < merShopInfoList.size(); j++) {
                            if ((merShopInfoList.get(j).getShopNo()).equals(latLonRespon.getContent().get(i).getShopId())) {
                                MyShop shop = new MyShop();
//                                if (storeInfo.getLatitude() != null && storeInfo.getLongitude() != null) {
                                shop.latitude = Double.parseDouble(latLonRespon.getContent().get(i).getGeo().getCoordinates().get(1));
                                shop.longitude = Double.parseDouble(latLonRespon.getContent().get(i).getGeo().getCoordinates().get(0));

//                                }
                                shop.name = merShopInfoList.get(j).getMerchantName();
                                shop.object = merShopInfoList.get(j);
                                final String todayTaskFlag = merShopInfoList.get(j).getTodayTaskFlag();
//                            if (!TextUtils.isEmpty(todayTaskFlag) && todayTaskFlag.equals("0")) {
                                mData.add(shop);
//                            }
                            }
                        }
                    }
                    initMarker();
                    if (flagCenter && mData != null && mData.get(0) != null && mData.size() > 0 && !TextUtils.isEmpty(mData.get(0).latitude + "") && !TextUtils.isEmpty(mData.get(0).latitude + "")) {
                        //设置中心点
                        setCenPoint(mData.get(0).latitude, mData.get(0).longitude);
                    }
                } else {//查询返回数据为空
                    Log.e("eeeee", "网店号查询经纬度数据为空");
                }
                break;
            case NetAPI.getShopLocation://获取单个网点的数据
                ShopLatLonAloneRespon latLonAloneRespon = (ShopLatLonAloneRespon) obj;
                if (latLonAloneRespon != null && latLonAloneRespon.getContent() != null ) {
                    if (mData == null) {
                        mData = new ArrayList<MyShop>();
                    } else if (mData.size() != 0) {
                        mData.clear();
                    }
                        for (int j = 0; j < merShopInfoList.size(); j++) {
                            if ((merShopInfoList.get(j).getShopNo()).equals(latLonAloneRespon.getContent().getShopId())) {
                                MyShop shop = new MyShop();
                                shop.latitude = Double.parseDouble(latLonAloneRespon.getContent().getGeo().getCoordinates().get(1));
                                shop.longitude = Double.parseDouble(latLonAloneRespon.getContent().getGeo().getCoordinates().get(0));
                                shop.name = merShopInfoList.get(j).getMerchantName();
                                shop.object = merShopInfoList.get(j);
                                final String todayTaskFlag = merShopInfoList.get(j).getTodayTaskFlag();
                                mData.add(shop);
                            }
                    }
                    initMarker();
                    if (flagCenter && mData != null && mData.get(0) != null && mData.size() > 0 && !TextUtils.isEmpty(mData.get(0).latitude + "") && !TextUtils.isEmpty(mData.get(0).latitude + "")) {
                        //设置中心点
                        setCenPoint(mData.get(0).latitude, mData.get(0).longitude);
                    }
                } else {//查询返回数据为空
                    Log.e("aaaaa", "单个网店号查询经纬度数据为空");
                }
                break;
            case NetAPI.ACTION_ADD_DOT_AND_ORDER://加入拜访
                mMerShopInfo.setTodayTaskFlag("1");
                setAddButtonClickable(mMerShopInfo);
                DotAndorderResp dotAndorderResp = (DotAndorderResp) obj;
                Toast.makeText(this, dotAndorderResp.getMessage(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_MER_PAGE_LIST:
                Utils.showToast(this, "获取失败：" + statusCode + "!");
                break;
            case NetAPI.ACTION_ADD_DOT_AND_ORDER://加入拜访
                Toast.makeText(this, statusCode, Toast.LENGTH_SHORT).show();
                break;
            case NetAPI.getShopLocationList://根据网店号查询经纬度
                Toast.makeText(this, statusCode, Toast.LENGTH_SHORT).show();
                break;
            case NetAPI.getShopLocation://根据单个网店号查询经纬度
                Toast.makeText(this, statusCode, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    List<MerShopInfo> merShopInfoList = new ArrayList<>();

    private void initMapViewMarkers() {
        boolean flag = false;
        // TODO 进行网点展示
        Log.i("g--", "网点列表返回的个数:" + mPageModel.getRows().size());
        List<MerShopInfo> listData = mPageModel.getRows();

        if (mData == null) {
            mData = new ArrayList<MyShop>();
        } else if (mData.size() != 0) {
            mData.clear();
        }
        if (merShopInfoList.size() > 0) {
            merShopInfoList.clear();
        }
        for (MerShopInfo storeInfo : listData) {
            MyShop shop = new MyShop();
            if (storeInfo.getLatitude() != null) {
                shop.latitude = storeInfo.getLatitude();
            } else {
                String dv = storeInfo.getVisitLatitude();
                if (!TextUtils.isEmpty(dv)) {
                    shop.latitude = Double.valueOf(dv);
                } else {
                    flag = true;
//                    shop.latitude = 31.2387130022d;
                }
            }
            if (storeInfo.getLongitude() != null) {
                shop.longitude = storeInfo.getLongitude();
            } else {
//                shop.longitude = storeInfo.getLongitude();
                String dv = storeInfo.getVisitLongitude();
                if (!TextUtils.isEmpty(dv)) {
                    shop.longitude = Double.valueOf(dv);
                } else {
                    flag = true;
//                    shop.longitude = 121.3965510000d;
                }
            }

            shop.name = storeInfo.getMerchantName();
            shop.object = storeInfo;
            final String todayTaskFlag = storeInfo.getTodayTaskFlag();
            if (
//                    !TextUtils.isEmpty(todayTaskFlag) && todayTaskFlag.equals("0") &&
                    !flag) {
                mData.add(shop);
            } else {
//                if (merShopInfoList.size() > 0) {
//                    merShopInfoList.clear();
//                }
                merShopInfoList.add(storeInfo);//没有经纬度的网点数据
            }
        }
        flag = false;
        initMarker();
    }

//    private void initMapViewMarkers() {
//        // TODO 进行网点展示
//        Log.i("g--", "网点列表返回的个数:" + mPageModel.getRows().size());
//        List<MerShopInfo> list = mPageModel.getRows();
//        if (mData == null) {
//            mData = new ArrayList<MyShop>();
//        } else if (mData.size() != 0) {
//            mData.clear();
//        }
//        for (MerShopInfo storeInfo : list) {
//            MyShop shop = new MyShop();
//            if (storeInfo.getLatitude() != null) {
//                shop.latitude = storeInfo.getLatitude();
//            } else {
//                String dv = storeInfo.getVisitLatitude();
//                if (!TextUtils.isEmpty(dv)) {
//                    shop.latitude = Double.valueOf(dv);
//                } else {
//                    shop.latitude = 31.2387130022d;
//                }
//            }
//            if (storeInfo.getLongitude() != null) {
//                shop.longitude = storeInfo.getLongitude();
//            } else {
////                shop.longitude = storeInfo.getLongitude();
//                String dv = storeInfo.getVisitLongitude();
//                if (!TextUtils.isEmpty(dv)) {
//                    shop.longitude = Double.valueOf(dv);
//                } else {
//                    shop.longitude = 121.3965510000d;
//                }
//            }
//
//            shop.name = storeInfo.getMerchantName();
//            shop.object = storeInfo;
//            final String todayTaskFlag = storeInfo.getTodayTaskFlag();
//            if (!TextUtils.isEmpty(todayTaskFlag) && todayTaskFlag.equals("0")) {
//                mData.add(shop);
//            }
//        }
//        initMarker();
//    }

    private ProgressDialog mProgressDialog;

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        } else if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    List<String> list = new ArrayList<>();

    public void getShopLatLonRequ() {
        ShopLatLonReque reque = new ShopLatLonReque();
        reque.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        reque.setShopIds(list);
        NetAPI.getShopLatLonRequest(this, this, reque);
        if (list != null) {
            list.clear();
        }
    }

/*
 * 获取单个网点的经纬度
 *
 **/
    public void getShopLatLonAlonRequ() {
        if(list != null&&list.size()>0){
        ShopLatLonAloneReque requeAlone = new ShopLatLonAloneReque();
        requeAlone.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        requeAlone.setShopId(list.get(0));
        NetAPI.getShopLatLonAloneRequest(this, this, requeAlone);
        if (list != null) {
            list.clear();
        }
        }
    }


    //如果有网店的经纬度数据为空，重新请求数据
    public void getLatLon() {
        if (mPageModel != null && mPageModel.getRows() != null && mPageModel.getRows().size() > 0) {
            for (int i = 0; i < mPageModel.getRows().size(); i++) {
                if (mPageModel.getRows().get(i).getLatitude() == null || mPageModel.getRows().get(i).getLongitude() == null) {
                    list.add(mPageModel.getRows().get(i).getShopNo());
                }
            }
            if(list.size()==1){
                getShopLatLonAlonRequ();
            }else{
                getShopLatLonRequ();
            }
        }
    }


    /*
    * 设置中心点
    * */
    public void setCenPoint(double lat, double lon) {
        LatLng cenpt = new LatLng(lat, lon);
        MapStatus mapStatus = new MapStatus.Builder().target(cenpt).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }


    @Override
    public void onRefreshAutoComplete(String text) {
        Toast.makeText(this, "开始搜索：" + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearch(String text) {
        Toast.makeText(this, "开始搜索：" + text, Toast.LENGTH_SHORT).show();
        pageReqInfo.setSearchCode(text);
        mPageNo = 1;
        toRequestStoreList();
        flagCenter = true;
    }

    @Override
    public void cancelSearch() {
        mPageNo = 1;
        toRequestStoreList();
    }
}

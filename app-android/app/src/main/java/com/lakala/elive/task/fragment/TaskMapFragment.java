package com.lakala.elive.task.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.DeleteTaskListReq;
import com.lakala.elive.beans.DeleteTaskListResp;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.map.activity.MyShop;
import com.lakala.elive.map.common.ImageUtil;
import com.lakala.elive.map.service.LocationService;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.activity.TaskDetailActivity;
import com.lakala.elive.task.activity.TaskListActivity;
import com.lakala.elive.task.common.InitMapLocationMerker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaofeng on 2017/3/20.
 * 任务列表的地图展示
 */
public class TaskMapFragment extends Fragment implements View.OnClickListener,ApiRequestListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_map_show, null);
    }

    private MapView mMapView;
    private TextView mTvShow;
    private RecyclerView mRecyclerView;
    private TextView mTvEmptyView;
    private int mWindth = 0;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView = (MapView) getView().findViewById(R.id.map_task_list_map);

        mTvShow = (TextView) getView().findViewById(R.id.tv_task_map_bottom_msg);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_task_map_bottom_list);
        mTvEmptyView = (TextView) getView().findViewById(R.id.tv_task_map_recyclerview_emptyview);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWindth = displayMetrics.widthPixels;

        initShowNum();
        if(mTaskListData!=null && mTaskListData.size() != 0){
            mTvEmptyView.setVisibility(View.GONE);
        } else {
            mTvEmptyView.setVisibility(View.VISIBLE);
        }
        initView();
    }

    private List<TaskListReqResp.ContentBean> mTaskListData = null;

    public void notifyDataChanged(){
        if(mRecyclerView != null&&mRecyclerView.getAdapter()!=null)
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
    public void setShowDatas(List<TaskListReqResp.ContentBean> list){
        mTaskListData = list;

        if(list == null ){
            /*for(TaskListReqResp.ContentBean bean:list){
                mTaskListData.add(new FraItemBean());
            }*/
            mTaskListData = new ArrayList<TaskListReqResp.ContentBean>();
        }

        if(mTaskListData.size() != 0){
            if(mTvEmptyView !=null)
                mTvEmptyView.setVisibility(View.GONE);
        } else {
            if(mTvEmptyView !=null)
                mTvEmptyView.setVisibility(View.VISIBLE);
        }
        initShowNum();
        if(mRecyclerView != null)
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }

    private void initMapMerker(){
        if(mTaskListData == null || mTaskListData.size() == 0)return;
        List<MyShop> list = new ArrayList<MyShop>();
        int c = 0;
        for(TaskListReqResp.ContentBean contentBean:mTaskListData){
            MyShop myShop = new MyShop();
            myShop.index = c;
            myShop.isShow = true;
            myShop.latitude = 0d;
            myShop.longitude = 0d;
            String shopName = contentBean.getShopName();
            if(TextUtils.isEmpty(shopName)){
                shopName = contentBean.getCustName();
            }
            myShop.name = shopName;
            myShop.object = contentBean;
            myShop.shopNo = contentBean.getShopNo();
            myShop.taskId = contentBean.getTaskId();
            myShop.style = 0;
            list.add(myShop);
            c++;
        }
        boolean bl = InitMapLocationMerker.getInstance().setToShowMerker(list);
        if(!bl)
        Toast.makeText(getActivity(),"数据问题，无法请求经纬度",Toast.LENGTH_SHORT).show();
    }

    private LocationService mLocationService;
    private void initShowNum(){
        if(mTvShow != null&&mTaskListData!=null)
        mTvShow.setText("已添加：" + mTaskListData.size() + "个网点");
    }
    private void initView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHodler>() {
            @Override
            public MyViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHodler(LayoutInflater.from(getActivity()).inflate(R.layout.item_task_map_bottom_view, parent, false));
            }
            @Override
            public void onBindViewHolder(MyViewHodler holder, int position) {
                // TODO 初始化显示
                holder.tvRemove.setTag(position);
                holder.tvSetBegin.setTag(position);
                holder.linearLayout.setTag(position);

                holder.tvDone.setVisibility(View.GONE);

                final TaskListReqResp.ContentBean contentBean = mTaskListData.get(position);
                String shopNo = contentBean.getShopNo();

                if(TextUtils.isEmpty(shopNo)){
                    shopNo = "";
                    holder.tvName.setText(checkString(contentBean.getCustName()));
                } else {
                    shopNo = " (" + shopNo + ")";
                    holder.tvName.setText(checkString(contentBean.getShopName()) + shopNo);
                }

                holder.tvAddress.setText(checkString(contentBean.getShopAddr()));

                // TODO 起点标识，与设为起点互斥.该状态显示为已经被设为起点
                if(contentBean.planOrder == 0){
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    holder.tvSetBegin.setVisibility(View.INVISIBLE);
                } else {
                    holder.tvStatus.setVisibility(View.GONE);
                    holder.tvSetBegin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public int getItemCount() {
                if(mTaskListData==null)return 0;
                return mTaskListData.size();
            }
        });

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private Paint paint = new Paint();
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                final int left = parent.getPaddingLeft();
                final int right = parent.getWidth() - parent.getPaddingRight();
                final int count = parent.getChildCount();
                paint.setColor(ContextCompat.getColor(getActivity(),R.color.gray_4));
                for (int i = 0 ;i < count;i++){
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int top = child.getTop()+params.bottomMargin;
                    final int bottom = top + 1;
                    c.drawLine(left,top,right,top,paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0,0,0,1);
            }
        });

        initMapView();
    }
    private String checkString(String string){
        if(TextUtils.isEmpty(string))return "";
        return string;
    }

    private void initLocation() {
        MyLocationListenner myListener = new MyLocationListenner();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        LocationClient mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);

        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * MyViewHolder
     */
    class MyViewHodler extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView tvName;
        TextView tvStatus;
        TextView tvAddress;
        TextView tvSetBegin;
        TextView tvRemove;
        /**
         * 已完成
         */
        TextView tvDone;

        public MyViewHodler(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_item_task_bottom_click);

            tvName = (TextView) itemView.findViewById(R.id.tv_task_map_bottom_item_name);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_task_map_bottom_item_status);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_task_map_bottom_item_address);

            tvSetBegin = (TextView) itemView.findViewById(R.id.tv_item_task_map_bottom_setbegin);
            tvRemove = (TextView) itemView.findViewById(R.id.tv_item_task_map_bottom_remove);

            tvDone = (TextView) itemView.findViewById(R.id.tv_item_task_map_bottom_done);
            final View.OnClickListener listener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int position = Integer.valueOf(v.getTag().toString());
                    final TaskListReqResp.ContentBean contentBean = mTaskListData.get(position);

                    switch (v.getId()){
                        case R.id.tv_item_task_map_bottom_setbegin:
                            // TODO 设为起点. 应该需要请求网络.如果不用，则缓存状态。需要更新另一个界面
                            Toast.makeText(getActivity(),"设为起点",Toast.LENGTH_SHORT).show();
                            setBeginPoint(position);
                            break;
                        case R.id.tv_item_task_map_bottom_remove:
                            // TODO 移除任务.移除成功后需要更新另一个界面
//                            Toast.makeText(getActivity(),"移除任务",Toast.LENGTH_SHORT).show();
                            deleteItem(position,contentBean.getTaskId());
                            break;
                        case R.id.linear_item_task_bottom_click:
                            // TODO 进入详情
//                            Toast.makeText(getActivity(),"进入详情",Toast.LENGTH_SHORT).show();
                            if(contentBean.getOrderType() == 1){
                                // TODO 网点详情
                                MerShopInfo merchantInfo = new MerShopInfo();
                                merchantInfo.setShopNo(contentBean.getShopNo());
//                              MerShopInfo merchantInfo = merInfoList.get(position - 1);
                                UiUtils.startActivityWithExObj(getActivity(), MerDetailActivity.class, Constants.EXTRAS_MER_INFO, merchantInfo);
                            } else if (contentBean.getOrderType() == 2){
                                // TODO 工单详情
                                TaskInfo taskInfo = new TaskInfo();
                                taskInfo.setShopNo(contentBean.getShopNo());
                                taskInfo.setStatus(contentBean.getStatus()+"");
                                taskInfo.setTaskId(contentBean.getOutTaskId());
                                taskInfo.setTaskBizId(contentBean.getTaskBizId());
                              UiUtils.startActivityWithExObj(getActivity(),TaskDetailActivity.class,Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
                            }
                            break;
                    }
                }
            };
            tvSetBegin.setOnClickListener(listener);
            tvRemove.setOnClickListener(listener);
            linearLayout.setOnClickListener(listener);

        }
    }

    private int mBeginIndex = -1;

    public int getmBeginIndex() {
        return mBeginIndex;
    }

    public void setmBeginIndex(int mBeginIndex) {
        this.mBeginIndex = mBeginIndex;
        setBeginPoint(mBeginIndex);
    }

    private void setBeginPoint(final int position){
        mBeginIndex = position;
        for(int i = 0;i < mTaskListData.size();i++){
            if(i == position){
                mTaskListData.get(position).planOrder = 0;
                mRecyclerView.getAdapter().notifyItemChanged(i);
            } else {
                if(mTaskListData.get(i).planOrder == 0)
                    mRecyclerView.getAdapter().notifyItemChanged(i);
                mTaskListData.get(i).planOrder = -1;
            }
        }
    }
    @Override
    public void onClick(View v) {
    }
    /**
     * 界面显示实体
     */
    private class FraItemBean {

    }
    private void showProgressDialog(String string){
        Activity activity = getActivity();
        if(activity == null)return;
        if(activity instanceof TaskListActivity)
            ((TaskListActivity)getActivity()).showProgressDialog(string);
    }
    private void closeProgressDialog() {
        Activity activity = getActivity();
        if(activity == null)return;
        if(activity instanceof TaskListActivity)
            ((TaskListActivity)getActivity()).closeProgressDialog();
    }
    private int mDelPosition = -1;
    private void deleteItem(int i,String taskId) {
        mDelPosition=i;
        showProgressDialog("删除中");
        DeleteTaskListReq deleteTaskListReq = new DeleteTaskListReq();
        deleteTaskListReq.setTaskDateStr("");
//        deleteTaskListReq.setTaskId(pointInfoRecycleAdapter.getData().get(i).getTaskId());
        deleteTaskListReq.setTaskId(taskId);
        NetAPI.deleteTaskList(getActivity(), this, deleteTaskListReq);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_DELETE_TASK_LIST:
                DeleteTaskListResp deleteTaskListResp = (DeleteTaskListResp) obj;
                if(deleteTaskListResp != null)
                Toast.makeText(getActivity(), deleteTaskListResp.getMessage(), Toast.LENGTH_SHORT).show();
                mTaskListData.remove(mDelPosition);
                mRecyclerView.getAdapter().notifyItemRemoved(mDelPosition);
                mRecyclerView.getAdapter().notifyItemRangeChanged(mDelPosition,mTaskListData.size());
                toNotifyOtherFragment();
                break;
            default:
                break;
        }
    }

    private void toNotifyOtherFragment(){
        final Activity activity = getActivity();
        if(activity instanceof TaskListActivity){
            final TaskListActivity taskListActivity = (TaskListActivity) activity;
            if(taskListActivity != null){
                taskListActivity.notifiyDataChanged(0);
            }
        }
    }
    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_DELETE_TASK_LIST:
                Utils.showToast(getActivity(), "删除失败：" + statusCode + "!");
                break;
            default:
                break;
        }
    }

    private BaiduMap mBaiduMap;

    private void initMapView() {
        mBaiduMap = mMapView.getMap();
        // 121.396551,31.238713
        LatLng cenpt = new LatLng(31.2387130022, 121.3965510000);
        MapStatus mapStatus = new MapStatus.Builder().target(cenpt).zoom(15).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        InitMapLocationMerker.getInstance().setBaiduMap(mBaiduMap,getActivity());
        initDataList();
        initMarker();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO 这里进行点击事件.  保存这个marker。可以用于更新和显示
                Toast.makeText(getActivity(), "地图点被点击了！", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // TODO 地图非marker点被点击出发.可以作为隐藏其他
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mLocationService = new LocationService(mBaiduMap,getActivity());
        mLocationService.startLocate();
    }

//    private ArrayList<MyShop> mData;

    private void initDataList() {
/*
        mData = new ArrayList<MyShop>();
        for (int i = 0; i < 3; i++) {
            MyShop shop = new MyShop();
            if (i == 0) {
                // 121.389508,31.239578
                shop.latitude = 31.2395780022;
                shop.longitude = 121.3895080000;
                shop.name = "店铺一";
            } else if (i == 1) {
                //121.402408,31.234761
                shop.latitude = 31.2347610022;
                shop.longitude = 121.4024080000;
                shop.name = "店铺二";
            } else if (i == 2) {
                //121.407726,31.242665
                shop.latitude = 31.2426650022;
                shop.longitude = 121.4077260000;
                shop.name = "店铺三";
            }
            shop.index = i;

            mData.add(shop);
        }
        */
    }

    private void initMarker() {
        /*
        for (Iterator iterator = mData.iterator(); iterator.hasNext(); ) {
            MyShop shop = (MyShop) iterator.next();
            LatLng ll = new LatLng(shop.latitude, shop.longitude);
*//*
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_item_marker_layout, null);

            TextView shopName = (TextView) view.findViewById(R.id.tv_map_item_marker_name);
            shopName.setText(shop.name);
            if(shop.name.equals("店铺二")){
                shopName.setBackgroundResource(R.drawable.map_item_oran_bg);
            }

            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(view));
*//*
            BitmapDescriptor markerView = ImageUtil.getPointBitmapDes(getActivity(),shop);
            Bundle bundle = new Bundle();
            bundle.putSerializable("HOTEL", shop);
            MarkerOptions markerOptions = new MarkerOptions().position(ll)
                    .icon(markerView).zIndex(shop.index).draggable(true).extraInfo(bundle);
            mBaiduMap.addOverlay(markerOptions);
        }
        */
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        initMapMerker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        destoryLoca();
    }
    public void destoryLoca(){
        InitMapLocationMerker.getInstance().desBaiduMap();
        if(mLocationService==null)return;
        mLocationService.stopLocate();
        mLocationService.destoryLocation();
        mLocationService = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}

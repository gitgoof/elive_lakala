package com.lakala.elive.map.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TaskListReq;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.map.adapter.RoutePlanAdapter;
import com.lakala.elive.map.bean.MyMarker;
import com.lakala.elive.map.common.Params;
import com.lakala.elive.map.press.RoutePlanPress;
import com.lakala.elive.map.service.LocationService;
import com.lakala.elive.map.service.MyLocationListenner;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.activity.TaskDetailActivity;
import com.lakala.elive.task.common.InitMapLocationMerker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaofeng on 2017/3/27.
 * 路径规划
 */

public class PathProjectActivity extends Activity implements View.OnClickListener,ApiRequestListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_path_project_layout);
        initWidget();
    }

    private RoutePlanAdapter mRoutePlanAdater;
    private TextView mTvBack;

    private TextView mTvTitleAdd;
    private TextView mTvRefresh;
    private TextView mTvDaohang;

    private RoutePlanPress routePlanPress;

    private MapView mMapView;
    private RecyclerView mRecyclerView;

    private void initWidget() {
        mMapView = (MapView) findViewById(R.id.map_path_projeect_map);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_path_project_bottom_list);
        mTvBack = (TextView) findViewById(R.id.tv_path_project_back);
        mTvBack.setOnClickListener(this);

        mTvTitleAdd = (TextView) findViewById(R.id.tv_path_project_add);
        mTvTitleAdd.setOnClickListener(this);
        mTvRefresh = (TextView) findViewById(R.id.tv_path_project_refresh);
        mTvRefresh.setOnClickListener(this);
        mTvDaohang = (TextView) findViewById(R.id.tv_path_project_daohang);
        mTvDaohang.setOnClickListener(this);
        init();
    }

    private BaiduMap mBaiduMap;
    private ArrayList<MyMarker> myMarkers;
    private LocationService mLocationService;
    private TaskListReqResp mTaskListReqResp;
    protected void init() {
        mBaiduMap = mMapView.getMap();
        mLocationService = new LocationService(mBaiduMap,PathProjectActivity.this);
        mLocationService.startLocate();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        InitMapLocationMerker.getInstance().setBaiduMap(mBaiduMap,this);
        requestTaskList();

        EventBus.getDefault().register(PathProjectActivity.this);
    }

    private synchronized void initPlan(final List<TaskListReqResp.ContentBean> contentBeenList){
        if(contentBeenList==null || contentBeenList.size()==0)return;
        // TODO 从上个界面传过来任务列表
        if (myMarkers != null) {
            myMarkers.clear();
        } else {
            myMarkers = new ArrayList<MyMarker>();
        }
        LatLng ll = mLocationService.myListener.ll;
        if(ll!= null){
            MyMarker myMarker = new MyMarker();
            myMarker.setLat(ll.latitude);
            myMarker.setLng(ll.longitude);
            myMarker.setTitle("我");
            myMarkers.add(myMarker);
        } else {
            mLocationService.myListener.setmReceiverLoca(new MyLocationListenner.ReceiverLoca() {
                @Override
                public void onReceiver(LatLng latLng) {
                    initPlan(contentBeenList);
                }
            });
        }

        for(int i = 0;i < contentBeenList.size();i++){
            TaskListReqResp.ContentBean contentBean = contentBeenList.get(i);
            int status = contentBean.getStatus();
            if(status == 5||status==1 ){
                mRoutePlanAdater.setmCurrentIndex(i);
            }
            String shopNo = contentBean.getShopNo();
            if(TextUtils.isEmpty(shopNo))continue;
            String lont = InitMapLocationMerker.getInstance().getLatLontById(shopNo);
            if(TextUtils.isEmpty(lont))continue;
            String[] longts = lont.split(",");
            if(longts.length != 3)return;
            MyMarker myMarker = new MyMarker();
            myMarker.setLat(Double.valueOf(longts[2]));
            myMarker.setLng(Double.valueOf(longts[1]));
            String shopName = contentBean.getShopName();
            if(TextUtils.isEmpty(shopName)){
                shopName = contentBean.getCustName();
            }
            myMarker.setTitle(shopName);
            myMarkers.add(myMarker);
        }
        if(mRoutePlanAdater== null)return;
        mRoutePlanAdater.notifyDataSetChanged();
        // TODO 导航路径初始化
        routePlanPress = new RoutePlanPress(this);
//        mRoutePlanAdater.setNewData(myMarkers);
        // TODO 因为没有数据，这里不进行下面的流程
        if(myMarkers.size() == 0){
            return;
        }
        // TODO 设置起点，终点，途经点
        routePlanPress.getRoute(mBaiduMap, myMarkers.get(0), myMarkers.get(myMarkers.size() - 1), myMarkers.subList(1, myMarkers.size() - 1));
        // 添加到地图上
        routePlanPress.addMarkerToMap(this, mBaiduMap, myMarkers);
    }

    private void initMapMerker(List<TaskListReqResp.ContentBean> contentBeenList){
        if(contentBeenList == null || contentBeenList.size() == 0)return;
        List<MyShop> list = new ArrayList<MyShop>();

        int c = 0;
        for(TaskListReqResp.ContentBean contentBean:contentBeenList){
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
            Toast.makeText(PathProjectActivity.this,"数据问题，无法请求经纬度",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        InitMapLocationMerker.getInstance().desBaiduMap();
        mMapView.onDestroy();
        mLocationService.stopLocate();
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        if(mRoutePlanAdater!= null)
        mRoutePlanAdater.setmPlanItemOnClickListener(null);

        EventBus.getDefault().unregister(PathProjectActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_path_project_back:
                PathProjectActivity.this.finish();
                break;
            case R.id.tv_path_project_add:
                Toast.makeText(PathProjectActivity.this, "该功能未实现!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_path_project_refresh:
//                Toast.makeText(PathProjectActivity.this, "重新规划!", Toast.LENGTH_SHORT).show();
                if(mTaskListReqResp == null)break;
                if(mTaskListReqResp.getContent() == null || mTaskListReqResp.getContent().size() == 0)break;
                initPlan(mTaskListReqResp.getContent());
                break;
            case R.id.tv_path_project_daohang:// 开始导航
//                Toast.makeText(PathProjectActivity.this, "导航!", Toast.LENGTH_SHORT).show();
                // 这里传入的是显示View的显示位置
                final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
                routePlanPress.showChooseWay(contentView);
                break;
        }
    }

    private void requestTaskList() {
        showProgressDialog("获取数据中");
        // 设置请求参数
        TaskListReq taskListReq = new TaskListReq();
        taskListReq.setAuthToken(Session.get(PathProjectActivity.this).getUserLoginInfo().getAuthToken());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        taskListReq.setTaskDateStr("" + simpleDateFormat.format(new Date()));// 不填默认今天，选择日期
        taskListReq.setUserId(Session.get(PathProjectActivity.this).getUserLoginInfo().getUserId());
        taskListReq.setFilterStatus("1");
        taskListReq.setLimitTaskCnt("1");
        NetAPI.getTaskList(PathProjectActivity.this, this, taskListReq);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                if(obj == null){
                    Utils.showToast(PathProjectActivity.this, "返回为空!");
                    break;
                }
                initShowView((TaskListReqResp) obj);
                break;

        }
    }
    private void initShowView(TaskListReqResp resp){
        mTaskListReqResp = resp;
        // 任务列表，需要经纬度
        final List<TaskListReqResp.ContentBean> contentBeenList = mTaskListReqResp.getContent();
        InitMapLocationMerker.getInstance().setmEndCallback(new InitMapLocationMerker.EndCallback() {
            @Override
            public void callback() {
                // TODO 更新界面
                initPlan(contentBeenList);
            }
        });
        initMapMerker(contentBeenList);
        // TODO 列表显示初始化
        mRoutePlanAdater = new RoutePlanAdapter(R.layout.item_path_project_list_layout,contentBeenList);
        mRecyclerView.setAdapter(mRoutePlanAdater);

        mRoutePlanAdater.setmCurrentIndex(0);

        mRoutePlanAdater.setmPlanItemOnClickListener(new RoutePlanAdapter.PlanItemOnClickListener() {
            @Override
            public void onClick(TaskListReqResp.ContentBean contentBean, int position) {
                if(contentBean == null)return;
                if(contentBean.getOrderType() == 1){
                    // TODO 网点详情
                    MerShopInfo merchantInfo = new MerShopInfo();
                    merchantInfo.setShopNo(contentBean.getShopNo());
                    Intent intent = new Intent(PathProjectActivity.this, MerDetailActivity.class);
                    intent.putExtra(Constants.EXTRAS_MER_INFO, merchantInfo);
                    startActivityForResult(intent,REQUEST_INTENT_FOR_MER_INFO);
                } else if (contentBean.getOrderType() == 2){
                    // TODO 工单详情
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setShopNo(contentBean.getShopNo());
                    taskInfo.setStatus(contentBean.getStatus()+"");
                    taskInfo.setTaskId(contentBean.getOutTaskId());
                    taskInfo.setTaskBizId(contentBean.getTaskBizId());
                    Intent intent = new Intent(PathProjectActivity.this, TaskDetailActivity.class);
                    intent.putExtra(Constants.EXTRAS_TASK_DEAL_INFO, taskInfo);
                    startActivityForResult(intent,REQUEST_INTENT_FOR_TASK_INFO);
                }
            }
        });
    }
    private final int REQUEST_INTENT_FOR_MER_INFO = 0X200;
    private final int REQUEST_INTENT_FOR_TASK_INFO = 0X201;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_INTENT_FOR_MER_INFO:
                if(resultCode == 100&&data!=null){
                    String merId = data.getStringExtra("mer_id");

                }
                break;
            case REQUEST_INTENT_FOR_TASK_INFO:
                if(resultCode == 100&&data!=null){
                    String taskId = data.getStringExtra("task_id");
                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                Utils.showToast(PathProjectActivity.this, "获取失败：" + statusCode + "!");
                break;
        }
    }
    protected ProgressDialog mProgressDialog;
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }
    public void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MesssageEventBus(MessageEvent event){
        if("path_task_for_plan".equals(event.type)){
            String taskId = event.result;
            if(mRoutePlanAdater == null)return;
            // TODO 更新列表显示
            final List<TaskListReqResp.ContentBean> contentBeenList = mRoutePlanAdater.getData();
            // TODO 列表显示初始化
            if(contentBeenList == null||contentBeenList.size() == 0)return;
            for(int i = 0;i < contentBeenList.size();i++){
                TaskListReqResp.ContentBean contentBean = contentBeenList.get(i);
                String task = contentBean.getTaskId();
                if(TextUtils.isEmpty(task))continue;
                if(task.equals(taskId)){
                    contentBean.setStatus(5);
                    mRoutePlanAdater.setmCurrentIndex(i);
                    mRoutePlanAdater.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}

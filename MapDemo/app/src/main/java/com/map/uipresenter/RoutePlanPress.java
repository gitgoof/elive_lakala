package com.map.uipresenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonElement;
import com.map.R;
import com.map.bean.MyMarker;
import com.map.bean.RoutePlan;
import com.map.cusui.SelectPicPopupWindow;
import com.map.uiview.RoutePlanView;
import com.map.util.AppExit;
import com.map.util.EncodedPolyline;
import com.map.util.MapUtil;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;

/**
 * Created by xiaogu on 2017/3/14.
 * 路径规划
 */

public class RoutePlanPress extends BasePress {
    Context context;
    RoutePlanView routePlanView;

    SelectPicPopupWindow menuWindow;

    public RoutePlanPress(Context context) {
        this.context = context;
        this.routePlanView = (RoutePlanView) context;
    }

    /**
     * 规划路线
     *
     * @param myMarkerStart 起点
     * @param myMarkerEnd   终点
     * @param myMarkers     途经点
     */
    public void getRoute(final BaiduMap baiduMap, MyMarker myMarkerStart, MyMarker myMarkerEnd, List<MyMarker> myMarkers) {
        final Map<String, String> map = new HashMap<>();
        map.put("origin", MapUtil.bd09togcj02(myMarkerStart.getLat(), myMarkerStart.getLng()));
        map.put("destination", MapUtil.bd09togcj02(myMarkerEnd.getLat(), myMarkerEnd.getLng()));

        String waypoints = "";
        if (myMarkers != null) {
            for (int i = 0; i < myMarkers.size(); i++) {//百度坐标转高德
                MyMarker myMarker = myMarkers.get(i);
                waypoints = waypoints + MapUtil.bd09togcj02(myMarker.getLat(), myMarker.getLng());
                if (i != myMarkers.size() - 1) waypoints = waypoints + "|";
            }
        }
        map.put("waypoints", waypoints);

        addSubscription(apiService.getRoutePlan(map, true), new Observer<RoutePlan>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("输出结果", "");
            }

            @Override
            public void onNext(RoutePlan routePlan) {
                List<LatLng> latLngs = EncodedPolyline.decodePoly(routePlan.getLine());
                List<LatLng> latLngList = new ArrayList<>();
                for (LatLng latLng : latLngs) {//高德坐标转百度
                    latLngList.add(MapUtil.gcj02tobd09(latLng.latitude, latLng.longitude));
                }
                drawLine(baiduMap, latLngList);
            }
        });
    }

    /**
     * 添加规划后的路线
     *
     * @param baiduMap
     * @param latLngs
     */
    public void drawLine(BaiduMap baiduMap, List<LatLng> latLngs) {
        OverlayOptions polygonOption = new PolylineOptions()
                .points(latLngs)
                .width(5)
                .color(0xFF2E95D4);
        baiduMap.addOverlay(polygonOption);
    }

    /**
     * 显示选择方式窗口
     */
    public void showChooseWay(View view) {
        //这两个用来判断两个客户端是否分别存在
        boolean baidu = false;
        boolean gaode = false;
        int type = 0;//是否存在百度高德客户端 0:两个都有，1:只有百度，2：只有高德，3都没有
        if (AppExit.isInstallByread("com.baidu.BaiduMap")) {//用来判断百度地图是否存在
            baidu = true;
        }
        if (AppExit.isInstallByread("com.autonavi.minimap")) {
            gaode = true;
        }
        if (baidu && gaode) {//两个都有
            type = 0;
        } else if (baidu && !gaode) {//只有百度
            type = 1;
        } else if (!baidu && gaode) {//只有高德
            type = 2;
        } else {//两个都没有,下载界面
            Uri uri = Uri.parse("http://map.baidu.com/zt/client/index/?from=singlemessage");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }

        // 实例化SelectPicPopupWindow
        menuWindow = new SelectPicPopupWindow((Activity) context, "使用百度导航", "使用高德导航", type);
        menuWindow.setClick(new PicChooseWayListener());
        // 显示窗口
        menuWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }


    class PicChooseWayListener implements View.OnClickListener {
        LatLng latLngStart = new LatLng(39.915129, 116.403478);//天安门
        LatLng latLngEnd = new LatLng(39.999001, 116.402436);//鸟巢

        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_baidu://上面的按钮
                    try {
                        String uriBaidu = "intent://map/direction?origin=" + latLngStart.latitude + "," + latLngStart.longitude + "&destination="
                                + latLngEnd.latitude + "," + latLngEnd.longitude
                                + "&mode=driving&src=dituwuyou#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                        Intent intent = null;
                        intent = Intent.getIntent(uriBaidu);
                        context.startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_gaode://下面的按钮
                    String gaoLatlngStart = MapUtil.bd09togcj02(latLngStart.latitude, latLngStart.longitude);
                    String[] starts = gaoLatlngStart.split(",");

                    String gaoLatlngEnd = MapUtil.bd09togcj02(latLngEnd.latitude, latLngEnd.longitude);
                    String[] ends = gaoLatlngEnd.split(",");

                    String uri = "androidamap://route?sourceApplication=softname&slat=" + starts[0] + "&slon=" + starts[1] + "&sname="
                            + "我的位置" + "&dlat=" + ends[0] + "&dlon=" + ends[1] + "&dname=" + "鸟巢" + "&dev=0&m=0&t=1";

                    Intent intent_gaode = new Intent("android.intent.action.VIEW", android.net.Uri.parse(uri));
                    intent_gaode.addCategory("android.intent.category.DEFAULT");
                    intent_gaode.setPackage("com.autonavi.minimap");
                    context.startActivity(intent_gaode);

                    break;
                default:
                    break;
            }
        }
    }

}

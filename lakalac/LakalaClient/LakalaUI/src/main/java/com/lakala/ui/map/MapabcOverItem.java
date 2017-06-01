package com.lakala.ui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.OverlayItem;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.ItemizedOverlay;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.Projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapabcOverItem extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> geoList =new ArrayList<OverlayItem>();
	private Drawable marker;
	private List<PoiItem> poiItems=new ArrayList<PoiItem>();
	private ArrayList<BranchInfo> branchInfos=new ArrayList<BranchInfo>();
	
	private Context context;
	private MapView mapView;
	private boolean isLinkClick=true;
	private MapabcPoiOverlay poiOverlay;

	/**
	 * 
	 * @param marker		标记图标
	 * @param context		上下文	
	 * @param branchInfos	网点信息
	 * @param mapView		地图
	 * @param isLinkClick	是否响应点击
	 */
	public MapabcOverItem(Drawable marker, Context context, ArrayList<BranchInfo> branchInfos, MapView mapView, boolean isLinkClick) {
		super(marker);
		
		this.marker=marker;
		this.context=context;
		this.mapView=mapView;
		this.isLinkClick=isLinkClick;
		this.branchInfos=branchInfos;
		GeoPoint[] geoPoints=new GeoPoint[branchInfos.size()];
		for (int i = 0; i < branchInfos.size(); i++) {
			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			geoPoints[i]=new GeoPoint((int)(branchInfos.get(i).latitude*1E6), (int)(branchInfos.get(i).longitude*1E6));
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			geoList.add(new OverlayItem(geoPoints[i], branchInfos.get(i).branchShortName, branchInfos.get(i).address));
			poiItems.add(new PoiItem(i+"", geoPoints[i], geoList.get(i).getTitle(), geoList.get(i).getSnippet()));
		}
		poiOverlay= new MapabcPoiOverlay(context,marker, poiItems,isLinkClick);

		poiOverlay.addToMap(mapView);
	}

	@Override
	protected OverlayItem createItem(int position) {
		
		return geoList.get(position);

	}

	@Override
	public int size() {
		
		return geoList.size();
	}
	
	@Override
	public boolean onTap(GeoPoint geoPoint, final MapView mapView) {
        mapView.getController().animateTo(geoPoint);
        mapView.invalidate();
		return super.onTap(geoPoint, mapView);
		
	}
	
	private int pos=0;
	@Override
	protected boolean onTap(int pos) {
		setFocus(geoList.get(pos));
		this.pos=pos;
		return true;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapview, boolean shadow) {
		
		Projection projection=mapview.getProjection();
		for (int i = size()-1; i >=0; i--) {
			OverlayItem overlayItem=getItem(i);
			String title=overlayItem.getTitle();
			Point point=projection.toPixels(overlayItem.getPoint(), null);
			
			Paint textPaint=new Paint();
			textPaint.setTextSize(15);
			textPaint.setColor(Color.BLACK);
			canvas.drawText(title, point.x-30, point.y-25, textPaint);
		}
		super.draw(canvas, mapview, shadow);
		boundCenterBottom(marker);
		mapView.invalidate();
		super.draw(canvas, mapview, shadow);
	}



    /**
     * 网点信息类
     */
    public static class BranchInfo implements Serializable {

        /**
         * 店头
         */
        public String titleName="";
        /**
         * 网点名称
         */
        public String branchName="";
        /**
         * 网点短名称
         */
        public String branchShortName="";


        /**	网点编号	*/
        public String branchNO="";

        /**	所在城市	*/
        public String city="";
        /**
         * 网点地址
         */
        public String address="";
        /**
         * 营业时间
         */
        public String openTime="";
        /**
         * 联系电话
         */
        public String phoneNumber="";
        /**
         * 标识建筑
         */
        public String marker="";
        /**
         * 维度
         */
        public double latitude=0;
        /**
         * 经度
         */
        public double longitude=0;

        /**
         * 距离当前位置（或者指定点）的距离
         */
        public double distance=0;

        /**
         * 距离当前位置（或者指定点）的角度方位
         */
        public double diretionDeGrees=0;
    }
}
package com.lakala.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;

import com.lakala.ui.R;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.Projection;

import java.util.LinkedList;

/**
 * 自定义MyLocationOverlay，更改当前位置MyLocation的图标
 *
 */
public class MapabcLocationOverlayProxy extends MyLocationOverlay {
	
	private Location myLocation;
	private Point mMapCoords=new Point();
	protected final Paint mPaint=new Paint();
	protected final Paint mCirclePaint=new Paint();
	private final float GPS_MARKER_CENTER_X;
	private final float GPS_MARKER_CENTER_Y;
	private Bitmap gps_marker=null;
	private LocationListenerProxy mLocationListener = null;
	private LinkedList<Runnable> mRunOnFirstFix=new LinkedList<Runnable>();
	private Context context;
	private static final long mLocationUpdateMinTime = 1000;
	private static final float mLocationUpdateMinDistance = 10.0f;
	
	public MapabcLocationOverlayProxy(Context context, MapView mapview) {
		super(context, mapview);
		this.context=context;
		gps_marker=((BitmapDrawable) context.getResources().getDrawable(R.drawable.marker_gpsvalid)).getBitmap();
		GPS_MARKER_CENTER_X = gps_marker.getWidth()/2 - 0.5f;
		GPS_MARKER_CENTER_Y = gps_marker.getHeight()/2 -0.5f;
		
	}

	@Override
	public boolean runOnFirstFix(Runnable runnable) {
		if (myLocation !=null) {
			new Thread(runnable).start();
			return true;
		}else {
			mRunOnFirstFix.addLast(runnable);
			return false;
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		myLocation=location;
		for (Runnable runnable : mRunOnFirstFix) {
			new Thread(runnable).start();
		}
		mRunOnFirstFix.clear();
		super.onLocationChanged(location);
	}
	
	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapview, Location location,GeoPoint point, long time) {
		
		Projection projection=mapview.getProjection();
		if (myLocation !=null) {
			mMapCoords=projection.toPixels(point, null);
			float radius=projection.metersToEquatorPixels(myLocation.getAccuracy());
			this.mCirclePaint.setAntiAlias(true);
			this.mCirclePaint.setARGB(35, 131, 182, 222);
			this.mCirclePaint.setAlpha(50);
			this.mCirclePaint.setStyle(Style.FILL);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);
			this.mCirclePaint.setARGB(225, 131, 182, 222);
			this.mCirclePaint.setAlpha(150);
			this.mCirclePaint.setStyle(Style.STROKE);
			this.mCirclePaint.setStrokeWidth(3);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);
			canvas.drawBitmap(gps_marker, mMapCoords.x - GPS_MARKER_CENTER_X, mMapCoords.y - GPS_MARKER_CENTER_Y, mPaint);
		}
	}
}

package com.lakala.ui.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.MapAbcException;
import com.mapabc.mapapi.core.OverlayItem;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.RouteMessageHandler;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.poisearch.PoiPagedResult;
import com.mapabc.mapapi.poisearch.PoiSearch;
import com.mapabc.mapapi.poisearch.PoiSearch.SearchBound;
import com.mapabc.mapapi.poisearch.PoiTypeDef;
import com.mapabc.mapapi.route.Route;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapabc地图管理器---实现高德地图的自动定位显示，标注地点数据，搜索地点，路线规划等功能<br>
 * 
 * {@link #enableMyLocation()} 实现地图自我定位须在onResume()时间里调用该方法.<br>
 * {@link #disableMyLocation()} 使用地图自我定位须在onPause()方面里面调用该方法，释放相关地图资源
 * {@link #enableLocation()} 实现自我定位（不使用地图）须在onResume()时间里调用该方法.<br>
 * {@link #disableLocation()} 使用自我定位（不使用地图）须在onPause()方面里面调用该方法，释放相关地图资源
 * 
 * @author Blues
 * @version 1.0
 * @since 2013-12-31
 *
 */
public class LKLMapabcMapManeger implements IMapManager{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private MyLocationOverlay myLocationOverlay;
    private WeakReference<Context> mContext;


	private ILocationCallback iLocationCallback;
	private PoiPagedResult result;
	private Dialog loadingDialog = null;
    private TextView loadingText ;
	private int mode = Route.BusDefault;
	private List<Route> routeResult;
	private RouteOverlay routeOverlay;
    private MapabcPoiOverlay poiOverlay;
	private RouteMessageHandler routeMessageHandler;
	private static LKLMapabcMapManeger instance;
	
	private Geocoder coder;// 逆地理编码
	private LocationManagerProxy locationManager = null;
	private LocationListenerProxy mLocationListener = null;
	private final long mLocationUpdateMinTime = 1000;
	private final float mLocationUpdateMinDistance = 10.0f;
	private List<Address> address;
	private double geoLat = 0;
	private double geoLng = 0;
	/**是否解析地理位置信息		*/
	private boolean isGetAddress = false;
    /**是否自动开启gps		*/
    private boolean isAutoGPS = false;
    /**定位过程中是否显示progress		*/
    private boolean isShowProgress = true;

	private String gpsCity = "";
	private String gpsProvince = "";
	private final String LOCATION_ERROR = "定位失败";

	public static LKLMapabcMapManeger  getInstance() {
		if (instance==null) {
			instance = new LKLMapabcMapManeger();
		}
		return instance;
	}
	
	public ILocationCallback getiLocationCallback() {
		return iLocationCallback;
	}

	/**
	 * 设置定位监听回调接收器，用于定位及地图自我定位的结果回调
	 * @param iLocationCallback
	 */
	public void setiLocationCallback(ILocationCallback iLocationCallback) {
		this.iLocationCallback = iLocationCallback;
	}
	
	/**
	 * 通用定位初始化（不使用地图view的情况下，只用来获取当前地理位置及解析地理位置）
	 * @param context
	 * @param isNeedGetAddress  定位成功获取当前经纬度后是否需要解析地理位置
     * @param isAutoGPS		是否自动开启gps
     * @param isShowProgress 定位过程是否显示progress
	 */
	public void init(Context context,boolean isNeedGetAddress,boolean isAutoGPS,boolean isShowProgress) {
		this.isGetAddress = isNeedGetAddress;
		this.mContext 	  = new WeakReference<Context>(context);
        this.isAutoGPS    = isAutoGPS;
        this.isShowProgress = isShowProgress;
        String mapKey = context.getResources().getString(R.string.lakala_map_key);
		locationManager = LocationManagerProxy.getInstance(mContext.get(), mapKey);
		coder 		    =  new Geocoder(mContext.get(),mapKey);
	}

	@Override
	public void init(Context context, View mapView,boolean isAutoGPS) {
        this.mContext 	  = new WeakReference<Context>(context);
        this.isAutoGPS    = isAutoGPS;
		mMapView = (MapView) mapView;
	    mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
	    mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
	    point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度
	                                                                            // (度 * 1E6)
	    mMapController.setCenter(point); // 设置地图中心点
	    mMapController.setZoom(12); // 设置地图zoom级别

        initProgressDialog();
        showDialog(mContext.get().getString(R.string.ui_progress_dialog_location_message));

	    myLocationOverlay = new MyLocationOverlay(mContext.get(), mMapView);
	    mMapView.getOverlays().add(myLocationOverlay);
	    handler.sendMessage(Message.obtain(handler, MapConstants.LKL_MY_START_LOCATION));
	}

    /**
     * 初始化加载对话框
     */
    private void initProgressDialog(){
        loadingDialog = new Dialog(mContext.get(), R.style.loading_dialog);
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        LinearLayout layout = (LinearLayout) LinearLayout.inflate(mContext.get(), R.layout.ui_progress_dialog_layout, null);
        loadingText = (TextView)layout.findViewById(R.id.progress_dialog_layout_message);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MapConstants.GET_LOCATION_OK:
				disableLocation();
				if (isGetAddress) {
					if (geoLat !=0 && geoLng !=0) {
						getAddressLocal(geoLat,geoLng);
					}
				}
				break;
			case MapConstants.GET_ADDRESS_DONE:
                 dismissDialog();
                if (gpsCity.equals("LOCATION_ERROR")){
                    if (iLocationCallback!=null) iLocationCallback.findLocationFailed();
                }else {
                    if (iLocationCallback!=null) iLocationCallback.findLocationSuccessful(geoLat,geoLng);
                }
				break;
			case MapConstants.LKL_MY_START_LOCATION:// 实现初次定位使定位结果居中显示
				myLocationOverlay.runOnFirstFix(new Runnable() {
					@Override
					public void run() {
						handler.sendMessage(Message.obtain(handler, MapConstants.FIRST_LOCATION));
					}
				});
				break;
			case MapConstants.FIRST_LOCATION:// 获取当前位置ok
				GeoPoint currentPoint = myLocationOverlay.getMyLocation();
				mMapController.animateTo(currentPoint, new Runnable() {
					@Override
					public void run() {
						handler.sendMessage(Message.obtain(handler, MapConstants.ANIMATE_FIRST_LOCATION_DONE));
					}
				});
				break;
			case MapConstants.ANIMATE_FIRST_LOCATION_DONE:// 定位后移动到当前位置成功
                dismissDialog();
                if (iLocationCallback!=null) iLocationCallback.findLocationSuccessful(geoLat,geoLng);
				break;
			case MapConstants.ROUTE_SEARCH_RESULT:// 线路规划
                if(contextNotExist()){
                    return ;
                }
				Route route = routeResult.get(0);
				if (routeOverlay != null) {
					routeOverlay.removeFromMap(mMapView);
				}
				routeOverlay = new RouteOverlay((MapActivity) mContext.get(), route);
				// 获取第一条路径的Overlay
				routeOverlay.registerRouteMessage(routeMessageHandler); // 注册消息处理函数
				routeOverlay.addToMap(mMapView); // 加入到地图
                dismissDialog();
				break;
			case MapConstants.POISEARCH:// 网点搜索
				List<PoiItem> poiItems;
				try {
					poiItems = result.getPage(1);
                    showMapMakers(poiItems);
				} catch (MapAbcException e) {
					e.printStackTrace();
				}
                dismissDialog();
				break;
			case MapConstants.ERROR://网点搜索错误
                dismissDialog();
                clearMapMarkers();
				break;
			case MapConstants.ROUTE_SEARCH_ERROR://线路规划失败
                dismissDialog();
				break;
			}
		};
	};
	
	
	/**
     * 开启定位功能
     * @return  true 开启成功   false 开启失败
     */
	public boolean enableLocation() {
        if(contextNotExist()){
            return false ;
        }
        if (isShowProgress) initProgressDialog();
        if (isAutoGPS) {
            if (!MapUtil.isGpsAvaiable(mContext.get())) {
                MapUtil.autoGps(mContext.get());
            }
        }
		boolean result = true;
        if (MapUtil.isNetworkAvailable(mContext.get())) {
            if (mLocationListener == null) {
                mLocationListener = new LocationListenerProxy(locationManager);
            }
            result = mLocationListener.startListening(mLocationListener2, mLocationUpdateMinTime, mLocationUpdateMinDistance);
            if (result == false) {
                dismissDialog();
                if (iLocationCallback != null) iLocationCallback.findLocationFailed();
            }
        }else{
            dismissDialog();
            if (iLocationCallback!=null) iLocationCallback.netError();

        }
		return result;
	}
	
	 /**
     * 停止定位功能
     */
	public void disableLocation() {
        if (isAutoGPS) {
            if(contextNotExist()){
                return ;
            }
            if (MapUtil.isGpsAvaiable(mContext.get())) {
                MapUtil.autoGps(mContext.get());
            }
        }
		if (mLocationListener != null) {
			mLocationListener.stopListening();
		}
		mLocationListener = null;
	}
	
    private LocationListener mLocationListener2 = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				 geoLat = location.getLatitude();
				 geoLng = location.getLongitude();
				 handler.sendEmptyMessage(MapConstants.GET_LOCATION_OK);
			}
		}
	};
	
	/**
     * 解析地理位置信息
     * @param mlat 维度
     * @param mLon 经度
     */
    public void getAddressLocal(final double mlat, final double mLon) {
		new Thread(new Runnable() {
			public void run() {
				try {
					address = coder.getFromLocation(mlat, mLon, 3);
					if (address != null && address.size() > 0) {
						if (address.get(0).getLocality()==null || address.get(0).getLocality().equals("")) {
							gpsCity=address.get(0).getAdminArea();
                            gpsProvince = address.get(0).getAdminArea();
						}else {
                            gpsProvince = address.get(0).getAdminArea();
                            gpsCity =address.get(0).getLocality();
						}
						gpsCity = gpsCity.replaceAll("市", "")
								         .replaceAll("自治盟", "")
								         .replaceAll("自治州", "")
								         .replaceAll("盟", "");

						handler.sendEmptyMessage(MapConstants.GET_ADDRESS_DONE);
					} else {
						gpsCity = LOCATION_ERROR;
						handler.sendEmptyMessage(MapConstants.GET_ADDRESS_DONE);
					}
				} catch (MapAbcException e) {
					gpsCity = LOCATION_ERROR;
					handler.sendEmptyMessage(MapConstants.GET_ADDRESS_DONE);
				}
			}
		}).start();
	}

    public String getGpsCity() {
        return gpsCity;
    }

    public String getGpsProvince() {
        return gpsProvince;
    }

    @Override
	public void enableMyLocation() {
	 try {
         if (isAutoGPS) {
             if(contextNotExist()){
                 return ;
             }
             if (!MapUtil.isGpsAvaiable(mContext.get())) {
                 MapUtil.autoGps(mContext.get());
             }
         }
	      myLocationOverlay.enableMyLocation();
	      myLocationOverlay.enableCompass();
	   } catch (Exception e) {
	   }
	}

	@Override
	public void disableMyLocation() {
        if(contextNotExist()){
            return ;
        }
        if (isAutoGPS) {
            if (MapUtil.isGpsAvaiable(mContext.get())) {
                MapUtil.autoGps(mContext.get());
            }
       }
	 if (this.myLocationOverlay != null) {
	      this.myLocationOverlay.disableMyLocation();
	      this.myLocationOverlay.disableCompass();
	   }	
	}

	@Override
	public void showMapMakers(ArrayList<MapabcOverItem.BranchInfo> branchInfos) {
        OverItemTS ot = new OverItemTS();
        List<PoiItem> poiItems = ot.getOverItemT(branchInfos);
        showMapMakers(poiItems);
	}

    /**
     * #解决crash#83
     * .标注地点地图
     *  @param poiItems
     */
	public void showMapMakers(List<PoiItem> poiItems) {
        try{
            if (mMapView!=null && mMapView.getOverlays()!=null){
                mMapView.getOverlays().clear();
                mMapView.removeAllViewsInLayout();// 清除先前点击留下的气泡
                mMapView.postInvalidate();
                mMapView.getOverlays().add(myLocationOverlay);
                if (poiItems != null && poiItems.size()>0){
                    if(contextNotExist()){
                        return ;
                    }
                    Drawable marker = mContext.get().getResources().getDrawable(R.drawable.map_mark);
                    poiOverlay = new MapabcPoiOverlay(mContext.get(), marker, poiItems,false); // 将结果的第一页添加到PoiOverlay
                    poiOverlay.addToMap(mMapView); // 将poiOverlay标注在地图上
                    poiOverlay.showPopupWindow(0);
                    mMapView.invalidate();
                }
            }
        } catch (Exception e) {
           //解决crash #1544
           e.printStackTrace();
        }
	}

	public class  OverItemTS  {
		
		private List<OverlayItem> geoList =new ArrayList<OverlayItem>();
		private List<PoiItem> poiItems=new ArrayList<PoiItem>();
		
		public List<PoiItem> getOverItemT(ArrayList<MapabcOverItem.BranchInfo> branchInfos) {
			GeoPoint[] geoPoints=new GeoPoint[branchInfos.size()];
			for (int i = 0; i < branchInfos.size(); i++) {
				// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
				geoPoints[i]=new GeoPoint((int)(branchInfos.get(i).latitude*1E6), (int)(branchInfos.get(i).longitude*1E6));
				// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
				geoList.add(new OverlayItem(geoPoints[i], branchInfos.get(i).titleName, branchInfos.get(i).address));
				poiItems.add(new PoiItem(i+"", geoPoints[i], geoList.get(i).getTitle(), geoList.get(i).getSnippet()));
			}
			return poiItems;
		}
	}	

	@Override
	public void clearMapMarkers() {
        if(null != mMapView){
            mMapView.getOverlays().clear();
            mMapView.removeAllViewsInLayout();// 清除先前点击留下的气泡
            mMapView.postInvalidate();
        }
	}
	
	@Override
	public void findPIOLine(int startPointLat,int startPointLon,int endPointLat,int endPointLon) {
		GeoPoint startPoint = new GeoPoint(startPointLat, startPointLon);
		GeoPoint endPoint = new GeoPoint(endPointLat, endPointLon);
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,endPoint);
        showDialog("搜索线路中...");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
                    if(contextNotExist()){
                        return ;
                    }

                    routeResult = Route.calculateRoute((Activity) mContext.get(), fromAndTo, mode);
					if(loadingDialog.isShowing()){
						if(routeResult!=null||routeResult.size()>0)
							handler.sendMessage(Message.obtain(handler, MapConstants.ROUTE_SEARCH_RESULT));
					}
				} catch (MapAbcException e) {
					handler.sendMessage(Message.obtain(handler, MapConstants.ROUTE_SEARCH_ERROR));
				}catch (Exception e) {
					handler.sendMessage(Message.obtain(handler, MapConstants.ROUTE_SEARCH_ERROR));
				}
			}
		});
		t.start();

	}
	
	@Override
	public void searBranch(String query) {
        showDialog("搜索网点中...");
        final String keywrod = query;
		Thread t = new Thread(new Runnable() {
		      public void run() {
		        try {
                    if(contextNotExist()){
                        return ;
                    }

                    PoiSearch poiSearch = new PoiSearch((Activity) mContext.get(), new PoiSearch.Query(keywrod, PoiTypeDef.Financial, "010")); // 设置搜索字符串，"010为城市区号"
		          // poiSearch.setPoiNumber(15);//自定义每页的搜索个数，最大为20
		          int latHeight = mMapView.getLatitudeSpan();// 地图左右经度跨度
		          int lonHeight = mMapView.getLongitudeSpan();// 地图上下维度跨度
		          GeoPoint currentPoint = mMapView.getMapCenter();
		          GeoPoint souWesPoint =new GeoPoint((long) (currentPoint.getLatitudeE6() - latHeight / 2),
		                  							(long) (currentPoint.getLongitudeE6() - lonHeight / 2));
		          GeoPoint norEasPoint =new GeoPoint((long) (currentPoint.getLatitudeE6() + latHeight / 2),
		                  							 (long) (currentPoint.getLongitudeE6() + lonHeight / 2));
		          poiSearch.setBound(new SearchBound(souWesPoint, norEasPoint));

		          result = poiSearch.searchPOI();
		        } catch (Exception e) {
					result = null;
		        }
		          if (result != null) {
		        	 dismissDialog();
		            handler.sendMessage(Message.obtain(handler, MapConstants.POISEARCH));
		          } else {
		        	dismissDialog();
		            handler.sendMessage(Message.obtain(handler, MapConstants.ERROR));
		          }
		      }
		    });
		    t.start();
	}

    /**
     * 显示 loadingDialog
     * @param loadMessage dialog message
     */
    private void showDialog(String loadMessage){

        if(contextNotExist()){
            return ;
        }

       if(null == loadingDialog){
            initProgressDialog();
       }

       if(loadingDialog.isShowing()){
            dismissDialog();
       }

       loadingText.setText(loadMessage);

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               loadingDialog.show();
           }
       },100);
    }

    /**
     * loadingDialog 消失
     */
    public void dismissDialog(){
        if(contextNotExist()){
            return ;
        }

        if(null != loadingDialog){
         loadingDialog.dismiss();
      }
    }

    /**
     *
     * @return
     */
    private boolean contextNotExist(){
        boolean isResult = false;
        if(null == mContext || mContext.get()==null || ((Activity)(mContext.get())).isFinishing()){
            isResult = true;
        }
        return isResult;
    }


}

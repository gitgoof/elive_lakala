package com.lakala.elive.map.activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.map.common.BaseActivity;

import java.util.List;


public class LocationActivity extends BaseActivity {


    public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
    Button btnLocationCheck = null;
    ImageButton imgBtnLocationCheck = null;
    BDLocation mLocation = null;

	@Override
	public void init() {
        //初始化页面视图
        btnLocationCheck = (Button)findViewById(R.id.btn_location_check);
        imgBtnLocationCheck = (ImageButton)findViewById(R.id.btn_iv_back);
        imgBtnLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前地图的位置信息
                if(mLocation!=null){
                    //在主线程执行代码
                    LocationActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "当前定位地址：\n"
                                    + "经度：" + mLocation.getLatitude() +  "\n"
                                    + "纬度：" + mLocation.getLongitude() + "\n"
                                    + "地址：" + mLocation.getAddrStr();

                            showToast(msg);
                            //数据是使用Intent返回
                            Intent intent = new Intent();
                            //把返回数据存入Intent
                            intent.putExtra(Constants.ADDR_STR,  mLocation.getAddrStr());
                            //设置返回数据
                            LocationActivity.this.setResult(RESULT_OK, intent);
                            //关闭Activity
                            LocationActivity.this.finish();
                        }

                    });

                }else{
                    showToast("定位失败！");
                }
            }
        });


		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener(myListener);    //注册监听函数
		initLocation();
		mBaiduMap.setMyLocationEnabled(true);    // 开启定位图层
		setMyLocationConfigeration(MyLocationConfiguration.LocationMode.COMPASS);
		mLocationClient.start();    // 开始定位

	}


	public class MyLocationListener implements BDLocationListener {
		// 在这个方法里面接收定位结果
		@Override
		public void onReceiveLocation(BDLocation location) {

            //获取到地址就在地图上显示
			if (location != null) {
				MyLocationData.Builder builder = new MyLocationData.Builder();
				builder.accuracy(location.getRadius());		// 设置精度
				builder.direction(location.getDirection());	// 设置方向
				builder.latitude(location.getLatitude());	// 设置纬度
				builder.longitude(location.getLongitude());	// 设置经度
				MyLocationData locationData = builder.build();
				mBaiduMap.setMyLocationData(locationData);	// 把定位数据显示到地图上
			}

			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());


			if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}

			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息

			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
            mLocation = location;
			Log.i("BaiduLocationApiDem", sb.toString());

		}

		@Override
		public void onConnectHotSpotMessage(String s, int i) {

		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_1:	// 罗盘态，显示定位方向圈，保持定位图标在地图中心
				setMyLocationConfigeration(MyLocationConfiguration.LocationMode.COMPASS);
				break;
			case KeyEvent.KEYCODE_2:	// 跟随态，保持定位图标在地图中心
				setMyLocationConfigeration(MyLocationConfiguration.LocationMode.FOLLOWING);
				break;
			case KeyEvent.KEYCODE_3:	// 普通态： 更新定位数据时不对地图做任何操作
				setMyLocationConfigeration(MyLocationConfiguration.LocationMode.NORMAL);
				break;

			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 设置定位图层的配置 */
	private void setMyLocationConfigeration(MyLocationConfiguration.LocationMode mode) {
		boolean enableDirection = true;	// 设置允许显示方向
		BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);	// 自定义定位的图标
		MyLocationConfiguration config = new MyLocationConfiguration(mode, enableDirection, customMarker);
        mBaiduMap.setMyLocationConfigeration(config);
	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();		// 停止定位
		super.onDestroy();
	}


	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span= 5000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}
}

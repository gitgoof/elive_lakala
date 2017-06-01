package com.lakala.ui.map;

/**
 * 地图自我定位回调接口
 * @author Blues
 *
 */
public interface ILocationCallback {

	/**
	 * 定位成功
	 */
	void findLocationSuccessful(double geoLat, double geoLng);

	/**
	 * 定位失败
	 */
	void findLocationFailed();

	
	/**
	 * 发生网络异常
	 */
	void netError();
	
}

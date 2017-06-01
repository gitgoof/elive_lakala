package com.lakala.elive;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.beans.UserLoginInfo;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import static android.content.Context.MODE_PRIVATE;

/**
 * 应用 Session
 * 
 * 包含了应用运行所需的必要信息
 * 
 * @author hongzhiliang
 * 
 */
public class Session extends Observable implements ApiRequestListener {

	private final static String TAG = Session.class.getSimpleName();

	/** 单例模式 */
	private static Session mInstance;

	/** The Application Debug flag */
	private String debugType;

	private Context mContext;

	public boolean isDebug;

	private String screenSize;

	private int osVersion;

	private String userAgent;

	private boolean isLogin;

	private boolean isAutoClearCache;

	private int versionCode;

	private String packageName;

	private String versionName;

	private String appName;

	private String imei;

	private String sim;

	private String macAddress;

	private String model;

	private String buildVersion;

	private String deviceId;

	private int currentVersion;
private SharedPreferences sp;
	private static UserLoginInfo mUserLoginInfo;

    //缓存系统常用数据字典
    private Map<String,Map<String,String>> sysDictMap = new HashMap<>();


    public List<DictDetailInfo> getSysDictMapByKey(String keyCode) {
        List<DictDetailInfo> tmpDictList = new ArrayList<DictDetailInfo>();
        tmpDictList.add(new DictDetailInfo("-1","---请选择---"));
        Map<String,String> visitTypeDict = sysDictMap.get(keyCode);
		if(visitTypeDict!=null && visitTypeDict.size() > 0){
			for(Map.Entry<String,String> entry:visitTypeDict.entrySet()){
				tmpDictList.add(new DictDetailInfo(entry.getKey(),entry.getValue()));
			}
		}
        return tmpDictList;
    }

	public List<DictDetailInfo> getSeltMapByKey(String keyCode) {
		List<DictDetailInfo> tmpDictList = new ArrayList<DictDetailInfo>();
		tmpDictList.add(new DictDetailInfo("-1","不限"));
		Map<String,String> visitTypeDict = sysDictMap.get(keyCode);
		if(visitTypeDict!=null && visitTypeDict.size() > 0){
			for(Map.Entry<String,String> entry:visitTypeDict.entrySet()){
				tmpDictList.add(new DictDetailInfo(entry.getKey(),entry.getValue()));
			}
		}
		return tmpDictList;
	}


    public void updateReportAttention() {
        super.setChanged();
        super.notifyObservers(Constants.REPORT_ATTENTION_UPDATE);
    }

    public Map<String, Map<String, String>> getSysDictMap() {
        return sysDictMap;
    }

    public void setSysDictMap(Map<String, Map<String, String>> sysDictMap) {
        this.sysDictMap = sysDictMap;
    }

    public UserLoginInfo getUserLoginInfo() {
		if(mUserLoginInfo==null){
			mUserLoginInfo=new UserLoginInfo();
		}
		mUserLoginInfo.setAuthToken(sp.getString(Constants.KEY_SP_ELIVE_USER_AUTHOR,"0"));;
        return mUserLoginInfo;
    }

    public void setUserLoginInfo(UserLoginInfo userLoginInfo) {
        mUserLoginInfo = userLoginInfo;
		//保存AuthToken用户信息
		sp.edit().putString(Constants.KEY_SP_ELIVE_USER_AUTHOR, userLoginInfo.getAuthToken()).commit();
    }

    /**
	 * default constructor
	 *
	 * @param context
	 */
	private Session(Context context) {
		synchronized (this) {
			mContext = context;
			osVersion = Build.VERSION.SDK_INT;
			buildVersion = Build.VERSION.RELEASE;
			getApplicationInfo();
			sp = context.getSharedPreferences("ELIVEUSERINFO", MODE_PRIVATE);
//			readSettings();
		}
	}


	/**
	 * 读取相关的配置文件信息
	 *
	 */
	private void readSettings() {

	}

	private void getApplicationInfo() {
		final PackageManager pm = (PackageManager) mContext.getPackageManager();
		try {
			final PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),0);
			versionName = pi.versionName;
			versionCode = pi.versionCode;
            Logger.i("getApplicationInfo", versionName + ":" + versionCode);
            final ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
			appName = String.valueOf(ai.loadLabel(pm));
			packageName = mContext.getPackageName();
			TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telMgr.getDeviceId();
			sim = telMgr.getSimSerialNumber();
			deviceId = telMgr.getDeviceId();
		} catch (NameNotFoundException e) {
			Log.d(TAG, "met some error when get application info");
		}

	}

	public static Session get(Context context) {
		if (mInstance == null) {
			mInstance = new Session(context);
		}
		return mInstance;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}

	public int getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(int osVersion) {
		this.osVersion = osVersion;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public boolean isAutoClearCache() {
		return isAutoClearCache;
	}

	public void setAutoClearCache(boolean isAutoClearCache) {
		this.isAutoClearCache = isAutoClearCache;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}


	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getVersionName() {
		if (TextUtils.isEmpty(versionName)) {
			getApplicationInfo();
		}
		return versionName;
	}

	public int getVersionCode() {
		if (versionCode <= 0) {
			getApplicationInfo();
		}
		return versionCode;
	}

	public String getIMEI() {
		if (TextUtils.isEmpty(imei)) {
			getApplicationInfo();
		}
		return imei;
	}

	public String getPackageName() {
		if (TextUtils.isEmpty(packageName)) {
			getApplicationInfo();
		}
		return packageName;
	}

	public String getSim() {
		if (TextUtils.isEmpty(sim)) {
			getApplicationInfo();
		}
		return sim;
	}

	public String getMac() {
		if (TextUtils.isEmpty(macAddress)) {
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			macAddress = info.getMacAddress();
		}
		return macAddress;
	}


	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			default:
				break;
		}
	}



	@Override
	public void onError(int method, String statusCode) {
		switch (method) {
				default:
				break;
		}
	}

    public void close() {

    }

}

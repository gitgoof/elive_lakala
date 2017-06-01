package com.lakala.shoudan.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.lakala.library.util.LogUtil;

import java.util.Stack;

/**
 * @ClassName: AppTaskManager

 */
public class XAtyTask {
	private static final String TAG = "ActivityTaskManager";
	private static Stack<Activity> activityStack;
	private static XAtyTask instance;

	private XAtyTask() {
	}

	/**
	 * 
	 * @Title: getAppManager @Description: TODO @param @return @return
	 * AppManager @throws
	 */
	public static synchronized XAtyTask getInstance() {
		if (instance == null) {
			instance = new XAtyTask();
		}
		return instance;
	}

	/**
	 * 
	 * @Title: addActivity @Description: TODO @param @param activity @return
	 * void @throws
	 */
	public void addAty(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 
	 * @Title: currentActivity @Description: TODO @param @return @return
	 * Activity @throws
	 */
	public Activity topAty() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 
	 * @Title: finishActivity @Description: TODO @param @return void @throws
	 */
	public void killAty() {
		Activity activity = activityStack.lastElement();
		killAty(activity);
	}

	/**
	 * 
	 * @Title: finishActivity @Description: TODO @param @param activity @return
	 * void @throws
	 */
	public void killAty(Activity activity) {
		try {
			if (activity != null) {

				activityStack.remove(activity);
				activity.finish();
				activity = null;
			}
		} catch (Exception ex) {
			LogUtil.e(TAG, ex.getMessage());
		}
	}

	/**
	 * 
	 * @Title: finishActivity @Description: TODO @param @param cls @return
	 * void @throws
	 */
	public void killAty(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				killAty(activity);
			}
		}
	}

	/**
	 * 
	 * @Title: finishAllActivity @Description: TODO @param @return void @throws
	 */
	public void killAllAty() {
		try {
			for (int i = 0, size = activityStack.size(); i < size; i++) {
				if (null != activityStack.get(i)) {
					activityStack.get(i).finish();
				}
			}
			activityStack.clear();
		} catch (Exception ex) {
			LogUtil.e(TAG, ex.getMessage());
		}

	}

	/**
	 * 
	 * @Title: getCount @Description: TODO @param @return @return
	 * Integer @throws
	 */
	public Integer getCount() {
		int count = 0;
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		count = activityStack.size();
		return count;
	}

	/**
	 * 
	 * @Title: AppExit @Description: TODO @param @param context @return
	 * void @throws
	 */
	public void onExit(Context context) {
		try {
			clearAppData();
			killAllAty();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception ex) {
			LogUtil.e(TAG, ex.getMessage());
		}
	}

	// 清除本地数据
	private void clearAppData() {

	}
}
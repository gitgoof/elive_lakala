package com.lakala.core.launcher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lakala.core.base.LKLActivityDelegate;
import com.lakala.library.util.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by wangchao on 14/11/10.
 */
public class ActivityLauncher implements LKLActivityDelegate {

    public static final String ACTIVITY_TYPE_NATIVE  = "native";
    public static final String ACTIVITY_TYPE_WEBVIEW = "webview";
    public static final String ACTIVITY_TYPE_WEBAPP  = "webapp";

    /** 页面的 action 参数 Key*/
    public static final String INTENT_PARAM_KEY_ACTION = "acAction";
    /** 标面参数 key */
    public static final String INTENT_PARAM_KEY_TITLE= "acTitle";
    /**业务requestCode */
    public static final String INTENT_PARAM_KEY_REQUEST_CODE = "acRequestCode";
    /** URL */
    public static final String INTENT_PARAM_KEY_URL = "acURL";
    /** Activity Key */
    public static final String INTENT_PARAM_KEY_ACTIVITYKEY = "acActivityKey";

    /**要打开的Activity 没有找到，这个错误没有参数*/
    public static final int ERROR_ACTIVITY_NOT_FOUND    = 1;
    /**Pop 操作时的 number 参数不正确，这个错误没有参数*/
    public static final int ERROR_POP_NUMBER_ERROR      = 2;
    /**当前Activity 栈为空不能进能行操作*/
    public static final int ERROR_ACTIVITY_STACK_IS_EMPTY  = 3;
    /**Config key 未找到*/
    public static final int ERROR_CONFIG_KEY_NOT_FOUND  = 4;

    //activities
    private final LinkedList<Activity> activities = new LinkedList<Activity>();
    private final Map<String,Activity> activityMap = new HashMap<>();

    private static final String TAG = "ActivityLauncher";

    //pop 操作时如果弹出的不只是顶层 Activity，则该参数保存的是最后一个要弹出的 Activity
    private Activity lastPopTarget;

    private JSONObject configs;

    public ActivityLauncher(){
        configs = new JSONObject();
    }

    public LinkedList<Activity> getActivities() {
        return activities;
    }

    /**
     * 使用 key 方法启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param key       目标 Activity 对应的 key，key 定义在一个配置文件中。
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean start(String key){
        return start(key,new Intent());
    }

    /**
     * 使用 key 方法启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param key       目标 Activity 对应的 key，key 定义在一个配置文件中。
     * @param intent    Intent 对象
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean start(String key, Intent intent){
        return startForResult(key, intent, -1);
    }

    /**
     * 使用 key 方法启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param key       目标 Activity 对应的 key，key 定义在一个配置文件中。
     * @param intent    Intent 对象。
     * @param requestCode 请求码
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startForResult(String key, Intent intent,int requestCode){
        return startForResult(getTopActivity(), key, intent, requestCode);
    }

    /**
     * 使用 key 方法启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param current   当前的 Activity
     * @param key       目标 Activity 对应的 key，key 定义在一个配置文件中。
     * @param intent    Intent 对象。
     * @param requestCode 请求码
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startForResult(Activity current,String key, Intent intent,int requestCode){
        intent = makeIntentByKey(current,key, intent);

        if (intent == null){
            return false;
        }

        if(!onPreStartActivity(current, key, intent, requestCode)){
            return false;
        }

        return startActivityForResult(current, intent, requestCode);
    }

    /**
     * 使用类名方式 启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param action     目标 Activity 的类名，注意：这个类名不是全名，是类名除 ”Activity“ 的前面部分，
     *                   例如：打开 com.lakala.core.activity.LoginActivity，其中 com.lakala.core
     *                   是包名，这里需要传递 “activity.Login”。
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startWithAction(String action){
        return startWithActionForResult(action, null, -1);
    }

    /**
     * 使用类名方式 启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param action     目标 Activity 的类名，注意：这个类名不是全名，是类名除 ”Activity“ 的前面部分，
     *                   例如：打开 com.lakala.core.activity.LoginActivity，其中 com.lakala.core
     *                   是包名，这里需要传递 “activity.Login”。
     * @param intent     Intent 对象。
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startWithAction(String action, Intent intent){
        return startWithActionForResult(action, intent, -1);
    }

    /**
     * 使用类名方式 启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param action     目标 Activity 的类名，注意：这个类名不是全名，是类名除 ”Activity“ 的前半部分，
     *                   例如：打开 com.lakala.core.activity.LoginActivity，其中 com.lakala.core
     *                   是包名，这里需要传递 “activity.Login”。
     * @param intent     Intent 对象。
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startWithActionForResult(String action, Intent intent,int requestCode){
        return startWithActionForResult(getTopActivity(), action, intent, requestCode);
    }

    /**
     * 使用类名方式 启动一个 Activity，缺省使用最顶层的 Activity 开启新 Activity。
     *
     * @param action     目标 Activity 的类名，注意：这个类名不是全名，是类名除 ”Activity“ 的前半部分，
     *                   例如：打开 com.lakala.core.activity.LoginActivity，其中 com.lakala.core
     *                   是包名，这里需要传递 “activity.Login”。
     * @param intent     Intent 对象。
     *
     * @return 如果启动成功返回 true，如果启动失败则返回 false。
     */
    public boolean startWithActionForResult(Activity current,String action, Intent intent, int requestCode){
        intent = (intent != null ? intent : new Intent());
        setPackgeAndClassByAction(current,action,intent);

        if(!onPreStartActivity(current, action, intent, requestCode)){
            return false;
        }

        return startActivityForResult(current, intent, requestCode);
    }

    /**
     * 设置 Key 配置数据
     * @param config key 配置数据，JSON 格式如下：<br>
     *               {<br>
     *                  "name": "配置文件名称",<br>
     *                  "checkURL": "配置文件更新地址",<br>
     *                  "version": "配置文件版本",<br>
     *                  "config": {<br>
     *                      ...<br>
     *                  }<br>
     *               }<br>
     */
    public void setKeyConfigs(JSONObject config){
        if (config == null){
            return;
        }

        this.configs = config;
    }

    public JSONObject getConfigs() {
        return configs;
    }
    /**
     * 关闭当前最顶层的 Activity，返回状态码为 RESULT_CANCELED
     *
     * @return 如果调用成功返回 true
     */
    public boolean finish(){
        return finish(Activity.RESULT_CANCELED,null);
    }

    /**
     * 关闭当前最顶层的 Activity
     * @param resultCode  返回码
     * @param intent      Intent 对象
     * @return            如果调用成功返回 true。
     */
    public boolean finish(int resultCode,Intent intent){
        return pop(1, resultCode, intent);
    }

    /**
     * 弹出导航栈中的Activity，直到 key 指定的 Activity 为止（key 所指定的 Activity 不弹出）。
     * @param key           Activity 的 Key
     * @param resultCode    返回码
     * @param intent        Intent 对象
     * @return              如果调用成功返回 true。
     */
    public boolean pop(String key,int resultCode,Intent intent){
        Activity activity = getActivityByKey(key);

        if (activity == null){
            return false;
        }

        Activity topActivity = getTopActivity();
        if (topActivity == null){
            onError(ERROR_ACTIVITY_STACK_IS_EMPTY,null);
            return false;
        }

        return pop(activity, resultCode, intent);
    }

    /**
     * 弹出导航栈中的Activity，直到 action 指定的 Activity 为止（action 所指定的 Activity 为最终要显示的 Activity）。
     * @param action        目标 Activity 的类名，注意：这个类名不是全名，是类名除 ”Activity“ 的前面部分，
     *                      例如：打开 com.lakala.core.activity.LoginActivity，其中 com.lakala.core
     *                      是包名，这里需要传递 “activity.Login”。
     *
     * @param resultCode    返回码
     * @param intent        Intent 对象
     * @return              如果调用成功返回 true。
     */
    public boolean popWithAction(String action,int resultCode,Intent intent){
        Activity willShowActivity = getActivityByAction(action);
        return pop(willShowActivity, resultCode, intent);
    }

    /**
     * 从导航栈中弹出指定数量的 Activity，如果 number 参数小于0 或大于当前栈中的 Activity 则调用失败。
     * @param number        要弹出的　Activity 数量
     * @return              如果调用成功返回 true。
     */
    public boolean pop(int number){
        return pop(number, -1, null);
    }

    /**
     * 从导航栈中弹出指定数量的 Activity，如果 number 参数小于0 或大于当前栈中的 Activity 则调用失败。
     * @param number        要弹出的　Activity 数量
     * @param resultCode    返回码
     * @param intent        Intent 对象
     * @return              如果调用成功返回 true。
     */
    public boolean pop(int number,int resultCode,Intent intent){
        int size = activities.size();
        if (number > size || number < 1){
            //number 值不正确
            LogUtil.e(TAG,"pop 视图的数量不正确，不能小于0或大于当前视图数量");
            onError(ERROR_POP_NUMBER_ERROR,null);
            return false;
        }

        //计算最一个要弹出的Activity 的索引
        int lastTarget = size - number;

        return popActivity(activities.get(lastTarget),resultCode,intent);
    }

    public void clearTop(Class clazz){
        clearTop(clazz,-1,null);
    }
    public void clearTop(Class clazz,int resultCode,Intent resultData){
        if(clazz == null){
            return;
        }
        String name = clazz.getName();
        Activity act = activityMap.get(name);
        if(act == null){
            Activity topActivity = getTopActivity();
            int mapSize = activityMap.size();
            if (mapSize > 1) {
                pop(mapSize - 1, resultCode, resultData);
            }
            Intent intent = new Intent(topActivity,clazz);
            startActivityForResult(topActivity, intent, 0);
        }else{
            pop(act,resultCode,resultData);
        }
    }
    public void clearWithoutFinish(){
        if(activities != null){
            activities.clear();
            activities.clear();
        }
        if(activityMap != null){
            activityMap.clear();
        }
    }

    /**
     * 从当前的导航栈中弹出视图
     * @param willShowTarget  要弹出的到的目标视图（弹出完成后将要显示的那个视图）。
     * @param resultCode      结果代码
     * @param intent          Intent 对象
     */
    public boolean pop(Activity willShowTarget,int resultCode,Intent intent){
        int index = activities.indexOf(willShowTarget);

        if (index < 0 || index >= activities.size() - 1){
            //队列中没有此视图或索值不正确
            LogUtil.e(TAG,"pop 操作失败，目标视图没有找到。");
            onError(ERROR_ACTIVITY_NOT_FOUND,null);
            return false;
        }

        Activity lastPopActivity = activities.get(index + 1);
        return popActivity(lastPopActivity,resultCode,intent);
    }

    /**
     * 获取栈顶的 Activitiy，即当前显示的 Activity。如果当前栈为空，则返回 null。
     * @return Activity
     */
    public Activity getTopActivity(){
        return activities.peekLast();
    }

    /**
     * 获取栈底的 Activitiy，即当根 Activity。如果当前栈为空，则返回 null。
     * @return Activity
     */
    public Activity getBottomActivity(){
        return activities.peekFirst();
    }

    /**
     * 处理消息
     *
     * @param code 当前消息的 code
     * @param data 当前消息的 data
     * @return     如果 return false 则不继续执行 launch
     */
    protected void onError(int code, Object data){

    }

    /**
     * 启动 Activity 之前, 生成 Intent 之后调用
     *
     * @param current       当前 Activity
     * @param key           目标 Activity 对应的 key，如果不是用key 方式打开的则该参数为 null
     * @param intent        Intent
     * @param requestCode   RequestCode
     * @return              如果 return false 则不会打开目标 Activity
     */
    protected boolean onPreStartActivity(Activity current, String key, Intent intent, int requestCode){
        return true;
    }

    /**
     * pop 系列方法实行弹出操作前触发此事件
     *
     * @param current   当前 Activity
     * @param target    目标 Activity，最终要显示的下层 Activity。
     * @param resultCode 请求码
     * @param intent    bundle
     * @return          如果 return false 则不会 pop 目标 Activity
     */
    protected boolean onPrePopActivity(Activity current,Activity target, int resultCode,Intent intent){
        return true;
    }

    /**
     * 根据 type 类型获取类名，子类可以重写这个方法以定制自已的 type 或 类名。
     * 当 type 为 native 时 action 参数为Activity类名，（不包括应用包名及 Activity 后缀），
     * 如：com.lakala.app.activity.DemoActivity，这里的 action 只需要传 ".activity.Demo"。
     * 注意前面的 "." 如果不写这个符号则表示你要传完整的包包及类名（不含后缀Activity)。
     *
     * 当 type 为其它值时 action 可为空值。
     *
     * @param context   当前应用上下文，从这个上下文中获取获包名。
     * @param type      Activity 类型
     * @param action    action Activity的类名。
     * @return  如果类型不正确或不能返回类名则返回 null
     */
    protected String getClassNameByType(Context context,String type,String action){
        String packageName = context.getApplicationContext().getPackageName();

        if (ACTIVITY_TYPE_NATIVE.equals(type)){
            if (action.startsWith(".")){
                return packageName + action + "Activity";
            }
            else{
                return action + "Activity";
            }
        }
        else if(ACTIVITY_TYPE_WEBAPP.equals(type)){
            return null;
        }
        else if(ACTIVITY_TYPE_WEBVIEW.equals(action)){
            return null;
        }
        else{
            return null;
        }
    }

    /**
     * 根据 config 配置设置 intent，子类可以重写这个方法定制自已的处理罗辑
     * @param config  配置数据
     * @param intent  将要启动的 intent
     */
    protected void setIntentByConfig(JSONObject config,Intent intent){
        String type = config.optString("type","WebApp");

        if (ACTIVITY_TYPE_NATIVE.equals(type)){
            //处理 native 类型的一些事情
        }
        else if(ACTIVITY_TYPE_WEBAPP.equals(type)){
            //处理 WebApp 类型的一些事情
        }
        else if(ACTIVITY_TYPE_WEBVIEW.equals(type)){
            //处理 WebView 类型的一些事情
        }
    }

    /**
     * 打开一个 Activity
     * @param current   当前 Activity
     * @param intent    Intent 实例
     * @param requestCode  请求码
     * @return 打开成功返回 true
     */
    private boolean startActivityForResult(Activity current, Intent intent,int requestCode) {
        try {
            current.startActivityForResult(intent, requestCode);
        }
        catch (ActivityNotFoundException e){
            //Activty 没有找到，报此异常。
            e.printStackTrace();

            onError(ERROR_ACTIVITY_NOT_FOUND,null);
            return false;
        }

        return true;
    }

    /**
     * 从当前的导航栈中弹出视图
     * @param lastPopTarget 最后一个要弹出的 Activity
     * @param resultCode    结果代码
     * @param intent        Intent 对象
     */
    private boolean popActivity(Activity lastPopTarget,int resultCode,Intent intent){
        Activity topActivity = getTopActivity();
        if (topActivity != null){
            topActivity.setResult(resultCode, intent);
        }
        else{
            onError(ERROR_ACTIVITY_STACK_IS_EMPTY,null);
            return false;
        }

        int index = activities.indexOf(lastPopTarget);
        Activity willShowActivity = null;
        if (index > 0){
            willShowActivity = activities.get(index - 1);
        }

        if (!onPrePopActivity(topActivity,willShowActivity,resultCode,intent)){
            return false;
        }

        if (!topActivity.equals(lastPopTarget)){
            this.lastPopTarget = lastPopTarget;
        }
        else{
            this.lastPopTarget = null;
        }

        topActivity.finish();

        return true;
    }

    private Intent makeIntentByKey(Context context,String key,Intent intent){
        JSONObject config = getConfigByKey(key);

        if (config == null){
            onError(ERROR_CONFIG_KEY_NOT_FOUND,null);
            LogUtil.e("ActivityLauncher",String.format("key '%s' not found",key));
            return null;
        }

        Intent newIntent = new Intent(intent);
        String type = config.optString("type","WebApp");
        String action = config.optString("action",null);
        String title = config.optString("title","");
        String packageName = context.getApplicationContext().getPackageName();
        String className = getClassNameByType(context,type,action);

        if (className == null){
            return newIntent;
        }

        newIntent.putExtra(INTENT_PARAM_KEY_ACTION,action);
        newIntent.putExtra(INTENT_PARAM_KEY_TITLE,title);
        newIntent.putExtra(INTENT_PARAM_KEY_ACTIVITYKEY,key);

        newIntent.setClassName(packageName,className);

        setIntentByConfig(config,newIntent);

        return newIntent;
    }

    /**
     * 解析 Action，action 参数为 Activity 类名，（不包括应用包名及 Activity 后缀），
     * 如：com.lakala.app.activity.DemoActivity，这里的 action 只需要传 activity.Demo。
     * 注意前面的 "." 如果不写这个符号则表示你要传完整的包包及类名（不含后缀Activity)。
     *
     * @param context  Activity 上下文
     * @param action action 的形式必为 ***.*** ...
     * @param intent Intent 对象
     */
    private void setPackgeAndClassByAction(Context context,String action,Intent intent){
        if (null == action || "".equals(action) || null == intent){
            return;
        }

        String packageName = context.getApplicationContext().getPackageName();
        String className = getClassNameByType(context,ACTIVITY_TYPE_NATIVE,action);

        intent.setClassName(packageName,className);
    }

    /**
     * 根据 action 从当前栈中找到对应 activity，如果没有找到就返回 null。
     * @param action  action Activity的类名
     * @return Activity 实例，如果未找到则返回 null。
     *
     * @see #getClassNameByType(Context context,String type,String action)
     */
    private Activity getActivityByAction(String action){
        Activity topActivity = getTopActivity();
        if (topActivity == null){
            return null;
        }

        String className = getClassNameByType(topActivity,ACTIVITY_TYPE_NATIVE,action);

        for (Activity activity : activities){
            if(activity.getClass().getName().equals(className)){
                return activity;
            }
        }

        return null;
    }

    /**
     * 根据 Key 从当前栈中找到对应 activity，如果没有找到就返回 null。
     * @param key  Activity 的 Key
     * @return Activity 实例，如果未找到则返回 null。
     *
     * @see #getClassNameByType(Context context,String type,String action)
     */
    private Activity getActivityByKey(String key){
        JSONObject config = getConfigByKey(key);

        if (config == null || key == null){
            onError(ERROR_CONFIG_KEY_NOT_FOUND,null);
            return null;
        }

        Activity topActivity = getTopActivity();
        if (topActivity == null){
            return null;
        }

        String className = getClassNameByType(topActivity,ACTIVITY_TYPE_NATIVE,config.optString("action"));

        for (Activity activity : activities){
            if(activity.getClass().getName().equals(className) &&
                    activity.getIntent() != null &&
                    key.equals(activity.getIntent().getStringExtra(INTENT_PARAM_KEY_ACTIVITYKEY))){
                return activity;
            }
        }

        return null;
    }

    public JSONObject getConfigByKey(String key){
        JSONObject data = this.configs.optJSONObject("config");

        if (data == null){
            return null;
        }

        return data.optJSONObject(key);
    }
    //---------- App Delegate ----------

    public void finishAll(){

        if(activities.size() >0){
            for(Activity activity : activities){
                if(!activity.isFinishing()){
                    activity.finish();
                }
            }
        }

        clearWithoutFinish();
        
    }

    @Override
    public void onCreate(Activity me, Bundle savedInstanceState) {
        //Activity 创建了，将它添加到 activities 列表中。
        activities.add(me);
        activityMap.put(me.getClass().getName(),me);
    }

    @Override
    public void onStart(Activity me) {
        //当栈顶 Activity Finish 并且还没消毁时，会调用下层 Activity 的 onStart 事件，
        //在这里判断下层的 Activity 是否是最后一个要弹出的 Activity 如果不是则继续 finish 操作，
        //如果是弹出此Activity 后置空 lastPopTarget。
        if (this.lastPopTarget != null){
            if (me.equals(this.lastPopTarget)){
                this.lastPopTarget = null;
            }
            me.finish();
        }
    }

    @Override
    public void onResume(Activity me) {
        //将当前显示的 Activity 放在栈顶
        if (activities.remove(me)){
            activities.add(me);
        }
    }

    @Override
    public void onPause(Activity me) {

    }

    @Override
    public void onStop(Activity me) {

    }

    @Override
    public void onDestroy(Activity me) {
        //Activity 被消毁（finish 或 旋转了屏幕），将它从activities 列表中移除
        activities.remove(me);
        activityMap.remove(me.getClass().getName());
    }
}

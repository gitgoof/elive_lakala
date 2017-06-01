package com.lakala.platform.cordovaplugin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.core.cordova.cordova.CordovaWebView;
import com.lakala.core.cordova.cordova.PluginResult;
import com.lakala.library.util.StringUtil;
import com.lakala.core.cordova.cordova.CordovaActivity;
import com.lakala.platform.R;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.ui.component.NavSubMenu;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件编写流程:
 * 1.在core-plugin.js或platform-plugin.js中编写js端插件代码;（在main.js中调用）
 * 2.在相应层,继承CordovaPlugin并Override execute方法,编写代码
 * 3.在LakalaClient模块中res/xml/config.xml中注册该插件
 * <p/>
 * Created by Vinchaos api on 13-12-28.
 * 核心插件
 */
public class Navigation extends CordovaPlugin implements NavSubMenu.OnSubMenuOptionClickListener, NavSubMenu.OnSubMenuOpenOrCloseListener{
    //获取导航条实例
    public static final String GET_NAVIGATION       = "getNavigation";
    //action
    private static final String ACTION_TITLE        = "setTitle";
    private static final String ACTION_FINISH       = "finish";
    private static final String ACTION_BACK         = "goBack";
    private static final String ACTION_SHOW_BACK    = "showBackButton";
    private static final String ACTION_HIDE_BACK    = "hideBackButton";
    private static final String ACTION_SHOW_ACTION  = "showActionButton";
    private static final String ACTION_HIDE_ACTION  = "hideActionButton";
    private static final String ACTION_SHOW_MENU    = "showMenu";
    private static final String ACTION_HIDE_MENU    = "hideMenu";
    private static final String ACTION_EXIT_APP     = "exitApp";

    private CordovaActivity activity;

    private CallbackContext callbackContext;
    private NavigationBar navigationBar;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity      = (CordovaActivity) this.cordova.getActivity();
        navigationBar = (NavigationBar) this.cordova.handleData(GET_NAVIGATION);

        if (action.equalsIgnoreCase(ACTION_TITLE)) {
            return setTitle(args, callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_FINISH)) {
            return finishActivity(callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_BACK)) {
            return goBack(callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_SHOW_BACK)) {
            return showBackBtn(callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_HIDE_BACK)) {
            return hideBackBtn(callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_SHOW_ACTION)) {
            return showActionBtn(args, callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_HIDE_ACTION)) {
            return hideActionBtn();
        }
        if (action.equalsIgnoreCase(ACTION_SHOW_MENU)) {
            return showSubMenu(args, callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_EXIT_APP)) {
            return exitApp();
        }

        return action.equalsIgnoreCase(ACTION_HIDE_MENU) && hideSubMenu();
    }

    /**
     * 设置title
     */
    private boolean setTitle(JSONArray args, CallbackContext callbackContext) {
        try {
            final String title = args.getString(0);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationBar.setTitle(title);
                }
            });
            callbackContext.success();
            return true;
        } catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            callbackContext.sendPluginResult(pluginResult);
            return false;
        }
    }

    /**
     * 退出应用
     *
     * @return
     */
    private boolean exitApp() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BusinessLauncher.getInstance().pop(BusinessLauncher.getInstance().getActivities().size());
            }
        });
        return true;
    }

    /**
     * finish当前Activity
     */
    private boolean finishActivity(final CallbackContext callbackContext) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                callbackContext.success();
            }
        });

        return true;
    }

    /**
     * goBack
     */
    private boolean goBack(final CallbackContext callbackContext) {
        final CordovaWebView web = this.webView;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag = web.canGoBack();
                if (flag) web.goBack();
                else activity.finish();
                callbackContext.success();
            }
        });


        return true;
    }

    /**
     * 显示backButton
     */
    private boolean showBackBtn(CallbackContext callbackContext) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationBar.setBackBtnVisibility(View.VISIBLE);
            }
        });
        callbackContext.success();
        return true;
    }

    /**
     * 隐藏backButton
     */
    private boolean hideBackBtn(CallbackContext callbackContext) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationBar.setBackBtnVisibility(View.GONE);
            }
        });
        callbackContext.success();
        return true;
    }

    /**
     * 显示ActionButton
     */
    private boolean showActionBtn(JSONArray args, CallbackContext callbackContext) {
        final String action = args.optString(0);
        if (action.contains(".png") || action.contains(".jpg")) {
            return showActionBtn(action, callbackContext);
        }
        if (StringUtil.isNotEmpty(action)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationBar.setActionBtnVisibility(View.VISIBLE);
                    navigationBar.setActionBtnText(action);
                }
            });
            callbackContext.success();
        }
        return true;
    }

    /**
     * 显示ActionButton,图片
     */
    private boolean showActionBtn(String action, CallbackContext callbackContext) {
        try {
            InputStream inputStream = activity.getAssets().open("image/" + action);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            final BitmapDrawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationBar.setActionBtnText("");
                    navigationBar.setActionBtnVisibility(View.VISIBLE);
                    navigationBar.setActionBtnBackground(drawable);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        callbackContext.success();
        return true;
    }

    /**
     * 隐藏ActionButton
     */
    private boolean hideActionBtn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationBar.setActionBtnVisibility(View.GONE);
            }
        });
        return true;
    }

    /**
     * 显示下拉菜单
     *
     * @return
     */
    private boolean showSubMenu(JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        final JSONArray data = args.optJSONArray(0);
        final int index = args.optInt(1);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSubMenu(data, index);
            }
        });
        return true;
    }
    /**
     * 设置navigationBar 下拉菜单
     */
    public void setSubMenu(JSONArray array, int index) {
        List<NavSubMenu.Option> list = new ArrayList<NavSubMenu.Option>();
        for (int i = 0; i < array.length(); i++) {
            list.add(new NavSubMenu.Option(array.optString(i)));
        }
        menuTitles = array;
        navigationBar.setTitle(array.optString(index));
        navigationBar.addSubMenu(NavigationBar.NavigationBarItem.title, list, 0, this, this);
        navigationBar.setTitleCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.jiaoyi_jilu_center_menu_down_icon, 0);
    }

    private JSONArray menuTitles;

    /**
     * 隐藏SubMenu
     */
    private boolean hideSubMenu(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationBar.removeSubMenu();
                navigationBar.setTitleCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        });
        return true;
    }

    /**
     * navigationBar menu
     */
    @Override
    public void onSubMenuOptionClick(int position, NavSubMenu.Option option) {
        if (menuTitles != null){
            String title = menuTitles.optString(position, "");
            if (!TextUtils.isEmpty(title)){
                navigationBar.setTitle(title);
            }
        }
        this.webView.sendJavascript("cordova._native.navigation.menuCallback(" + position + ");");
    }

    @Override
    public void open() {
        navigationBar.setTitleCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.jiaoyi_jilu_center_menu_up_icon, 0);
    }

    @Override
    public void close() {
        navigationBar.setTitleCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.jiaoyi_jilu_center_menu_down_icon, 0);
    }

}

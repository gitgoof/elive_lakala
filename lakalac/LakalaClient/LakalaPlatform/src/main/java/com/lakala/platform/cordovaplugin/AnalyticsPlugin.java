package com.lakala.platform.cordovaplugin;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;

import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * .数据统计插件
 * Created by Blues on 14-3-3.
 */
public class AnalyticsPlugin extends CordovaPlugin{

    private static final String EVENT = "event";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (EVENT.equals(action)){
            return event(args,callbackContext);
        }
        return false;
    }

    /**
     *
     * .数据统计
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean event(JSONArray args,CallbackContext callbackContext){

        String eventId = args.optString(0);
        String label = args.optString(1);
        String acc = args.optString(2);
        String origin = args.optString(3);

        User user = ApplicationEx.getInstance().getUser();
        String userMobile = "";
        if (user!=null) userMobile = user.getLoginName();

        StatisticManager.getInstance().onEvent(eventId,label,acc,origin,userMobile,this.cordova.getActivity());
        callbackContext.success();
        return true;
    }


}

package com.lakala.shoudan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.BuildConfig;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/19.
 */
public class WindowDialog {

    private static WindowDialog windowDialog;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private View view;
    private TextView tv_code,tv_snv,tv_svn1;
    private SharedPreferences defaultSP;

    public static WindowDialog getInstance(){
        if(windowDialog==null){
            windowDialog=new WindowDialog();
        }
        return windowDialog;
    }
    public void initWindowDialog(Context context){
        String type=com.lakala.shoudan.BuildConfig.FLAVOR;
        if(type.equals("生产")){
           return;
        }
        Context wcontext=context.getApplicationContext();
        defaultSP = PreferenceManager.getDefaultSharedPreferences(wcontext);
        windowManager= (WindowManager) wcontext.getSystemService(Context.WINDOW_SERVICE);
        params=new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        view=LayoutInflater.from(wcontext).inflate(R.layout.windows_dialog,null);
        tv_code= (TextView) view.findViewById(R.id.tv_code);
        tv_snv= (TextView) view.findViewById(R.id.tv_svn);
        tv_svn1= (TextView) view.findViewById(R.id.tv_svn1);
        tv_code.setText("Android:"+ com.lakala.shoudan.BuildConfig.VERSION_NAME+"\n"+
                com.lakala.shoudan.BuildConfig.FLAVOR+"\n"+"版本号:"+com.lakala.shoudan.BuildConfig.VERSION_CODE);
        tv_snv.setText("客户端:"+PropertiesUtil.svnCode());
        tv_svn1.setText("服务端:"+defaultSP.getString("svnCode","000"));

        getsvnCode(wcontext);
    }

    public void addViews(){
        if(windowManager!=null){
            windowManager.addView(view,params);
        }
    }
    public void removeViews(){
        if(windowManager!=null){
            windowManager.removeView(view);
        }
    }
    public void setSvnCode(String code){
        defaultSP.edit().putString("svnCode",code).commit();
        tv_svn1.setText("服务端:"+code);
    }

    public void getsvnCode(Context context){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/appServerVersion/SVNVersion", HttpRequest.RequestMethod.GET,false);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        setSvnCode(jsonObject.optString("SVNVerison"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
            }
        }));
        businessRequest.execute();
    }
}

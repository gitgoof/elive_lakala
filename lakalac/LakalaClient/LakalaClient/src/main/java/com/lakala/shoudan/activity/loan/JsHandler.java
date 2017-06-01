package com.lakala.shoudan.activity.loan;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/12/28.
 */
public class JsHandler {
    public static final String TAG = "klzx";
    public static int REQUEST_CODE=123;
    private Context context;

    JsHandler(Context context){
        this.context=context;
    }

    @JavascriptInterface
    public void faceRecongniton(String name,String id){
        JSONObject data=new JSONObject();
        try {
            data.put("",name);
            data.put("",id);
            data.put("",2);
            data.put("",2);
            data.put("","20002");
        }catch (JSONException e){
            e.printStackTrace();
        }
        ((CameraActivity)context).goCamera();
    }
}

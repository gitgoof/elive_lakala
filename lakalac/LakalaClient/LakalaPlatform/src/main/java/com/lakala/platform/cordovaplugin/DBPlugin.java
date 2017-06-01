package com.lakala.platform.cordovaplugin;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.platform.dao.DataBaseDao;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Blues on 14-1-16.
 */
public class DBPlugin extends CordovaPlugin {

    /** 执行增删改命令 */
    private static final String  EXECUTE= "exec";
    /** 执行查询命令*/
    private static final String  QUERY = "select";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (QUERY.equals(action)){//查询操作
            return query(args,callbackContext);
        }

        if (EXECUTE.equals(action)){//增删改

            return  execute(args, callbackContext);
        }
        return false;
    }

    /**
     *     操作据库
     */
    private boolean execute(JSONArray args, CallbackContext callbackContext) throws JSONException{
        String cmd = args.getString(0);

        boolean result = new DataBaseDao().exec(cmd, convertObjectArg2StrArr(args.get(1)));
        callbackContext.success(Boolean.toString(result));
        return  true;
    }

    private String[] convertObjectArg2StrArr(Object object) throws JSONException{

        String[] datas;
        if("".equals(object) || "null".equals(object.toString())){
            datas = new String[0];
        }else{
            JSONArray data = (JSONArray)(object);
            datas = new String[data.length()];
            for(int i=0;i<data.length();i++){
                    datas[i] = data.getString(i);
            }
        }
        return datas;

    }

    /**
     * 查询数据库数据
     * */
    private boolean query(JSONArray args, CallbackContext callbackContext) throws JSONException{
        String cmdString = args.getString(0);
        String[] datas = convertObjectArg2StrArr(args.get(1));
        StringBuilder resultData = new StringBuilder();
        boolean result = new DataBaseDao().query(cmdString, datas,resultData);
        callbackContext.success(new JSONArray(resultData.toString()));
        return  true;
    }

    /**
     * 添加数据到数据库
     */
    private boolean insertData(JSONArray args) throws  JSONException{
        String cmdString = args.getString(0);
        String[] datas = convertObjectArg2StrArr(args.get(1));
        new DataBaseDao().exec(cmdString, datas);
        return  true;
    }


    /**
     * 添加数据到数据库
     */
    private boolean deleteData(JSONArray args) throws  JSONException{
        String cmdString = args.getString(0);
        JSONArray data = (JSONArray)(args.get(1));
        String[] datas = new String[data.length()];
        for(int i=0;i<data.length();i++){
            datas[i] = data.getString(i);
        }
        new DataBaseDao().exec(cmdString, datas);
        return  true;
    }


    /**
     * 更新数据库数据到
     */
    private boolean update(JSONArray args) throws JSONException{
        String cmdString = args.getString(0);
        JSONArray data = (JSONArray)(args.get(1));
        String[] datas = new String[data.length()];
        for(int i=0;i<data.length();i++){
            datas[i] = data.getString(i);
        }
        new DataBaseDao().exec(cmdString, datas);
        return  true;
    }
}

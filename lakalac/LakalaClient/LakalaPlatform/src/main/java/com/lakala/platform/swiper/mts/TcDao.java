package com.lakala.platform.swiper.mts;

import android.content.ContentValues;
import android.database.Cursor;

import com.lakala.core.dao.BaseDao;
import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.http.BusinessRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangchao on 14-8-11.
 * 保存TC,并且异步上传TC
 */
public class TcDao extends BaseDao {

    //表名
    private static final String TABLE_NAME_TC = "tc_db";
    //TC所有字段,Json
    private static final String TC_JSON       = "tc_json";
    //当前日期
    private static final String _DATE         = "_current_date";
    //创建表
    private static final String CREATE_SQL    = "create table if not exists " + TABLE_NAME_TC + "(" +
                                                _DATE + " long," +
                                                TC_JSON + " text)";
    //instance
    private static TcDao instance;

    //tc list
    private List<JSONObject> list;

    //内部构造
    private TcDao(){
        super();
        db.execSQL(CREATE_SQL);
    }

    public static synchronized TcDao getInstance(){
        if(instance == null) {
            instance = new TcDao();
        }
        return instance;
    }

    /**
     * 保存TC
     *
     * @param tc
     */
    public synchronized void storeTc(JSONObject tc){
        if(tc == null) return;

        ContentValues values = new ContentValues();
        values.put(TC_JSON, tc.toString());
        values.put(_DATE, new Date().getTime());
        db.insert(TABLE_NAME_TC, null, values);

        //异步上传
        asyncUploadTc();
    }

    /**
     * 获取TC列表
     *
     * @return
     */
    private List<JSONObject> getTc(){
        List<JSONObject> list = new ArrayList<JSONObject>();

        Cursor cursor = db.query(TABLE_NAME_TC, null, null, null, null, null, _DATE + " desc");
        if (cursor == null) return list;

        while (cursor.moveToNext()) {
            String tcStr = cursor.getString(cursor.getColumnIndex(TC_JSON));
            try {
                JSONObject tcJson = new JSONObject(tcStr);
                list.add(tcJson);
            } catch (JSONException e) {
                LogUtil.print(e);
            }
        }

        cursor.close();

        return list;
    }

    /**
     * 异步上传
     */
    private void asyncUploadTc(){

         list = getTc();
        if(list == null || list.size() == 0) return;

        exec();
    }

    /**
     * 执行请求
     */
    private void exec(){
        final BusinessRequest request = getRequest(list.get(0));
        request.setIHttpRequestEvents(new IHttpRequestEvents(){
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                //modify by lvyonggang,解决crash #129
                if(null != list && list.size() > 0){
                    db.delete(TABLE_NAME_TC, TC_JSON + "= ?", new String[]{list.get(0).toString()});
                    list.remove(0);
                }
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                if(null != list && list.size() >0){
                    list.remove(0);
                }
            }

            @Override
            public void onFinish(HttpRequest request) {
                super.onFinish(request);
                if (null != list && list.size() != 0){
                    exec();
                }
            }
        });
        request.execute();
    }

    /**
     * 获取request
     *
     * @param tc
     * @return
     */
    private BusinessRequest getRequest(JSONObject tc){
        String URL = "asyTransTc.do";

        BusinessRequest businessRequest = BusinessRequest.obtainRequest(null, BusinessRequest.SCHEME_MTS, URL, HttpRequest.RequestMethod.POST, false);

        for (Iterator<String> iterator = tc.keys(); iterator.hasNext(); ) {
            String key = iterator.next();
            businessRequest.getRequestParams().put(key, tc.optString(key));
        }


        return businessRequest;
    }
}

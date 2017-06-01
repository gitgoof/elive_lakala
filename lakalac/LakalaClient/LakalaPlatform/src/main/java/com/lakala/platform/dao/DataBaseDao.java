package com.lakala.platform.dao;

import android.database.Cursor;

import com.lakala.core.dao.BaseDao;
import com.lakala.library.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Blues on 14-1-21.
 */
public class DataBaseDao extends BaseDao {

    /**
     *
     *  执行sql操作
     * @param cmdSql  SQL命令
     * @param args     参数数据
     * @return   true  操作成功 / false 操作失败
     *
     * */
    public  boolean exec(String cmdSql,String[] args){
        LogUtil.print("cmd =" + cmdSql + "args =" + args);
        db.execSQL(cmdSql,args);
        return  true;
    }

    /**
     * 查询数据操作
     * @param cmdSql    SQL命令
     * @param args      参数数据
     * @param result  结果数据json格式
     * @return   true  操作成功 / false 操作失败
     */
    public  boolean query(String cmdSql,String[] args,StringBuilder result) throws JSONException {
        LogUtil.print("cmd =" + cmdSql + "args =" + args);
        Cursor mCursor = db.rawQuery(cmdSql, args);

        int count = mCursor.getCount();
        if(count <= 0){
            mCursor.close();
            return false;
        }

        JSONArray jsonArray  = new JSONArray();

        int columnCount = mCursor.getColumnCount();
        mCursor.moveToFirst();
        do{
            JSONObject jsonObject = new JSONObject();
            for(int i = 0 ; i < columnCount ; i++){
                String name = mCursor.getColumnName(i);
                String value = mCursor.getString(i);
                jsonObject.put(name,value);
            }
            jsonArray.put(jsonObject);

        }while(mCursor.moveToNext());
        result.append(jsonArray.toString());
        mCursor.close();
        return  true;
    }
}

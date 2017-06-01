package com.lakala.shoudan.activity.payment;

import android.content.ContentValues;
import android.database.Cursor;

import com.lakala.core.dao.BaseDao;
import com.lakala.platform.common.ApplicationEx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/2/10.
 */
public class TcDatabase extends BaseDao {

    private static final String TABLE_NAME = "t_tc";

    private static TcDatabase  tcDatabase;

    //创建用户表sql语句
    private final String sql_t_user = "create table if not exists t_tc(" +
            "_id INTEGER primary key," +    //手机号，登录用户名
            "userName text," +                //用户id
            "tc_content text" + ")"; //tc值


    public static synchronized TcDatabase getInstance(){
        if (tcDatabase == null){
            tcDatabase = new TcDatabase();
        }
        return tcDatabase;
    }



    private TcDatabase() {
        super();
        createTable();
    }

    private void createTable(){
        db.execSQL(sql_t_user);
    }


    public synchronized Map<Integer, String> getTcList(String usr){

        Map<Integer, String> tcContents = new HashMap<Integer, String>();
        Cursor cursor = db.query(TABLE_NAME,null,"userName = ?",new String[]{usr},null,null,null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do{
                    tcContents.put(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("tc_content")));
                }while (cursor.moveToNext());

            }

            cursor.close();
            cursor=null;


        }
        return tcContents;

    }

    public synchronized void deleteTcById(int id){
        String idStr = id + "";
        db.delete(TABLE_NAME, "_id = ?", new String[]{idStr});
    }

    public synchronized void saveTc(String tcContent){

        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", ApplicationEx.getInstance().getUser().getLoginName());
        contentValues.put("tc_content", tcContent);
        db.insert(TABLE_NAME,null,contentValues);

    }


}

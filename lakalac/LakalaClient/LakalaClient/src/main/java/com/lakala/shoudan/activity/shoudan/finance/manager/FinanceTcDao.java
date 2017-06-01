package com.lakala.shoudan.activity.shoudan.finance.manager;

import android.content.ContentValues;
import android.database.Cursor;

import com.lakala.core.dao.BaseDao;
import com.lakala.platform.common.ApplicationEx;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by LMQ on 2015/12/4.
 */
public class FinanceTcDao extends BaseDao {
    private static final String FUND_TC_TABLE_CREATE =  "CREATE TABLE IF NOT EXISTS " +
            TableEntity.FUND_TC_TABLE_NAME + " (" +                 //表名
            TableEntity.ID + " INTEGER PRIMARY KEY," +             //字段 ID
            TableEntity.TC_CONTENT + " TEXT" + ")";                   //字段 保存tc相关信息
    private static final String SQL_DELETE_FUND_TC_TABLE =
            "DROP TABLE IF EXISTS " + TableEntity.FUND_TC_TABLE_NAME;
    private static FinanceTcDao instance = null;

    private Object invokeSync = new Object();
    private FinanceTcDao() {
        super(ApplicationEx.getInstance().getSession().getUser().getLoginName());
        db.execSQL(FUND_TC_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        super.onUpgrade(sqLiteDatabase, i, i2);
        sqLiteDatabase.execSQL(SQL_DELETE_FUND_TC_TABLE);
    }
    public void insertFundTcTable(String json){
        synchronized (invokeSync){
            ContentValues values = new ContentValues();
            values.put(TableEntity.TC_CONTENT,json);
            db.insert(TableEntity.FUND_TC_TABLE_NAME, TableEntity.TC_CONTENT, values);
        }
    }
    /**
     * 查询tc表中的全部信息
     * @return
     */
    public Cursor queryFundTcTable(){
        synchronized (invokeSync){
            String sql = "select * from " + TableEntity.FUND_TC_TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            return cursor;
        }
    }
    /**
     * 删除指定 id 的tc content
     * @param id
     */
    public void deleteFundTcContent(String id){
        synchronized (invokeSync){
            String selection = TableEntity.ID + " = ? ";
            String[] selectionArgs = { id};
            db.delete(TableEntity.FUND_TC_TABLE_NAME, selection, selectionArgs);
        }
    }

    public static FinanceTcDao getInstance(){
        if(instance == null){
            instance = new FinanceTcDao();
        }
        return instance;
    }
    public static class TableEntity{
        public static final String FUND_TC_TABLE_NAME = "fund_tc_table";
        /**
         * TC关键数据
         */
        public static final String TC_CONTENT = "tc_content";
        /**
         * Primary Key
         */
        public static final String ID = "_id";
    }
}

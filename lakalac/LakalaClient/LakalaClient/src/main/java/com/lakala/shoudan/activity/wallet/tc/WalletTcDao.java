package com.lakala.shoudan.activity.wallet.tc;

import android.content.ContentValues;
import android.database.Cursor;

import com.lakala.core.dao.BaseDao;
import com.lakala.platform.common.ApplicationEx;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by fengxuan on 2015/12/25.
 */
public class WalletTcDao extends BaseDao{

    private static final String WALLET_TC_TABLE_CREATE =  "CREATE TABLE IF NOT EXISTS " +
            TableEntity.WALLET_TC_TABLE_NAME + " (" +                 //表名
            TableEntity.ID + " INTEGER PRIMARY KEY," +             //字段 ID
            TableEntity.TC_CONTENT + " TEXT" + ")";                   //字段 保存tc相关信息

    private static final String SQL_DELETE_WALLET_TC_TABLE =
            "DROP TABLE IF EXISTS " + TableEntity.WALLET_TC_TABLE_NAME;

    private static WalletTcDao instance = null;

    private WalletTcDao(){
        super(ApplicationEx.getInstance().getSession().getUser().getLoginName());
        db.execSQL(WALLET_TC_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        super.onUpgrade(sqLiteDatabase, i, i2);
        sqLiteDatabase.execSQL(SQL_DELETE_WALLET_TC_TABLE);
    }

    private Object invokeSync = new Object();

    public void insertFundTcTable(String json){
        synchronized (invokeSync){
            ContentValues values = new ContentValues();
            values.put(TableEntity.WALLET_TC_TABLE_NAME,json);
            db.insert(TableEntity.WALLET_TC_TABLE_NAME, TableEntity.TC_CONTENT, values);
        }
    }

    /**
     * 查询tc表中的全部信息
     * @return
     */
    public Cursor queryWalletTcTable(){
        synchronized (invokeSync){
            String sql = "select * from " + TableEntity.WALLET_TC_TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            return cursor;
        }
    }
    /**
     * 删除指定 id 的tc content
     * @param id
     */
    public void deleteWalletTcContent(String id){
        synchronized (invokeSync){
            String selection = TableEntity.ID + " = ? ";
            String[] selectionArgs = { id};
            db.delete(TableEntity.WALLET_TC_TABLE_NAME, selection, selectionArgs);
        }
    }


    public static WalletTcDao getInstance(){
        if (instance == null){
            instance = new WalletTcDao();
        }
        return instance;
    }

    public static class TableEntity{
        public static final String WALLET_TC_TABLE_NAME = "wallet_tc_table";
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

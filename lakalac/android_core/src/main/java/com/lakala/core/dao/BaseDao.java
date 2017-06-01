package com.lakala.core.dao;

import android.content.Context;

import com.lakala.core.LibApplicationEx;
import com.lakala.library.DBHelper;
import com.lakala.library.LastVersionDBHelper;
import com.lakala.library.OnUpgradeListener;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * 数据操作对象基础类
 * 对数据库的操作，需要继承此类，使用db对象执行sql语句
 * 如果需要更换默认数据库，请重写getDataBaseFile方法
 * Created by xyz on 13-12-20.
 */
public abstract class BaseDao implements OnUpgradeListener{

    protected SQLiteDatabase db;

    protected SQLiteDatabase lastVersionDb;

    private Context context;

    public BaseDao(){
        this(null);
    }
    public BaseDao(String userName){
        this.context = LibApplicationEx.getInstance();
        DBHelper helper = DBHelper.getInstance(userName,context);
        helper.setUpgradeListener(this);
        db = helper.getDatabase();
        lastVersionDb = LastVersionDBHelper.getInstance(context).getDatabase();
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2){

    }
    /**
     * 判断表是否存在
     * 判断未加密的数据库中的表是否存在，不能使用此方法
     * @param tableName 表名
     * @return true: 存在 false: 不存在
     */
    @Deprecated
    public boolean isTableExist(String tableName){
        boolean isExist = false ;
        if(tableName == null || "".equals(tableName)){
            return isExist;
        }
        String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?";
        Cursor cursor = lastVersionDb.rawQuery(sql, new String[] {"table", tableName});

        if (cursor.moveToFirst()){
            int count = cursor.getInt(0);
            isExist = count > 0;
        }
        cursor.close();
        return isExist;
    }

    /**
     * 判断表是否存在
     * @param tableName 表名
     * @return true: 存在 false: 不存在
     */
    public boolean isExist(String tableName){
        boolean isExist = false ;
        if(tableName == null || "".equals(tableName)){
            return isExist;
        }
        String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {"table", tableName});

        if (cursor.moveToFirst()){
            int count = cursor.getInt(0);
            isExist = count > 0;
        }
        cursor.close();
        return isExist;
    }

}

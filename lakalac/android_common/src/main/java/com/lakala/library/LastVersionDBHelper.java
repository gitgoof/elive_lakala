package com.lakala.library;


import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * 数据库操作协助类，使用SQLCipher对数据库加密
 * 这个类用来访问1.0.0没加密的数据库
 * Created by xyz on 13-12-20.
 */
public class LastVersionDBHelper extends SQLiteOpenHelper{

    private static LastVersionDBHelper instance;

    private Context context;

    private static final String DB_FILE_PATH = "lakala.db";

    public static LastVersionDBHelper getInstance(Context context){
        if(instance == null){
            instance = new LastVersionDBHelper(context,DB_FILE_PATH,null,6);
        }

        return instance;
    }

    private LastVersionDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }


    /**
     * 加载数据库lib资源库，必须在任何数据库操作之前执行此方法
     */
    public void loadLibs(){
        SQLiteDatabase.loadLibs(context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public SQLiteDatabase getDatabase(){
        return getWritableDatabase("");
    }

}


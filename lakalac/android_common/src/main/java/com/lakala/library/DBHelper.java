package com.lakala.library;


import android.content.Context;
import android.os.Debug;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lakala.library.encryption.Base64;
import com.lakala.library.encryption.Digest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * 数据库操作协助类，使用SQLCipher对数据库加密
 * Created by xyz on 13-12-20.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static DBHelper instance;
    private static int version = 8;

    private SQLiteDatabase db;

    private Context context;
    private OnUpgradeListener upgradeListener;

    public DBHelper setUpgradeListener(OnUpgradeListener upgradeListener) {
        this.upgradeListener = upgradeListener;
        return this;
    }

    //数据库加密使用的key，测试版本不加密
    private static final String KEY = "lakaladb13572468";
    private String dbKey = "";
    private String userName = "";

    public static DBHelper getInstance(Context context){
        return getInstance(null,context);
    }
    public static DBHelper getInstance(String userName,Context context){
        if(instance == null || !TextUtils.equals(instance.userName,userName)){
            StringBuilder filePath = new StringBuilder();
            filePath.append(TextUtils.isEmpty(userName)?"":userName);
            filePath.append("devices.db");
            instance = new DBHelper(context,filePath.toString(),null,version);
            instance.userName = userName;
        }

        return instance;
    }

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        dbKey = genKey();
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
        if(upgradeListener != null){
            upgradeListener.onUpgrade(sqLiteDatabase, i, i2);
        }
    }

    /*
    为每个手机的Lakala数据库生成唯一的key
     */
    private String genKey() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String ime = manager.getDeviceId();
        if (StringUtil.isEmpty(ime)){
            ime = "lof943kvirtmjujew0fjytn734";
        }
        ime += KEY;
        return Base64.encodeToString(Digest.md5(ime).getBytes(), 0);
    }

    public SQLiteDatabase getDatabase(){
        return getWritableDatabase("");
    }

}


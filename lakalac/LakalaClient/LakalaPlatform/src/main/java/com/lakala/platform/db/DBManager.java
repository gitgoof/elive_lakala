package com.lakala.platform.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huwei on 31/8/16.
 */
public class DBManager {
    public static String db_name = "lakala";
    public static final int version = 1;
    public static DbUtils db_utils;
    public static String db_dir = "";
    private static DBManager dbManger;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
    }

    public static DBManager getIntance(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            dbManger = null;
        } else {
            db_name = "lakala" + "_" + name;
            if (dbManger == null) {
                dbManger = new DBManager(context);
                dbManger.createDataBase(context, db_name);
            }
            String oldDbName = dbManger.getDbName(context);
            if (!TextUtils.isEmpty(oldDbName) && !TextUtils.equals(oldDbName, db_name)) {
                dbManger.createDataBase(context, db_name);
            }
        }
        return dbManger;
    }

    public boolean expireUpdate() {
        return false;
    }

    public boolean saveUserInfo() {
        return false;
    }

    public boolean updateUserInfo() {
        return false;
    }

    public boolean saveCounter() {
        return false;
    }

    public boolean saveSleep() {
        return false;
    }

    public boolean saveTask() {
        return false;
    }

    private String dbName;

    private synchronized void createDataBase(Context context, String db_name) {
        dbManger.saveDbName(context, db_name);
        db_utils = DbUtils.create(context, db_name, version, dbUpgradeListener);
    }

    public synchronized void createTable(List<Class> cList) {
        try {
            for (Class c : cList) {
                db_utils.createTableIfNotExist(c);
            }
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void save(Object entity) {
        try {
            db_utils.save(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveAll(List list) {
        try {
            db_utils.saveAll(list);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void saveOrupdateAll(List list) {
        try {
            db_utils.saveOrUpdateAll(list);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 保存对象关联数据库生成的id
    public synchronized void saveBindingId(Object object) {
        try {
            db_utils.saveBindingId(object);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized List getObject(Class entity) {
        List b = null;
        try {
            b = db_utils.findAll(entity);
            if (b == null)
                return new ArrayList();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return b;
    }

    // 查询所有记录
    public synchronized List getAllObject(Class entity) {
        List b = null;
        try {
            b = db_utils.findAll(entity);
            if (b == null)
                return new ArrayList();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }


    // 查询多条记录
    public synchronized List getAllObject(Selector selection) {
        List b = null;
        try {
            b = db_utils.findAll(selection);
            if (b == null)
                return new ArrayList();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public synchronized List getAllObject(Class entity, Selector selection) {
        List b = null;
        try {
            b = db_utils.findAll(selection);
            if (b == null)
                return new ArrayList();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    // 查询单条记录
    public synchronized Object getObject(Class entity, Selector selector) {
        try {
            return db_utils.findFirst(selector);

        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void update(Object object, WhereBuilder whereBuilder, String... updateColumnNames) {
        try {

            db_utils.update(object, whereBuilder, updateColumnNames);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 删除单条记录
    public synchronized void deleteObject(Object entity) {
        try {
            db_utils.delete(entity);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void deleteObjectById(Class<?> clazz, String msgId) {
        try {
            db_utils.deleteById(clazz, msgId);
//            db_utils.delete(clazz, WhereBuilder.b("id", "=", msgId));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 删除表中所有数据
    public synchronized void clearTable(Class entity) {
        try {
            db_utils.deleteAll(entity);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void clearAllTable(List list) {
        try {
            db_utils.deleteAll(list);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static DbUpgradeListener dbUpgradeListener = new DbUpgradeListener() {

        @Override
        public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {

            }
        }
    };

    public void saveDbName(Context context, String loginName) {
        SharedPreferences shp = context.getSharedPreferences(
                "db_loginName", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString("db_before", loginName);
        editor.commit();
    }

    public String getDbName(Context context) {
        SharedPreferences shp = context.getSharedPreferences(
                "db_loginName", Context.MODE_WORLD_WRITEABLE);
        String db_loginName = shp.getString("db_before", "");
        return db_loginName;
    }
}

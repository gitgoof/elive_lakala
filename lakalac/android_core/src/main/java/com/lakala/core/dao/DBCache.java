package com.lakala.core.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lakala.core.LibApplicationEx;
import com.lakala.core.cache.CacheKey;
import com.lakala.core.cache.ExpireDate;
import com.lakala.core.cache.ICache;
import com.lakala.core.cordova.cordova.LOG;

import net.sqlcipher.Cursor;

/**
 * 数据库缓存类，该类缓存的数据存在本地的 cache 数据库中，该数据库是所有 DBCache 实例共享的，
 * 所以 key 必需为一，否则会出现不可预料的结果。<br>
 * removeExpire 方法将会移出 1 分钟前过期的数所在，延迟 1 分钟删除是为了多线程访问时不会出现同一时间读取、删除的情况。
 * 该类不是线程安全的，如果在多个线程中同时使用需要自已保证数据的同步。
 * @author Michael
 *
 */
public class DBCache extends BaseDao implements ICache<String,String> {
	private static final String CACHE_TABLE       = "cache";
	private static final String FIELD_NAME_KEY    = "key";
	private static final String FIELD_NAME_EXPIRE_DATE = "expire_date";
	private static final String FIELD_NAME_DATA        = "data";
	private static final String FIELD_NAME_VERSION = "version";
	private static final int    FIELD_VERSION = 0; //初始版本从0开始
	
	private static final String[] ALL_FIELD          = {FIELD_NAME_KEY,FIELD_NAME_EXPIRE_DATE,FIELD_NAME_DATA, FIELD_NAME_VERSION};
	private static final String[] KEY_FIELD          = {FIELD_NAME_KEY};
	private static final String[] DATA_FIELD         = {FIELD_NAME_DATA};
	private static final String[] EXPIRE_DATE_FIELD  = {FIELD_NAME_EXPIRE_DATE};
	private static final String[] ABOUT_EXPIRE_FIELD = {FIELD_NAME_EXPIRE_DATE, FIELD_NAME_VERSION};

	//查询指定 key
	private static final String   WHERE_KEY     = String.format("%s=?", FIELD_NAME_KEY);
	//查询1分钟前过期的数据和版本过期数据
	private static final String   WHERE_EXPIRED = String.format(
			"datetime(%s) < datetime('now','-1 minutes') or (%s > 0 and %s < %d)",
			FIELD_NAME_EXPIRE_DATE,
            FIELD_NAME_VERSION,
            FIELD_NAME_VERSION,
            FIELD_VERSION);
	
	//key类别
	private String  category;

	public DBCache(Context context){
		//调用父类构构造方法，如果数据库不存在则创建它。
        super();
		this.category = ""; //设定kye 缺省类别为空串。

        //初始化表,如果表不存在则创建之
        String sql = String.format(
                "create table if not exists %s (%s TEXT PRIMARY KEY,%s Text,%s Text,%s INTEGER)",
                CACHE_TABLE,
                FIELD_NAME_KEY,
                FIELD_NAME_EXPIRE_DATE,
                FIELD_NAME_DATA,
                FIELD_NAME_VERSION);
        this.db.execSQL(sql);
	}



    @Override
    public boolean put(CacheKey key, ExpireDate date, String data) {
        PackageManager packageManager = LibApplicationEx.getInstance().getApplicationContext().getPackageManager();
        int versionCode = 0;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(LibApplicationEx.getInstance().getPackageName(),0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        put(key,date,data,versionCode);
        return true;
    }

    @Override
    public boolean put(CacheKey key, int version, String data) {

        put(key,null,data,version);
        return true;
    }

    @Override
    public boolean put(CacheKey key, ExpireDate date, int version, String data) {
        put(key,date,data,version);
        return true;
    }

    @Override
    public String get(CacheKey key) {
        String data = null;

        //如果数据未过期，则获取数据。
        if (!isExpire(key)){
            String[] whereValue = new String[]{getKey(key)};
            Cursor cursor = this.db.query(CACHE_TABLE, ALL_FIELD, WHERE_KEY, whereValue,null,null,null);

            if (cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(FIELD_NAME_DATA);
                data = cursor.getString(columnIndex);
            }

            cursor.close();
        }

        return data;
    }

    /**
     *
     * @param key         数据的 key
     * @param newVersion  数据的最新版本，传 -1 表示不进行版本比较。
     * @return
     */
    @Override
    public String get(CacheKey key, int newVersion) {
        if (newVersion == -1){
            return get(key);
        }

        return isExpire(key,newVersion)? null: get(key);
    }

    @Override
    public String getExpiredData(CacheKey key) {
        String data = null;
        String[] whereValue = new String[]{getKey(key)};

        //根据 key 查询数据库。
        Cursor cursor = this.db.query(CACHE_TABLE, DATA_FIELD, WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){
            //无论数据是否过期，均返回该数据。
            int columnIndex = cursor.getColumnIndex(FIELD_NAME_DATA);
            data = cursor.getString(columnIndex);
        }

        cursor.close();

        return data;
    }

    @Override
    public ExpireDate getExpireDate(CacheKey key) {
        ExpireDate expireDate = null;
        String[] whereValue = new String[]{getKey(key)};

        //根据 key 查询数据库。
        Cursor cursor = this.db.query(CACHE_TABLE, EXPIRE_DATE_FIELD, WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){
            //获取过期时间
            int columnIndex = cursor.getColumnIndex(FIELD_NAME_EXPIRE_DATE);
            expireDate = ExpireDate.expireWithUTC(cursor.getString(columnIndex));
        }

        cursor.close();

        return expireDate;
    }

    @Override
    public int getVersion(CacheKey key) {
        int retVersion = -1;
        String[] whereValue = new String[]{getKey(key)};

        //根据 key 查询数据库。
        Cursor cursor = this.db.query(CACHE_TABLE,ABOUT_EXPIRE_FIELD , WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){
            //获取过期时间
            int columnIndex = cursor.getColumnIndex(FIELD_NAME_VERSION);
            retVersion = cursor.getInt(columnIndex);
        }

        cursor.close();

        return retVersion;
    }

    @Override
    public void remove(CacheKey key) {
        String[] whereValue = new String[]{getKey(key)};
        this.db.delete(CACHE_TABLE, WHERE_KEY, whereValue);
    }

    @Override
    public void clean() {
        //TODO 待实现
    }

    /*
    @Override
	public void removeExpire() {
		SQLiteDatabase write = getWritableDatabase();

		//删除当前key分类下所有过期的数据
		String where = String.format("(%s) And %s", WHERE_EXPIRED, getKeyCategoryWhere());
		write.delete(CACHE_TABLE, where, null);
		write.close();
	}
     */
    @Override
    public boolean isExpire(CacheKey key) {
        boolean expire = true;


        String[] whereValue = new String[]{getKey(key)};

        Cursor cursor = this.db.query(CACHE_TABLE, ABOUT_EXPIRE_FIELD, WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){
            //获取过期时间
            int columnIndex       = cursor.getColumnIndex(FIELD_NAME_EXPIRE_DATE);
            ExpireDate expireDate = ExpireDate.expireWithUTC(cursor.getString(columnIndex));

            expire = isExpire(expireDate);
        }

        cursor.close();

        return expire;
    }

    /**
     * 对数据进版本比较
     * @param key
     * @param newVersion  数据的最新版本，传 -1 表示不进行版本比较。
     * @return
     */
    @Override
    public boolean isExpire(CacheKey key, int newVersion) {
        if (newVersion == -1){
            return isExpire(key);
        }

        boolean expire = true;

        String[] whereValue = new String[]{getKey(key)};
        Cursor cursor = this.db.query(CACHE_TABLE, ABOUT_EXPIRE_FIELD, WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){

            //获取缓存数据时保存的 App 版本号。
            int columnIndex           = cursor.getColumnIndex(FIELD_NAME_VERSION);
            int appVersion        = cursor.getInt(columnIndex);

            expire = (newVersion > appVersion);
        }

        cursor.close();

        return expire;
    }

    /**
     * 根据版本和时间确定是否过期
     * @param key
     * @param version
     * @return
     */
    public boolean isExpireByDateAndVersion(CacheKey key,int version){
        if (version == -1){
            return isExpire(key);
        }

        boolean expire = true;

        String[] whereValue = new String[]{getKey(key)};
        Cursor cursor = this.db.query(CACHE_TABLE, ABOUT_EXPIRE_FIELD, WHERE_KEY, whereValue,null,null,null);

        if (cursor.moveToFirst()){

            //获取过期时间
            int columnIndex       = cursor.getColumnIndex(FIELD_NAME_EXPIRE_DATE);
            ExpireDate expireDate = ExpireDate.expireWithUTC(cursor.getString(columnIndex));

            //获取缓存数据时保存的 App 版本号。
            columnIndex           = cursor.getColumnIndex(FIELD_NAME_VERSION);
            int appVersion        = cursor.getInt(columnIndex);

            expire = isExpire(expireDate) && version > appVersion;
        }

        cursor.close();

        return expire;
    }

    @Override
    public boolean isExist(CacheKey key) {
        boolean exist = false;


        String[] whereValue = new String[]{getKey(key)};
        Cursor cursor = this.db.query(CACHE_TABLE, KEY_FIELD, WHERE_KEY, whereValue,null,null,null);

        exist = cursor.moveToFirst();

        cursor.close();


        return exist;
    }

//    @Override
//    public Iterator<CacheKey> keyIterator() {
//
//        String where = getKeyCategoryWhere();
//        Cursor cursor = this.db.query(CACHE_TABLE, KEY_FIELD, where, null,null,null,null);
//
//        KeyIterator iterator = new KeyIterator(this,this.db,cursor);
//
//        return iterator;
//    }

    /**
     * 设置 key 类别，些值保留为系统使用，无需设定。
     * @param category key的类别名，类别名最长为 24 个字符，超出部分将被截断。
     */
    public void setCategory(String category){
        if (category == null){
            category = "";
        }
        this.category = category;
    }

    /**
     * 获取 Key 分类名称。
     * @return　key 分类名
     */
    public String getCategory()
    {
        return category;
    }

//    /**
//     * 获取1分钟前过期的 key。
//     * @return 返回过期的数据迭代器
//     */
//    public Iterator<CacheKey> expireDateKeyIterator(){
//        String where = String.format("(%s) And %s", WHERE_EXPIRED, getKeyCategoryWhere());
//        Cursor cursor = this.db.query(CACHE_TABLE, KEY_FIELD, where, null,null,null,null);
//
//
//        KeyIterator iterator = new KeyIterator(this,this.db,cursor);
//
//        return iterator;
//    }

    /**
     * 插入一条记录
     * @param key      CacheKey 对象
     * @param date     过期时间
     * @param data     数据
     * @param version  app 版本
     */
    private void put(CacheKey key, ExpireDate date, String data,int version) {
        String k = getKey(key);
        String whereValue[];

        String expireString = date == null ? "" : date.toString();

        //查询 key 是否存在
        whereValue = new String[]{k};
        Cursor cursor = this.db.query(CACHE_TABLE, KEY_FIELD, WHERE_KEY, whereValue,null,null,null);

        ContentValues values = new ContentValues();

        if (!cursor.moveToFirst()){
            //还没有该key，用inster插入一条新数据。
            values.put(FIELD_NAME_KEY, k);
            values.put(FIELD_NAME_EXPIRE_DATE,expireString);
            values.put(FIELD_NAME_DATA, data);
            values.put(FIELD_NAME_VERSION, version);

            this.db.insert(CACHE_TABLE,FIELD_NAME_DATA,values);
        }
        else{
            //Key 已经存在，更新当前数据。
            values.put(FIELD_NAME_EXPIRE_DATE,expireString);
            values.put(FIELD_NAME_DATA, data);
            values.put(FIELD_NAME_VERSION, version);

            this.db.update(CACHE_TABLE, values, WHERE_KEY, whereValue);
        }

        cursor.close();
    }

    /**
     * 根据过期时间、数据版本号判断数据是否过期。
     * @param expireDate  过期时间
     * @return 过期或日期无效返回true，没过期返回false
     */
    private boolean isExpire(ExpireDate expireDate){
        if (expireDate == null || expireDate.isExpire()){
            //数据已过期或时间格式错误
            return true;
        }
        else{
            return false;
        }
    }

    private String getKey(CacheKey key){
        return String.format("%s.%s", category,key.getCacheKey());
    }

    private String getKey(String key){
        return String.format("%s.%s", category,key);
    }

    /**
     * 获得 key 分类查询字符串.
     * @return  分类字符串
     */
    private String getKeyCategoryWhere(){
        //查询当前分类（使用 setCategory方法设置 key 分类）下的所有的 key。
        return String.format("%s LIKE '%s%%'",FIELD_NAME_KEY,getKey(""));
    }

//    /**
//     * CacheKey 迭代器。
//     * @author Michael
//     */
//    public class KeyIterator implements Iterator<CacheKey>{
//        private Cursor         cursor;
//        private SQLiteDatabase read;
//        private DBCache cache;
//        private CacheKey       key;
//
//        private boolean firstCallNext; //是否是第一次调用 next 方法。
//        private int	keyColumnIndex = 0;
//
//        public KeyIterator(DBCache cache,SQLiteDatabase read,Cursor cursor){
//            this.cursor = cursor;
//            this.read   = read;
//            this.cache  = cache;
//
//            firstCallNext = true;
//
//            if (cursor != null){
//                keyColumnIndex = cursor.getColumnIndex(FIELD_NAME_KEY);
//            }
//        }
//
//        @Override
//        protected void finalize() throws Throwable {
//            if (cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
//
//            if (read != null && read.isOpen()){
//                read.close();
//            }
//
//            cache = null;
//
//            super.finalize();
//        }
//
//        @Override
//        public boolean hasNext() {
//            if (cursor == null) return false;
//
//            if (firstCallNext){
//                return cursor.moveToFirst();
//            }
//            else{
//                boolean isLast = cursor.isLast();
//                if (isLast){
//                    //如果已到了最后一行数据了则关闭游标
//                    cursor.close();
//                    read.close();
//
//                    cursor = null;
//                    read   = null;
//                }
//
//                return !isLast;
//            }
//        }
//
//        @Override
//        public CacheKey next() {
//            boolean hasData = false;
//
//            if (cursor == null){
//                throw new NoSuchElementException();
//            }
//
//            //移动游标到第一条记录。
//            if (firstCallNext){
//                hasData = cursor.moveToFirst();
//            }
//            else{
//                hasData = cursor.moveToNext();
//            }
//
//            firstCallNext = false;
//
//            if (hasData){
//                String keyEncode = cursor.getString(keyColumnIndex);
//
//                //存在数据库中的key 是带有key分类前缀的（***.key），现在将前缀去除。
//                if (keyEncode != null){
//                    String keys[] = keyEncode.split("\\.");
//                    if (keys != null && keys.length > 0){
//                        keyEncode = keys[keys.length - 1];
//                    }
//                }
//
//                //创建一个CacheKey对象，然后将key值设置进去。
//                key = CacheKey.generate("") ;
//                //key.setKeyEncode(keyEncode);//TODO
//
//                return key;
//            }
//            else{
//                throw new NoSuchElementException();
//            }
//        }
//
//        @Override
//        public void remove() {
//            if (cache == null || key == null) return;
//
//            cache.remove(key);
//        }
//    }
}

package com.lakala.core.cache;

import android.content.Context;

import com.lakala.library.util.StringUtil;
import com.lakala.core.dao.DBCache;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * 文件缓存器，该类将文件缓存在应用程序缓存目录中或外部存储器中，被缓存的文件可以被多个 FileCache 实例访问，
 * 所以 key 必需为一，否则会出现不可预料的结果。
 * 该类不是线程安全的，如果在多个线程中同时访问一个文件请自已保证同步性。
 * @author Michael
 */
public class FileCache implements ICache<byte[],InputStream> {

	//当前文件缓存目录（全路径）
	private String cachePath;
    //缓存根路径（全路径）
    private String rootPath;
	//数据库缓存对象
	private DBCache cache;
    //缓存的category
    private String category;

    /**
     * 上下文对象
     * @param context
     */
    public FileCache(Context context){
        this(context,"filecache");
    }

    /**
     * 创建文件缓存实例。
     * @param context       上下文对象
     * @param exRootName    缓存根路径名
     */
	public FileCache(Context context, String exRootName){

        if (StringUtil.isEmpty(exRootName)){
            exRootName = "/filecache";
        }

        if (!exRootName.startsWith("/"))
            exRootName = "/".concat(exRootName);

        //使用应用程序下的 cache 目录。
        rootPath = context.getCacheDir().getAbsolutePath().concat(exRootName);

		cache = new DBCache(context);

        setCategory("file");
	}

    @Override
    public void setCategory(String category) {
        if (category == null){
            return;
        }

        cache.setCategory(category);

        cachePath = String.format("%s/%s", rootPath, category);

        //如果目录不存在，则创建它。
        (new File(cachePath)).mkdirs();

        this.category = category;
    }

    @Override
	public boolean put(CacheKey key, ExpireDate date, byte[] buffer) {
		boolean success = save(key.getCacheKey(),buffer);

		if (!success)
			return false;

		String record = toRecord(key.getCacheKey());

		return cache.put(key, date, record);
	}

    @Override
    public boolean put(CacheKey key, int version, byte[] data) {
        boolean success = save(key.getCacheKey(),data);

        if (!success)
            return false;

        String record = toRecord(key.getCacheKey());

        return cache.put(key, version, record);
    }

    @Override
    public boolean put(CacheKey key, ExpireDate date, int version, byte[] data) {
        boolean success = save(key.getCacheKey(),data);

        if (!success)
            return false;

        String record = toRecord(key.getCacheKey());

        return cache.put(key, date, version, record);
    }

    @Override
	public InputStream get(CacheKey key) {
		//从数据库中获取缓存文件信息
		String record = cache.get(key);
        return getInputStream(record);
	}

    @Override
    public InputStream get(CacheKey key, int newVersion) {
        String record = cache.get(key,newVersion);
        return getInputStream(record);
    }

    @Override
	public InputStream getExpiredData(CacheKey key) {
		//从数据库中获取缓存文件信息
		String record = cache.getExpiredData(key);
		if (record == null)
			return null;

		//得到缓存文件路径
		String path   = toPath(record);

		if (path == null)
			return null;

		try {
			FileInputStream fis = new FileInputStream(path);
			return fis;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public ExpireDate getExpireDate(CacheKey key) {
		return cache.getExpireDate(key);
	}

    @Override
    public int getVersion(CacheKey key) {
        return cache.getVersion(key);
    }

    @Override
	public void remove(CacheKey key) {
		String record = cache.getExpiredData(key);

		//数据库中没有该文件信息
		if (record == null)
			return;

		//得到缓存文件路径
		String path = toPath(record);

		if (path == null)
			return;

		File file = new File(path);
		file.delete();

		cache.remove(key);
	}

    @Override
    public void clean() {
        //暂不实现，没有需要。
    }

    @Override
	public boolean isExpire(CacheKey key) {
		return cache.isExpire(key);
	}

    @Override
    public boolean isExpire(CacheKey key, int newVersion) {
        return cache.isExpire(key,newVersion);
    }

    @Override
    public boolean isExpireByDateAndVersion(CacheKey key, int newVersion){
        return cache.isExpireByDateAndVersion(key,newVersion);
    }

    @Override
	public boolean isExist(CacheKey key) {
		String record = cache.getExpiredData(key);

		//数据库中没有该文件信息
		if (record == null)
			return false;

		//得到缓存文件路径
		String path = toPath(record);

		if (path == null)
			return false;

		File file = new File(path);

		return file.isFile() && file.exists();
	}

//	@Override
//	public Iterator<CacheKey> keyIterator() {
//		return cache.keyIterator();
//	}

    /**
     * 获取缓存文件的路径，如果指定的 key 没缓存过则返回 null。该方法并不检查数据是否过期或文件是否真实存在。
     * @param key 缓存 Key
     * @return  文件全路径
     */
    public String getFilePath(CacheKey key){
        String record = cache.get(key);
        if (record != null){
            return toPath(record);
        }
        else{
            return null;
        }
    }

	/**
	 * 将文件名转换成数据库记录
	 * @param filename 文件名
	 * @return 数据库记录
	 */
	private String toRecord(String filename){
		return String.format("{'location':%d,'filename':'%s'}", filename);
	}

	/**
	 * 将数据库记录转换成文件全路径
	 * @param record  数据库记录
	 * @return 如果成功返回文件全路径，如果失败返回 null。
	 */
	private String toPath(String record){
		try {
			JSONObject json = new JSONObject(record);

			//数据库记录的信息中没有filename 字段，说明数据格式有错误。
			if (!json.has("filename")){
				return null;
			}

			String filename = json.getString("filename");
			if (StringUtil.isEmpty(filename)){
				//文件名为空，不正确。
				return null;
			}

			return genFilePath(filename);
			
		} catch (Exception e) {}
		
		return null;
	}
	
	/**
	 * 保存文件
	 * @param filename 文件名
	 * @return
	 */
	private boolean save(String filename,byte[] buffer){
		FileOutputStream fos = null;
		try {
			String path = genFilePath(filename);
			
			if (path == null)
				return false;
			
			fos = new FileOutputStream(path);
			fos.write(buffer);
			fos.close();
			
			return true;
		} 
		catch (Exception e) {
			return false;
		}
		finally {
			try{
				if (fos != null)
					fos.close();
			}catch(Exception e){};
		}
	}

    /**
     * 通过文件名生成文件缓存的路径
     * @param filename
     * @return  文件缓存的绝对路径(包括文件名)
     */
	private String genFilePath(String filename){
        return String.format("%s/%s/%s", cachePath, category, filename);
	}

    private InputStream getInputStream(String record){
        if (record == null)
            return null;

        //得到缓存文件路径
        String path = toPath(record);

        if (path == null)
            return null;

        try {
            FileInputStream fis = new FileInputStream(path);
            return fis;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}

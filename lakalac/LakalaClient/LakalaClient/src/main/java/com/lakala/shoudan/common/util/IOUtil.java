package com.lakala.shoudan.common.util;

import android.os.Environment;
import android.os.StatFs;

import com.lakala.library.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * 有关文件操作的工具类
 * @author xyz
 *
 */
public class IOUtil {
	//存储卡已经安装标志
//	private  boolean mExternalStorageAvailable 	= false;
	
	/**  存储卡具有写权限标志  */
	private  boolean mExternalStorageWriteable 	= false;
	
	/**  SD卡根目录  */
	public static final  String  sdCardRootPath = Environment.getExternalStorageDirectory().getPath()+"/";
	
	public static final String lakalalog = sdCardRootPath+"lakalalog.txt";
	public  IOUtil(){
		 String state = Environment.getExternalStorageState();
		 if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {		     
		     mExternalStorageWriteable = false;
		 }
	}
	
	/**
	 * 私有方法，计算文件空闲块大小
	 * @param file  目标文件
	 * @return      文件大小
	 */
	private long freeFileSize(File file) {
		 long availableBlocks = 0;
		 long blockSize = 0;
		 try {
			StatFs statfs = null;
			 if(file.exists())
			 {
				 statfs = new StatFs(file.getPath());  
				 availableBlocks = statfs.getAvailableBlocks();
				 blockSize = statfs.getBlockSize();
			 }
		} catch (Exception e) {
             LogUtil.print(e);
		}   
		return availableBlocks * blockSize;   
	}
	
	
	/**
	 * 判断sd卡是否存在
	 * @return  true 存在   </br>  flase  未安装SD卡
	 */
	public static boolean isSdCardAvailable() {
		 String state = Environment.getExternalStorageState();
		 if (Environment.MEDIA_MOUNTED.equals(state)) {
		     return true;
		 } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		     return true;	     
		 } else {
		     return false;
		 }		
	}
	/**
	 * 创建目录
	 * @param    dirPath 目录的名称
	 * @return   true 创建成功   </br> false 创建失败
	 */
	public  boolean createDir(String dirPath) {
		if (dirPath == null || !mExternalStorageWriteable) {
			return false;
		}
		try {
			if (!dirPath.startsWith(sdCardRootPath)) {
				dirPath = sdCardRootPath + dirPath;
			}
			File dir = new File(dirPath);
			if (dir.exists()) {
				return true;
			}
//			if (!dirPath.endsWith(File.separator)) {
//				dirPath = dirPath + File.separator;
//			}
			return dir.mkdirs();
		} catch (Exception e) {
            LogUtil.print(e);
			return false;
		}
	}
	
	
	/**
	 * 获取空闲的内存大小
	 * @return 空闲内存大小
	 * @see    # freeFileSize(File)
	 */
	public  long getFreeMemory(){
		File file = Environment.getDataDirectory(); 
		return freeFileSize(file); 
	}
	
	/**
	 *     获取空闲的sd卡大小
	 * @return  空闲的SD卡大小
	 *  @see   # freeFileSize(File)
	 */
	public  long getFreeSD(){
        File file = Environment.getExternalStorageDirectory();   
        return freeFileSize(file);   
    }
	
	
	/**
	 * 删除文件，如果是目录，递归删除目录下的所有文件
	 * @param file  要删除的文件
	 * @return   true  删除成功 </br>  false 删除失败
	 */
	public boolean delFile(File file) {
		if (file == null ) {
			return false;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File subFile : files) {
					if (!delFile(subFile)) {
						return false;
					}
				}
			}
		}
		
		return file.delete();
	}
	/**
	 * 删除文件，如果是目录，递归删除目录下的所有文件
	 * @param filePath    要删除的文件或目录
	 * @return  true  删除成功 </br>  false 删除失败
	 * @see   # delFile(File)
	 */
	public boolean delFile(String filePath) {
		if (!filePath.startsWith(sdCardRootPath)) {
			filePath = sdCardRootPath + filePath;
		}
		File file = new File(filePath);
		return delFile(file);
	}
	/**
	 * 将信息写入文件,追加模式
	 * @param filePath 要写入到文件的所在路径
	 * @param message  追加信息
	 * @return   true  写入成功 </br>  false 写入失败
	 * @see   # getFreeSD()
	 */
	public boolean write(String filePath,String message) {
		if(filePath == null || message == null || !mExternalStorageWriteable
				|| getFreeSD() < 100) 
			return false;
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath, true);
			fos.write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fos = null;
		}
		return true;
	}
	
	
	 
	
}

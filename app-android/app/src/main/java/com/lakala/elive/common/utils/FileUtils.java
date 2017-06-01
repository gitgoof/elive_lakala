package com.lakala.elive.common.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static String ELIVE_SD_SAVE_PATH = Environment.getExternalStorageDirectory() + "/com.lakala.elive/";

	public static String saveBitmap(Bitmap bm, String picName) {
        String savePath = "";
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(ELIVE_SD_SAVE_PATH, picName + ".JPEG");
            savePath = f.getAbsolutePath();
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return savePath;
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(ELIVE_SD_SAVE_PATH + dirName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}




	public static boolean isFileExist(String fileName) {
		File file = new File(ELIVE_SD_SAVE_PATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(ELIVE_SD_SAVE_PATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(ELIVE_SD_SAVE_PATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}



    public static File createLocalSavePath(String localSavePath) throws IOException {
        File dir = new File(localSavePath);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileDirExist(String localSavePath) {
        File file = new File(localSavePath);
        return file.exists();
    }

    public static byte[] getImageFileBytes(String localFilePath) throws Exception{
        byte[] buffer = new byte[1024];
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        int len = -1;
        inputStream  = new FileInputStream(localFilePath);
        outputStream= new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();

    }

}

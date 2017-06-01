package com.lakala.platform.common;

import android.content.Context;

/**
 * 包目录文件管理器
 * Created by xyz on 14-2-22.
 */
public class PackageFileManager {

    private Context context;

    private static PackageFileManager instance;

    private final static String ASSETS_DIR  = "/assets";
    private final static String CONFIG_DIR  = "/config";
    private final static String IMAGE_DIR   = "/images";
    private final static String PMOBILE_DIR = "/www";
    private final static String AGREEMENTS_DIR = "/app/pages/agreements/";

    private String packageFilePath;

    private PackageFileManager(){
        context = ApplicationEx.getInstance();

        packageFilePath =  context.getFilesDir().getPath();
    }

    public static PackageFileManager getInstance(){
        if(instance == null){
            instance = new PackageFileManager();
        }

        return instance;
    }

    /**
     * 获取根路径
     * @return
     */
    public String getRootPath() {
        return packageFilePath.concat(ASSETS_DIR);
    }

    /**
     * 获取配置文件目录的绝对路径
     * @return
     */
    public String getConfigPath(){
        return packageFilePath.concat(ASSETS_DIR).concat(CONFIG_DIR);
    }

    /**
     * 获取图片目录的绝对路径
     *
     * @return
     */
    public String getImagePath(){
        return packageFilePath.concat(ASSETS_DIR).concat(PMOBILE_DIR).concat(IMAGE_DIR);
    }

    /**
     * 获取bundle目录的绝对路径
     * @return
     */
    public String getPmobilePath(){
        return packageFilePath.concat(ASSETS_DIR).concat(PMOBILE_DIR);
    }

    /**
     * 获取bundle目录下协议子目录的绝对路径
     * @return
     */
    public String getAgreementsDirPath(){
        return packageFilePath.concat(ASSETS_DIR).concat(PMOBILE_DIR).concat(AGREEMENTS_DIR);
    }
}

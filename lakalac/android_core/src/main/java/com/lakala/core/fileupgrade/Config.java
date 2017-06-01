package com.lakala.core.fileupgrade;

import android.content.Context;

import java.io.File;

/**
 * <p>Description  : Config.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午1:36.</p>
 */
public class Config {

    //Module Name
    public static final String  MODULE_NAME = "FileUpgrade";

    private PathConfig     pathConfig;
    private RequestConfig  requestConfig;
    private SuffixConfig   suffixConfig;
    private UpdateConfig   updateConfig;

    private FileMainEntity mainEntity;
    private Context        context;
    private String         publicKey;
    private boolean        DEBUG;

    private static Config instance;

    private Config(ConfigEntity config){

        if (!config.checkParam()){
            throw new IllegalArgumentException("请检查您的参数设置是否正确!");
        }

        this.DEBUG      = config.isDEBUG();
        this.context    = config.getContext();
        //init public key
        this.publicKey  = config.getPublicKey();
        // init  PathConfig
        pathConfig      = new PathConfig(context.getFilesDir().getPath());
        pathConfig.setMainName(config.getMainFileName());
        pathConfig.setMainDirectory(config.getMainFileDirectoryName());
        pathConfig.setMainInAssetsPath(config.getMainInAssetsPath());
        pathConfig.setPackageConfig(config.getPackageConfig());
        pathConfig.setPackageDirectory(config.getPackageDir());
        pathConfig.setPackageWWW(config.getPackageWWW());
        //init RequestConfig
        requestConfig   = new RequestConfig();
        requestConfig.setBaseUrl(config.getRequestBaseUrl());
        requestConfig.setCommonRequestParams(config.getRequestParams());
        requestConfig.setTimeout(config.getRequestTimeout());
        //init SuffixConfig
        suffixConfig    = new SuffixConfig(config.getUpgradeSuffix(), config.getBundleSuffix(), config.getOriginalFileSuffix());
        //init UpgradeConfig
        updateConfig    = new UpdateConfig();
        updateConfig.setMaxForceUpgradeTimes(config.getMaxForceUpgradeTimes());
        //init FileMainEntity
        mainEntity      = new FileMainEntity(pathConfig.getMainName(), pathConfig.getMainDirectory(), suffixConfig.getOriginalFileSuffix());
    }

    /**
     * 初始化方法必须在 getInstance 方法前调用
     *
     * @param config  ConfigEntity
     */
    public static void init(ConfigEntity config){

        if (instance == null){
            instance = new Config(config);
        }
    }

    /**
     * 获取实例
     *
     * @return Config instance
     */
    public static synchronized Config getInstance(){

        if (instance == null){
            throw new IllegalArgumentException("you must invoke method init().");
        }

        if (instance.context == null){
            throw new IllegalArgumentException("Context must not be null, when the method invoked.");
        }

        return instance;
    }

    /**
     * 获取 Context
     *
     * @return context
     */
    /*package*/ Context getContext(){
        return context;
    }

    /**
     * 获取验签公钥
     *
     * @return String
     */
    /*package*/ String getPublicKey(){
        return publicKey;
    }

    /**
     * 获取 DEBUG 标识
     *
     * @return boolean
     */
    /*package*/ boolean isDebug(){
        return DEBUG;
    }

    /**
     * 获取保存所有路径的实体
     *
     * @return PathEntity
     */
    public PathConfig getPathConfig(){
        return pathConfig;
    }

    /**
     * 获取 Request 实体
     *
     * @return RequestEntity
     */
    /*package*/ RequestConfig getRequestConfig(){
        return requestConfig;
    }

    /**
     * 获取 Suffix 实体
     *
     * @return SuffixEntity
     */
    /*package*/ SuffixConfig getSuffixConfig(){
        return suffixConfig;
    }

    /**
     * 获取 Update 实体
     *
     * @return UpdateConfig
     */
    /*package*/ UpdateConfig getUpdateConfig(){
        return updateConfig;
    }

    /**
     * 获取主配置文件实体
     *
     * @return FileMainEntity
     */
    /*package*/ FileMainEntity getMainEntity(){
        return mainEntity;
    }


    /*                  update config                   */
    /*package*/ static class UpdateConfig {

        private int maxForceUpgradeTimes;

        public void setMaxForceUpgradeTimes(int max){
            this.maxForceUpgradeTimes = max;
        }

        /**
         * 获取强制更新最大次数
         *
         * @return int
         */
        public int getMaxForceUpgradeTimes(){
            return maxForceUpgradeTimes;
        }
    }

    /*                  Suffix Config                   */
    /*package*/ static class SuffixConfig {

        private String upgradeSuffix;
        private String bundleSuffix;
        private String originalFileSuffix;

        public SuffixConfig(String upgradeSuffix, String bundleSuffix, String originalFileSuffix){
            this.upgradeSuffix      = upgradeSuffix;
            this.bundleSuffix       = bundleSuffix;
            this.originalFileSuffix = originalFileSuffix;
        }

        /**
         * 获取 upgrade 文件后缀
         * @return suffix
         */
        public String getUpgradeSuffix(){
            return upgradeSuffix;
        }

        /**
         * 获取 bundle 文件后缀
         * @return suffix
         */
        public String getBundleSuffix(){
            return bundleSuffix;
        }

        /**
         * 获取 assets 中原始文件后缀
         * @return suffix
         */
        public String getOriginalFileSuffix(){
            return originalFileSuffix;
        }
    }

    /*                  Request Config                  */
    /*package*/ static class RequestConfig {

        private Object[] params;
        private String   baseUrl;
        private int      timeout;

        public RequestConfig(){

        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setBaseUrl(String url){
            this.baseUrl = url;
        }

        /**
         * 获取请求 Base URL
         *
         * @return String
         */
        public String getBaseURL(){
            return baseUrl;
        }

        /**
         * 设置公共参数
         *
         * @param keysAndValues  key and value
         */
        public void setCommonRequestParams(Object... keysAndValues){
            int len = keysAndValues.length;
            if (len % 2 != 0)
                throw new IllegalArgumentException("Supplied arguments must be even");

            this.params = keysAndValues;
        }

        /**
         * 获取公共参数
         *
         * @return  params[]
         */
        public Object[] getCommonRequestParams(){
            return params;
        }
    }


    /*                  路径配置                    */
    public static class PathConfig {

        //主配置文件名字
        private String MAIN_NAME             = "main.upgrade";
        private String MAIN_DIRECTORY        = "config";
        private String MAIN_IN_ASSETS_PATH   = "assets/config";

        //保存所有文件的目录
        private String PACKAGE_DIR           = "/assets";
        private String PACKAGE_WWW           = "/www";
        private String PACKAGE_CONFIG        = "/config";

        //项目包路径
        private String packageFilePath;

        public PathConfig(String packageFilePath){
            this.packageFilePath = packageFilePath;
        }

        public void setMainName(String mainName){
            this.MAIN_NAME = mainName;
        }

        public void setMainDirectory(String mainDirectory){
            this.MAIN_DIRECTORY = mainDirectory;
        }

        public void setMainInAssetsPath(String path){
            this.MAIN_IN_ASSETS_PATH = path;
        }

        public void setPackageDirectory(String directory){
            this.PACKAGE_DIR = directory;
        }

        public void setPackageWWW(String www){
            this.PACKAGE_WWW = www;
        }

        public void setPackageConfig(String config){
            this.PACKAGE_CONFIG = config;
        }

        /**
         * 获取主配置文件名字
         *
         * @return  Main Name
         */
        public String getMainName(){
            return MAIN_NAME;
        }

        /**
         * 获取主配置文件的目录
         *
         * @return  Main Directory
         */
        public String getMainDirectory(){
            return MAIN_DIRECTORY;
        }

        /**
         * 获取主配置文件绝对路径
         *
         * @return path
         */
        public String getMainConfigPath(){
            return getConfigPath().concat(File.separator).concat(MAIN_NAME);
        }

        /**
         * 获取主配置文件在 assets 中的路径
         *
         * @return path
         */
        public String getMainConfigInAssetsPath(){
            return MAIN_IN_ASSETS_PATH.concat(File.separator).concat(MAIN_NAME);
        }

        /**
         * 获取保存文件的根目录路径
         *
         * @return path
         */
        public String getRootPath(){
            return packageFilePath.concat(PACKAGE_DIR);
        }

        /**
         * 获取配置文件路径
         *
         * @return
         */
        public String getConfigPath(){
            return getRootPath().concat(PACKAGE_CONFIG);
        }

        /**
         * 获取 WWW 路径
         *
         * @return
         */
        public String getWWWPath(){
            return getRootPath().concat(PACKAGE_WWW);
        }

    }

}

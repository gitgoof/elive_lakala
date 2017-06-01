package com.lakala.core.fileupgrade;

import android.content.Context;

/**
 * <p>Description  : TODO.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/11.</p>
 * <p>Time         : 下午10:41.</p>
 */
public class ConfigEntity {

    private static final String NAME = ConfigEntity.class.getName();

    //debug  flag
    private boolean  DEBUG;
    //context
    private Context  context;
    //public key
    private String   publicKey;

    private String   mainFileName           = "main.upgrade";
    private String   mainFileDirectoryName  = "config";
    private String   mainInAssetsPath       = "assets/config";

    private String   packageDir             = "/assets";
    private String   packageWWW             = "/www";
    private String   packageConfig          = "/config";

    private String   requestBaseUrl;
    private Object[] requestParams;
    private int      requestTimeout         = 60*1000;

    private String   upgradeSuffix          = ".upgrade";
    private String   bundleSuffix           = ".zip";
    private String   originalFileSuffix     = ".zip";

    private int      maxForceUpgradeTimes   = 3;

    public ConfigEntity(){

    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMainFileName() {
        return mainFileName;
    }

    public void setMainFileName(String mainFileName) {
        this.mainFileName = mainFileName;
    }

    public String getMainFileDirectoryName() {
        return mainFileDirectoryName;
    }

    public void setMainFileDirectoryName(String mainFileDirectoryName) {
        this.mainFileDirectoryName = mainFileDirectoryName;
    }

    public String getMainInAssetsPath() {
        return mainInAssetsPath;
    }

    public void setMainInAssetsPath(String mainInAssetsPath) {
        this.mainInAssetsPath = mainInAssetsPath;
    }

    public String getPackageDir() {
        return packageDir;
    }

    public void setPackageDir(String packageDir) {
        this.packageDir = packageDir;
    }

    public String getPackageWWW() {
        return packageWWW;
    }

    public void setPackageWWW(String packageWWW) {
        this.packageWWW = packageWWW;
    }

    public String getPackageConfig() {
        return packageConfig;
    }

    public void setPackageConfig(String packageConfig) {
        this.packageConfig = packageConfig;
    }

    public String getRequestBaseUrl() {
        return requestBaseUrl;
    }

    public void setRequestBaseUrl(String requestBaseUrl) {
        this.requestBaseUrl = requestBaseUrl;
    }

    public Object[] getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Object[] requestParams) {
        this.requestParams = requestParams;
    }

    public String getUpgradeSuffix() {
        return upgradeSuffix;
    }

    public void setUpgradeSuffix(String upgradeSuffix) {
        this.upgradeSuffix = upgradeSuffix;
    }

    public String getBundleSuffix() {
        return bundleSuffix;
    }

    public void setBundleSuffix(String bundleSuffix) {
        this.bundleSuffix = bundleSuffix;
    }

    public String getOriginalFileSuffix() {
        return originalFileSuffix;
    }

    public void setOriginalFileSuffix(String originalFileSuffix) {
        this.originalFileSuffix = originalFileSuffix;
    }

    public int getMaxForceUpgradeTimes() {
        return maxForceUpgradeTimes;
    }

    public void setMaxForceUpgradeTimes(int maxForceUpgradeTimes) {
        this.maxForceUpgradeTimes = maxForceUpgradeTimes;
    }

    /*package*/ boolean checkParam(){
        if (context == null){
            LOG.e(Utils.errorMessage("context 不能为 null", NAME));
            return false;
        }

        if (Utils.isEmpty(publicKey)){
            LOG.e(Utils.errorMessage("public key 不能为 null", NAME));
            return false;
        }

        if (Utils.isEmpty(mainFileName)){
            LOG.e(Utils.errorMessage("主配置文件名，不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(mainFileDirectoryName)){
            LOG.e(Utils.errorMessage("主配置文件保存的相对目录名不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(mainInAssetsPath)){
            LOG.e(Utils.errorMessage("主配置文件在 assets 中的路径不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(packageDir)){
            LOG.e(Utils.errorMessage("包下存储更新文件的根目录名不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(packageWWW)){
            LOG.e(Utils.errorMessage("包下 bundle 目录名不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(packageConfig)){
            LOG.e(Utils.errorMessage("包下配置文件目录不能为空", NAME));
            return false;
        }

        if (requestTimeout == 0){
            LOG.e(Utils.errorMessage("请求超时时间不能为0", NAME));
            return false;
        }

        if (Utils.isEmpty(requestBaseUrl)){
            LOG.e(Utils.errorMessage("请求根路径不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(upgradeSuffix)){
            LOG.e(Utils.errorMessage("upgrade 文件后缀不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(bundleSuffix)){
            LOG.e(Utils.errorMessage("bundle 后缀不能为空", NAME));
            return false;
        }

        if (Utils.isEmpty(originalFileSuffix)){
            LOG.e(Utils.errorMessage("原始文件（下载下来的文件或预装文件）后缀不能为空", NAME));
            return false;
        }

        return true;
    }
}

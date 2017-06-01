package com.lakala.core.fileupgrade;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * <p>Description  : 主配置文件实体.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/10.</p>
 * <p>Time         : 下午8:16.</p>
 */
/*package*/ class FileMainEntity implements EntityInterface {

    final public static String TAG = "main";

    private String MAIN_NAME       ;
    private String MAIN_DIRECTORY  ;
    private String ORIGINAL_SUFFIX ;

    //是否为最新实体（请求返回空数据或成功返回文件流都为最新实体）
    private boolean newest;
    //是否为新下载下来的实体，主要区分当前使用的实体是否为下载下来的
    private boolean newDownload;

    public FileMainEntity(String name, String directory, String originalSuffix){
        this.MAIN_NAME        = name;
        this.MAIN_DIRECTORY   = directory;
        this.ORIGINAL_SUFFIX  = originalSuffix;
    }

    public boolean isNewest() {
        return newest;
    }

    public void setNewest(boolean newest) {
        this.newest = newest;
    }

    public boolean isNewDownload() {
        return newDownload;
    }

    public void setNewDownload(boolean newDownload) {
        this.newDownload = newDownload;
    }

    /**
     * 校验主文件是否有效，返回可用的 File
     *
     * @return File
     * @throws Exception
     */
    public File checkValidateSignIn() throws Exception {
        File main = getFile();
        //#1.
        if (!main.exists()){
            copyAssetFileAndDecompress();
        }

        //#2.
        if (!checkLocalFile()){
            copyAssetFileAndDecompress();
        }

        return main;
    }

    @Override public String getCheckURL() {
        String url = "";
        try {
            JSONObject main = new JSONObject(getConfigFileContent());
                       url  = main.optString(Keys.CHECK_URL);
        } catch (Exception e) {
            LOG.e(Utils.errorMessage(e.getMessage(), FileMainEntity.class.getName()));
        }
        return url;
    }

    @Override public String getFileName() {
        return MAIN_NAME;
    }

    @Override public String getTargetDirectory() {
        return MAIN_DIRECTORY;
    }

    @Override public String getMD5ForEntityPath() {
        return Digest.md5(getFile());
    }

    /**
     * 复制 assets 中的文件到包中目标路径，并解压
     *
     * @return   是否成功，试过失败表示 assets 中预装中没有路径对应的文件
     */
    @Override public boolean copyAssetFileAndDecompress() throws Exception {
        String assetsPathZip = getFilePathInAssets();
        String targetPathZip = getZipFilePath();
        //copy
        boolean isExistInAsset = Utils.copyAssetFile(Config.getInstance().getContext(), assetsPathZip, targetPathZip);

        if (!isExistInAsset){
            throw new IllegalArgumentException("assets 中不存在 main.upgrade.zip 文件!");
        }

        //decompress
        Utils.decompressZip(targetPathZip, "");
        //delete
        Utils.deleteFile(targetPathZip);

        return true;
    }

    /**
     * 校验下载的文件是否有效
     *
     * @return  boolean
     */
    @Override public boolean checkDownloadFile() {
        boolean valid       = false;
        String  tempDirPath = Config.getInstance().getPathConfig().getRootPath()
                .concat(File.separator)
                .concat(MAIN_DIRECTORY)
                .concat(File.separator)
                .concat(System.currentTimeMillis() + "");
        try {
            //解压
            Utils.decompressZip(getDownloadFile(), tempDirPath);
            //验证
            valid = CheckSign.checkSign(new File(tempDirPath.concat(File.separator).concat(MAIN_NAME)));
        } catch (IOException e) {
            e.printStackTrace();
            valid = false;
        }finally {
            Utils.deleteFile(tempDirPath);
        }
        return valid;
    }

    /**
     * 检验本地文件是否有效
     *
     * @return boolean
     */
    @Override public boolean checkLocalFile() {
        return CheckSign.checkSign(getFile());
    }

    /**
     * 解压下载的实体
     *
     * @return  boolean
     */
    @Override public boolean decompressEntity() {
        boolean success = false;
        try {
            if (!getDownloadFile().exists()){
                return false;
            }

            success = Utils.decompressZip(getDownloadFile(), "");
        } catch (IOException e) {
            LOG.e(e.getMessage(), e);
            success = false;
        }
        return success;
    }

    /**
     * 删除下载文件
     *
     * @return 是否成功
     */
    @Override public boolean deleteDownloadFile() {
        return Utils.deleteFile(getDownloadFile());
    }

    /**
     * 获取 Main 配置文件内容
     * #1. 校验包目录下面是否存在主配置文件，若不存在则复制预装版本(app 预装 assets 中必须存在主配置文件)
     * #2. 校验主配置文件是否有效，若无效，则复制预装版本到包目录
     * #3. 读取文件
     *
     * @return String
     */
    @Override
    public String getConfigFileContent() throws Exception {
        File main = checkValidateSignIn();

        return Utils.readFile(main);
    }

    /**
     * 获取文件
     *
     * @return File
     */
    @Override public File getFile() {
        return new File(getFilePath());
    }

    /**
     * 获取下载的文件
     *
     * @return File
     */
    @Override public File getDownloadFile() {
        return new File(getZipFilePath());
    }

    /**
     * 获取文件绝对路径
     *
     * @return Path
     */
    @Override public String getFilePath() {
        return Config.getInstance().getPathConfig().getMainConfigPath();
    }

    /**
     * 获取文件在 assets 中的路径，预装 assets 中的文件后缀都为 .zip ,所以拼上了后缀
     *
     * @return Path
     */
    @Override public String getFilePathInAssets() {
        return Config.getInstance().getPathConfig().getMainConfigInAssetsPath()
                .concat(ORIGINAL_SUFFIX);
    }

    /**
     * 获取 zip 文件路径
     *
     * @return Path
     */
    @Override public String getZipFilePath() {
        return getFilePath().concat(ORIGINAL_SUFFIX);
    }
}

package com.lakala.core.fileupgrade;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * <p>Description  : 文件实体.
 *                   除了主配置文件的其他实体。</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 上午11:45.</p>
 */
public class FileEntity implements EntityInterface {

    public static final long serialVersionUID = 9527L;

    /*                  当前实体对应文件的状态                 */
    public enum FileStatus{
        NormalFile,                  //正常文件可用文件
        DownloadFile,                //更新，下载下来的文件
        OldFile,                     //该文件更新失败，或其上级文件更新失败
        NewFile,                     //新增文件
        InvalidateAndNotExistFile,   //验证无效且 app 预装 assets 中没有的文件
        InvalidateButExistFile       //验证无效但是该文件在 app 预装 assets中存在
    }


    //文件后缀
    private static final String UPGRADE_SUFFIX        = Config.getInstance().getSuffixConfig().getUpgradeSuffix();
    private static final String BUNDLE_SUFFIX         = Config.getInstance().getSuffixConfig().getBundleSuffix();
    //在assets中预装文件的后缀
    private static final String ORIGINAL_FILE_SUFFIX  = Config.getInstance().getSuffixConfig().getOriginalFileSuffix();
    //如果强制更新，最多请求3次
    private static final int    MAX_FORCE_TIMES       = Config.getInstance().getUpdateConfig().getMaxForceUpgradeTimes();


    /*                  配置文件中的属性                    */
    //若该文件为预装，则表示该文件在assets中的目录，否则为空
    private String     bundleDirectory;
    //当前版本
    private int        version;
    //与 version 对应文件的 MD5 值。
    private String     MD5;
    //需要更新时的下载地址。
    private String     checkURL;
    //文件名
    private String     fileName;
    //是否是需要强制更新
    private boolean    forceUpdate;
    //文件存放的目录（相对路径）
    private String     targetDirectory;
    //指定zip文件的展开目录（相对路径），可以不配置，则表示不展开。
    private String     expandDirectory;
    //是否需要验签
    private boolean    verifySign;


    /*                  附加属性                    */
    //文件等级
    private int        fileLevel;
    //当前 Entity 对应文件的状态
    private FileStatus fileStatus;
    //该实体的 tag
    private String     tag;
    //child tag,如果这个实体对应的文件后缀为 .upgrade 那么这个文件存在孩子实体
    private String     childTag;
    //parent tag,如果这个实体是 .upgrade 文件对应的子实体，那么该tag表示其父实体的 tag
    private String     parentTag;
    //强制更新次数
    private int        forceTimes;
    //是否为新下载下来的实体，主要区分当前使用的实体是否为下载下来的
    private boolean    newDownload;

    public FileEntity(String tag, JSONObject data){
        setBundleDirectory(data.optString(Keys.BUNDLE_DIRECTORY));
        setVersion        (data.optInt(Keys.VERSION, -1));
        setMD5            (data.optString(Keys.MD5));
        setCheckURL       (data.optString(Keys.CHECK_URL));
        setFileName       (data.optString(Keys.FILE_NAME));
        setForceUpdate    (data.optBoolean(Keys.FORCE_UPDATE));
        setTargetDirectory(data.optString(Keys.TARGET_DIRECTORY));
        setExpandDirectory(data.optString(Keys.EXPAND_DIRECTORY));
        setFileStatus     (FileStatus.NormalFile);
        setTag            (tag);
//        setFileLevel();
        this.forceTimes = 0;
    }

    public boolean isNewDownload() {
        return newDownload;
    }

    public void setNewDownload(boolean newDownload) {
        this.newDownload = newDownload;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChildTag() {
        return childTag;
    }

    public void setChildTag(String childTag) {
        this.childTag = childTag;
    }

    public String getParentTag() {
        return parentTag;
    }

    public void setParentTag(String parentTag) {
        this.parentTag = parentTag;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getBundleDirectory() {
        return bundleDirectory;
    }

    public void setBundleDirectory(String bundleDirectory) {
        this.bundleDirectory = bundleDirectory;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    @Override public String getCheckURL() {
        return checkURL;
    }

    public void setCheckURL(String checkURL) {
        this.checkURL = checkURL;
    }

    @Override public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getExpandDirectory() {
        return expandDirectory;
    }

    public void setExpandDirectory(String expandDirectory) {
        this.expandDirectory = expandDirectory;
    }

    public boolean isVerifySign() {
        return verifySign;
    }

    public void setVerifySign(boolean verifySign) {
        this.verifySign = verifySign;
    }

    public int getFileLevel() {
        return fileLevel;
    }

    public void setFileLevel(int fileLevel) {
        this.fileLevel = fileLevel;
    }


    /**
     * 获取该实体全名
     * e.g. tag@childTag, 如果childTag为空则为 tag@
     *
     * @return
     */
    public String getFullName(){
        return String.format("%s@%s@%s", getParentTag(), getTag(), getChildTag());
    }

    /**
     * 该方法用于控制强制更新的次数，如果强制更新次数达到上线，
     * 那么将不在进行强制更新
     *
     * @return    canForceUpdate
     */
    public boolean canForceUpdate(){
        forceTimes++;
        return forceTimes <= MAX_FORCE_TIMES;
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
        //decompress
        Utils.decompressZip(targetPathZip, "");
        //delete
        Utils.deleteFile(targetPathZip);

        //如果解压后需要二次解压，那么继续解压
        if (!Utils.isEmpty(getExpandDirectory())){
            isExistInAsset = Utils.decompressZip(getFilePath(),
                                                 Config.getInstance().getPathConfig().getRootPath()
                                                       .concat(File.separator)
                                                       .concat(getExpandDirectory()));
        }

        return isExistInAsset;
    }

    /**
     * 验证该实体下载的文件是否有效
     * 把下载下来的文件解压缩到一个临时文件夹中，然后使用解压出来的签名文件和目标文件进行验证
     * 验证成功后，删除临时文件夹。如果该文件不需要验证，那么无需做以上操作。
     *
     * @return   下载的文件是否有效
     */
    @Override public boolean checkDownloadFile(){
        if (!isVerifySign()){
            return true;
        }

        boolean valid       = false;
        String  tempDirPath = Config.getInstance().getPathConfig().getRootPath()
                                    .concat(File.separator)
                                    .concat(getTargetDirectory())
                                    .concat(File.separator)
                                    .concat(System.currentTimeMillis() + "");
        try {
            //解压
            Utils.decompressZip(getZipFilePath(), tempDirPath);
            //验证
            valid = CheckSign.checkSign(new File(tempDirPath.concat(File.separator).concat(getFileName())));
        } catch (IOException e) {
            e.printStackTrace();
            valid = false;
        }finally {
            Utils.deleteFile(tempDirPath);
        }
        return valid;
    }

    /**
     * 检测本地文件是否有效
     *
     * @return boolean
     */
    @Override public boolean checkLocalFile() {
        if (isBundleFile()){
            String path = Config.getInstance().getPathConfig().getRootPath()
                                .concat(File.separator)
                                .concat(getExpandDirectory());
            return CheckSign.checkBundle(path);
        }
        return CheckSign.checkSign(getFile());
    }

    /**
     * 该方法执行操作如下:
     * 1.判断下载下来的文件是否存在；
     * 2.将下载下来的zip文件解压到当前目录；
     * 3.如果该实体的 {@link #expandDirectory} 属性不为空，那么表示执
     *   完第一步后的文件还可以二次解压，并将其解压到 {@link #expandDirectory} 目录中。
     *
     * ps: 执行该方法表示之前已经对下载的文件进行验签了，下载的文件是有效的。
     *
     * @return  解压是否成功
     */
    @Override public boolean decompressEntity(){
        boolean success = false;

        try {
            //#1.
            if (!getDownloadFile().exists()){
                return false;
            }
            //#2.
            success = Utils.decompressZip(getDownloadFile(), "");
            //#3.
            if (!Utils.isEmpty(getExpandDirectory())){
                success = Utils.decompressZip(getFilePath(),
                                              Config.getInstance().getPathConfig().getRootPath()
                                                    .concat(File.separator)
                                                    .concat(getExpandDirectory()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    /**
     * 删除下载文件
     *
     * @return  是否成功
     */
    @Override public boolean deleteDownloadFile() {
        return !getDownloadFile().exists() || Utils.deleteFile(getDownloadFile());
    }

    /**
     * 获取文件内容, 调用该方法前要校验文件有效性
     *
     * @return String
     * @throws Exception
     */
    @Override public String getConfigFileContent() throws Exception{
        return Utils.readFile(getFile());
    }

    /**
     * 获取实体绝对路径的文件
     *
     * @return  File
     */
    @Override public File getFile(){
        return new File(getFilePath());
    }

    /**
     * 获取该实体对应下载的文件
     *
     * @return  File
     */
    @Override public File getDownloadFile(){
        return new File(getZipFilePath());
    }

    /**
     * 获取实体对应文件的绝对路径
     *
     * @return path
     */
    @Override public String getFilePath(){
        return Config.getInstance().getPathConfig().getRootPath()
                .concat(File.separator)
                .concat(getTargetDirectory())
                .concat(File.separator)
                .concat(getFileName());
    }

    /**
     * 获取实体对应的文件，app assets中的路径
     * 由于所有预装文件均为压缩文件，所以要加上.zip后缀。
     * 
     * @return path
     */
    @Override public String getFilePathInAssets(){
        return getBundleDirectory()
                .concat(File.separator)
                .concat(getFileName())
                .concat(ORIGINAL_FILE_SUFFIX);
    }

    /**
     * 获取将预装文件复制到包目录下的zip路径
     *
     * @return path
     */
    @Override public String getZipFilePath(){
        return getFilePath().concat(ORIGINAL_FILE_SUFFIX);
    }

    /**
     * 获取 Entity 中对应本地文件的 MD5
     *
     * @return MD5
     */
    @Override public String getMD5ForEntityPath(){
        if (!getFile().exists()){
            return "";
        }
        return Digest.md5(getFile());
    }

    /**
     * 是否需要更新, 如果本地不存在该文件，那么返回 true
     *
     * @return boolean
     */
    public boolean needUpgrade(){
        if (!getFile().exists()){
            return true;
        }
        String realMD5 = Digest.md5(getFile());
        return !realMD5.equals(getMD5());
    }

    /**
     * 该配置是否为 config 文件
     *
     * @return  boolean
     */
    public boolean isConfigFile(){
        return !getFileSuffix().equals(UPGRADE_SUFFIX) && !getFileSuffix().equals(BUNDLE_SUFFIX);
    }

    /**
     * 该配置是否为 upgrade 文件
     *
     * @return  boolean
     */
    public boolean isUpgradeFile(){
        return getFileSuffix().equals(UPGRADE_SUFFIX);
    }

    /**
     * 该配置是否为 bundle 文件
     *
     * @return  boolean
     */
    public boolean isBundleFile(){
        return getFileSuffix().equals(BUNDLE_SUFFIX);
    }

    /**
     * 获取文件后缀
     *
     * @return suffix
     */
    private String getFileSuffix(){
        return getFileName().substring(getFileName().lastIndexOf("."), getFileName().length());
    }
}

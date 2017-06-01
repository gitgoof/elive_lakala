package com.lakala.core.fileupgrade;

import java.io.File;
import java.io.Serializable;

/**
 * <p>Description  : 实体接口.
 *                   2014/12/23    继承Serializable。</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/10.</p>
 * <p>Time         : 下午8:18.</p>
 */
/*package*/ interface EntityInterface extends Serializable{

    /**
     * 获取更新 URL
     *
     * @return String
     */
    public String getCheckURL();

    /**
     * 获取文件名字（不包含路径，只是文件名字）
     *
     * @return String
     */
    public String getFileName();

    /**
     * 获取目标目录名（不包含路径，只是目录名字）
     *
     * @return  String
     */
    public String getTargetDirectory();

    /**
     * 获取 Entity 对应包目录下文件的 MD5
     *
     * @return MD5
     */
    public String getMD5ForEntityPath();

    /**
     * 复制 assets 中的文件到包中目标路径，并解压
     *
     * @return   是否成功，试过失败表示 assets 中预装中没有路径对应的文件
     */
    public boolean copyAssetFileAndDecompress() throws Exception;

    /**
     * 校验下载的实体是否有效
     *
     * @return boolean
     */
    public boolean checkDownloadFile();

    /**
     * 校验本地实体是否有效
     *
     * @return boolean
     */
    public boolean checkLocalFile();

    /**
     * 解压下载后的实体
     *
     * @return 是否成功
     */
    public boolean decompressEntity();

    /**
     * 删除下载文件
     *
     * @return 是否成功
     */
    public boolean deleteDownloadFile();

    /**
     * 获取配置文件内容
     *
     * @return String
     */
    public String getConfigFileContent() throws Exception;

    /**
     * 获取实体绝对路径的文件
     *
     * @return  File
     */
    public File getFile();

    /**
     * 获取该实体对应下载的文件
     *
     * @return  File
     */
    public File getDownloadFile();

    /**
     * 获取实体对应文件的绝对路径
     *
     * @return path
     */
    public String getFilePath();

    /**
     * 获取实体对应的文件，app 预装 assets中的路径
     *
     * @return path
     */
    public String getFilePathInAssets();

    /**
     * 获取将预装文件复制到包目录下的zip路径
     *
     * @return path
     */
    public String getZipFilePath();
}

package com.lakala.core.fileupgrade;

/**
 * <p>Description  : 保存模块内部所有的 key ,包括 File Entity 的中所有的 key 等.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 上午9:16.</p>
 */
/*package*/ class Keys {


    /*                  FileEntity keys                 */

    //文件 MD5
    public static final String MD5              = "MD5";
    //请求 URL
    public static final String CHECK_URL        = "checkURL";
    //文件名
    public static final String FILE_NAME        = "fileName";
    //是否强制更新
    public static final String FORCE_UPDATE     = "forceUpdate";
    //目标文件夹路径
    public static final String TARGET_DIRECTORY = "targetDirectory";
    //解压文件夹路径
    public static final String EXPAND_DIRECTORY = "expandDirectory";
    //是否需要验签
    public static final String VERIFY_SIGN      = "verifySign";
    //版本
    public static final String VERSION          = "version";
    //预装bundle文件夹
    public static final String BUNDLE_DIRECTORY = "bundleDirectory";
    //文件等级
    public static final String FILE_LEVEl       = "fileLevel";
    //FileEntity 对应的文件状态
    public static final String FILE_STATUS      = "fileStatus";

    //文件实体中，对应获取子实体的 key 值
    public static final String CHILD_KEY        = "config";


}

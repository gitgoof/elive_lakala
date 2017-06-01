package com.lakala.core.fileupgrade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Description  : 文件更新外部调用类.
 *                   类初始化时，需要传入{@link com.lakala.core.fileupgrade.ConfigEntity}实例，该实例主要配置文件
 *                   更新所需要的参数。
 *                   该类主要提供一下方法：
 *                   1.{@link #fileInit()} 文件初始化方法，用于复制所有的文件到 app 包目录下的相应位置
 *                   2.{@link #allCheck()} 检测所有文件是否需要更新
 *                   3.{@link #singleCheck(String...)} 检测单个文件是否需要更新
 *                   4.{@link #read(String...)} 读取文件
 *                   5.{@link #isLocalFileValid()} 检测本地文件是否有效
 *                   6.{@link #getDownloadList()} 获取下载的所有文件列表
 *                   7.{@link #getSuccessEntityList()} 获取所有下载成功的文件列表
 *                   8.{@link #getFailureEntityList()} 获取所有下载失败的文件列表
 *                   9.{@link #getInvalidList()} 获取所有本地无效的文件列表</p>
 *
 *                   文件更新模块相关广播 Action:
 *                   {@link #ACTION_CHECK_ALL_FINISHED}
 *                   {@link #ACTION_CHECK_SINGLE_FINISHED}
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/10.</p>
 * <p>Time         : 下午4:30.</p>
 */
public class FileUpgradeExternalInvoke {

    /*                  文件更新模块相关的广播 Action                 */
    public static final String ACTION_CHECK_ALL_FINISHED     = "com.lakala.fileupgrade.check_all_finished";
    public static final String ACTION_CHECK_SINGLE_FINISHED  = "com.lakala.fileupgrade.check_single_finished";
    public static final String ACTION_LOCAL_FILE_INVALIDED   = "com.lakala.fileupgrade.local_file_invalided";

    private Context             context;
    private Config              config;
    private DownloadManager     downloadManager;
    private AlarmManager        alarmManager;

    private final Vector<FileEntity> invalidList = new Vector<FileEntity>();

    private static FileUpgradeExternalInvoke instance;

    private FileUpgradeExternalInvoke(ConfigEntity config){
        Config.init(config);

        this.context         = Config.getInstance().getContext();
        this.config          = Config.getInstance();
        this.downloadManager = DownloadManager.getInstance();
    }

    /**
     * FileUpgradeExternalInvoke 类是单例模式的，需要用此方法获取 FileUpgradeExternalInvoke 实例。
     * @param config 第一次获取实例时需要传递该参数，后续可以传null。
     * @return FileUpgradeExternalInvoke 实例
     */
    public static synchronized FileUpgradeExternalInvoke getInstance(ConfigEntity config){

        if (instance == null){
            if (config == null){
                throw new IllegalArgumentException("ConfigEntity must not be null.");
            }

            instance = new FileUpgradeExternalInvoke(config);
        }

        return instance;
    }

    /**
     * 获取正在更新或将要更新的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入当前需要更新的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getDownloadList(){
        return downloadManager.getDownloadList();
    }

    /**
     * 获取已经更新成功的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入本次更新成功的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getSuccessEntityList(){
        return downloadManager.getSuccessEntityList();
    }

    /**
     * 获取更新失败的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入本次更新失败的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getFailureEntityList(){
        return downloadManager.getFailureEntityList();
    }

    /**
     * 获取无效文件列表，保存执行{@link #isLocalFileValid()}后所有无效的实体
     *
     * @return List
     */
    public Vector<FileEntity> getInvalidList(){
        return invalidList;
    }

    /**
     * 获取当前 PathConfig 实体
     *
     * @return {@link com.lakala.core.fileupgrade.Config.PathConfig}
     */
    public Config.PathConfig getPathConfig() {
        return config.getPathConfig();
    }

    /**
     * 文件初始化，复制 assets 中所有文件到相应包路径，并且解压。
     * 该方法是同步的，耗时的
     * 1.复制主升级描述文件到 ”data/data/.../assets/...“ 目录中
     * 2.迭代复制升级描文件中所有文件。
     */
    public void fileInit(){

        FileMainEntity mainEntity = config.getMainEntity();
        try {
            //移动主配置文件
            mainEntity.copyAssetFileAndDecompress();
            //迭代移动其他文件
            iterationAllChildWithConfig(mainEntity);

        } catch (Exception e) {
            LOG.e(e.getMessage(), e);
        }
    }

    /**
     * 检查更新由主升级描述文件（main.upgrade）、子升级描述文件列表出的所有文件。
     */
    public void allCheck(){
        //如果当前正在检测所有文件，那么 return
        if (downloadManager.isAllChecking()){
            return;
        }

        downloadManager.setAllChecking(true);

        //清空列表
        downloadManager.clear();

        Intent intent = new Intent(context, FileUpgradeService.class);
        intent.putExtra(FileUpgradeService.ACTION_KEY, FileUpgradeService.ACTION_ALL_CHECK);
        intent.putExtra(FileUpgradeService.TAG_KEY, new String[]{});
        context.startService(intent);
    }

    /**
     * 查检更新单个文件
     * 1.如果检测主升级描述文件（main.upgrade）则 tag 参数可不传。
     * 2.如果更新主升级描述文件中的某一项则真接传递该项的 tag 值。
     * 3.如果更新子升级描述文件中的某一项则首先传子描述升级文件的tag，然后在传递目标 tag。比如
     *   检查 webapp 中的 resource 文件：singleCheck("webapp","resource");
     *
     * @param tag String[]
     */
    public void singleCheck(String... tag){
        //如果当前没有检测单个文件，那么清空列表
        if (!downloadManager.isSingleChecking()){
            downloadManager.setSingleChecking(true);
            //清空列表
            downloadManager.clear();
        }

        Intent intent = new Intent(context, FileUpgradeService.class);
        intent.putExtra(FileUpgradeService.ACTION_KEY, FileUpgradeService.ACTION_SINGLE_CHECK);
        intent.putExtra(FileUpgradeService.TAG_KEY, tag);
        context.startService(intent);
    }

    /**
     * 重新下载下载失败的 FileEntity ，不会清空当前下载列表。
     * 该方法主要用于，检测更新所有文件或单个文件后，想重新下载当前失败列表中的实体的方法。
     *
     * @param entities  下载失败列表的 Entity list
     */
    public void checkFailureEntity(Vector<FileEntity> entities){
        for (FileEntity entity : entities){
            downloadManager.remove(entity);
        }

        downloadManager.setAllChecking(true);

        Intent intent = new Intent(context, FileUpgradeService.class);
        intent.putExtra(FileUpgradeService.ACTION_KEY, FileUpgradeService.ACTION_ALL_CHECK);
        intent.putExtra(FileUpgradeService.TAG_KEY, new String[]{});
        context.startService(intent);
    }

    /**
     * 文本方式打开由 tag 指出的文件。
     * @param tag  tag  用法请参考 {@link #singleCheck(String...)}
     * @return  String
     */
    public String read(String... tag){
        ReadConfigHandler reader = new ReadConfigHandler();
        FileUpgrade.getInstance().check(false, reader, tag);
        return reader.getContent();
    }

    /**
     * 检测由主升级描述文件（main.upgrade）、子升级描述文件列表出的所有文件是否有效。
     * 验签不过视为文件无效。
     * 无效的文件会放入一个列表中，可以使用 {@link #getInvalidList()} 方法获取该列表。
     */
    public boolean isLocalFileValid(){
        //清空无效列表
        invalidList.clear();

        FileMainEntity mainEntity = config.getMainEntity();
        try {
            if (!mainEntity.checkLocalFile()){
                mainEntity.copyAssetFileAndDecompress();
            }

            if (!iterationCheckLocalFile(mainEntity)){
                return false;
            }
        } catch (Exception e) {
            LOG.e(e.getMessage(), e);
        }

        return true;
    }

    /**
     * 启动检测本地文件有效性服务
     *
     * @param intervalMillis 检测间隔，具体检测间隔时间会在改值上下浮动
     */
    public void startCheckLocalFileService(long intervalMillis){

                      alarmManager   = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent        intent         = new Intent(context, CheckLocalFileReceiver.class);

        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalMillis, pendingIntent);
    }

    /**
     * 停止检测本地文件有效性服务
     */
    public void stopCheckLocalFileService(){
        if (alarmManager == null){
            return;
        }
        Intent        intent         = new Intent(context, CheckLocalFileReceiver.class);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * 迭代实体，校验文件有效性
     *
     * @param entity    Entity
     * @return          boolean
     * @throws Exception
     */
    private boolean iterationCheckLocalFile(EntityInterface entity) throws Exception {
        iterationEntity(entity, 1);
        return invalidList.size() == 0;
    }

    /**
     * 迭代移动实体中的相应配置文件
     *
     * @param entity        Entity
     * @throws Exception
     */
    private void iterationAllChildWithConfig(EntityInterface entity) throws Exception {
        iterationEntity(entity, 0);
    }

    /**
     * 迭代实体
     *
     * @param entity            Entity
     * @param type              操作类型
     *                          0 : 复制预装 assets 中的文件到包目录
     *                          1 : 校验包目录下文件有效性
     * @throws Exception
     */
    private void iterationEntity(EntityInterface entity, int type) throws Exception{

        JSONObject file   = new JSONObject(entity.getConfigFileContent());
        JSONObject config = file.optJSONObject(Keys.CHILD_KEY);

        Iterator<String> keys = config.keys();

        while (keys != null && keys.hasNext()){

            String key    = keys.next();
            FileEntity en = new FileEntity(key, config.optJSONObject(key));

            //检测当前实体对应的文件, 是否移动成功
            if (type == 0){
                en.copyAssetFileAndDecompress();
            }
            //检测当前实体对应的文件, 是否有效
            if (type == 1 && !en.checkLocalFile()){
                invalidList.add(en);
            }

            //如果当前文件为 .upgrade 文件，那么检测子文件
            if (en.isUpgradeFile()){
                if (type == 0){
                    iterationAllChildWithConfig(en);
                }
                if (type == 1){
                    iterationCheckLocalFile(en);
                }
            }
        }
    }


    /*                  读取文件                    */
    private final class ReadConfigHandler extends FileUpgradeDelegate {

        //文件内容
        private String content;

        @Override public void onLocalExist(EntityInterface entity) {
            try {
                content = entity.getConfigFileContent();
            } catch (Exception e) {
                LOG.e(e.getMessage(), e);
                content = "";
            }
        }

        @Override public void onError(String id, Object data) {
            LOG.e("error message: %s , line : %s", id, data.toString());
        }

        /**
         * 获取文件内容，如果读取的配置文件存在
         *
         * @return String
         */
        public String getContent(){
            return content;
        }
    }
}

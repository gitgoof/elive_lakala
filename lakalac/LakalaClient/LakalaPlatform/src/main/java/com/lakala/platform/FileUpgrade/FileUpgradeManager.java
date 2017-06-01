package com.lakala.platform.FileUpgrade;

import android.content.Context;

import com.lakala.core.fileupgrade.ConfigEntity;
import com.lakala.core.fileupgrade.FileEntity;
import com.lakala.core.fileupgrade.FileUpgradeExternalInvoke;
import com.lakala.library.jni.LakalaNative;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.config.Config;

import org.json.JSONObject;

import java.util.Vector;

/**
 * <p>Description  : 平台层文件更新管理类.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/19.</p>
 * <p>Time         : 下午2:30.</p>
 */
public class FileUpgradeManager {

    public static final String ACTION_CHECK_ALL_FINISHED     = FileUpgradeExternalInvoke.ACTION_CHECK_ALL_FINISHED;
    public static final String ACTION_CHECK_SINGLE_FINISHED  = FileUpgradeExternalInvoke.ACTION_CHECK_SINGLE_FINISHED;
    public static final String ACTION_LOCAL_FILE_INVALIDED   = FileUpgradeExternalInvoke.ACTION_LOCAL_FILE_INVALIDED;

    private FileUpgradeExternalInvoke externalInvoke;

    private static FileUpgradeManager instance;

    private FileUpgradeManager(){
        init();
    }

    /*                  init                    */
    private void init(){


        Context context = ApplicationEx.getInstance();

        ConfigEntity config = new ConfigEntity();
        config.setDEBUG(true);
        config.setContext(context);
        config.setRequestTimeout(90*1000);
        config.setPublicKey(LakalaNative.getLoginPublicKey());
        config.setRequestBaseUrl("");
        config.setRequestParams(new String[]{
                "_Platform", "android",
                "_AppVersion", "38",//AppUtil.getInstance(context).getAppVersionCode(),
                "_SubChannelId", "Sb"//BusinessRequestParams.SUB_CHANNEL_ID
        });

        externalInvoke = FileUpgradeExternalInvoke.getInstance(config);
    }

    public static synchronized FileUpgradeManager getInstance(){

        if (instance == null){
            instance = new FileUpgradeManager();
        }

        return instance;
    }

    /**
     * 初始化方法
     *
     * @param isFirst 是否为第一次
     */
    public void init(boolean isFirst){
        if (isFirst){
            externalInvoke.fileInit();
        }else {
            boolean isValid = externalInvoke.isLocalFileValid();
            //如果本地文件有效，则检测所有文件是否需要更新
            //若本地文件无效，则检测本地有效性服务会提示用户下载官方正版app
            if (isValid){
                //检测所有文件是否需要更新
                externalInvoke.allCheck();
            }
        }
//        启动检测本地文件有效性服务
        externalInvoke.startCheckLocalFileService(10*60*1000);
    }

    /**
     * 读取文件
     *
     * @param tag 读取文件对应的 tag
     * @return    String
     */
    public String read(String... tag){
        return externalInvoke.read(tag);
    }

    /**
     * 停止检测本地文件有效性服务
     */
    public void stopCheckLocalFileService(){
        externalInvoke.stopCheckLocalFileService();
    }

    /**
     * 获取更新提示文字
     *
     * @return String
     */
    public String getUpdateMessage(){
        JSONObject web = ConfigFileManager.getInstance().readWebAppConfig();
        return web.optString("updateMsg");
    }

    /**
     * 获取正在更新或将要更新的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入当前需要更新的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getDownloadList(){
        return externalInvoke.getDownloadList();
    }

    /**
     * 获取已经更新成功的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入本次更新成功的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getSuccessEntityList(){
        return externalInvoke.getSuccessEntityList();
    }

    /**
     * 获取更新失败的文件列表。
     * 调用 allCheck、singleCheck 会清空列表中的旧数据，然后存入本次更新失败的文件。
     *
     * @return List
     */
    public Vector<FileEntity> getFailureEntityList(){
        return externalInvoke.getFailureEntityList();
    }

    /**
     * 获取无效文件列表，保存执行检查本地文件后所有无效的实体
     *
     * @return List
     */
    public Vector<FileEntity> getInvalidList(){
        return externalInvoke.getInvalidList();
    }

}

package com.lakala.core.fileupgrade;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Description  : 文件更新服务.
 *
 *  ***2014/12/24*** 增加判断，如果上次检测更新完成后有更新失败的文件，那么本次更新直接下载上次失败的
 *                   文件，前提是主配置文件不是新下载的文件。</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/11.</p>
 * <p>Time         : 上午11:12.</p>
 */
public class FileUpgradeService extends Service{
    //tag key
    public static final String TAG_KEY              = "tag_key";
    //action key
    public static final String ACTION_KEY           = "action_key";
    //执行单个文件检测
    public static final String ACTION_SINGLE_CHECK  = "action_single_check";
    //对所有文件进行检测
    public static final String ACTION_ALL_CHECK     = "action_all_check";

    private static final String HANDLER_THREAD_NAME = "FileUpgrade";

    private ServiceHandler serviceHandler;

    /*                  服务消息处理                  */
    private final class ServiceHandler extends Handler{

        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override public void handleMessage(Message msg) {

            int      startId  = msg.arg1;
            String[] tag      = (String[]) msg.obj;

            switch (msg.what){
                //all check
                case 1:
                    checkAll(startId, tag);
                    break;
                //single check
                case 2:
                    checkFile(startId, tag);
                    break;
            }

        }
    }

    @Override public void onCreate() {
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        serviceHandler = new ServiceHandler(handlerThread.getLooper());
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        unpackSendMessage(intent, flags, startId);

        //保证服务被异常 kill 的时候，重启服务，并传入 intent
        return START_REDELIVER_INTENT;
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 解包，并发送消息
     */
    private void unpackSendMessage(Intent intent, int flags, int startId){

        String action = intent.getStringExtra(ACTION_KEY);

        Message message = serviceHandler.obtainMessage();

        switch (action){
            case ACTION_SINGLE_CHECK:
                message.what = 2;
                break;
            case ACTION_ALL_CHECK:
                message.what = 1;
                break;
        }

        message.arg1 = startId;
        message.obj  = intent.getStringArrayExtra(TAG_KEY);

        serviceHandler.sendMessage(message);
    }

    /**
     * check 单个文件
     *
     * @param startId  当前服务 ID
     * @param tag      tag
     */
    private void checkFile(int startId, String... tag){
        FileUpgrade.getInstance().check(new FileUpgradeHandler(startId), tag);
    }

    /**
     * check 所有文件
     *
     * @param startId  当前服务 ID
     * @param tag      tag
     */
    private void checkAll(int startId, String... tag){
        FileUpgrade.getInstance().check(new FileUpgradeHandler(startId, true), tag);
    }

    /**
     * 当全部检测完成后发送
     */
    private void sendAllCheckEndBroadcast(){
        DownloadManager.getInstance().setAllChecking(false);
        Intent intent = new Intent(FileUpgradeExternalInvoke.ACTION_CHECK_ALL_FINISHED);
        sendStickyBroadcast(intent);
    }

    /**
     * 当单个文件检测完成后发送
     */
    private void sendSingCheckEndBroadcast(){
        DownloadManager.getInstance().setSingleChecking(false);
        Intent intent = new Intent(FileUpgradeExternalInvoke.ACTION_CHECK_SINGLE_FINISHED);
        sendStickyBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        LOG.e("============= Check Service has been destroyed ==============");
        super.onDestroy();
    }

    /*              文件更新处理器                 */
    private final class FileUpgradeHandler extends FileUpgradeDelegate {

        //启动服务 ID
        private int     startId;
        //当前是否为检测所有文件模式。
        private boolean handleCheckAll;

        public FileUpgradeHandler(int startId){
            this.startId         = startId;
            this.handleCheckAll  = false;
        }

        public FileUpgradeHandler(int startId, boolean handleCheckAll){
            this.startId         = startId;
            this.handleCheckAll  = handleCheckAll;
        }

        @Override public void onStart(EntityInterface entity) {
        }

        @Override public void onProgress(int bytesWritten, int totalSize, EntityInterface entity) {
        }

        @Override public void onSuccess(EntityInterface entity) {
            handleSuccess(entity);
        }

        @Override public void onFailure(EntityInterface entity) {
        }

        @Override public void onFinish(EntityInterface entity) {
            handleFinish(entity);
        }

        @Override public void onError(String id, Object data) {
            LOG.e("error message: %s , line : %s", id, data.toString());
        }

        /**
         * 处理检测成功
         *
         * @param entity Entity
         */
        private void handleSuccess(EntityInterface entity){
            if (handleCheckAll && entity instanceof FileEntity){
                FileEntity en = (FileEntity) entity;
                //如果当前更新成功文件为 .upgrade 文件，那么直接解压，以便后面迭代检测子文件
                if (en.isUpgradeFile() && en.isNewDownload()){
                    en.decompressEntity();
                }
            }

        }

        /**
         * 处理检测结束
         *
         * @param entity Entity
         */
        private void handleFinish(EntityInterface entity){
            //处理检测所有文件
            if (handleCheckAll){
                boolean isFileMainEntity = entity instanceof FileMainEntity;
                boolean isFileEntity     = entity instanceof FileEntity;

                //主配置文件
                if (isFileMainEntity){
                    try {
                        FileMainEntity mainEntity = (FileMainEntity) entity;
                        //以下两种情况，直接发送广播完成文件更新校验
                        //1.当前使用的主配置文件不是下载下来的;
                        //2.上次下载任务完成但是有下载失败的文件.
                        if (!mainEntity.isNewDownload() && !handleLast()){
                            return;
                        }

                        //主配置文件检测完成后，迭代所有子文件
                        iterationMain(startId, mainEntity);

                    } catch (Exception e) {
                        LOG.e(e.getMessage(), e);
                    }

                    return;
                }

                //其他文件
                if (isFileEntity) {
                    FileEntity en = (FileEntity) entity;

                    //如果当前文件为 .upgrade 文件，那么迭代检测其子文件
                    if (en.isUpgradeFile()) {
                        try {
                            iterationUpgrade(startId, en);
                        } catch (Exception e) {
                            LOG.e(e.getMessage(), e);
                        }
                    }
                    //检测完成一个文件后，执行remove
                    DownloadManager.getInstance().removeCheckCount();

                    boolean isFinished = DownloadManager.getInstance().allCheckFinish();
                    LOG.e("check size : " + DownloadManager.getInstance().getCheckList().size());
                    //判断所有文件下载完成（完成状态包括下载成功和失败）后，发送检测完成广播
                    if (isFinished){

                        //保存本次所有下载列表（包括成功,失败和下载总表）
                        DownloadManager.getInstance().saveLastList();

                        //发送广播，所有文件校验完成
                        sendAllCheckEndBroadcast();
                        stopSelf(startId);
                    }
                }

            }
            //处理检测单个文件
            else {
                //发送广播，检验单个文件完成
                sendSingCheckEndBroadcast();
                //本次请求结束，
                stopSelf(startId);
            }
        }

        /**
         * 处理上次任务
         * 判断上次下载任务完成后是否有下载失败的文件
         *
         * @return true 表示没有以上情况，false 表示有。
         */
        private boolean handleLast(){
            Vector<FileEntity> downloadList = DownloadManager.getInstance().getSaveDownloadList();
            Vector<FileEntity> successList  = DownloadManager.getInstance().getSaveSuccessList();
            Vector<FileEntity> failureList  = DownloadManager.getInstance().getSaveFailureList();

            int downloadLen = downloadList.size();
            int successLen  = successList.size();
            int failureLen  = failureList.size();

            //如果下载列表长度为0 或 下载失败列表为0，那么：
            //1.上次没有需要更新的文件；
            //2.这两个列表对应的文件被删除。
            if (downloadLen == 0 || failureLen == 0){
                return true;
            }

            //如果成功列表为0 且下载列表和失败列表 size 不相等，那么：
            //1.成功列表被删除了或被篡改了；
            if (successLen == 0 && downloadLen != failureLen){
                return true;
            }


            for (FileEntity failureEn : failureList){
                //添加所有失败文件
                DownloadManager.getInstance().addCheckCount();

                //在 download 列表中删除之前失败的实体
                String fileName = failureEn.getFileName();
                for (FileEntity downloadEn : downloadList){
                    if (downloadEn.getFileName().equals(fileName)){
                        downloadList.remove(downloadEn);
                    }
                }
            }
            //将当前列表状态存入下载列表中
            DownloadManager.getInstance().addDownloadEntityList(downloadList);
            DownloadManager.getInstance().addSuccessEntityList(successList);

            //检测更新
            for (FileEntity failureEn : failureList){
                //如果父 tag 为主配置文件
                if (failureEn.getParentTag().equals(FileMainEntity.TAG)){
                    checkAll(startId, failureEn.getTag(), failureEn.getChildTag());
                }else {
                    checkAll(startId, failureEn.getParentTag(), failureEn.getTag());
                }
            }

            return false;
        }

        /**
         * 迭代校验 main 中子文件
         */
        private void iterationMain(int startId, FileMainEntity entity) throws Exception {

            JSONObject file   = new JSONObject(entity.getConfigFileContent());
            JSONObject config = file.optJSONObject(Keys.CHILD_KEY);

            JSONArray arr = config.names();
            for (int i = 0; i < arr.length(); i++){
                //添加所有 main 文件中的子文件
                DownloadManager.getInstance().addCheckCount();
            }

            Iterator keys = config.keys();

            while (keys != null && keys.hasNext()){
                String key    = (String) keys.next();
                checkAll(startId, key);
            }
        }

        /**
         * 迭代校验 .upgrade 文件中子文件
         */
        private void iterationUpgrade(int startId, FileEntity entity) throws Exception {

            JSONObject file   = new JSONObject(entity.getConfigFileContent());
            JSONObject config = file.optJSONObject(Keys.CHILD_KEY);

            JSONArray arr = config.names();
            for (int i = 0; i < arr.length(); i++){
                //添加所有 .upgrade 文件中的子文件
                DownloadManager.getInstance().addCheckCount();
            }

            Iterator keys = config.keys();

            while (keys != null && keys.hasNext()){

                String key    = (String) keys.next();

                checkAll(startId, entity.getTag(), key);
            }
        }

    }

}

package com.lakala.platform.FileUpgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.lakala.core.fileupgrade.FileEntity;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.StatisticManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <p>Description  : 文件检测完成处理.
 *                   1.检测所有文件是否需要更新.有需要更新的文件时，
 *                   所有文件处理完成后会发送 {@link FileUpgradeManager#ACTION_CHECK_ALL_FINISHED}
 *                   广播；若没有一个需要更新的文件时，所有文件处理完成后也会发送该广播。
 *                   2.检测单个文件是否需要更新，处理完成后
 *                   会发送 {@link FileUpgradeManager#ACTION_CHECK_SINGLE_FINISHED}
 *                   广播。</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/19.</p>
 * <p>Time         : 下午4:50.</p>
 */
public class UpgradeResultHandler extends BroadcastReceiver {

    private FragmentActivity activity;

    public UpgradeResultHandler(FragmentActivity activity){
        this.activity = activity;
    }

    @Override public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equalsIgnoreCase(FileUpgradeManager.ACTION_CHECK_ALL_FINISHED)){
            handleAllCheck();
        }
        else if (action.equalsIgnoreCase(FileUpgradeManager.ACTION_CHECK_SINGLE_FINISHED)){
            handleSingCheck();
        }

        context.removeStickyBroadcast(intent);
    }

    /**
     * 处理所有文件检测完成
     */
    private void handleAllCheck(){
        final Vector<FileEntity> downloadList = FileUpgradeManager.getInstance().getDownloadList();
        final Vector<FileEntity> successList  = FileUpgradeManager.getInstance().getSuccessEntityList();
        final Vector<FileEntity> failureList  = FileUpgradeManager.getInstance().getFailureEntityList();

        LogUtil.print("FileUpgrade", "All Check Finished : downloadList : " + downloadList.size() +
                ", successList : " + successList.size() +
                ", failureList : " + failureList.size());
        //1.处理有文件需要下载且需要下载的所有文件下载成功
        if (successList.size() != 0 && downloadList.size() == successList.size() && failureList.size() == 0){
            //统计下载成功
            statisticsFileUpgrade("FileUpgradeSuccess", getDesc(successList));

            UpgradeFinishedHelper.showAskInstallDialog();
        }

        //2.处理有文件需要下载且需要下载的文件中有下载失败的文件，现在暂时不重新发送
        if (failureList.size() != 0 && downloadList.size() == (successList.size() + failureList.size())){
            //如果重新下载只需调用  checkFailureEntity(Vector<FileEntity> entities)
            //统计下载失败
            statisticsFileUpgrade("FileUpgradeFailure", getDesc(failureList));
        }

    }

    /**
     * 处理单个文件下载完成
     */
    private void handleSingCheck(){
        final Vector<FileEntity> downloadList = FileUpgradeManager.getInstance().getDownloadList();
        final Vector<FileEntity> successList  = FileUpgradeManager.getInstance().getSuccessEntityList();
        final Vector<FileEntity> failureList  = FileUpgradeManager.getInstance().getFailureEntityList();

        LogUtil.print("FileUpgrade", "Single Check Finished : downloadList : " + downloadList.size() +
                ", successList : " + successList.size() +
                ", failureList : " + failureList.size());

        //处理下载成功, 只处理异步把非 bundle 文件解压
        if (successList.size() != 0 && downloadList.size() == successList.size() && failureList.size() == 0) {

            statisticsFileUpgrade("FileUpgradeSuccess", getDesc(successList));

            Intent intent = new Intent(activity, HandleResultService.class);
            intent.putExtra(HandleResultService.ACTION_KEY, HandleResultService.CHECK_SINGLE_SUCCESS);
            activity.startService(intent);
        }

        //处理有下载失败文件的情况，现在暂时不重新发送
        if (failureList.size() != 0 && downloadList.size() == (successList.size() + failureList.size())){
            //如果重新下载只需调用  checkFailureEntity(Vector<FileEntity> entities)
            statisticsFileUpgrade("FileUpgradeFailure", getDesc(failureList));
        }

    }

    /**
     * 获取描述
     *
     * @param vector    list
     * @return          String
     */
    private String getDesc(Vector<FileEntity> vector){
        StringBuilder builder = new StringBuilder();

        builder.append("文件名:");
        for (FileEntity entity : vector){
            builder.append(entity.getFileName());
            builder.append(",");
        }
        String end = "共" + vector.size() + "个文件。";

        builder.append(end);

        return builder.toString();
    }

    /**
     * 统计更新结果
     *
     * @param label label
     * @param desc  内容描述
     */
    private void statisticsFileUpgrade(String label, String desc){
        User user = ApplicationEx.getInstance().getUser();
        String userMobile = "";
        if (user != null) {
            userMobile = user.getLoginName();
        }
        Map<String,String> stringHashMap = new HashMap<String, String>();
        stringHashMap.put("desc", desc);
//        StatisticManager.getInstance(activity).onEvent("FileUpgrade", label, "1", "", userMobile, stringHashMap);

    }
}
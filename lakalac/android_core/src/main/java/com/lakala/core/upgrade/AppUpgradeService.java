package com.lakala.core.upgrade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.lakala.core.R;
import com.lakala.core.fileupgrade.Config;
import com.lakala.core.http.DefaultHttpResponseHandler;
import com.lakala.core.http.HttpRequest;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.FileUtil;
import com.loopj.lakala.http.AsyncHttpClient;
import com.loopj.lakala.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.IOException;

/**
 * app升级服务
 * <p/>
 * 启动app升级服务
 * 启动方式一：（执行顺序1,2），启动方式二（执行2）
 * 1.bindService  提供下载apk包进度监听服务方法（可以不启动绑定）
 * 2.startService 开启下载服务，发送通知
 * <p/>
 * 关闭服务：
 * 如果启动（1，2），请执行unbindService，stopService
 * 如果只启动（2），下载完成或失败会自动终止服务
 * <p/>
 * Created by jerry on 13-12-16.
 * modified by LL on 15-1-6.
 */
public class AppUpgradeService extends Service {

    /**
     * 采用原生下载客户端,减少对其他模块的依赖
     */
    private static AsyncHttpClient CLIENT;

    /**
     * 参数传递key值定义
     */
    public static final String KEY_URL = "url";

    /**
     * 通知ID
     */
    private static final int NOTIFICATION_CODE = 99;

    /**
     * 通知管理器
     */
    private NotificationManager mNotificationManager;

    /**
     * 通知管理
     */
    private NotificationCompat.Builder builder;

    /**
     * 升级下载apk包进度监听回调
     */
    private OnUpgradeProgressListener mOnUpgradeProgressListener;

    public void setOnUpgradeProgressListener(OnUpgradeProgressListener onUpgradeProgressListener) {
        this.mOnUpgradeProgressListener = onUpgradeProgressListener;
    }

    /**
     * 升级状态
     */
    private UpgradeStatus upgradeStatus = null;

    /**
     * 升级状态
     */
    private enum UpgradeStatus {
        start, progress, success, fail
    }

    /**
     * service与Activity通讯
     */
    private UpgradeServiceBinder mUpgradeServiceBinder = new UpgradeServiceBinder();

    /**
     * service与Activity通讯
     */
    public class UpgradeServiceBinder extends Binder {

        /**
         * @return 在绑定服务的情况下, 通过Binder暴露Service实例供外部调用
         */
        public AppUpgradeService getService() {
            return AppUpgradeService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mUpgradeServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra(KEY_URL);
        //已经下载apk ,不需要重复下载
        if (TextUtils.isEmpty(url) || upgradeStatus == UpgradeStatus.start || upgradeStatus == UpgradeStatus.progress) {
            return START_NOT_STICKY;
        }
        //开始下载
        startUpgrade(url);
        return START_REDELIVER_INTENT;
    }

    /**
     * 开始升级下载apk
     *
     * @param urlStr apk下载地址
     */
    public void startUpgrade(String urlStr) {
        //下载apk文件
        File file = createApkFile(urlStr);
        if (file == null) {
            failNotify();
            stopSelf();
            return;
        }
//        HttpRequest httpRequest = getDownloadRequest(urlStr, file);
//        httpRequest.setIHttpRequestEvents(new UpgradeResponseHandler());
//        httpRequest.execute();
        if(CLIENT==null){
            new AsyncHttpClient(getApplicationContext());
        }
        CLIENT.get(urlStr, new UpgradeResponseHandler(file));
    }

    /**
     * 获取保存apk文件
     *
     * @param url 文件下载地址
     */
    private File createApkFile(String url) {
        //创建文件夹
        String path = FileUtil.sdCardAvailable() ? FileUtil.getSDCardPath() + File.separator + "lakala" : FileUtil.getInnerPath(getApplicationContext()) + File.separator + "lakala";
        if (!FileUtil.createDirectory(path)) {
            return null;
        }
        //创建文件
        String name = url.substring(url.lastIndexOf("/"));
        File file = new File(path + File.separator + name);
        try {
            FileUtil.deleteFile(file);
            if (file.createNewFile()) {
                return file;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 设置http请求
     *
     * @param url  请求url
     * @param file 下载成功后文件保存位置
     * @return HttpRequest
     */
    private HttpRequest getDownloadRequest(String url, File file) {
        HttpRequest httpRequest = new HttpRequest(null);
        DefaultHttpResponseHandler responseHandler = (DefaultHttpResponseHandler) httpRequest.getResponseHandler();
        HttpRequestParams httpRequestParams = httpRequest.getRequestParams();
        //设置参数
        httpRequestParams.put("Accept-Encoding", "gzip, deflate");
        httpRequestParams.put("Content-Type", "text/plain;charset=UTF-8");
        //设置请求url
        httpRequest.setRequestURL(url);
        //设置请求方式
        httpRequest.setRequestMethod(HttpRequest.RequestMethod.GET);
        //设置下载类型为文件
        responseHandler.setResponseDataType(DefaultHttpResponseHandler.RESPONSE_DATA_TYPE_FILE);
        responseHandler.setFile(file);

        return httpRequest;
    }

    /**
     * 发送通知
     */
    private void sendNotify() {
        if (mOnUpgradeProgressListener != null) {
            mOnUpgradeProgressListener.onStart();
            return;
        }

//        Notification notification = new Notification(
//            android.R.drawable.stat_sys_download,
//            getString(R.string.core_download_lakala),
//            System.currentTimeMillis());
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_upgrade);
//        remoteViews.setImageViewResource(R.id.id_upgrade_image_logo, getApplicationInfo().icon);
//        remoteViews.setTextViewText(R.id.id_upgrade_prompt_text, getString(R.string.core_download_lakala));
//        remoteViews.setProgressBar(R.id.id_upgrade_progressBar, 100, 0, false);
//        notification.contentView = remoteViews;
//        notification.flags = Notification.FLAG_NO_CLEAR;

        if (builder == null) {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setOngoing(true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.core_upgrade_lakala))
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(getString(R.string.core_download_lakala))
                .setContentText("")
                .setProgress(100, 0, true);

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(NOTIFICATION_CODE, builder.build());
    }

    /**
     * 更新通知(提前生成RemoteView对象,防止update通知造成通知栏卡死现象)
     */
    private void updateNotify(int progress) {
        if (mOnUpgradeProgressListener != null) {
            mOnUpgradeProgressListener.onProgress(progress);
            return;
        }

//        Notification notification = new Notification(
//            android.R.drawable.stat_sys_download,
//            getString(R.string.core_downloading_lakala),
//            System.currentTimeMillis());
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_upgrade);
//        remoteViews.setImageViewResource(R.id.id_upgrade_image_logo, getApplicationInfo().icon);
//        remoteViews.setTextViewText(R.id.id_upgrade_prompt_text,String.format(getString(R.string.core_downloading_progress),progress) + "%");
//        remoteViews.setProgressBar(R.id.id_upgrade_progressBar, 100, progress, false);
//        notification.contentView = remoteViews;
//        notification.flags = Notification.FLAG_NO_CLEAR;

        builder.setDefaults(0)
                .setOngoing(true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.core_upgrade_lakala))
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(getString(R.string.core_downloading_lakala))
                .setContentText(String.format(getString(R.string.core_downloading_progress), progress) + "%")
                .setProgress(100, progress, false)
                .setContentInfo(progress + "%");
        mNotificationManager.notify(NOTIFICATION_CODE, builder.build());
    }

    /**
     * 完成通知
     */
    private void completeNotify(File file) {
        if (mOnUpgradeProgressListener != null) {
            mOnUpgradeProgressListener.onComplete(file);
            return;
        }
//        Notification notification = new Notification(android.R.drawable.stat_sys_download_done,getString(R.string.core_downloading_lakala),System.currentTimeMillis());
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_upgrade);
//        remoteViews.setImageViewResource(R.id.id_upgrade_image_logo, getApplicationInfo().icon);
//        remoteViews.setTextViewText(R.id.id_upgrade_prompt_text,getString(R.string.core_download_lakala_complete));
//        remoteViews.setProgressBar(R.id.id_upgrade_progressBar, 100, 100, false);
//        notification.contentView = remoteViews;
//        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(false)
                .setAutoCancel(true)
                .setTicker(getString(R.string.core_download_lakala_complete))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(getString(R.string.core_download_lakala_complete))
                .setContentText(getString(R.string.core_download_lakala_complete_prompt))
                .setProgress(100, 100, false)
                .setContentInfo(100 + "%")
                .setContentIntent(pendingIntent);
        mNotificationManager.notify(NOTIFICATION_CODE, builder.build());

        installApk(file);
    }

    /**
     * 错误通知
     */
    private void failNotify() {
        if (mOnUpgradeProgressListener != null) {
            mOnUpgradeProgressListener.onFailure();
            return;
        }

//        Notification notification = new Notification(android.R.drawable.stat_sys_download_done,getString(R.string.core_download_lakala_fail),System.currentTimeMillis());
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_upgrade_fail);
//        remoteViews.setImageViewResource(R.id.id_upgrade_fail_image_logo, getApplicationInfo().icon);
//        remoteViews.setTextViewText(R.id.id_upgrade_fail_prompt_text, getString(R.string.core_download_lakala_fail));
//        notification.contentView = remoteViews;
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        mNotificationManager.notify(NOTIFICATION_CODE, notification);

        builder.setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(false)
                .setAutoCancel(true)
                .setTicker(getString(R.string.core_download_lakala_fail))
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(getString(R.string.core_download_lakala_fail))
                .setContentText(getString(R.string.core_download_lakala_fail))
                .setProgress(100, 0, true)
                .setContentInfo(0 + "%");
        mNotificationManager.notify(NOTIFICATION_CODE, builder.build());
    }

    /**
     * 下载结束通知,注意  如果是绑定启动的服务,需要外部通过回掉函数手动调用unbind函数停止service;
     * 如果是startService则不需要关系,下载完成(成功或者失败)都会自动停止服务
     */
    private void finishNotify() {
        if (mOnUpgradeProgressListener != null) {
            mOnUpgradeProgressListener.onFinished();
            return;
        }
        this.stopSelf();
    }

    /**
     * 安装下载后的apk文件
     */
    public void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    /**
     * 下载升级包进度监听器
     */
    public interface OnUpgradeProgressListener {
        /**
         * 开始升级
         */
        void onStart();

        /**
         * 更新升级包进度
         */
        void onProgress(int progress);

        /**
         * 完成升级
         */
        void onComplete(File file);

        /**
         * 下载失败
         */
        void onFailure();

        /**
         * 下载完成,用来解绑服务
         */
        void onFinished();
    }

    /**
     * 升级服务回调
     */
    private class UpgradeResponseHandler extends FileAsyncHttpResponseHandler {
        private int cacheProgress, delta;

        public UpgradeResponseHandler(File file) {
            super(file);
        }

        @Override
        public void onStart() {
            upgradeStatus = UpgradeStatus.start;
            sendNotify();
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
            upgradeStatus = UpgradeStatus.progress;
            //计算进度
            delta += bytesWritten - cacheProgress;
            if (Float.parseFloat(Integer.toString(delta)) / Float.parseFloat(Long.toString(totalSize)) > 0.01) {
                updateNotify((int) (Float.parseFloat(Long.toString(bytesWritten)) / totalSize * 100));
                delta = 0;
            }
            cacheProgress = bytesWritten;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
            upgradeStatus = UpgradeStatus.fail;

            failNotify();
            finishNotify();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            upgradeStatus = UpgradeStatus.success;
            completeNotify(file);
            finishNotify();
        }
    }

}

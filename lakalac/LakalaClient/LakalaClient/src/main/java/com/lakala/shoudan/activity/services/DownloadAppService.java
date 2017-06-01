package com.lakala.shoudan.activity.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.Parameters;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by HUASHO on 2015/2/3.
 */
public class DownloadAppService extends Service{
    private NotificationManager nm;
    private Notification notification;
    private File tempFile = null;
    private boolean cancelUpdate = false;
    private MyHandler myHandler;
    private int download_precent = 0;
    private RemoteViews views;
    private int notificationId = 1234;
    private int stardId = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stardId=999;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.stardId ==999) {
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                this.stardId=startId;
                downloadApp(intent);
                // 启动线程开始执行下载任务
                downFile(intent.getStringExtra("downloadUrl"), Environment.getExternalStorageDirectory());
            }else {
                downloadApp(intent);
                File downloadFile = new File("/data/data/"+ ApplicationEx.getInstance().getPackageName());
                downFile(intent.getStringExtra("downloadUrl"),downloadFile);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadApp(Intent intent) {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
         notification.icon=android.R.drawable.stat_sys_download_done;
        notification.tickerText = getString(R.string.app_name) + getString(R.string.undate);
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        // 设置任务栏中下载进程显示的views
        views = new RemoteViews(getPackageName(), R.layout.layout_update);
        notification.contentView = views;
        views.setTextViewText(R.id.tvProcess, getString(R.string.start_download));
        views.setProgressBar(R.id.pbDownload, 0,download_precent, false);
        Intent intent2 = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent2, 0);
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this, "", "", contentIntent);
        // 将下载任务添加到任务栏中
        nm.notify(notificationId, notification);
        myHandler = new MyHandler(Looper.myLooper(), this);
        // 初始化下载任务内容views
        Message message = myHandler.obtainMessage(3, 0);
        myHandler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 下载更新文件
    private void downFile(final String url,final File upgradeFilePath) {
        new Thread() {
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    HttpParams httpParams = client.getParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                    HttpConnectionParams.setSoTimeout(httpParams, 10000);
                    HttpResponse response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    if (is != null) {
                        String fileName = url.substring(url.lastIndexOf("/") + 1);
                        // 已读出流作为参数创建一个带有缓冲的输出流
                        BufferedInputStream bis = new BufferedInputStream(is);

                        BufferedOutputStream bos = null;
                        FileOutputStream fos = null;
                        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                            File rootFile = new File(upgradeFilePath,"/lakala");
                            if (!rootFile.exists() && !rootFile.isDirectory())
                                rootFile.mkdirs();

                            tempFile = new File(upgradeFilePath,"/lakala/"+ fileName);
                            if (tempFile.exists())
                                tempFile.delete();
                            tempFile.createNewFile();

                            // 创建一个新的写入流，讲读取到的图像数据写入到文件中
                            fos = new FileOutputStream(tempFile);
                            // 已写入流作为参数创建一个带有缓冲的写入流
                            bos = new BufferedOutputStream(fos);
                        }else {
                            Context context = getApplicationContext();
                            //Context.MODE_WORLD_READABLE 下载的文件增加可读权限
                            fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
                            // 已写入流作为参数创建一个带有缓冲的写入流
                            bos = new BufferedOutputStream(fos);
                            tempFile = new File(getFilesDir(), fileName);
                        }
                        Intent broadcastIntent01 = new Intent();
                        broadcastIntent01.setAction(Parameters.DOWNLOAD_SERVICE_DOWNLOAD_START_ACTION);
                        sendBroadcast(broadcastIntent01);
                        int read;
                        long count = 0;
                        int precent = 0;
                        byte[] buffer = new byte[20480];
                        while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
                            bos.write(buffer, 0, read);
                            count += read;
                            precent = (int) (((double) count / length) * 100);
                            // 每下载完成5%就通知任务栏进行修改下载进度
                            if (precent - download_precent >= 3) {
                                download_precent = precent;
                                Message message = myHandler.obtainMessage(3,precent);
                                myHandler.sendMessage(message);
                            }
                        }
                        if (null!=bos) {
                            bos.flush();
                            bos.close();
                        }
                        if (null!=fos) {
                            fos.flush();
                            fos.close();
                        }
                        if (null!=is) {
                            is.close();
                            bis.close();
                        }
                    }

                    if (!cancelUpdate) {
                        Message message = null;
                        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                            message = myHandler.obtainMessage(2, tempFile);
                        }else {
                            File noSdCardDownloadFile = new File("/data/data/"+ ApplicationEx.getInstance().getPackageName()+"/files/"+tempFile.getName());
                            message = myHandler.obtainMessage(2, noSdCardDownloadFile);
                        }
                        myHandler.sendMessage(message);
                    } else {
                        tempFile.delete();
                    }
                } catch (ClientProtocolException e) {
                    Message message = myHandler.obtainMessage(4, getResources().getString(R.string.down_fail));
                    myHandler.sendMessage(message);
                    e.printStackTrace();
                } catch (IOException e) {
                    Message message = myHandler.obtainMessage(4, getResources().getString(R.string.down_fail));
                    myHandler.sendMessage(message);
                    e.printStackTrace();
                }catch (Exception e) {
                    Message message = myHandler.obtainMessage(4,getResources().getString(R.string.down_fail));
                    myHandler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 安装下载后的apk文件
    private void Instanll(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /* 事件处理类 */
    class MyHandler extends Handler {
        private Context context;

        public MyHandler(Looper looper, Context c) {
            super(looper);
            this.context = c;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent broadcastIntent = new Intent();
            Bundle bundle=new Bundle();
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        ToastUtil.toast(context,msg.obj.toString());
                        break;
                    case 1:
                        break;
                    case 2:// 下载完成后清除所有下载信息，执行安装提示
                        download_precent = 0;
                        nm.cancel(notificationId);
                        String filepath=((File)(msg.obj)).getPath();
                        bundle.putString("filepath", filepath);
                        broadcastIntent.putExtras(bundle);
                        broadcastIntent.setAction(Parameters.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETE_ACTION);
                        sendBroadcast(broadcastIntent);
                        Instanll((File) msg.obj, context);
                        // 停止掉当前的服务
                        stopSelf();
                        break;
                    case 3:// 更新状态栏上的下载进度信息
                        views.setTextViewText(R.id.tvProcess, getString(R.string.had_download)+ download_precent + "%");
                        views.setProgressBar(R.id.pbDownload, 100,download_precent, false);
                        notification.contentView = views;
                        nm.notify(notificationId, notification);
                        bundle.putString("download_precent", download_precent+"");
                        broadcastIntent.putExtras(bundle);
                        broadcastIntent.setAction(Parameters.DOWNLOAD_SERVICE_DOWNLOAD_PROGRESS_ACTION);
                        sendBroadcast(broadcastIntent);
                        break;
                    case 4://出现异常
                        nm.cancel(notificationId);
                        broadcastIntent.setAction(Parameters.DOWNLOAD_SERVICE_DISCONNECT_NETWORK_ACTION);
                        sendBroadcast(broadcastIntent);
                        LogUtil.d("download", "download error");
                        stopSelf();
                        break;
                }
            }
        }
    }
}

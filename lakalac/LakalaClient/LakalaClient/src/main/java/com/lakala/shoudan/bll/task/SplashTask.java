package com.lakala.shoudan.bll.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.SplashActivity;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 启动页面任务类
 * 负责查询当前需要显示的启动页资源
 * 查询结束后，设置启动图片名称
 * Created by xyz on 13-9-2.
 */
public class SplashTask {

    String splashFileName = "";
    String endTime = "";
    String updateurl = "";
    String updateTime = "";
    private Context context;

    public SplashTask(Context context) {
        this.context = context;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    File file = (File) msg.obj;
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    break;
                default:
                    break;

            }
        }
    };

    private File file;

    public void execute() {

        ShoudanService.getInstance().getAds(AdDownloadManager.Type.SPLASH.getValue(), "", new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //如果返回结果为空，或返回的非成功结果码，则直接退出
                if (resultServices == null || !resultServices.isRetCodeSuccess()) {
                    return;
                }
                try {
                    final JSONArray ads = new JSONArray(resultServices.retData);
                    if (ads == null || ads.length() == 0) {
                        return;
                    }
                    for (int i = 0; i < ads.length(); i++) {
                        try {
                            JSONObject ad = ads.getJSONObject(i);
                            updateTime = ad.optString("updateDatetime", "");
                            JSONArray advDatas = ad.getJSONArray("advData");
                            for (int j = 0; j < advDatas.length(); j++) {
                                JSONObject tempAdvData = advDatas.optJSONObject(j);
                                updateurl = tempAdvData.optString("content");
                                splashFileName = updateurl.substring(updateurl.lastIndexOf("/") + 1);
                                endTime = tempAdvData.optString("expire");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String curUpdateTime = LklPreferences.getInstance().getString(SplashActivity.CURRENT_SPLASH_UPDATETIME_KEY);
                    if (Util.isEmpty(curUpdateTime)) {
                        curUpdateTime = "0";
                    }
//                    Glide.with(context)
//                            .load(updateurl)
//                            .asBitmap()
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    File appDir = new File(Environment.getExternalStorageDirectory(), "lklcm");
//                                    if (!appDir.exists()) {
//                                        appDir.mkdir();
//                                    }
//                                    android.os.Message message = new android.os.Message();
//                                    file = new File(appDir, splashFileName);
//                                    try {
//                                        FileOutputStream fos = new FileOutputStream(file);
//                                        resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                                        fos.flush();
//                                        fos.close();
//
//                                        MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                                                file.getAbsolutePath(), splashFileName, null);
//                                        message.what = 1;
//                                        message.obj = file;
//                                        handler.sendMessageDelayed(message, 800);
//                                    } catch (FileNotFoundException e) {
//                                        e.printStackTrace();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                }
//                            });

//


//                    File dir = new File(Environment.getExternalStorageDirectory(), "lklcm");
//                    final String filePath = dir.getAbsolutePath() + File.separator + splashFileName;
//                    Log.d("SplashTask", filePath);
//                    final File file = new File(filePath);
                    File fileDir = ApplicationEx.getInstance().getDir("theme", Context.MODE_PRIVATE);
                    final String filePath = fileDir.getAbsolutePath() + File.separator + splashFileName;
                    final File file = new File(filePath);
                    //如果后台更新大于本地更新时间，就去下载图片
                    long updatetime = Long.parseLong(updateTime);
                    long curtime = Long.parseLong(curUpdateTime);
                    if (updatetime <= curtime) {
                        //文件存在，设置当前的启动页图片名称,下次启动使用这次设置的资源图片
                        LogUtil.print("splash file is already exists,no use to download");
                        LklPreferences.getInstance().putString(SplashActivity.CURRENT_SPLASH_KEY, updateurl);
                    } else {
                        //文件不存在，则从网络获取对应的资源
                        DownloadTask downloadTask = new DownloadTask(updateurl, filePath);
                        downloadTask.setiDownloadTask(new DownloadTask.IDownloadTask() {
                            @Override
                            public void downloadCompleted() {
                                //下载完成后，继续判断文件是否存在，如果存在，设置为下次启动使用的splash图片
                                LogUtil.print("download splash completed");
                                if (file.exists()) {
                                    LogUtil.print("download splash completed && file exists");
                                    LklPreferences.getInstance().putString(SplashActivity.CURRENT_SPLASH_KEY, updateurl);
                                    LklPreferences.getInstance().putString(SplashActivity.CURRENT_SPLASH_ENDTIME_KEY, endTime);
                                    LklPreferences.getInstance().putString(SplashActivity.CURRENT_SPLASH_UPDATETIME_KEY, updateTime);
                                }
                            }
                        });

                        downloadTask.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, context.getResources().getString(R.string.socket_fail));
            }
        });
    }

}

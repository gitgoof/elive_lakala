package com.lakala.elive.common.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.lakala.elive.Constants;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 *
 * Xutils 下载服务工具类
 * 
 */
public class DownLoadUtil {

    private static RequestParams params;

    /**
     * 下载工具类
     *
     * @param url
     * @param path
     */
    public static void downLoadApkFile(String url, final String path, final Context mContext,final ProgressDialog mProgressDialog) {
        params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(path);

        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
//                Toast.makeText(x.app(), "开始下载", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if(mProgressDialog != null){
                    mProgressDialog.setMax((int) total);//设置进度条的最大值
                    mProgressDialog.setProgress((int) current);//设置当前进度
                }
            }

            @Override
            public void onSuccess(File result) {
                if(mProgressDialog != null){
                    mProgressDialog.dismiss();//隐藏进度条
                }
                Toast.makeText(mContext, "版本下载成功,安装中...！",Toast.LENGTH_SHORT).show();//下载成功
                Utils.installApk(mContext, new File(Constants.saveFileName));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(mProgressDialog != null){
                    mProgressDialog.dismiss();//隐藏进度条
                }
                Toast.makeText(mContext, "下载新版本失败！", Toast.LENGTH_SHORT).show();//下载失败
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });
    }


}
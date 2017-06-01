package com.lakala.shoudan.bll.task;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 负责下载任务
 * 从给定的url下载文件到指定的目录
 * Created by xyz on 13-9-2.
 */
public class DownloadTask extends AsyncTask{

    private String fileUrl;         //需要下载文件的url

    private String desFilePath;     //下载后文件保存位置

    private IDownloadTask iDownloadTask; //下载接口

    /**
     * 下载任务接口
     */
    public interface IDownloadTask {
        /**
         * 下载完成，调用此方法
         */
        public void downloadCompleted();
    }

    public DownloadTask(String url, String desFilePath){
        this.fileUrl = url;
        this.desFilePath = desFilePath;
    }


    /**
     * 设置下载监听器
     * @param iDownloadTask
     */
    public void setiDownloadTask(IDownloadTask iDownloadTask) {
        this.iDownloadTask = iDownloadTask;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(fileUrl);

            httpURLConnection = (HttpURLConnection)url.openConnection();

            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

            fileOutputStream = new FileOutputStream(desFilePath);

            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1){
               fileOutputStream.write(buffer,0,len);
            }
        } catch (Exception e) {
        }finally {
            //释放资源
            try {
                if (fileOutputStream != null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }

                if (inputStream != null){
                    inputStream.close();
                }

                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (iDownloadTask != null){
            iDownloadTask.downloadCompleted();
        }
    }
}

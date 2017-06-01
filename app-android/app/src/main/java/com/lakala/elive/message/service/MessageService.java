package com.lakala.elive.message.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lakala.elive.R;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.message.activity.MessageActivity;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/11/17 16:52
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MessageService extends Service implements ApiRequestListener {
    //获取消息线程
    private MessageThread messageThread = null;

    //点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;

    //通知栏消息
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化
        messageNotification = new Notification();
        messageNotification.icon = R.drawable.icon;
        messageNotification.tickerText = "新消息";
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificatioManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        messageIntent = new Intent(this, MessageActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this,0,messageIntent,0);

        //开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 从服务器端获取消息
     *
     */
    class MessageThread extends Thread{
        //运行状态，下一步骤有大用
        public boolean isRunning = true;
        public void run() {
            while(isRunning){
                try {
                    //休息10分钟
                    Thread.sleep(600000);
                    //获取服务器消息
                    String serverMessage = getServerMessage();
                    if(serverMessage!=null&&!"".equals(serverMessage)){

//                        Notification notification = new Notification.Builder(getApplicationContext())
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());

                        mBuilder.setContentTitle("测试标题")//设置通知栏标题
                                .setContentText("测试内容") //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                        .setContentIntent(messagePendingIntent)//getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//  .setNumber(number) //设置通知集合的数量
                                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

//                        mBuilder.setAutoCancel(true)
//                                .setContentTitle("title")
//                                .setContentText("describe")
//                                .setContentIntent(messagePendingIntent)
//                                .setSmallIcon(R.mipmap.ic_launcher)
//                                .setWhen(System.currentTimeMillis())
//                                .build();

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 这里以此方法为服务器Demo，仅作示例
     * @return 返回服务器要推送的消息，否则如果为空的话，不推送
     */
    public String getServerMessage(){
        return "YES!";
    }

    @Override
    public void onSuccess(int method, Object obj) {

    }

    @Override
    public void onError(int method, String statusCode) {

    }


}

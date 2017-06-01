package com.lakala.core.swiper.Detector;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lakala.core.swiper.SwiperController;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.library.util.LogUtil;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Vinchaos api on 14-1-4.
 */
public class SwiperDetectorWIFI extends SwiperDetector implements Handler.Callback {
    /**
     * 设备插入
     */
    private final static int HM_DEVICE_PLUGGED = 203;
    /**
     * 设备拔出
     */
    private final static int HM_DEVICE_UNPLUGGED = 204;

    private SwiperDetectorListener listener;
    private Context context;
    private SwiperController controller;
    private Handler handler;
    /**socket广播接收*/
    private Thread socketThread;
    /**是否退出SocketThread*/
    private boolean exitSocketThread;
    /**是否销毁*/
    private boolean isDestroyed;
    /**Wifi是否已经连接*/
    private boolean wifiIsDevicePresent;
    /**当前WIFI设备是否是无线支付猫*/
    private boolean wifiIsPayfi;
    /**socket异常、广播异常最大连续次数，当超过次数时认为无线猫断开*/
    private long failedMaxTimes = 2;
    /**socket 超时时间*/
    private int socketTimeoutTime = 5 * 1000;

    public SwiperDetectorWIFI(Context context) {
        this.context = context;
        this.handler = new Handler(this);
    }

    @Override
    public boolean isStartup() {
        return false;
    }

    @Override
    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//网络状态改变广播
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//WiFi状态改变广播
        filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
        this.context.registerReceiver(this, filter);
        exitSocketThread = true;
        isDestroyed = false;
        socketThread = new Thread(new ReceiveSocketBroadcastThread(controller));
        socketThread.start();
    }

    @Override
    public void stop() {
        try {
            this.context.unregisterReceiver(this);
        } catch (Exception e) {}
        exitSocketThread = false;
        isDestroyed = true;
    }

    @Override
    public boolean isConnected() {
        return wifiIsDevicePresent && wifiIsPayfi;
    }

    @Override
    public SwiperDefine.SwiperPortType getSwiperPortType() {
        return SwiperDefine.SwiperPortType.TYPE_WIFI;
    }

    @Override
    public void setListener(SwiperDetectorListener listener) {
        this.listener = listener;
        this.controller = listener.getSwiperController();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            wifiStateReceive(intent);
        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            networkStateReceive(intent);
        }
    }

    /**
     * 处理WIFI状态广播
     *
     * @param intent
     */
    private void wifiStateReceive(Intent intent) {

        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

        if (wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiState == WifiManager.WIFI_STATE_DISABLING
                || wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
            //WIFI 被禁用
            setWPayfiState(false, false);
        }
    }

    /**
     * 处理wifi 网络状态广播
     *
     * @param intent
     */
    private void networkStateReceive(Intent intent) {
        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        //检测 wifi 是否已连接。
        if (info != null && info.isConnected()) {
            //WIFI 网络连接
            setWPayfiState(true, false);
            //启动广播接收线程，检测无线猫广播。
            continueReceive();
        } else {
            //WIFI 网络断开
            setWPayfiState(false, false);
        }
    }

    /**
     * 唤醒广播接收线程，继续检测无线猫广播。
     */
    private void continueReceive() {
        if (socketThread == null)
            return;

        synchronized (socketThread) {
            socketThread.notifyAll();
        }
    }

    /**
     * 设置 PAYFI 状态，如果状态发生改变则发送插拨事件。
     *
     * @param wifiPresent WIFI 是否已经连接
     * @param isPayfi     当前WIFI设置是否是PAYFI
     */
    private void setWPayfiState(boolean wifiPresent, boolean isPayfi) {
        //保存旧的设备接入状态，当状态发生改变时触发设备插拨事件。
        boolean oldPayfiIsDevicePresent = isConnected();

        //更新设备状态
        wifiIsDevicePresent = wifiPresent;
        wifiIsPayfi = wifiPresent && isPayfi;

        //当设备的接入状态发生改变时，发送插拨事件。
        if (oldPayfiIsDevicePresent != isConnected()) {
            if (!oldPayfiIsDevicePresent)
                LogUtil.e("SwiperController", "PayFi is connected");
            else
                LogUtil.e("SwiperController", "PayFi is disconnected");

            //发送物理设备插拨事件
            raisePhysicalDevicePlugEvents();

        }
    }

    /**
     * 获取 PAY-Fi心跳检测超时最大连续次数。
     *
     * @return
     */
    public long getPayFiHeartbeatsTimeoutMaxTimes() {
        return failedMaxTimes;
    }

    /**
     * 设置 PAY-Fi心跳检测超时最大连续次数，在最大连续超时次数内检测到无线支付猫广播，
     * 则认为当前接入的WIFI是无线支付猫，如果当超过最大连续超时次数时认为当前接入的WIFI
     * 不是无线猫支付猫，该结果将维持到WIFI　断开或重新连接。默认最大次数为 2 次。
     *
     * @param failedMaxTimes
     */
    public void setPayFiHeartbeatsTimeoutMaxTimes(long failedMaxTimes) {
        this.failedMaxTimes = failedMaxTimes;
    }

    /**
     * 获取PAY-Fi心跳检测超时时间，单位毫秒。
     * 默认 5000 毫秒。
     *
     * @return
     */
    public int getPayFiHeartbeatsTimeout() {
        return socketTimeoutTime;
    }

    /**
     * 设置PAY-Fi心跳检测超时时间，单位毫秒。
     *
     * @param timeoutTime
     */
    public void setPayFiHeartbeatsTimeout(int timeoutTime) {
        this.socketTimeoutTime = timeoutTime;
    }

    /**
     * 发送物理设备插拨事件
     */
    private void raisePhysicalDevicePlugEvents() {
        if (isDestroyed || handler == null)
            return;
        Message msg = handler.obtainMessage();
        if (isConnected())
            msg.what = HM_DEVICE_PLUGGED;
        else
            msg.what = HM_DEVICE_UNPLUGGED;

        handler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HM_DEVICE_PLUGGED:
                //设备插入,通知客户的侦听器
                if (listener != null)
                    listener.onConnected(this);
                break;
            case HM_DEVICE_UNPLUGGED:
                //设备拨出,通知客户的侦听器
                if (listener != null)
                    listener.onDisconnected(this);
                break;
        }
        return true;
    }

    /**
     * 无线支付猫接入检测
     *
     * @author Michael
     */
    private class ReceiveSocketBroadcastThread implements Runnable {
        private SwiperController _this = null;
        private DatagramPacket dataPacket = null;
        private byte[] buffer;
        /**
         * UPD Socket 用于接收MoFi广播*
         */
        private DatagramSocket udpSocket = null;

        /**
         * 无线猫广播数据*
         */
        private byte[] LKL_IDT_MOFi_0 = null;
        private byte[] LKL_IDT_MOFi_1 = null;

        /**
         * 广播、多播锁*
         */
        private WifiManager.MulticastLock multicasLock = null;

        public ReceiveSocketBroadcastThread(SwiperController _this) {
            this._this = _this;
            //创建数据包，用于接收socket数据
            buffer = new byte[256];
            dataPacket = new DatagramPacket(buffer, buffer.length);

            LKL_IDT_MOFi_0 = "LKL_IDT_MOFi-0".getBytes();
            LKL_IDT_MOFi_1 = "LKL_IDT_MOFi-1".getBytes();

            //获取广播、多播锁，有些手机默认是没有打开的，所以在接收广播之前要打开。
            //需要配置 CHANGE_WIFI_MULTICAST_STATE 权限
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            multicasLock = manager.createMulticastLock("LakalaSwiperController");
        }

        @Override
        public void run() {
            boolean bPayfiIsDevicePresent = false;
            long failedTimes = 0;

            //开启广播发送及接收功能
            multicasLock.acquire();

            //循环接收 socket 广播
            while (!exitSocketThread) {
                try {

                    synchronized (socketThread) {
                        //当WIFI 没有连接或已经识别出是 PAYFI 设备或超过最大失败次数，
                        //则挂起线程，等待WIFI重新连接时在重新执行检测步骤。
                        if (!wifiIsDevicePresent || wifiIsPayfi || failedTimes >= failedMaxTimes) {
                            //关闭socket
                            if (udpSocket != null) {
                                udpSocket.close();
                                udpSocket = null;
                            }

                            multicasLock.release();
                            socketThread.wait();
                            multicasLock.acquire();
                            failedTimes = 0; //重置连续失败次数

                            if (isDestroyed) {
                                //当消毁线程时直接退出，不在执行下边的循环。
                                break;
                            }
                        }
                    }
                    //每秒种接收一次广播
                    Thread.sleep(1000);

                    //预设无线猫没有接入
                    bPayfiIsDevicePresent = false;

                    //socket 为空时创建新socket
                    if (udpSocket == null) {
                        udpSocket = new DatagramSocket(9890);
                        //receive 超时时间。
                        udpSocket.setSoTimeout(socketTimeoutTime);
                        udpSocket.setBroadcast(true);
                    } else {
                        //socket 超时时间改变了，重新设置。
                        if (udpSocket.getSoTimeout() != socketTimeoutTime)
                            udpSocket.setSoTimeout(socketTimeoutTime);
                    }

                    //接收udp数据包直到接收完成或超时
                    udpSocket.receive(dataPacket);

                    //在此判断广播数据是否是 MoFi，如果是则说明是无线猫设备。
                    if (isMOFiBroadcastData(dataPacket, LKL_IDT_MOFi_0) ||
                            isMOFiBroadcastData(dataPacket, LKL_IDT_MOFi_1)) {
                        bPayfiIsDevicePresent = true;
                    } else {
                        bPayfiIsDevicePresent = false;
                        LogUtil.d("SwiperController", "Not PayFi broadcast data");
                    }
                } catch (BindException e) {
                    //端口被占用，直接进行下一次循环，不计算失败次数。
                    LogUtil.d("SwiperController", "UDP BindException");
                    continue;
                } catch (SocketException e) {
                    //Socket 异常时关闭当前socket，重创建Socket。
                    if (udpSocket != null)
                        udpSocket.close();
                    udpSocket = null;
                    LogUtil.d("SwiperController", "UDP SocketException");
                } catch (IOException e) {
                    //忽略IO异常
                    LogUtil.d("SwiperController", "UDP Timeout");
                } catch (InterruptedException e) {
                    LogUtil.d("SwiperController", "UDP InterruptedException");
                } catch (Exception e) {
                    LogUtil.d("SwiperController", "UDP Other Exception");
                }

                if (bPayfiIsDevicePresent) {
                    //检测到当前WIFI设备是无线猫，设置无线猫接入标识并发送插拨事件。
                    setWPayfiState(wifiIsDevicePresent, true);
                } else {
                    //检测未成功，增加失败记数。
                    failedTimes++;
                }
            }

            if (udpSocket != null)
                udpSocket.close();

            udpSocket = null;
            multicasLock.release();
            socketThread = null;
            LogUtil.d("SwiperController", "Socket Thread End.");
        }

        private boolean isMOFiBroadcastData(DatagramPacket dataPacket, byte[] tag) {
            if (tag == null || dataPacket == null)
                return false;

            int dataLength = dataPacket.getLength();

            //播数据与标识数据长度不同，说明不是无线猫的广播
            if (dataLength != tag.length)
                return false;

            byte[] data = dataPacket.getData();

            //比较广播数据与标识数据，如果完全相同说明是无线猫的广播。
            for (int index = 0; index < tag.length; index++) {
                if (data[index] != tag[index])
                    return false;
            }

            return true;
        }
    }
}

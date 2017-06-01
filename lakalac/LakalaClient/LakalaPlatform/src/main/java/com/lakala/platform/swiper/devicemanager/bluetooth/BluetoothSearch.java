package com.lakala.platform.swiper.devicemanager.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.lakala.library.util.LogUtil;

import java.util.Set;
import java.util.TreeSet;


/**
 * 蓝牙工具类
 * * Created by More on 14-1-10.
 */
public class BluetoothSearch {

    /** 蓝牙控制器 **/
    private BluetoothAdapter bluetoothAdapter;

    private Context context;

    /**
     * 是否正在进行蓝牙搜索
     */
    private boolean isSearching = false;

    /** 蓝牙搜索事件监听 **/
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(android.bluetooth.BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                if(onDeviceFoundListener != null){
                    android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
                    if(device.getName() == null || device.getName().length() == 0){
                        return;
                    }
                    if(isDeviceLegal(device.getName())){

                        NLDevice bluetoothDevice = new NLDevice();
                        bluetoothDevice.setMacAddress(device.getAddress());
                        bluetoothDevice.setName(device.getName());
                        bluetoothDevice.setConnectType(ConnectType.BLUETOOTH);
                        bluetoothDevice.setDefault(false);
                        onDeviceFoundListener.onDeviceFound(bluetoothDevice);

                    }


                }
            }
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                if(BluetoothAdapter.STATE_ON == intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE)){
                    if(onBluetoothEnableListener != null)
                        onBluetoothEnableListener.onEnableResult(true);
                }
            }
        }
    };

    private boolean isDeviceLegal(String name){

//        if(Parameters.isUseDeveloperURL()){
//            return true;
//        }

        if(name.contains("ME30")){
            return true;
        }

        String[] strArray = name.split("-");

        if(strArray.length != 3){
            return false;
        }

        if(!"L".equals(strArray[0])){
            return false;
        }

        return true;
    }

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                if(BluetoothAdapter.STATE_ON == intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE)){
                    if(onBluetoothEnableListener != null)
                        handler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        }
    };

    private boolean onReceived = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(onReceived){
                return;
            }
            onReceived = true;
            if(onBluetoothEnableListener != null){
                onBluetoothEnableListener.onEnableResult(isEnable());
            }
        }
    };

    /**
     * 实例化
     * 注册蓝牙搜索广播
     * @param context
     */
    public BluetoothSearch(Context context){
        this.context = context;
        isSearching = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 检查当前蓝牙状态
     * @return
     */
    public boolean isEnable(){
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 开启蓝牙
     */
    public void enableBluetooth(final OnBluetoothEnableListener onBluetoothEnableListener){
        if (bluetoothAdapter.isEnabled()){

            onBluetoothEnableListener.onEnableResult(true);

        }else{

            Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    try{

                        context.unregisterReceiver(stateReceiver);
                        if(!bluetoothAdapter.isEnabled()){
                            onBluetoothEnableListener.onEnableResult(false);
                        }

                    }catch (Exception e){
                        LogUtil.print(e);
                    }

                }
            };

            this.onBluetoothEnableListener = onBluetoothEnableListener;
            context.registerReceiver(stateReceiver,  new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            onReceived = false;
            bluetoothAdapter.enable();
            handler.obtainMessage(1234);
            handler.sendEmptyMessageDelayed(1234, 8000);
        }
    }

    private OnDeviceFoundListener onDeviceFoundListener;

    private OnBluetoothEnableListener onBluetoothEnableListener;

    /**
     * 开始蓝牙搜索
     * @param onDeviceFoundListener
     */
    public void startSearch(OnDeviceFoundListener onDeviceFoundListener){

        context.registerReceiver(broadcastReceiver, new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND));
        this.onDeviceFoundListener = onDeviceFoundListener;
        bluetoothAdapter.startDiscovery();
        isSearching = true;
    }

    /** 搜索到的蓝牙 **/
    private Set<NLDevice> foundDevices = new TreeSet<NLDevice>();

    /**
     * 开启一段限定时长的蓝牙搜索
     * @param milliseconds 蓝牙搜索时间
     *
     * @param onDiscoveryFinishedlistener 完成搜索的事件监听
     */
    public void startDiscoveryForDefineTime(int milliseconds, final String targetMac, final OnDiscoveryFinishedListener onDiscoveryFinishedlistener){

        //context.registerReceiver(broadcastReceiver, new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND));
        foundDevices.clear();
        if(isSearching())
            return;
        isSearching = true;
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                isSearching = false;
                stopDiscovery();
                onDiscoveryFinishedlistener.onFinished(foundDevices);
            }
        };
        handler.obtainMessage();
        startSearch(new OnDeviceFoundListener() {
            @Override
            public void onDeviceFound(NLDevice bluetoothDevice) {
                if(bluetoothDevice.getName() != null)
                    foundDevices.add(bluetoothDevice);
                if(bluetoothDevice.getMacAddress().equals(targetMac)){
                    onDiscoveryFinishedlistener.onTargetDeviceFound(bluetoothDevice);
                }
            }
        });
        handler.sendEmptyMessageDelayed(0, milliseconds);
    }

    public boolean isSearching() {
        return isSearching && isEnable();
    }

    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
    }

    /**
     * 停止搜索
     */
    public void stopDiscovery(){
        isSearching = false;
        bluetoothAdapter.cancelDiscovery();
        try{
            context.unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){

        }


    }

    /**
     * 注销
     */
    public void destorySearch(){
        bluetoothAdapter.cancelDiscovery();
    }

}

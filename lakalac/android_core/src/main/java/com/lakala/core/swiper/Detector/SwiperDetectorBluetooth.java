package com.lakala.core.swiper.Detector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lakala.core.swiper.Adapter.SwiperAdapterBluetooth;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchao on 14-4-10.
 */
public class SwiperDetectorBluetooth extends SwiperDetector {

    //蓝牙适配器
    private BluetoothAdapter adapter;

    private Context context;

    private SwiperDetectorListener listener;
    //是否启动
    private boolean isStartup;
    //扫描到的蓝牙地址列表
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    public SwiperDetectorBluetooth(Context context) {
        this.context = context;
    }

    private void initBluetooth() {
        if (adapter == null)
            adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (!adapter.isEnabled())
                adapter.enable();
        } else {
            ((SwiperBluetoothDetectorListener) listener).detectorError("disabled", null);
        }
    }

    /**
     * 取消搜索
     */
    public void cancelDiscovery(){
        if (adapter != null && adapter.isDiscovering())
            adapter.cancelDiscovery();
    }

    @Override
    public boolean isStartup() {
        return isStartup;
    }

    @Override
    public void start() {
        if (context == null) {
            return;
        }
        devices.clear();
        initBluetooth();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(this, filter);
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();
        isStartup = true;
    }

    @Override
    public void stop() {
        if (context == null)
            return;

        if (adapter != null && adapter.isDiscovering())
            adapter.cancelDiscovery();

        devices.clear();
        isStartup = false;
        try {
            context.unregisterReceiver(this);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isConnected() {
        if(listener == null){
            return false;
        }
        if(listener.getSwiperController().getCurrentAdapter() instanceof SwiperAdapterBluetooth){
            return listener.getSwiperController().getCurrentAdapter().isDevicePresent();
        }
        return false;
    }

    @Override
    public SwiperDefine.SwiperPortType getSwiperPortType() {
        return SwiperDefine.SwiperPortType.TYPE_BLUETOOTH;
    }

    @Override
    public void setListener(SwiperDetectorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // 获取查找到的蓝牙设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null)
                return;

            BluetoothClass bluetoothClass = device.getBluetoothClass();
            //Get the Bluetooth class of the remote device on error,bluetoothClass is null
            if (bluetoothClass == null) return;

            if (!devices.contains(device) &&
                    bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET){
                //如果设备没有名字则过滤
                if(StringUtil.isEmpty(device.getName())) return;
                devices.add(device);
                ((SwiperBluetoothDetectorListener) listener).deviceAddressList(devices, device);
            }

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //获取当前list
            ((SwiperBluetoothDetectorListener) listener).deviceAddressList(devices, null);
        }
    }

}

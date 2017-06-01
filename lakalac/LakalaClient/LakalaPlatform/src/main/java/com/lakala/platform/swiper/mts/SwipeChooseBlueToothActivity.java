package com.lakala.platform.swiper.mts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lakala.core.swiper.Detector.SwiperBluetoothDetectorListener;
import com.lakala.core.swiper.Detector.SwiperDetector;
import com.lakala.core.swiper.Detector.SwiperDetectorBluetooth;
import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperController;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.swiper.mts.protocal.EProtocalType;
import com.lakala.platform.swiper.mts.protocal.ProtocalActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lianglong on 14-6-16.
 */
public class SwipeChooseBlueToothActivity extends BaseActionBarActivity implements SwiperBluetoothDetectorListener,
        GetSwipeKsnTask.GetKsnListener,
        Handler.Callback{
    //检查蓝牙是否可用
    private static final int CHECK_BLUETOOTH        = 200;
    //去设置，打开蓝牙
    private static final int SET_BLUETOOTH          = 201;
    //搜索蓝牙设备
    private static final int SEARCH_BLUETOOTH       = 202;
    //未搜索到蓝牙设备
    private static final int NOT_FOUND_BLUETOOTH    = 203;


    private DialogController builder                = DialogController.getInstance();
    private List<BluetoothDevice> devices           = new ArrayList<BluetoothDevice>();
    private DeviceAdapter adapter                   = new DeviceAdapter();

    private BluetoothAdapter bluetoothAdapter       = BluetoothAdapter.getDefaultAdapter();
    private SwiperDetectorBluetooth detector        = new SwiperDetectorBluetooth(this);
    private SwipeLauncher launcher                  = SwipeLauncher.getInstance();

    private int selectedPosition                    = -1;
    private Button confirm;
    private View help,search;
    private ProgressBar progressBar;
    private ListView listView;
    private LinearLayout bluetooth_loading_finish_ll,bluetooth_loading_ll,bluetooth_search_ll;
    private TextView bluetooth_hint;

    private boolean isShowList                      = false;
    private boolean isSearchingBluetooth            = false;

    private Handler statusHandler;

    @Override
    public boolean handleMessage(Message msg) {
        int status = msg.what;

        setView(status);

        switch (status){
            case CHECK_BLUETOOTH:
                sendStatus(bluetoothAdapter.isEnabled() ? SEARCH_BLUETOOTH : SET_BLUETOOTH);
                break;
            case SET_BLUETOOTH:
                break;
            case SEARCH_BLUETOOTH:
                start();
                break;
            case NOT_FOUND_BLUETOOTH:

                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bluetooth_device);
        navigationBar.setTitle("连接蓝牙刷卡器");

        statusHandler               = new Handler(this);

        bluetooth_loading_finish_ll = (LinearLayout) findViewById(R.id.bluetooth_loading_finish_ll);
        bluetooth_loading_ll        = (LinearLayout) findViewById(R.id.bluetooth_loading_ll);
        bluetooth_search_ll         = (LinearLayout) findViewById(R.id.bluetooth_search_ll);

        bluetooth_hint              = (TextView) findViewById(R.id.bluetooth_hint);

        confirm = (Button) findViewById(R.id.id_common_guide_button);
        confirm.setText(R.string.com_confirm);
        confirm.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        search = findViewById(R.id.search);
        search.setOnClickListener(this);

        help = findViewById(R.id.help);
        help.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.bluetooth_id_progressBar);

        detector.setListener(this);
        //如果蓝牙为开启状态，则直接搜索设备
        if(bluetoothAdapter.isEnabled()){
            sendStatus(SEARCH_BLUETOOTH);
            return;
        }
        bluetoothAdapter.enable();
        //设置View默认状态
        setView(CHECK_BLUETOOTH);
        statusHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断蓝牙是否可用
                sendStatus(CHECK_BLUETOOTH);
            }
        }, 2000);
    }

    /**
     * 发送消息
     *
     * @param status
     */
    private void sendStatus(int status){
        Message temp = statusHandler.obtainMessage(status);
        statusHandler.sendMessage(temp);
    }

    /**
     * 开始搜索
     */
    private void start(){

        isSearchingBluetooth = true;
        isShowList           = true;
        selectedPosition     = -1;
        devices.clear();

        if (bluetoothAdapter.isEnabled()){
            detector.start();
            isShowProgress(true);
        }
    }

    /**
     * 是否显示progress
     *
     * @param b
     */
    private void isShowProgress(boolean b){
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    /**
     * 根据状态设置当前view
     *
     * @param status
     */
    private void setView(int status){
        int bt_loading           = status     == CHECK_BLUETOOTH  ? View.VISIBLE : View.GONE;
        int bt_loading_finished  = bt_loading == View.VISIBLE     ? View.GONE    : View.VISIBLE;
        int bt_search            = status     == SEARCH_BLUETOOTH ? View.VISIBLE : View.GONE;
        int bt_hint              = bt_search  == View.VISIBLE     ? View.GONE    : View.VISIBLE;

        bluetooth_loading_ll.setVisibility(bt_loading);
        bluetooth_loading_finish_ll.setVisibility(bt_loading_finished);
        bluetooth_search_ll.setVisibility(bt_search);
        bluetooth_hint.setVisibility(bt_hint);

        switch (status){
            case CHECK_BLUETOOTH:
                confirm.setEnabled(false);
                confirm.setText(getString(R.string.com_confirm));
                break;
            case SET_BLUETOOTH:
                confirm.setEnabled(true);
                confirm.setText("去设置");
                bluetooth_hint.setText("请先打开您的手机蓝牙");
                break;
            case SEARCH_BLUETOOTH:
                confirm.setEnabled(false);
                confirm.setText(getString(R.string.com_confirm));
                break;
            case NOT_FOUND_BLUETOOTH:
                confirm.setEnabled(true);
                confirm.setText("再次搜索");
                bluetooth_hint.setText("未能搜索到您的蓝牙刷卡器，请确定刷卡器已开机。");
                break;
        }
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view == search){
            if(isSearchingBluetooth){
                ToastUtil.toast(this, "正在搜索，请稍后。");
                return;
            }
            sendStatus(SEARCH_BLUETOOTH);
        }  else if (view == help){
            Intent intent = new Intent(this, ProtocalActivity.class);
            intent.putExtra(ProtocalActivity.PROTOCAL_KEY, EProtocalType.SWIPE_INTRODUCE);
            startActivity(intent);
        } else if (view == confirm){
            String temp = confirm.getText().toString();
            //确定
            if(temp.equals(getString(R.string.com_confirm))){
                if(detector != null){
                    detector.cancelDiscovery();
                }

                launcher.setSwipeType(ESwiperType.Bluetooth.toString());
                final BluetoothDevice device = devices.get(selectedPosition);
                if (SwiperManager.getInstance(launcher).connectBluetooth(device.getAddress())){
                    //todo #235行代码，为了判断刷卡器功能，临时代码，等SDK提供后删除
                    launcher.setConnectionBluetooth(device.getName());
                    launcher.setCurrentConnectInvalid(true);
                    launcher.getKsn(this);
                } else {
                    ToastUtil.toast(this, "刷卡器连接失败");
                }

            }else
            //去设置
            if(temp.equals("去设置")){
                if (bluetoothAdapter.isEnabled()) {
                    sendStatus(SEARCH_BLUETOOTH);
                } else {
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 111);
                }
            }else
            //再次搜索
            if(temp.equals("再次搜索")){
                sendStatus(SEARCH_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 &&  bluetoothAdapter.isEnabled()) {
            sendStatus(SEARCH_BLUETOOTH);
        } else {
            sendStatus(SET_BLUETOOTH);
        }
    }

    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {
        if(!isShowList) return;

        if(newMacs == null){
            isSearchingBluetooth = false;
            isShowProgress(false);
        }else {
            devices.clear();
            devices.addAll(macs);
            listView.setAdapter(adapter);
        }

        if (macs.isEmpty()){
            sendStatus(NOT_FOUND_BLUETOOTH);
        }
    }

    @Override
    public void detectorError(String id, Object data) {

    }

    @Override
    public void onConnected(SwiperDetector detector) {

    }

    @Override
    public void onDisconnected(SwiperDetector detector) {

    }

    @Override
    public SwiperController getSwiperController() {
        return null;
    }

    @Override
    public void GetKsnSuccess(String ksn) {
        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKsnSuccess, this);

        launcher.setCurrentConnectInvalid(false);
        launcher.setKsn(ksn);
        launcher.setCurrentConnectInvalid(false);
        launcher.launch(null);
        finish();
    }

    @Override
    public void GetKsnFailure() {
        builder.showAlertDialog(this, getResources().getString(R.string.plat_select_swipe_getksn_title), getResources().getString(R.string.plat_select_swipe2));

        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKSNFailure, this);
        SwipeLauncher.getInstance().statisticGetKSNStatus(StatisticManager.getKSNFailureSelect, this);
    }

    @Override
    public FragmentActivity ksnActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.stop();
    }

    private class DeviceAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView     = View.inflate(SwipeChooseBlueToothActivity.this, R.layout.item_bluetooth_device, null);
                holder          = new ViewHolder();
                holder.name     = (TextView) convertView.findViewById(R.id.device_name);
                holder.address  = (TextView) convertView.findViewById(R.id.device_address);
                holder.selected = (ImageView) convertView.findViewById(R.id.selected);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectedPosition = position;
                        DeviceAdapter.this.notifyDataSetChanged();
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BluetoothDevice device = getItem(position);
            holder.name.setText(device.getName());
            holder.address.setText(device.getAddress());
            if (selectedPosition >= 0 && selectedPosition == position){
                holder.selected.setImageResource(R.drawable.ui_check_on);
            } else {
                holder.selected.setImageResource(R.drawable.ui_check_off);
            }
            confirm.setEnabled(selectedPosition >= 0);
            return convertView;
        }

        class ViewHolder{
            TextView name,address;
            ImageView selected;
        }
    }
}

package com.lakala.shoudan.activity.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.bluetooth.BluetoothSearch;
import com.lakala.platform.swiper.devicemanager.bluetooth.ConnectType;
import com.lakala.platform.swiper.devicemanager.bluetooth.NLDevice;
import com.lakala.platform.swiper.devicemanager.bluetooth.OnBluetoothEnableListener;
import com.lakala.platform.swiper.devicemanager.bluetooth.OnDiscoveryFinishedListener;
import com.lakala.platform.swiper.devicemanager.connection.DeviceSp;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertListDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 设备管理页面
 * Created by More on 14-1-8.
 */
public class DeviceManagementActivity extends AppBaseActivity {

    private DeviceSp dbManager;
    private DeviceListAdapter deviceListAdapter;
    private ListView deviceList;
    private BluetoothSearch bluetoothSearch;
    private List<NLDevice> nlDeviceList;

    private TextView connectionTitle;
    private TextView connectionType;
    private TextView connectionDeviceName;

    private View layoutCurrentDevie;


    private SwiperManager swiperManager;

    private View footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_management);
        dbManager = DeviceSp.getInstance();
        bluetoothSearch = new BluetoothSearch(this);
        nlDeviceList = new ArrayList<NLDevice>();
        swiperManager = SwiperManager.getInstance(null);
        initUI();

    }

    protected void initUI() {

        initView();
        navigationBar.setActionBtnText("帮助");
        navigationBar.setTitle("设备连接管理");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.More_DeviceConnectManage, context);
                    ProtocalActivity.open(DeviceManagementActivity.this, ProtocalType.CONNECTION_HELP);
                } else if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });

        layoutCurrentDevie = findViewById(R.id.layout_current_device);

        connectionTitle = (TextView) findViewById(R.id.connection_title);
        connectionDeviceName = (TextView) findViewById(R.id.connection_device_name);
        connectionType = (TextView) findViewById(R.id.connection_device_type);
        displayPresentConnection();
    }

    private void displayPresentConnection() {
        NLDevice presentDevice = swiperManager.getNlDevice();

        if (!swiperManager.isDeviceConnected() || null == presentDevice) {
            connectionTitle.setText("当前未连接设备");
            connectionType.setText("");
            connectionDeviceName.setText("");
            connectionType.setVisibility(View.GONE);

            findViewById(R.id.layout_tips_connect).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_cennecting).setVisibility(View.GONE);

            deviceListAdapter.setPresentDeviceName("");
            deviceListAdapter.notifyDataSetChanged();
        } else {
            connectionTitle.setText("当前设备");
            connectionType.setText("已通过" + (swiperManager.getCurrentSwipeType() == ESwiperType.QV30E ? "音频连接" : "蓝牙连接"));
            connectionDeviceName.setText(presentDevice.getName());
            layoutCurrentDevie.setVisibility(View.VISIBLE);
            connectionType.setVisibility(View.VISIBLE);

            findViewById(R.id.layout_tips_connect).setVisibility(View.GONE);
            findViewById(R.id.layout_cennecting).setVisibility(View.VISIBLE);


            //连接成功
            deviceListAdapter.setPresentDeviceName(presentDevice.getName());
            deviceListAdapter.notifyDataSetChanged();
        }

    }

    private void initView() {
        footerLayout = LayoutInflater.from(this).inflate(R.layout.device_list_item, null);
        deviceList = (ListView) findViewById(R.id.deviceList);
        deviceListAdapter = new DeviceListAdapter(this);
        deviceList.setAdapter(deviceListAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDeviceManageDialog(i);
            }
        });
    }

    private final int STOP_DISCOVERY = 11;
    private final int DEVICE_MATCH_SUCCESS = 12;
    private final int DEVICE_MATCH_FAIL = 13;
    private final int CONNECT_SUCCESS = 14;
    private final int CONNECT_FAIL = 15;


    private Handler defaultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case STOP_DISCOVERY:
                    break;
                case DEVICE_MATCH_SUCCESS:
                    displayPresentConnection();
                    setListSelectedDeviceAdded(msg.arg1);
                    break;
                case DEVICE_MATCH_FAIL:
                    ToastUtil.toast(DeviceManagementActivity.this, "设备验证未通过");
                    break;
                case CONNECT_FAIL:
                    ToastUtil.toast(DeviceManagementActivity.this, "连接失败");
                    displayPresentConnection();
                    break;
                case CONNECT_SUCCESS:
                    ToastUtil.toast(DeviceManagementActivity.this, "连接成功");
                    displayPresentConnection();
                    break;
                default:
                    break;
            }

            return;

        }
    };

    /**
     * Display bluetooth devices those searched
     */
    private void showDeviceSelection() {
        if (nlDeviceList.size() == 0) {
            hideProgressDialog();
            ToastUtil.toast(this, "未找到蓝牙设备");
            return;
        }
        List<String> deviceListStr = new ArrayList<String>();
        for (int i = 0; i < nlDeviceList.size(); i++) {
            deviceListStr.add(nlDeviceList.get(i).getName() == null ? "Unknown Device" : nlDeviceList.get(i).getName());
        }

        AlertListDialog alertListDialog = new AlertListDialog();
        alertListDialog.setTitle("请选择连接设备");
        alertListDialog.setButtons(new String[]{getString(R.string.cancel)});
        alertListDialog.setItems(deviceListStr, new AlertListDialog.ItemClickListener() {
            @Override
            public void onItemClick(com.lakala.ui.dialog.AlertDialog dialog, int index) {
                validateCertainBluetoothDevice(index);
            }
        });
        alertListDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                hideProgressDialog();
                dialog.dismiss();
            }
        });
        alertListDialog.show(getSupportFragmentManager());


    }

    private void validateCertainBluetoothDevice(final int listIndex) {
        swiperManager.disconnect();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swiperManager.setSwiperType(ESwiperType.Bluetooth);
            }
        });

        MutexThreadManager.runThread("ValidateSwipeType", new Runnable() {
            @Override
            public void run() {
                showProgressDialog("正在验证...");
                swiperManager.setConnectionDevice(nlDeviceList.get(listIndex));
                Message msg = new Message();

                String ksn = swiperManager.getKsn();
                if (!TextUtils.isEmpty(ksn)) {
                    msg.what = DEVICE_MATCH_SUCCESS;
                    msg.arg1 = listIndex;
                    defaultHandler.sendMessage(msg);
                } else {
                    defaultHandler.sendEmptyMessage(DEVICE_MATCH_FAIL);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }
        });

    }

    private void connectTo(final NLDevice nlDevice) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swiperManager.setSwiperType(ESwiperType.Bluetooth);
            }
        });


        MutexThreadManager.runThread("ValidateSwipeType", new Runnable() {
            @Override
            public void run() {
                showProgressDialog("正在连接...");
                try {
                    Thread.sleep(1222);
                } catch (Exception e) {

                }
                swiperManager.disconnect();

                swiperManager.setConnectionDevice(nlDevice);
                Message msg = new Message();
                String ksn = swiperManager.getKsn();

                if (!TextUtils.isEmpty(ksn)) {
                    msg.what = CONNECT_SUCCESS;
                    defaultHandler.sendMessage(msg);
                } else {
                    defaultHandler.sendEmptyMessage(CONNECT_FAIL);
                }
            }
        });

    }


    /**
     * 开始6.666秒的蓝牙搜索
     */
    private void startBluetoothSearch() {
        if (nlDeviceList == null)
            nlDeviceList = new ArrayList<NLDevice>();
        nlDeviceList.clear();
        showProgressDialog("正在搜索...");
        bluetoothSearch.enableBluetooth(new OnBluetoothEnableListener() {
            @Override
            public void onEnableResult(boolean b) {
                if (b) {
                    bluetoothSearch.startDiscoveryForDefineTime(6666, "", new OnDiscoveryFinishedListener() {
                        @Override
                        public void onFinished(Set<NLDevice> nlDevices) {
                            nlDeviceList.addAll(nlDevices);
                            showDeviceSelection();
                        }

                        @Override
                        public void onTargetDeviceFound(NLDevice nlDevice) {
                            ToastUtil.toast(context, nlDevice.getName());
                        }
                    });
                } else {
                    ToastUtil.toast(DeviceManagementActivity.this, "请求开启蓝牙失败");
                }
            }
        });

    }

    /**
     * before insert ,
     * query dbs with macAddress to check if the device is added
     *
     * @param nlDevice
     */
    private void addDevice(NLDevice nlDevice) {

        if (dbManager.saveDevice(nlDevice)) {
            ToastUtil.toast(this, "已添加过该设备");
        }

        deviceListAdapter.updateDevice();
        deviceListAdapter.notifyDataSetChanged();

    }

    private void addDevice(String name, String signature, ConnectType type) {

        NLDevice tempDevice = new NLDevice(name, signature, type);
        addDevice(tempDevice);
    }

    private void setListSelectedDeviceAdded(int selectionIndex) {
        addDevice(nlDeviceList.get(selectionIndex));
    }

    private void deleteDevice(int i) {
        NLDevice deviceTableInfo = (NLDevice) deviceListAdapter.getItem(i);
        if (deviceTableInfo != null && swiperManager.getNlDevice() != null && deviceTableInfo.getName().equals(swiperManager.getNlDevice().getName())) {
            swiperManager.disconnect();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayPresentConnection();
                }
            }, 2000);

        }
        dbManager.deleteDevice(deviceTableInfo);
//        deviceListAdapter.notifyDataSetChanged();
//        deviceList = null;
//        deviceListAdapter = null;
//        deviceList = (ListView) findViewById(R.id.deviceList);
//        deviceListAdapter = new DeviceListAdapter(this);
//        deviceList.setAdapter(deviceListAdapter);
        deviceListAdapter.updateDevice();
        deviceListAdapter.notifyDataSetChanged();
    }

    private NLDevice getDevice(int i) {
        NLDevice deviceTableInfo = (NLDevice) deviceListAdapter.getItem(i);
        return deviceTableInfo;
    }

    /**
     * @param i
     */
    private void setDefault(int i) {
//    	DeviceListAdapter.DeviceTableInfo deviceTableInfo = (DeviceListAdapter.DeviceTableInfo)deviceListAdapter.getItem(i);
//        dbManager.setD(deviceTableInfo.id);
//        deviceListAdapter.notifyDataSetChanged();
    }

    private void showAddDeviceDialog() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhmmss");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"添加蓝牙设备", "取消"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        startBluetoothSearch();
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }


                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeviceManageDialog(final int index) {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.More_AddNewDevice, this);
        if (index == deviceListAdapter.getCount() - 1) {
            showAddDeviceDialog();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"连接", "删除"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:
                                connectTo(getDevice(index));
                                break;
                            case 1: {
                                deleteDevice(index);

                            }
                            break;
                            default:
                                break;
                        }
                        dialogInterface.dismiss();
                    }

                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSearch != null)
                bluetoothSearch.destorySearch();
        } catch (Exception e) {
            LogUtil.print(e);
        }

    }

    /**
     * Created by More on 14-1-9.
     */
    public static class DeviceListAdapter extends BaseAdapter {

        private Context context;

        private DeviceSp dbManager;

        private List<NLDevice> devices;

        private String presentDeviceName;

        public void setPresentDeviceName(String presentDeviceName) {
            this.presentDeviceName = presentDeviceName;
        }

        public DeviceListAdapter(Context context) {
            this.context = context;
            dbManager = DeviceSp.getInstance();
            devices = queryDevice();
        }

        public List<NLDevice> queryDevice() {
            if (null == devices) {
                devices = new ArrayList<NLDevice>();
            }
            devices.clear();

            devices.addAll(dbManager.getAllDevices());
            devices.add(new NLDevice());
            return devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int i) {
            return devices.get(i);
        }

        public void updateDevice() {
            devices = queryDevice();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (null == convertView) {
                convertView = LinearLayout.inflate(context, R.layout.device_list_item, null);
                holder = new ViewHolder();
                holder.deviceNmae = (TextView) convertView.findViewById(R.id.device_name);
                holder.connectTypeIcon = (ImageView) convertView.findViewById(R.id.connect_type_icon);
                holder.isDefault = (TextView) convertView.findViewById(R.id.is_default);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NLDevice deviceTableInfo = (NLDevice) getItem(i);
            if (null == deviceTableInfo.getName()) {
                holder.deviceNmae.setText("添加新设备");
                holder.deviceNmae.setTextColor(context.getResources().getColor(R.color.blue_0c79ff));
            } else {
                holder.deviceNmae.setTextColor(context.getResources().getColor(R.color.black));
                holder.deviceNmae.setText(deviceTableInfo.getName());

                if (null != presentDeviceName && presentDeviceName.equals(deviceTableInfo.getName())) {
                    holder.deviceNmae.setTextColor(context.getResources().getColor(R.color.red));
                }

//                 if(DeviceTableEntity.TRUE.equals(deviceTableInfo.defaultStr)){
//                     holder.isDefault.setVisibility(View.VISIBLE);
//
//                 }else{
//                     holder.isDefault.setVisibility(View.INVISIBLE);
//                 }
            }
            return convertView;
        }

        private class ViewHolder {

            public ImageView connectTypeIcon;

            public TextView deviceNmae;

            public TextView isDefault;

        }

//        public class DeviceTableInfo{
//        	public String id;
//        	public String name;
//        	public String defaultStr;
//        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

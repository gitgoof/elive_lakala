package com.lakala.platform.swiper.devicemanager.connection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.Session;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.platform.swiper.devicemanager.bluetooth.BluetoothSearch;
import com.lakala.platform.swiper.devicemanager.bluetooth.NLDevice;
import com.lakala.platform.swiper.devicemanager.bluetooth.OnBluetoothEnableListener;
import com.lakala.platform.swiper.devicemanager.bluetooth.OnDiscoveryFinishedListener;
import com.lakala.platform.swiper.devicemanager.connection.base.BaseConnectionManager;
import com.lakala.platform.swiper.devicemanager.connection.base.ConnectionStateListener;
import com.lakala.platform.swiper.devicemanager.connection.devicevalidate.DeviceOnlineValidate;
import com.lakala.platform.swiper.devicemanager.connection.devicevalidate.DeviceOnlineValidateListener;
import com.lakala.platform.swiper.devicemanager.connection.devicevalidate.DeviceValidateEvent;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManager;
import com.loopj.lakala.http.RequestParams;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by More on 15/1/15.
 */
public class MultiConnectionManager extends BaseConnectionManager implements MultiConnectionManagerInt {

    private BluetoothSearch bluetoothSearch;

    private DeviceOnlineValidate deviceOnlineValidate;

    private SwiperManager swiperManager;

    private Handler handler;

    boolean isDefaultDeviceFound = false;

    private List<NLDevice> deviceList = new ArrayList<NLDevice>();

    private Session session = ApplicationEx.getInstance().getSession();

    private CountDownLatch latch = new CountDownLatch(1);

    private Object invokeSync = new Object();

    public MultiConnectionManager(Context context, ConnectionStateListener listener) {
        super(context, listener);
        this.context = context;
        bluetoothSearch = new BluetoothSearch(context.getApplicationContext());
        swiperManager = SwiperManager.getInstance(swiperDetection);
        LogUtil.print("QB","CurrentSwipeType:"+swiperManager.getCurrentSwipeType());
        if(swiperManager.getCurrentSwipeType() != ESwiperType.Bluetooth ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swiperManager.setSwiperType(ESwiperType.Bluetooth);
                }
            });

        }
        swiperManager.setListener(swiperDetection);
        swiperManager.setEnableListener(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler = new Handler();
            }
        });
        validating = false;
        deviceOnlineValidate = new DeviceOnlineValidate();
    }

    private void checkQv30e(){
        //检测是否音频的刷卡器
        //Run in thread

        if(context == null){
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swiperManager.setSwiperType(ESwiperType.QV30E);
            }
        });

        startValidate();

    }


    private SwiperDetection swiperDetection = new SwiperDetection(){

        @Override
        public void onDeviceConnected(SwiperDefine.SwiperPortType type) {
            if(type == SwiperDefine.SwiperPortType.TYPE_AUDIO){
                onPhyDeviceInsert();

                //开启音频刷卡器搜索
                if(swiperManager.getCurrentSwipeType() != ESwiperType.QV30E)
                    onConnectionState(SwiperProcessState.DEVICE_PLUGGED);//提示去检测连接设备

                checkQv30e();
            }
        }

        @Override
        public void onDeviceDisconnected(SwiperDefine.SwiperPortType type) {
            if(type == SwiperDefine.SwiperPortType.TYPE_AUDIO){

                phyDeviceUsable = false;
                MutexThreadManager.runThread("onDeviceDisconnected",new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await(10, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (invokeSync){

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (validating || swiperManager.isDeviceConnected()) {
                                        return;
                                    }
                                    if (!bluetoothSearch.isSearching() && !isTipsShowing()){
                                            finish();
                                    }else{
                                        onConnectionState(SwiperProcessState.SEARCHING);
                                    }

                                }
                            },500);
                        }
                    }
                });
            }

        }

        @Override
        public void onCurrentConnected() {
            super.onCurrentConnected();
        }

        @Override
        public void onCurrentDisconnected() {

        }

        @Override
        public void onInterrupted() {
            finish();
        }
    };


    /**
     * 联网签到回调, 这会是最终的出口
     */
    private DeviceOnlineValidateListener deviceOnlineValidateListener = new DeviceOnlineValidateListener() {

        @Override
        public void onEvent(DeviceValidateEvent deviceValidateEvent) {

            switch (deviceValidateEvent){
                case Disable:
                    swiperNotOpenDialog();
                    break;
                case Imsi_error:
                    showMessageAndExit("刷卡器与当前设备冲突,请更换刷卡器");
                    break;
                case Unusable:
                    String tel = ApplicationEx.getInstance().getUser().getAppConfig()
                            .getLakalServantTel();
                    String formatString = "刷卡器不可用，请更换刷卡器。如有疑问请致电%s";
                    String msg = String.format(formatString, tel);
                    showMessageAndExit(msg,true);
                    break;
                case User_Conflict:
//                    showUserError("用户身份冲突,请更换刷卡器或重新验证");
                    showUserError("刷卡器与您的登录手机号码冲突，点击“重新登录”，输入开通刷卡器时的手机号码登录。");
                    break;
                case Error:

                    badNetWorkDialogd();
                    break;
                case Enable:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(swiperManager.getCurrentSwipeType() == ESwiperType.QV30E){
                                NLDevice nlDevice  = new NLDevice();
                                nlDevice.setName("音频" + ApplicationEx.getInstance().getSession().getCurrentKSN());
                                swiperManager.setNlDevice(nlDevice);
                            }
                            DeviceSp.getInstance().saveDeviceDefault(swiperManager.getNlDevice());
                        }
                    });
                    LogUtil.print("Devices is Enable");
                    break;
            }
            if(deviceValidateEvent == DeviceValidateEvent.Enable){
                onConnectionState(SwiperProcessState.SIGN_UP_SUCCESS);
            }else if(deviceValidateEvent == DeviceValidateEvent.Disable){
                onConnectionState(SwiperProcessState.NEW_DEVICE);
            }else{
                onConnectionState(SwiperProcessState.SIGN_UP_FAILED);
                if(swiperManager.getCurrentSwipeType() == ESwiperType.Bluetooth
                        && deviceValidateEvent != DeviceValidateEvent.Disable)
                    swiperManager.disconnect();
            }

        }
    };

    /**
     * 有可用的物理外接设备
     */
    private boolean phyDeviceUsable = false;

    /*
     *  正在验证设备
     */
    private boolean validating = false;

    private void startValidate(){


        MutexThreadManager.runThread("validate thread", new Runnable() {
            @Override
            public void run() {

                hideTips();

                String ksn = "";
                try{
                    validating = true;

                    //获取ksn作为 设备是否匹配的标识
                    onConnectionState(SwiperProcessState.DEVICE_PLUGGED);

                    if(getDefaultSwiperType() == SwiperDefine.SwiperPortType.TYPE_AUDIO)
                        Thread.sleep(1000);
                    ksn = swiperManager.getKsn();
                    swiperManager.setCurrentValidKsn();

                }catch (Exception e){
                    LogUtil.print(e);
                }
                validating = false;
                if("".equals(ksn)){
                    //未取到,说明设备不合法
                    showDialogIllegalDevice();

                }else{

                    if(swiperManager.getCurrentSwipeType() == ESwiperType.QV30E)
                        onPhyDeviceInsert();
                    onConnectionState(SwiperProcessState.ONLINE_VALIDATING);

                    validate(ksn);

                }

            }
        });


    }

    private void validate(final String ksn){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(TerminalKey.hasKey(ksn)){
                    //已经连接过，并且签到成功
                    onConnectionState(SwiperProcessState.SIGN_UP_SUCCESS);
                    return;
                }

                deviceOnlineValidate.setDeviceOnlineValidateListener(deviceOnlineValidateListener);
                deviceOnlineValidate.startValidate(ksn,swiperManager.getDeviceInfo());
            }
        });

    }

    private void showDialogIllegalDevice(){

        if(swiperManager.getCurrentSwipeType() == ESwiperType.Bluetooth){
            illegalBluetoothDevice();
        }else{
            if(!bluetoothSearch.isEnable()){
                illegalAudioDevice();
                return;
            }
            //蓝牙是开启的,并且已完成蓝牙搜索
            if(!bluetoothSearch.isSearching()){
                showSearchResult();
            }else{
                onConnectionState(SwiperProcessState.SEARCHING);
            }
            return;
        }

    }
    
    private void onConnectionState(SwiperProcessState state){
        LogUtil.print("SwiperProcessState =" + state);
        if(state == SwiperProcessState.DEVICE_PLUGGED){
            hideTips();
        }
        listener.onConnectionState(state);

    }


    @Override
    public void checkConnection(Context context) {

        if(context != null){
            this.context = context;
        }
        hideTips();
        if(swiperManager == null){
            swiperManager = SwiperManager.getInstance(swiperDetection);
        }
        swiperManager.setListener(swiperDetection);

        boolean isDevicePre = isDevicePre();
        LogUtil.print("isDevicePre" + isDevicePre);
        if(isDevicePre){

            startValidate();

//            if(isDeviceValidate() && swiperManager.getCurrentPortType() != SwiperDefine.SwiperPortType.TYPE_BLUETOOTH){
//                onConnectionState(SwiperProcessState.SIGN_UP_SUCCESS);
//            }else{
//                startValidate();
//            }
            return;
        }

        //无设备连接 设置链接设备为物理插入的检测连接
        swiperManager.setEnableListener(true);//会启动音频检测, 如果是旧的sdk 设置刷卡器类型也是一样的效果
        starBtSearch();

    }

    private OnDiscoveryFinishedListener onDiscoveryFinishedListener = new OnDiscoveryFinishedListener() {
        @Override
        public void onFinished(Set<NLDevice> nlDevices) {

            if(isDevicePre()){

            }

        }

        @Override
        public void onTargetDeviceFound(NLDevice nlDevice) {

        }
    };

    private void starBtSearch(){
        if(bluetoothSearch == null){
            bluetoothSearch = new BluetoothSearch(context);
        }
        String defaultAdd = "";

        NLDevice nlDevice = DeviceSp.getInstance().getDefaultDevice();
        if(nlDevice != null ){
            defaultAdd = nlDevice.getMacAddress();
        }

        if (bluetoothSearch.isEnable()) {

            onConnectionState(SwiperProcessState.SEARCHING);
            deviceList.clear();
            bluetoothSearch.startDiscoveryForDefineTime( 10 * 1000, defaultAdd, new OnDiscoveryFinishedListener() {
                @Override
                public void onFinished(Set<NLDevice> nlDevices) {

                    onSearchFinished(nlDevices);
                    latch.countDown();
                }

                @Override
                public void onTargetDeviceFound(NLDevice nlDevice) {

                    isDefaultDeviceFound = true;
                    swiperManager.setSwiperType(ESwiperType.Bluetooth);
                    swiperManager.setConnectionDevice(nlDevice);

                    deviceList.add(nlDevice);
                    startValidate();
                    latch.countDown();

                }
            });

        }else {
            requestBluetoothOpen();

        }
    }

    private void onSearchFinished(Set<NLDevice> nlDevices){

        if (validating || isDevicePre() || context == null) {
           return;
        }

        if(isDefaultDeviceFound){
            isDefaultDeviceFound = false;
            return;
        }

        onConnectionState(SwiperProcessState.FINISH_SEARCHING);

        deviceList.clear();
        deviceList.addAll(nlDevices);
        showSearchResult();
    }

    /**
     * 如音频口无设备，客户端提示用户：没有找到刷卡器，请打开蓝牙或把刷卡器插入耳机孔。
     * 左侧按钮“帮助”，
     * 右侧按钮“确定”（进入连接刷卡器页面到提示用户，时间为10-15秒），在应用内打开蓝牙
     */
    private void noAudioDeviceWithBluetoothClosed(){

        onConnectionState(SwiperProcessState.NONE_AVAILABLE_AUDIO_DEVICE_WITH_BLUETOOTH_CLOSED);
        com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();
        alertDialog.setCancelable(false);
        alertDialog.setMessage("没有找到刷卡器，请打开蓝牙");
        alertDialog.setButtons(new String[]{ applicationContext.getString(R.string.ui_cancel), applicationContext.getString(R.string.request_open_bluetooth)});
        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        finish();
                        break;
                    case 1:
                        if(!bluetoothSearch.isEnable()){
                            requestEnableBluetooth();
                        }
                        break;
                }
            }
        });

        showTips(alertDialog);

    }

    private void requestEnableBluetooth(){

        onConnectionState(SwiperProcessState.OPENING_BLUETOOTH);
        bluetoothSearch.enableBluetooth(new OnBluetoothEnableListener() {
            @Override
            public void onEnableResult(boolean b) {

                if (b) {
                    checkConnection(null);
                } else {
                    finish();
                    ToastUtil.toast(context,"开启蓝牙失败");
                }
            }
        });
    }

    /**
     * 联网验证备失败
     */
    private void badNetWorkDialogd(){
        com.lakala.ui.dialog.AlertDialog builder = new com.lakala.ui.dialog.AlertDialog();

        builder.setMessage("当前网络不佳,无法验证您的设备");
        builder.setCancelable(false);
        builder.setButtons(new String[]{applicationContext.getString(R.string.ui_certain)});
        builder.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                finish();
            }
        });
        showTips(builder);
    }

    /**
     * 在应用内进行蓝牙配对/连接；配对/连接失败，
     * 提示用户：设备未连接成功，请重新连接；左侧按钮为取消，右侧按钮为重新连接；
     */
    private void illegalBluetoothDevice(){



        //当前没有合适的蓝牙设备, 恢复对音频的监听
//        if (swiperManager.getCurrentSwipeType() != ESwiperType.QV30E) {
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    swiperManager.setSwiperType(ESwiperType.QV30E);
//                }
//            });
//
//
//        }

        com.lakala.ui.dialog.AlertDialog builder = new com.lakala.ui.dialog.AlertDialog();

        builder.setMessage("设备未连接成功，请重新连接");
        builder.setCancelable(false);
        builder.setButtons(new String[]{applicationContext.getString(R.string.ui_cancel), applicationContext.getString(R.string.reconnect)});

        builder.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        finish();
                        break;
                    case 1:
                        if (null == deviceList || deviceList.size() == 0) {
                            startValidate();
                        } else {
                            showSearchResult();
                        }
                        break;
                }
            }
        });

        showTips(builder);

    }


    /**
     * 如音频口连接设备，客户端获取KSN，获取失败，
     * 提示用户：现在连接的刷卡器不适配，请打开蓝牙或重新连接刷卡器
     */
    private void illegalAudioDevice(){

        com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();

        alertDialog.setMessage("现在连接的刷卡器不适配，请打开蓝牙或重新连接刷卡器");

        alertDialog.setButtons(new String[]{applicationContext.getString(R.string.ui_cancel), applicationContext.getString(R.string.ui_certain)});

        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        requestEnableBluetooth();
                        break;
                }
            }
        });
        showTips(alertDialog);

    }


    /**
     * 显示蓝牙搜索结果
     */
    private List<String> deviceNames;
    private void showSearchResult(){

        if( validating){
            return;
        }

//        if((swiperManager.getCurrentSwiperType() == SwiperType.QV30E && swiperManager.isSwiperTypeMatched()) || swiperManager.isSwiperValid()){
//            return;
//        }
        if(deviceList == null || deviceList.size() == 0){
            noneAvailableBluetoothDevice();
            return;
        }
        //show bluetooth devices
        if(deviceNames != null){
            deviceNames.clear();
        }else{
            deviceNames = new ArrayList<String>();
        }

        for(int i = 0; i < deviceList.size() ; i++){
            deviceNames.add(deviceList.get(i).getName() == null ? "Unknown Device" : deviceList.get(i).getName());
        }

        final com.lakala.ui.dialog.BluetoothListDialog builder = new com.lakala.ui.dialog.BluetoothListDialog();
        builder.setButtons(new String[]{applicationContext.getString(R.string.button_ok)});
        builder.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){

            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                int position = builder.getSelectedPosition();
                swiperManager.setSwiperType(ESwiperType.Bluetooth);
                swiperManager.setConnectionDevice(deviceList.get(position));
                startValidate();

            }

            @Override
            public boolean onKeyEvent(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    finish();
                    return true;
                }
                return super.onKeyEvent(dialog, keyCode, keyEvent);
            }
        });
        builder.setTitle("请选择要连接的蓝牙收款宝");

        builder.setData(deviceNames);

        builder.setCancelable(false);


        showTips(builder);

    }

    /**
     * 修改---刷卡器属于关机，未开启状态，提示此信息
     * 搜索音频口是否连接设备，未搜索到音频设备，
     * 提示用户：没有连接设备，请连接拉卡拉刷卡器；确认后进入设备管理界面；
     */
    private void noneAvailableBluetoothDevice(){

        com.lakala.ui.dialog.AlertDialog builder = new com.lakala.ui.dialog.AlertDialog();

        builder.setCancelable(false);
        builder.setMessage("没有找到刷卡器，请确保蓝牙刷卡器已开启");

        builder.setButtons(new String[]{applicationContext.getString(R.string.ui_cancel), applicationContext.getString(R.string.restart_search)});
        builder.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        finish();
                        break;
                    case 1:
                        checkConnection(null);
                        break;
                }
                dialog.dismiss();
            }
        });

        showTips(builder);

    }

    private com.lakala.ui.dialog.AlertDialog tipsDialog;

    public synchronized void hideTips(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(tipsDialog != null && tipsDialog.isShowing()){
                    LogUtil.print(getClass().getName(), "Hide tips is called" + tipsDialog.getMessage());
                    tipsDialog.dismissAllowingStateLoss();

                }

            }
        });
    }

    private boolean isTipsShowing(){
        return tipsDialog == null ? false : tipsDialog.isShowing();
    }

    private void showTips(final com.lakala.ui.dialog.AlertDialog builder){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                synchronized (invokeSync){

                    if(tipsDialog != null && tipsDialog.isShowing()){
                        LogUtil.print(getClass().getName(), "Hide tips" + tipsDialog.getMessage());

                        tipsDialog.dismissAllowingStateLoss();
                    }
                    tipsDialog = builder;
                    tipsDialog.show(((FragmentActivity)context).getSupportFragmentManager());
                }

            }
        });
    }



    private void requestBluetoothOpen(){
        if (!isDevicePre()) {
            noAudioDeviceWithBluetoothClosed();
        }else{
            startValidate();
        }
    }


    @Override
    public SwiperDefine.SwiperPortType getDefaultSwiperType() {
        return null;
    }

    @Override
    public void destroy() {
        context = null;
        swiperManager.setListener(null);
//        swiperManager.setEnableListener(false);
        if(bluetoothSearch != null){
            bluetoothSearch.stopDiscovery();
        }
    }

    @Override
    public boolean isPhyDeviceInsert() {

        return phyDeviceUsable;
    }

    @Override
    public void onPhyDeviceInsert() {
        phyDeviceUsable = true;
    }

    @Override
    public boolean isDeviceValidate() {
        String ksn = ApplicationEx.getInstance().getSession().getCurrentKSN();

        return !"".equals(ksn) && TerminalKey.hasKey(ksn);
    }



    @Override
    public SwiperDefine.SwiperPortType getPreDeviceType() {
        return null;
    }

    @Override
    public boolean isDevicePre() {
        LogUtil.print("QB",swiperManager.isDeviceConnected()+"");
        return swiperManager.isDeviceConnected() && !"".equals(ApplicationEx.getInstance().getSession().getCurrentKSN());
    }

    @Override
    public void onlineValidate(DeviceOnlineValidateListener deviceOnlineValidateListener) {

    }

    private void showMessageAndExit(String msg){
        showMessageAndExit(msg,false);
    }

    private void showMessageAndExit(String msg,boolean containPhone){
        com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();
        alertDialog.setTitle(applicationContext.getString(R.string.ui_tip));
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{applicationContext.getString(R.string.ui_certain)});
        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        ((Activity)context).finish();
                        break;

                }
            }
        });
        if(containPhone){
            alertDialog.setMessageAutoLinkMask(Linkify.PHONE_NUMBERS);
        }

        showTips(alertDialog);
    }
    private void showUserError(String msg){
        final com.lakala.ui.dialog.AlertDialog alertDialog = new com.lakala.ui.dialog.AlertDialog();
        alertDialog.setMessage(msg);
        alertDialog.setTitle("用户身份异常");
        alertDialog.setButtons(new String[]{"取消","重新登录"});
        alertDialog.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){
            @Override
            public void onDestroy() {

            }

            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        ((Activity)context).finish();
                        break;
                    case 1:

                        try {
                            //最后一次登录标致为false
                            UserDao.getInstance().setAllUserLoginFalse();
                            //保存当前登录用户名
                            String loginName = ApplicationEx.getInstance().getUser().getLoginName();
                            LklPreferences.getInstance().putString(LKlPreferencesKey.KEY_LOGIN_NAME, loginName);
                        }catch (Exception e){
                            LogUtil.print(e);
                        }
                        //清除用户信息
                        ApplicationEx.getInstance().getSession().clear();

                        try {
                            Class<?> loginClass = Class.forName(
                                    "com.lakala.shoudan.activity.login.LoginActivity");
                            Intent intent = new Intent(context,loginClass);
                            ((Activity)context).startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        showTips(alertDialog);
    }

    private Context applicationContext = ApplicationEx.getInstance();


    private void swiperNotOpenDialog(){

        com.lakala.ui.dialog.AlertDialog builder = new com.lakala.ui.dialog.AlertDialog();
        builder.setCancelable(false);
        builder.setMessage(String.format("发现新刷卡器，此刷卡器即将与用户%s绑定，下次使用此刷卡器时请用该用户名登录", ApplicationEx.getInstance().getUser().getLoginName()));
        builder.setButtons(new String[]{applicationContext.getString(R.string.ui_give_up), applicationContext.getString(R.string.ui_continue)});

        builder.setDialogDelegate(new com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate(){

            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                switch (index){
                    case 0:
                        finish();
                        break;
                    case 1:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swiperBind();
                            }
                        });
                        break;

                }
            }
        });

        showTips(builder);

    }

    private void swiperBind(){

        onConnectionState(SwiperProcessState.BINDING);

        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context, "v1.0/terminal/user", HttpRequest.RequestMethod.POST, true);

        if(swiperManager != null && swiperManager.getDeviceInfo() != null){

            GetDeviceInfo temp = swiperManager.getDeviceInfo();

            businessRequest.getRequestParams().put("mver", temp.getFirmwareVersion());
            businessRequest.getRequestParams().put("aver", temp.getAppVersion());
            businessRequest.getRequestParams().put("pver", temp.getCommandVersion());
        }

        businessRequest.setResponseHandler(new ServiceResultCallback(){
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(BusinessRequest.SUCCESS_CODE.equals(resultServices.retCode)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toast(context, "设备绑定成功!");
                            checkConnection(null);
                        }
                    });
                }else{
                    showMessageAndExit(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                showMessageAndExit("网络连接异常,请稍后重试");
            }
        });

        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("psamNo", ApplicationEx.getInstance().getSession().getCurrentKSN());
        businessRequest.execute();

    }


}

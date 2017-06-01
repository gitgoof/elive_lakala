package com.lakala.core.swiper.Adapter;

import android.content.Context;
import android.text.TextUtils;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.cswiper6.bluetooth.CSwiperController;
import com.lakala.cswiper6.bluetooth.CommandProtocolVer;
import com.lakala.library.util.LogUtil;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.emv.AIDConfig;
import com.newland.mtype.module.common.emv.CAPublicKey;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by wangchao on 14-4-10.
 */
public class SwiperAdapterBluetooth extends SwiperEmvAdapter implements CSwiperController.CSwiperStateChangedListener, CSwiperController.EmvControllerListener {

    private SwiperAdapterListener listener;
    private CSwiperController controller;

    public SwiperAdapterBluetooth(Context context) {
        try {
            this.controller = new CSwiperController(context, this, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SwiperAdapterBluetooth(Context context, String[] params) {
        try {
            this.controller = new CSwiperController(context, params, this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SwiperEmvAdapter
     */
    @Override
    public boolean setConnectParams(String[] params) {
        if (controller == null)
            return false;
        return controller.setConnectParams(params);
    }

    @Override
    public void addAID(AIDConfig aidConfig) {
        if (controller == null) return;
        try {
            controller.addAID(aidConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAID(byte[] aid) {
        if (controller == null) return;
        try {
            controller.deleteAID(aid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAllAID() {
        if (controller == null) return;
        try {
            controller.clearAllAID();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCAPublicKey(byte[] rid, CAPublicKey capk) {
        if (controller == null) return;
        try {
            controller.addCAPublicKey(rid, capk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCAPublicKey(byte[] rid, int index) {
        if (controller == null) return;
        try {
            controller.deleteCAPublicKey(rid, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAllCAPublicKey(byte[] rid) {
        if (controller == null) return;
        try {
            controller.clearAllCAPublicKey(rid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSecondIssuance(SecondIssuanceRequest request) {
        if (controller == null) return;
        try {
            controller.doSecondIssuance(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelPininput() {
        if (controller == null) return;
        controller.cancelPininput();
    }

    @Override
    public void cancelCardRead() {
        if (controller == null) return;
        controller.cancelCardRead();
    }

    @Override
    public void startSwiper(String amount, ModuleType[] cardReaderModuleTypes) {
        if (controller == null) return;
        if (controller.getCSwiperState() == CSwiperController.CSwiperControllerState.STATE_IDLE) {
            controller.startCSwiper(amount, cardReaderModuleTypes, null, null, null);
        }
    }

    @Override
    public void startSwiper(String amount, ModuleType[] cardReaderModuleTypes, final byte[] businessCode, final byte[] serviceCode, final byte[] additionalMsg) {
        if (controller == null) return;
        if (controller.getCSwiperState() == CSwiperController.CSwiperControllerState.STATE_IDLE) {
            LogUtil.print(cardReaderModuleTypes.toString() + "size" + cardReaderModuleTypes.length);
            controller.startCSwiper(amount, cardReaderModuleTypes, businessCode, serviceCode, additionalMsg);
        }
    }

    @Override
    public Map<String, Object> getSwipeCardExtendData() {
        if (controller == null) return null;
        return controller.getSwipeCardExtendData();
    }

    @Override
    public byte[] fetchProdAllocation() {
        if (controller == null) return null;
        return null;
    }

    @Override
    public void cancelEmv(boolean b) {
        if (controller == null) return;
        controller.cancelEmv(b);
    }

    @Override
    public boolean isDevicePresent() {
        if (controller == null)
            return false;
        return controller.isDevicePresent();
    }

    @Override
    public void startSwiper() {

    }

    @Override
    public void stopSwiper() {
        if (controller == null) return;
        controller.stopCSwiper();
    }

    @Override
    public void deleteSwiper() {
        if (controller == null) return;
        controller.deleteCSwiper();
    }

    @Override
    public SwiperDefine.SwiperControllerState getCSwiperState() {
        if (controller == null)
            return null;
        return SwiperDefine.SwiperControllerState.valueOf(controller.getCSwiperState().toString());
    }

    @Override
    public String getKSN() {
        if (controller == null) return "";
        return controller.getKSN();
    }

    @Override
    public ESwiperType getSwiperType() {
        return ESwiperType.Bluetooth;
    }

    @Override
    public void setListener(SwiperAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void startSwiper(String amount) {

    }

    @Override
    public void startInputPIN() {
        if (controller == null) return;
        controller.startInputPin();

    }

    @Override
    public void setStartParameter(int index, Object data) {
        if (controller == null) return;
        controller.setStartParameter(index, data);
    }

    @Override
    public void resetScreen() {
        if (controller == null) return;
        controller.resetScreen();
    }

    /**
     * CSwiperStateChangedListener
     */

    @Override
    public void onDevicePlugged() {
        if (listener == null) return;
        listener.onDevicePlugged();
    }

    @Override
    public void onDeviceUnplugged() {
        if (listener == null) return;
        listener.onDeviceUnplugged();
    }

    @Override
    public void onWaitingForDevice() {
        if (listener == null) return;
        listener.onWaitingForDevice();
    }

    @Override
    public void onDeviceConnected() {
        onDevicePlugged();
    }

    @Override
    public void onWaitingForCardSwipe() {
        if (listener == null) return;
        listener.onWaitingForCardSwipe();
    }

    @Override
    public void onWaitingForPinEnter() {
        if (listener == null || !(listener instanceof SwiperCollectionAdapterListener)) return;
        ((SwiperCollectionAdapterListener) listener).onWaitingForPinEnter();
    }

    @Override
    public void onError(String s) {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onError(s);
    }

    @Override
    public void onInterrupted() {
        if (listener == null) return;
        listener.onInterrupted();
    }

    @Override
    public void onTimeout() {
        if (listener == null) return;
        listener.onTimeout();
    }

    @Override
    public void onDecodeCompleted(String s, String s2, String s3, int i, int i2, int i3, String s4, String s5, String s6, String s7) {
        if (listener == null) {
            return;
        }
        listener.onDecodeCompleted(s, s2, s3, i, i2, i3, s4, s5, s6, s7);
    }

    @Override
    public void onPinInputCompleted(String radomNumber, String PIN, int length, byte[] macRandom, byte[] mac) {
        if (listener == null || !(listener instanceof SwiperCollectionAdapterListener)) return;
            ((SwiperCollectionAdapterListener) listener).onPinInputCompleted(radomNumber, PIN, length, macRandom, mac);
    }

    @Override
    public void onDecodeError(CSwiperController.DecodeResult decodeResult) {
        if (listener == null) return;
        listener.onDecodeError(SwiperDefine.SwiperControllerDecodeResult.valueOf(decodeResult.toString()));
    }

    @Override
    public void onDecodingStart() {
        if (listener == null) return;
        listener.onDecodingStart();
    }

    @Override
    public void onCardSwipeDetected() {
        if (listener == null) return;
        listener.onCardSwipeDetected();
    }

    @Override
    public void onNoDeviceDetected() {
        if (listener == null) return;
        listener.onNoDeviceDetected();
    }

    @Override
    public void onDeviceDisconnected() {
        onDeviceUnplugged();
    }

    /**
     * EmvControllerListener
     */

    @Override
    public void onRequestSelectApplication(EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onRequestSelectApplication(emvTransInfo);
    }

    @Override
    public void onRequestTransferConfirm(EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onRequestTransferConfirm(emvTransInfo);
    }

    @Override
    public void onRequestPinEntry(EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onRequestPinEntry(emvTransInfo);
    }

    @Override
    public void onRequestOnline(EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onRequestOnline(emvTransInfo);
    }

    @Override
    public void onEmvFinished(boolean b, EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onEmvFinished(b, emvTransInfo);
    }

    @Override
    public void onFallback(EmvTransInfo emvTransInfo) throws Exception {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onFallback(emvTransInfo);
    }

    @Override
    public void onQPBOCFinished(EmvTransInfo emvTransInfo) {
        if (listener == null || !(listener instanceof SwiperEmvAdapterListener)) return;
        ((SwiperEmvAdapterListener) listener).onQPBOCFinished(emvTransInfo);
    }

    @Override
    public void setCommandProtocolVer(CommandProtocolVer commandProtocolVer) {
        if(controller != null){
            controller.setCommandProtocolVer(commandProtocolVer);
        }
    }

    @Override
    public CommandProtocolVer getDeviceSupportedProtocol() {


        //去空格不区分大小写后判定 LKLV2.0
        //TODO
        if(controller != null){
            LogUtil.print(controller.getDeviceInfo().getCommandVersion());
            if(!TextUtils.isEmpty(controller.getDeviceInfo().getCommandVersion())){

                String cv = controller.getDeviceInfo().getCommandVersion();

                cv = cv.replace("LKLV", "");

                BigDecimal bigDecimal = new BigDecimal(cv);


                if(bigDecimal.compareTo(new BigDecimal(3.0)) >= 0){

                    return CommandProtocolVer.VERSION_THREE;
                }

                if(bigDecimal.compareTo(new BigDecimal(2.0)) >= 0){

                    return CommandProtocolVer.VERSION_TWO;

                }

            }
        }

        return CommandProtocolVer.VERSION_ONE;

    }

    @Override
    public GetDeviceInfo getDeviceInfo() {

        if(controller != null){
            return controller.getDeviceInfo();
        }

        return null;
    }

    @Override
    public boolean isSupportNCCARD() {
        if(controller != null){
            return controller.isSupportNCCARD();
        }
        return false;
    }
}

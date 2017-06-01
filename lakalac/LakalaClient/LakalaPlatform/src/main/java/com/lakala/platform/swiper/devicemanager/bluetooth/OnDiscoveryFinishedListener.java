package com.lakala.platform.swiper.devicemanager.bluetooth;



import java.util.Set;

/**
 * Created by More on 14-1-27.
 */
public interface OnDiscoveryFinishedListener {

    void onFinished(Set<NLDevice> nlDevices);

    void onTargetDeviceFound(NLDevice nlDevice);
}

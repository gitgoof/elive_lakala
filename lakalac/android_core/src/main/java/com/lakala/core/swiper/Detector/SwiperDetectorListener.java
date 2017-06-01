package com.lakala.core.swiper.Detector;

import com.lakala.core.swiper.SwiperController;

/**
 * Created by Vinchaos api on 14-1-6.
 */
public interface SwiperDetectorListener {

    void onConnected(SwiperDetector detector);

    void onDisconnected(SwiperDetector detector);

    SwiperController getSwiperController();
}

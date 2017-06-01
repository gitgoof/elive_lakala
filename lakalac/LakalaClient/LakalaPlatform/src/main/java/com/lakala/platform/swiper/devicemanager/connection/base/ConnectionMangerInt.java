package com.lakala.platform.swiper.devicemanager.connection.base;


import android.content.Context;

import com.lakala.core.swiper.SwiperDefine;

/**
 * Created by More on 14-8-25.
 */
public interface ConnectionMangerInt {

    void checkConnection(Context context);

    SwiperDefine.SwiperPortType getDefaultSwiperType();

    void destroy();

}

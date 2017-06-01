package com.lakala.platform.swiper.devicemanager.connection.devicevalidate;

/**
 * Created by More on 15/1/15.
 */
public enum DeviceValidateEvent {


    Disable,//未开通

    User_Conflict,//用户冲突

    Enable,// 可用

    Unusable,//不可用

    Imsi_error,//imsi绑定错误

    Error,//验证时异常

}

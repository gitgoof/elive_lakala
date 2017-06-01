package com.lakala.platform.http;

import android.content.Context;
import android.os.Build;

import com.lakala.library.encryption.Digest;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.AppUtil;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;

import java.util.Calendar;
import java.util.Date;


/**
 * 设置请求公共参数
 * Created by xyz on 13-12-17.
 */
public class CommonRequestParams extends HttpRequestParams{

    protected void addCommonParams(){
        Context context = ApplicationEx.getInstance();

        this.put("_Platform","android");
        this.put("_DeviceModel", Build.MODEL);
        this.put("_OSVersion", DeviceUtil.getPhoneOSVersion());
        this.put("_AppVersion", AppUtil.getAppVersionCode(context));
        this.put("_AppBundleVersion",""); //客户端业务模块版本号

        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();

        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );

        String md5Value = Digest.md5(guid);

        this.put("_Guid",md5Value);
        this.put("_TimeStamp",new Date().getTime()+"");
        this.put("_DeviceId",deviceId);
        this.put("_OriginalDeviceId", DeviceUtil.getIMEI(context));
        this.put("_SubChannelId","10000001");
        this.put("_RefChannelId", AppUtil.getAppChannel(context));

        this.put("_AccessToken", ApplicationEx.getInstance().getSession().getUser().getAccessToken());
        this.put("_RefreshToken",ApplicationEx.getInstance().getSession().getUser().getRefreshToken());
    }

}

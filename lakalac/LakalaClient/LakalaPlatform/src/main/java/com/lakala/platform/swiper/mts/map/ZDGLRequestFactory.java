package com.lakala.platform.swiper.mts.map;

import android.content.Context;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.http.BusinessRequest;

/**
 * 信用卡账单管理
 *
 * Created by Jerry on 14-1-23.
 */
public class ZDGLRequestFactory {

    private ZDGLRequestFactory() {
    }


    /**
     * 获取网点银行列表
     * @param BankId
     * @return
     */
    public static BusinessRequest banksQuery(Context context, String BankId){

        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "credit/banksQry.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("BankId", BankId);

        return request;
    }

    /**
     * 获取拉卡拉网点
     * @return
     */
    public static BusinessRequest storeInfoQry(Context context,
                                               String Nex,
                                                 String Ney,
                                                 String Swx,
                                                 String Swy,
                                                 String PageSize,
                                                 String PageStart ){
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "credit/storeInfoQry.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("Nex", Nex);
        request.getRequestParams().put("Ney", Ney);
        request.getRequestParams().put("Swx", Swx);
        request.getRequestParams().put("Swy", Swy);
        request.getRequestParams().put("PageSize", PageSize);
        request.getRequestParams().put("PageStart", PageStart);

        return request;
    }

}

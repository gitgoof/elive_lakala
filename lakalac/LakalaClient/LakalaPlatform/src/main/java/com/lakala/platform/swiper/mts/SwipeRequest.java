package com.lakala.platform.swiper.mts;

import android.content.Context;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.http.BusinessRequest;

/**
 * Created by wangchao on 14-2-12.
 */
public class SwipeRequest {


    /**
     * 终端状态查询/
     *
     * @param TerminalId 终端号
     */
    public static BusinessRequest queryTerminalState(Context context, String TerminalId) {

        String URL = BusinessRequest.SCHEME_MTS + "common/queryTerminalState.do";

        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, URL, HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("TerminalId", TerminalId);
        return request;
    }

    /**
     * 终端绑定
     *
     * @param TerminalId 终端号
     */
    public static BusinessRequest bindTerminal(Context context,
                                               String TerminalId,
                                               String IMSI,
                                               String TelecomOperators,
                                               String MobileModel,
                                               String MobileProduct,
                                               String MobileManuFacturer) {

        String URL = BusinessRequest.SCHEME_MTS + "common/bindTerminal.do";


        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, URL, HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("TerminalId", TerminalId);
        request.getRequestParams().put("IMSI", IMSI);
        request.getRequestParams().put("TelecomOperators", TelecomOperators);
        request.getRequestParams().put("MobileModel", MobileModel);
        request.getRequestParams().put("MobileProduct", MobileProduct);
        request.getRequestParams().put("MobileManuFacturer", MobileManuFacturer);

        return request;
    }

    /**
     * 终端登录
     *
     * @param TerminalId 终端号
     */
    @Deprecated
    public static BusinessRequest terminalLogin(Context context, String TerminalId) {

        String URL = BusinessRequest.SCHEME_MTS + "common/terminalLogin.do";

        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, URL, HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("TerminalId", TerminalId);

        return request;
    }

    /**
     * 刷卡器列表查询
     *
     * @return
     */
    public static BusinessRequest queryUnitInfoList(Context context) {

        String URL = BusinessRequest.SCHEME_MTS + "queryUnitInfoList.do";

        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, URL, HttpRequest.RequestMethod.POST, true);

        return request;
    }
}

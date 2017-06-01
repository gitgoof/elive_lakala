package com.lakala.library.exception;

/**
 * 网络异常处理类,所有网络相关异常通过此类包装。
 * 网络连接超时，服务端返回502，500等
 * Created by xyz on 14-1-2.
 */
public class HttpException extends BaseException{

    public static final int ERRCODE_REQUEST_RESPONSE_ERROR = 9999;
    /**请求超时（数据已发送）*/
    public static final int ERRCODE_REQUEST_TIMEOUT = 1000;
    /**不能连接到主机*/
    public static final int ERRCODE_REQUEST_CANNOT_CONNECT_TO_HOST = 1002;
    /**未知错误*/
    public static final int ERRCODE_REQUEST_UNKNOWN_ERROR = 1003;
    /**无网络*/
    public static final int ERRCODE_REQUEST_NOT_NETWORK   = 1004;
    /**服务器证书无效（可能使用了代理）*/
    public static final int ERRCODE_REQUEST_CERTIFICATE_INVAILD  = 1006;
    /**服务器证书过期（也有可能是本地时间不正确）*/
    public static final int ERRCODE_REQUEST_CERTIFICATE_HAS_BADDATE  = 1007;
    /**不能连接到国际互连网*/
    public static final int ERRCODE_REQUEST_NOT_INTERNET  = 1008;
    /**交易失败*/
    public static final int ERRCODE_REQUEST_TRANSACTION_FAIL = 4000;
    /**解析应答数据失败*/
    public static final int ERRCODE_REQUEST_PARASE_DATA_FAIL = 4001;

    //http response 状态码
    private int statusCode;

    public HttpException(int statusCode,String errorData,String errorMessage,Throwable e){
        super(errorMessage,e);
        this.errData = errorData;
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

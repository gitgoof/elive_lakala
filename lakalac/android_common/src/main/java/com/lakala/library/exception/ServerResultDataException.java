package com.lakala.library.exception;

/**
 * 服务端返回数据异常
 * 如Json解析异常等
 *
 * Created by jerry on 13-12-25.
 */
public class ServerResultDataException extends BaseException{

    /**
     * @param errData           异常发生的原因
     * @param errorMessage      异常信息
     * @param originalException 原始异常对象
     */
    public ServerResultDataException(String errData, String errorMessage, Exception originalException) {
        super(errData, errorMessage, originalException);
    }

    /**
     * @param errData           异常发生的原因
     * @param errorMessage      异常信息
     */
    public ServerResultDataException(String errData, String errorMessage) {
        super(errData, errorMessage);
    }

    /**
     * @param errorMessage      异常信息
     * @param originalException 原始异常对象
     */
    public ServerResultDataException(String errorMessage, Exception originalException) {
        super(errorMessage, originalException);
    }

    /**
     * @param errorMessage      异常信息
     */
    public ServerResultDataException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * @param originalException 原始异常对象
     */
    public ServerResultDataException(Exception originalException) {
        super(originalException);
    }
}

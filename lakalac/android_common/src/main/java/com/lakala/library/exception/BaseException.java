package com.lakala.library.exception;

/**
 * 基础异常类
 * errData              异常的详细数据,用此数据可以更确切的了解异常的发生原因
 * errorMessage         异常的描述信息
 * originalException    原始异常对象
 *
 * Created by jerry on 13-12-14.
 */
public class BaseException extends Exception{
    protected String errData      = "";

    public BaseException(
            String errData,
            String errorMessage,
            Throwable originalException) {

        super(errorMessage,originalException);
        this.errData = errData;
    }

    public BaseException(
            String errData,
            String errorMessage) {
        super(errorMessage);
        this.errData = errData;
    }

    public BaseException(
            String errorMessage,
            Throwable originalException) {
        super(errorMessage,originalException);
    }

    public BaseException(String errorMessage) {
        super(errorMessage);
    }

    public BaseException(Exception originalException) {
        super(originalException);
    }

    /**
     * 获取异常的详细数据
     */
    public String getData() {
        return errData;
    }
}

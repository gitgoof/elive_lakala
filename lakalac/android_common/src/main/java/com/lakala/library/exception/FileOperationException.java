package com.lakala.library.exception;

/**
 * 所有的文件IO操作异常
 *
 * Created by jerry on 13-12-25.
 */
public class FileOperationException extends BaseException{

    /**
     * @param errData           异常发生的原因
     * @param errorMessage      异常信息
     * @param originalException 原始异常对象
     */
    public FileOperationException(String errData, String errorMessage, Exception originalException) {
        super(errData, errorMessage, originalException);
    }

    /**
     * @param errData           异常发生的原因
     * @param errorMessage      异常信息
     */
    public FileOperationException(String errData, String errorMessage) {
        super(errData, errorMessage);
    }

    /**
     * @param errorMessage      异常信息
     * @param originalException 原始异常对象
     */
    public FileOperationException(String errorMessage, Exception originalException) {
        super(errorMessage, originalException);
    }

    /**
     * @param errorMessage      异常信息
     */
    public FileOperationException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * @param originalException 原始异常对象
     */
    public FileOperationException(Exception originalException) {
        super(originalException);
    }
}

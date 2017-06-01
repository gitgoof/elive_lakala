package com.lakala.library.exception;

/**
 * Created by Vinchaos api on 14-1-6.
 * 刷卡器异常
 */
public class SwiperException extends BaseException {

    public SwiperException(String errData, String errorMessage) {
        super(errData, errorMessage);
    }
}

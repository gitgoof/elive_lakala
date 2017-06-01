package com.lakala.library.exception;

/**
 * 缓存数据异常
 * Created by xyz on 14-1-2.
 */
public class CacheException extends BaseException{

    public CacheException(String cache,String errorMessage){
        super(cache,errorMessage);
    }
}

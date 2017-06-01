package com.lakala.core.http;

import com.lakala.library.exception.BaseException;

/**
 * Created by Michael on 14-10-14.
 */
public class IHttpRequestEvents {

    public void onStart(HttpRequest request){};

    public void onSuccess(HttpRequest request){};

    public void onFailure(HttpRequest request, BaseException exception){};

    public void onFinish(HttpRequest request){};

    public void onProgress(HttpRequest request, int bytesWritten, int totalSize){};

    public void onCancel(HttpRequest request){};
}

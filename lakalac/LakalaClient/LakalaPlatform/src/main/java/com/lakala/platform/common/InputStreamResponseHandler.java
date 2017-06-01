package com.lakala.platform.common;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.LogUtil;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by fengx on 2015/12/8.
 */
public abstract class InputStreamResponseHandler extends HttpResponseHandler{

    public abstract void onSuccess(InputStream inputStream);

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        LogUtil.print("statusCode::"+statusCode);
        LogUtil.print("responseBody::"+getResponseString(responseBody,getCharset()));
        onSuccess(new ByteArrayInputStream(responseBody));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        super.onFailure(statusCode, headers, responseBody, error);
        LogUtil.print("statusCode::"+statusCode);
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }
}

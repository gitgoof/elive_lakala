package com.lakala.platform.response;

/**
 * Created by More on 15/2/10.
 */
public interface ServiceResultCallback {

    public void onSuccess(ResultServices resultServices);

    public void onEvent(HttpConnectEvent connectEvent);

}

package com.lakala.elive.common.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    
	private final Gson mGson;
	private final Class<T> mClazz;
	private final Listener<T> mListener;
	private Map<String, String> headers;
	private Map<String, String> params;
    private String mRequestBody;
    
	public GsonRequest(int method, String url, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		mGson = new Gson();
	}

	public GsonRequest(int method, String url, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener,
                       Gson gson, Map<String, String> params) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		mGson = new Gson();
		this.params = params;
		this.headers = null;
	}

    public GsonRequest(int method, String url, String jsonRequest, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		mGson = new Gson();
		mRequestBody = jsonRequest;
		this.headers = null;
    }


    public GsonRequest(int method, String url, String jsonRequest, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener,Map<String, String> params) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = new Gson();
        mRequestBody = jsonRequest;
        this.params = params;
    }

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");  
        headers.put("Content-Type", "application/json;charset=UTF-8");
        return headers;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, "UTF-8");
			Log.i("g--","response:" + json);
			return Response.success(mGson.fromJson(json, mClazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return this.params;
	}
	
    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
    

}

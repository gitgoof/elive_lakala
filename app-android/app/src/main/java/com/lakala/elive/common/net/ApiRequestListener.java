package com.lakala.elive.common.net;

public interface ApiRequestListener {

	/**
	 * The CALLBACK for success API HTTP response
	 * 
	 *            the HTTP response
	 */
	void onSuccess(int method, Object obj);

	/**
	 * The CALLBACK for failure API HTTP response
	 * 
	 * @param statusCode
	 *            the HTTP response status code
	 */
	void onError(int method, String statusCode);
	
	
	
}

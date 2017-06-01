/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.common.SSLConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);
        
        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
        	// No maximum size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        }
        else
        {
        	// Disk cache size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }

        queue.start();

        return queue;
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack)
    {
    	return newRequestQueue(context, stack, -1);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }


    //add ssl support 2016 09 22

    public static RequestQueue createHttpsRequestQueue(Context context,HttpStack stack){
        File cacheDir=new File(context.getCacheDir(),DEFAULT_CACHE_DIR);
        String userAgent="volley/0";
        try{
            String packageName = context.getPackageName();
            PackageInfo info  = context.getPackageManager().getPackageInfo(packageName,0);
            userAgent = packageName + "/" + info.versionCode;
        }catch(PackageManager.NameNotFoundException ex){
            ex.printStackTrace();
        }
        if(stack==null){
            if(Build.VERSION.SDK_INT>=9){
                stack=new HurlStack(null,getSocketFactory(context));
            }else{
                stack=new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }
        Network network=new BasicNetwork(stack);
        RequestQueue queue=new RequestQueue(new DiskBasedCache(cacheDir),network);
        queue.start();
        return queue;
    }
    /**
     * create SocketFactory
     * @param context
     * @return
     */
    private static SSLSocketFactory getSocketFactory(Context mContext){
        try{
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }
            );
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustManagers = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                        }
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {

                        }
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {

                        }
                    }
            };
            KeyManager[] keyManagers = createKeyManagers(mContext);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers,trustManagers,new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static KeyManager[] createKeyManagers(Context mContext) throws CertificateException,
            IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        InputStream inputStream = mContext.getResources().getAssets().open("client.p12");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream, SSLConstants.ELIVE_SSL_CLENT_PASSWD.toCharArray());
        printKeystoreInfo(keyStore);//for debug
        KeyManager[] managers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//PKIX "X509")
        keyManagerFactory.init(keyStore,SSLConstants.ELIVE_SSL_CLENT_PASSWD.toCharArray());
        managers =keyManagerFactory.getKeyManagers();
        return managers;
    }

    private static void printKeystoreInfo(KeyStore keystore) throws KeyStoreException {
//      System.out.println("Provider : " +keystore.getProvider().getName());
//      System.out.println("Type : " +keystore.getType());
//      System.out.println("Size : " +keystore.size());

        Enumeration en = keystore.aliases();
        while (en.hasMoreElements()) {
            System.out.println("Alias: " +en.nextElement());
        }
    }



}


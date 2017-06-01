/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.lakala.http;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.lakala.library.BuildConfig;
import com.lakala.library.util.CertificateCode;
import com.lakala.library.util.InputStreamUtils;
import com.lakala.library.util.LogUtil;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * This file is introduced to fix HTTPS Post bug on API &lt; ICS see
 * http://code.google.com/p/android/issues/detail?id=13117#c14 <p>&nbsp;</p> Warning! This omits SSL
 * certificate validation on every device, use with caution
 */
public class MySSLSocketFactory extends SSLSocketFactory {

    private static final String TAG = "MySSLSocketFactory";
    private static final String KEY_STORE_TYPE_BKS = "BKS";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型
//    private static final String KEY_STORE_CLIENT_PATH = "";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_CLIENT_PATH = "MPOS_ANDROID_62_YYB.pfx";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_TRUST_PATH = "MPOS_ANDROID_62_YYB.crt";//客户端验证服务器端的证书库
    private static final String KEY_STORE_PASSWORD = "lakala";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "lakala";//客户端证书库密码
    SSLContext sslContext;

    private SSLContext getPropertySSLContext() throws NoSuchAlgorithmException {

        if(sslContext != null){
            return sslContext;
        }

        try{
            LogUtil.print(""+ Build.VERSION.SDK_INT) ;
            if(Build.VERSION.SDK_INT <= 15){
                supportV1_2 = false;
                return SSLContext.getInstance(TLS_V1);
            }else{
                supportV1_2 = true;
                return SSLContext.getInstance(TLS_V1_2);
            }
        }catch (NoSuchAlgorithmException e){
            LogUtil.print(e);
            supportV1_2 = false;
            return SSLContext.getInstance(TLS_V1);

        }



    }


    private String readCaCert() throws Exception {
        AssetManager assetManager = CertificateCode.application.getAssets();
        InputStream inputStream = assetManager.open(KEY_STORE_TRUST_PATH);
        return IOUtil.readFully(inputStream);
    }
    private InputStream readStream() throws Exception {
        return CertificateCode.application.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
    }
    /**
     * Creates a new SSL Socket Factory with the given KeyStore.
     *
     * @param truststore A KeyStore to create the SSL Socket Factory in context of
     * @throws java.security.NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws java.security.KeyManagementException    KeyManagementException
     * @throws java.security.KeyStoreException         KeyStoreException
     * @throws java.security.UnrecoverableKeyException UnrecoverableKeyException
     */
    public MySSLSocketFactory(Context context,KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, IOException {
        super(truststore);
        sslContext = getPropertySSLContext();
        if("内网测试".equals(CertificateCode.envir)||"外网测试".equals(CertificateCode.envir)){
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
        sslContext.init(null, new TrustManager[]{tm}, null);
        }else {
            try {
                SSLContextFactory.getInstance().makeContext(sslContext,readStream(), KEY_STORE_PASSWORD, readCaCert());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.print(TAG,"sslContext：init异常");
            }
        }
    }

    public static SSLSocketFactory getSslSocketFactory(Context context) {
        try {
            // 服务器端需要验证的客户端证书
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            // 客户端信任的服务器端证书
            KeyStore trustStore;

            InputStream tsIn = null;
            InputStream ksIn=null;
            try {
                ksIn=CertificateCode.ksIn;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ksIn.close();
                } catch (Exception ignore) {
                }
            }
            try {
                tsIn=CertificateCode.tsIn;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, getKeystoreOfCA(tsIn));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        SSLSocket s = (SSLSocket)sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        protocolList(s);
        return s;
    }

    private static final String TLS_V1_2 = "TLSv1.2";
    private static final String TLS_V1 = "TLS";
    private boolean supportV1_2 = false;

    private void protocolList(SSLSocket sslSocket){

        if(supportV1_2){
            sslSocket.setEnabledProtocols(new String[]{TLS_V1_2});
        }

    }

    @Override
    public Socket createSocket() throws IOException {
        LogUtil.e(getClass().getName(), "createSocket no params");
        SSLSocket s = (SSLSocket)sslContext.getSocketFactory().createSocket();
        protocolList(s);
        return s;
    }

    /**
     * Makes HttpsURLConnection trusts a set of certificates specified by the KeyStore
     */
    public void fixHttpsURLConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    /**
     * Gets a KeyStore containing the Certificate
     *
     * @param cert InputStream of the Certificate
     * @return KeyStore
     */
    public static KeyStore getKeystoreOfCA(InputStream cert) {

        // Load CAs from an InputStream
        InputStream caInput = null;
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(cert);
            ca = cf.generateCertificate(caInput);
        } catch (CertificateException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    /**
     * Gets a Default KeyStore
     *
     * @return KeyStore
     */
    public static KeyStore getKeystore() {
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return trustStore;
    }

    /**
     * Returns a SSlSocketFactory which trusts all certificates
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getFixedSocketFactory(Context context) {
        SSLSocketFactory socketFactory;
        try {
            socketFactory = new MySSLSocketFactory(context,getKeystoreOfCA(CertificateCode.tsIn));
//            socketFactory = getSslSocketFactory(context);
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Throwable t) {
            t.printStackTrace();
            socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        return socketFactory;
    }

    /**
     * Gets a DefaultHttpClient which trusts a set of certificates specified by the KeyStore
     *
     * @param keyStore custom provided KeyStore instance
     * @return DefaultHttpClient
     */
    public static DefaultHttpClient getNewHttpClient(KeyStore keyStore) {

        try {
            SSLSocketFactory sf = new MySSLSocketFactory(null,keyStore);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
//        X509TrustManager tm = new X509TrustManager() {
//            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//
//            }
//
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
////
////        sslContext.init(null, new TrustManager[]{tm}, null);
//
////        SSLSocket s = null;
////        try {
////            s = (SSLSocket)sslContext.getSocketFactory().createSocket();
////            protocolList(s);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        // 服务器端需要验证的客户端证书
//        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
//        // 客户端信任的服务器端证书
//        KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
//
//        LogUtil.print("httpssss","1");
////        InputStream ksIn = context.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
//
//        InputStream ksIn = null;
//        try {
//            ksIn=CertificateCode.ksIn;
//            if(CertificateCode.ksIn!=null){
//                LogUtil.print("httpssss","have");
//            }else {
//                LogUtil.print("httpssss","null");
//            }
////            ksIn = InputStreamUtils.byteTOInputStream(CertificateCode.CLIENT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        InputStream tsIn = context.getResources().getAssets().open(KEY_STORE_TRUST_PATH);
//        InputStream tsIn=null;
//        try {
//            tsIn=CertificateCode.tsIn;
//            if(CertificateCode.tsIn!=null){
//                LogUtil.print("httpssss","have");
//            }else {
//                LogUtil.print("httpssss","null");
//            }
////            tsIn=InputStreamUtils.byteTOInputStream(CertificateCode.TRUST);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        finally {
////            try {
////                ksIn.close();
////            } catch (Exception ignore) {
////            }
////            try {
////                tsIn.close();
////            } catch (Exception ignore) {
////            }
////        }
////        SSLContext sslContext = SSLContext.getInstance("TLS");
//
//        CertificateFactory cerFactory = null;
//        try {
//            cerFactory = CertificateFactory
//                    .getInstance("X.509");
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        Certificate cer = null;
//        try {
//            cer = cerFactory.generateCertificate(tsIn);
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        KeyStore sssssss = KeyStore.getInstance("BKS");
//        try {
//            sssssss.load(null, null);
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        sssssss.setCertificateEntry("trust", cer);
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(sssssss);
//
////        try {
////            LogUtil.print("httpssss",trustStore.getProvider().toString());
////            if(trustStore.aliases().hasMoreElements()) {
////                String alias=trustStore.aliases().nextElement();
////                LogUtil.print("httpssss",TrustManagerFactory.getDefaultAlgorithm());
////                LogUtil.print("httpssss",alias);
////                LogUtil.print("httpssss",trustStore.getCertificate(alias).toString());
////                LogUtil.print("httpssss","KeyFormat:"+trustStore.getCertificate(alias).getPublicKey().getFormat());
////                LogUtil.print("httpssss","KeyAlgorithm:"+trustStore.getCertificate(alias).getPublicKey().getAlgorithm());
////                LogUtil.print("httpssss","KeyEncodedLength:"+trustStore.getCertificate(alias).getPublicKey().getEncoded().length);
////            }
////        }catch (Exception s){
////            LogUtil.print("httpssss","异常");
////        }
//        try {
//            keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
////            trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
////        try {
////            LogUtil.print("httpssss",keyStore.getProvider().toString());
////            if(keyStore.aliases().hasMoreElements()) {
////                String alias=keyStore.aliases().nextElement();
////                LogUtil.print("httpssss",alias);
////                LogUtil.print("httpssss",keyStore.getCertificate(alias).toString());
////                LogUtil.print("httpssss","KeyFormat:"+keyStore.getCertificate(alias).getPublicKey().getFormat());
////                LogUtil.print("httpssss","KeyAlgorithm:"+keyStore.getCertificate(alias).getPublicKey().getAlgorithm());
////                LogUtil.print("httpssss","KeyEncodedLength:"+keyStore.getCertificate(alias).getPublicKey().getEncoded().length);
////            }
////        }catch (Exception s){
////            LogUtil.print("httpssss","异常");
////        }
////
////        CertificateFactory certificateFactory = null;
////        try {
////            certificateFactory = CertificateFactory.getInstance("X.509");
////        } catch (CertificateException e) {
////            e.printStackTrace();
////        }
////        try {
////            trustStore.load(null);
////        } catch (CertificateException e) {
////            e.printStackTrace();
////        }
////        int index = 0;
////            String certificateAlias = Integer.toString(index++);
////        try {
////            trustStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(tsIn));
////        } catch (CertificateException e) {
////            e.printStackTrace();
////        }
////        try
////            {
////                if (tsIn != null)
////                    tsIn.close();
////            } catch (IOException e)
////
////            {
////            }
////        TrustManagerFactory trustManagerFactory = null;
////
////        trustManagerFactory = TrustManagerFactory.
////                getInstance(TrustManagerFactory.getDefaultAlgorithm());
////        trustManagerFactory.init(getKeystoreOfCA(tsIn));
//
////        TrustManager[] tms=trustManagerFactory.getTrustManagers();
//        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
////        sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{tm}, null);
////        sslContext.init(null, new TrustManager[]{tm}, null);
}

package com.loopj.lakala.http;

import android.util.Base64;

import com.lakala.library.util.CertificateCode;
import com.lakala.library.util.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * A factory for SSLContexts.
 * Builds an SSLContext with custom KeyStore and TrustStore, to work with a client cert signed by a self-signed CA cert.
 */
public class SSLContextFactory {

    private static SSLContextFactory theInstance = null;

    private SSLContextFactory() {
    }

    public static SSLContextFactory getInstance() {
        LogUtil.print("httpssss","getInstance0");
        if(theInstance == null) {
            theInstance = new SSLContextFactory();
        }
        return theInstance;
    }

    /**
     * Creates an SSLContext with the client and server certificates
     * @param keyInputStream A File containing the client certificate
     * @param clientCertPassword Password for the client certificate
     * @param caCertString A String containing the server certificate
     * @return An initialized SSLContext
     * @throws Exception
     */
    public void makeContext(SSLContext sslContext,InputStream keyInputStream, String clientCertPassword, String caCertString) throws Exception {
        final KeyStore keyStore = loadPKCS12KeyStore(keyInputStream, clientCertPassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, clientCertPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();
        final KeyStore trustStore = loadPEMTrustStore(caCertString);
        TrustManager[] trustManagers = {new CustomTrustManager(trustStore)};

        sslContext.init(keyManagers, trustManagers, new SecureRandom());
    }

    /**
     * Produces a KeyStore from a String containing a PEM certificate (typically, the server's CA certificate)
     * @param certificateString A String containing the PEM-encoded certificate
     * @return a KeyStore (to be used as a trust store) that contains the certificate
     * @throws Exception
     */
    private KeyStore loadPEMTrustStore(String certificateString) throws Exception {

        byte[] der = loadPemCertificate(new ByteArrayInputStream(certificateString.getBytes()));
        ByteArrayInputStream derInputStream = new ByteArrayInputStream(der);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
        String alias = cert.getSubjectX500Principal().getName();

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, cert);

        return trustStore;
    }

    /**
     * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically the client certificate
     * @param keyInputStream A file containing the client certificate
     * @param clientCertPassword Password for the certificate
     * @return A KeyStore containing the certificate from the certificateFile
     * @throws Exception
     */
    private KeyStore loadPKCS12KeyStore(InputStream keyInputStream, String clientCertPassword) throws Exception {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyInputStream, clientCertPassword.toCharArray());
        } finally {
            try {
                if(keyInputStream != null) {
                    keyInputStream.close();
                }
            } catch(IOException ex) {
            }
        }
        return keyStore;
    }

    /**
     * Reads and decodes a base-64 encoded DER certificate (a .pem certificate), typically the server's CA cert.
     * @param certificateStream an InputStream from which to read the cert
     * @return a byte[] containing the decoded certificate
     * @throws IOException
     */
    byte[] loadPemCertificate(InputStream certificateStream) throws IOException {

        byte[] der = null;
        BufferedReader br = null;

        try {
            StringBuilder buf = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(certificateStream));

            String line = br.readLine();
            while(line != null) {
                if(!line.startsWith("--")){
                    buf.append(line);
                }
                line = br.readLine();
            }

            String pem = buf.toString();
            der = Base64.decode(pem, Base64.DEFAULT);

        } finally {
           if(br != null) {
               br.close();
           }
        }

        return der;
    }
}

package com.cfg.takephotodemo.httpsdemo;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Created by cfg on 17-4-26.
 */

public class SSLConnection {

    private static TrustManager[] trustManagers;

    private static final String KEY_STORE_TYPE_BKS = "bks";

    private static final String KEY_STORE_TYPE_P12 = "PKCS12";

    private static final String keyStoreFileName = "client.p12";

    private static final String keyStorePassword = "123456";

    private static final String trustStoreFileName = "server.bks";

    private static final String trustStorePassword = "123456";

    private static final String alias = null;//"client";

    private static Context pContext = null;

    public static void allowAllSSL(Context ct) {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {

//TODO Auto-generated method stub

                return true;

            }

        });

        pContext = ct;

        javax.net.ssl.SSLContext context;

        if (trustManagers == null) {

            try {

                KeyManager[] keyManagers = createKeyManagers(keyStoreFileName, keyStorePassword, alias);

                trustManagers = createTrustManagers(trustStoreFileName, trustStorePassword);

                context = javax.net.ssl.SSLContext.getInstance("TLS");

                context.init(keyManagers, trustManagers, new SecureRandom());

                javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            } catch (KeyStoreException e) {

                e.printStackTrace();

            } catch (NoSuchAlgorithmException e) {

                e.printStackTrace();

            } catch (CertificateException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();

            } catch (KeyManagementException e) {
                Log.e("allowAllSSL", e.toString());

            }//new TrustManager[]{new _FakeX509TrustManager()};

        }

    }

    private static KeyManager[] createKeyManagers(String keyStoreFileName, String keyStorePassword, String alias) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        InputStream inputStream = pContext.getResources().getAssets().open(keyStoreFileName);
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
        keyStore.load(inputStream, keyStorePassword.toCharArray());
        printKeystoreInfo(keyStore);//for debug
        KeyManager[] managers;
        if (alias != null) {
            managers = new KeyManager[]{new SSLConnection().new AliasKeyManager(keyStore, alias, keyStorePassword)};
        } else {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//PKIX "X509")
            keyManagerFactory.init(keyStore, keyStorePassword == null ? null : keyStorePassword.toCharArray());
            managers = keyManagerFactory.getKeyManagers();
        }
        return managers;
    }

    private static TrustManager[] createTrustManagers(String trustStoreFileName, String trustStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        InputStream inputStream = pContext.getResources().getAssets().open(trustStoreFileName);
        KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
        trustStore.load(inputStream, trustStorePassword.toCharArray());
        printKeystoreInfo(trustStore);//for debug
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }

    private static void printKeystoreInfo(KeyStore keystore) throws KeyStoreException {

        System.out.println("Provider : " + keystore.getProvider().getName());

        System.out.println("Type : " + keystore.getType());

        System.out.println("Size : " + keystore.size());

        Enumeration en = keystore.aliases();

        while (en.hasMoreElements()) {
            System.out.println("Alias: " + en.nextElement());

        }
    }

    public class AliasKeyManager implements X509KeyManager {
        private KeyStore _ks;
        private String _alias;
        private String _password;

        public AliasKeyManager(KeyStore ks, String alias, String password) {
            _ks = ks;
            _alias = alias;
            _password = password;
        }

        public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
            return _alias;
        }

        public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
            return _alias;
        }

        public X509Certificate[] getCertificateChain(String alias) {
            try {
                java.security.cert.Certificate[] certificates = this._ks.getCertificateChain(alias);
                if (certificates == null) {
                    throw new FileNotFoundException("no certificate found for alias:" + alias);
                }
                X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
                System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
                return x509Certificates;
            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }
        }


        public String[] getClientAliases(String str, Principal[] principal) {

            return new String[]{
                    _alias
            };

        }


        public PrivateKey getPrivateKey(String alias) {
            try {
                return (PrivateKey) _ks.getKey(alias, _password == null ? null : _password.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }


        public String[] getServerAliases(String str, Principal[] principal) {

            return new String[]{
                    _alias
            };

        }


    }

}
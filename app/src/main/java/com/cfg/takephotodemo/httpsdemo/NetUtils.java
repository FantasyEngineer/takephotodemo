package com.cfg.takephotodemo.httpsdemo;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.util.MutableLong;

import com.cfg.takephotodemo.Appp;
import com.cfg.takephotodemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by cfg on 17-4-25.
 */

public class NetUtils {
    public static final String TAG = NetUtils.class.getName();
    public static final String FINAL_URL = "http://www.youzhu.com";
    public static final String BASE_URL2 = "https://api.youzhu.com/";
    public ClientApi clientApi;
    public ClientApi clientApi2;


    public static ClientApi getInstance() {
        NetUtils netUtils = new NetUtils();
        return netUtils.clientApi;
    }

    public static ClientApi getInstance2() {
        NetUtils netUtils = new NetUtils(12);
        return netUtils.clientApi2;
    }

    SSLContext sslContext;

    private NetUtils() {
        OkHttpClient builder = null;

        try {
            builder = new OkHttpClient().newBuilder().socketFactory(aaaa())
                    //                .addInterceptor(null).
                    //              .addNetworkInterceptor(interceptor)
                    .build();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        clientApi = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .client(builder)
                .addConverterFactory(new StringFactory())
                .build()
                .create(ClientApi.class);
    }

    private NetUtils(int m) {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        OkHttpClient builder2 = null;

        builder2 = new OkHttpClient().newBuilder()
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sslContext.getSocketFactory(), trustManagers[0])
                //                .addInterceptor(null).
                //              .addNetworkInterceptor(interceptor)
                .build();

        clientApi2 = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .client(builder2)
                .addConverterFactory(new StringFactory())
                .build()
                .create(ClientApi.class);
    }

    interface ClientApi {
        @FormUrlEncoded  //获取首页数据
        @POST("/gateway")
        Call<String> getIndex(@Field(value = "method") String method, @Field(value = "action") String action, @Field(value = "params") String params);

    }


    X509TrustManager[] trustManagers = new X509TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                /**
                 * 检查服务器主机
                 * @param x509Certificates
                 * @param authType
                 * @throws CertificateException
                 */
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {

                    if (x509Certificates == null) {
                        Log.d(TAG, "---------x509Certificates==null");
                    }
                    if (x509Certificates.length < 0) {
                        Log.d(TAG, "-x509Certificates.length<0-----");

                    }
                    for (X509Certificate cert : x509Certificates) {

                        cert.checkValidity();
                        try {
                            cert.verify(getX509Certificate().getPublicKey());
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchProviderException e) {
                            e.printStackTrace();
                        } catch (SignatureException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };
    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.d("hostnameVerifier", hostname);
            Log.d("hostnameVerifier", session.getPeerHost());
            Log.d("hostnameVerifier", session.getCipherSuite());
            Log.d("hostnameVerifier", session.isValid() + "");
            return true;
        }
    };

    public static X509Certificate getX509Certificate() throws CertificateException {

        InputStream in = Appp.appContext.getResources().openRawResource(R.raw.youzhu);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate servercert = (X509Certificate) certificateFactory.generateCertificate(in);
        System.out.println("X509Certificate=" + servercert.getSubjectDN());
        return servercert;

    }

    public static SSLSocketFactory aaaa() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        InputStream in = Appp.appContext.getResources().openRawResource(R.raw.youzhu);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) certificateFactory.generateCertificate(in);
        System.out.println("ca=-aaaa--aaaa" + ((X509Certificate) cer).getSubjectDN());

        // Create a KeyStore containing our trusted CAs
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);
        keystore.setCertificateEntry("ca", cer);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();//X509TrustManager
        System.out.println("ca=-aaaa--algorithm---" + algorithm);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
        System.out.println("ca=-aaaa--trustManagerFactory---" + trustManagerFactory.getAlgorithm());
        trustManagerFactory.init(keystore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    /**
     * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
     *
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLSocketFactory initSSL() throws CertificateException, IOException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        InputStream in = getAssets().open("load-der.crt");
        InputStream in = Appp.appContext.getResources()
                .openRawResource(R.raw.youzhu);
        Certificate ca = cf.generateCertificate(in);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null);
        keystore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keystore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }


    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            KeyStore ksTrust = KeyStore.getInstance("BKS");
//            KeyStore ksTrust = KeyStore.getInstance("PKCS12");
            InputStream instream = Appp.appContext.getResources()
                    .openRawResource(R.raw.youzhubks2);
            ksTrust.load(instream, "123456".toCharArray());
            //TrustManager decides which certificate authorities to use.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");//TrustManagerFactory.getDefaultAlgorithm()
            tmf.init(ksTrust);
            SSLContext sslContext = SSLContext.getInstance("TLS");

//            String[] cipherSuites = sslContext.getSupportedCipherSuites(); sslSocket.setEnabledCipherSuites(cipherSuites);
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

}

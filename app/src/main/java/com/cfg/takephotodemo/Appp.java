package com.cfg.takephotodemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by cfg on 17-4-25.
 */

public class Appp extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

    }

    /*
获取公钥key的方法（读取.crt认证文件）/home/cfg/桌面
*/
    private String getKeyFromCRT() {
        String key = "";
        CertificateFactory certificatefactory;
        X509Certificate Cert;
        InputStream bais;
        PublicKey pk;
        try {
            //若此处不加参数 "BC" 会报异常：CertificateException - OpenSSLX509CertificateFactory$ParsingException
            certificatefactory = CertificateFactory.getInstance("X.509", "BC");
            //读取放在项目中assets文件夹下的.crt文件；你可以读取绝对路径文件下的crt，返回一个InputStream（或其子类）即可。
            bais = getResources().openRawResource(R.raw.youzhu);
//            bais = this.getAssets().open("youzhu.crt");
            Cert = (X509Certificate) certificatefactory.generateCertificate(bais);
            pk = Cert.getPublicKey();
            // key = bse.encode(pk.getEncoded());
            //            Log.e("源key-----"+ Cert.getPublicKey());
            //            Log.e("加密key-----"+bse.encode(pk.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        key = key.replaceAll("\\n", "").trim();//去掉文件中的换行符
        return key;
    }

    public class SslContextFactory {
        private static final String CLIENT_TRUST_PASSWORD = "changeit";//信任证书密码，该证书默认密码是changeit
        private static final String CLIENT_AGREEMENT = "TLS";//使用协议
        private static final String CLIENT_TRUST_MANAGER = "X509";
        private static final String CLIENT_TRUST_KEYSTORE = "BKS";
        SSLContext sslContext = null;

        public SSLContext getSslSocket() {
            try {
//取得SSL的SSLContext实例
                sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
//取得TrustManagerFactory的X509密钥管理器实例
                TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
//取得BKS密库实例
                KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
                InputStream is = getResources().openRawResource(R.raw.youzhubks2);
                try {
                    tks.load(is, CLIENT_TRUST_PASSWORD.toCharArray());
                } finally {
                    is.close();
                }
//初始化密钥管理器
                trustManager.init(tks);
//初始化SSLContext
                sslContext.init(null, trustManager.getTrustManagers(), null);
            } catch (Exception e) {
                Log.e("SslContextFactory", e.getMessage());
            }
            return sslContext;
        }
    }

    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            KeyStore ksTrust = KeyStore.getInstance("BKS");
            InputStream instream = context.getResources()
                    .openRawResource(R.raw.youzhubks2);
            ksTrust.load(instream, "123456".toCharArray());
            //TrustManager decides which certificate authorities to use.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");//TrustManagerFactory.getDefaultAlgorithm()
            tmf.init(ksTrust);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }


}

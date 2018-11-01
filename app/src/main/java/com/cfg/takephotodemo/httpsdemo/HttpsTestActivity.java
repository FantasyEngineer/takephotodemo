package com.cfg.takephotodemo.httpsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cfg.takephotodemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpsTestActivity extends Activity {
    private TextView tv_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https_test);
        tv_response = (TextView) findViewById(R.id.tv_https_response);

    }

    public void 链接服务器(View view) {

        String params = map2String("region_name", "青岛市");
        Call<String> index = NetUtils.getInstance().getIndex("getBanner", "Region", params);
        index.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("onResponse", response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();

            }
        });

    }

    /**
     * 用于进行参数的转换
     *
     * @param params
     * @return
     */
    public static String map2String(String... params) {
        String resT = "";
        JSONObject reqD = new JSONObject();//用于加密的数据
        Log.e("map2String", params.toString());
        try {
            for (int i = 0; i < params.length; i += 2) {
                reqD.put(params[i], params[i + 1]);
            }
            reqD.put("deviceId", "fahsjlfgasjdlvmzxvashkfgaskj");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resT = reqD.toString();
        Log.e("map2String", resT);
        return resT;

    }


    public void 链接222服务器(View view) {
        String params = map2String("region_name", "青岛市");
        Call<String> index = NetUtils.getInstance2().getIndex("getBanner", "Region", params);
        index.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("onResponse", response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();

            }
        });

    }
}

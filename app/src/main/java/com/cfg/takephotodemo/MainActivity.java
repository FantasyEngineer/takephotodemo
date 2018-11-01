package com.cfg.takephotodemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cfg.takephotodemo.httpsdemo.HttpsTestActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void idcard(View view) {
        Intent intent = new Intent(this, CardPhotoActivity.class);
        startActivity(intent);
    }

    public void shuiyin(View view) {
        Intent intent = new Intent(this, TakeUtilsActivity.class);
        startActivity(intent);
    }

    public void csdntest(View view) {
        Intent intent = new Intent(this, CSDNTestActivity.class);
        startActivity(intent);
    }

    public void ziticolor(View view) {
        Intent intent = new Intent(this, HStringActivity.class);
        startActivity(intent);
    }

    public void Https测试(View view) {
        Intent intent = new Intent(this, HttpsTestActivity.class);
        startActivity(intent);
    }
}

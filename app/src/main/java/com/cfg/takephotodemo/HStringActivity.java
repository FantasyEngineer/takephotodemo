package com.cfg.takephotodemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

public class HStringActivity extends AppCompatActivity {

    private TextView textView;

    public static String sm = "设置字体为<green>绿色</green>的字体";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hstring);
        textView = (TextView) findViewById(R.id.textview);

        SpannableString s = HString.htmlToString(sm);
        textView.setText(s);
    }

}

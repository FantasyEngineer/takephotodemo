package com.cfg.takephotodemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by cfg on 17-4-21.
 */

public class CSDNTestActivity extends Activity {

    private Button button;
    private CameraSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 全屏显示
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_csdntest);

        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.takePicture();
//                sceenshot();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mCameraSurfaceView.destroyDrawingCache();
    }

    //    public void autoFocus() {
//        mCameraSurfaceView.setAutoFocus();
//    }

//    public void sceenshot() {
//        View dView = findViewById(R.id.rl);
//        dView.setDrawingCacheEnabled(true);
//        dView.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
//        if (bitmap != null) {
//            try {
//                // 获取内置SD卡路径
//                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
//                // 图片文件路径
//                String filePath = sdCardPath + File.separator + "screenshot.png";
//                File file = new File(filePath);
//                FileOutputStream os = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//                os.flush();
//                os.close();
//            } catch (Exception e) {
//            }
//        }
//    }
}


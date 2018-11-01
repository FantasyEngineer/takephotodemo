package com.cfg.takephotodemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

public class CardPhotoActivity extends Activity implements SurfaceHolder.Callback {


    private ImageView image_idcard;
    private int mCameraId;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    private CameraTopRectView cameraTopRectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_photo);
        Log.d("CardPhotoActivity", "CardPhotoActivity");
        cameraTopRectView = (CameraTopRectView) findViewById(R.id.CameraTopRectView_idcard);

        image_idcard = (ImageView) findViewById(R.id.image_idcard);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview_idcard);
        mSurfaceView.setFocusable(true);//设置信息
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//设置打开的摄像头为后置摄像头
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mCamera.autoFocus(autoFocusCallback); //设置相机为自动对焦模式，就不用认为去点击了
                return false;
            }


        });
    }

    /**
     * 这是点击surfaceview聚焦所调用的方法
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //success = true，聚焦成功，否则聚焦失败
            //在这里我们可以在点击相机后是否聚焦成功，然后做我们的一些操作，这里我就省略了，大家自行根据需要添加
        }
    };

   /*截屏RelativeLayout部分，水印布局的部分，
            *@return Bitmap
    */

    public Bitmap Interceptionscreen(RelativeLayout relativeLayout) {
        View view = relativeLayout;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        view.destroyDrawingCache();
        bitmap = null;
        return bitmap1;
    }
   /*截屏RelativeLayout部分，水印布局的部分，
            *@return Bitmap
    */

//    public Bitmap InterceptionscreenRelativelatyou(Bitmap bitmap1) {
//
//        Log.d("bitmap", bitmap1.getHeight() + "h---h");
//        Log.d("bitmap", bitmap1.getWidth() + "w---w");
//        int height = cameraTopRectView.getViewHeight();
//        int width = cameraTopRectView.getViewWidth();
//        int left = cameraTopRectView.getRectLeft();
//        int right = cameraTopRectView.getRectRight();
//        int top = cameraTopRectView.getRectTop();//300
//        int bottom = cameraTopRectView.getRectBottom();//1620
//        Log.d("cameraTopRectView", height + "---height");
//        Log.d("cameraTopRectView", width + "---width");
//        Log.d("cameraTopRectView", left + "---left");
//        Log.d("cameraTopRectView", right + "---right");
//        Log.d("cameraTopRectView", top + "---top");
//        Log.d("cameraTopRectView", bottom + "---bottom");
//
////        Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, right - left, bottom - top, true);
//
//        Bitmap ggg = Bitmap.createBitmap(bitmap, top, left, bottom + 20 - top, right + 20 - left);// 截取
//
//        Log.d("cameraTopRectView", ggg.getHeight() + "---ggg");
//        return ggg;
//    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open(mCameraId);
            Camera.getCameraInfo(mCameraId, cameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 我们在此周期方法里面打开摄像头
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            openCamera();//打开后置摄像头
        }
    }

    /**
     * 打开后置摄像头
     */
    private void openCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        this.cameraInfo = cameraInfo;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) { //后置摄像头 CAMERA_FACING_FRONT
            mCamera = Camera.open();
            mCamera.startPreview();//开始预览相机
        }
    }

    /**
     * 开始预览相机
     *
     * @param camera        camera
     * @param surfaceHolder surfaceHolder
     */
    private void startPreview(Camera camera, SurfaceHolder surfaceHolder) {
        camera.setDisplayOrientation(setCameraDisplayOrientation());
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        camera.startPreview();//调用此方法，然后真正的预览相机
    }

    /**
     * 用于旋转角度.不写会旋转
     *
     * @return
     */

    public int setCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return
                result;
//        camera.setDisplayOrientation(result);
    }

    /**
     * 检查设备是否有摄像头 的方法
     *
     * @param context context
     * @return boolean
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    ;
    private Bitmap mBitmap;
    private int bitmapWidth;
    private int bitmapHeight;

    public void takemphotoidcard(View view) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {//data 将会返回图片的字节数组

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.d("bitmap", bitmap.getWidth() + "---bitmap_getWidth");
                Log.d("bitmap", bitmap.getHeight() + "---bitmap_getHeight");
                Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, cameraTopRectView.getWidth(), cameraTopRectView.getHeight(), true);
                Log.d("data.length", data.length + "---data,--length");

//                bitmap = matrixBitmap(bitmap, 90); //此方法将Bitmap 旋转90度
                Log.d("cameraTopRectView", cameraTopRectView.getWidth() + "---cameraTopRectView_getWidth");
                Log.d("cameraTopRectView", cameraTopRectView.getHeight() + "---cameraTopRectView_getHeight");
                Log.d("sizeBitmap", sizeBitmap.getWidth() + "---sizeBitmap_getWidth");
                Log.d("sizeBitmap", sizeBitmap.getHeight() + "---sizeBitmap_getHeight");
                Log.d("mSurfaceView", mSurfaceView.getWidth() + "---mSurfaceView_getWidth");
                Log.d("mSurfaceView", mSurfaceView.getHeight() + "---mSurfaceView_getHeight");


                if (sizeBitmap != null) {
//                    image_idcard.setImageBitmap(bitmap);
                    image_idcard.setImageBitmap(cameraTopRectView.getIdBitmap(sizeBitmap));
                    mSurfaceView.setOnTouchListener(null);
                } else {
                    camera.stopPreview();
                    camera.release();
                }



//                camera = null;
                //拍照返回的图像逆时针旋转了90度，原因未了解。
//                String path = ImageUtil.saveBitmap(null, String.valueOf(currentTimeMillis), bitmap);
//                //将图片进行保存到本地，返回保存的地址
//                if (path != null && path.length() > 0) {
//                    waterIntent.setClass(MainActivity.this, PreviewPhotoActivity.class);// 跳转到预览界面
//                    waterIntent.putExtra(StaticParam.PIC_PATH, path);
//                    startActivityForResult(waterIntent, REQUEST_CODE);
//                } else {
//                    camera.stopPreview();
//                    camera.release();
//                    camera = null;
//                }
            }
        });

    }

    /**
     * 对bitmap进行旋转
     *
     * @param bitmap
     * @param i
     * @return
     */
    public Bitmap matrixBitmap(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return
                bm;
    }

    /**
     * 重新拍摄按钮
     *
     * @param view
     */
    public void rephoto(View view) {
        if (mCamera != null) {
            mCamera.startPreview();
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    mCamera.autoFocus(autoFocusCallback); //设置相机为自动对焦模式，就不用认为去点击了
                    return false;
                }


            });
        }
    }


}

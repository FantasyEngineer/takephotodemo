package com.cfg.takephotodemo;

/**
 * Created by cfg on 17-4-21.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;
    private CameraTopRectView topView;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        topView = new CameraTopRectView(context, attrs);

        initView();


    }

    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        Log.d(TAG, "getHeight():" + getHeight());
        Log.d(TAG, "getWidth():" + getWidth());

    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open();//开启相机
            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");

        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
//        mCamera.takePicture(null, null, jpeg);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success=" + success);
            System.out.println(success);
        }
    }


    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(90);
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG, "shutter");
            System.out.println("执行了吗+1");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");
            System.out.println("执行了吗+2");
        }
    };

    boolean canStart = true;
    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {


        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            canStart = true;
            testYuanshi(data);
        }

    };

    private void testYuanshi2(byte[] data) {
        Bitmap bitmap;
        topView.draw(new Canvas());
        BufferedOutputStream bos = null;
        Bitmap bm = null;
        if (data != null) {
        }

        try {
            // 获得图片
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap front = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.timg);
            bm = mergeBitmap(bm, front);

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, Environment.getExternalStorageDirectory().toString());
                String kgxuanzhuan = Environment.getExternalStorageDirectory().toString() + "/kgxuanzhuan" + System.currentTimeMillis() + ".jpg";//照片保存路径


                Log.e("bm---", bm.getWidth() + "bm---- getwidth");
                Log.e("bm---", bm.getHeight() + "bm---- getHeight");
                Matrix m = new Matrix();
                int height = bm.getHeight();
                int width = bm.getWidth();
                if (width > height) {//需要进行旋转 如果宽度大于高度,
//
//                    bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true); //旋转后的图片
//                    Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, topView.getViewHeight(), topView.getViewWidth(), true);
//                    bm = Bitmap.createBitmap(sizeBitmap, topView.getRectTop(), topView.getRectLeft(), topView.getRectBottom() - topView.getRectTop(), topView.getRectRight() - topView.getRectLeft());// 截取

                    Log.d("---------------", "---------旋转------------------");
//                    Log.e("bitmap---", bitmap.getWidth() + "bitmap---- getwidth");
//                    Log.e("bitmap---", bitmap.getHeight() + "bitmap---- getHeight");
                    File kxz = new File(kgxuanzhuan);
                    if (!kxz.exists()) {
                        kxz.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(kxz));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中

                } else {
                    Log.d("bm---", bm.getWidth() + "bm---- getwidth");
                    Log.d("bm---", bm.getHeight() + "bm---- getHeight");
                }


            } else {
                Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();//输出
                bos.close();//关闭
                bm.recycle();// 回收bitmap空间
                mCamera.stopPreview();// 关闭预览
                mCamera.startPreview();// 开启预览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void takePicture() {
        //设置参数,并拍照
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        if (canStart) {
            canStart = false;
            mCamera.takePicture(null, null, jpeg);
        }
    }

//    public void setAutoFocus(){
//        mCamera.autoFocus(this);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void testYuanshi(final byte[] data) {
        mCamera.stopPreview();// 关闭预览
        mCamera.startPreview();// 开启预览
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                BufferedOutputStream bos = null;
                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap front = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.timg);
                    bm = mergeBitmap(bm, front);
                    String yuanshif = Environment.getExternalStorageDirectory().toString() + "/yuanshi" + System.currentTimeMillis() + ".jpg";//照片保存路径
                    File yuanshi = new File(yuanshif);
                    if (!yuanshi.exists()) {
                        yuanshi.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(yuanshi));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                } catch (Exception exception) {
                    e.onError(exception);
                } finally {
                    e.onComplete();
                    try {
                        bos.flush();//输出
                        bos.close();//关闭
                        bm.recycle();// 回收bitmap空间
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }

            }
        }).subscribeOn(Schedulers.io()).subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
//                mCamera.stopPreview();// 关闭预览
//                mCamera.startPreview();// 开启预览
            }

            @Override
            public void onError(Throwable e) {
                Log.d("camera", "出现了问题" + e.getMessage());
            }

            @Override
            public void onComplete() {
//                mCamera.stopPreview();// 关闭预览
//                mCamera.startPreview();// 开启预览
            }
        });

//        try {
//            // 获得图片
//            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Bitmap front = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.timg);
//            //旋转，相机旋转后，这里不用
////            Matrix m = new Matrix();
////            m.setRotate(-90);
////            front = Bitmap.createBitmap(front, 0, 0, front.getWidth(), front.getHeight(), m, true); //旋转后的图片
//            bm = mergeBitmap(bm, front);
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                Log.d(TAG, Environment.getExternalStorageDirectory().toString());
//                String yuanshif = Environment.getExternalStorageDirectory().toString() + "/yuanshi" + System.currentTimeMillis() + ".jpg";//照片保存路径
//                String kgxuanzhuan = Environment.getExternalStorageDirectory().toString() + "/kgxuanzhuan" + System.currentTimeMillis() + ".jpg";//照片保存路径
//                String meiyouxuanzhuan = Environment.getExternalStorageDirectory().toString() + "/meiyouxuanzhuan" + System.currentTimeMillis() + ".jpg";//照片保存路径
//                String finalimag = Environment.getExternalStorageDirectory().toString() + "/finalimag" + System.currentTimeMillis() + ".jpg";//照片保存路径
//                String shenfenzheng = Environment.getExternalStorageDirectory().toString() + "/shenfenzheng" + System.currentTimeMillis() + ".jpg";//照片保存路径
//
//
//                Log.e("bm---", bm.getWidth() + "bm---- getwidth");
//                Log.e("bm---", bm.getHeight() + "bm---- getHeight");
//                File yuanshi = new File(yuanshif);
//                if (!yuanshi.exists()) {
//                    yuanshi.createNewFile();
//                }
//                bos = new BufferedOutputStream(new FileOutputStream(yuanshi));
//                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
//                bos.flush();//输出
//                bos.close();//关闭
        //旋转后的图片

//                Matrix m = new Matrix();
//                int height = bm.getHeight();
//                int width = bm.getWidth();
//                if (width > height) {//需要进行旋转 如果宽度大于高度,
//                    m.setRotate(90);
//                    bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true); //旋转后的图片
//                    Log.d("---------------", "---------旋转------------------");
//                    Log.e("bitmap---", bitmap.getWidth() + "bitmap---- getwidth");
//                    Log.e("bitmap---", bitmap.getHeight() + "bitmap---- getHeight");
//                    File kxz = new File(kgxuanzhuan);
//                    if (!kxz.exists()) {
//                        kxz.createNewFile();
//                    }
//                    bos = new BufferedOutputStream(new FileOutputStream(kxz));
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
//                    bos.flush();//输出
//                    bos.close();//关闭
//                } else {
//                Log.d("---------------", "-------没有进行--旋转------------------");
//                bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true); //旋转后的图片
//
//                Log.e("bitmap---", bitmap.getWidth() + "bitmap---- getwidth");
//                Log.e("bitmap---", bitmap.getHeight() + "bitmap---- getHeight");
//                File myxz = new File(meiyouxuanzhuan);
//                if (!myxz.exists()) {
//                    myxz.createNewFile();
//                }
//                bos = new BufferedOutputStream(new FileOutputStream(myxz));
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
//                bos.flush();//输出
//                bos.close();//关闭
//                }

//                Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, topView.getViewWidth(), topView.getViewHeight(), true);
//                bm = Bitmap.createBitmap(sizeBitmap, topView.getRectLeft(), topView.getRectTop(), topView.getRectRight() - topView.getRectLeft(), topView.getRectBottom() - topView.getRectTop());// 截取
//
//                File ffimg = new File(finalimag);
//                if (!ffimg.exists()) {
//                    ffimg.createNewFile();
//                }
//                bos = new BufferedOutputStream(new FileOutputStream(ffimg));
//
//                System.out.println(" bm.compress    -getHeight-   " + bm.getHeight());
//                System.out.println(" bm.compress    -getWidth-   " + bm.getWidth());
//
//                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
//
//            } else {
//                Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                bos.flush();//输出
//                bos.close();//关闭
//                bm.recycle();// 回收bitmap空间
//                mCamera.stopPreview();// 关闭预览
//                mCamera.startPreview();// 开启预览
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     *
     * @param backBitmap  在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            Log.e(TAG, "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }
}

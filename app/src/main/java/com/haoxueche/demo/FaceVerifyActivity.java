package com.haoxueche.demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.haoxueche.mz200alib.activity.BaseActivity;
import com.haoxueche.mz200alib.camera.CameraManager;
import com.haoxueche.mz200alib.util.ContextHolder;
import com.haoxueche.mz200alib.util.FileUtil;
import com.haoxueche.mz200alib.util.ImageUtil;
import com.haoxueche.mz200alib.util.SharePreferUtil;
import com.haoxueche.mz200alib.util.SystemUtil;
import com.haoxueche.mz200alib.util.T;
import com.haoxueche.mz200alib.widget.FaceView;
import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiezhongming on 17/9/27.
 * 检测人脸拍照
 */

public class FaceVerifyActivity extends BaseActivity implements Camera.FaceDetectionListener {

    private final String TAG = "FaceVerifyActivity";
    // 一个空字节数组
    public static final byte[] EMPTY_BYTES = new byte[0];

    protected FrameLayout cameraPreview;
    protected FaceView faceView;
    protected SurfaceView surfaceView;
    protected SurfaceHolder surfaceHolder;
    // 是否检测到人脸
    private boolean getFace = false;
    // 摄像头ID
    private int cameraId = CameraInfo.CAMERA_FACING_FRONT;
    // 是否强制拍照
    private boolean forceTakePicture = false;
    // 超时
    private Disposable disposable;

    private boolean isCameraOpened;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(TAG, "onCreate");
        super.setContentView(R.layout.activity_face_verify);
        SystemUtil.startWakeLock(true);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                openCamera();
                emitter.onNext(0);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                initView();
                checkFlashLight();
                startTimeOut(25);
            }
        }).compose(RxLifecycle.bind(this).<Integer>disposeObservableWhen(LifecycleEvent.DESTROY_VIEW)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.i(TAG, "onStop");
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


    /**
     * 初始化数据
     */
    private void initData() {
    }

    /**
     * 拍照超时自动结束
     *
     * @param delay 超时时间
     */
    private void startTimeOut(int delay) {
        cancelTimeout();
        Observable.timer(delay, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        if (forceTakePicture) {
                            takePicture();
                        } else {
                            finish(RESULT_CANCELED);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {

        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);
        faceView = (FaceView) findViewById(R.id.faceView);
        faceView.setCameraId(cameraId);

        surfaceView = new SurfaceView(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                surfaceView.setVisibility(View.VISIBLE);
                CameraManager.getInstance().starPreview(CameraManager.DEFAULT_WIDTH,
                        CameraManager.DEFAULT_HEIGHT, surfaceHolder, new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                if (camera != null) {
                                    camera.setPreviewCallback(null);
                                }
                                Log.i(TAG, "onPreviewFrame: ");

                                Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {

                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        CameraManager.getInstance().startFaceDetect
                                                (FaceVerifyActivity
                                                .this);
                                    }
                                });
                            }
                        });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
            }
        });

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraPreview.addView(surfaceView);
    }

    /**
     * 开启摄像头
     */
    private void openCamera() {
        Camera camera = CameraManager.getInstance().openCamera(cameraId);
        // 开启摄像头失败
        if (camera == null) {
            finish(RESULT_CANCELED);
        } else {
            isCameraOpened = true;
        }
    }

    private void checkFlashLight() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int month = calendar.get(Calendar.MONTH) + 1;
        //	福建日出时间:06:22 (北京时间)日落时间:17:13 (北京时间)
        if (month >= 10 || month <= 2) {
            if (hour <= 6 || hour >= 17) {
                openFlashLight();
            }
        } else {
            if (hour <= 5 || hour >= 18) {
                openFlashLight();
            }
        }
    }


    private void openFlashLight() {
        if (CameraManager.getInstance().getCamera() != null && CameraManager.getInstance()
                .getCamera().getParameters() != null) {
            try {
                //开启红外补光灯，针对外置摄像头
                CameraManager.openIRLED();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        faceView.setFaces(faces);
        if (faces != null && faces.length > 0 && !getFace) {
            getFace = true;
            takePicture();
        }
    }

    /**
     * 拍照
     */
    @SuppressLint("CheckResult")
    private void takePicture() {
        CameraManager.getInstance().takePicture(cameraId, CameraManager.DEFAULT_WIDTH,
                CameraManager.DEFAULT_HEIGHT)
                .observeOn(Schedulers.io())
                .map(new Function<byte[], Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull byte[] bytes) throws Exception {
                        Log.i(TAG, "apply: " + Thread.currentThread().getName());
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                })
                .map(new Function<Bitmap, byte[]>() {
                    @Override
                    public byte[] apply(@NonNull Bitmap bitmap) throws Exception {
                        Log.i(TAG, "apply: " + Thread.currentThread().getName() + "-----" +
                                bitmap.getByteCount());
                        // 外置摄像头故障
                        if (bitmap == null) {
                            return EMPTY_BYTES;
                        }
                        byte[] bytes;
                        //是否cameraId 为1 即外置摄像头，需要镜面翻转
                        if (cameraId == CameraManager.CAMERA_FACING_FRONT) {
                            Bitmap bitmap2 = ImageUtil.convert(bitmap);
                            if (SharePreferUtil.isOutCameraRotate()) {
                                bitmap2 = ImageUtil.rotate(bitmap2, 180);
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bitmap = bitmap2;
                            bytes = baos.toByteArray();
                        } else {
                            if (SharePreferUtil.isInsideCameraRotate()) {
                                bitmap = ImageUtil.rotate(bitmap, 180);
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bytes = baos.toByteArray();
                        }
                        // 如果是蓝屏,直接跳过后续处理
                        byte[] mErrorBlueData = FileUtil.input2byte(ContextHolder.getInstance()
                                .getAssets().open("error_blue.jpg"));
                        // 外置摄像头故障
                        if (bytes == null) {
                            throw new IllegalStateException("bytes null");
                        } else if (Arrays.equals(bytes, mErrorBlueData)) {
                            throw new IllegalStateException("camera blue");
                        }

                        return ImageUtil.compressImage(bitmap, 35);
                    }
                })
                .map(new Function<byte[], String>() {
                    @Override
                    public String apply(@NonNull byte[] bytes) throws Exception {
                        Log.i(TAG, "apply: " + Thread.currentThread().getName());
                        Log.i(TAG, "accept: " + bytes.length);
                        // 外置摄像头故障
                        if (bytes.length == 0) {
                            return "";
                        }
                        String path = FileUtil.IMAGE_TEMP_ABSOLUTE_DIR;
                        FileUtil.saveBytesToFile(bytes, path);
                        return path;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        Log.i(TAG, "accept: " + Thread.currentThread().getName());
                        Log.i(TAG, "accept file len: " + new File(path).length() + "-------\nfile" +
                                " path:" + path);
                        finish(RESULT_OK);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Log.e(TAG, "accept: ", throwable);
                        // 外置摄像头故障
                        if (throwable.getMessage().equals("camera blue")) {
                            T.showSpeak("外置摄像头故障，请学员和教练签退并通知维护人员修理设备");
                            finish(CameraManager.RESULT_HARDWARE_ERROR);
                        } else {
                            finish(RESULT_CANCELED);
                        }
                    }
                });
    }

    /**
     * 关闭界面
     *
     * @param code
     */
    private void finish(int code) {
        setResult(code);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.i(TAG, "onDestroy");
        if (isCameraOpened) {
            CameraManager.closeIRLED();
            CameraManager.getInstance().stopFaceDetect();
            CameraManager.getInstance().closeCamera();
            cancelTimeout();
        }
    }

    private void cancelTimeout() {
        if (disposable != null
                && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}

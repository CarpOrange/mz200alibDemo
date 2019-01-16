package com.haoxueche.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.TextureView;

import com.haoxueche.mz200alib.activity.BaseActivity;
import com.haoxueche.mz200alib.camera2.Camera2Manager;
import com.haoxueche.mz200alib.camera2.Camera2Manager.FaceDectionListener;
import com.haoxueche.mz200alib.camera2.Camera2Manager.PictureCallback;
import com.haoxueche.mz200alib.camera2.CameraConfig;
import com.haoxueche.mz200alib.util.CameraUtil;
import com.haoxueche.mz200alib.util.ContextHolder;
import com.haoxueche.mz200alib.util.FileUtil;
import com.haoxueche.mz200alib.util.ImageUtil;
import com.haoxueche.mz200alib.util.SharePreferUtil;
import com.haoxueche.mz200alib.util.SystemUtil;
import com.haoxueche.mz200alib.util.T;
import com.haoxueche.mz200alib.widget.FaceView1;
import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
 * Created by xiezhongming on 18/6/6.
 * 检测人脸拍照
 */

public class FaceVerifyNewActivity extends BaseActivity implements FaceDectionListener,
        PictureCallback {

    private final String TAG = "FaceVerifyNewActivity";
    // 一个空字节数组
    public static final byte[] EMPTY_BYTES = new byte[0];
    //人脸检测延迟的时间
    public static final int FACE_DECT_DELAY_TIME = 2;

    private TextureView textureView;
    private FaceView1 faceView;
    private Camera2Manager camera2Manager;

    // 是否检测到人脸
    private boolean getFace = false;
    // 是否强制拍照
    private boolean forceTakePicture = false;
    //是否是前置相机
    private boolean isFrontCamera = true;
    //是否开启闪光灯
    private boolean flashOn;
    private boolean cameraRotate;

    private String waterText = "这个字段是用来添加照片水印的\n你可以试一试\n支持换行的";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i("onCreate");
        initIntentData();
        super.setContentView(R.layout.activity_face_verify_new);
        SystemUtil.startWakeLock(true);
        initView();
        flashOn = checkFlashLight();
        startTimeOut(25);
    }


    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            camera2Manager = Camera2Manager.newInstance(ContextHolder.getInstance(), textureView
                    , faceView, null, cameraRotate, isFrontCamera
                    , flashOn, FaceVerifyNewActivity.this);
            camera2Manager.openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };


    @Override
    protected void onResume() {
        super.onResume();
        init();
        L.w("onResume");
    }

    /***
     * 开启相机
     */
    @SuppressLint("CheckResult")
    private void init() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                if (textureView.isAvailable()) {
                    camera2Manager = Camera2Manager.newInstance(ContextHolder.getInstance(),
                            textureView
                            , faceView, null, cameraRotate, isFrontCamera
                            , flashOn, FaceVerifyNewActivity.this);
                    camera2Manager.openCamera();
                } else {
                    textureView.setSurfaceTextureListener(mSurfaceTextureListener);
                }
                emitter.onNext(0L);
                emitter.onComplete();
            }
        }).delay(FACE_DECT_DELAY_TIME, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>disposeObservableWhen(LifecycleEvent.DESTROY_VIEW))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long integer) throws Exception {
                        camera2Manager.setFaceDectionListener(FaceVerifyNewActivity.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        L.e(e);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.w("onPause");
        unInit();
    }

    private void unInit() {
        if (camera2Manager != null) {
            camera2Manager.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.i("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


    /**
     * 获取数据
     */
    private void initIntentData() {
        Intent intent = getIntent();
        forceTakePicture = intent.getBooleanExtra("forceTakePicture", false);
        L.i("forceTakePicture==" + forceTakePicture);

        //相机画面是否需要旋转180度，用来适配摄像头
        if (isFrontCamera) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
    }

    /**
     * 拍照超时自动结束
     *
     * @param delay 超时时间
     */
    private void startTimeOut(int delay) {
        Observable.timer(delay, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bind(this)
                        .<Long>disposeObservableWhen(LifecycleEvent.DESTROY_VIEW))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
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
                        L.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        textureView = (TextureView) findViewById(R.id.texture);
        faceView = (FaceView1) findViewById(R.id.faceView);
    }


    /**
     * 拍照
     */
    private void takePicture() {
        camera2Manager.takePicture();
    }

    /**
     * 关闭界面
     *
     * @param code
     */
    private void finish(int code, Bundle data) {
        Intent intent = null;
        if (data != null) {
            intent = new Intent();
            intent.putExtras(data);
        }
        setResult(code, intent);
        finish();
        L.w("finish");
    }

    /**
     * 关闭界面
     *
     * @param code
     */
    private void finish(int code) {
        finish(code, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.i("onDestroy");
        CameraUtil.closeIRLED();
        SystemUtil.setGpioLow(65);
        L.w("onDestroy1");
    }

    /**
     * 判断是否需要开启外置相机红外补光，根据月份判断
     * @return
     */
    private boolean checkFlashLight() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int month = calendar.get(Calendar.MONTH) + 1;
        //	福建日出时间:06:22 (北京时间)日落时间:17:13 (北京时间)
        if (month >= 10 || month <= 2) {
            if (hour <= 6 || hour >= 17) {
                CameraUtil.openIRLED();
                return true;
            }
        } else {
            if (hour <= 5 || hour >= 18) {
                CameraUtil.openIRLED();
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                camera2Manager.release();
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 拍照回调
     * @param data
     */
    @SuppressLint("CheckResult")
    @Override
    public void onPictureTake(byte[] data) {
        Observable.just(data)
                .observeOn(Schedulers.io())
                .map(new Function<byte[], Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull byte[] bytes) throws Exception {
                        L.i("apply: " + Thread.currentThread().getName());
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                })
                .map(new Function<Bitmap, byte[]>() {
                    @Override
                    public byte[] apply(@NonNull Bitmap bitmap) throws Exception {
                        L.i("apply: " + Thread.currentThread().getName() + "-----" +
                                bitmap.getByteCount());
                        byte[] bytes;
                        //是否cameraId 为1 即外置摄像头，需要镜面翻转
                        if (camera2Manager != null && camera2Manager.isCurrentFrontCamera() &&
                                !SharePreferUtil.isOutPhotoMirrorFlip()) {
                            Bitmap bitmap2 = ImageUtil.convert(bitmap);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bitmap = bitmap2;
                            bytes = baos.toByteArray();
                        } else {
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
                        } else if (FileUtil.getSimilarity(bytes, mErrorBlueData) > 0.9) {
                            String path = FileUtil.IMAGE_TEMP_ABSOLUTE_DIR;
                            FileUtil.saveBytesToFile(ImageUtil.compressImage(bitmap, 35), path);
                            throw new IllegalStateException("camera blue");
                        }
                        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                        textPaint.setColor(ContextHolder.getInstance().getResources().getColor(R
                                .color.yellowa700));
                        textPaint.setTextSize(12);
                        bitmap = ImageUtil.drawTextToBitmap(BitmapFactory.decodeByteArray(bytes,
                                0, bytes.length),
                                waterText, textPaint, 0,
                                0);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
                        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                        return ImageUtil.compressImage(bitmap, 50);

                    }
                })
                .map(new Function<byte[], String[]>() {
                    @Override
                    public String[] apply(@NonNull byte[] bytes) throws Exception {
                        L.i("apply: " + Thread.currentThread().getName());
                        L.i("accept: " + bytes.length);
                        // 外置摄像头故障
                        if (bytes.length == 0) {
                            return new String[]{};
                        }
                        String photoNo = String.valueOf(System.currentTimeMillis() / 1000);
                        /**
                         * 照片保存路径
                         */
                        String path = FileUtil.DEFAULT_IMG_DIR + File.separator + photoNo + ".jpg";
                        FileUtil.saveBytesToFile(bytes, path);
                        String[] photoInfo = new String[3];
                        photoInfo[0] = photoNo;
                        photoInfo[1] = String.valueOf(bytes.length);
                        photoInfo[2] = path;
                        return photoInfo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bind(this)
                .<String[]>disposeObservableWhen(LifecycleEvent.DESTROY_VIEW))
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(final String[] photoInfo) throws Exception {
                        T.showSpeak("拍照结束");
                        String path = "";
                        if (photoInfo != null && photoInfo.length == 3) {
                            path = photoInfo[2];
                        }
                        final Bundle bundle = new Bundle();
                        bundle.putStringArray("photoInfo", photoInfo);
                        L.i("accept: " + Thread.currentThread().getName());
                        L.i("accept file len: " + new File(path).length() + "-------\nfile " +
                                "path:" + path);
                        finish(RESULT_OK, bundle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        L.e("accept: ", throwable);
                        // 外置摄像头故障
                        if ("camera blue".equals(throwable.getMessage())) {
                            T.showSpeak("外置摄像头故障，请学员和教练签退并通知维护人员修理设备");
                            finish(CameraConfig.RESULT_OUT_CAMERA_ERROR);
                        } else {
                            T.show("发生异常: " + throwable.getMessage());
                            finish(RESULT_CANCELED);
                        }
                    }
                });

    }

    /**
     * 相机出错
     */
    @Override
    public void onCameraError() {
        L.i("onCameraError");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showSpeak("相机故障，请重新操作，若多次操作仍无法启动相机请重启设备。");
                finish(CameraConfig.RESULT_HARDWARE_ERROR);
            }
        });
    }

    /**
     * 相机卡住了，应该是系统底层的错误，关闭相机后重新调用可以
     */
    @Override
    public void onCameraBlocked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showSpeak("相机卡住，请重新操作，若多次操作仍无法启动相机请重启设备。");
                finish(CameraConfig.RESULT_HARDWARE_ERROR);
            }
        });
    }


    /**
     * 人脸检测回调
     * @param faces
     */
    @Override
    public void onFaceDect(Face[] faces) {
        if (faces != null && faces.length > 0 && !getFace) {
            getFace = true;
            takePicture();
        }
    }
}

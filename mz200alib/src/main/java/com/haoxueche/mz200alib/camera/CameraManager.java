package com.haoxueche.mz200alib.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceHolder;

import com.haoxueche.mz200alib.util.SharePreferUtil;
import com.haoxueche.mz200alib.util.SystemUtil;
import com.haoxueche.winterlog.L;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by xiezhongming on 17/9/22.
 * 摄像头管理类
 */

public class CameraManager {

    private final String TAG = "CameraManager";

    /**
     * The facing of the camera is opposite to that of the screen.
     */
    public static final int CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK;

    /**
     * The facing of the camera is the same as that of the screen.
     */
    public static int CAMERA_FACING_FRONT = CameraInfo.CAMERA_FACING_FRONT;

    private final static CameraManager INSTANCE = new CameraManager();
    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;

    private Camera camera;
    private int cameraId;
    private boolean isCameraUsing;


    private boolean isStartFaceDetect = false;

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        return INSTANCE;
    }

    /**
     * 打开指定摄像头
     *
     * @param cameraId
     */
    public Camera openCamera(int cameraId) {
        if(isCameraUsing) {
            return null;
        }
        this.cameraId = cameraId;
        try {
            camera = Camera.open(cameraId);
            isCameraUsing = true;
            if (cameraId != CameraManager.CAMERA_FACING_FRONT && SharePreferUtil.isInsideCameraRotate()) {
                //使用内置摄像头
                camera.setDisplayOrientation(180);
            } else if(cameraId == CameraManager.CAMERA_FACING_FRONT && SharePreferUtil.isOutCameraRotate()) {
                //使用外置摄像头

            }
        } catch (Exception e) {
            Log.e(TAG, "openCamera: 开启摄像头失败");
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 开始预览
     */
    public void starPreview(int width, int height, SurfaceHolder holder, Camera.PreviewCallback callback) {
        try {
            Camera.Parameters params = camera.getParameters();
//            L.i(TAG, "params.flatten()==" + params.flatten().replaceAll(";", "\n")); //打印所有相机参数
            params.get("face-beauty");
            params.setPreviewSize(width, height);
            params.setPictureSize(width, height);
            camera.setParameters(params);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(callback);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            Log.e(TAG, "starPreview: 开启预览失败");
            // 预览失败,回调
            if (callback != null) {
                callback.onPreviewFrame(null, null);
            }
        }
    }

    /**
     * 开启人脸检测
     *
     * @param listener 检测监听
     */
    public void startFaceDetect(Camera.FaceDetectionListener listener) {
        try {
            if (camera != null
                    && camera.getParameters().getMaxNumDetectedFaces() > 0
                    && !isStartFaceDetect) {
                if (listener != null) {
                    camera.setFaceDetectionListener(listener);
                }
                camera.startFaceDetection();
                isStartFaceDetect = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭人脸检测
     */
    public void stopFaceDetect() {
        try {
            if (camera != null
                    && isStartFaceDetect) {
                isStartFaceDetect = false;
                camera.setFaceDetectionListener(null);
                camera.stopFaceDetection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     *
     * @param cameraId
     * @param width
     * @param height
     * @return
     */
    public Observable<byte[]> takePicture(final int cameraId, final int width, final int height) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<byte[]> e) throws Exception {
                if (camera == null) {
                    openCamera(cameraId);
                    if (camera == null) {
                        e.onError(new Throwable("摄像头打开失败"));
                        return;
                    }
                    starPreview(width, height, null, null);
                }
                try {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            L.i(TAG, "onPictureTaken: ");
                            camera.stopPreview();
                            isStartFaceDetect = false;
                            if (data != null) {
                                if (!e.isDisposed()) {
                                    e.onNext(data);
                                    e.onComplete();
                                } else {
                                    L.d(TAG, "onPictureTaken: canceled");
                                }
                            } else {
                                e.onError(new Throwable("拍照失败"));
                            }
                        }
                    });
                } catch (Exception e1) {
                    e1.printStackTrace();
                    if (!e.isDisposed()) {
                        e.onError(e1);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        closeCamera();
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        L.i(TAG, "run:doOnDispose ");
                        closeCamera();
                    }
                });
    }

    /**
     * 关闭摄像头
     */
    public synchronized void closeCamera() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewDisplay(null);
                camera.setPreviewCallback(null);
                camera.setFaceDetectionListener(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.release();
            camera = null;
        }
        isCameraUsing = false;
    }

    /**
     * 开启外置摄像头红外补光
     */
    public static void openIRLED() {
        SystemUtil.setGpioHight(221);
    }

    /**
     * 关闭外置摄像头红外补光
     */
    public static void closeIRLED() {
        SystemUtil.setGpioLow(221);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean isCameraUsing() {
        return isCameraUsing;
    }



    public int getCameraId() {
        return cameraId;
    }

}

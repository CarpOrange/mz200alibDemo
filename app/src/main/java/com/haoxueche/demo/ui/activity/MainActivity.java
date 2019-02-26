package com.haoxueche.demo.ui.activity;

import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.haoxueche.demo.R;
import com.haoxueche.demo.listener.TakePicture2Listener;
import com.haoxueche.demo.listener.TakePictureListener;
import com.haoxueche.demo.manager.FaceVerifyManager;
import com.haoxueche.demo.manager.FaceVerifyNewManager;
import com.haoxueche.mz200alib.util.CameraConfig;
import com.haoxueche.mz200alib.util.FileUtil;
import com.haoxueche.mz200alib.util.T;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void icTest(View view) {
        Intent intent = new Intent(this, ICCardActivity.class);
        startActivity(intent);
    }

    public void idTest(View view) {
        Intent intent = new Intent(this, IdCardActivity.class);
        startActivity(intent);
    }

    public void camera2Test(View view) {
        Intent intent = new Intent(this, FaceVerifyNewActivity.class);
        startActivity(intent);
    }

    public void cameraTest(View view) {
        Intent intent = new Intent(this, FaceVerifyActivity.class);
        startActivity(intent);
    }

    public void floatTakePhoto(View view) {
        final int exampleRequestCode = 1;
        FaceVerifyManager.getInstance().takePhoto(CameraInfo.CAMERA_FACING_FRONT, true, false,
                true, exampleRequestCode, new TakePictureListener() {

            @Override
            public void onTakePictureBack(int requestCode, int resultCode) {
                switch (requestCode) {
                    case exampleRequestCode:
                        if (resultCode == RESULT_OK) {
                            T.show("照片保存在：" + FileUtil.IMAGE_TEMP_ABSOLUTE_DIR);
                        } else if (resultCode == CameraConfig.RESULT_OUT_CAMERA_ERROR) {
                            T.showSpeak("外置摄像头故障，请学员和教练签退并通知维护人员修理设备");
                        } else {
                            T.showSpeak("拍照失败");
                        }
                        break;
                }
            }
        });
    }

    public void floatTakePhoto2(View view) {
        final int exampleRequestCode = 2;
        FaceVerifyNewManager.getInstance().takePhoto(5, true, true, true, true,
                exampleRequestCode, new TakePicture2Listener() {

                    @Override
                    public void onTakePictureBack(int requestCode, int resultCode, String[]
                            photoInfo) {
                        switch (requestCode) {
                            case exampleRequestCode:
                                if (resultCode == RESULT_OK) {
                                    //判断数据是否合法
                                    if (photoInfo == null || photoInfo.length < 3) {
                                        return;
                                    }
                                    //照片编号
                                    String photoNo = photoInfo[0];
                                    //照片文件大小
                                    int length = Integer.parseInt(photoInfo[1]);
                                    //照片路径
                                    String path = photoInfo[2];
                                    T.show("照片保存在：" + path);
                                } else if (resultCode == CameraConfig.RESULT_OUT_CAMERA_ERROR) {
                                    T.showSpeak("外置摄像头故障，请学员和教练签退并通知维护人员修理设备");
                                    break;
                                }
                        }
                    }
                }
        );
    }
}

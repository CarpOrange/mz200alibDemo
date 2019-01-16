package com.haoxueche.mz200alib.camera2;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/15.
 * 相机业务相关参数
 */
public class CameraConfig {
    public final static int RESULT_HARDWARE_ERROR = 6; //相机故障
    public final static int RESULT_CAMERA_USING = 7; //相机使用中
    public final static int RESULT_OUT_CAMERA_ERROR = 8; //外置相机蓝屏
    public final static int RESULT_FACE_VERIFY_FAIL = 9; //人脸验证失败
    public final static int RESULT_FACE_VERIFY_HTTP_ERROR = 10; //人脸验证http出错  网络问题等情况
    public final static int RESULT_CAMERA_BLOCKED = 11; //相机卡住了

}

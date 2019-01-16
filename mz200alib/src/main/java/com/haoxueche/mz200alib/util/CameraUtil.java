package com.haoxueche.mz200alib.util;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/15.
 */
public class CameraUtil {
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
}

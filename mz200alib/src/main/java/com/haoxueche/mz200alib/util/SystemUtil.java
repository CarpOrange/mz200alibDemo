package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.haoxueche.winterlog.L;
import com.temolin.hardware.GPIO_Pin;

import java.lang.reflect.Method;

import static android.content.Context.POWER_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by xiezhongming on 17/9/18.
 * 系统工具类
 */

public class SystemUtil {
    public static final String TAG = "SystemUtil";

    private static PowerManager.WakeLock mWakeLock;
     private static PowerManager pm = null;


    /**
     * 强制开启屏幕
     */
    public static void startWakeLock(boolean stop) {
        if (pm == null) {
            pm = (PowerManager) ContextHolder.getInstance().getSystemService(POWER_SERVICE);
        }

        if (pm.isScreenOn()) {
            return;
        }
        stopWakeLock();
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager
                .ACQUIRE_CAUSES_WAKEUP, "wakeLock");
        mWakeLock.acquire();
        if (stop) {
            stopWakeLock();
        }
    }

    /**
     * 在休眠时间到后，屏幕会自动关闭。
     */
    public static void stopWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    public static void setGpioHight(int gpioIndex) {
        L.i( "setGpioHight==" + gpioIndex);
        GPIO_Pin gpio_pin = new GPIO_Pin(gpioIndex);
        gpio_pin.setModeOUTPUT();
        gpio_pin.setHIGH();
        gpio_pin.close();
    }

    public static void setGpioLow(int gpioIndex) {
        L.i( "setGpioLow==" + gpioIndex);
        GPIO_Pin gpio_pin = new GPIO_Pin(gpioIndex);
        gpio_pin.setModeOUTPUT();
        gpio_pin.setLOW();
        gpio_pin.close();
    }

    /**
     * android5.0以上设备此方法有效
     *
     * @return 获取双卡手机的卡2 imei
     */
    public static String getImei(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        Class clazz = manager.getClass();
        Method getImei = null;//(int slotId)
        String imei2 = "";
        try {
            getImei = clazz.getDeclaredMethod("getImei", int.class);
            //获得IMEI 1的信息：
//            getImei.invoke(manager, 0);
            //获得IMEI 2的信息：
            imei2 = (String) getImei.invoke(manager, 1);
        } catch (Exception e) {
            L.e(e);
        }
        return imei2;
    }


}

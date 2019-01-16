package com.haoxueche.mz200alib.util;

import android.os.PowerManager;

import com.haoxueche.winterlog.L;
import com.temolin.hardware.GPIO_Pin;

import static android.content.Context.POWER_SERVICE;

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


}

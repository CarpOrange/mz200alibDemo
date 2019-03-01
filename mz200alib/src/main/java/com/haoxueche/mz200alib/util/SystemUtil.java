package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.haoxueche.winterlog.L;
import com.temolin.hardware.GPIO_Pin;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by xiezhongming on 17/9/18.
 * 系统工具类
 */

public class SystemUtil {
    public static final String TAG = "SystemUtil";

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

    /**
     *
     * @param context
     * @param seconds 锁屏时间，秒
     * @return 设置是否生效
     */
    public static boolean setScreenLockTime(Context context, int seconds) {
        boolean setSucc = false;
       try {
           setSucc =  Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System
                   .SCREEN_OFF_TIMEOUT, 1000 * seconds); //毫秒数
       } catch (Exception e) {
           L.e(e);
       }
        return setSucc;
    }

    /**
     * 禁止状态栏下拉
     *
     * @param context
     * @return
     */
    public static boolean disableStatusBar(Context context) {
        Object service = context.getSystemService("statusbar");
        try {
            Class<?> statusBarManager = Class
                    .forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod("disable", int.class);
            //expand.invoke(service, 0x00000000);
            expand.invoke(service, 0x00010000);
            return true;
        } catch (Exception e) {
            L.e(e);
        }
        return false;
    }

    /**
     * @param applicationInfo
     * @return 是否系统app
     */
    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


}

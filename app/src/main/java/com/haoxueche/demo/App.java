package com.haoxueche.demo;

import android.app.Application;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.haoxueche.mz200alib.location.LocationHelper;
import com.haoxueche.winterlog.timber.LogUtil;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/14.
 */
public class App extends Application {
    private WakeLock cpuWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        //log工具初始化
        try {
            LogUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //每隔一段时间重新发起定位，主要是用来让设备gps模块定位更准，减少漂移
       try {
           LocationHelper.getInstance().locateAtIntervals();
       } catch (Exception e) {
            e.printStackTrace();
       }

       //保持cpu运转
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App");
        cpuWakeLock.acquire();
    }
}

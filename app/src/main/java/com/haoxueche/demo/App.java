package com.haoxueche.demo;

import android.app.Application;

import com.haoxueche.mz200alib.location.LocationHelper;
import com.haoxueche.winterlog.timber.LogUtil;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/14.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            LogUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
       try {
           //每隔一段时间重新发起定位，主要是用来让设备gps模块定位更准，减少漂移
           LocationHelper.getInstance().locateAtIntervals();
       } catch (Exception e) {
            e.printStackTrace();
       }
    }
}

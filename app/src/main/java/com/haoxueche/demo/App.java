package com.haoxueche.demo;

import android.app.Application;

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
    }
}

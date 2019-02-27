package com.haoxueche.winterlog.timber;


import com.haoxueche.winterlog.BuildConfig;
import com.haoxueche.winterlog.timber.Timber.DebugTree;

/**
 * Created by Lyc(987424501@qq.com) on 2018/12/18.
 */
public class LogUtil {
    public static void init() {
        if(BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
    }
}

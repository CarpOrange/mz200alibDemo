package com.haoxueche.winterlog.timber;


import android.content.Context;

import com.haoxueche.winterlog.BuildConfig;
import com.haoxueche.winterlog.timber.Timber.DebugTree;

/**
 * Created by Lyc(987424501@qq.com) on 2018/12/18.
 */
public class LogUtil {
    public static void init(Context context, String password, String savePath, int saveDay, int maxSize) {
        if(BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
        Timber.plant(new LoganTree(context, password, savePath, saveDay, maxSize));
    }
}

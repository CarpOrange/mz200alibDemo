package com.haoxueche.demo;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.haoxueche.winterlog.L;
import com.haoxueche.winterlog.timber.LogUtil;

import java.io.File;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/14.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            /**
             * 这里为什么要专门指定app存储路径而不使用Context.getExternalFilesDir()呢？
             * 因为发现刚开机的时候这个方法返回null，会导致异常
             */
            String savePath = getRoot()
                    + "Android" + File.separator + "data" + File.separator + BuildConfig
                    .APPLICATION_ID
                    + File.separator + "files" + File.separator + "logan_v1";
            LogUtil.init(this, "1234567890123456",
                    savePath, 10, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRoot() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //这个是设备底层的bug,launcherApp容易遇到
        if(TextUtils.isEmpty(path) || path.contains("null")) {
            throw new IllegalStateException("设备存储路径未初始化:" + path);
        }
        L.d("root==" + path);
        return path + File.separator;
    }

}

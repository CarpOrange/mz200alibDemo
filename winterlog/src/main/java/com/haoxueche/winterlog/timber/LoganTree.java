package com.haoxueche.winterlog.timber;

import android.content.Context;

import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import com.haoxueche.winterlog.timber.Timber.DebugTree;

/**
 * Created by Lyc(987424501@qq.com) on 2018/12/19.
 */
public class LoganTree extends DebugTree {
    public LoganTree(Context context, String password, String savePath, int saveDay, long maxSize) {
        LoganConfig config = new LoganConfig.Builder()
                .setCachePath(context.getFilesDir().getAbsolutePath())
                .setPath(savePath)
                .setEncryptKey16(password.getBytes())
                .setEncryptIV16(password.getBytes())
                .setDay(saveDay)
                .setMaxFile(maxSize)
                .build();
        Logan.init(config);
        Logan.setDebug(true);
    }

    public final String FORMAT = "%s[%s] %s";
    @Override
    protected void log(boolean persistence, int priority,  String tag, String message,
                        Throwable throwable) {
        if(persistence) {
            Logan.w(String.format(FORMAT, Util.getPriorityStr(priority), tag, message), 1);
        }
    }
}

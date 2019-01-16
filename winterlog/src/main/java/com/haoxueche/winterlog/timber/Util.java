package com.haoxueche.winterlog.timber;

import android.util.Log;

/**
 * Created by Lyc(987424501@qq.com) on 2018/12/20.
 */
public class Util {
    public static String getPriorityStr(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return "[V]";
            case Log.DEBUG:
                return "[D]";
            case Log.INFO:
                return "[I]";
            case Log.WARN:
                return "[W]";
            case Log.ERROR:
                return "[E]";
            case Log.ASSERT:
                return "[A]";
            default:
                return "[UNKNOWN]";
        }
    }
}

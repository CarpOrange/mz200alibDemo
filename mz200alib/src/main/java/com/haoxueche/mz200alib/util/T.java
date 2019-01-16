package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.widget.Toast;

import com.haoxueche.mz200alib.util.SpeechUtils.SpeechCallback;


public class T {

    public static void show(String msg) {

//        Toast.makeText(ContextHolder.getInstance(), msg, Toast.LENGTH_SHORT).show();
		showToast(ContextHolder.getInstance(), msg);
    }

    public static void showSpeak(String msg) {
        T.show(msg);
        SpeechUtils.getInstance().speak(msg);
    }

    public static void showSpeak(String msg, SpeechCallback callback) {
        T.show(msg);
        SpeechUtils.getInstance().speak(msg, callback);
    }

    public static void show(String msg, int time) {
        Toast.makeText(ContextHolder.getInstance(), msg, time).show();
    }

    public static void show(int resId) {
        Toast.makeText(ContextHolder.getInstance(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(int resId, int duration) {
        Toast.makeText(ContextHolder.getInstance(), resId, duration).show();
    }

    public static void show(Context context, String msg) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int resId) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String msg, int duration) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, duration).show();
    }

    public static void show(Context context, int resId, int duration) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resId, duration).show();
    }

    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else if(message != null){
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

}

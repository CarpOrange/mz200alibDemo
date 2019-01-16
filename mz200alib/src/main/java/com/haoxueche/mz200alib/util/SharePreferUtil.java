package com.haoxueche.mz200alib.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LiYuchen on 2017/1/10.
 * 987424501@qq.com
 */
public class SharePreferUtil {

    public static final String KEY_INSIDE_CAMERA_ROTATE = "inside_camera_rotate";
    public static final String KEY_OUT_CAMERA_ROTATE = "out_camera_rotate";
    public static final String KEY_OUT_PHOTO_MIRROR_FLIP = "out_camera_mirror_flip";

    public static SharedPreferences getSharedPreferences() {
        return ContextHolder.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public static void putString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public static void putInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public static void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }


    public static void putLong(String key, long value) {
        getEditor().putLong(key, value).commit();
    }

    public static boolean isInsideCameraRotate() {
        return getSharedPreferences().getBoolean(KEY_INSIDE_CAMERA_ROTATE, false);
    }

    public static boolean isOutCameraRotate() {
        return getSharedPreferences().getBoolean(KEY_OUT_CAMERA_ROTATE, false);
    }


    public static Boolean isOutPhotoMirrorFlip() {
        return getSharedPreferences().getBoolean(KEY_OUT_PHOTO_MIRROR_FLIP, false);
    }

}

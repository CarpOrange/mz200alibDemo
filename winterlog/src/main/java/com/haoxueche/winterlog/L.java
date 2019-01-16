package com.haoxueche.winterlog;

import com.haoxueche.winterlog.timber.Timber;

/**
 * Created by Lyc(987424501@qq.com) on 2018/12/19.
 * Log模块，各项目可通用。由于只在MZ200A型智能计时设备上测试过，别的项目使用前需多机型、系统验证
 */
public class L {

    /**
     * Log a verbose message with optional format args.
     */
    public static void v(boolean persistence, String message, Object... args) {
        Timber.v(persistence, message, args);
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(boolean persistence, Throwable t, String message, Object... args) {
        Timber.v(persistence, t, message, args);
    }

    /**
     * Log a verbose exception.
     */
    public static void v(boolean persistence, Throwable t) {
        Timber.v(persistence, t);
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(boolean persistence, String message, Object... args) {
        Timber.d(persistence, message, args);
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(boolean persistence, Throwable t, String message, Object... args) {
        Timber.d(persistence, t, message, args);
    }

    /**
     * Log a debug exception.
     */
    public static void d(boolean persistence, Throwable t) {
        Timber.d(persistence, t);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(boolean persistence, String message, Object... args) {
        Timber.i(persistence, message, args);
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(boolean persistence, Throwable t, String message, Object... args) {
        Timber.i(persistence, t, message, args);
    }

    /**
     * Log an info exception.
     */
    public static void i(boolean persistence, Throwable t) {
        Timber.i(persistence, t);
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(boolean persistence, String message, Object... args) {
        Timber.w(persistence, message, args);
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(boolean persistence, Throwable t, String message, Object... args) {
        Timber.w(persistence, t, message, args);
    }

    /**
     * Log a warning exception.
     */
    public static void w(boolean persistence, Throwable t) {
        Timber.w(persistence, t);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(boolean persistence, String message, Object... args) {
        Timber.e(persistence, message, args);
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(boolean persistence, Throwable t, String message, Object... args) {
        Timber.e(persistence, t, message, args);
    }

    /**
     * Log an error exception.
     */
    public static void e(boolean persistence, Throwable t) {
        Timber.e(persistence, t);
    }

    /**
     * Log a verbose message with optional format args.
     */
    public static void v(String message, Object... args) {
        v(false, message, args);
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(Throwable t, String message, Object... args) {
        v(false, t, message, args);
    }

    /**
     * Log a verbose exception.
     */
    public static void v(Throwable t) {
        v(false, t);
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(String message, Object... args) {
        d(false, message, args);
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(Throwable t, String message, Object... args) {
        d(false, t, message, args);
    }

    /**
     * Log a debug exception.
     */
    public static void d(Throwable t) {
        d(false, t);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(String message, Object... args) {
        i(false, message, args);
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(Throwable t, String message, Object... args) {
        i(false, t, message, args);
    }

    /**
     * Log an info exception.
     */
    public static void i(Throwable t) {
        i(false, t);
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(String message, Object... args) {
        w(false, message, args);
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(Throwable t, String message, Object... args) {
        L.w(false, t, message, args);
    }

    /**
     * Log a warning exception.
     */
    public static void w(Throwable t) {
        w(false, t);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(String message, Object... args) {
        e(false, message, args);
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(Throwable t, String message, Object... args) {
        e(false, t, message, args);
    }

    /**
     * Log an error exception.
     */
    public static void e(Throwable t) {
        e(false, t);
    }

}

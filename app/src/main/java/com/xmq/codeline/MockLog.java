package com.xmq.codeline;

import android.util.Log;

/**
 * @author xmqyeah
 * @CreateDate 2021/11/24 21:52
 */
public final class MockLog {
    final static String TAG = "DefTag";
    public MockLog(String tag) {
        this.tag = tag;
    }
    public static MockLog get(String tag) {
        return new MockLog(tag);
    }
    String tag;
    public static void d(String message) {
        get(TAG).debug(message);
//        return 1;
    }
    public static void i(String message) {
        Log.i(TAG, message);
    }
    public static void i(String tag, String message) {
        Log.i(tag, message);
    }
    public static void i(String tag, String message, Object... args) {
        Log.i(tag, String.format(message, args));
    }
    public static void w(String message) {
        Log.w(TAG, message);
    }
    public static void e(String message) {
        Log.e(TAG, message);
    }
    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
    public void debug(String message) {
        Log.d(tag, message);
    }
//    public void i(String message) {
//        Log.i(tag, message);
//    }
//    public void w(String message) {
//        Log.w(tag, message);
//    }
//    public void e(String message) {
//        Log.e(tag, message);
//    }
//    public void e(String message, Throwable throwable) {
//        Log.e(tag, message, throwable);
//    }

}

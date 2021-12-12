package com.xmq.codeline;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 2021-12-12 22:54:49.240 18521-18521/com.xmq.codeline I/System.out: Hello MockLog.d
         * 2021-12-12 22:54:49.240 18521-18521/com.xmq.codeline D/DefTag: (MainActivity.java:13)MainActivity onCreate()
         * 2021-12-12 22:54:49.240 18521-18521/com.xmq.codeline I/System.out: Invoke TestUtil.onCreate()
         */
        MockLog.d("MainActivity onCreate()");
        TestUtil.INSTANCE.onCreate();
    }

    byte tet(){
        return 0;
    }
    char test() {
        return ' ';
    }

    /**
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline D/TestUtil: clickLog()
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline I/(TestUtil.kt:17)TestUtil: clickLog()
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline I/System.out: Hello MockLog.d
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline D/DefTag: (MainActivity.java:25)clickLog debug
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline I/DefTag: (MainActivity.java:26)clickLog info
     * 2021-12-12 22:55:03.279 18521-18521/com.xmq.codeline I/(MainActivity.java:27)clickTag: clickLog info
     * 2021-12-12 22:55:03.280 18521-18521/com.xmq.codeline I/clickTag: clickLog : click arg== 5
     * 2021-12-12 22:55:03.280 18521-18521/com.xmq.codeline W/DefTag: (MainActivity.java:29)clickLog warn
     * 2021-12-12 22:55:03.280 18521-18521/com.xmq.codeline E/DefTag: (MainActivity.java:30)clickLog error
     * 2021-12-12 22:55:03.282 18521-18521/com.xmq.codeline E/DefTag: (MainActivity.java:32)clickLog error
     *     java.util.EmptyStackException
     *         at com.xmq.codeline.MainActivity.clickLog(MainActivity.java:32)
     *         at java.lang.reflect.Method.invoke(Native Method)
     *         at androidx.appcompat.app.AppCompatViewInflater$DeclaredOnClickListener.onClick(AppCompatViewInflater.java:409)
     *         at android.view.View.performClick(View.java:7481)
     *         at android.widget.TextView.performClick(TextView.java:13926)
     * 2021-12-12 22:55:03.283 18521-18521/com.xmq.codeline E/(MainActivity.java:33)clickTag: clickLog error
     *     java.util.EmptyStackException
     *         at com.xmq.codeline.MainActivity.clickLog(MainActivity.java:33)
     *         at java.lang.reflect.Method.invoke(Native Method)
     *         at androidx.appcompat.app.AppCompatViewInflater$DeclaredOnClickListener.onClick(AppCompatViewInflater.java:409)
     *         at android.view.View.performClick(View.java:7481)
     *         at android.widget.TextView.performClick(TextView.java:13926)
     * @param view
     */
    public void clickLog(View view) {
        TestUtil.INSTANCE.clickLog();
        MockLog.d("clickLog debug");
        MockLog.i("clickLog info");
        MockLog.i("clickTag", "clickLog info");
        MockLog.i("clickTag", "clickLog : %s== %d", "click arg", 5);
        MockLog.w("clickLog warn");
        MockLog.e("clickLog error");
        test("::");
        MockLog.e("clickLog error", new EmptyStackException());
        MockLog.e("clickTag", "clickLog error", new EmptyStackException());
    }

    public String test(String info) {
        return info;
    }
    public String test3(String info) {
        return info;
    }
}
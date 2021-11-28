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
        MockLog.d("MainActivity onCreate()");
        TestUtil.INSTANCE.onCreate();
    }

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
}
package com.example.learningenglishapplication.Home;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

// Lớp này sẽ chạy đầu tiên khi ứng dụng khởi động
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable dynamic color on supported devices
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}

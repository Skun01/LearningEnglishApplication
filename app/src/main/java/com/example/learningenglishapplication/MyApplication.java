package com.example.learningenglishapplication;

import android.app.Application;

// Lớp này sẽ chạy đầu tiên khi ứng dụng khởi động
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Code trong này sẽ chạy một lần duy nhất khi app mở
        // Chúng ta sẽ thêm logic áp dụng theme ở đây sau, khi đã có người dùng đăng nhập
    }
}

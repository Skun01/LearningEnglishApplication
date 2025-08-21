package com.example.learningenglishapplication.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.learningenglishapplication.Utils.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy userId từ intent
        long userId = intent.getLongExtra("userId", -1);
        
        if (userId != -1) {
            // Hiển thị thông báo nhắc nhở học tập
            NotificationHelper.showNotification(context, userId);
        }
    }
}
package com.example.learningenglishapplication.System;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyNotificationScheduler {

    private static final String CHANNEL_ID = "daily_notification_channel";
    private static final String WORK_TAG = "daily_notification_work";
    private static final int NOTIFICATION_ID = 1001;

    // Setup notification channel (bắt buộc cho Android 8.0+)
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo hàng ngày lúc 7h sáng");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Gửi notification
    public static void showDailyNotification(Context context) {
        // Tạo intent mở app khi click notification
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Thay bằng icon của bạn
                .setContentTitle("🌟 Chào buổi sáng!")
                .setContentText("Hãy bắt đầu học tiếng Anh ngay nào! 📚")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Chào buổi sáng! Đã đến lúc học tiếng Anh rồi. Hôm nay bạn sẽ học gì mới? 🚀"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // Tự động xóa khi click
                .setDefaults(NotificationCompat.DEFAULT_ALL); // Sound, vibrate, lights

        // Hiển thị notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Schedule notification chạy mỗi ngày 7h sáng
    public static void scheduleDailyNotification(Context context) {
        // Tạo notification channel trước
        createNotificationChannel(context);

        // Constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build();

        // Tạo periodic work request
        PeriodicWorkRequest dailyNotificationWork = new PeriodicWorkRequest.Builder(
                DailyNotificationWorker.class,
                1, TimeUnit.MINUTES // Mỗi 24h
        )
                .setConstraints(constraints)
                .setInitialDelay(calculateDelayTo7AM(), TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build();

        // Enqueue work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyNotificationWork
        );
    }

    // Tính delay đến 7h sáng
    private static long calculateDelayTo7AM() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, 20); // 7h sáng
        target.set(Calendar.MINUTE, 58);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        // Nếu đã qua 7h sáng hôm nay thì schedule cho 7h sáng ngày mai
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        return target.getTimeInMillis() - now.getTimeInMillis();
    }

    // Hủy schedule
    public static void cancelDailyNotification(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG);
    }

    // Test notification ngay lập tức
    public static void testNotificationNow(Context context) {
        createNotificationChannel(context);
        showDailyNotification(context);
    }
}
package com.example.learningenglishapplication.Utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.learningenglishapplication.Data.DataHelper.UserSettingDataHelper;
import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Receivers.NotificationReceiver;

import java.util.Calendar;

public class NotificationHelper {

    private static final String CHANNEL_ID = "learning_reminder_channel";
    private static final String CHANNEL_NAME = "Nhắc nhở học tập";
    private static final String CHANNEL_DESCRIPTION = "Kênh thông báo nhắc nhở học tập hàng ngày";
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_REQUEST_CODE = 123;

    private Context context;
    private UserSettingDataHelper settingHelper;

    public NotificationHelper(Context context) {
        this.context = context;
        this.settingHelper = new UserSettingDataHelper(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Tạo notification channel (chỉ cần thiết từ Android 8.0 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(long userId, int hourOfDay, int minute) {
        // Kiểm tra xem người dùng có bật thông báo không
        if (!settingHelper.isNotificationsEnabled(userId)) {
            cancelNotification();
            return;
        }

        // Tạo intent cho NotificationReceiver
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("userId", userId);
        
        // Tạo PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Thiết lập thời gian thông báo
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Nếu thời gian đã qua trong ngày, đặt cho ngày hôm sau
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Lấy AlarmManager và đặt lịch thông báo lặp lại hàng ngày
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public void cancelNotification() {
        // Hủy thông báo đã lên lịch
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void showNotification(Context context, long userId) {
        // Lấy thông tin mục tiêu học tập hàng ngày
        UserSettingDataHelper settingHelper = new UserSettingDataHelper(context);
        int dailyGoal = settingHelper.getDailyGoal(userId);
        int todayLearned = settingHelper.getTodayLearnedWords(userId);

        // Tạo nội dung thông báo
        String title = "Nhắc nhở học tập";
        String message;
        
        if (todayLearned >= dailyGoal) {
            message = "Chúc mừng! Bạn đã hoàn thành mục tiêu học tập hôm nay.";
        } else {
            int remaining = dailyGoal - todayLearned;
            message = "Bạn còn " + remaining + " từ vựng để đạt mục tiêu hôm nay. Hãy tiếp tục học!";
        }

        // Tạo intent để mở ứng dụng khi nhấn vào thông báo
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Xây dựng thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
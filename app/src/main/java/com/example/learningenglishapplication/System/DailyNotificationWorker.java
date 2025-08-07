package com.example.learningenglishapplication.System;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("WorkerHasAPublicModifier")
class DailyNotificationWorker extends androidx.work.Worker {

    public DailyNotificationWorker(@androidx.annotation.NonNull Context context,
                                   @androidx.annotation.NonNull androidx.work.WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @androidx.annotation.NonNull
    @Override
    public Result doWork() {
        android.util.Log.d("DailyNotificationWorker", "Sending daily notification at 7AM...");

        // Gửi notification
        DailyNotificationScheduler.showDailyNotification(getApplicationContext());

        return Result.success();
    }
}
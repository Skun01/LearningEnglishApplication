package com.example.learningenglishapplication.Data.DAO;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_STAT_DATE;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_STAT_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_STAT_WORDS_LEARNED;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_VERSION;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_STATISTICS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticsDAO extends SQLiteOpenHelper {

    public StatisticsDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Ghi nhận một từ mới được học cho ngày hôm nay.
     */
    public void logWordLearned(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Kiểm tra xem đã có bản ghi cho ngày hôm nay chưa
        Cursor cursor = db.query(TABLE_STATISTICS, null,
                COLUMN_STAT_USER_ID + "=? AND " + COLUMN_STAT_DATE + "=?",
                new String[]{String.valueOf(userId), todayDate}, null, null, null);

        if (cursor.moveToFirst()) {
            // Đã có -> UPDATE
            int currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STAT_WORDS_LEARNED));
            ContentValues values = new ContentValues();
            values.put(COLUMN_STAT_WORDS_LEARNED, currentCount + 1);
            db.update(TABLE_STATISTICS, values,
                    COLUMN_STAT_USER_ID + "=? AND " + COLUMN_STAT_DATE + "=?",
                    new String[]{String.valueOf(userId), todayDate});
        } else {
            // Chưa có -> INSERT
            ContentValues values = new ContentValues();
            values.put(COLUMN_STAT_USER_ID, userId);
            values.put(COLUMN_STAT_DATE, todayDate);
            values.put(COLUMN_STAT_WORDS_LEARNED, 1);
            db.insert(TABLE_STATISTICS, null, values);
        }
        cursor.close();
        db.close();
    }

    /**
     * Lấy dữ liệu thống kê trong 7 ngày gần nhất để vẽ biểu đồ
     */
    public Cursor getWeeklyStats(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Lấy dữ liệu từ 7 ngày trước đến hôm nay
        String query = "SELECT * FROM " + TABLE_STATISTICS + " WHERE " + COLUMN_STAT_USER_ID + " = " + userId +
                " AND " + COLUMN_STAT_DATE + " >= date('now', '-7 days') ORDER BY " + COLUMN_STAT_DATE + " ASC";
        return db.rawQuery(query, null);
    }
}

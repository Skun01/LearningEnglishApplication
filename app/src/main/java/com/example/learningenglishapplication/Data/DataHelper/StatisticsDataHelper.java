package com.example.learningenglishapplication.Data.DataHelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.learningenglishapplication.Data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticsDataHelper {
    private final DatabaseHelper dbHelper;

    public StatisticsDataHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Ghi nhận một từ mới được học cho ngày hôm nay.
     * Tự động UPDATE nếu đã có bản ghi cho ngày hôm nay, ngược lại sẽ INSERT.
     */
    public void logWordLearned(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Kiểm tra xem đã có bản ghi cho ngày hôm nay chưa
        Cursor cursor = db.query(DatabaseHelper.TABLE_STATISTICS, null,
                DatabaseHelper.COLUMN_STAT_USER_ID + "=? AND " + DatabaseHelper.COLUMN_STAT_DATE + "=?",
                new String[]{String.valueOf(userId), todayDate}, null, null, null);

        if (cursor.moveToFirst()) {
            // Đã có -> UPDATE: Tăng số từ đã học lên 1
            int currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED));
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED, currentCount + 1);
            db.update(DatabaseHelper.TABLE_STATISTICS, values,
                    DatabaseHelper.COLUMN_STAT_USER_ID + "=? AND " + DatabaseHelper.COLUMN_STAT_DATE + "=?",
                    new String[]{String.valueOf(userId), todayDate});
        } else {
            // Chưa có -> INSERT: Tạo bản ghi mới với số từ là 1
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_STAT_USER_ID, userId);
            values.put(DatabaseHelper.COLUMN_STAT_DATE, todayDate);
            values.put(DatabaseHelper.COLUMN_STAT_WORDS_LEARNED, 1);
            db.insert(DatabaseHelper.TABLE_STATISTICS, null, values);
        }
        cursor.close();
        db.close();
    }

    /**
     * Lấy dữ liệu thống kê trong 7 ngày gần nhất để vẽ biểu đồ.
     * Trả về Cursor để Activity xử lý.
     */
    public Cursor getWeeklyStats(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_STATISTICS +
                " WHERE " + DatabaseHelper.COLUMN_STAT_USER_ID + " = " + userId +
                " AND " + DatabaseHelper.COLUMN_STAT_DATE + " >= date('now', '-7 days')" +
                " ORDER BY " + DatabaseHelper.COLUMN_STAT_DATE + " ASC";
        return db.rawQuery(query, null);
    }
}
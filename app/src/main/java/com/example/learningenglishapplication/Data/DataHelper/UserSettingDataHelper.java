package com.example.learningenglishapplication.Data.DataHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.learningenglishapplication.Data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserSettingDataHelper extends DatabaseHelper {

    public UserSettingDataHelper(Context context) {
        super(context);
    }

    /**
     * Lưu theme cho user. Nếu chưa có thì INSERT, có rồi thì UPDATE.
     */
    public void saveThemeSetting(long userId, String theme) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETTING_USER_ID, userId);
        values.put(COLUMN_SETTING_THEME, theme);

        // replace() sẽ insert mới hoặc update dựa trên primary key
        db.replace(TABLE_USER_SETTINGS, null, values);
        db.close();
    }

    /**
     * Lấy theme hiện tại của user.
     * @return "Light", "Dark" hoặc "System" (mặc định)
     */
    public String getThemeSetting(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER_SETTINGS,
                new String[]{COLUMN_SETTING_THEME},
                COLUMN_SETTING_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SETTING_THEME));
            cursor.close();
            db.close();
            return theme;
        }
        cursor.close();
        db.close();
        return "System";
    }

    /**
     * Ghi nhận một từ mới được học trong ngày hôm nay.
     */
    public void logWordLearned(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Cursor cursor = db.query(
                TABLE_STATISTICS,
                null,
                COLUMN_STAT_USER_ID + "=? AND " + COLUMN_STAT_DATE + "=?",
                new String[]{String.valueOf(userId), todayDate},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            // đã có bản ghi -> update count
            int currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STAT_WORDS_LEARNED));
            ContentValues values = new ContentValues();
            values.put(COLUMN_STAT_WORDS_LEARNED, currentCount + 1);
            db.update(
                    TABLE_STATISTICS,
                    values,
                    COLUMN_STAT_USER_ID + "=? AND " + COLUMN_STAT_DATE + "=?",
                    new String[]{String.valueOf(userId), todayDate}
            );
        } else {
            // chưa có -> insert mới
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
     * Lấy dữ liệu thống kê trong 7 ngày gần nhất để vẽ biểu đồ.
     */
    public Cursor getWeeklyStats(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT * FROM " + TABLE_STATISTICS +
                        " WHERE " + COLUMN_STAT_USER_ID + " = ?" +
                        " AND " + COLUMN_STAT_DATE + " >= date('now', '-6 days')" +
                        " ORDER BY " + COLUMN_STAT_DATE + " ASC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
}

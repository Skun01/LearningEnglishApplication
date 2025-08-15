package com.example.learningenglishapplication.Data.DataHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.learningenglishapplication.Data.DatabaseHelper;


public class UserSettingDataHelper {
    private final DatabaseHelper dbHelper;

    public UserSettingDataHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Lưu hoặc cập nhật cài đặt theme cho người dùng.
     * Sử dụng db.replace() để tự động INSERT nếu chưa có, hoặc UPDATE nếu đã có.
     */
    public void saveThemeSetting(long userId, String theme) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SETTING_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_SETTING_THEME, theme);
        db.replace(DatabaseHelper.TABLE_USER_SETTINGS, null, values);
        db.close();
    }

    /**
     * Lấy cài đặt theme của người dùng.
     * @return "Light", "Dark", hoặc "System" (làm mặc định).
     */
    public String getThemeSetting(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_SETTINGS,
                new String[]{DatabaseHelper.COLUMN_SETTING_THEME},
                DatabaseHelper.COLUMN_SETTING_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        String theme = "System"; // Giá trị mặc định
        if (cursor.moveToFirst()) {
            theme = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SETTING_THEME));
        }
        cursor.close();
        db.close();
        return theme;
    }
}
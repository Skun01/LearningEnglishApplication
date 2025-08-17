package com.example.learningenglishapplication.Data.DataHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.learningenglishapplication.Data.DatabaseHelper;

public class UserDataHelper extends DatabaseHelper {

    public UserDataHelper(Context context) {
        super(context);
    }

    // Thêm user mới
    public boolean addUser(String email, String password, String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_NICKNAME, nickname);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1; // true nếu insert thành công
    }

    // Kiểm tra đăng nhập -> trả về userId, -1 nếu sai
    public long checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();
        db.close();

        return userId;
    }

    // Lấy nickname của user (nếu cần hiển thị trong Profile)
    public String getNickname(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_NICKNAME},
                COLUMN_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        String nickname = null;
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NICKNAME));
        }

        cursor.close();
        db.close();

        return nickname;
    }
}

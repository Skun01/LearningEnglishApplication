package com.example.learningenglishapplication.Data.DAO;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_EMAIL;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_PASSWORD;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_VERSION;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_USERS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDAO extends SQLiteOpenHelper {

    public UserDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // ----- CÁC PHƯƠNG THỨC CHO USERS -----

    /**
     * Thêm một người dùng mới vào database
     * @return true nếu thành công, false nếu thất bại (ví dụ: email đã tồn tại)
     */
    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        // **LƯU Ý QUAN TRỌNG**: Trong thực tế, KHÔNG BAO GIỜ lưu mật khẩu dạng text.
        // Bạn phải HASH mật khẩu trước khi lưu. Ví dụ: dùng thư viện BCrypt.
        // Vì mục đích hướng dẫn, chúng ta sẽ tạm thời lưu dạng text.
        values.put(COLUMN_USER_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Kiểm tra thông tin đăng nhập của người dùng
     * @return User ID nếu đăng nhập thành công, -1 nếu thất bại.
     */
    public long checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

}

package com.example.learningenglishapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Giả sử bạn có một model Category.java
// import com.example.vocabularyapp.model.Category;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "VocabularyApp.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    // ... (Thêm các tên bảng khác ở đây)

    // Bảng Users - Các cột
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password_hash";

    // Bảng Categories - Các cột
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_USER_ID = "user_id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_DESCRIPTION = "description";


    // Câu lệnh tạo bảng Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL" + ")";

    // Câu lệnh tạo bảng Categories
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CAT_USER_ID + " INTEGER,"
            + COLUMN_CAT_NAME + " TEXT NOT NULL,"
            + COLUMN_CAT_DESCRIPTION + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

    // ... (Thêm các câu lệnh tạo bảng khác ở đây)


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        // ... (Thực thi các câu lệnh tạo bảng khác)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        // Tạo lại bảng
        onCreate(db);
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

    // ----- CÁC PHƯƠNG THỨC CHO CATEGORIES -----

    /**
     * Thêm một thể loại mới
     */
    public boolean addCategory(String name, String description, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME, name);
        values.put(COLUMN_CAT_DESCRIPTION, description);
        values.put(COLUMN_CAT_USER_ID, userId);
        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Lấy tất cả thể loại của một người dùng
     * @return Danh sách các thể loại
     */

    /**
     * Tìm kiếm thể loại theo tên cho một người dùng cụ thể
     */
    public Cursor getAllCategories(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CATEGORIES, null, COLUMN_CAT_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Cursor searchCategories(long userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CAT_USER_ID + "=? AND " + COLUMN_CAT_NAME + " LIKE ?";
        // Dấu '%' là ký tự đại diện trong SQL, tìm bất cứ chuỗi nào chứa query
        String[] selectionArgs = {String.valueOf(userId), "%" + query + "%"};
        return db.query(TABLE_CATEGORIES, null, selection, selectionArgs, null, null, COLUMN_CAT_NAME);
    }
}
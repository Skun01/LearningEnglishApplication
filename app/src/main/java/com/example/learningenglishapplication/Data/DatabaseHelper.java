package com.example.learningenglishapplication.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Map;
import java.util.HashMap;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

// Giả sử bạn có một model Category.java
// import com.example.vocabularyapp.model.Category;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "VocabularyApp.db";
    public static final int DATABASE_VERSION = 1;

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

    // Tên bảng và cột cho Vocabularies
    public static final String TABLE_VOCABULARIES = "vocabularies";
    public static final String COLUMN_VOCAB_ID = "id";
    public static final String COLUMN_VOCAB_USER_ID = "user_id";
    public static final String COLUMN_VOCAB_CATEGORY_ID = "category_id";
    public static final String COLUMN_VOCAB_WORD = "word";
    public static final String COLUMN_VOCAB_MEANING = "meaning";
    public static final String COLUMN_VOCAB_LEARNED = "learned";
    public static final String COLUMN_VOCAB_DATE_LEARNED = "date_learned";

    // Cần có các cột cho bảng Statistics
    public static final String TABLE_STATISTICS = "statistics";
    public static final String COLUMN_STAT_USER_ID = "user_id";
    public static final String COLUMN_STAT_DATE = "date";
    public static final String COLUMN_STAT_WORDS_LEARNED = "words_learned";


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

    // Câu lệnh tạo bảng Vocabularies
    private static final String CREATE_TABLE_VOCABULARIES = "CREATE TABLE " + TABLE_VOCABULARIES + "("
            + COLUMN_VOCAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_VOCAB_USER_ID + " INTEGER,"
            + COLUMN_VOCAB_CATEGORY_ID + " INTEGER,"
            + COLUMN_VOCAB_WORD + " TEXT NOT NULL,"
            + COLUMN_VOCAB_MEANING + " TEXT,"
            + COLUMN_VOCAB_LEARNED + " TEXT,"
            + COLUMN_VOCAB_DATE_LEARNED + " TEXT,"
            // Thêm các cột khác nếu cần
            + "FOREIGN KEY(" + COLUMN_VOCAB_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY(" + COLUMN_VOCAB_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + ")" + ")";

    // Câu lệnh tạo bảng Statistics
    private static final String CREATE_TABLE_STATISTICS = "CREATE TABLE " + TABLE_STATISTICS + "("
            + COLUMN_STAT_USER_ID + " INTEGER,"
            + COLUMN_STAT_DATE + " TEXT," // Lưu dưới dạng YYYY-MM-DD
            + COLUMN_STAT_WORDS_LEARNED + " INTEGER DEFAULT 0" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_VOCABULARIES);
        db.execSQL(CREATE_TABLE_STATISTICS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        // Tạo lại bảng
        onCreate(db);
    }
}
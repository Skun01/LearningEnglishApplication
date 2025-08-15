package com.example.learningenglishapplication.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "VocabularyApp.db";
    private static final int DATABASE_VERSION = 2;

    // ----- BẢNG USERS -----
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password_hash";
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL" + ")";

    // ----- BẢNG CATEGORIES -----
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_USER_ID = "user_id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_DESCRIPTION = "description";
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CAT_USER_ID + " INTEGER,"
            + COLUMN_CAT_NAME + " TEXT NOT NULL,"
            + COLUMN_CAT_DESCRIPTION + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

    // ----- BẢNG VOCABULARIES -----
    public static final String TABLE_VOCABULARIES = "vocabularies";
    public static final String COLUMN_VOCAB_ID = "id";
    public static final String COLUMN_VOCAB_USER_ID = "user_id";
    public static final String COLUMN_VOCAB_CATEGORY_ID = "category_id";
    public static final String COLUMN_VOCAB_WORD = "word";
    public static final String COLUMN_VOCAB_MEANING = "meaning";
    public static final String COLUMN_VOCAB_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_VOCAB_IS_FAVORITE = "is_favorite";
    public static final String COLUMN_VOCAB_LEARNED = "learned";
    public static final String COLUMN_VOCAB_DATE_LEARNED = "date_learned";

    private static final String CREATE_TABLE_VOCABULARIES = "CREATE TABLE " + TABLE_VOCABULARIES + "("
            + COLUMN_VOCAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_VOCAB_USER_ID + " INTEGER,"
            + COLUMN_VOCAB_CATEGORY_ID + " INTEGER,"
            + COLUMN_VOCAB_WORD + " TEXT NOT NULL,"
            + COLUMN_VOCAB_MEANING + " TEXT,"
            + COLUMN_VOCAB_PRONUNCIATION + " TEXT,"
            + COLUMN_VOCAB_IS_FAVORITE + " INTEGER DEFAULT 0,"
            + COLUMN_VOCAB_LEARNED + " INTEGER DEFAULT 0,"
            + COLUMN_VOCAB_DATE_LEARNED + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_VOCAB_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY(" + COLUMN_VOCAB_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + ")" + ")";

    // ----- BẢNG USER_SETTINGS -----
    public static final String TABLE_USER_SETTINGS = "user_settings";
    public static final String COLUMN_SETTING_USER_ID = "user_id";
    public static final String COLUMN_SETTING_THEME = "theme";
    private static final String CREATE_TABLE_USER_SETTINGS = "CREATE TABLE " + TABLE_USER_SETTINGS + "("
            + COLUMN_SETTING_USER_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_SETTING_THEME + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_SETTING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

    // ----- BẢNG STATISTICS -----
    public static final String TABLE_STATISTICS = "statistics";
    public static final String COLUMN_STAT_USER_ID = "user_id";
    public static final String COLUMN_STAT_DATE = "date";
    public static final String COLUMN_STAT_WORDS_LEARNED = "words_learned";
    private static final String CREATE_TABLE_STATISTICS = "CREATE TABLE " + TABLE_STATISTICS + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STAT_USER_ID + " INTEGER,"
            + COLUMN_STAT_DATE + " TEXT,"
            + COLUMN_STAT_WORDS_LEARNED + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_STAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_VOCABULARIES);
        db.execSQL(CREATE_TABLE_USER_SETTINGS);
        db.execSQL(CREATE_TABLE_STATISTICS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Tạo lại các bảng mới
        onCreate(db);
    }
}
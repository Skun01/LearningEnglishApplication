package com.example.learningenglishapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Map;
import java.util.HashMap;
import com.example.learningenglishapplication.model.Vocabulary;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    // Tên bảng và cột cho Vocabularies
    public static final String TABLE_VOCABULARIES = "vocabularies";
    public static final String COLUMN_VOCAB_ID = "id";
    public static final String COLUMN_VOCAB_USER_ID = "user_id";
    public static final String COLUMN_VOCAB_CATEGORY_ID = "category_id";
    public static final String COLUMN_VOCAB_WORD = "word";
    public static final String COLUMN_VOCAB_MEANING = "meaning";

    // Tên bảng và cột cho UserSettings
    public static final String TABLE_USER_SETTINGS = "user_settings";
    public static final String COLUMN_SETTING_USER_ID = "user_id";
    public static final String COLUMN_SETTING_THEME = "theme";

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
            // Thêm các cột khác nếu cần
            + "FOREIGN KEY(" + COLUMN_VOCAB_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY(" + COLUMN_VOCAB_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + ")" + ")";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_USER_SETTINGS = "CREATE TABLE " + TABLE_USER_SETTINGS + "("
            + COLUMN_SETTING_USER_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_SETTING_THEME + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_SETTING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

    // Câu lệnh tạo bảng Statistics
    private static final String CREATE_TABLE_STATISTICS = "CREATE TABLE " + TABLE_STATISTICS + "("
            + COLUMN_STAT_USER_ID + " INTEGER,"
            + COLUMN_STAT_DATE + " TEXT," // Lưu dưới dạng YYYY-MM-DD
            + COLUMN_STAT_WORDS_LEARNED + " INTEGER DEFAULT 0,"
            + "PRIMARY KEY (" + COLUMN_STAT_USER_ID + ", " + COLUMN_STAT_DATE + ")," // Khóa chính kép
            + "FOREIGN KEY(" + COLUMN_STAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_VOCABULARIES);
        db.execSQL(CREATE_TABLE_USER_SETTINGS);
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

    public boolean addVocabulary(long userId, long categoryId, String word, String meaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOCAB_USER_ID, userId);
        values.put(COLUMN_VOCAB_CATEGORY_ID, categoryId);
        values.put(COLUMN_VOCAB_WORD, word);
        values.put(COLUMN_VOCAB_MEANING, meaning);
        long result = db.insert(TABLE_VOCABULARIES, null, values);
        return result != -1;
    }

    public Cursor getVocabulariesForCategory(long categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VOCABULARIES, null, COLUMN_VOCAB_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)}, null, null, null);
    }

    public String getCategoryName(long categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CAT_NAME}, COLUMN_CAT_ID + "=?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAT_NAME));
            cursor.close();
            return name;
        }
        return "Không rõ";
    }

    /**
     * Lấy danh sách từ vựng của một thể loại dưới dạng List<Vocabulary>
     */
    public List<Vocabulary> getVocabulariesAsList(long categoryId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOCABULARIES, null, COLUMN_VOCAB_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VOCAB_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOCAB_MEANING));

                vocabularyList.add(new Vocabulary(id, word, meaning));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vocabularyList;
    }

    // ----- CÁC PHƯƠƠNG THỨC MỚI CHO CATEGORIES -----

    /**
     * Cập nhật thông tin của một thể loại đã có.
     * @return số dòng bị ảnh hưởng (nên là 1 nếu thành công).
     */
    public int updateCategory(long categoryId, String newName, String newDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME, newName);
        values.put(COLUMN_CAT_DESCRIPTION, newDescription);

        // Cập nhật dòng có ID tương ứng
        return db.update(TABLE_CATEGORIES, values, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(categoryId)});
    }

    /**
     * Xóa một thể loại và TẤT CẢ các từ vựng bên trong nó.
     */
    public void deleteCategory(long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // BƯỚC 1: Xóa tất cả từ vựng có category_id tương ứng.
        // Đây gọi là xóa theo tầng (cascade delete) để đảm bảo dữ liệu sạch.
        db.delete(TABLE_VOCABULARIES, COLUMN_VOCAB_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});

        // BƯỚC 2: Sau khi từ vựng đã được xóa, xóa thể loại đó.
        db.delete(TABLE_CATEGORIES, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    // ----- CÁC PHƯƠNG THỨC MỚI CHO VOCABULARIES -----

    /**
     * Cập nhật thông tin của một từ vựng.
     * @return số dòng bị ảnh hưởng (nên là 1 nếu thành công).
     */
    public int updateVocabulary(long vocabId, String newWord, String newMeaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOCAB_WORD, newWord);
        values.put(COLUMN_VOCAB_MEANING, newMeaning);
        // Bạn có thể thêm các trường khác để cập nhật ở đây

        return db.update(TABLE_VOCABULARIES, values, COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
    }

    /**
     * Xóa một từ vựng duy nhất.
     */
    public void deleteVocabulary(long vocabId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VOCABULARIES, COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    /**
     * Lấy thông tin một từ vựng duy nhất (cần cho chức năng sửa)
     */
    public Cursor getVocabulary(long vocabId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VOCABULARIES, null, COLUMN_VOCAB_ID + "=?", new String[]{String.valueOf(vocabId)}, null, null, null);
    }

    /**
     * Lấy một Map chứa ID và Tên của tất cả thể loại thuộc về người dùng.
     * Dùng Map để dễ dàng lấy ID khi người dùng chọn Tên trong Spinner.
     */
    public Map<String, Long> getCategoriesForSpinner(long userId) {
        Map<String, Long> categoryMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES,
                new String[]{COLUMN_CAT_ID, COLUMN_CAT_NAME},
                COLUMN_CAT_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, COLUMN_CAT_NAME);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CAT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAT_NAME));
                categoryMap.put(name, id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryMap;
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa cho một người dùng.
     * Tìm trong cả cột 'word' và 'meaning'.
     */
    public Cursor searchVocabularies(long userId, String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_VOCAB_USER_ID + " = ? AND (" + COLUMN_VOCAB_WORD + " LIKE ? OR " + COLUMN_VOCAB_MEANING + " LIKE ?)";
        String[] selectionArgs = {String.valueOf(userId), "%" + keyword + "%", "%" + keyword + "%"};

        // Sắp xếp kết quả theo từ để dễ nhìn
        return db.query(TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, COLUMN_VOCAB_WORD);
    }

    // ----- CÁC PHƯƠNG THỨC MỚI CHO CÀI ĐẶT -----

    /**
     * Lưu hoặc cập nhật cài đặt theme cho người dùng.
     */
    public void saveThemeSetting(long userId, String theme) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETTING_USER_ID, userId);
        values.put(COLUMN_SETTING_THEME, theme);
        // "REPLACE" sẽ INSERT nếu chưa có, hoặc UPDATE nếu đã có key
        db.replace(TABLE_USER_SETTINGS, null, values);
        db.close();
    }

    /**
     * Lấy cài đặt theme của người dùng.
     * @return "Light", "Dark", hoặc "System" (mặc định)
     */
    public String getThemeSetting(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_SETTINGS, new String[]{COLUMN_SETTING_THEME},
                COLUMN_SETTING_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            String theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SETTING_THEME));
            cursor.close();
            return theme;
        }
        return "System"; // Trả về mặc định nếu chưa có cài đặt
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
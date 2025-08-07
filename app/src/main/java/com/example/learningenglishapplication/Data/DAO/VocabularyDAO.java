package com.example.learningenglishapplication.Data.DAO;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_VOCAB_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_VOCAB_MEANING;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_VOCAB_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_VOCAB_WORD;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_VERSION;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_CATEGORIES;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_VOCABULARIES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class VocabularyDAO extends SQLiteOpenHelper {

    public VocabularyDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
}

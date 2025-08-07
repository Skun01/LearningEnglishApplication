package com.example.learningenglishapplication.Data.DAO;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_DESCRIPTION;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID;
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

import com.example.learningenglishapplication.Data.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends SQLiteOpenHelper {

    public CategoryDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
}

package com.example.learningenglishapplication.Data.DataHelper;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_CAT_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_CATEGORIES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.learningenglishapplication.Data.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDataHelper {
    private final DatabaseHelper dbHelper;

    public CategoryDataHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean addCategory(String name, String description, long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CAT_NAME, name);
        values.put(DatabaseHelper.COLUMN_CAT_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_CAT_USER_ID, userId);
        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result != -1;
    }
    /**
    * lấy 1 id cụ thể theo category*/
    public Cursor getCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(
                TABLE_CATEGORIES,
                null,
                COLUMN_CAT_ID + "=?",
                new String[]{String.valueOf(categoryId)},
                null,
                null,
                null
        );
    }

    public Cursor getAllCategories(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(TABLE_CATEGORIES, null,
                DatabaseHelper.COLUMN_CAT_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, DatabaseHelper.COLUMN_CAT_NAME);
    }

    public Cursor searchCategories(long userId, String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_CAT_USER_ID + "=? AND " + DatabaseHelper.COLUMN_CAT_NAME + " LIKE ?";
        String[] selectionArgs = {String.valueOf(userId), "%" + query + "%"};
        return db.query(TABLE_CATEGORIES, null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_CAT_NAME);
    }

    public int updateCategory(long categoryId, String newName, String newDescription) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CAT_NAME, newName);
        values.put(DatabaseHelper.COLUMN_CAT_DESCRIPTION, newDescription);
        int result = db.update(TABLE_CATEGORIES, values, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
        return result;
    }

    public void deleteCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_VOCABULARIES, DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.delete(TABLE_CATEGORIES, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    public Map<String, Long> getCategoriesForSpinner(long userId) {
        Map<String, Long> categoryMap = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES,
                new String[]{COLUMN_CAT_ID, DatabaseHelper.COLUMN_CAT_NAME},
                DatabaseHelper.COLUMN_CAT_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, DatabaseHelper.COLUMN_CAT_NAME);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CAT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
                categoryMap.put(name, id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Không đóng database ở đây để tránh lỗi SQLiteConnectionPool.close()
        // db.close();
        return categoryMap;
    }
    
    /**
     * Lấy danh sách ID của tất cả các danh mục thuộc về một người dùng
     * @param userId ID của người dùng
     * @return Danh sách các ID danh mục
     */
    public List<Long> getAllCategoryIds(long userId) {
        List<Long> categoryIds = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES,
                new String[]{COLUMN_CAT_ID},
                DatabaseHelper.COLUMN_CAT_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CAT_ID));
                categoryIds.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Không đóng database ở đây để tránh lỗi SQLiteConnectionPool.close()
        // db.close();
        return categoryIds;
    }
}
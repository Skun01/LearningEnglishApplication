package com.example.learningenglishapplication.Data.DataHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class VocabularyDataHelper {
    private final DatabaseHelper dbHelper;

    public VocabularyDataHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean addVocabulary(long userId, long categoryId, String word, String meaning) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.COLUMN_VOCAB_WORD, word);
        values.put(DatabaseHelper.COLUMN_VOCAB_MEANING, meaning);
        long result = db.insert(DatabaseHelper.TABLE_VOCABULARIES, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getVocabulariesForCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null,
                DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)}, null, null, DatabaseHelper.COLUMN_VOCAB_WORD);
    }

    public List<Vocabulary> getVocabulariesAsList(long categoryId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = getVocabulariesForCategory(categoryId); // Tái sử dụng hàm trên

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
                vocabularyList.add(new Vocabulary(id, word, meaning));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vocabularyList;
    }

    public Cursor getVocabulary(long vocabId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, DatabaseHelper.COLUMN_VOCAB_ID + "=?", new String[]{String.valueOf(vocabId)}, null, null, null);
    }

    public int updateVocabulary(long vocabId, String newWord, String newMeaning) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_WORD, newWord);
        values.put(DatabaseHelper.COLUMN_VOCAB_MEANING, newMeaning);
        int result = db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
        return result;
    }

    public void deleteVocabulary(long vocabId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_VOCABULARIES, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    public Cursor searchVocabularies(long userId, String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_USER_ID + " = ? AND (" + DatabaseHelper.COLUMN_VOCAB_WORD + " LIKE ? OR " + DatabaseHelper.COLUMN_VOCAB_MEANING + " LIKE ?)";
        String[] selectionArgs = {String.valueOf(userId), "%" + keyword + "%", "%" + keyword + "%"};
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_VOCAB_WORD);
    }
}
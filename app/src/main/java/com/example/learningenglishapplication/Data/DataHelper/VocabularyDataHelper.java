package com.example.learningenglishapplication.Data.DataHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VocabularyDataHelper {

    private SQLiteOpenHelper dbHelper;

    public VocabularyDataHelper(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Thêm một từ vựng mới vào database.
     * Trả về ID của từ vựng mới được thêm vào.
     */
    public long addVocabulary(long userId, long categoryId, String word, String pronunciation, String meaning) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.COLUMN_VOCAB_WORD, word);
        values.put(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION, pronunciation);
        values.put(DatabaseHelper.COLUMN_VOCAB_MEANING, meaning);
        values.put(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE, 0);
        values.put(DatabaseHelper.COLUMN_VOCAB_LEARNED, 0);
        values.put(DatabaseHelper.COLUMN_VOCAB_DATE_LEARNED, (String) null);
        values.put(DatabaseHelper.COLUMN_VOCAB_IMAGE_URI, (String) null);
        values.put(DatabaseHelper.COLUMN_VOCAB_AUDIO_URI, (String) null);
        values.put(DatabaseHelper.COLUMN_VOCAB_BOX, 1);
        values.put(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW, 0);

        long newRowId = db.insert(DatabaseHelper.TABLE_VOCABULARIES, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Cập nhật thông tin của một từ vựng.
     * Trả về số dòng bị ảnh hưởng (1 nếu thành công).
     * Hàm này được giữ nguyên để phục vụ các mục đích cập nhật khác không cần check trùng (ví dụ: cập nhật trạng thái yêu thích)
     */
    public int updateVocabulary(long vocabId, String word, String pronunciation, String meaning) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_WORD, word);
        values.put(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION, pronunciation);
        values.put(DatabaseHelper.COLUMN_VOCAB_MEANING, meaning);
        int result = db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
        return result;
    }

    /**
     * Cập nhật thông tin của một từ vựng, bao gồm cả việc kiểm tra trùng lặp.
     * Trả về:
     * 1: Cập nhật thành công.
     * 0: Không có dòng nào được cập nhật.
     * -1: Từ vựng đã tồn tại (lỗi trùng lặp).
     */
    public int updateVocabularyWithCheck(long vocabId, long categoryId, String newWord, String newPronunciation, String newMeaning) {
        // Kiểm tra xem từ vựng mới đã tồn tại trong cùng một thể loại hay chưa, loại trừ từ vựng đang cập nhật
        if (isVocabularyExistsForUpdate(vocabId, categoryId, newWord, newMeaning)) {
            return -1; // Trả về -1 để báo hiệu lỗi trùng lặp
        }

        // Nếu không trùng, tiến hành cập nhật
        return updateVocabulary(vocabId, newWord, newPronunciation, newMeaning);
    }

    /**
     * Xóa một từ vựng duy nhất.
     */
    public void deleteVocabulary(long vocabId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_VOCABULARIES, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    /**
     * Lấy thông tin một từ vựng duy nhất.
     */
    public Vocabulary getVocabularyById(long vocabId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_VOCABULARIES,
                null,
                DatabaseHelper.COLUMN_VOCAB_ID + " = ?",
                new String[]{String.valueOf(vocabId)},
                null, null, null);

        Vocabulary vocabulary = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
            String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
            String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION));
            int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE));
            int learned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_LEARNED));
            String dateLearned = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_DATE_LEARNED));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IMAGE_URI));
            String audioUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_AUDIO_URI));
            int box = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_BOX));
            long nextReview = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW));

            vocabulary = new Vocabulary(id, word, meaning, pronunciation, isFavorite, learned, dateLearned, imageUri, audioUri, box, nextReview);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return vocabulary;
    }

    /**
     * Cập nhật trạng thái yêu thích của một từ vựng.
     */
    public void updateFavoriteStatus(long vocabId, int isFavorite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE, isFavorite);
        db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    /**
     * Đánh dấu một từ vựng đã được học.
     */
    public void markVocabularyAsLearned(long vocabId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_LEARNED, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(new Date());
        values.put(DatabaseHelper.COLUMN_VOCAB_DATE_LEARNED, date);

        db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    /**
     * Đánh dấu một từ vựng là chưa học (hoặc reset trạng thái).
     */
    public void markVocabularyAsUnlearned(long vocabId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_LEARNED, 0);
        values.put(DatabaseHelper.COLUMN_VOCAB_DATE_LEARNED, (String) null);

        db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }

    /**
     * Đếm số từ vựng đã được đánh dấu là yêu thích trong một thể loại.
     */
    public int countFavoriteVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                        " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE + " = 1",
                new String[]{String.valueOf(categoryId)});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Đếm số từ vựng chưa được học trong một thể loại.
     */
    public int countUnlearnedVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                        " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_LEARNED + " = 0",
                new String[]{String.valueOf(categoryId)});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Đếm số từ vựng đã học trong một thể loại.
     */
    public int countLearnedVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                        " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_LEARNED + " = 1",
                new String[]{String.valueOf(categoryId)});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return count;
    }

    // --- CÁC PHƯƠNG THỨC MỚI ĐỂ LẤY DANH SÁCH RIÊNG BIỆT ---

    public Cursor getFavoriteVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE + " = 1";
        String[] selectionArgs = {String.valueOf(categoryId)};
        String orderBy = DatabaseHelper.COLUMN_VOCAB_WORD + " ASC";
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getUnlearnedVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_LEARNED + " = 0";
        String[] selectionArgs = {String.valueOf(categoryId)};
        String orderBy = DatabaseHelper.COLUMN_VOCAB_WORD + " ASC";
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getLearnedVocabularies(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_LEARNED + " = 1";
        String[] selectionArgs = {String.valueOf(categoryId)};
        String orderBy = DatabaseHelper.COLUMN_VOCAB_WORD + " ASC";
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, orderBy);
    }

    /**
     * Lấy danh sách từ vựng của một thể loại dưới dạng Cursor,
     * ưu tiên: Yêu thích > Chưa học > Đã học.
     */
    public Cursor getVocabulariesForCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String orderBy = DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE + " DESC, " +
                DatabaseHelper.COLUMN_VOCAB_LEARNED + " ASC, " +
                DatabaseHelper.COLUMN_VOCAB_WORD + " ASC";
        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)}, null, null, orderBy);
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa trong một thể loại.
     * Kết quả được sắp xếp theo ưu tiên: Yêu thích > Chưa học > Đã học.
     */
    public Cursor searchVocabulariesInCategory(long categoryId, String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String orderBy = DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE + " DESC, " +
                DatabaseHelper.COLUMN_VOCAB_LEARNED + " ASC, " +
                DatabaseHelper.COLUMN_VOCAB_WORD + " ASC";
        return db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                        " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_VOCAB_WORD + " LIKE ? ORDER BY " + orderBy,
                new String[]{String.valueOf(categoryId), "%" + keyword + "%"}
        );
    }

    /**
     * Lấy danh sách từ vựng của một thể loại dưới dạng List<Vocabulary>
     */
    public List<Vocabulary> getVocabulariesAsList(long categoryId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_VOCABULARIES, null, DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION));
                int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IMAGE_URI));
                String audioUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_AUDIO_URI));
                int box = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_BOX));
                long nextReview = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW));

                vocabularyList.add(new Vocabulary(id, word, meaning, pronunciation, isFavorite, 0, null, imageUri, audioUri, box, nextReview));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vocabularyList;
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa cho một người dùng.
     * Tìm trong cả cột 'word' và 'meaning'.
     */
    public Cursor searchVocabularies(long userId, String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_USER_ID + " = ? AND (" + DatabaseHelper.COLUMN_VOCAB_WORD + " LIKE ? OR " + DatabaseHelper.COLUMN_VOCAB_MEANING + " LIKE ?)";
        String[] selectionArgs = {String.valueOf(userId), "%" + keyword + "%", "%" + keyword + "%"};

        return db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_VOCAB_WORD);
    }

    /**
     * Lấy danh sách từ vựng cho chức năng Flashcard, ưu tiên những từ chưa học.
     */
    public List<Vocabulary> getVocabulariesForFlashcard(long categoryId) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long now = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND (" +
                DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW + " IS NULL OR " + DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW + " <= ?) ORDER BY " + DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW + " ASC",
                new String[]{String.valueOf(categoryId), String.valueOf(now)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION));
                int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE));
                int learned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_LEARNED));
                String dateLearned = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_DATE_LEARNED));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IMAGE_URI));
                String audioUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_AUDIO_URI));
                int box = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_BOX));
                long nextReview = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW));

                vocabularies.add(new Vocabulary(id, word, meaning, pronunciation, isFavorite, learned, dateLearned, imageUri, audioUri, box, nextReview));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vocabularies;
    }

    /**
     * Kiểm tra xem một từ vựng đã tồn tại trong một thể loại cụ thể hay chưa.
     *
     * @param categoryId ID của thể loại.
     * @param word       Từ cần kiểm tra.
     * @param meaning    Nghĩa của từ cần kiểm tra.
     * @return true nếu từ đã tồn tại, false nếu chưa.
     */
    public boolean isVocabularyExists(long categoryId, String word, String meaning) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                    " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_VOCAB_WORD + " = ? AND " +
                    DatabaseHelper.COLUMN_VOCAB_MEANING + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId), word, meaning});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                exists = count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    /**
     * Kiểm tra xem một từ vựng đã tồn tại trong một thể loại cụ thể hay chưa,
     * loại trừ từ vựng đang được cập nhật.
     *
     * @param vocabId    ID của từ vựng đang được cập nhật.
     * @param categoryId ID của thể loại.
     * @param word       Từ cần kiểm tra.
     * @param meaning    Nghĩa của từ cần kiểm tra.
     * @return true nếu từ đã tồn tại, false nếu chưa.
     */
    public boolean isVocabularyExistsForUpdate(long vocabId, long categoryId, String word, String meaning) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARIES +
                    " WHERE " + DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_VOCAB_WORD + " = ? AND " +
                    DatabaseHelper.COLUMN_VOCAB_MEANING + " = ? AND " +
                    DatabaseHelper.COLUMN_VOCAB_ID + " != ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId), word, meaning, String.valueOf(vocabId)});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                exists = count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    // --- Spaced repetition helpers ---
    public void updateReviewSchedule(long vocabId, int box, long nextReview) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_VOCAB_BOX, box);
        values.put(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW, nextReview);
        db.update(DatabaseHelper.TABLE_VOCABULARIES, values, DatabaseHelper.COLUMN_VOCAB_ID + " = ?", new String[]{String.valueOf(vocabId)});
        db.close();
    }
    
    /**
     * Lấy danh sách từ vựng yêu thích của một thể loại dưới dạng List<Vocabulary>
     */
    public List<Vocabulary> getFavoriteVocabulariesAsList(long categoryId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_VOCAB_CATEGORY_ID + " = ? AND " + DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE + " = 1";
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_VOCABULARIES, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_PRONUNCIATION));
                int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IMAGE_URI));
                String audioUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_AUDIO_URI));
                int box = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_BOX));
                long nextReview = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_NEXT_REVIEW));

                vocabularyList.add(new Vocabulary(id, word, meaning, pronunciation, isFavorite, 0, null, imageUri, audioUri, box, nextReview));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vocabularyList;
    }
}
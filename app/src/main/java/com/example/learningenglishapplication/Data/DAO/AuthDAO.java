package com.example.learningenglishapplication.Data.DAO;

import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_EMAIL;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_ID;
import static com.example.learningenglishapplication.Data.DatabaseHelper.COLUMN_USER_PASSWORD;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_NAME;
import static com.example.learningenglishapplication.Data.DatabaseHelper.DATABASE_VERSION;
import static com.example.learningenglishapplication.Data.DatabaseHelper.TABLE_USERS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AuthDAO extends SQLiteOpenHelper {

    public AuthDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

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

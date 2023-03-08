package com.sklerbidi.therapistmobileapp.CustomClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ipain.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE agreement_table (id INTEGER PRIMARY KEY AUTOINCREMENT, agreed INTEGER)";
        db.execSQL(createTable);

        String createUserTable = "CREATE TABLE user_table (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS agreement_table");
        db.execSQL("DROP TABLE IF EXISTS user_table");
    }
}





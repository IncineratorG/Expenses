package com.costs.newcosts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * TODO: Add a class header comment
 */

public class DB_Sms extends SQLiteOpenHelper {

    private static final String tag = "DB_Tag";
    private static DB_Sms dbInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sms.db";



    private DB_Sms(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTableNotes = "CREATE TABLE " + TABLE_NOTES + " (" +
//                NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                EXPENSES_ID + " INTEGER, " +
//                NOTE_TEXT + " TEXT, " +
//                SMS_MILLIS + " INTEGER, " +
//                VALUE + " REAL)";
//
//        db.execSQL(createTableNotes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
//        onCreate(db);
    }

}

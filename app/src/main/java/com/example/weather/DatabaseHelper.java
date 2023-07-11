package com.example.weather;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CITIES";
    // Table columns
    public static final String _ID = "_id";
    public static final String CITY = "city";

    // Database Information
    static final String DB_NAME = "CITIES.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CITY + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATE_TABLE);
         try {
         InputStream is = MainActivity.getContext().getResources().getAssets().open(
         "worldCities.sql");

         String[] statements = FileHelper.parseSqlFile(is);

         for (String statement : statements) {
         db.execSQL(statement);
         }
         } catch (Exception ex) {
         ex.printStackTrace();
         }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
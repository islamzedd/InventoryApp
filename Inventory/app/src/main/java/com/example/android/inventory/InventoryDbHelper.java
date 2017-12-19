package com.example.android.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items";
    private static final int DATABASE_VERSION = 1;

    InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DATABASE_TABLE = "CREATE TABLE "+InventoryContract.InventoryEntry.TABLE_NAME+"("+
                InventoryContract.InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                InventoryContract.InventoryEntry.COLUMN_NAME+" TEXT NOT NULL, "+
                InventoryContract.InventoryEntry.COLUMN_PRICE+" DOUBLE NOT NULL, "+
                InventoryContract.InventoryEntry.COLUMN_QUANTITY+" INTEGER NOT NULL DEFAULT 0, "+
                InventoryContract.InventoryEntry.COLUMN_UNIT+" TEXT, "+
                InventoryContract.InventoryEntry.COLUMN_EMAIL+" TEXT, "+
                InventoryContract.InventoryEntry.COLUMN_URI+" TEXT);";
        db.execSQL(SQL_CREATE_DATABASE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+InventoryContract.InventoryEntry.TABLE_NAME);
        onCreate(db);
    }
}

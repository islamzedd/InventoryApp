package com.example.android.inventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InventoryProvider extends ContentProvider {

    //you don't really need to check this it is a really basic content provider nothing special
    //it never caused an error and it wasn't changed win the weird error happened

    private InventoryDbHelper mDbHelper;

    private static UriMatcher mUriMatcher;
    private static final int Items = 10;
    private static final int Item = 1;

    static {
        mUriMatcher = new UriMatcher(9);
        mUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_ITEMS,Items);
        mUriMatcher.addURI(InventoryContract.AUTHORITY,InventoryContract.PATH_ITEMS+"/#",Item);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match){
            case Items:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case Item:
                selection = InventoryContract.InventoryEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can not query uri : "+uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case Items:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case Item:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Canot get type for this uri : "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        switch (match){
            case Items:
                long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return ContentUris.withAppendedId(uri,id);
            default:
                throw new IllegalArgumentException("Can not insert uri :"+uri.toString());
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int numberOfItemsDeleted;
        switch (match){
            case Items:
                numberOfItemsDeleted= database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return numberOfItemsDeleted;
            case Item:
                selection = InventoryContract.InventoryEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numberOfItemsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return numberOfItemsDeleted;
            default:
                throw new IllegalArgumentException("Cannot delete this uri : "+uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int numberOfRowsUpdated;
        switch (match){
            case Items:
                numberOfRowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return numberOfRowsUpdated;
            case Item:
                selection = InventoryContract.InventoryEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numberOfRowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return numberOfRowsUpdated;
            default:
                throw new IllegalArgumentException("Cannot update this uri : "+uri);
        }
    }
}

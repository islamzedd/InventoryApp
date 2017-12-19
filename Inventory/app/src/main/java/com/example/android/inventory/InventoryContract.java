package com.example.android.inventory;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Zidan on 10-Dec-17.
 */

public final class InventoryContract {

    public static final String AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH_ITEMS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+AUTHORITY+"/"+PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+AUTHORITY+"/"+PATH_ITEMS;


        public static final String TABLE_NAME = "items";
        public static final String _ID ="_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_EMAIL ="email";
        public static final String COLUMN_URI = "uri";

    }
}

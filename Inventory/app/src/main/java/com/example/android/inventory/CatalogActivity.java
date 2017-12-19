package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private InventoryCursorAdapter inventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        //create The list view for inventory items
        ListView itemsListView = (ListView) findViewById(R.id.items_list);
        inventoryCursorAdapter = new InventoryCursorAdapter(this);
        itemsListView.setAdapter(inventoryCursorAdapter);
        itemsListView.setEmptyView(findViewById(R.id.empty_view));

        //set a click listener to open DetailsActivity whenver an item is clicked
        //passing in the uri of that item
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this,DetailsActivity.class);
                Uri uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //initialize the cursor loader
        getSupportLoaderManager().initLoader(0,null,this);


        //add a click listener to open details activity to add a new item
        //passes no uri
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab_button);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this,DetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    //inglate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catalog_menu,menu);
        return true;
    }


    //handle options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_items:
                deleteAll();
                return true;
            case R.id.insert_dummy_data:
                insertDummy();
                return true;
            default:
                return false;
        }
    }

    //function to insert Dummy data for testing and debugging purposes
    private void insertDummy(){
        ContentValues values = new ContentValues();
        values.put("name","Milk");
        values.put("price",10.0);
        values.put("quantity",11);
        values.put("unit","bottles");
        values.put("email","provider@company.xyz");
        values.put("uri","");
        Uri uri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI,values);
        if(ContentUris.parseId(uri) == -1){
            Log.v(CatalogActivity.class.getName(),"An error occured and nothing was added");
            Toast.makeText(this,"Insertion failed",Toast.LENGTH_SHORT).show();
        }
    }


    //delete all items in the items table
    private void deleteAll(){
        int numberOfRowsDeleted = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI,null,null);
        if(numberOfRowsDeleted == 0){
            Log.v(CatalogActivity.class.getName(),"An error occured and nothing was delted");
            Toast.makeText(this,"Deletion failed",Toast.LENGTH_SHORT).show();
        }
    }


    //create a cursor loader with a cursor having all the database fields
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_UNIT,
                InventoryContract.InventoryEntry.COLUMN_URI};
        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryCursorAdapter.swapCursor(null);
    }
}
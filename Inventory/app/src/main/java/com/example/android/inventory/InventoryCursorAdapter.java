package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.inventory.R.id.quantity;

class InventoryCursorAdapter extends CursorAdapter {

    InventoryCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //get references for views
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(quantity);
        final CircleImageView image = (CircleImageView) view.findViewById(R.id.image);


        //get the id of the item in the database
        //it is used in the sale_button click listener to update the quantity of this item only
        final String _id = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));


        //set the name and price fields
        nameTextView.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME)));
        priceTextView.setText("$"+cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE)));

        //get the quantity and unit database fields
        final String quantity = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY));
        final String unit = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_UNIT));

        //set the quantity field
        quantityTextView.setText(quantity +" "+unit);

        //get the uri of the image
        final String imageUri = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_URI));

        //if the user set an image display it
        if(!TextUtils.isEmpty(imageUri)){
            image.setImageURI(Uri.parse(imageUri));
        }


        //click listener for the sale button to decrease the quantity by one

        ImageView sale_button = (ImageView) view.findViewById(R.id.sale_button);
        sale_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newQuantity = String.valueOf(Integer.parseInt(quantity)-1);
                if((Integer.parseInt(newQuantity)+1) > 0 ) {
                    quantityTextView.setText(newQuantity + " " + unit);
                    ContentValues values = new ContentValues();
                    values.put("quantity",newQuantity);
                    mContext.getContentResolver().update(InventoryContract.InventoryEntry.CONTENT_URI,
                            values,"_id=?",new String[]{_id});
                }
                else{
                    Toast.makeText(mContext,"You can't have a negative quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

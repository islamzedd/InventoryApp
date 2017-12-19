package com.example.android.inventory;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsActivity extends AppCompatActivity {

    //email validation regex
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;";
    Uri uri;
    private TextInputEditText nameEditText;
    private TextInputEditText priceEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText unitEditText;
    private TextInputEditText emailEditText;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);

    //a boolean whose value is true if all the inputs are valid and the item was inserted otherwise false
    private boolean added = false;

    //boolean set to true if the user interacted with details avtivity
    //used to decide wether to call showBackConfirationDialogue or not
    private boolean itemChanged = false;

    //variables for picking the image
    private final int PICK_IMAGE = 1;
    private String imageUri = "";
    private ImageView detailImage;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            itemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        uri = getIntent().getData();

        //checks if the user is updating or adding an item
        if (uri == null) {
            setTitle("Add item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit item");
        }

        nameEditText = (TextInputEditText) findViewById(R.id.detail_name);
        priceEditText = (TextInputEditText) findViewById(R.id.detail_price);
        quantityEditText = (TextInputEditText) findViewById(R.id.detail_quantity);
        unitEditText = (TextInputEditText) findViewById(R.id.detail_unit);
        emailEditText = (TextInputEditText) findViewById(R.id.detail_email);
        detailImage = (ImageView) findViewById(R.id.detail_image);

        nameEditText.setOnTouchListener(onTouchListener);
        priceEditText.setOnTouchListener(onTouchListener);
        quantityEditText.setOnTouchListener(onTouchListener);
        unitEditText.setOnTouchListener(onTouchListener);
        emailEditText.setOnTouchListener(onTouchListener);

        Button contactButton = (Button) findViewById(R.id.contact_button);
        if (uri == null) {
            contactButton.setVisibility(View.GONE);
        }

        //implicit intent for emailing the provider
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(intent.EXTRA_EMAIL, new String[]{emailEditText.getText().toString()});
                if (intent.resolveActivity(DetailsActivity.this.getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailsActivity.this, "You don't have an e-mail app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //click listener to edit the image of the item
        //if it's clicked an implicit intent is sent to get an image file
        ImageView editImage = (ImageView) findViewById(R.id.edit);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(intent.resolveActivity(DetailsActivity.this.getPackageManager()) != null){
                    startActivityForResult(intent,PICK_IMAGE);
                }
            }
        });

        //method to populate the item's attriputes(name,price,..etc)
        query();
    }


    //ask the user to confirm exiting without saving changes
    @Override
    public void onBackPressed() {
        if(!itemChanged){
            super.onBackPressed();
            return;
        }

        showBackConfirmationDialogue(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }


    //set the image value in the ui and store the uri in imageUri to be saved if the user commits changes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData().toString();
            detailImage.setImageURI(data.getData());
        }
    }

    //method to populate the item's attriputes(name,price,..etc)
    private void query() {
        if (uri == null) {
            return;
        }
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        nameEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME)));
        priceEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE)));
        quantityEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY)));
        unitEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_UNIT)));
        emailEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_EMAIL)));
        imageUri  = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_URI));
        if(!TextUtils.isEmpty(imageUri)){
            detailImage.setImageURI(Uri.parse(imageUri));
        }
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        if (uri == null) {
            MenuItem item = menu.findItem(R.id.delete_item);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                showDeleteConfirmationDialogue();
                return true;
            case R.id.save_item:
                saveItem();
                if (added) {
                    finish();
                }
                return true;
            case android.R.id.home:
                //ask the user to confirm exiting without saving change
                if(!itemChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                showBackConfirmationDialogue(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    }
                });
                return true;
            default:
                return false;
        }
    }

    //deletes item from items table
    private void deleteItem() {
        int numberOfRowsDeleted = getContentResolver().delete(uri, null, null);
        if (numberOfRowsDeleted == 0) {
            Log.v(CatalogActivity.class.getName(), "An error occured and nothing was delted");
            Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    //saves the item into the table after validating the input
    //it also handles the error messages shown to the user
    private void saveItem() {

        String name = nameEditText.getText().toString();
        int quantity;
        double price = 0;
        try {
            price = Double.parseDouble(priceEditText.getText().toString());
        } catch (NumberFormatException e) {
            priceEditText.setError("Price needs to be a number greater than or equal to zero");
        }
        try {
            quantity = Integer.parseInt(quantityEditText.getText().toString());
        } catch (NumberFormatException e) {
            quantity = 0;
        }
        String unit = unitEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Each item must have a name");
        } else {
            if (validateEmail(email)) {
                emailEditText.setError("This email is invalid");
            } else {
                if (uri == null) {
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("price", price);
                    values.put("quantity", quantity);
                    values.put("unit", unit);
                    values.put("email", email);
                    values.put("uri",imageUri);
                    Uri uri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
                    if (ContentUris.parseId(uri) == -1) {
                        Log.v(CatalogActivity.class.getName(), "An error occured and nothing was added");
                        Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Item inserted", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("price", price);
                values.put("quantity", quantity);
                values.put("unit", unit);
                values.put("email", email);
                values.put("uri",imageUri);
                int numberOfRowsUpdated = getContentResolver().update(uri, values, null, null);
                if (numberOfRowsUpdated == 0) {
                    Log.v(CatalogActivity.class.getName(), "An error occured and nothing was upated");
                    Toast.makeText(this, "updating failed", Toast.LENGTH_SHORT).show();
                } else {
                    added = true;
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }


    //function to validate email value
    private boolean validateEmail(String email) {
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //do i really need to explain this?
    private void showDeleteConfirmationDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this item ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //same here
    private  void showBackConfirmationDialogue(DialogInterface.OnClickListener positiveAction){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Changes");
        builder.setPositiveButton("Discard", positiveAction);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

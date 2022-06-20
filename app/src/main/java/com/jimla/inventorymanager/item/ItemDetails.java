package com.jimla.inventorymanager.item;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ItemDetails extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private TextView itemDetailsHeader;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemRfid;
    private Spinner conditionSpinner;
    private Button scanButton;
    private Button photoButton;
    private Button createButton;
    private Button deleteButton;
    private Button editButton;

    private RecyclerView recyclerViewImages;

    private Item item;

    private ArrayList<Image> images;

    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private final ArrayList<String> photoUris = new ArrayList<>();

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        //initDB();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                item = new Item(
                        extras.getInt("itemId"),
                        extras.getString("itemName"),
                        extras.getString("itemDescription"));
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        initRecyclerView();
        setupUI();

        if (item == null) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

/*    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        itemDao = db.itemDao();
        imageDao = db.imageDao();
    }*/

/*    private void loadItem() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                item = itemDao.loadById(itemId);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    private void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case CREATE:
                itemName.setFocusable(true);
                itemName.setFocusableInTouchMode(true);
                itemDescription.setFocusable(true);
                itemDescription.setFocusableInTouchMode(true);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);
                photoButton.setVisibility(View.VISIBLE);
                scanButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                createButton.setVisibility(View.VISIBLE);
                itemDetailsHeader.setText(getString(R.string.item_create));
                break;
            case EDIT:
                itemName.setFocusable(true);
                itemName.setFocusableInTouchMode(true);
                itemDescription.setFocusable(true);
                itemDescription.setFocusableInTouchMode(true);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);
                createButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                photoButton.setVisibility(View.VISIBLE);
                scanButton.setVisibility(View.VISIBLE);
                itemDetailsHeader.setText(getString(R.string.item_edit));
                editButton.setText(getString(R.string.save_button));
                break;
            case VIEW:
                itemName.setFocusable(false);
                itemName.setFocusableInTouchMode(false);
                itemDescription.setFocusable(false);
                itemDescription.setFocusableInTouchMode(false);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);
                createButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                photoButton.setVisibility(View.INVISIBLE);
                scanButton.setVisibility(View.INVISIBLE);
                itemDetailsHeader.setText(getString(R.string.item_view));
                editButton.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        itemDetailsHeader = findViewById(R.id.tvItemDetailsHeader);
        itemName = findViewById(R.id.etItemName);
        itemDescription = findViewById(R.id.etDescription);
        itemRfid = findViewById(R.id.etRfid);
        conditionSpinner = findViewById(R.id.spCondition);
        photoButton = findViewById(R.id.photoButton);
        scanButton = findViewById(R.id.scanButton);
        createButton = findViewById(R.id.createButton2);
        editButton = findViewById(R.id.editButton2);
        deleteButton = findViewById(R.id.deleteButton2);
/*
        Calendar calendar = Calendar.getInstance();

        if (item != null) {
            calendar.setTimeInMillis(item.birthdate);
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.start_date_format));
                Date date = dateFormat.parse(getString(R.string.start_date));
                calendar.setTimeInMillis(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        itemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    itemName.clearFocus();
                }
                return false;
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                int dummyRfid = r.nextInt(1000) + 1;
                StringBuilder str = new StringBuilder(dummyRfid + "");
                while (str.length() < 16)
                    str.insert(0, "0");
                itemRfid.setText(str);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();

/*                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);*/
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mode) {
                    case EDIT:
                        updateItem();
                        setMode(Mode.VIEW);
                        break;
                    case VIEW:
                        setMode(Mode.EDIT);
                        break;
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {

                        finish();
                    }
                }).start();
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.pref_condition));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(dataAdapter);
        conditionSpinner.setSelection(0, false);
    }

    private void initRecyclerView() {
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImages.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setAdapter();
    }

    private void setAdapter() {
        ImageAdapter.OnItemClickListener listener = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //ArrayList<Bitmap> images = new ArrayList<>();

                if (mode == Mode.CREATE) {
                    for (String photo : photoUris) {
                        //images.add(getImageFromStorage(photo));
                    }
                } else {
/*                    List<ImageEntry> imageEntries = imageDao.loadByItemId(itemId);
                    int listIndex = 0;
                    for (ImageEntry e : imageEntries) {
                        items.put(listIndex++, e.id);
                        images.add(getImageFromStorage(e.photo));
                    }*/
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewImages.setAdapter(new ImageAdapter(images, listener));
                    }
                });
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ItemDetails.this, ImageDetails.class);
        //intent.putExtra("photoId", items.get(position));

        startActivity(intent);
    }

    private void createItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {

/*                int id = writeItem();
                if (id > 0) {
                    for (String photo : photoUris)
                        writeImage(id, photo);
                }*/

                finish();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

/*    private int writeItem() {
        List<ItemEntry> users = itemDao.getAll();

        int highestId = 0;
        for (ItemEntry c : users) {
            if (c.id > highestId)
                highestId = c.id;
        }
        highestId = highestId + 1;

        ItemEntry itemEntry = new ItemEntry(highestId, roomId, itemName.getText().toString(), itemRfid.getText().toString(), itemDescription.getText().toString(), System.currentTimeMillis());

        try {
            itemDao.insert(itemEntry);
            return highestId;
        } catch (Exception e) {
            Log.e("error", "Duplicate key");
        }
        return -1;
    }*/

/*    private void writeImage(int id, String photo) {
        List<ImageEntry> images = imageDao.getAll();

        int highestId = 0;
        for (ImageEntry c : images) {
            if (c.id > highestId)
                highestId = c.id;
        }
        highestId = highestId + 1;

        ImageEntry imageEntry = new ImageEntry(highestId, id, itemDescription.getText().toString(), photo, System.currentTimeMillis());

        try {
            imageDao.insert(imageEntry);
        } catch (Exception e) {
            Log.e("error", "Duplicate key");
        }

    }*/

    private void updateItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateFieldsFromItem() {
        if (item != null) {
            itemName.setText(item.itemName);
            itemRfid.setText(item.itemEpc);
            itemDescription.setText(item.itemDescription);
        }
    }

/*    private void takePicture_OLD() {
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (mode == Mode.CREATE)
                photoUris.add(currentPhotoPath);
            else if (mode == Mode.EDIT) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        //writeImage(itemId, currentPhotoPath);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setAdapter();
        }
    }

    private Bitmap getImageFromStorage(String path) {
        Uri imageUri = Uri.parse(path);
        Bitmap bitmap = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
            bitmap = ImageDecoder.decodeBitmap(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("error", "Error creating file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.jimla.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file://" + image.getAbsolutePath();
        return image;
    }
}
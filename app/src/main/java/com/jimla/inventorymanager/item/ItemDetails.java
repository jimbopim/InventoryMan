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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.common.BaseActivity;
import com.jimla.inventorymanager.site.NurHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemDetails extends BaseActivity implements ImageAdapter.OnItemClickListener {

    private TextView tvItemDetailsHeader;
    private TextView tvItemName;
    private TextView tvItemDescription;
    private TextView tvItemRfid;
    private Spinner spCondition;
    private Spinner spType;
    private Button btnScan;
    private Button btnPhoto;
    private Button btnCreate;
    private Button btnDelete;
    private Button btnEdit;

    private RecyclerView recyclerViewImages;

    private Item currentItem;

    private ArrayList<Image> images;

    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private final ArrayList<String> photoUris = new ArrayList<>();

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW, SCAN
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentItem = new Item(
                        extras.getInt("itemId"),
                        extras.getString("itemName"),
                        extras.getString("itemDescription"));
            }
        } else {
            currentItem = new Item(
                    savedInstanceState.getInt("itemId"),
                    savedInstanceState.getString("itemName"),
                    savedInstanceState.getString("itemDescription"));
        }

        initRecyclerView();
        setupUI();

        if (currentItem == null) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Bundle extras = data.getExtras();
                            tvItemRfid.setText(extras.getString("tagEpc"));
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("itemId", currentItem.itemId);
        savedInstanceState.putString("itemName", currentItem.itemName);
        savedInstanceState.putString("itemDescription", currentItem.itemDescription);
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
/*                itemName.setFocusable(true);
                itemName.setFocusableInTouchMode(true);
                itemDescription.setFocusable(true);
                itemDescription.setFocusableInTouchMode(true);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);*/
                btnPhoto.setVisibility(View.VISIBLE);
                btnScan.setVisibility(View.VISIBLE);
                btnScan.setText("Read Tag");
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnCreate.setVisibility(View.VISIBLE);
                tvItemDetailsHeader.setText(getString(R.string.item_create));
                tvItemName.setEnabled(true);
                tvItemDescription.setEnabled(true);
                break;
            case EDIT:
/*                itemName.setFocusable(true);
                itemName.setFocusableInTouchMode(true);
                itemDescription.setFocusable(true);
                itemDescription.setFocusableInTouchMode(true);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);*/
                btnPhoto.setVisibility(View.VISIBLE);
                btnScan.setVisibility(View.VISIBLE);
                btnScan.setText("Start Scan Tag");
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setText(getString(R.string.save_button));
                btnCreate.setVisibility(View.INVISIBLE);
                tvItemDetailsHeader.setText(getString(R.string.item_edit));
                tvItemName.setEnabled(true);
                tvItemDescription.setEnabled(true);
                break;
            case VIEW:
/*                itemName.setFocusable(false);
                itemName.setFocusableInTouchMode(false);
                itemDescription.setFocusable(false);
                itemDescription.setFocusableInTouchMode(false);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);*/
                btnPhoto.setVisibility(View.INVISIBLE);
                btnScan.setVisibility(View.INVISIBLE);
                btnScan.setText("");
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setText(getString(R.string.edit_button));
                btnCreate.setVisibility(View.INVISIBLE);
                tvItemDetailsHeader.setText(getString(R.string.item_view));
                tvItemName.setEnabled(false);
                tvItemDescription.setEnabled(false);
                break;
            case SCAN:
/*                itemName.setFocusable(false);
                itemName.setFocusableInTouchMode(false);
                itemDescription.setFocusable(false);
                itemDescription.setFocusableInTouchMode(false);
                itemRfid.setFocusable(false);
                itemRfid.setFocusableInTouchMode(false);*/
                btnPhoto.setVisibility(View.INVISIBLE);
                btnScan.setVisibility(View.VISIBLE);
                btnScan.setText("Scan Tag");
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setText("Return");
                btnCreate.setVisibility(View.INVISIBLE);
                tvItemDetailsHeader.setText("Scan Tag");
                tvItemName.setEnabled(false);
                tvItemDescription.setEnabled(false);
                break;
        }
    }

    private void setupUI() {
        tvItemDetailsHeader = findViewById(R.id.tvItemDetailsHeader);
        tvItemName = findViewById(R.id.etItemName);
        tvItemDescription = findViewById(R.id.etDescription);
        tvItemRfid = findViewById(R.id.etRfid);
        spType = findViewById(R.id.spItemType);
        spCondition = findViewById(R.id.spCondition);
        btnPhoto = findViewById(R.id.photoButton);
        btnScan = findViewById(R.id.scanButton);
        btnCreate = findViewById(R.id.createButton2);
        btnEdit = findViewById(R.id.editButton2);
        btnDelete = findViewById(R.id.deleteButton2);
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

        tvItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvItemName.clearFocus();
                }
                return false;
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Intent intent = new Intent(ItemDetails.this, Nur.class);

                someActivityResultLauncher.launch(intent);*/

                switch (mode) {
                    case EDIT:
                        setMode(Mode.SCAN);
                        break;
                    case CREATE:
                        tvItemRfid.setText(NurHandler.getInstance().getNearestTagEpc());
                        break;
                    case SCAN:
                        tvItemRfid.setText(NurHandler.getInstance().getNearestTagEpc());
                        setMode(Mode.EDIT);
                        break;
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();

/*                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);*/
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
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
                    case SCAN:
                        setMode(Mode.EDIT);
                        break;
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {

                        finish();
                    }
                }).start();
            }
        });

        ArrayAdapter<String> conditionSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.pref_condition));
        conditionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCondition.setAdapter(conditionSpinnerAdapter);
        spCondition.setSelection(0, false);

        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.pref_type));
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeSpinnerAdapter);
        spType.setSelection(0, false);
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
        if (currentItem != null) {
            tvItemName.setText(currentItem.itemName);
            tvItemRfid.setText(currentItem.itemEpc);
            tvItemDescription.setText(currentItem.itemDescription);
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
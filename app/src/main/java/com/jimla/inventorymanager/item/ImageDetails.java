package com.jimla.inventorymanager.item;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.AppDatabase;
import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.project.ProjectEntry;
import com.jimla.inventorymanager.room.RoomActivity;

import java.util.List;

public class ImageDetails extends AppCompatActivity {

    private TextView description;
    private Button deleteButton;
    private ImageView imageView;

    private ImageDao imageDao;
    private ImageEntry item;

    private int photoId = 0;
    private Mode mode;

    boolean set = false;

    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        initDB();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                photoId = extras.getInt("photoId");
                loadItem();
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        setupUI();

        if (photoId == 0) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        imageDao = db.imageDao();
    }

    private void loadItem() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                item = imageDao.loadById(photoId);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case VIEW:
                description.setFocusable(false);
                description.setFocusableInTouchMode(false);
                deleteButton.setVisibility(View.VISIBLE);
                break;
            default:
                description.setFocusable(true);
                description.setFocusableInTouchMode(true);
                deleteButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupUI() {
        imageView = findViewById(R.id.imageView);
        description = findViewById(R.id.tvDescription);
        deleteButton = findViewById(R.id.btnDelete);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        imageDao.delete(photoId);
                        finish();
                    }
                }).start();
            }
        });
    }

    private void updateItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ImageEntry itemEntry = imageDao.loadById(photoId);
                itemEntry.description = description.getText().toString();
                item = itemEntry;
                imageDao.update(itemEntry);

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

    private void updateFieldsFromItem() {
        if (item != null) {
            imageView.setImageBitmap(getImageFromStorage(item.photo));

            description.setText(item.description);
        }
    }
}
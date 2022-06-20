package com.jimla.inventorymanager.item;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;

public class ImageDetails extends AppCompatActivity {

    private TextView description;
    private Button deleteButton;
    private ImageView imageView;

    private int photoId = 0;

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                photoId = extras.getInt("photoId");
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

                        finish();
                    }
                }).start();
            }
        });
    }

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
/*        if (item != null) {
            imageView.setImageBitmap(getImageFromStorage(item.photo));

            description.setText(item.description);
        }*/
    }
}
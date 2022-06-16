package com.jimla.inventorymanager.item;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.AppDatabase;
import com.jimla.inventorymanager.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ItemDetails extends AppCompatActivity implements ImagesAdapter.OnItemClickListener  {

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
    private final HashMap<Integer, Integer> items = new HashMap<>();

    private ItemDao itemDao;
    private ItemEntry item;

    private ImageDao imageDao;

    private static final int CAMERA_REQUEST = 1888;
    //private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private int itemId = 0;
    private int projectId = 0;
    private int roomId = 0;

    ArrayList<String> photoBase64 = new ArrayList<>();

    private Mode mode;

    boolean set = false;

    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        initDB();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                itemId = extras.getInt("itemId");
                roomId = extras.getInt("roomId");
                loadItem();
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        initRecyclerView();
        setupUI();

        if (itemId == 0) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        itemDao = db.itemDao();
        imageDao = db.imageDao();
    }

    private void loadItem() {
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
    }

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
        //imageView = findViewById(R.id.imageView);
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
                while(str.length() < 16)
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
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra("item", items.get(position));

                startActivityForResult(intent, CAMERA_REQUEST);

/*                int permission = checkSelfPermission(Manifest.permission.CAMERA);
                //test
                permission = PackageManager.PERMISSION_GRANTED;
                if (permission != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }*/
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
                //moveText();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        itemDao.delete(itemId);
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
        ImagesAdapter.ViewHolder.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Bitmap> images = new ArrayList<>();

                if(mode == Mode.CREATE) {
                    for(String photo : photoBase64) {
                        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        images.add(decodedByte);
                    }
                }
                else {
                    List<ImageEntry> imageEntries = imageDao.loadByItemId(itemId);

                    for (ImageEntry e : imageEntries) {
                        byte[] decodedString = Base64.decode(e.photo, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        images.add(decodedByte);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewImages.setAdapter(new ImagesAdapter(images));
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
/*
        Intent intent = new Intent(ItemDetails.this, dsad.class);
        intent.putExtra("itemId", items.get(position));

        startActivity(intent);*/
    }

    private void createItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {

                int id = writeItem();
                if(id > 0) {
                    for(String photo : photoBase64)
                        writeImage(id, photo);
                }

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

    private int writeItem() {
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
    }

    private void writeImage(int id, String photo) {
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

    }

    private void updateItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ItemEntry itemEntry = itemDao.loadById(itemId);
                itemEntry.name = itemName.getText().toString();
                itemEntry.rfid = itemRfid.getText().toString();
                itemEntry.description = itemDescription.getText().toString();
/*                if(photoBase64 != null)
                    itemEntry.photo = photoBase64;*/

                item = itemEntry;
                itemDao.update(itemEntry);

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
            itemName.setText(item.name);
            itemRfid.setText(item.rfid);
            itemDescription.setText(item.description);

/*            if(item.photo != null) {
                byte[] decodedString = Base64.decode(item.photo, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //imageView.setImageBitmap(decodedByte);
            }*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream .toByteArray();

            String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            if(mode == Mode.CREATE)
                photoBase64.add(base64);
            else if(mode == Mode.EDIT) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        writeImage(itemId, base64);
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
}
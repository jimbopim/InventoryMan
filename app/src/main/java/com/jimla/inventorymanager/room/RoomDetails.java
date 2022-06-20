package com.jimla.inventorymanager.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.item.ItemActivity;

public class RoomDetails extends AppCompatActivity {

    private TextView roomDetailsHeader;
    private TextView tvRoomName;
    private NumberPicker floorPicker;
    private TextView tvDescription;
    private Button openRoomsButton;
    private Button createButton;
    private Button deleteButton;
    private Button editButton;

    private Room room;

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                room = new Room(
                        extras.getInt("roomId"),
                        extras.getString("roomName"),
                        extras.getString("roomDescription"));
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        setupUI();

        if (room == null) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    private void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case CREATE:
                tvRoomName.setFocusable(true);
                tvRoomName.setFocusableInTouchMode(true);
                tvDescription.setFocusable(true);
                tvDescription.setFocusableInTouchMode(true);
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                createButton.setVisibility(View.VISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                floorPicker.setFocusable(true);
                floorPicker.setFocusableInTouchMode(true);
                floorPicker.setEnabled(true);
                roomDetailsHeader.setText(getString(R.string.room_create));
                break;
            case EDIT:
                tvRoomName.setFocusable(true);
                tvRoomName.setFocusableInTouchMode(true);
                tvDescription.setFocusable(true);
                tvDescription.setFocusableInTouchMode(true);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                floorPicker.setFocusable(true);
                floorPicker.setFocusableInTouchMode(true);
                roomDetailsHeader.setText(getString(R.string.room_edit));
                floorPicker.setEnabled(true);
                editButton.setText(getString(R.string.save_button));
                break;
            case VIEW:
                tvRoomName.setFocusable(false);
                tvRoomName.setFocusableInTouchMode(false);
                tvDescription.setFocusable(false);
                tvDescription.setFocusableInTouchMode(false);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                roomDetailsHeader.setText(getString(R.string.room_view));
                floorPicker.setFocusable(false);
                floorPicker.setFocusableInTouchMode(false);
                floorPicker.setEnabled(false);
                editButton.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        roomDetailsHeader = findViewById(R.id.tvRoomDetailsHeader);
        tvRoomName = findViewById(R.id.etRoomName);
        floorPicker = findViewById(R.id.floorPicker);
        tvDescription = findViewById(R.id.etDescription);
        openRoomsButton = findViewById(R.id.openItemsButton);
        createButton = findViewById(R.id.createButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        tvRoomName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvRoomName.clearFocus();
                }
                return false;
            }
        });

        openRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomDetails.this, ItemActivity.class);
                intent.putExtra("roomId", room.roomId);

                startActivity(intent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
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

        int max = 60;
        String[] choices = new String[max];
        choices[10] = "Not selected";

        for (int i = -10; i < max - 10; i++) {
            if (i < 0)
                choices[i + 10] = "Basement floor " + Math.abs(i);
            else if (i == 0)
                choices[i + 10] = "Not selected";
            else if (i == 1)
                choices[i + 10] = "Ground floor";
            else
                choices[i + 10] = "Floor " + (i - 1);
        }
        floorPicker.setMinValue(0);
        floorPicker.setMaxValue(max - 1);
        floorPicker.setDisplayedValues(choices);
        floorPicker.setValue(10);
        floorPicker.setWrapSelectorWheel(false);
    }

    private void createItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {

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
        if (room != null) {
            tvRoomName.setText(room.roomName);
            tvDescription.setText(room.roomDescription);
            //floorPicker.setValue(item.floor);
        }
    }
}
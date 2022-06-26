package com.jimla.inventorymanager.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.common.BaseActivity;
import com.jimla.inventorymanager.item.ItemActivity;

public class RoomDetails extends BaseActivity {

    private TextView tvRoomName;
    private NumberPicker pkrFloor;
    private TextView tvDescription;
    private Button btnOpenRooms;
    private Button btnCreate;
    private Button btnDelete;
    private Button btnEdit;

    private Room currentRoom;

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_room_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentRoom = new Room(
                        extras.getInt("roomId"),
                        extras.getString("roomName"),
                        extras.getString("roomDescription"));
            }
        } else {
            currentRoom = new Room(
                    savedInstanceState.getInt("roomId"),
                    savedInstanceState.getString("roomName"),
                    savedInstanceState.getString("roomDescription"));
        }

        setupUI();

        if (currentRoom == null) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_room_details;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("roomId", currentRoom.roomId);
        savedInstanceState.putString("roomName", currentRoom.roomName);
        savedInstanceState.putString("roomDescription", currentRoom.roomDescription);
    }

    private void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case CREATE:
                tvRoomName.setFocusable(true);
                tvRoomName.setFocusableInTouchMode(true);
                tvDescription.setFocusable(true);
                tvDescription.setFocusableInTouchMode(true);
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnCreate.setVisibility(View.VISIBLE);
                btnOpenRooms.setVisibility(View.INVISIBLE);
                pkrFloor.setFocusable(true);
                pkrFloor.setFocusableInTouchMode(true);
                pkrFloor.setEnabled(true);
                setHeader1Text(getString(R.string.room_create));
                break;
            case EDIT:
                tvRoomName.setFocusable(true);
                tvRoomName.setFocusableInTouchMode(true);
                tvDescription.setFocusable(true);
                tvDescription.setFocusableInTouchMode(true);
                btnCreate.setVisibility(View.INVISIBLE);
                btnOpenRooms.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                pkrFloor.setFocusable(true);
                pkrFloor.setFocusableInTouchMode(true);
                setHeader1Text(getString(R.string.room_edit));
                pkrFloor.setEnabled(true);
                btnEdit.setText(getString(R.string.save_button));
                break;
            case VIEW:
                tvRoomName.setFocusable(false);
                tvRoomName.setFocusableInTouchMode(false);
                tvDescription.setFocusable(false);
                tvDescription.setFocusableInTouchMode(false);
                btnCreate.setVisibility(View.INVISIBLE);
                btnOpenRooms.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                setHeader1Text(getString(R.string.room_view));
                pkrFloor.setFocusable(false);
                pkrFloor.setFocusableInTouchMode(false);
                pkrFloor.setEnabled(false);
                btnEdit.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        tvRoomName = findViewById(R.id.etRoomName);
        pkrFloor = findViewById(R.id.floorPicker);
        tvDescription = findViewById(R.id.etDescription);
        btnOpenRooms = findViewById(R.id.openItemsButton);
        btnCreate = findViewById(R.id.createButton);
        btnEdit = findViewById(R.id.editButton);
        btnDelete = findViewById(R.id.deleteButton);

        tvRoomName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvRoomName.clearFocus();
                }
                return false;
            }
        });

        btnOpenRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomDetails.this, ItemActivity.class);
                intent.putExtra("roomId", currentRoom.roomId);

                startActivity(intent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
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
        pkrFloor.setMinValue(0);
        pkrFloor.setMaxValue(max - 1);
        pkrFloor.setDisplayedValues(choices);
        pkrFloor.setValue(10);
        pkrFloor.setWrapSelectorWheel(false);
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
        if (currentRoom != null) {
            tvRoomName.setText(currentRoom.roomName);
            tvDescription.setText(currentRoom.roomDescription);
            //floorPicker.setValue(item.floor);
        }
    }
}
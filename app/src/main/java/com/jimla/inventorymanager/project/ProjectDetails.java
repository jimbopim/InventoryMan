package com.jimla.inventorymanager.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.room.RoomActivity;

public class ProjectDetails extends AppCompatActivity {

    private TextView projectDetailsHeader;
    private TextView projectName;
    private TextView description;
    private Button openRoomsButton;
    private Button createButton;
    private Button deleteButton;
    private Button editButton;

    private Site site;

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                site = new Site(
                        extras.getInt("siteId"),
                        extras.getInt("siteType"),
                        extras.getString("siteName"),
                        extras.getString("siteDescription"),
                        extras.getInt("siteStartDate"));
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        setupUI();

        if (site == null) {
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
                projectName.setFocusable(true);
                projectName.setFocusableInTouchMode(true);
                description.setFocusable(true);
                description.setFocusableInTouchMode(true);
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                createButton.setVisibility(View.VISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                projectDetailsHeader.setText(getString(R.string.project_create));
                break;
            case EDIT:
                projectName.setFocusable(true);
                projectName.setFocusableInTouchMode(true);
                description.setFocusable(true);
                description.setFocusableInTouchMode(true);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                projectDetailsHeader.setText(getString(R.string.project_edit));
                editButton.setText(getString(R.string.save_button));
                break;
            case VIEW:
                projectName.setFocusable(false);
                projectName.setFocusableInTouchMode(false);
                description.setFocusable(false);
                description.setFocusableInTouchMode(false);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                projectDetailsHeader.setText(getString(R.string.project_view));
                editButton.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        projectDetailsHeader = findViewById(R.id.tvProjectDetailsHeader);
        projectName = findViewById(R.id.etProjectName);
        description = findViewById(R.id.etDescription);
        openRoomsButton = findViewById(R.id.openItemsButton);
        createButton = findViewById(R.id.createButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        projectName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    projectName.clearFocus();
                }
                return false;
            }
        });

        openRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectDetails.this, RoomActivity.class);
                intent.putExtra("siteId", site.siteId);

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
        if (site != null) {
            projectName.setText(site.siteName);
            description.setText(site.description);
        }
    }
}
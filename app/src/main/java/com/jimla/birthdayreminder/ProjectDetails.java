package com.jimla.birthdayreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProjectDetails extends AppCompatActivity {

    private TextView projectName;
    private TextView description;
    private Button openRoomsButton;
    private Button createButton;
    private Button deleteButton;
    private Button editButton;

    private ProjectDao projectDao;
    private ProjectEntry item;

    private int projectId = 0;
    private Mode mode;

    boolean set = false;

    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        initDB();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                projectId = extras.getInt("projectId");
                loadItem();
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        setupUI();

        if (projectId == 0) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        projectDao = db.projectDao();
    }

    private void loadItem() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                item = projectDao.loadById(projectId);
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
                projectName.setFocusable(true);
                projectName.setFocusableInTouchMode(true);
                description.setFocusable(true);
                description.setFocusableInTouchMode(true);
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                createButton.setVisibility(View.VISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                //createButton.setEnabled(true);
                break;
            case EDIT:
                projectName.setFocusable(true);
                projectName.setFocusableInTouchMode(true);
                description.setFocusable(true);
                description.setFocusableInTouchMode(true);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.INVISIBLE);
                //createButton.setEnabled(false);
                editButton.setText(getString(R.string.save_button));
                break;
            case VIEW:
                projectName.setFocusable(false);
                projectName.setFocusableInTouchMode(false);
                description.setFocusable(false);
                description.setFocusableInTouchMode(false);
                createButton.setVisibility(View.INVISIBLE);
                openRoomsButton.setVisibility(View.VISIBLE);
                //createButton.setEnabled(false);
                editButton.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        projectName = findViewById(R.id.etProjectName);
        description = findViewById(R.id.etDescription);
        openRoomsButton = findViewById(R.id.openInventoryButton);
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
                intent.putExtra("projectId", projectId);

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
                //moveText();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        projectDao.delete(projectId);
                        finish();
                    }
                }).start();
            }
        });
    }

    private void createItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {

                List<ProjectEntry> users = projectDao.getAll();

                int highestId = 0;
                for (ProjectEntry c : users) {
                    if (c.id > highestId)
                        highestId = c.id;
                }
                highestId = highestId + 1;

                ProjectEntry projectEntry = new ProjectEntry(highestId, projectName.getText().toString(), description.getText().toString(), System.currentTimeMillis());
                try {
                    projectDao.insert(projectEntry);
                } catch (Exception e) {
                    Log.e("error", "Duplicate key");
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

    private void updateItem() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ProjectEntry itemEntry = projectDao.loadById(projectId);
                itemEntry.name = projectName.getText().toString();
                itemEntry.description = description.getText().toString();
                item = itemEntry;
                projectDao.update(itemEntry);

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
            projectName.setText(item.name);
            description.setText(item.description);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("debug", "onResume/ProjectDetails");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
        Log.e("debug", "onStop/ProjectDetails");
    }
}
package com.jimla.inventorymanager.site;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.common.BaseActivity;
import com.jimla.inventorymanager.room.RoomActivity;

public class SiteDetails extends BaseActivity {

    private TextView tvSiteName;
    private TextView tvSiteDescription;
    private Button btnOpenRooms;
    private Button btnCreate;
    private Button btnDelete;
    private Button btnEdit;

    private Site currentSite;

    private Mode mode;
    enum Mode {
        CREATE, EDIT, VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_site_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentSite = new Site(
                        extras.getInt("siteId"),
                        extras.getInt("siteType"),
                        extras.getString("siteName"),
                        extras.getString("siteDescription"),
                        extras.getInt("siteStartDate"));
            }
        } else {
            currentSite = new Site(
                    savedInstanceState.getInt("siteId"),
                    savedInstanceState.getInt("siteType"),
                    savedInstanceState.getString("siteName"),
                    savedInstanceState.getString("siteDescription"),
                    savedInstanceState.getInt("siteStartDate"));
        }

        setupUI();

        if (currentSite == null) {
            setMode(Mode.CREATE);
        } else {
            setMode(Mode.VIEW);
            updateFieldsFromItem();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_site_details;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("siteId", currentSite.siteId);
        savedInstanceState.putInt("siteType", currentSite.siteType);
        savedInstanceState.putString("siteName", currentSite.siteName);
        savedInstanceState.putString("siteDescription", currentSite.description);
        savedInstanceState.putInt("siteStartDate", currentSite.startDate);

    }

    private void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case CREATE:
                tvSiteName.setFocusable(true);
                tvSiteName.setFocusableInTouchMode(true);
                tvSiteDescription.setFocusable(true);
                tvSiteDescription.setFocusableInTouchMode(true);
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnCreate.setVisibility(View.VISIBLE);
                btnOpenRooms.setVisibility(View.INVISIBLE);
                setHeader1Text(getString(R.string.site_create));
                break;
            case EDIT:
                tvSiteName.setFocusable(true);
                tvSiteName.setFocusableInTouchMode(true);
                tvSiteDescription.setFocusable(true);
                tvSiteDescription.setFocusableInTouchMode(true);
                btnCreate.setVisibility(View.INVISIBLE);
                btnOpenRooms.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                setHeader1Text(getString(R.string.site_edit));
                btnEdit.setText(getString(R.string.save_button));
                break;
            case VIEW:
                tvSiteName.setFocusable(false);
                tvSiteName.setFocusableInTouchMode(false);
                tvSiteDescription.setFocusable(false);
                tvSiteDescription.setFocusableInTouchMode(false);
                btnCreate.setVisibility(View.INVISIBLE);
                btnOpenRooms.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                setHeader1Text(getString(R.string.site_view));
                btnEdit.setText(getString(R.string.edit_button));
                break;
        }
    }

    private void setupUI() {
        tvSiteName = findViewById(R.id.etSiteName);
        tvSiteDescription = findViewById(R.id.etDescription);
        btnOpenRooms = findViewById(R.id.openItemsButton);
        btnCreate = findViewById(R.id.createButton);
        btnEdit = findViewById(R.id.editButton);
        btnDelete = findViewById(R.id.deleteButton);

        tvSiteName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvSiteName.clearFocus();
                }
                return false;
            }
        });

        btnOpenRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SiteDetails.this, RoomActivity.class);
                intent.putExtra("siteId", currentSite.siteId);

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
        if (currentSite != null) {
            tvSiteName.setText(currentSite.siteName);
            tvSiteDescription.setText(currentSite.description);
        }
    }
}
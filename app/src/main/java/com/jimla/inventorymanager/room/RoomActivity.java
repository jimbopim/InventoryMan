package com.jimla.inventorymanager.room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.AppDatabase;
import com.jimla.inventorymanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomActivity extends AppCompatActivity implements RoomAdapter.OnItemClickListener {

    private RoomDao roomDao;
    private RecyclerView recyclerView;

    private final HashMap<Integer, Integer> rooms = new HashMap<>();

    private int projectId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                projectId = extras.getInt("projectId");
            }
        } else {
            //contact = (Contact) savedInstanceState.getSerializable("CONTACT");
        }

        initDB();
        initRecyclerView();
        initUI();
    }

    private void initDB() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getApplicationContext(), getString(R.string.db_name));

        roomDao = db.roomDao();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        RoomAdapter.ViewHolder.setOnItemClickListener(this);
    }

    private void initUI() {
        Button buttonAdd = findViewById(R.id.button_add_new3);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, RoomDetails.class);
                intent.putExtra("projectId", projectId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(RoomActivity.this, RoomDetails.class);
        intent.putExtra("roomId", rooms.get(position));
        //intent.putExtra("projectId", projectId);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("debug", "onResume/RoomActivity");
        setAdapter();
    }

    private void setAdapter() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<RoomEntry> entries = roomDao.loadByProjectId(projectId);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> extra = new ArrayList<>();
                int listIndex = 0;
                for (RoomEntry e : entries) {
                    rooms.put(listIndex++, e.id);
                    names.add(e.name);
                    extra.add(e.description);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new RoomAdapter(names, extra));
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
}
package com.jimla.birthdayreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemClickListener {

    private ItemDao itemDao;
    private RecyclerView recyclerView;

    private final HashMap<Integer, Integer> items = new HashMap<>();

    private int roomId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                roomId = extras.getInt("roomId");
                //loadItem();
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

        itemDao = db.itemDao();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        InventoryAdapter.ViewHolder.setOnItemClickListener(this);
    }

    private void initUI() {
        Button buttonAdd = findViewById(R.id.button_add_new2);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, ItemDetails.class);
                intent.putExtra("roomId", roomId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(InventoryActivity.this, ItemDetails.class);
        intent.putExtra("itemId", items.get(position));

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("debug", "onResume/InventoryActivity");
        setAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
        Log.e("debug", "onStop/InventoryActivity");
    }

    private void setAdapter() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ItemEntry> entries = itemDao.loadByRoomId(roomId);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> rfid = new ArrayList<>();
                int listIndex = 0;
                for (ItemEntry e : entries) {
                    items.put(listIndex++, e.id);
                    names.add(e.name);
                    rfid.add(e.rfid);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new InventoryAdapter(names, rfid));
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
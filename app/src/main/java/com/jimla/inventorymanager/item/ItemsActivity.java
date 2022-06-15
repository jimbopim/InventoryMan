package com.jimla.inventorymanager.item;

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

public class ItemsActivity extends AppCompatActivity implements ItemsAdapter.OnItemClickListener {

    private ItemDao itemDao;
    private RecyclerView recyclerView;

    private final HashMap<Integer, Integer> items = new HashMap<>();

    private int roomId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                roomId = extras.getInt("roomId");
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
        ItemsAdapter.ViewHolder.setOnItemClickListener(this);
    }

    private void initUI() {
        Button buttonAdd = findViewById(R.id.button_add_new2);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsActivity.this, ItemDetails.class);
                intent.putExtra("roomId", roomId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(ItemsActivity.this, ItemDetails.class);
        intent.putExtra("itemId", items.get(position));

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("debug", "onResume/ItemsActivity");
        setAdapter();
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
                        recyclerView.setAdapter(new ItemsAdapter(names, rfid));
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
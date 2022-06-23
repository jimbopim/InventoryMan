package com.jimla.inventorymanager.room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.common.BaseActivity;
import com.jimla.inventorymanager.item.ItemActivity;
import com.jimla.inventorymanager.search.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RoomActivity extends BaseActivity implements RoomAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;

    private final ArrayList<Room> rooms = new ArrayList<>();

    private int currentSiteId;

    private ProgressDialog dialog;

    private int tries = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentSiteId = extras.getInt("siteId");
            }
        } else {
            currentSiteId = savedInstanceState.getInt("siteId");
        }

        initRecyclerView();
        initUI();
        fetchData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("siteId", currentSiteId);
    }

    private void fetchData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        String urlString = getResources().getString(R.string.api_inventory) + "room" + "?siteId=" + currentSiteId;
        StringRequest request = new StringRequest(urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                string = new String(string.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                rooms.clear();
                rooms.addAll(parseJsonData(string));
                setAdapter();
                if(tries > 1)
                    Toast.makeText(getApplicationContext(), "Fetched data, " + tries + " tries", Toast.LENGTH_SHORT).show();
                tries = 0;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(tries > 5) {
                    Toast.makeText(getApplicationContext(), "Data could not be fetched", Toast.LENGTH_SHORT).show();
                    Log.e("debug", volleyError.toString());
                    dialog.dismiss();
                    tries = 0;
                }
                else {
                    tries++;
                    fetchData();
                }
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(RoomActivity.this);
        rQueue.add(request);
    }

    private ArrayList<Room> parseJsonData(String jsonString) {
        ArrayList<Room> tempSitesArray = new ArrayList<>();
        try {
            JSONArray sitesJson = new JSONArray(jsonString);

            for (int i = 0; i < sitesJson.length(); ++i) {
                JSONObject jsonObject = sitesJson.getJSONObject(i);
                int roomId = jsonObject.getInt("roomId");
                String roomName = jsonObject.getString("name");
                String roomDescription = jsonObject.getString("description");

                Room site = new Room(roomId, roomName, roomDescription);
                tempSitesArray.add(site);
            }
            for (Room s : tempSitesArray)
                Log.i("debug", s.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();

        return tempSitesArray;
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void initUI() {
        Button btnAdd = findViewById(R.id.button_add_new3);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, RoomDetails.class);
                intent.putExtra("siteId", currentSiteId);

                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, SearchActivity.class);
                intent.putExtra("siteId", currentSiteId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(RoomActivity.this, ItemActivity.class);
        Room room = roomAdapter.getRoom(position);
        intent.putExtra("roomId", room.roomId);

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(RoomActivity.this, RoomDetails.class);
        Room room = roomAdapter.getRoom(position);
        intent.putExtra("roomId", room.roomId);
        intent.putExtra("roomName", room.roomName);
        intent.putExtra("roomDescription", room.roomDescription);

        startActivity(intent);
    }

    private void setAdapter() {
        RoomAdapter.OnItemClickListener listener = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                roomAdapter = new RoomAdapter(rooms, listener);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(roomAdapter);
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
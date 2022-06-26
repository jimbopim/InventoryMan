package com.jimla.inventorymanager.item;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ItemActivity extends BaseActivity implements ItemAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    private final ArrayList<Item> items = new ArrayList<>();

    private int currentRoomId = 0;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_items);
        setHeader1Text(getString(R.string.items_list_header));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentRoomId = extras.getInt("roomId");
            }
        } else {
            currentRoomId = savedInstanceState.getInt("roomId");
        }

        initRecyclerView();
        initUI();
        fetchData();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_items;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("roomId", currentRoomId);
    }

    private void fetchData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        String urlString = getResources().getString(R.string.api_inventory) + "item" + "?siteId=" + "1" + "&roomId=" + currentRoomId;
        StringRequest request = new StringRequest(urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                string = new String(string.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                items.clear();
                items.addAll(parseJsonData(string));
                setAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                Log.e("debug", volleyError.toString());
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ItemActivity.this);
        rQueue.add(request);
    }

    private ArrayList<Item> parseJsonData(String jsonString) {
        ArrayList<Item> tempSitesArray = new ArrayList<>();
        try {
            JSONArray sitesJson = new JSONArray(jsonString);

            for (int i = 0; i < sitesJson.length(); ++i) {
                JSONObject jsonObject = sitesJson.getJSONObject(i);
                int itemId = jsonObject.getInt("itemId");
                String itemName = jsonObject.getString("itemName");
                String itemDescription = jsonObject.getString("itemDescription");

                Item site = new Item(itemId, itemName, itemDescription);
                tempSitesArray.add(site);
            }
            for (Item s : tempSitesArray)
                Log.i("debug", s.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();

        return tempSitesArray;
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void initUI() {
        Button btnAdd = findViewById(R.id.button_add_new2);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, ItemDetails.class);
                intent.putExtra("roomId", currentRoomId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(ItemActivity.this, ItemDetails.class);
        Item item = itemAdapter.getItem(position);
        intent.putExtra("roomId", currentRoomId);
        intent.putExtra("itemId", item.itemId);
        intent.putExtra("itemName", item.itemName);
        intent.putExtra("itemDescription", item.itemDescription);

        startActivity(intent);
    }

    private void setAdapter() {
        ItemAdapter.OnItemClickListener listener = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                itemAdapter = new ItemAdapter(items, listener);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(itemAdapter);
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
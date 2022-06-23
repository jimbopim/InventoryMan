package com.jimla.inventorymanager.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.jimla.inventorymanager.item.Item;
import com.jimla.inventorymanager.item.ItemActivity;
import com.jimla.inventorymanager.item.ItemDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements SearchAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;

    private EditText etSearch;

    private final ArrayList<Item> items = new ArrayList<>();

    private int currentSiteId = 0;
    private String restoredSearchString = null;

    private ProgressDialog dialog;

    private int tries = 1;

    private final String baseUrl = "https://sekinsvinteg2t.kinnarps.com:8122/api/inventory/item?";
    //https://sekinsvinteg2t.kinnarps.com:8122/api/inventory/item?siteId=1&pageNumber=1&hits=3&searchKey=motus

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentSiteId = extras.getInt("siteId");
            }
        } else {
            currentSiteId = savedInstanceState.getInt("siteId");
            restoredSearchString = savedInstanceState.getString("searchString");
        }

        initRecyclerView();
        initUI();

        if(restoredSearchString != null)
            fetchData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("siteId", currentSiteId);
        savedInstanceState.putString("searchString", etSearch.getText().toString());
    }

    private String getPar(String name, String value) {
        return "&" + name + "=" + value;
    }

    private String getPar(String name, int value) {
        return getPar(name, String.valueOf(value));
    }

    private void fetchData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        String urlString = baseUrl + getPar("pageNumber", 1) + getPar("hits", 10) + getPar("siteId", currentSiteId) + getPar("searchKey", etSearch.getText().toString());
        Log.i("debug", urlString);
        StringRequest request = new StringRequest(urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                string = new String(string.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                items.clear();
                items.addAll(parseJsonData(string));
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

        RequestQueue rQueue = Volley.newRequestQueue(SearchActivity.this);
        rQueue.add(request);
    }

    private ArrayList<Item> parseJsonData(String jsonString) {
        ArrayList<Item> tempSitesArray = new ArrayList<>();
        try {
            JSONArray sitesJson = new JSONArray(jsonString);

            for (int i = 0; i < sitesJson.length(); ++i) {
                JSONObject jsonObject = sitesJson.getJSONObject(i);
                int roomId = jsonObject.getInt("roomId");
                int itemId = jsonObject.getInt("itemId");
                String itemName = jsonObject.getString("itemName");
                String itemDescription = jsonObject.getString("itemDescription");

                Item item = new Item(itemId, itemName, itemDescription);
                item.roomId = roomId;

                tempSitesArray.add(item);
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
        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void initUI() {
        etSearch = findViewById(R.id.etSearch);
        if(restoredSearchString != null)
            etSearch.setText(restoredSearchString);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                fetchData();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SearchActivity.this, ItemDetails.class);
        Item item = searchAdapter.getItem(position);
        intent.putExtra("roomId", item.roomId);
        intent.putExtra("itemId", item.itemId);
        intent.putExtra("itemName", item.itemName);
        intent.putExtra("itemDescription", item.itemDescription);

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(SearchActivity.this, ItemActivity.class);
        Item item = searchAdapter.getItem(position);
        intent.putExtra("roomId", item.roomId);

        startActivity(intent);
    }

    private void setAdapter() {
        SearchAdapter.OnItemClickListener listener = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                searchAdapter = new SearchAdapter(items, listener);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(searchAdapter);
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

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
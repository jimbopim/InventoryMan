package com.jimla.inventorymanager.site;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jimla.inventorymanager.room.RoomActivity;
import com.nordicid.nurapi.NurDeviceListActivity;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class SiteActivity extends AppCompatActivity implements SiteAdapter.OnItemClickListener, NurHandler.InventoryControllerListener {

    private RecyclerView recyclerView;
    private SiteAdapter siteAdapter;

    TextView tvConnection;

    private final ArrayList<Site> sites = new ArrayList<>();

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        trustEveryone(); //TODO OBS ENDAST TESTSYFTE
        initRecyclerView();
        initUI();
        fetchData();
        startReader();
    }

    private void startReader() {
        NurHandler nurHandler = NurHandler.getInstance();
        nurHandler.setInventoryControllerListener(this);

        //nurHandler.selectDeviceForConnection(this); //TODO Flytta till settings
        showOnUI(tvConnection, "Connecting reader...");
        nurHandler.autoSelectConnection(this);
    }

    private void fetchData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest request = new StringRequest(getResources().getString(R.string.api_sites), new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                string = new String(string.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                sites.clear();
                sites.addAll(parseJsonData(string));
                setAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Error reading from server", Toast.LENGTH_SHORT).show();
                Log.e("debug", volleyError.toString());
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SiteActivity.this);
        rQueue.add(request);
    }

    private ArrayList<Site> parseJsonData(String jsonString) {
        ArrayList<Site> tempSitesArray = new ArrayList<Site>();
        try {
            JSONArray sitesJson = new JSONArray(jsonString);

            for (int i = 0; i < sitesJson.length(); ++i) {
                JSONObject jsonObject = sitesJson.getJSONObject(i);
                int siteId = jsonObject.getInt("siteId");
                int siteType = jsonObject.getInt("siteType");
                String siteName = jsonObject.getString("siteName");
                String description = jsonObject.getString("description");
                int startDate = jsonObject.getInt("startDate");

                Site site = new Site(siteId, siteType, siteName, description, startDate);
                tempSitesArray.add(site);
            }
            for (Site s : tempSitesArray)
                Log.i("debug", s.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();

        return tempSitesArray;
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void initUI() {
        tvConnection = findViewById(R.id.tvConn);

        Button buttonAdd = findViewById(R.id.button_add_new);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SiteActivity.this, SiteDetails.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SiteActivity.this, RoomActivity.class);
        Site site = siteAdapter.getSite(position);
        intent.putExtra("siteId", site.siteId);

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(SiteActivity.this, SiteDetails.class);

        Site site = siteAdapter.getSite(position);
        intent.putExtra("siteId", site.siteId);
        intent.putExtra("siteType", site.siteType);
        intent.putExtra("siteName", site.siteName);
        intent.putExtra("siteDescription", site.description);
        intent.putExtra("siteStartDate", site.startDate);

        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NurHandler.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NurHandler.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NurHandler.getInstance().onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NurHandler.getInstance().onResume();
    }

    private void setAdapter() {
        SiteAdapter.OnItemClickListener listener = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                siteAdapter = new SiteAdapter(sites, listener);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(siteAdapter);
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

    private void trustEveryone() { //TODO OBS ENDAST TESTSYFTE
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        NurHandler nurHandler = NurHandler.getInstance();
        //NurApiAutoConnectTransport mAutoTransport = nurHandler.getAutoTransport();
        switch (requestCode)
        {
            case NurDeviceListActivity.REQUEST_SELECT_DEVICE: {
                if (data == null || resultCode != NurDeviceListActivity.RESULT_OK)
                    return;
                nurHandler.connectionSelected(data, this);
            }
            break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void showOnUI(TextView textView, String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @Override
    public void tagFound(NurTag tag, boolean isNew) {

    }

    @Override
    public void inventoryRoundDone(NurTagStorage storage, int newTagsOffset, int newTagsAdded) {

    }

    @Override
    public void readerDisconnected() {
        showOnUI(tvConnection, "Reader disconnected");
    }

    @Override
    public void readerConnected() {
        showOnUI(tvConnection, "Reader connected");
    }

    @Override
    public void inventoryStateChanged() {

    }

    @Override
    public void IOChangeEvent(NurEventIOChange event) {

    }
}
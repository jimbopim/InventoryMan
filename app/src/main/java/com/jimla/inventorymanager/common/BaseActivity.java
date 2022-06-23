package com.jimla.inventorymanager.common;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.site.NurHandler;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class BaseActivity extends AppCompatActivity implements NurHandler.InventoryControllerListener {

    //TextView tvConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_site);

        trustEveryone(); //TODO OBS ENDAST TESTSYFTE
        initUI();
        startReader();
    }

    private void initUI() {
        //tvConnection = findViewById(R.id.tvConn);
    }

    private void startReader() {
        NurHandler nurHandler = NurHandler.getInstance();
        nurHandler.setInventoryControllerListener(this);

        if(!nurHandler.isConnected()) {
            //nurHandler.selectDeviceForConnection(this); //TODO Flytta till settings
            //showOnUI(tvConnection, "Connecting reader...");
            nurHandler.autoSelectConnection(this);
        }
    }

    private void showOnUI(TextView textView, String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
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

    @Override
    public void tagFound(NurTag tag, boolean isNew) {

    }

    @Override
    public void inventoryRoundDone(NurTagStorage storage, int newTagsOffset, int newTagsAdded) {

    }

    @Override
    public void readerDisconnected() {
        //showOnUI(tvConnection, "Reader disconnected");
    }

    @Override
    public void readerConnected() {
        //showOnUI(tvConnection, "Reader connected");
    }

    @Override
    public void inventoryStateChanged() {

    }

    @Override
    public void IOChangeEvent(NurEventIOChange event) {

    }
}

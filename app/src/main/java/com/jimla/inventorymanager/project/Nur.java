/*
package com.jimla.inventorymanager.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jimla.inventorymanager.R;
import com.nordicid.nurapi.BleScanner;
import com.nordicid.nurapi.NurApi;
import com.nordicid.nurapi.NurApiAutoConnectTransport;
import com.nordicid.nurapi.NurApiBLEAutoConnect;
import com.nordicid.nurapi.NurApiErrors;
import com.nordicid.nurapi.NurApiException;
import com.nordicid.nurapi.NurApiListener;
import com.nordicid.nurapi.NurDeviceListActivity;
import com.nordicid.nurapi.NurDeviceSpec;
import com.nordicid.nurapi.NurEventAutotune;
import com.nordicid.nurapi.NurEventClientInfo;
import com.nordicid.nurapi.NurEventDeviceInfo;
import com.nordicid.nurapi.NurEventEpcEnum;
import com.nordicid.nurapi.NurEventFrequencyHop;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurEventInventory;
import com.nordicid.nurapi.NurEventNxpAlarm;
import com.nordicid.nurapi.NurEventProgrammingProgress;
import com.nordicid.nurapi.NurEventTagTrackingChange;
import com.nordicid.nurapi.NurEventTagTrackingData;
import com.nordicid.nurapi.NurEventTraceTag;
import com.nordicid.nurapi.NurEventTriggeredRead;
import com.nordicid.nurapi.NurRespReaderInfo;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;
import com.nordicid.tdt.EPCTagEngine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Nur extends AppCompatActivity implements NurApiListener {
    NurApi nurApi;

    TextView tvConnStatus;

    private NurApiAutoConnectTransport mAutoTransport;
    private NurApiBLEAutoConnect mBLEAuto= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);

        nurApi = new NurApi();
        nurApi.setListener(this);
        Log.i("debug", "NurApi version: " + nurApi.getFileVersion());

        initUI();
        //BleScanner.init(this);
        NurDeviceListActivity.startDeviceRequest(Nur.this, nurApi);

        String strAddress = "DF:86:FA:B8:01:08";  //Known address of EXA
        mBLEAuto= new NurApiBLEAutoConnect(Nur.this, nurApi);
        mBLEAuto.setAddress(strAddress); //Auto connection start here

        showOnUI(tvConnStatus, "Connecting...");
    }

    private void initUI() {

        tvConnStatus = findViewById(R.id.tvConnStatus);
        Button buttonRead = findViewById(R.id.btnRead);

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    doSingleInventory(true);

                    Intent data = new Intent();
                    String text = mTagStorage.get(0) + "";
                    Log.i("debug", text);
                    data.putExtra("tagEpc", text);
                    setResult(RESULT_OK, data);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    */
/**
     \* DeviceList activity result \* @param requestCode We are intrest code "NurDeviceListActivity.REQUEST\_SELECT\_DEVICE" (32778)
     \* @param resultCode If RESULT_OK user has selected device and then we create NurDeviceSpec (spec) and transport
     \* @param data
     *//*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode)
        {
            case NurDeviceListActivity.REQUEST_SELECT_DEVICE: {
                if (data == null || resultCode != NurDeviceListActivity.RESULT_OK)
                    return;
                try {
                    NurDeviceSpec spec = new NurDeviceSpec(data.getStringExtra(NurDeviceListActivity.SPECSTR));

                    if (mAutoTransport != null) {
                        //Dispose existing transport
                        mAutoTransport.dispose();
                    }

                    String strAddress;
                    mAutoTransport = NurDeviceSpec.createAutoConnectTransport(this, nurApi, spec);
                    strAddress = spec.getAddress();
                    mAutoTransport.setAddress(strAddress);

                } catch (Exception e) {
                    showOnUI(tvConnStatus, e.getMessage());
                }
            }
            break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

*/
/*    public NurTag getNearestTag() throws Exception {
        if (!nurApi.isConnected())
            return null;

        // Make sure antenna autoswitch is enabled
        if (nurApi.getSetupSelectedAntenna() != NurApi.ANTENNAID_AUTOSELECT)
            nurApi.setSetupSelectedAntenna(NurApi.ANTENNAID_AUTOSELECT);

        // Clear old readings
        clearInventoryReadings();

        // Perform inventory
        try {
            nurApi.inventory();
            // Fetch tags from NUR
            nurApi.fetchTags();
        }
        catch (NurApiException ex)
        {
            Log.i("INV", ex.getMessage());
            // Did not get any tags
            if (ex.error == NurApiErrors.NO_TAG)
                return true;

            throw ex;
        }
        // Handle inventoried tags
        handleInventoryResult();

        return true;
    }*//*


    public boolean doSingleInventory(Boolean clearReadings) throws Exception {
        if (!nurApi.isConnected())
            return false;

        // Make sure antenna autoswitch is enabled
        if (nurApi.getSetupSelectedAntenna() != NurApi.ANTENNAID_AUTOSELECT)
            nurApi.setSetupSelectedAntenna(NurApi.ANTENNAID_AUTOSELECT);

        // Clear old readings
        if(clearReadings)
            clearInventoryReadings();
        // Perform inventory
        try {
            nurApi.inventory();
            // Fetch tags from NUR
            nurApi.fetchTags();
        }
        catch (NurApiException ex)
        {
            Log.i("INV", ex.getMessage());
            // Did not get any tags
            if (ex.error == NurApiErrors.NO_TAG)
                return true;

            throw ex;
        }
        // Handle inventoried tags
        handleInventoryResult();

        return true;
    }

    private NurTagStorage mTagStorage = new NurTagStorage();
    public boolean mReadTDTPureUri;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    public int mInvType; //0=epc 1=epc+tid 2=epc+user
    private boolean mAddGpsCoord;
    private ArrayList<HashMap<String, String>> mListViewAdapterData = new ArrayList<HashMap<String,String>>();
    private InventoryControllerListener mInventoryListener = null;
    private int mAddedUnique = 0;


    @SuppressWarnings("unchecked")
    void handleInventoryResult()
    {
        synchronized (nurApi.getStorage())
        {
            HashMap<String, String> tmp;
            NurTagStorage tagStorage = nurApi.getStorage();
            int curUniqueCount = mTagStorage.size();

            //Log.i("INV","storageSize=" + Integer.toString(tagStorage.size()));
            // Add tags tp internal tag storage
            for (int i = 0; i < tagStorage.size(); i++) {

                NurTag tag = tagStorage.get(i);
                Log.i("debug", tag.getEpcString());
                if (mTagStorage.addTag(tag))
                {
                    tmp = new HashMap<String, String>();
                    // Add new
                    if(mReadTDTPureUri) {
                        try {
                            //Check if tag is GS1 coded. Exception fired if not and plain EPC shown.
                            //This is TDT (TagDataTranslation) library feature.
                            EPCTagEngine engine = new EPCTagEngine(tag.getEpcString());
                            //Looks like it is GS1 coded.
                            //tmp.put("epc", engine.buildPureIdentityURI());
                            tmp.put("epc", engine.buildTagURI());
                        } catch (Exception ex) {
                            //Not GS1 coded. Show only EPC hex string.
                            tmp.put("epc", tag.getEpcString());
                        }
                    }
                    else
                        tmp.put("epc", tag.getEpcString());

                    tmp.put("rssi", Integer.toString(tag.getRssi()));
                    tmp.put("maxrssi", Integer.toString(tag.getRssi()));
                    tmp.put("timestamp", Integer.toString(tag.getTimestamp()));
                    tmp.put("freq", Integer.toString(tag.getFreq())+" kHz Ch: "+Integer.toString(tag.getChannel()));
                    tmp.put("found", "1");
                    tmp.put("foundpercent", "100");
                    tmp.put("firstseentime", dateFormatter.format(new Date()));
                    tmp.put("lastseentime", dateFormatter.format(new Date()));
                    tmp.put("invtype",Integer.toString(mInvType));

                    if(mInvType > 0) {
                        byte[] irdata = tag.getIrData();
                        if(irdata != null)
                            tmp.put("irdata", NurApi.byteArrayToHexString(irdata));
                        else tmp.put("irdata", "");
                    }
                    else tmp.put("irdata","");

                    if(mAddGpsCoord) {
                        //tmp.put("gps",AppTemplate.getAppTemplate().getLocation()); //TODO
                    }
                    //Log.w("INV","Update type=" + mInvType + " epc="+tag.getEpcString() + " ir=" +tmp.get("irdata"));
                    tag.setUserdata(tmp);
                    mListViewAdapterData.add(tmp);

                    if (mInventoryListener != null)
                        mInventoryListener.tagFound(tag, true);

                }
                else
                {
                    // Update
                    tag = mTagStorage.getTag(tag.getEpc());
                    tmp = (HashMap<String, String>) tag.getUserdata();
                    tmp.put("rssi", Integer.toString(tag.getRssi()));

                    String rss = tmp.get("maxrssi");
                    int val = Integer.decode(rss);
                    if(tag.getRssi()>val)
                        tmp.put("maxrssi", Integer.toString(tag.getRssi()));

                    tmp.put("timestamp", Integer.toString(tag.getTimestamp()));
                    tmp.put("freq", Integer.toString(tag.getFreq())+" kHz (Ch: "+Integer.toString(tag.getChannel())+")");
                    tmp.put("found", Integer.toString(tag.getUpdateCount()));
                    //tmp.put("foundpercent", Integer.toString((int) (((double) tag.getUpdateCount()) / (double) mStats.getInventoryRounds() * 100)));
                    tmp.put("lastseentime", dateFormatter.format(new Date()));
                    tmp.put("invtype",Integer.toString(mInvType));


                    if(mInvType > 0) {
                        byte[] irdata = tag.getIrData();
                        if(irdata != null)
                            tmp.put("irdata", NurApi.byteArrayToHexString(irdata));
                        else tmp.put("irdata", "");
                    }
                    else tmp.put("irdata","");

                    //if(mAddGpsCoord)
                    //    tmp.put("gps", AppTemplate.getAppTemplate().getLocation());

                    //Log.w("INV","Update type=" + mInvType + " epc="+tag.getEpcString() + " ir=" +tmp.get("irdata"));
                    if (mInventoryListener != null)
                        mInventoryListener.tagFound(tag, false);
                }
            }

            // Clear NurApi tag storage
            tagStorage.clear();

            // Check & report new unique tags
            mAddedUnique = mTagStorage.size() - curUniqueCount;
            if (mAddedUnique > 0)
            {
                //mStats.setTagsFoundInTimeSecs();

                // Report round done w/ new unique tags
                if (mInventoryListener != null)
                    mInventoryListener.inventoryRoundDone(mTagStorage, curUniqueCount, mAddedUnique);
            }

        }
    }

    public interface InventoryControllerListener {
        public void tagFound(NurTag tag, boolean isNew);
        public void inventoryRoundDone(NurTagStorage storage, int newTagsOffset, int newTagsAdded);
        public void readerDisconnected();
        public void readerConnected();
        public void inventoryStateChanged();
        public void IOChangeEvent(NurEventIOChange event);
    }

    public void clearInventoryReadings() {

        //mAddedUnique = 0;
        nurApi.getStorage().clear();
        //mTagStorage.clear();
        //mStats.clear();
        //mListViewAdapterData.clear();

        //if (isInventoryRunning())
        //    mStats.start();
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
    public void logEvent(int i, String s) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mBLEAuto.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBLEAuto.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBLEAuto.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBLEAuto.onResume();
    }

    @Override
    public void connectedEvent() {
        try {  //EXA connected. Show serial.
            NurRespReaderInfo info = nurApi.getReaderInfo();
            //mConnStatus=info.altSerial;
            showOnUI(tvConnStatus, "Connected - " + info.altSerial);
        }
        catch (Exception ex) {  //Show error if any..
            showOnUI(tvConnStatus, ex.getMessage());
        }
        //showOnUI(tvConnStatus, "Connected");
    }

    @Override
    public void disconnectedEvent() {
        showOnUI(tvConnStatus, "Disconnected");
    }

    @Override
    public void bootEvent(String s) {

    }

    @Override
    public void inventoryStreamEvent(NurEventInventory nurEventInventory) {

    }

    @Override
    public void IOChangeEvent(NurEventIOChange nurEventIOChange) {

    }

    @Override
    public void traceTagEvent(NurEventTraceTag nurEventTraceTag) {

    }

    @Override
    public void triggeredReadEvent(NurEventTriggeredRead nurEventTriggeredRead) {

    }

    @Override
    public void frequencyHopEvent(NurEventFrequencyHop nurEventFrequencyHop) {

    }

    @Override
    public void debugMessageEvent(String s) {

    }

    @Override
    public void inventoryExtendedStreamEvent(NurEventInventory nurEventInventory) {

    }

    @Override
    public void programmingProgressEvent(NurEventProgrammingProgress nurEventProgrammingProgress) {

    }

    @Override
    public void deviceSearchEvent(NurEventDeviceInfo nurEventDeviceInfo) {

    }

    @Override
    public void clientConnectedEvent(NurEventClientInfo nurEventClientInfo) {

    }

    @Override
    public void clientDisconnectedEvent(NurEventClientInfo nurEventClientInfo) {

    }

    @Override
    public void nxpEasAlarmEvent(NurEventNxpAlarm nurEventNxpAlarm) {

    }

    @Override
    public void epcEnumEvent(NurEventEpcEnum nurEventEpcEnum) {

    }

    @Override
    public void autotuneEvent(NurEventAutotune nurEventAutotune) {

    }

    @Override
    public void tagTrackingScanEvent(NurEventTagTrackingData nurEventTagTrackingData) {

    }

    @Override
    public void tagTrackingChangeEvent(NurEventTagTrackingChange nurEventTagTrackingChange) {

    }
}
*/

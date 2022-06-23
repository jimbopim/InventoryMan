package com.jimla.inventorymanager.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

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
import com.nordicid.nurapi.NurIRConfig;
import com.nordicid.nurapi.NurSmartPairSupport;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NurHandler implements NurApiListener {
    private static SharedPreferences mApplicationPrefences = null;

    private boolean connected;
    private NurApi nurApi;
    private NurApiAutoConnectTransport mAutoTransport;

    private NurApiBLEAutoConnect mBLEAuto = null;

    private static NurHandler instance;

    private NurTagStorage mTagStorage = new NurTagStorage();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    public int mInvType; //0=epc 1=epc+tid 2=epc+user
    private InventoryControllerListener mInventoryListener = null;
    private int mAddedUnique = 0;


    public static NurHandler getInstance() {
        if (instance == null)
            instance = new NurHandler();

        return instance;
    }

    private NurHandler() {
        nurApi = new NurApi();
        nurApi.setListener(this);
        Log.i("debug", "NurApi version: " + nurApi.getFileVersion());
    }

    public void setInventoryControllerListener(InventoryControllerListener listener) {
        mInventoryListener = listener;
    }

    public void autoSelectConnection(Activity activity) {
        if (mApplicationPrefences == null)
            mApplicationPrefences = activity.getSharedPreferences("InventoryManagerReader", Context.MODE_PRIVATE);
        loadSettings(activity);
    }

    public void selectDeviceForConnection(Activity activity) {
        if (!nurApi.isConnected()) {

            BleScanner.init(activity);
            NurDeviceListActivity.startDeviceRequest(activity, nurApi);

            String strAddress = "DF:86:FA:B8:01:08";  //Known address of EXA
            mBLEAuto = new NurApiBLEAutoConnect(activity, nurApi);
            mBLEAuto.setAddress(strAddress); //Auto connection start here
        }
    }

    public void connectionSelected(Intent data, Activity activity) {

        try {
            NurDeviceSpec spec = new NurDeviceSpec(data.getStringExtra(NurDeviceListActivity.SPECSTR));

            if (mAutoTransport != null) {
                //Dispose existing transport
                mAutoTransport.dispose();
            }

            String strAddress;
            mAutoTransport = NurDeviceSpec.createAutoConnectTransport(activity, getInstance().nurApi, spec);
            strAddress = spec.getAddress();
            mAutoTransport.setAddress(strAddress);

            saveSettings(spec, activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveSettings(NurDeviceSpec connSpec, Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("InventoryManagerReader", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (mAutoTransport == null) {
            editor.putString("specStr", "");
        } else {
            editor.putString("specStr", connSpec.getSpec());
        }
        editor.apply();

        //updateStatus();
    }

    public void loadSettings(Activity activity) {
        //Beeper.setEnabled(mApplicationPrefences.getBoolean("Sounds", true));

        String specStr = mApplicationPrefences.getString("specStr", "");
        if (specStr.length() == 0) {
            String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);
            if (manufacturer.contains("nordicid") || manufacturer.contains("nordic id")) {
                // Defaults to integrated reader
                specStr = "type=INT;addr=integrated_reader";
            }
        }

        if (specStr.length() > 0) {
            NurDeviceSpec spec = new NurDeviceSpec(specStr);

            if (mAutoTransport != null) {
                System.out.println("Dispose transport");
                mAutoTransport.dispose();
            }

            try {
                String strAddress;
                mAutoTransport = NurDeviceSpec.createAutoConnectTransport(activity, nurApi, spec);
                strAddress = spec.getAddress();

                mAutoTransport.setAddress(strAddress);
            } catch (NurApiException e) {
                e.printStackTrace();
            }
        }

        NurSmartPairSupport.setSettingsString(mApplicationPrefences.getString("SmartPairSettings", "{}"));

        //updateStatus();
    }

    public String getNearestTagEpc() {
        String epc = null;
        try {
            epc = getNearestTag().getEpcString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (epc == null)
            epc = "";
        return epc;
    }

    private NurTag getNearestTag() throws Exception {
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
        } catch (NurApiException ex) {
            Log.i("INV", ex.getMessage());
            // Did not get any tags
            if (ex.error == NurApiErrors.NO_TAG)
                return null;

            throw ex;
        }
        // Handle inventoried tags
        handleInventoryResult();

        NurTag nearest = null;
        for (int i = 0; i < mTagStorage.size(); i++) {
            NurTag t = mTagStorage.get(i);
            Log.i("debug", "Tag: " + t.getEpcString() + " RSSI: " + t.getRssi());
            if (nearest == null)
                nearest = t;
            else if (t.getRssi() > nearest.getRssi()) {
                nearest = t;
            }
        }

        return nearest;
    }

/*    public boolean doSingleInventory(Boolean clearReadings) throws Exception {
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
    }*/

    @SuppressWarnings("unchecked")
    void handleInventoryResult() {
        synchronized (nurApi.getStorage()) {
            HashMap<String, String> tmp;
            NurTagStorage tagStorage = nurApi.getStorage();
            int curUniqueCount = mTagStorage.size();

            //Log.i("INV","storageSize=" + Integer.toString(tagStorage.size()));
            // Add tags tp internal tag storage
            for (int i = 0; i < tagStorage.size(); i++) {

                NurTag tag = tagStorage.get(i);
                if (mTagStorage.addTag(tag)) {
                    tmp = new HashMap<String, String>();
                    // Add new
/*                    if(mReadTDTPureUri) {
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
                    else*/
                    tmp.put("epc", tag.getEpcString());

                    tmp.put("rssi", Integer.toString(tag.getRssi()));
                    tmp.put("maxrssi", Integer.toString(tag.getRssi()));
                    tmp.put("timestamp", Integer.toString(tag.getTimestamp()));
                    tmp.put("freq", Integer.toString(tag.getFreq()) + " kHz Ch: " + Integer.toString(tag.getChannel()));
                    tmp.put("found", "1");
                    tmp.put("foundpercent", "100");
                    tmp.put("firstseentime", dateFormatter.format(new Date()));
                    tmp.put("lastseentime", dateFormatter.format(new Date()));
                    tmp.put("invtype", Integer.toString(mInvType));

                    if (mInvType > 0) {
                        byte[] irdata = tag.getIrData();
                        if (irdata != null)
                            tmp.put("irdata", NurApi.byteArrayToHexString(irdata));
                        else tmp.put("irdata", "");
                    } else tmp.put("irdata", "");

                    //if(mAddGpsCoord) {
                    //tmp.put("gps",AppTemplate.getAppTemplate().getLocation());
                    //}
                    //Log.w("INV","Update type=" + mInvType + " epc="+tag.getEpcString() + " ir=" +tmp.get("irdata"));
                    tag.setUserdata(tmp);
                    //mListViewAdapterData.add(tmp);

                    if (mInventoryListener != null)
                        mInventoryListener.tagFound(tag, true);

                } else {
                    // Update
                    tag = mTagStorage.getTag(tag.getEpc());
                    tmp = (HashMap<String, String>) tag.getUserdata();
                    tmp.put("rssi", Integer.toString(tag.getRssi()));

                    String rss = tmp.get("maxrssi");
                    int val = Integer.decode(rss);
                    if (tag.getRssi() > val)
                        tmp.put("maxrssi", Integer.toString(tag.getRssi()));

                    tmp.put("timestamp", Integer.toString(tag.getTimestamp()));
                    tmp.put("freq", Integer.toString(tag.getFreq()) + " kHz (Ch: " + Integer.toString(tag.getChannel()) + ")");
                    tmp.put("found", Integer.toString(tag.getUpdateCount()));
                    //tmp.put("foundpercent", Integer.toString((int) (((double) tag.getUpdateCount()) / (double) mStats.getInventoryRounds() * 100)));
                    tmp.put("lastseentime", dateFormatter.format(new Date()));
                    tmp.put("invtype", Integer.toString(mInvType));


                    if (mInvType > 0) {
                        byte[] irdata = tag.getIrData();
                        if (irdata != null)
                            tmp.put("irdata", NurApi.byteArrayToHexString(irdata));
                        else tmp.put("irdata", "");
                    } else tmp.put("irdata", "");

                    //Log.w("INV","Update type=" + mInvType + " epc="+tag.getEpcString() + " ir=" +tmp.get("irdata"));
                    if (mInventoryListener != null)
                        mInventoryListener.tagFound(tag, false);
                }
            }

            // Clear NurApi tag storage
            tagStorage.clear();

            // Check & report new unique tags
            mAddedUnique = mTagStorage.size() - curUniqueCount;
            if (mAddedUnique > 0) {
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
        mAddedUnique = 0;
        nurApi.getStorage().clear();
        mTagStorage.clear();
    }

    public void stopInventory() {
        try {
            //mInventoryRunning = false;

            // Stop reading
            if (nurApi.isConnected()) {

                nurApi.stopInventoryStream();
                NurIRConfig ir = new NurIRConfig();
                ir.IsRunning = false;
                nurApi.setIRConfig(ir);
                nurApi.setSetupOpFlags(nurApi.getSetupOpFlags() & ~NurApi.OPFLAGS_INVSTREAM_ZEROS);
            }

            // Stop beeper thread
/*            if (mBeeperThread != null) {
                mBeeperThread.join(5000);
                mBeeperThread = null;
            }*/

        } catch (Exception err) {
            err.printStackTrace();
        }

        // Notify state change
        if (mInventoryListener != null)
            mInventoryListener.inventoryStateChanged();
    }

    @Override
    public void logEvent(int i, String s) {

    }

    protected void onPause() {
        if (mBLEAuto != null)
            mBLEAuto.onPause();
    }

    protected void onStop() {
        if (mBLEAuto != null)
            mBLEAuto.onStop();
    }

    protected void onDestroy() {
        if (mBLEAuto != null)
            mBLEAuto.onDestroy();
    }

    protected void onResume() {
        if (mBLEAuto != null)
            mBLEAuto.onResume();
    }

    @Override
    public void connectedEvent() {
        //NurRespReaderInfo info = nurApi.getReaderInfo();
        //mConnStatus=info.altSerial;
        connected = true;
        if (mInventoryListener != null) {
            mInventoryListener.readerConnected();
        }
    }

    @Override
    public void disconnectedEvent() {
        connected = false;
        //showOnUI(tvConnStatus, "Disconnected");
        if (mInventoryListener != null) {
            mInventoryListener.readerDisconnected();
            stopInventory();
        }
    }

    @Override
    public void bootEvent(String s) {

    }

    @Override
    public void inventoryStreamEvent(NurEventInventory nurEventInventory) {

    }

    @Override
    public void IOChangeEvent(NurEventIOChange event) {
        if (mInventoryListener != null) {
            mInventoryListener.IOChangeEvent(event);
        }
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

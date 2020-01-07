package com.klakier.bluemouse;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.Process;
import android.util.Log;

import java.util.UUID;

public class BLEServerService extends Service {

    final static String TAG = "Klakier.BLEService";

    private final IBinder binder = new LocalBinder();

    public Handler myHandler;
    Thread testThread;
    volatile Looper looper;

    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    public static UUID HID_SERVICE = UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");

    public BLEServerService() {
        Log.d(TAG, "constructor");
    }

    public void halt () {
        stopSelf();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d(TAG, "MAIN LOOP: LOOPER PREPARED");
                looper = Looper.myLooper();
                myHandler = new Handler();
                Log.d(TAG, "MAIN LOOP: HANDLER CREATED");
                Looper.loop();
                Log.d(TAG, "MAIN LOOP: FINISHED");
            }
        });
        testThread.start();

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        startAdvertising();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (looper != null)
            looper.quitSafely();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        while(myHandler == null);

        myHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: test");
            }
        });
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: delayed, should stop after this");
            }
        }, 2000);
        myHandler.postDelayed(null, 2500);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    public class LocalBinder extends Binder {
        BLEServerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BLEServerService.this;
        }
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };

    public void startServer(){

    }

    public void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(HID_SERVICE))
                .build();

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
    }

}

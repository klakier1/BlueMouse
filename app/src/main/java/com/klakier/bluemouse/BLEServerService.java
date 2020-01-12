package com.klakier.bluemouse;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import static com.klakier.bluemouse.HIDProfile.HID_SERVICE;

public class BLEServerService extends Service {

    final static String TAG = "Klakier.BLEService";

    private final IBinder binder = new LocalBinder();

    public Handler myHandler;
    Thread testThread;
    volatile Looper looper;

    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    /* Collection of notification subscribers */
    private Set<BluetoothDevice> mRegisteredDevices = new HashSet<>();

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: " + errorCode);
        }
    };
    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                //Remove device from any active subscriptions
                mRegisteredDevices.remove(device);
            }
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG, "onCharacteristicWriteRequest: " + device.getName()
                    + "char: " + characteristic.getUuid().toString()
                    + "valLen" + value.length);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicReadRequest: " + device.toString() + "char: "
                    + characteristic.getUuid().toString());

            if( HIDProfile.HID_INFORMATION_CHARACTERISTIC.equals(characteristic.getUuid())){
                Log.d(TAG, "onCharacteristicReadRequest: HID_INFO");
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothProfile.GATT_SERVER,
                        0,
                        HIDProfile.HID_INFO);
            }

//            long now = System.currentTimeMillis();
//            if (TimeProfile.CURRENT_TIME.equals(characteristic.getUuid())) {
//                Log.i(TAG, "Read CurrentTime");
//                mBluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_SUCCESS,
//                        0,
//                        TimeProfile.getExactTime(now, TimeProfile.ADJUST_NONE));
//            } else if (TimeProfile.LOCAL_TIME_INFO.equals(characteristic.getUuid())) {
//                Log.i(TAG, "Read LocalTimeInfo");
//                mBluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_SUCCESS,
//                        0,
//                        TimeProfile.getLocalTimeInfo(now));
//            } else {
//                // Invalid characteristic
//                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
//                mBluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_FAILURE,
//                        0,
//                        null);
//            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattDescriptor descriptor) {

            Log.d(TAG, "onDescriptorReadRequest: " + device.toString()
                    + " desc: " + descriptor.getUuid().toString());
//            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
//                Log.d(TAG, "Config descriptor read");
//                byte[] returnValue;
//                if (mRegisteredDevices.contains(device)) {
//                    returnValue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
//                } else {
//                    returnValue = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
//                }
//                mBluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_FAILURE,
//                        0,
//                        returnValue);
//            } else {
//                Log.w(TAG, "Unknown descriptor read request");
//                mBluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_FAILURE,
//                        0,
//                        null);
//            }
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {

            Log.d(TAG, "onDescriptorWriteRequest: " + device.getAddress()
                    + "desc:" + descriptor.getUuid().toString()
                    + " valLen: " + value.length);
//            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
//                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
//                    Log.d(TAG, "Subscribe device to notifications: " + device);
//                    mRegisteredDevices.add(device);
//                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
//                    Log.d(TAG, "Unsubscribe device from notifications: " + device);
//                    mRegisteredDevices.remove(device);
//                }
//
//                if (responseNeeded) {
//                    mBluetoothGattServer.sendResponse(device,
//                            requestId,
//                            BluetoothGatt.GATT_SUCCESS,
//                            0,
//                            null);
//                }
//            } else {
//                Log.w(TAG, "Unknown descriptor write request");
//                if (responseNeeded) {
//                    mBluetoothGattServer.sendResponse(device,
//                            requestId,
//                            BluetoothGatt.GATT_FAILURE,
//                            0,
//                            null);
//                }
//            }
        }
    };

    public BLEServerService() {
        Log.d(TAG, "constructor");
    }

    public void halt() {
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
        startServer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopServer();
            stopAdvertising();
        }

        if (looper != null)
            looper.quitSafely();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        while (myHandler == null) ;

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

    public void startServer() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        mBluetoothGattServer.addService(HIDProfile.createHIDService());
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

    private void stopServer() {
        if (mBluetoothGattServer == null) return;

        mBluetoothGattServer.close();
    }

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    public class LocalBinder extends Binder {
        BLEServerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BLEServerService.this;
        }
    }
}

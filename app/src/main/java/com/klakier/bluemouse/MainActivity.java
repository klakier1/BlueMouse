package com.klakier.bluemouse;

import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Klakier.MainActivity";
    private Intent mServiceIntent;
    private BLEServerService mService;
    private boolean mBound;
    static final byte[] f4398b = {5, 1, 9, 6, -95, 1, -123, 1, 5, 7, 25, -32, 41, -25, 21, 0, 37, 1, 117, 1, -107, 8, -127, 2, 117, 8, -107, 1, -127, 1, 117, 8, -107, 5, 21, 0, 37, 101, 5, 7, 25, 0, 41, 101, -127, 0, -64, 5, 1, 9, 2, -95, 1, -123, 2, 9, 1, -95, 0, 5, 9, 25, 1, 41, 3, 21, 0, 37, 1, 117, 1, -107, 3, -127, 2, 117, 5, -107, 1, -127, 1, 5, 1, 9, 48, 9, 49, 9, 56, 21, -127, 37, Byte.MAX_VALUE, 117, 8, -107, 3, -127, 6, 5, 12, 10, 56, 2, -107, 1, -127, 6, -64, -64};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mServiceIntent = new Intent(this, BLEServerService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        Log.d(TAG, "onDestroy: ");
        if (isFinishing()) {
            stopService(mServiceIntent);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(mBound)
                unbindService(connection);
                mService.halt();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BLEServerService.LocalBinder binder = (BLEServerService.LocalBinder) service;
            mService = binder.getService();
            Log.d(TAG, "onServiceConnected: ");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected: ");
            mBound = false;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(TAG, "onBindingDied: ");
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, "onNullBinding: ");
        }
    };
}

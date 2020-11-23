package com.co2team.covidbuster;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.co2team.covidbuster.model.SensorData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mIsScanning = false;
    private BluetoothLeScanner mScanner;
    private HandlerThread scanningThread = new HandlerThread("ScanningThread");

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            ScanRecord scanRecord = result.getScanRecord();
            if(scanRecord.getManufacturerSpecificData().size() >= 0) {
                SensorData sensorData = SensorData.processPayload(scanRecord.getManufacturerSpecificData().valueAt(0));

                Log.i(TAG, "Scanning Room: " + sensorData.getRoomValue() + "; co2: " + sensorData.getCo2Value() + " Temp: " + sensorData.getTemperatureValue() + " Humid: " + sensorData.getHumidityValue() +  " Battery: " + sensorData.getBatteryValue());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed, errorCode = " + errorCode);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        boolean hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (hasBle) {
            Log.d(TAG, "BLE available");
            BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "BLE enabled");
                mScanner = bluetoothAdapter.getBluetoothLeScanner();
                String[] permissions = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                int requestCode = 0;
                ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);
            } else {
                Log.d(TAG, "BLE not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(TAG, "BLE not available");
        }
    }

    private void scan() {
        if(mIsScanning) {
            Log.d(TAG, "Already scanning ...");
            return;
        }

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setDeviceName("COVID BUSTER PERIPHERAL").build());
        ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)).build();

        Log.d(TAG, "start scan");
        mIsScanning = true;

        scanningThread.start();
        Handler handler = new Handler(scanningThread.getLooper());
        Runnable runnable = () -> mScanner.startScan(filters, settings, mScanCallback);
        handler.post(runnable);
    }

    @Override
    public void onRequestPermissionsResult (
            int requestCode, String[] permissions, int[] grantResults)
    {
        if (isLocationEnabled()) {
            scan();
        } else {
            Log.d(TAG, "Location not enabled");
        }
    }

    private boolean isLocationEnabled() {
        // Based on https://stackoverflow.com/questions/10311834
        boolean enabled;
        int locationMode;
        try { // 19+
            locationMode = Settings.Secure.getInt(
                    this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            enabled = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } catch (Settings.SettingNotFoundException e) {
            enabled = false;
        }
        return enabled;
    }

}
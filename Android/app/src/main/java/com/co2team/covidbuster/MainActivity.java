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
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;

import com.co2team.covidbuster.model.RoomCo2Data;
import com.co2team.covidbuster.model.SensorData;
import com.co2team.covidbuster.service.BackendService;
import com.co2team.covidbuster.ui.TabAdapter;
import com.co2team.covidbuster.ui.currentroom.CurrentRoomFragment;
import com.co2team.covidbuster.ui.currentroom.CurrentRoomViewModel;
import com.co2team.covidbuster.ui.roomlist.RoomListFragment;
import com.co2team.covidbuster.util.Utils;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mIsScanning = false;
    private BluetoothLeScanner mScanner;
    private final HandlerThread scanningThread = new HandlerThread("ScanningThread");
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final BackendService backendService = new BackendService();
    private CurrentRoomViewModel roomViewModel;
    private boolean ignoreValuesForNextTwoSeconds = false;

    private ArrayList covidDeviceList = new ArrayList();

    private final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ignoreValuesForNextTwoSeconds) {
                return;
            }

            ScanRecord scanRecord = result.getScanRecord();
            //covid UUID 0000fd6f-0000-1000-8000-00805f9b34fb
            if(scanRecord.getServiceData().toString().contains("0000fd6f-0000-1000-8000-00805f9b34fb")){
                if(!covidDeviceList.contains(result.getDevice())){
                    covidDeviceList.add(result.getDevice());
                    Log.i(TAG, result.getDevice().toString());
                }
                Log.i(TAG, "Number of devices: " + covidDeviceList.size());

            }

            if(scanRecord.getDeviceName() == "COVID BUSTER PERIPHERAL"){
                if (scanRecord.getManufacturerSpecificData().size() >= 0) {
                    SensorData sensorData = SensorData.processPayload(scanRecord.getManufacturerSpecificData().valueAt(0));

                    Log.i(TAG, "Scanning Room: " + sensorData.getRoomId() + "; co2: " + sensorData.getCo2Value() + " Temp: " + sensorData.getTemperatureValue() + " Humid: " + sensorData.getHumidityValue() + " Battery: " + sensorData.getBatteryValue());

                    backendService.uploadCo2Measurement(sensorData.getCo2Value(), sensorData.getRoomId());
                    roomViewModel.setRoomName(Utils.Companion.getRoomName(sensorData.getRoomId()));
                    roomViewModel.setRoomId(sensorData.getRoomId());
                    roomViewModel.setRoomData(new RoomCo2Data(sensorData.getCo2Value(), LocalDateTime.now()));

                    // Advertisement gets executed many (~5) times within a short period. We ignore all but one value within a 2 second period.
                    ignoreValuesForNextTwoSeconds = true;
                    handler.postDelayed(() -> ignoreValuesForNextTwoSeconds = false, 2000);

                    // If no data was read for the last 10s, we assume that we've left the rum. Thus clear the current room data.
                    handler.postDelayed(() -> {
                        if(roomViewModel.getLastUpdated().isBefore(LocalDateTime.now().minusSeconds(10))) {
                            roomViewModel.clearData();
                        }
                    }, 10000);
                }
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

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(RoomListFragment.Companion.newInstance(), "Room List");
        adapter.addFragment(CurrentRoomFragment.Companion.newInstance(), "Current Room");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        roomViewModel = new ViewModelProvider(this).get(CurrentRoomViewModel.class);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

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
        if (mIsScanning) {
            Log.d(TAG, "Already scanning ...");
            return;
        }

        List<ScanFilter> filters = new ArrayList<>();
        //filters.add(new ScanFilter.Builder().setDeviceName("COVID BUSTER PERIPHERAL").build());
        ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)).build();

        Log.d(TAG, "start scan");
        mIsScanning = true;

        scanningThread.start();
        Handler handler = new Handler(scanningThread.getLooper());
        Runnable runnable = () -> mScanner.startScan(filters, settings, mScanCallback);
        handler.post(runnable);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
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
            //noinspection deprecation
            locationMode = Settings.Secure.getInt(
                    this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            enabled = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } catch (Settings.SettingNotFoundException e) {
            enabled = false;
        }
        return enabled;
    }

}
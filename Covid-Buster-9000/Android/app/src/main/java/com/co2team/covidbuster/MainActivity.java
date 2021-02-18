package com.co2team.covidbuster;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.co2team.covidbuster.util.Constants;
import com.co2team.covidbuster.util.Utils;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.co2team.covidbuster.util.Constants.NEED_TO_VENTILATE_NOTIFICATION_ID;
import static com.co2team.covidbuster.util.Constants.SERVICE_NAME_COVID_BUSTER_PERIPHERAL;
import static com.co2team.covidbuster.util.Constants.SWISS_COVID_APP_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "61310408-9a44-4e9f-8588-6abb0ed0c93f";
    private static final String EXTRA_NOTIFICATION_LOAD = "com.co2team.covidbuster.MainActivity.NOTIFICATION_LOAD";

    private NotificationCompat.Builder notificationBuilder;
    private boolean hasNotified = false;
    private ViewPager viewPager;

    private boolean mIsScanning = false;
    private BluetoothLeScanner mScanner;
    private final HandlerThread scanningThread = new HandlerThread("ScanningThread");
    private final Handler delayHandler = new Handler(Looper.getMainLooper());

    private final BackendService backendService = new BackendService();
    private CurrentRoomViewModel roomViewModel;
    private boolean ignoreValuesForNextTwoSeconds = false;

    private boolean covidDeviceOnCollect = false;
    private final Set<BluetoothDevice> covidDeviceList = new HashSet<>();

    private final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            ScanRecord scanRecord = result.getScanRecord();

            checkForCovidApps(result, scanRecord);

            checkForPeripheral(scanRecord);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed, errorCode = " + errorCode);
        }

        private void checkForPeripheral(ScanRecord scanRecord) {
            if (!ignoreValuesForNextTwoSeconds && scanRecord.getDeviceName() != null &&
                    scanRecord.getDeviceName().equals(SERVICE_NAME_COVID_BUSTER_PERIPHERAL) &&
                    scanRecord.getManufacturerSpecificData().size() >= 0) {
                SensorData sensorData = SensorData.processPayload(scanRecord.getManufacturerSpecificData().valueAt(0));

                Log.i(TAG, "Found Covid Buster Peripheral: " + sensorData.getRoomId() + "; co2: " + sensorData.getCo2Value() + " Temp: " + sensorData.getTemperatureValue() + " Humid: " + sensorData.getHumidityValue() + " Battery: " + sensorData.getBatteryValue());

                backendService.uploadCo2Measurement(sensorData.getCo2Value(), sensorData.getRoomId());
                updateCurrentRoom(sensorData);
                notifyIfDangerous(sensorData);
            }
        }

        private void updateCurrentRoom(SensorData sensorData) {
            roomViewModel.setRoomName(Utils.Companion.getRoomName(sensorData.getRoomId()));
            roomViewModel.setRoomId(sensorData.getRoomId());
            roomViewModel.setRoomData(new RoomCo2Data(sensorData.getCo2Value(), LocalDateTime.now()));

            // Advertisement gets executed many (~5) times within a short period. We ignore all but one value within a 2 second period.
            ignoreValuesForNextTwoSeconds = true;
            delayHandler.postDelayed(() -> ignoreValuesForNextTwoSeconds = false, 2000);

            // If no data was read for the last 10s, we assume that we've left the rum. Thus clear the current room data.
            delayHandler.postDelayed(() -> {
                if (roomViewModel.getLastUpdated().isBefore(LocalDateTime.now().minusSeconds(10))) {
                    roomViewModel.clearData();
                }
            }, 10000);
        }

        private void notifyIfDangerous(SensorData sensorData) {
            if (sensorData.getCo2Value() > Constants.DANGEROUS_CO2_THRESHOLD) {
                if (!hasNotified) {
                    showNotification();
                    // when value drops below DANGEROUS_CO2_THRESHOLD again, enable the notification again
                    hasNotified = true;
                }
            } else {
                hasNotified = false;
            }
        }

        private void checkForCovidApps(ScanResult result, ScanRecord scanRecord) {
            if (scanRecord.getServiceData().toString().contains(SWISS_COVID_APP_ID) && result.getRssi() > -90) {
                covidDeviceList.add(result.getDevice());
            }

            if (!covidDeviceOnCollect) {
                delayHandler.postDelayed(() -> {
                    covidDeviceOnCollect = false;
                    Log.i(TAG, "Found Covid Apps: " + covidDeviceList.size());
                    roomViewModel.setNumberOfCovidDevices(covidDeviceList.size());
                    covidDeviceList.clear();
                }, 4000);
                covidDeviceOnCollect = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(RoomListFragment.Companion.newInstance(), "Room List");
        adapter.addFragment(CurrentRoomFragment.Companion.newInstance(), "Current Room");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        roomViewModel = new ViewModelProvider(this).get(CurrentRoomViewModel.class);

        createNotificationChannel();
        createNotification();

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int currentItemIndex = intent.getIntExtra(EXTRA_NOTIFICATION_LOAD, -1);
        if (currentItemIndex != -1) {
            viewPager.setCurrentItem(currentItemIndex, true);
        }
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createNotification() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Give PendingIntent (MainActivity) an Extra, which enables the direct loading of the CurrentRoomFragment (Index: 1)
        // inside the TabLayout after the notification was pressed
        intent.putExtra(EXTRA_NOTIFICATION_LOAD, 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_alarm)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setLights(Color.RED, 3000, 3000)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    private void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NEED_TO_VENTILATE_NOTIFICATION_ID, notificationBuilder.build());

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }


    private void scan() {
        if (mIsScanning) {
            Log.d(TAG, "Already scanning ...");
            return;
        }

        List<ScanFilter> filters = new ArrayList<>();
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
            locationMode = Settings.Secure.getInt(
                    this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            enabled = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } catch (Settings.SettingNotFoundException e) {
            enabled = false;
        }
        return enabled;
    }

}
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrftoolbox.hygrometer;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.Menu;
import android.widget.TextView;

import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileServiceReadyActivity;
import no.nordicsemi.android.nrftoolbox.hygrometer.settings.SettingsActivity;

public class HygrometerActivity extends BleProfileServiceReadyActivity<HygrometerService. HygrometerBinder> {
	@SuppressWarnings("unused")
	private final String TAG = "HygrometerActivity";

	private TextView humidityMeasurementView;
	private TextView heaterStateView;
	private TextView batteryLevelView;

	@Override
	protected void onCreateView(final Bundle savedInstanceState) {
		setContentView(R.layout.activity_feature_hygrometer);
		humidityMeasurementView = findViewById(R.id.hygrometer_humidity_measurement);
		heaterStateView = findViewById(R.id.hygrometer_heater_state);
		batteryLevelView = findViewById(R.id.battery);
		findViewById(R.id.action_set_name).setOnClickListener(v -> {
			if (isDeviceConnected()) {
				getService().setHeaterState("true");
			}
		});
	}

	@Override
	protected void onInitialize(final Bundle savedInstanceState) {
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void setDefaultUI() {
		humidityMeasurementView.setText(R.string.not_available_value);
		heaterStateView.setText(R.string.not_available_value);
		batteryLevelView.setText(R.string.not_available);
	}

	@Override
	protected int getLoggerProfileTitle() {
		return R.string.hygrometer_feature_title;
	}

	@Override
	protected int getAboutTextId() {
		return R.string.hygrometer_about_text;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.settings_and_about, menu);
		return true;
	}

	@Override
	protected boolean onOptionsItemSelected(final int itemId) {
		switch (itemId) {
			case R.id.action_settings:
				final Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}

	@Override
	protected int getDefaultDeviceName() {
		return R.string.template_default_name;
	}

	@Override
	protected UUID getFilterUUID() {
		return HygrometerManager.HUMIDITY_SERVICE_UUID;
	}

	@Override
	protected Class<? extends BleProfileService> getServiceClass() {
		return HygrometerService.class;
	}

	@Override
	protected void onServiceBound(final HygrometerService.HygrometerBinder binder) {
		// not used
	}

	@Override
	protected void onServiceUnbound() {
		// not used
	}

	@Override
	public void onServicesDiscovered(@NonNull final BluetoothDevice device, final boolean optionalServicesFound) {
		// this may notify user or show some views
	}

	@Override
	public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
		super.onDeviceDisconnected(device);
		setDefaultUI();
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			final BluetoothDevice device = intent.getParcelableExtra(HygrometerService.EXTRA_DEVICE);
			if (HygrometerService.BROADCAST_HUMIDITY_MEASUREMENT.equals(action)) {
				final double value = intent.getDoubleExtra(HygrometerService.EXTRA_HUMIDITY_MEASUREMENT, 0.0);
				humidityMeasurementView.setText(String.valueOf(value));
			} else if (HygrometerService.BROADCAST_HEATER_STATE.equals(action)) {
				final boolean value = intent.getBooleanExtra(HygrometerService.EXTRA_HEATER_STATE, false);
				heaterStateView.setText(String.valueOf(value));
			} else if (HygrometerService.BROADCAST_BATTERY_LEVEL.equals(action)) {
				final int batteryLevel = intent.getIntExtra(HygrometerService.EXTRA_BATTERY_LEVEL, 0);
				batteryLevelView.setText(getString(R.string.battery, batteryLevel));
			}
		}
	};

	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HygrometerService.BROADCAST_HUMIDITY_MEASUREMENT);
		intentFilter.addAction(HygrometerService.BROADCAST_HEATER_STATE);
		intentFilter.addAction(HygrometerService.BROADCAST_BATTERY_LEVEL);
		return intentFilter;
	}
}

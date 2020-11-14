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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.nrftoolbox.battery.BatteryManager;
//import no.nordicsemi.android.nrftoolbox.parser.TemplateParser;
import no.nordicsemi.android.nrftoolbox.hygrometer.callback.HumidityMeasurementDataCallback;
import no.nordicsemi.android.nrftoolbox.hygrometer.callback.HeaterStateDataCallback;

public class HygrometerManager extends BatteryManager<HygrometerManagerCallbacks> {
	static final UUID HUMIDITY_SERVICE_UUID = 
	    UUID.fromString("6b750001-006c-4f1b-8e32-a20d9d19aa13");
	private static final UUID HUMIDITY_MEASUREMENT_CHARACTERISTIC_UUID = 
	    UUID.fromString("6b750002-006c-4f1b-8e32-a20d9d19aa13");
	private static final UUID HEATER_STATE_CHARACTERISTIC_UUID = 
	    UUID.fromString("6b750003-006c-4f1b-8e32-a20d9d19aa13");

    private BluetoothGattCharacteristic mHumidityMeasurementCharacteristic;
    private BluetoothGattCharacteristic mHeaterStateCharacteristic;

	public HygrometerManager(final Context context) {
		super(context);
	}

	@NonNull
	@Override
	protected BatteryManagerGattCallback getGattCallback() {
		return new HygrometerManagerGattCallback();
	}

	private class HygrometerManagerGattCallback extends BatteryManagerGattCallback {

		@Override
		protected void initialize() {
			super.initialize();
			HygrometerManager.this.requestMtu(43)
				.with((device, mtu) -> log(LogContract.Log.Level.APPLICATION, "MTU changed to " + mtu))
				.done(device -> { /* skip */ })
				.fail((device, status) -> log(Log.WARN, "MTU change not supported"))
				.enqueue();

			HygrometerManager.this.setNotificationCallback(mHumidityMeasurementCharacteristic)
				.with(new HumidityMeasurementDataCallback() {
					@Override
					public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
						log(LogContract.Log.Level.APPLICATION, data.toString()); //TemplateParser.parse(data));
						super.onDataReceived(device, data);
					}

					@Override
					public void onHumidityMeasurementValueReceived(@NonNull final BluetoothDevice device, final double value) {
						mCallbacks.onHumidityMeasurementValueReceived(device, value);
					}

					@Override
					public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
						log(Log.WARN, "Invalid data received: " + data);
					}
				});

			HygrometerManager.this.enableNotifications(mHumidityMeasurementCharacteristic)
				.with((device, data) -> log(Log.DEBUG, "Data sent: " + data))
				.done(device -> log(LogContract.Log.Level.APPLICATION, "Notifications enabled successfully"))
				.fail((device, status) -> log(Log.WARN, "Failed to enable notifications"))
				.enqueue();
		}

		@Override
		protected boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(HUMIDITY_SERVICE_UUID);
			if (service != null) {
				mHumidityMeasurementCharacteristic = 
				    service.getCharacteristic(HUMIDITY_MEASUREMENT_CHARACTERISTIC_UUID);
			    mHeaterStateCharacteristic = 
				    service.getCharacteristic(HEATER_STATE_CHARACTERISTIC_UUID);
			}
			return
				(mHumidityMeasurementCharacteristic != null) && 
				(mHeaterStateCharacteristic != null);
		}

		@Override
		protected boolean isOptionalServiceSupported(@NonNull final BluetoothGatt gatt) {
			return super.isOptionalServiceSupported(gatt);
		}

		@Override
		protected void onDeviceDisconnected() {
			super.onDeviceDisconnected();
			mHumidityMeasurementCharacteristic = null;
			mHeaterStateCharacteristic = null;
		}

		@Override
		protected void onDeviceReady() {
			super.onDeviceReady();
			HygrometerManager.this.readCharacteristic(mHeaterStateCharacteristic)
				.with((device, data) -> {
					if (data.size() > 0) {
						final Integer value = data.getIntValue(Data.FORMAT_UINT8, 0);
						log(LogContract.Log.Level.APPLICATION, "Heater state value '" + value + "' has been read!");
					} else {
						log(Log.WARN, "Value is empty!");
					}
				})
				.enqueue();
		}
	}

	void setHeaterState(final String value) {
		log(Log.VERBOSE, "Setting heater state to \"" + value + "\"");
		this.writeCharacteristic(mHeaterStateCharacteristic, Data.from(value)) // TODO
			.split() // if data > MTU
			.with((device, data) -> log(Log.DEBUG, data.size() + " bytes were sent"))
			.done(device -> log(LogContract.Log.Level.APPLICATION, "Heater state set to \"" + value + "\""))
			.fail((device, status) -> log(Log.WARN, "Failed to set heater state"))
			.enqueue();
	}

	public static UUID getUUID(){
		return HUMIDITY_SERVICE_UUID;
	}
}

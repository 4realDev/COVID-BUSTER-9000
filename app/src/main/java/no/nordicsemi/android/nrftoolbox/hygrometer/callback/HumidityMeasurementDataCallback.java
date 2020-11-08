package no.nordicsemi.android.nrftoolbox.hygrometer.callback;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class HumidityMeasurementDataCallback implements ProfileDataCallback,
	HumidityMeasurementCharacteristicCallback {

	@Override
	public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
		if (data.size() == 2) {
			int h2HiByte = data.getIntValue(Data.FORMAT_UINT8, 0);
			int h2LoByte = data.getIntValue(Data.FORMAT_UINT8, 1);
			double value = ((h2HiByte << 8) + h2LoByte) / 100.0;
			onHumidityMeasurementValueReceived(device, value);
		} else {
			onInvalidDataReceived(device, data);
		}
	}
}

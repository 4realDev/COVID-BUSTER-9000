package no.nordicsemi.android.nrftoolbox.hygrometer.callback;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class HeaterStateDataCallback implements ProfileDataCallback,
	HeaterStateCharacteristicCallback {

	@Override
	public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
		if (data.size() == 1) {
			boolean value = data.getIntValue(Data.FORMAT_UINT8, 0) != 0;
			onHeaterStateValueReceived(device, value);
		} else {
			onInvalidDataReceived(device, data);
		}
	}
}

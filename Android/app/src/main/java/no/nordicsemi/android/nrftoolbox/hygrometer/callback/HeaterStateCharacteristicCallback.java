package no.nordicsemi.android.nrftoolbox.hygrometer.callback;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

public interface HeaterStateCharacteristicCallback {
	void onHeaterStateValueReceived(@NonNull final BluetoothDevice device, boolean value);
}

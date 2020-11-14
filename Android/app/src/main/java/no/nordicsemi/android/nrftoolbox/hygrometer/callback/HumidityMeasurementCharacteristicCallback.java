package no.nordicsemi.android.nrftoolbox.hygrometer.callback;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

public interface HumidityMeasurementCharacteristicCallback {
	void onHumidityMeasurementValueReceived(@NonNull final BluetoothDevice device, double value);
}

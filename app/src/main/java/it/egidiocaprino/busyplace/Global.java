package it.egidiocaprino.busyplace;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class Global {

    static volatile String deviceId;

    public static synchronized String getDeviceId(Context context) {
        if (deviceId == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice = tm.getDeviceId();
            String tmSerial = tm.getSimSerialNumber();
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString().replace("\"", "\\\"");
        }

        return deviceId;
    }

}

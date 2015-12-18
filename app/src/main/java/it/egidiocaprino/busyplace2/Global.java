package it.egidiocaprino.busyplace2;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class Global {

    static volatile String deviceId;

    // @todo This method needs to be improved: consider tables do not have a telephony ID.
    public static synchronized String getDeviceId(Context context) {
        if (deviceId == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice = emptyIfNull(tm.getDeviceId());
            String tmSerial = emptyIfNull(tm.getSimSerialNumber());
            String androidId = emptyIfNull(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString().replace("\"", "\\\"");
        }

        return deviceId;
    }

    static String emptyIfNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

}

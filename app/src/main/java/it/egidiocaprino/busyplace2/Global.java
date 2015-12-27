package it.egidiocaprino.busyplace2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class Global {

    public static final String[] requiredPermissions = new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.READ_PHONE_STATE
    };

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

    public static boolean hasPermissions(Context context) {
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    static String emptyIfNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

}

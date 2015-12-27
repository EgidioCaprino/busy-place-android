package it.egidiocaprino.busyplace2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

public class PositionUpdate extends BroadcastReceiver {

    public static void setRepeatingIntent(Context context) {
        Intent intent = new Intent(context, PositionUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    public static Location getLastLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return location;
    }

    @Override public void onReceive(Context context, Intent intent) {
        if (Global.hasPermissions(context)) {
            String deviceId = Global.getDeviceId(context);
            Location location = getLastLocation(context);
            if (location != null) {
                new SendPositionAsyncTask().execute(deviceId, location.getLatitude(), location.getLongitude());
            }
        }
    }

}

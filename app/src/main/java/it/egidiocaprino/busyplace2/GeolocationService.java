package it.egidiocaprino.busyplace2;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public class GeolocationService {

    public double calculateRadius(VisibleRegion visibleRegion) {
        LatLng center = visibleRegion.latLngBounds.getCenter();
        LatLng northEast = visibleRegion.latLngBounds.northeast;

        LatLng radiusLatitudePoint = new LatLng(northEast.latitude, center.longitude);
        LatLng radiusLongitudePoint = new LatLng(center.latitude, northEast.longitude);

        double radiusLongitude = calculateDistance(center, radiusLongitudePoint);
        double radiusLatitude = calculateDistance(center, radiusLatitudePoint);

        return Math.min(radiusLongitude, radiusLatitude);
    }

    public double calculateDistance(LatLng a, LatLng b) {
        Location locationA = new Location(LocationManager.GPS_PROVIDER);
        Location locationB = new Location(LocationManager.GPS_PROVIDER);

        locationA.setLatitude(a.latitude);
        locationA.setLongitude(a.longitude);

        locationB.setLatitude(b.latitude);
        locationB.setLongitude(b.longitude);

        return locationA.distanceTo(locationB);
    }

}

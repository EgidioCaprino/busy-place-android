package it.egidiocaprino.busyplace2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnCountListener, ActivityCompat.OnRequestPermissionsResultCallback {

    final GeolocationService geolocationService = new GeolocationService();
    final String markerTitle = "Tap to display the count";
    final String loadingTitle = "Loading...";
    final float defaultZoom = 10;
    final int circleColor = Color.argb(100, 255, 0, 0);
    final int permissionRequestCode = 1;

    Circle circle;
    Marker marker;
    AsyncTask<Double, Void, String> countAsyncTask;
    SupportMapFragment mapFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        PositionUpdate.setRepeatingIntent(this);
        OnBootReceiver.enable(this);

        if (Global.hasPermissions(this)) {
            mapFragment.getMapAsync(this);
        } else {
            requestLocationPermissions();
        }
    }


    @Override public void onMapReady(final GoogleMap googleMap) {
        // Center in the last know location.
        Location lastLocation = PositionUpdate.getLastLocation(this);
        LatLng latLng;

        if (lastLocation != null) {
            latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        } else {
            latLng = new LatLng(51.520201, -0.104800);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));

        // Enable my location button.
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override public void onCameraChange(CameraPosition cameraPosition) {
                LatLng center = cameraPosition.target;

                Projection projection = googleMap.getProjection();
                VisibleRegion visibleRegion = projection.getVisibleRegion();
                double radius = geolocationService.calculateRadius(visibleRegion);

                if (circle == null) {
                    CircleOptions options = new CircleOptions().center(center).radius(radius);
                    circle = googleMap.addCircle(options);

                    circle.setFillColor(circleColor);
                    circle.setStrokeColor(Color.RED);
                } else {
                    circle.setCenter(center);
                    circle.setRadius(radius);
                }

                if (marker == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(center).title(markerTitle);
                    marker = googleMap.addMarker(markerOptions);
                } else {
                    marker.setPosition(center);
                    marker.setTitle(markerTitle);
                }
                marker.showInfoWindow();
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override public void onMapClick(LatLng latLng) {
                marker.setTitle(loadingTitle);
                marker.showInfoWindow();

                if (countAsyncTask != null) {
                    countAsyncTask.cancel(true);
                }

                LatLng center = googleMap.getCameraPosition().target;
                Projection projection = googleMap.getProjection();
                VisibleRegion visibleRegion = projection.getVisibleRegion();
                double radius = geolocationService.calculateRadius(visibleRegion);

                countAsyncTask = new CountAsyncTask(MapsActivity.this);
                countAsyncTask.execute(center.latitude, center.longitude, radius / 1000.0);
            }
        });
    }

    @Override public void onCount(String count) {
        String people;
        if ("1".equals(count)) {
            people = "person";
        } else {
            people = "people";
        }

        marker.setTitle(count + " " + people);
        marker.showInfoWindow();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(this)
                        .setTitle("Warning!")
                        .setMessage("You cannot use " + R.string.title_activity_maps + " if you do not grant Location permissions.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                requestLocationPermissions();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                    return;
                }
            }

            mapFragment.getMapAsync(this);
        }
    }

    void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, Global.requiredPermissions, permissionRequestCode);
    }

}

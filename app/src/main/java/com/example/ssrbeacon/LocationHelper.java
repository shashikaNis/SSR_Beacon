package com.example.ssrbeacon;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseHelper firebaseHelper;
    private Context context;

    public LocationHelper(Context context, FirebaseFirestore db) {
        this.context = context;
        this.firebaseHelper = new FirebaseHelper(db, context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setMinUpdateIntervalMillis(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        // location request aken location anakota meka thama call wenne
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Log.d("LocationHelper", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    // meken location aka firebase helper ake update location akata yawanawa
                    firebaseHelper.updateChildLocation(location.getLatitude(), location.getLongitude());
                }
            }
        };
        // methana if aka dala check karala tinne location ganna prmissions tinoda kla permision naththan location ganna aka nawaththala danawa
        // nawaththanna kalin message akak pennano
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}

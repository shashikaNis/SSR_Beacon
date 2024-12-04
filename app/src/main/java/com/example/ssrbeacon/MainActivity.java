package com.example.ssrbeacon;


import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseFirestore db;


    private Timer timer;
    private TimerTask timerTask = new TimerTask() {


        @Override
        public void run() {

        }
    };

    public void start() {
        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setMinUpdateIntervalMillis(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    // Handle the location update here
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

//        fusedLocationClient.getCurrentLocation(locationRequest,cancellationToken ).addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                Log.d("SSRLocation", location.toString());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("Location", e.toString());
//            }
//        });
//        if(timer != null) {
//            return;
//        }
//        timer = new Timer();
//        timer.scheduleAtFixedRate(timerTask, 0, 2000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("FirebaseResult", queryDocumentSnapshots.getDocuments().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseResult", e.getMessage());
            }
        });
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        start();
        EditText childCode = findViewById(R.id.et_child_code);
        Button submitButton = findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Child code", childCode.getText().toString());
            }
        });
    }
}
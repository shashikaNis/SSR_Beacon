package com.example.ssrbeacon;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private FirebaseHelper firebaseHelper;

    private LocationHelper locationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseHelper = new FirebaseHelper(db, this);
        locationHelper = new LocationHelper(this, db);

        /// me tika auto haduna awa
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // methanin pahalata thama api haduwe

        /// shared prefference aka pavichi karanne ape parent id aka save karannai gannai
        /// methanadi karala tiyenne ganna aka (get)
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String parentId = sharedPreferences.getString("parentId", null);
        String childId = sharedPreferences.getString("childId", null);
        Log.d("MainActivity", "ParentID:"+parentId+" childID:"+childId);
        // methana karala tiyenne parent id akai child id akai tiyanawada balanawa tiyanawanm witharak location ganna aka start karanna kiyala tiyenne
        if (parentId != null && childId != null) {
            locationHelper.startLocationUpdates();
        }

        EditText childCode = findViewById(R.id.et_child_code);
        EditText parentCode = findViewById(R.id.et_parent_code);
        Button submitButton = findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // me if aken karala tiyenne text box walin ana parent code akai child code akai emptyda kiyala balanne
                // code 2ka gahanne nathiwa button aka abuwoth message akak pennana dala tiyenne
                // message aka pennala athanin nawathinna thama return aka danne
                if(childCode.getText().toString().isEmpty() || parentCode.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // methanin wenne textbox walin gaththa parent code akai child code akai Firebase helper ake validateParent code kiyana function akata yawala tiyanawa
                // ctrl+ click karama a function aka tiyana thanata yanawa
                firebaseHelper.validateParentCode(parentCode.getText().toString(), childCode.getText().toString());
                Log.d("Child code", childCode.getText().toString());
            }
        });
    }
}
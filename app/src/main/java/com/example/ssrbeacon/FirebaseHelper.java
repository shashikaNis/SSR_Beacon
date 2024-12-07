package com.example.ssrbeacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class FirebaseHelper {
    private FirebaseFirestore db;
    private Context context;

    public FirebaseHelper(FirebaseFirestore db, Context context) {
        this.db = db;
        this.context = context;
    }

    // meken wenne parent id ekai child id akai phone ake save kara gannawa

    private void saveToSharedPrences(String parentId, String childId) {
       // methanadi shared prefference aka use karala tiyenne parent id akai child id akai save karanna (put)
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parentId", parentId);
        editor.putString("childId", childId);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void logError(Exception e, String message) {
        Log.e("FirebaseResult", message + ":" + e.getMessage());
    }

    /// Meken wenne child code aken childwa hoya gannawa
    // child inne parent collection aka athulene
    // athakota parent id aken parent collection akata ghin athanin code akata galapena child hoya gannawa

    private void validateChildIdCode(String parentId, String childCodText) {
        db.collection("users").document(parentId).collection("children")
                .whereEqualTo("childCode", childCodText).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showToast("Invalid child code");
                        return;
                    }

                    String childId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    saveToSharedPrences(parentId, childId);
                    showToast("Success");
                })
                .addOnFailureListener(e -> logError(e, "Faild to validate child code"));
    }

    public void validateParentCode(String parentCodeText, String childCodeText) {
        db.collection("users").whereEqualTo("parentCode", parentCodeText).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showToast("Invalid parent code");
                        return;
                    }
                    String parentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    validateChildIdCode(parentId, childCodeText);
                })
                .addOnFailureListener(e -> logError(e, "Faild to validate parent code"));
    }

    /// Meken wenne childge location eka update karanawa

    public void updateChildLocation( double latitude, double longitude) {
        Log.d("FirebaseResult", "Updating location");

        // methanadith shared pereference aka use karala tiyenne parent id akai child id akai ganna (get)
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String parentId = sharedPreferences.getString("parentId", "");
        String childId = sharedPreferences.getString("childId", "");
        GeoPoint location = new GeoPoint(latitude, longitude);

        Log.d("FirebaseResult", "Parent ID: " + parentId + ", Child ID: " + childId);

        db.collection("users").document(parentId)
                .collection("children").document(childId)
                .update("location", location).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseResult", "Location updated successfully");
                        } else {
                            Log.e("FirebaseResult", "Failed to update location: " + task.getException().getMessage());
                        }
                    }});
    }
}

package com.technician.maintainmore;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AssignedServiceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AssignedServiceDetailsActivityInfo";

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String whoBookedService, bookingID;
    String lat, lng;
    
    TextView displayUserName, displayUserEmail, displayUserPhoneNumber, displayUserAddress;
    TextView displayServiceName, displayServiceDescription, displayUserTotalServices, displayRequiredPerServiceTime;
    TextView displayPrisePerService, displayTotalPrice, displayBookingDate, displayBookingTime, displayVisitingDate, displayVisitingTime;
    
    Button buttonCancel, buttonServiceCompleted;
    ImageButton buttonShowDirection;


//   a String userName, userEmail, userPhoneNumber, userAddress;
//   b String serviceName, serviceDescription, totalServices,requiredTimePerService;
//   c String pricePerService, totalPrice, bookingDate, bookingTime, visitingDate, visitingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_service_details);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        
        LinkedIDes();
        GetDataFromPreviousActivity();
        GetDataFromDataBase();


        buttonShowDirection.setOnClickListener(view -> ShowDirection());
        
        buttonCancel.setOnClickListener(view -> finish());
        buttonServiceCompleted.setOnClickListener(view -> ServiceCompleted());

    }

    @SuppressLint("LongLogTag")
    private void ShowDirection() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + lat +","+ lng + "&mode=l"));
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(this, "Please install Google Maps for this service", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    
    @SuppressLint("LongLogTag")
    private void ServiceCompleted() {
        db.collection("Bookings").document(bookingID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    db.collection("Bookings Completed").document(bookingID)
                            .set(Objects.requireNonNull(document.getData()));
                    Log.d(TAG, "DocumentSnapshot data: " + document.getString("assignedTechnician"));

                    db.collection("Bookings Completed").document(bookingID)
                            .update("bookingStatus", "Completed");

                    db.collection("Technicians")
                            .document(Objects.requireNonNull(document.getString("assignedTechnician")))
                            .update("availabilityStatus","Free").addOnCompleteListener(task1 ->
                            Log.d(TAG, "update")).addOnFailureListener(e -> Log.d(TAG, "ere: " + e.getMessage()));

                    db.collection("Bookings").document(bookingID).delete().addOnSuccessListener(unused -> {
                        Log.i(TAG, "BookingCanceled");

                        Toast.makeText(this, "Service Completed", Toast.LENGTH_SHORT).show();
                        
                        finish();
                    });
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void GetDataFromDataBase() {

        db.collection("Users").document(whoBookedService).addSnapshotListener(((value, error) -> {

            if (error != null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            if (value != null && value.exists()){
                displayUserName.setText(value.getString("name"));
                displayUserEmail.setText(value.getString("email"));
                displayUserPhoneNumber.setText(value.getString("phoneNumber"));
                displayUserAddress.setText(value.getString("address"));
                
                lat = String.valueOf(value.getDouble("latitude"));
                lng = String.valueOf(value.getDouble("longitude"));
            }
        }));

        db.collection("Bookings").document(bookingID).addSnapshotListener(((value, error) -> {

            if (error != null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            if (value != null && value.exists()){
                displayServiceName.setText(value.getString("serviceName"));
                displayServiceDescription.setText(value.getString("serviceDescription"));
                displayUserTotalServices.setText(value.getString("totalServices"));
                displayRequiredPerServiceTime.setText(value.getString("requiredTime"));
                displayPrisePerService.setText(value.getString("servicePrice"));
                displayTotalPrice.setText(value.getString("totalServicesPrice"));
                displayBookingDate.setText(value.getString("bookingDate"));
                displayBookingTime.setText(value.getString("bookingTime"));
                displayVisitingDate.setText(value.getString("visitingDate"));
                displayVisitingTime.setText(value.getString("visitingTime"));
            }
        }));
    }


    private void GetDataFromPreviousActivity() {
        Intent intent = getIntent();
        whoBookedService = intent.getStringExtra("whoBookedService");
        bookingID = intent.getStringExtra("bookingID");
    }

    private void LinkedIDes() {

        displayUserName = findViewById(R.id.displayUserName);
        displayUserEmail = findViewById(R.id.displayUserEmail);
        displayUserPhoneNumber = findViewById(R.id.displayUserPhoneNumber);
        displayUserAddress = findViewById(R.id.displayUserAddress);

        displayServiceName = findViewById(R.id.displayServiceName);
        displayServiceDescription = findViewById(R.id.displayServiceDescription);
        displayUserTotalServices = findViewById(R.id.displayUserTotalServices);
        displayRequiredPerServiceTime = findViewById(R.id.displayRequiredPerServiceTime);

        displayPrisePerService = findViewById(R.id.displayPrisePerService);
        displayTotalPrice = findViewById(R.id.displayTotalPrice);
        displayBookingDate = findViewById(R.id.displayBookingDate);
        displayBookingTime = findViewById(R.id.displayBookingTime);
        displayVisitingDate = findViewById(R.id.displayVisitingDate);
        displayVisitingTime = findViewById(R.id.displayVisitingTime);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonServiceCompleted = findViewById(R.id.buttonServiceCompleted);

        buttonShowDirection = findViewById(R.id.buttonShowDirection);

    }
}
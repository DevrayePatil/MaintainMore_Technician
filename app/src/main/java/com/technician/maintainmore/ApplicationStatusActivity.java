package com.technician.maintainmore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ApplicationStatusActivity extends AppCompatActivity {


    String technicianID;

    FirebaseStorage firebaseStorage;
    FirebaseUser technician;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db ;
    DocumentReference documentReference;


    Toolbar toolbar;

    TextView displayName, displayEmail;
    ImageView profilePicture;
    Button buttonSignOut;


    protected void onStart() {

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        technician = firebaseAuth.getCurrentUser();
        technicianID = Objects.requireNonNull(technician).getUid();

        if (technician !=null) {

            technicianID = Objects.requireNonNull(technician).getUid();

            db.collection("Technicians").document(technicianID).
                    addSnapshotListener((value, error) ->{

                        if (value != null && Objects.requireNonNull(value.getString("approvalStatus")).equals("Approved")) {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                    });

        }

        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_status);

        firebaseAuth = FirebaseAuth.getInstance();
        technician = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        technicianID = Objects.requireNonNull(technician).getUid();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        displayName = findViewById(R.id.displayName);
        displayEmail = findViewById(R.id.displayEmail);

        profilePicture = findViewById(R.id.profilePicture);
        buttonSignOut = findViewById(R.id.buttonSignOut);

        buttonSignOut.setOnClickListener(view -> SignOut());

        LoginUserInfo();
    }

    private void LoginUserInfo() {

        documentReference = db.collection("Technicians").document(technicianID);

        documentReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            if (value != null && value.exists()){
                displayName.setText(value.getString("name"));
                displayEmail.setText(value.getString("email"));
                Glide.with(getApplicationContext()).load(value.getString("imageUrl"))
                        .placeholder(R.drawable.ic_person).into(profilePicture);
            }
        });
    }


    private void SignOut(){

        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }

}
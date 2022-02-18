package com.technician.maintainmore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CompleteProfileActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_ID = 0;
    private static final int CERTIFICATE_IMAGE_REQUEST_ID = 1;
    private static final String TAG = "CompleteProfileActivityInfo";
    String chooseGender;
    String serviceCertificateUrl = "";


    String technicianID;

    Uri uri, uriCertificate;
    FirebaseStorage firebaseStorage;
    FirebaseUser technician;
    StorageReference storageReference;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db ;

    DocumentReference documentReference;

    Toolbar toolbar;


    TextView displayName, displayEmail;
    ImageView profilePicture, serviceCertificate;
    TextInputEditText fullName, email, phoneNumber, dateOfBirth;
    ImageButton buttonChooseIcon;
    Button buttonChooseCertificate;
    AutoCompleteTextView chooseTechnicalRole;

    Button buttonCancel, buttonSave;


    RadioGroup radioGroup;
    RadioButton radioButton;


    protected void onStart() {

        technician = firebaseAuth.getCurrentUser();

        if (technician !=null) {

            technicianID = Objects.requireNonNull(technician).getUid();

            db.collection("Technicians").document(technicianID).
                    addSnapshotListener((value, error) ->{

                        if (value != null) {
                            if (value.getString("serviceCertificate") == null
                                    || value.getString("phoneNumber") == null
                                    || value.getString("gender") == null
                                    || value.getString("technicalRole") == null
                                    ) {
                                return;
                            }

                            if (Objects.requireNonNull(value.getString("approvalStatus")).equals("Approved")){
                                startActivity(new Intent(this, MainActivity.class));
                            }
                            else {
                                startActivity(new Intent(this, ApplicationStatusActivity.class));
                            }

                        }

                    });

        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        displayName = findViewById(R.id.displayName);
        displayEmail = findViewById(R.id.displayEmail);

        profilePicture = findViewById(R.id.profilePicture);

        serviceCertificate = findViewById(R.id.serviceCertificate);

        fullName = findViewById(R.id.textInputLayout_FullName);
        email = findViewById(R.id.textInputLayout_Email);
        phoneNumber = findViewById(R.id.textInputLayout_Phone);
        dateOfBirth = findViewById(R.id.textInputLayout_DOB);

        radioGroup = findViewById(R.id.radioGroup);
        chooseTechnicalRole = findViewById(R.id.chooseTechnicalRole);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);


        email.setEnabled(false);


        buttonChooseIcon = findViewById(R.id.buttonChangePicture);
        buttonChooseCertificate = findViewById(R.id.buttonChooseServiceCertificate);

        buttonChooseIcon.setOnClickListener(view -> ChooseIcon());
        buttonChooseCertificate.setOnClickListener(view -> ChooseCertificateImage());

        buttonCancel.setOnClickListener(view -> finishAffinity());
        buttonSave.setOnClickListener(view -> SaveInformationToDB());

        LoginUserInfo();
        InformationFromDB();


        List<String> choose_role = Arrays.asList(getResources().getStringArray(R.array.technical_roles));

        ArrayAdapter<? extends String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.drop_down_choose_role, choose_role);
        chooseTechnicalRole.setAdapter(arrayAdapter);

    }

    private void SaveInformationToDB() {


        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonID);
        chooseGender = radioButton.getText().toString();

        email.setEnabled(false);

        String FullName = Objects.requireNonNull(fullName.getText()).toString();
//        String EmailID = email.getText().toString();
        String PhoneNumber = Objects.requireNonNull(phoneNumber.getText()).toString();
        String DOB = Objects.requireNonNull(dateOfBirth.getText()).toString();
        String technicalRole = Objects.requireNonNull(chooseTechnicalRole.getText()).toString();


        if (FullName.equals("")){
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PhoneNumber.equals("")){
            Toast.makeText(this, "Please Enter your Phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DOB.equals("")){
            Toast.makeText(this, "Please Enter your Date of Birth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (serviceCertificateUrl.equals("")){
            Toast.makeText(this, "Please Upload Certificate", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("Technicians").document(technicianID).update(
                "name", FullName,
                "gender", chooseGender,
                "phoneNumber", PhoneNumber,
                "dob", DOB,
                "technicalRole", technicalRole,
                "serviceCertificate", serviceCertificateUrl

        ).addOnSuccessListener(unused ->
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to create link" + e,
                                Toast.LENGTH_SHORT).show());

    }


    private void InformationFromDB() {

        RadioButton radioButtonMale, radioButtonFemale;

        radioButtonMale = findViewById(R.id.radio_button_1);
        radioButtonFemale = findViewById(R.id.radio_button_2);


        documentReference = db.collection("Technicians").document(technicianID);

        documentReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            if (value != null && value.exists()){
                fullName.setText(value.getString("name"));
                email.setText(value.getString("email"));
                phoneNumber.setText(value.getString("phoneNumber"));
                dateOfBirth.setText(value.getString("dob"));
                serviceCertificateUrl = value.getString("serviceCertificate");

                Glide.with(getApplicationContext()).load(value.getString("serviceCertificate"))
                        .placeholder(R.drawable.ic_person).into(serviceCertificate);

                String genderValue = value.getString("gender");

                if (Objects.equals(genderValue, "Male")){
                    radioButtonMale.setChecked(true);
                }else {
                    radioButtonFemale.setChecked(true);
                }
            }
        });
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


    private void ChooseCertificateImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Image"),CERTIFICATE_IMAGE_REQUEST_ID);
    }

    private void ChooseIcon(){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Choose Image"),IMAGE_REQUEST_ID);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK && data != null &
                (data != null ? data.getData() : null) != null){
            uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                profilePicture.setImageBitmap(bitmap);

                String randomID = UUID.randomUUID().toString();

                storageReference = storageReference.child("Service Pictures/" + randomID);
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                            db.collection("Technician").document(technicianID)
                                    .update("imageUrl",String.valueOf(uri))
                            );
                    Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();

                })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        if (requestCode == CERTIFICATE_IMAGE_REQUEST_ID && resultCode == RESULT_OK && data != null &
                (data != null ? data.getData() : null) != null){
            uriCertificate = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriCertificate);
                serviceCertificate.setImageBitmap(bitmap);

                String randomID = UUID.randomUUID().toString();

                storageReference = storageReference.child("Service Pictures/" + randomID);
                storageReference.putFile(uriCertificate).addOnSuccessListener(taskSnapshot -> {

                    storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                            serviceCertificateUrl = String.valueOf(uri));
                    Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }


    }



    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_exit);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finishAffinity());
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();


    }

}
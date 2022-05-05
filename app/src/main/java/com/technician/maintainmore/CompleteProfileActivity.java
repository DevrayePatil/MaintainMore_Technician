package com.technician.maintainmore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
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
import java.util.regex.Pattern;

public class CompleteProfileActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_ID = 0;
    private static final int CERTIFICATE_IMAGE_REQUEST_ID = 1;
    private static final String TAG = "CompleteProfileActivityInfo";

    String selectGender = "";
    String serviceCertificateUrl;

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
    RadioButton radioButtonMale, radioButtonFemale;


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

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        Log.i(TAG, "CompleteActivity");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinkIDes();

        email.setEnabled(false);

        buttonChooseIcon.setOnClickListener(view -> ChooseIcon());
        dateOfBirth.setOnClickListener(view -> DatePickerDateOfBirth());
        buttonChooseCertificate.setOnClickListener(view -> ChooseCertificateImage());

        buttonCancel.setOnClickListener(view -> firebaseAuth.signOut());
        buttonSave.setOnClickListener(view -> SaveInformationToDB());

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radioButtonMale) {
                selectGender = radioButtonMale.getText().toString();
            } else if (i == R.id.radioButtonFemale) {
                selectGender = radioButtonFemale.getText().toString();
            }
        });

        LoginUserInfo();
        InformationFromDB();

        List<String> choose_role = Arrays.asList(getResources().getStringArray(R.array.technical_roles));

        ArrayAdapter<? extends String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.drop_down_choose_role, choose_role);
        chooseTechnicalRole.setAdapter(arrayAdapter);

    }


    private void DatePickerDateOfBirth() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
//        constraintBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Choose Date");
        builder.setSelection(today);
        builder.setCalendarConstraints(constraintBuilder.build());


        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKET");
        materialDatePicker.addOnPositiveButtonClickListener(selection ->
                dateOfBirth.setText(materialDatePicker.getHeaderText())
        );
    }

    private void SaveInformationToDB() {

        String FullName = Objects.requireNonNull(fullName.getText()).toString();
//        String EmailID = email.getText().toString();
        String PhoneNumber = Objects.requireNonNull(phoneNumber.getText()).toString();
        String DOB = Objects.requireNonNull(dateOfBirth.getText()).toString();
        String technicalRole = Objects.requireNonNull(chooseTechnicalRole.getText()).toString();

        Pattern patternMobileNumber = Pattern.compile("(0/91)?[6-9][0-9]{9}");



        if (FullName.equals("")){
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectGender.equals("")){
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PhoneNumber.equals("")){
            Toast.makeText(this, "Please Enter your Phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PhoneNumber.matches(String.valueOf(patternMobileNumber))){
            phoneNumber.setError("Please Enter Valid Number");
            return;
        }
        if (technicalRole.equals("")){
            Toast.makeText(this, "Please Choose technical Role", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DOB.equals("")){
            Toast.makeText(this, "Please Enter your Date of Birth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (serviceCertificateUrl == null) {
            Toast.makeText(this, "Please Choose Certificate", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (serviceCertificateUrl.equals("")) {
            Toast.makeText(this, "Please Upload Certificate", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("Technicians").document(technicianID).update(
                "name", FullName,
                "gender", selectGender,
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
                }else if (Objects.equals(genderValue, "Female")){
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


    private void LinkIDes() {
        displayName = findViewById(R.id.displayName);
        displayEmail = findViewById(R.id.displayEmail);

        profilePicture = findViewById(R.id.profilePicture);

        serviceCertificate = findViewById(R.id.serviceCertificate);

        fullName = findViewById(R.id.textInputLayout_FullName);
        email = findViewById(R.id.textInputLayout_Email);
        phoneNumber = findViewById(R.id.textInputLayout_Phone);
        dateOfBirth = findViewById(R.id.textInputLayout_DOB);

        radioGroup = findViewById(R.id.radioGroup);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);


        chooseTechnicalRole = findViewById(R.id.chooseTechnicalRole);

        buttonChooseIcon = findViewById(R.id.buttonChangePicture);
        buttonChooseCertificate = findViewById(R.id.buttonChooseServiceCertificate);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);
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

                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed"+ e, Toast.LENGTH_SHORT).show());
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
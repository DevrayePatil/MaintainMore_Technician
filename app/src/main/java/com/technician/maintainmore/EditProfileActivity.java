package com.technician.maintainmore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.util.Objects;
import java.util.regex.Pattern;


public class EditProfileActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_ID = 1;
    private static final String TAG = "EditProfileActivityInfo";
    String checkedName;

    String technicianID;

    Uri uri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db ;

    DocumentReference documentReference;




    TextView displayName, displayEmail;
    ImageView profilePicture;
    TextInputEditText fullName, email, phoneNumber, dateOfBirth;

    Button buttonCancel, buttonSave;
    ImageButton buttonChangePicture;

    RadioGroup radioGroup;
    RadioButton radioButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();




        displayName = findViewById(R.id.displayName);
        displayEmail = findViewById(R.id.displayEmail);

        profilePicture = findViewById(R.id.profilePicture);

        fullName = findViewById(R.id.textInputLayout_FullName);
        email = findViewById(R.id.textInputLayout_Email);
        phoneNumber = findViewById(R.id.textInputLayout_Phone);
        dateOfBirth = findViewById(R.id.textInputLayout_DOB);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);
        buttonChangePicture = findViewById(R.id.buttonChangePicture);

        radioGroup = findViewById(R.id.radioGroup);


        buttonCancel.setOnClickListener(view -> finish());
        buttonSave.setOnClickListener(view -> UpdatePersonalInformation());
        buttonChangePicture.setOnClickListener(view -> ChangePicture());

        dateOfBirth.setShowSoftInputOnFocus(false);
        dateOfBirth.setOnClickListener(view -> DatePickerDateOfBirth());





        if (firebaseUser!=null) {
            LoginUserInfo();
        }

        PersonalInformation();


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

    private void UpdatePersonalInformation() {


        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonID);
        checkedName = radioButton.getText().toString();

        email.setEnabled(false);

        String FullName = Objects.requireNonNull(fullName.getText()).toString();
//        String EmailID = email.getText().toString();
        String PhoneNumber = Objects.requireNonNull(phoneNumber.getText()).toString();
        String DOB = Objects.requireNonNull(dateOfBirth.getText()).toString();

        Pattern patternMobileNumber = Pattern.compile("(0/91)?[6-9][0-9]{9}");



        if (FullName.equals("")){
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
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
        if (DOB.equals("")){
            Toast.makeText(this, "Please Enter your Date of Birth", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("Technicians").document(technicianID).update(
                "name", FullName,
                "gender", checkedName,
                "phoneNumber", PhoneNumber,
                "dob", DOB


        ).addOnSuccessListener(unused ->
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to create link" + e,
                                Toast.LENGTH_SHORT).show());

    }



    private void setImageURL(String uri) {

        db.collection("Technicians").document(technicianID).update(
                "imageUrl", uri
        ).addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "link created", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to create link" + e, Toast.LENGTH_SHORT).show());

        Log.i(TAG, "Saved link: " + uri);
    }
    private void PersonalInformation() {

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

                String technicianGender = value.getString("gender");

                if (Objects.equals(technicianGender, "Male")){
                    radioButtonMale.setChecked(true);
                }else if(Objects.equals(technicianGender, "Female")){
                    radioButtonFemale.setChecked(true);
                }
            }
        });

    }

    private void ChangePicture(){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Choose Image"),IMAGE_REQUEST_ID);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK && data != null &
                (data != null ? data.getData() : null) != null){
            uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                profilePicture.setImageBitmap(bitmap);

                storageReference = storageReference.child("Profile Pictures/" + technicianID);
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> setImageURL(String.valueOf(uri)));

                    Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
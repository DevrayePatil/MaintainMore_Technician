package com.technician.maintainmore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    String technicianID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseUser technician;

    EditText EmailId, Password;
    Button buttonLogin, buttonSignup;

    @Override
    protected void onStart() {

        technician = firebaseAuth.getCurrentUser();

        if (technician !=null) {

            technicianID = Objects.requireNonNull(technician).getUid();

            db.collection("Technicians").document(technicianID).
                    addSnapshotListener((value, error) ->{

                        if (value != null) {
                            if (value.getString("serviceCertificate") == null
                            || value.getString("phoneNumber") == null
                            || value.getString("technicalRole") == null
                            ) {
                                startActivity(new Intent(this, CompleteProfileActivity.class));
                            }
                            else {

                                if (Objects.requireNonNull(value.getString("approvalStatus")).equals("Approved")){
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                                else {
                                    startActivity(new Intent(this, ApplicationStatusActivity.class));
                                }
                            }
                        }

                    });

        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EmailId = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);

        buttonLogin= findViewById(R.id.buttonLogin);
        buttonSignup= findViewById(R.id.buttonSignup);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonLogin.setOnClickListener(view -> SignIn());
        buttonSignup.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));




    }

    public void SignIn() {

        String emailId = EmailId.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (TextUtils.isEmpty(emailId)){
            EmailId.setError("Please Enter EmailId");
            return;
        }
        if (TextUtils.isEmpty(password)){
            Password.setError("Please Enter Password");

            return;
        }


//        progressBar.setVisibility(View.VISIBLE);
        final SweetAlertDialog progressDialog= new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
//                        progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

//                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        SweetAlertDialog sweetAlertDialog= new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Login Successful");
                        sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> {
                            startActivity(new Intent(getApplicationContext(), CompleteProfileActivity.class));
                            finish();
                            sweetAlertDialog1.dismissWithAnimation();
                        }).setCanceledOnTouchOutside(false);
                        sweetAlertDialog.show();



                    } else {

//                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.ERROR_TYPE).setTitleText(Objects.requireNonNull(task.getException()).getMessage()).show();
                    }
                });

    }

}
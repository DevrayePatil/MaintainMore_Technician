package com.technician.maintainmore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup";

    String EmailIdPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    String notificationToken;



    Button buttonLogin, buttonSignup;
    Toolbar toolbar;

    EditText fullName, email, password, rePassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        fullName = findViewById(R.id.editText_fullName);
        email = findViewById(R.id.editText_email);
        password = findViewById(R.id.editText_password);
        rePassword = findViewById(R.id.editText_rePassword);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    notificationToken = task.getResult();
                });


        buttonSignup.setOnClickListener(view -> signUp());
        buttonLogin.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finishAffinity();
        });

    }

    public void signUp() {

        String userName = fullName.getText().toString().trim();
        String emailId = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String conformPassword = rePassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)){
            fullName.setError("Please Enter UserName");
            return;
        }
        else if (TextUtils.isEmpty(emailId)){
            email.setError("Please Enter EmailId");
            return;
        }
        else if (!emailId.matches(EmailIdPattern)){
            email.setError("Please enter valid EmailId");
            return;
        }
        else if (TextUtils.isEmpty(Password)){
            password.setError("Please Enter Password");
            return;
        }
        else if (TextUtils.isEmpty(conformPassword)){
            rePassword.setError("Please Re-Enter Password");
            return;
        }
        else if (password.length()<6){
            password.setError("Password is too week");
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);

        final SweetAlertDialog progressDialog= new SweetAlertDialog(SignupActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (Password.equals(conformPassword)){
            Log.e(TAG,"error");
            firebaseAuth.createUserWithEmailAndPassword(emailId, Password)
                    .addOnCompleteListener(this, task -> {

//                            progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
//                                Toast.makeText(SignUp.this, "Successful", Toast.LENGTH_SHORT).show();

                            String userID = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();

                            Map<String, String> technician = new HashMap<>();
                            technician.put("name", userName);
                            technician.put("email", emailId);
                            technician.put("password", Password);
                            technician.put("walletBalanceInINR","0");
                            technician.put("approvalStatus","Registered");
                            technician.put("notificationToken",notificationToken);
                            technician.put("availabilityStatus","Free");

                            db.collection("Technicians").document(userID).set(technician)
                                    .addOnSuccessListener(unused ->
                                            new SweetAlertDialog(SignupActivity.this,SweetAlertDialog.SUCCESS_TYPE).setTitleText("SignUp Successful")
                                    .setConfirmClickListener(sweetAlertDialog -> {

                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        sweetAlertDialog.dismissWithAnimation();

                                    }).show())
                            .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "failed"+e, Toast.LENGTH_SHORT).show());



                        } else {

//                                Toast.makeText(SignUp.this, "failed", Toast.LENGTH_SHORT).show();
                            new SweetAlertDialog(SignupActivity.this,SweetAlertDialog.ERROR_TYPE).setTitleText(Objects.requireNonNull(task.getException()).getMessage()).show();

                        }


                    });
        }
        else {
//            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            rePassword.setError("Password Doesn't Match");
        }
    }
}
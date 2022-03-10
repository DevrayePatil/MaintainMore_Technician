package com.technician.maintainmore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.technician.maintainmore.Fragments.BookingsFragment;
import com.technician.maintainmore.Fragments.HomeFragment;
import com.technician.maintainmore.Fragments.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityInfo";

    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");


        setSupportActionBar(toolbar);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        linearLayout = findViewById(R.id.fragmentContainer);
        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelected);




        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
        fragmentTransaction.commit();
    }


    public BottomNavigationView.OnNavigationItemSelectedListener itemSelected = item -> {

        Fragment setFragment = null;


        if (item.getItemId() == R.id.home){
            setFragment = new HomeFragment();
            Log.i(TAG,"Home Clicked");


        }
        else if(item.getItemId() == R.id.booking){
            setFragment = new BookingsFragment();
            Log.i(TAG,"Booking Clicked");
        }
        else if(item.getItemId() == R.id.profile){
            setFragment = new ProfileFragment();
            Log.i(TAG,"Profile Clicked");

        }


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        assert setFragment != null;
        fragmentTransaction.replace(R.id.fragmentContainer, setFragment);
        fragmentTransaction.commit();

        return true;
    };

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
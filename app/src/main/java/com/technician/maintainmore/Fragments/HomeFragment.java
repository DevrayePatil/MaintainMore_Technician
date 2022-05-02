package com.technician.maintainmore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technician.maintainmore.R;

import java.util.Objects;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragmentInfo";


    FirebaseFirestore db;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String technicianID;

    TextView displayNumberOfAssignedBookings, displayNumberOfCompletedBookings;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();


        displayNumberOfAssignedBookings = view.findViewById(R.id.displayNumberOfAssignedBookings);
        displayNumberOfCompletedBookings = view.findViewById(R.id.displayNumberOfCompletedBookings);


        db.collection("Bookings").whereEqualTo("assignedTechnician", technicianID)
                .whereEqualTo("bookingStatus", "Booked")
                .addSnapshotListener((value, error) -> {
                    assert value != null;

                    Log.d(TAG, String.valueOf(value.size()));
                    displayNumberOfAssignedBookings.setText(String.valueOf(value.size()));

                });

        db.collection("Bookings Completed").whereEqualTo("assignedTechnician", technicianID)
                .whereEqualTo("bookingStatus", "Completed")
                .addSnapshotListener((value, error) -> {
                    assert value != null;

//                    Toast.makeText(requireActivity(), "size"  + value.size(), Toast.LENGTH_SHORT).show();
                    displayNumberOfCompletedBookings.setText(String.valueOf(value.size()));


                });

        return view;
    }
}
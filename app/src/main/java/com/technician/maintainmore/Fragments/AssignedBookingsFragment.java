package com.technician.maintainmore.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.technician.maintainmore.Adapters.AssignedServiceAdapter;
import com.technician.maintainmore.AssignedServiceDetailsActivity;
import com.technician.maintainmore.Modals.AssignedServiceModal;
import com.technician.maintainmore.R;

import java.util.ArrayList;
import java.util.Objects;


public class AssignedBookingsFragment extends Fragment implements AssignedServiceAdapter.viewHolder.OnAssignedServiceClickListener{

//    private static final String TAG = "AssignedBookingsFragmentInfo";

    String technicianID;

    RecyclerView recyclerView_assignedServices;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public AssignedBookingsFragment() {
        // Required empty public constructor
    }

    ArrayList<AssignedServiceModal> serviceModals = new ArrayList<>();

    String whoBookedService, userName, userEmail, userPhoneNumber;
    String bookingID, serviceIconUrl, serviceName, totalServices,servicePrice, totalServicesPrice;
    String bookingDate, bookingTime, visitingDate, visitingTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assigned_bookings, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();


        recyclerView_assignedServices = view.findViewById(R.id.recyclerView_assignedServices);


        db.collection("Bookings").whereEqualTo("assignedTechnician",technicianID).addSnapshotListener((value, error) -> {
            serviceModals.clear();
            assert value != null;
            for (DocumentSnapshot snapshot: value) {

                if (Objects.equals(snapshot.getString("bookingStatus"), "Booked")) {

                    whoBookedService = snapshot.getString("whoBookedService");
                    assert whoBookedService != null;

                    db.collection("Users").document(whoBookedService).addSnapshotListener((value1, error1) -> {
                        if (error1 != null) {
                            Toast.makeText(getActivity(), "Error" + error1, Toast.LENGTH_SHORT).show();
                            Log.i("Error:", error1.getMessage());
                        }
                        if (value1 != null && value1.exists()) {
                            userName = value1.getString("name");
                            userEmail = value1.getString("email");
                            userPhoneNumber = value1.getString("phoneNumber");

                        }
                    });

                    bookingID = snapshot.getId();
                    serviceIconUrl = snapshot.getString("serviceIcon");
                    serviceName = snapshot.getString("serviceName");
                    totalServices = snapshot.getString("totalServices");
                    servicePrice = snapshot.getString("servicePrice");
                    totalServicesPrice = snapshot.getString("totalServicesPrice");

                    bookingDate = snapshot.getString("bookingDate");
                    bookingTime = snapshot.getString("bookingTime");
                    visitingDate = snapshot.getString("visitingDate");
                    visitingTime = snapshot.getString("visitingTime");


                    Toast.makeText(requireActivity(), "name :" + userName, Toast.LENGTH_SHORT).show();

                    serviceModals.add(new AssignedServiceModal(
                            whoBookedService, userName, userEmail, userPhoneNumber,
                            bookingID, serviceIconUrl, serviceName,
                            totalServices, servicePrice,
                            totalServicesPrice, bookingDate,
                            bookingTime, visitingDate,
                            visitingTime
                    ));

                }
            }
            AssignedServiceAdapter serviceAdapter = new AssignedServiceAdapter(serviceModals, getContext(), this);
            recyclerView_assignedServices.setAdapter(serviceAdapter);

        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_assignedServices.setLayoutManager(linearLayoutManager);


        return view;
    }

    @Override
    public void onAssignedServiceCardClickListener(int position) {

        String whoBookedService = serviceModals.get(position).getWhoBookedService();
        String bookingID = serviceModals.get(position).getBookingID();

        Intent intent = new Intent(requireActivity(), AssignedServiceDetailsActivity.class);

        intent.putExtra("whoBookedService", whoBookedService);
        intent.putExtra("bookingID", bookingID);

        startActivity(intent);

    }
}
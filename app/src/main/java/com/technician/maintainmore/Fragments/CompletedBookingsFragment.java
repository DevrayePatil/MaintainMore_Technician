package com.technician.maintainmore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technician.maintainmore.Adapters.AssignedServiceAdapter;
import com.technician.maintainmore.Modals.AssignedServiceModal;
import com.technician.maintainmore.R;

import java.util.ArrayList;
import java.util.Objects;


public class CompletedBookingsFragment extends Fragment
    implements AssignedServiceAdapter.viewHolder.OnAssignedServiceClickListener{

    String technicianID;

    RecyclerView recyclerView_completedServices;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public CompletedBookingsFragment() {
        // Required empty public constructor
    }

    ArrayList<AssignedServiceModal> serviceModals = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_completed_bookings, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        technicianID = Objects.requireNonNull(firebaseUser).getUid();

        recyclerView_completedServices = view.findViewById(R.id.recyclerView_completedServices);

        db.collection("Bookings Completed").whereEqualTo("assignedTechnician",technicianID)
                .addSnapshotListener((value, error) -> {
            serviceModals.clear();
            assert value != null;
            for (DocumentSnapshot snapshot: value) {
                if (Objects.equals(snapshot.getString("bookingStatus"), "Completed")) {

                    serviceModals.add(new AssignedServiceModal(
                    snapshot.getId(), snapshot.getString("whoBookedService"),
                    snapshot.getString("serviceIcon"), snapshot.getString("serviceName"),
                    snapshot.getString("serviceDescription"),
                    snapshot.getString("totalServices"), snapshot.getString("servicePrice"),
                    snapshot.getString("totalServicesPrice"),
                    snapshot.getString("bookingDate"), snapshot.getString("bookingTime"),
                    snapshot.getString("visitingDate"), snapshot.getString("visitingTime")
                    ));
                }
            }
            AssignedServiceAdapter serviceAdapter = new AssignedServiceAdapter(serviceModals, getContext(), this);
            recyclerView_completedServices.setAdapter(serviceAdapter);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_completedServices.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onAssignedServiceCardClickListener(int position) {

    }
}
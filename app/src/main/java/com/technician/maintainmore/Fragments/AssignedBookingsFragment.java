package com.technician.maintainmore.Fragments;

import android.content.Intent;
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
import com.technician.maintainmore.AssignedServiceDetailsActivity;
import com.technician.maintainmore.Modals.AssignedServiceModal;
import com.technician.maintainmore.R;

import java.util.ArrayList;
import java.util.Objects;


public class AssignedBookingsFragment extends Fragment
        implements AssignedServiceAdapter.viewHolder.OnAssignedServiceClickListener{

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
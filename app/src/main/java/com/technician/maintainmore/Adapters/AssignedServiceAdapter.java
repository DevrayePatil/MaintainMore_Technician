package com.technician.maintainmore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.technician.maintainmore.Modals.AssignedServiceModal;
import com.technician.maintainmore.R;

import java.util.ArrayList;

public class AssignedServiceAdapter extends RecyclerView.Adapter<AssignedServiceAdapter.viewHolder>{

    ArrayList<AssignedServiceModal> serviceModals;
    Context context;

    viewHolder.OnAssignedServiceClickListener clickListener;

    public AssignedServiceAdapter(ArrayList<AssignedServiceModal> serviceModals, Context context,
                                  viewHolder.OnAssignedServiceClickListener clickListener) {
        this.serviceModals = serviceModals;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_assigned_service, parent, false);
        return new viewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AssignedServiceModal modal = serviceModals.get(position);

        holder.displayServiceName.setText(modal.getServiceName());

        holder.displayTotalServices.setText(modal.getTotalServices());
        holder.displayTotalAmount.setText(modal.getTotalServicesPrice());
        holder.displayVisitingDate.setText(modal.getVisitingDate());
        holder.displayVisitingTime.setText(modal.getVisitingTime());

        Glide.with(context).load(modal.getServiceIcon()).placeholder(R.drawable.ic_person).into(holder.profilePicture);



    }

    @Override
    public int getItemCount() {
        return serviceModals.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView displayServiceName;
        TextView displayUserName,displayUserEmail, displayUserPhoneNumber;
        TextView displayTotalServices, displayTotalAmount, displayVisitingDate, displayVisitingTime;

        ImageView profilePicture;

        OnAssignedServiceClickListener assignedServiceClickListener;

        public viewHolder(@NonNull View itemView, OnAssignedServiceClickListener assignedServiceClickListener) {
            super(itemView);

            displayServiceName = itemView.findViewById(R.id.displayServiceName);

            displayUserName = itemView.findViewById(R.id.displayUserName);
            displayUserEmail = itemView.findViewById(R.id.displayUserEmail);
            displayUserPhoneNumber = itemView.findViewById(R.id.displayUserPhoneNumber);

            displayTotalServices = itemView.findViewById(R.id.displayTotalServices);
            displayTotalAmount = itemView.findViewById(R.id.displayTotalAmount);
            displayVisitingDate = itemView.findViewById(R.id.displayVisitingDate);
            displayVisitingTime = itemView.findViewById(R.id.displayVisitingTime);

            profilePicture = itemView.findViewById(R.id.profilePicture);


            this.assignedServiceClickListener = assignedServiceClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            assignedServiceClickListener.onAssignedServiceCardClickListener(getAdapterPosition());
        }

        public interface OnAssignedServiceClickListener{
            void onAssignedServiceCardClickListener(int position);
        }

    }
}

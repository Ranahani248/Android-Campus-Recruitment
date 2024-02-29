package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.SQLData;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    public static List<JobItem> jobList;
    private OnItemClickListener onItemClickListener; // Define listener member variable

    public JobAdapter(List<JobItem> jobList, OnItemClickListener onItemClickListener) {
        JobAdapter.jobList = jobList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobItem jobItem = jobList.get(position);
        holder.jobTitleTextView.setText(jobItem.getJobTitle());
        holder.companyName.setText(jobItem.getJobCompany());
        String recruiterid = jobItem.getRecruiterid();
        loadRecruiterProfilePicture(holder.imageView, recruiterid);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(jobItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(jobList.size(), 10);
    }

    static void loadRecruiterProfilePicture(ImageView imageView, String recruiterId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference recruiterDocRef = db.collection("Recruiters").document(recruiterId);
        recruiterDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profilePictureUrl = documentSnapshot.getString("profilePicture");
                // Load the profile picture into the ImageView using Glide
                Glide.with(imageView.getContext())
                        .load(profilePictureUrl)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(imageView);
            }
        });


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView, companyName;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_description);
            companyName = itemView.findViewById(R.id.companyName);
             imageView = itemView.findViewById(R.id.logo);
        }
    }

    // Define the interface for the click listener
    public interface OnItemClickListener {
        void onItemClick(JobItem jobItem);
    }
}

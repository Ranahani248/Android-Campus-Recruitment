package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    public static List<JobItem> jobList;
    private OnItemClickListener onItemClickListener; // Define listener member variable

    public JobAdapter(List<JobItem> jobList, OnItemClickListener onItemClickListener) {
        this.jobList = jobList;
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

        // Set the click listener for the item
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_description);
        }
    }

    // Define the interface for the click listener
    public interface OnItemClickListener {
        void onItemClick(JobItem jobItem);
    }
}

package com.example.androidcampusrecruitmentsystem;

// JobAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter_recruiter extends RecyclerView.Adapter<JobAdapter_recruiter.ViewHolder> {

    static     List<JobItem> jobList_recruiter;
    private JobAdapter_recruiter.OnItemClickListener onItemClickListener;

    public JobAdapter_recruiter(List<JobItem> jobList, JobAdapter_recruiter.OnItemClickListener onItemClickListener) {
        jobList_recruiter = jobList;
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
        JobItem jobItem = jobList_recruiter.get(position);
        holder.jobTitleTextView.setText(jobItem.getJobTitle());
        holder.companyName.setText(jobItem.getJobCompany());
        holder.logo.setImageResource(R.drawable.logo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(jobItem);
                }
            }
        });

    }
   public interface OnItemClickListener {
        void onItemClick(JobItem jobItem);
    }
    @Override
    public  int getItemCount() {
        return Math.min(jobList_recruiter.size(), 10);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView, companyName;
        ImageView logo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_description);
            companyName = itemView.findViewById(R.id.companyName);
            logo = itemView.findViewById(R.id.logo);
        }
    }
}

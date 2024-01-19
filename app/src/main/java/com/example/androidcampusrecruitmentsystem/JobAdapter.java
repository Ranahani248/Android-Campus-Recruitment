package com.example.androidcampusrecruitmentsystem;

// JobAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private List<JobItem> jobList;

    public JobAdapter(List<JobItem> jobList) {
        this.jobList = jobList;
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
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_description);
        }
    }
}

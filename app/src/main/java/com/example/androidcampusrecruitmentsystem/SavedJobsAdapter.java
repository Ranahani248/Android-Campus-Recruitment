package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.ViewHolder> {
    private List<JobItem> savedjobList;

    public SavedJobsAdapter(List<JobItem> savedjobList) {
        this.savedjobList = savedjobList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobItem jobItem = savedjobList.get(position);
        holder.jobTitleTextView.setText(jobItem.getJobTitle());
    }


    @Override
    public int getItemCount() {
       return Math.min(savedjobList.size(), 10);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_description);
        }
    }
}

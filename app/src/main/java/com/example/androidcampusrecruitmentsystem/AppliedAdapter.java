package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppliedAdapter extends RecyclerView.Adapter<AppliedAdapter.ViewHolder> {

List<JobItem> applieditemlist ;

    public AppliedAdapter(List<JobItem> applieditemlist) {
        this.applieditemlist = applieditemlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applied_jobs_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobItem jobItem = applieditemlist.get(position);
        holder.jobtitle.setText(jobItem.getJobTitle());
    }

    @Override
    public int getItemCount() {
        return applieditemlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobtitle = itemView.findViewById(R.id.job_description);
        }
    }
}

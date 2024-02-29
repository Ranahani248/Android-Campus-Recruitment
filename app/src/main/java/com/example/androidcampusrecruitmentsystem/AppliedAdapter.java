package com.example.androidcampusrecruitmentsystem;

import static com.example.androidcampusrecruitmentsystem.JobAdapter.loadRecruiterProfilePicture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppliedAdapter extends RecyclerView.Adapter<AppliedAdapter.ViewHolder> {

List<JobItem> applieditemlist ;
    private OnItemClickListener mListener;


    public AppliedAdapter(List<JobItem> applieditemlist) {
        this.applieditemlist = applieditemlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applied_jobs_cards, parent, false);
        return new ViewHolder(view);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(JobItem jobItem);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobItem jobItem = applieditemlist.get(position);
        holder.jobtitle.setText(jobItem.getJobTitle());
        holder.companyName.setText(jobItem.getJobCompany());
        String recruiterid = jobItem.getRecruiterid();
        loadRecruiterProfilePicture(holder.imageView, recruiterid);

        holder.itemView.setOnClickListener(v -> {

                if (mListener != null) {
                    mListener.onItemClick(jobItem);

            }
        });
    }

    @Override
    public int getItemCount() {
        return applieditemlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobtitle, companyName;
        ImageView  imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobtitle = itemView.findViewById(R.id.job_description);
            companyName = itemView.findViewById(R.id.companyName);
            imageView = itemView.findViewById(R.id.logo);
        }
    }
}

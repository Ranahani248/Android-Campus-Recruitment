package com.example.androidcampusrecruitmentsystem;

import static com.example.androidcampusrecruitmentsystem.JobAdapter.loadRecruiterProfilePicture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.ViewHolder> {
     static List<JobItem> savedjobList;
    private OnItemClickListener listener;

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
        holder.companyName.setText(jobItem.getJobCompany());
        String recruiterid = jobItem.getRecruiterid();
        loadRecruiterProfilePicture(holder.imageView, recruiterid);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(jobItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
       return Math.min(savedjobList.size(), 10);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(JobItem jobItem);
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
}

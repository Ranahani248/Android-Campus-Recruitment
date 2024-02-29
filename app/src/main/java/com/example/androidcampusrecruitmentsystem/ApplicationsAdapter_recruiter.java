package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplicationsAdapter_recruiter extends RecyclerView.Adapter<ApplicationsAdapter_recruiter.ViewHolder> {

    private List<Application> applicationList;
    private ApplicationsAdapter_recruiter.OnItemClickListener listener;
    public ApplicationsAdapter_recruiter(List<Application> applicationList, ApplicationsAdapter_recruiter.OnItemClickListener listener) {
        this.applicationList = applicationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);

        // Assuming Application class has attributes like jobTitle, applicantName, etc.
        holder.jobTitleTextView.setText(application.getJobTitle());
        holder.studentName.setText(application.getApplicantName());

        String profilePictureUrl = application.getProfilePictureUrl();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(profilePictureUrl)
                    .into(holder.profilePicture);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(application));

        // You can bind other data to respective TextViews as needed
        // holder.otherTextView.setText(application.getOtherAttribute());
    }
    public interface OnItemClickListener{
        void onItemClicked(Application application);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView, studentName;
        CircleImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.job_title);
            studentName = itemView.findViewById(R.id.Student_name);
            profilePicture = itemView.findViewById(R.id.logo);
            // Initialize other TextViews here if needed
        }
    }
}

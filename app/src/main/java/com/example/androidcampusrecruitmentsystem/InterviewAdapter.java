package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InterviewAdapter extends RecyclerView.Adapter<InterviewAdapter.ViewHolder> {

    private List<InterviewItem> interviewItemList;
    RecyclerView recyclerView;
    TestFragment testFragment= null;


    public InterviewAdapter(List<InterviewItem> interviewItemList, RecyclerView recyclerView, TestFragment testFragment) {
        this.interviewItemList = interviewItemList;
        this.recyclerView = recyclerView;
        if(testFragment!=null){
            this.testFragment = testFragment;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interview_viewholder, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InterviewItem testItem = interviewItemList.get(position);
        holder.bind(testItem);
    }
    @Override
    public int getItemCount() {
        return interviewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, nameTextView;
        LinearLayout interview_viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_interview);
            nameTextView = itemView.findViewById(R.id.recruiter_name_interview);
            interview_viewHolder = itemView.findViewById(R.id.interview_viewholder);
            interview_viewHolder.setOnClickListener(v -> {
                {
                    Intent intent = new Intent(testFragment.getContext(), ApproveInterview.class);
                    intent.putExtra("interviewId", interviewItemList.get(getAdapterPosition()).getScheduleinterviewID());
                    intent.putExtra("jobId", interviewItemList.get(getAdapterPosition()).getJobID());
                    intent.putExtra("recruiterId", interviewItemList.get(getAdapterPosition()).getRecruiterID());
                    intent.putExtra("fromDate", interviewItemList.get(getAdapterPosition()).getFrom_date());
                    intent.putExtra("toDate", interviewItemList.get(getAdapterPosition()).getTo_date());
                    intent.putExtra("fromTime", interviewItemList.get(getAdapterPosition()).getFrom_time());
                    intent.putExtra("toTime", interviewItemList.get(getAdapterPosition()).getTo_time());
                    testFragment.startActivity(intent);
                }
            });
        }

        public void bind(InterviewItem testItem) {
            titleTextView.setText(testItem.getJobTitle());
            nameTextView.setText(testItem.getRecruiterName());


        }
    }
}

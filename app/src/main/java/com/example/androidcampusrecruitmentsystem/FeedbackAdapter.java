package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.TestViewHolder> {

    private List<FeedbackITem> feedbackITemList;
    RecyclerView recyclerView;


    public FeedbackAdapter(List<FeedbackITem> feedbackITemList, RecyclerView recyclerView) {
        this.feedbackITemList = feedbackITemList;
        this.recyclerView = recyclerView;

    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_viewholder, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        FeedbackITem feedbackITem = feedbackITemList.get(position);
        holder.delete.setOnClickListener(v->{
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("Feedback").document(feedbackITem.getFeedbackId()).delete();
            feedbackITemList.remove(position);
            notifyDataSetChanged();
        });
        holder.bind(feedbackITem);
    }

    @Override
    public int getItemCount() {
        return feedbackITemList.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView feedbackRecruiter, feedbackJob, feedback;
        Button delete;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackRecruiter = itemView.findViewById(R.id.recruiter_feedback);
            feedbackJob = itemView.findViewById(R.id.job_feedback);
            feedback = itemView.findViewById(R.id.feedback);
            delete = itemView.findViewById(R.id.delete_feedback);


        }

        public void bind(FeedbackITem testItem) {
            feedbackRecruiter.setText(testItem.getRecruiter());
            feedbackJob.setText(testItem.getJobtitle());
            feedback.setText(testItem.getFeedback());

        }
    }
}

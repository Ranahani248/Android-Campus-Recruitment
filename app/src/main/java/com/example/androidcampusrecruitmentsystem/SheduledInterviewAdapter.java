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

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SheduledInterviewAdapter extends RecyclerView.Adapter<SheduledInterviewAdapter.TestViewHolder> {

    private List<SheduledInterviewItem> interviewlist;
    RecyclerView recyclerView;
    TestFragment testFragment= null;

    TestFragment_recruiter testFragment_recruiter= null;



    public SheduledInterviewAdapter(List<SheduledInterviewItem> interviewlist, RecyclerView recyclerView, TestFragment testFragment, TestFragment_recruiter testFragment_recruiter) {
        this.interviewlist = interviewlist;
        this.recyclerView = recyclerView;
        if(testFragment!=null){
            this.testFragment = testFragment;}
        if (testFragment_recruiter != null) {
            this.testFragment_recruiter = testFragment_recruiter;
        }

    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sheduledinterview_viewholder, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        SheduledInterviewItem testItem = interviewlist.get(position);
        holder.test_viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testFragment!=null){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String uid = auth.getUid();
                    Intent intent = new Intent(testFragment.getContext(), ConnectingActivity.class);
                    intent.putExtra("incoming", uid);
                    intent.putExtra("outgoing", testItem.getOutgoingId());
                    intent.putExtra("createdBy", testItem.getRecruiterName());
                    testFragment.startActivity(intent);
                }
                if(testFragment_recruiter!=null){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String uid = auth.getUid();
                    Intent intent = new Intent(testFragment_recruiter.getContext(), ConnectingActivity.class);
                    intent.putExtra("incoming", uid);
                    intent.putExtra("outgoing", testItem.getOutgoingId());
                    intent.putExtra("createdBy", testItem.getRecruiterName());

                    testFragment_recruiter.startActivity(intent);

                }
            }
        });

        holder.bind(testItem);
    }

    @Override
    public int getItemCount() {
        return interviewlist.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, dateTextView, nameTextView;
        LinearLayout test_viewHolder;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            dateTextView = itemView.findViewById(R.id.end_date);
            nameTextView = itemView.findViewById(R.id.Student_name);
            test_viewHolder = itemView.findViewById(R.id.test_viewHolder);


        }

        public void bind(SheduledInterviewItem testItem) {
            titleTextView.setText(testItem.getJobtitle());
            String date = testItem.getStartDate() + " - " + testItem.getStartTime();
            dateTextView.setText(date);
            nameTextView.setText(testItem.getRecruiterName());

        }
    }
}

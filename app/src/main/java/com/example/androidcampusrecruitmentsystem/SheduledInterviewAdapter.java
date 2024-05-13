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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        holder.test_viewHolder.setOnClickListener(view -> {

          if(isDateTimeTodayAndAhead(testItem.getStartDate(),testItem.getStartTime())) {

              if (testFragment != null) {
                  FirebaseFirestore db = FirebaseFirestore.getInstance();
                  FirebaseAuth auth = FirebaseAuth.getInstance();
                  String uid = auth.getUid();
                  final boolean[] exist = {false};
                  db.collection("rooms").get().addOnSuccessListener(queryDocumentSnapshots -> {
                      for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                          if (documentSnapshot.getString("studentId").equals(uid)) {
                              Intent intent = new Intent(testFragment.getContext(), ConnectingActivity.class);
                              intent.putExtra("studentId", uid);
                              intent.putExtra("studentName", testItem.getRecruiterName());
                              testFragment.startActivity(intent);
                              exist[0] = true;
                          }
                      }
                      if (!exist[0]) {
                          Toast.makeText(holder.test_viewHolder.getContext(), "No Room Created By recruiter", Toast.LENGTH_SHORT).show();
                      }
                  });


              }
              if (testFragment_recruiter != null) {
                  FirebaseAuth auth = FirebaseAuth.getInstance();
                  String uid = auth.getUid();
                  Intent intent = new Intent(testFragment_recruiter.getContext(), ConnectingActivity.class);
                  intent.putExtra("username", testItem.getRecruiterName());
                  intent.putExtra("recruiterId", uid);
                  intent.putExtra("studentID", testItem.getOutgoingId());

                  testFragment_recruiter.startActivity(intent);

              }
          }
          else {
              Toast.makeText(holder.test_viewHolder.getContext(), "Interview time not started or day passed", Toast.LENGTH_SHORT).show();
          }
        });

        holder.bind(testItem);
    }
    boolean isDateTimeTodayAndAhead(String date, String time) {
        // Convert date and time strings to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date dateTime = sdf.parse(date + " " + time);
            long dateTimeMillis = dateTime.getTime();
            long currentMillis = System.currentTimeMillis();
            // Check if date is today and time is ahead of current time
            return isSameDate(dateTime, new Date(currentMillis)) && dateTimeMillis > currentMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isSameDate(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
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

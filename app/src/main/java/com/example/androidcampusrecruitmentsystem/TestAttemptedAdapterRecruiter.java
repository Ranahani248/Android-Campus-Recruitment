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

public class TestAttemptedAdapterRecruiter extends RecyclerView.Adapter<TestAttemptedAdapterRecruiter.TestViewHolder> {

    private List<TestListItemRecruiter> testList;
    RecyclerView recyclerView;
    TestFragment_recruiter testFragment= null;


    public TestAttemptedAdapterRecruiter(List<TestListItemRecruiter> testList, RecyclerView recyclerView, TestFragment_recruiter testFragment) {
        this.testList = testList;
        this.recyclerView = recyclerView;
        if(testFragment!=null){
            this.testFragment = testFragment;}

    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attempted_test_viewholder, parent, false);
        return new TestViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        TestListItemRecruiter testItem = testList.get(position);
        holder.bind(testItem);
    }
    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, nameTextView;
        LinearLayout test_viewHolder;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.attemptedTitle);
            nameTextView = itemView.findViewById(R.id.Student_name_attemptedTest);
            test_viewHolder = itemView.findViewById(R.id.attemptedTest_viewHolder);
            test_viewHolder.setOnClickListener(v -> {
                    {
                    Intent intent = new Intent(testFragment.getContext(), Check_Test.class);
                    intent.putExtra("attemptedTestId", testList.get(getAdapterPosition()).getAttemptedTestId());
                    testFragment.startActivity(intent);
                    }
            });
        }

        public void bind(TestListItemRecruiter testItem) {
            titleTextView.setText(testItem.getJobTitle());
            nameTextView.setText(testItem.getStudentName());

        }
    }
}

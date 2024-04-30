package com.example.androidcampusrecruitmentsystem;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestListAdapterRecruiter extends RecyclerView.Adapter<TestListAdapterRecruiter.TestViewHolder> {

    private List<TestListItemRecruiter> testList;
    RecyclerView recyclerView;
     TestFragment testFragment= null;


    public TestListAdapterRecruiter(List<TestListItemRecruiter> testList, RecyclerView recyclerView, TestFragment testFragment) {
        this.testList = testList;
        this.recyclerView = recyclerView;
        if(testFragment!=null){
        this.testFragment = testFragment;}

    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_viewholder, parent, false);
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

        private TextView titleTextView, dateTextView, nameTextView;
        LinearLayout test_viewHolder;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            dateTextView = itemView.findViewById(R.id.end_date);
            nameTextView = itemView.findViewById(R.id.Student_name);
            test_viewHolder = itemView.findViewById(R.id.test_viewHolder);

            test_viewHolder.setOnClickListener(v -> {
                if(!testList.get(getAdapterPosition()).isisApplied()) {
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        try {
                            Date dateTime = sdf.parse(testList.get(getAdapterPosition()).getStartDate());
                            assert dateTime != null;
                            long dateTimeMillis = dateTime.getTime();
                            long currentMillis = System.currentTimeMillis();
                           if( !(dateTimeMillis > currentMillis)){

                               Intent intent = new Intent(testFragment.getContext(), AttemptTest.class);
                               intent.putExtra("testId", testList.get(getAdapterPosition()).getTestId());
                              testFragment.startActivity(intent);
                           }
                           else {
                               Log.d("TestViewHolder", "Time Not Started");
                           }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }  }


                 });

        }

        public void bind(TestListItemRecruiter testItem) {
            titleTextView.setText(testItem.getJobTitle());
            String date = testItem.getStartDate() + " - " + testItem.getEndDate();
            dateTextView.setText(date);
            nameTextView.setText(testItem.getRecruiterName());

            if(testItem.isisApplied())
            {
                nameTextView.setText(testItem.getStudentName());
            }
        }
    }
}

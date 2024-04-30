package com.example.androidcampusrecruitmentsystem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestFragment extends Fragment {

    private RecyclerView test_recyclerView;
    private TestListAdapterRecruiter testAdapter;
    private List<TestListItemRecruiter> testItemList;
    private FirebaseFirestore firestore;
    ConstraintLayout test_layoutStudent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        testItemList = new ArrayList<>();
        loadTests();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        test_recyclerView = view.findViewById(R.id.testRecycle_student);
        testAdapter = new TestListAdapterRecruiter(testItemList, test_recyclerView, this);
        test_layoutStudent = view.findViewById(R.id.test_layoutStudent);
        test_layoutStudent.setVisibility(View.GONE);
        test_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        test_recyclerView.setAdapter(testAdapter);
        return view;
    }

    private void loadTests() {
        firestore.collection("test")
                .whereEqualTo("studentID", getCurrentUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        test_layoutStudent.setVisibility(View.VISIBLE);
                        return;
                    }
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String testId = documentSnapshot.getId();
                        String endDate = documentSnapshot.getString("end_date");
                        String endTime = documentSnapshot.getString("end_time");
                        String startDate = documentSnapshot.getString("start_date");
                        String startTime = documentSnapshot.getString("start_time");
                        String recruiterId = documentSnapshot.getString("RecruiterId");
                        String jobId = documentSnapshot.getString("jobId");
                        String startDateTime = startDate + " " + startTime;
                        String endDateTime = endDate + " " + endTime;

                        // Check if end date and time are before current date and time
                        if (isDateTimeBeforeCurrent(endDate, endTime, true)) {
                            removeTestFromDatabase(documentSnapshot.getId());
                            return;
                        }
                        // Check if current date and time are before start date and time
                        else if (isDateTimeBeforeCurrent(startDate, startTime, false)) {

                        }
                        TestListItemRecruiter testItem = new TestListItemRecruiter( startDateTime, endDateTime,"RecruiterName", "jobTitle", testId);


                        assert recruiterId != null;
                        firestore.collection("Recruiters").document(recruiterId).get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                    String RecruiterName = documentSnapshot1.getString("name");
                                    testItem.setRecruiterName(RecruiterName);
                                    retrieveJobName(jobId, testItem);
                                }).addOnFailureListener(e -> {
                                    retrieveJobName(jobId, testItem);
                                });

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

     boolean isDateTimeBeforeCurrent(String date, String time, boolean after) {
        // Convert date and time strings to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date dateTime = sdf.parse(date + " " + time);
            long dateTimeMillis = dateTime.getTime();
            long currentMillis = System.currentTimeMillis();
            if( !after) {
                Log.d("TestFragment_recruiter", "Start time after Current time: " + dateTimeMillis);
                return dateTimeMillis > currentMillis;
            }
            else{
                Log.d("TestFragment_recruiter", "end time before Current time: " + dateTimeMillis);
                return dateTimeMillis < currentMillis;}
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void addItem(TestListItemRecruiter testItem) {
        testItemList.add(testItem);
        testAdapter.notifyDataSetChanged();

    }

    private void removeTestFromDatabase(String testId) {

        firestore.collection("test").document(testId).delete()
                .addOnSuccessListener(v -> {
                    Log.d("TestFragment_recruiter", "Test removed from database");
                });

    }

    private void retrieveJobName(String jobId, TestListItemRecruiter testItem) {
        Log.d("TestFragment_recruiter", "Job ID: " + jobId);
        firestore.collection("Jobs").document(jobId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String jobTitle = documentSnapshot.getString("jobTitle");
                    testItem.setJobTitle(jobTitle);
                    addItem(testItem);
                    Log.d("TestFragment_recruiter", "Job title: " + jobTitle);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    addItem(testItem);
                });


    }


    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        else{ return null;}
    }
}
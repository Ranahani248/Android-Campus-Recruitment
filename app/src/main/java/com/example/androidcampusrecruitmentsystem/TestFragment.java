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

    private RecyclerView test_recyclerView, feedback_recyclerView, interview_recyclerView, scheduledInterviews_recyclerView;
    private TestListAdapterStudent testAdapter;
    private SheduledInterviewAdapter scheduleInterviewAdapter;
    FeedbackAdapter feedbackAdapter;
    private List<TestListItemStudent> testItemList;
    private List<SheduledInterviewItem> interviewList;

    InterviewAdapter interviewAdapter;

    List<FeedbackITem> feedbackItemList = new ArrayList<>();
    List<InterviewItem> interviewItemList = new ArrayList<>();
    private FirebaseFirestore firestore;
    ConstraintLayout test_layoutStudent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        testItemList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        test_recyclerView = view.findViewById(R.id.testRecycle_student);
        testAdapter = new TestListAdapterStudent(testItemList, test_recyclerView, this);
        test_layoutStudent = view.findViewById(R.id.test_layoutStudent);
        interview_recyclerView = view.findViewById(R.id.interviewRecycle);
        feedback_recyclerView = view.findViewById(R.id.feedback_recycle);
        scheduledInterviews_recyclerView = view.findViewById(R.id.schduleInterview_recycle);
        interviewList = new ArrayList<>();

        scheduleInterviewAdapter = new SheduledInterviewAdapter(interviewList, scheduledInterviews_recyclerView, this, null);
        scheduledInterviews_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduledInterviews_recyclerView.setAdapter(scheduleInterviewAdapter);



        interviewAdapter = new InterviewAdapter(interviewItemList, interview_recyclerView, this);
        interview_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        interview_recyclerView.setAdapter(interviewAdapter);

        feedbackAdapter = new FeedbackAdapter(feedbackItemList, feedback_recyclerView);
        feedback_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedback_recyclerView.setAdapter(feedbackAdapter);

        test_layoutStudent.setVisibility(View.GONE);
        test_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        test_recyclerView.setAdapter(testAdapter);
        loadScheduledInterviews();
        loadTests();
        loadInterviews();
        loadFeedbacks();
        return view;
    }
    public void loadScheduledInterviews() {
        firestore.collection("Interviews").get().addOnSuccessListener(queryDocumentSnapshots -> {
            interviewList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.getString("studentID").equals(getCurrentUserId())) {
                    String interviewId = documentSnapshot.getId();
                    String startDate = documentSnapshot.getString("scheduleDate");
                    String startTime = documentSnapshot.getString("scheduleTime");
                    String jobId = documentSnapshot.getString("jobID");
                    String recruiterID = documentSnapshot.getString("recruiterID");

                    SheduledInterviewItem scheduledInterviewItem = new SheduledInterviewItem(startDate, startTime, "Job Title", "Recruiter Name", interviewId, recruiterID);
                    getScheduleInterviewJob(jobId, scheduledInterviewItem, recruiterID);
                }
            }
        });
    }
        public void getScheduleInterviewJob(String jobId, SheduledInterviewItem scheduledInterviewItem, String recruiterID) {
            firestore.collection("Jobs").document(jobId).get().addOnSuccessListener(documentSnapshot -> {
                String jobTitle = documentSnapshot.getString("jobTitle");
                scheduledInterviewItem.setJobtitle(jobTitle);
               getScheduleInterviewRecruiter(recruiterID, scheduledInterviewItem);
            }).addOnFailureListener(e -> {
                getScheduleInterviewRecruiter(recruiterID, scheduledInterviewItem);

            });

        }
        public void getScheduleInterviewRecruiter(String recruiterID, SheduledInterviewItem scheduledInterviewItem) {
            firestore.collection("Recruiters").document(recruiterID).get().addOnSuccessListener(documentSnapshot -> {
                String recruiterName = documentSnapshot.getString("name");
                scheduledInterviewItem.setRecruiterName(recruiterName);
                interviewList.add(scheduledInterviewItem);
                scheduleInterviewAdapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                interviewList.add(scheduledInterviewItem);
                scheduleInterviewAdapter.notifyDataSetChanged();
            });
        }
    public void loadInterviews() {
        firestore.collection("ScheduleInterview").get().addOnSuccessListener(queryDocumentSnapshots -> {
            interviewItemList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.getString("studentID").equals(getCurrentUserId())) {
                    String interviewId = documentSnapshot.getId();
                    String fromDate = documentSnapshot.getString("fromdate");
                    String toDate = documentSnapshot.getString("todate");
                    String fromTime = documentSnapshot.getString("fromtime");
                    String toTime = documentSnapshot.getString("totime");
                    String jobId = documentSnapshot.getString("jobID");
                    String recruiterId = documentSnapshot.getString("recruiterID");

                    InterviewItem interviewItem = new InterviewItem(fromDate, toDate, fromTime, toTime, "RecruiterName", "job Title", interviewId, recruiterId, jobId);
                    getJobTitle(jobId, interviewItem, recruiterId);
                }
            }

        });
    }
    public void getJobTitle(String jobId, InterviewItem interviewItem, String recruiterId) {
        firestore.collection("Jobs").document(jobId).get().addOnSuccessListener(documentSnapshot -> {
            String jobTitle = documentSnapshot.getString("jobTitle");
            interviewItem.setJobTitle(jobTitle);
            getRecruiterName(recruiterId, interviewItem);
        }).addOnFailureListener(e -> {
            getRecruiterName(recruiterId, interviewItem);
        });
    }
    public void getRecruiterName(String recruiterId, InterviewItem interviewItem) {
        firestore.collection("Recruiters").document(recruiterId).get().addOnSuccessListener(documentSnapshot -> {
            String name = documentSnapshot.getString("name");
            interviewItem.setRecruiterName(name);
            interviewItemList.add(interviewItem);
            interviewAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            interviewItemList.add(interviewItem);
            interviewAdapter.notifyDataSetChanged();
        });
    }



    public void loadFeedbacks() {
        firestore.collection("Feedback").get().addOnSuccessListener(queryDocumentSnapshots -> {
            feedbackItemList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.getString("studentID").equals(getCurrentUserId())){
                String feedbackId = documentSnapshot.getId();
                String feedback = documentSnapshot.getString("feedback");
                String jobId = documentSnapshot.getString("jobID");
                String recruiterId = documentSnapshot.getString("recruiterID");
                FeedbackITem feedbackITem = new FeedbackITem(feedback,"recruiter","job Title",feedbackId);
                getRecruiterID(recruiterId,jobId,feedbackITem);
            }}
            });
    }
    public void getRecruiterID(String recruiterId, String jobId, FeedbackITem feedbackITem) {
        firestore.collection("Recruiters").document(recruiterId).get().addOnSuccessListener(documentSnapshot -> {
            String name = documentSnapshot.getString("name");
            feedbackITem.setRecruiter(name);
            getJobTitle(jobId,feedbackITem);
        }).addOnFailureListener(e ->{
            getJobTitle(jobId,feedbackITem);
                });
    }
    public void getJobTitle(String jobId, FeedbackITem feedbackITem) {
        firestore.collection("Jobs").document(jobId).get().addOnSuccessListener(documentSnapshot -> {
            String title = documentSnapshot.getString("jobTitle");
            feedbackITem.setJobtitle(title);
            feedbackItemList.add(feedbackITem);
            feedbackAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            feedbackItemList.add(feedbackITem);
            feedbackAdapter.notifyDataSetChanged();
        });
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
                        TestListItemStudent testItem = new TestListItemStudent( startDateTime, endDateTime,"RecruiterName", "jobTitle", testId);


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
    public void addItem(TestListItemStudent testItem) {
        testItemList.add(testItem);
        testAdapter.notifyDataSetChanged();

    }

    private void removeTestFromDatabase(String testId) {

        firestore.collection("test").document(testId).delete()
                .addOnSuccessListener(v -> {
                    Log.d("TestFragment_recruiter", "Test removed from database");
                });

    }

    private void retrieveJobName(String jobId, TestListItemStudent testItem) {
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
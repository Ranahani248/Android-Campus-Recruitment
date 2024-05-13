package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Check_Test extends AppCompatActivity {
    RecyclerView MCQ_recycler, question_recycler;
    CheckMcqAdapter mcqAdapter;
    CheckQuestionAdapter questionAdapter;

    List<AttempMcqItem> mcqItemList;
    Button feedbackSubmit, delete;
    EditText feedback;

    List<AttemptquestionItem> questionItemList;

    String attemptedTestId;
    String testID, JobID,RecruiterID, studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_test);
        attemptedTestId = getIntent().getStringExtra("attemptedTestId");
        MCQ_recycler = findViewById(R.id.MCQ_recycler_check);
        question_recycler = findViewById(R.id.Questions_Recycler_check);
        feedbackSubmit = findViewById(R.id.give_feedback_check);
        feedback = findViewById(R.id.feedback_check);
        delete = findViewById(R.id.delete_check);


        mcqItemList = new ArrayList<>();
        MCQ_recycler.setLayoutManager(new LinearLayoutManager(this));
        mcqAdapter = new CheckMcqAdapter(MCQ_recycler,this,mcqItemList);
        MCQ_recycler.setAdapter(mcqAdapter);

        questionItemList = new ArrayList<>();
        question_recycler.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new CheckQuestionAdapter(question_recycler,this,questionItemList);
        question_recycler.setAdapter(questionAdapter);

        ids();


        delete.setOnClickListener(v->{
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firestore.collection("AttemptedTest").document(attemptedTestId).collection("questions");
            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete();
                    }

                }
            });
            firestore.collection("AttemptedTest").document(attemptedTestId).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(Check_Test.this, "Test deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Check_Test.this, MainActivityRecruiter.class);
                startActivity(intent);
            });
        });

        feedbackSubmit.setOnClickListener(v-> {
           if (feedback.getText().toString().isEmpty()) {
               feedback.setError("Feedback cannot be empty");
               feedback.requestFocus();
           } else {
               FirebaseFirestore firestore = FirebaseFirestore.getInstance();
               Map<String, Object> feedbackMap = new HashMap<>();
               feedbackMap.put("feedback", feedback.getText().toString());
               feedbackMap.put("studentID", studentID);
               feedbackMap.put("jobID", JobID);
               feedbackMap.put("recruiterID", RecruiterID);
               feedbackMap.put("testID", testID);
              firestore.collection("Feedback").add(feedbackMap).addOnSuccessListener(documentReference -> {
                  Toast.makeText(Check_Test.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(Check_Test.this, MainActivityRecruiter.class);
                  startActivity(intent);
                feedbackSubmit.setEnabled(false);
              });
           }
        });



    }
    public void checkFeedback(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Feedback").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot feedbackSnapshot : queryDocumentSnapshots) {
                if (Objects.equals(feedbackSnapshot.getString("studentID"), studentID) && feedbackSnapshot.getString("recruiterID").equals(RecruiterID) && feedbackSnapshot.getString("testID").equals(testID)) {
                    feedbackSubmit.setEnabled(false);
                    feedbackSubmit.setText("Feedback Submitted");
                    break;
                }
            }
        });

    }

    private void ids() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("AttemptedTest").document(attemptedTestId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            RecruiterID = documentSnapshot.getString("RecruiterId");
            JobID = documentSnapshot.getString("jobId");
            studentID = documentSnapshot.getString("studentID");
            testID = documentSnapshot.getString("testID");
            fillMcqItemList();
            fillquestionItemList();
            checkFeedback();
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.getMessage()));
    }
    private void fillMcqItemList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("AttemptedTest")
                .document(attemptedTestId)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    mcqItemList.clear();

                    for (QueryDocumentSnapshot questionSnapshot : queryDocumentSnapshots) {
                        if (questionSnapshot.contains("mcq_questions")) {
                            List<Map<String, Object>> mcqQuestions = (List<Map<String, Object>>) questionSnapshot.get("mcq_questions");
                            if (mcqQuestions != null) {
                                Log.d("TAG", "mcqQuestions "+mcqQuestions);
                                // Iterate through the items in the "mcq_questions" array
                                for (Map<String, Object> mcqQuestion : mcqQuestions) {
                                    // Access the fields of each mcqQuestion item
                                    String question = (String) mcqQuestion.get("question");
                                    String option1 = (String) mcqQuestion.get("option1");
                                    String option2 = (String) mcqQuestion.get("option2");
                                    String option3 = (String) mcqQuestion.get("option3");
                                    String option4 = (String) mcqQuestion.get("option4");
                                    String answer = (String) mcqQuestion.get("answer");

                                    // Create an AttempMcqItem object and add it to the list
                                    AttempMcqItem mcqItem = new AttempMcqItem(question, option1, option2, option3, option4, answer);
                                    mcqItemList.add(mcqItem);
                                }
                            }
                        }
                    }

                    mcqAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
    private void fillquestionItemList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("AttemptedTest")
                .document(attemptedTestId)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questionItemList.clear();

                    for (QueryDocumentSnapshot questionSnapshot : queryDocumentSnapshots) {
                        if (questionSnapshot.contains("text_questions")) {
                            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionSnapshot.get("text_questions");
                            if (questions != null) {
                    Log.d("TAG", "questions "+questions);
                            for (Map<String, Object> question1 : questions) {
                                    String question = (String) question1.get("question");
                                    String answer = (String) question1.get("answer");

                                    // Create an AttempMcqItem object and add it to the list
                                    AttemptquestionItem questionitem = new AttemptquestionItem(question, answer);
                                    questionItemList.add(questionitem);
                                }
                            }
                        }
                    }
                    questionAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
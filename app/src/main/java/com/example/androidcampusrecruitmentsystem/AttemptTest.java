package com.example.androidcampusrecruitmentsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class AttemptTest extends AppCompatActivity {
RecyclerView MCQ_recycler, question_recycler;
AttemptMcqAdapter mcqAdapter;
List<AttempMcqItem> mcqItemList;
AttemptQuestionAdapter questionAdapter;
List<AttemptquestionItem> questionItemList;

    TextView TextView , TextView2;
    Button submit;
String testID, JobID,RecruiterID, studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_test);
        MCQ_recycler = findViewById(R.id.MCQ_recycler_attempt);
        TextView = findViewById(R.id.TextView);
        TextView2 = findViewById(R.id.TextView2);
        testID = getIntent().getStringExtra("testId");
        submit = findViewById(R.id.submit_button);
        question_recycler = findViewById(R.id.Questions_Recycler);



        mcqItemList = new ArrayList<>();
        MCQ_recycler.setLayoutManager(new LinearLayoutManager(this));
        mcqAdapter = new AttemptMcqAdapter(MCQ_recycler,this,mcqItemList);

        MCQ_recycler.setAdapter(mcqAdapter);

        questionItemList = new ArrayList<>();
        question_recycler.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new AttemptQuestionAdapter(question_recycler,this,questionItemList);
        ids();
        question_recycler.setAdapter(questionAdapter);

        submit.setOnClickListener(v ->{
        boolean checkq =   questionAdapter.checkAll();
        boolean checkm =   mcqAdapter.checkAll();
            if(checkq && checkm){
                submitTest();
            }
            else{
                Toast.makeText(AttemptTest.this, "Please attempt and save all answers", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void submitTest(){

        submit.setEnabled(false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("test").document(testID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    RecruiterID = documentSnapshot.getString("RecruiterId");
                    JobID = documentSnapshot.getString("jobId");
                });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new document in the "test" collection
        DocumentReference testRef = db.collection("AttemptedTest").document();

        // Create a map to hold the test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("studentID", studentID);
        testData.put("RecruiterId", RecruiterID);
        testData.put("jobId", JobID);
        testData.put("testID", testID);

        testRef.set(testData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Test document successfully added
                        submit.setEnabled(true);

                        Toast.makeText(AttemptTest.this, "Test scheduled successfully", Toast.LENGTH_SHORT).show();

                        // Now add questions to this test
                        addQuestionsToTest(testRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error adding test document
                        submit.setEnabled(true);

                        Toast.makeText(AttemptTest.this, "Error scheduling test: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void addQuestionsToTest(DocumentReference testRef) {

        Map<String, Object> questionsData = new HashMap<>();

        List<Map<String, Object>> questionData = new ArrayList<>();
        for (int i = 0; i < questionAdapter.getquestionList().size(); i++) {
            String question = questionAdapter.getquestionList().get(i).getQuestion();
            String answer = questionAdapter.getquestionList().get(i).getAnswer();
            Map<String, Object> questionData1 = new HashMap<>();
            questionData1.put("question", question);
            questionData1.put("answer", answer);
            questionData.add(questionData1);
        }


        List<Map<String, Object>> mcqQuestionsData = new ArrayList<>();
        for (int i = 0; i < mcqAdapter.getMcqItemList().size(); i++) {
            String question = mcqAdapter.getMcqItemList().get(i).getQuestion();
            String option1 = mcqAdapter.getMcqItemList().get(i).getOption1();
            String option2 = mcqAdapter.getMcqItemList().get(i).getOption2();
            String option3 = mcqAdapter.getMcqItemList().get(i).getOption3();
            String option4 = mcqAdapter.getMcqItemList().get(i).getOption4();
            String answer = mcqAdapter.getMcqItemList().get(i).getAnswer();

            Map<String, Object> mcqQuestionData = new HashMap<>();
            mcqQuestionData.put("question", question);
            mcqQuestionData.put("option1", option1);
            mcqQuestionData.put("option2", option2);
            mcqQuestionData.put("option3", option3);
            mcqQuestionData.put("option4", option4);
            mcqQuestionData.put("answer", answer);
            mcqQuestionsData.add(mcqQuestionData);
        }

        // Create a map to hold the questions data
        questionsData.put("mcq_questions", mcqQuestionsData);
        questionsData.put("text_questions", questionData);

        // Add the questions data to Firestore as a subcollection of the test document
        testRef.collection("questions")
                .add(questionsData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submit.setEnabled(true);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference collectionReference = db.collection("test").document(testID).collection("questions");
                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    documentSnapshot.getReference().delete();
                                }

                            }
                        });
                        db.collection("test").document(testID).delete();

                        // Questions added successfully
                        Toast.makeText(AttemptTest.this, "Questions added to test", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AttemptTest.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error adding questions
                        submit.setEnabled(true);

                        Toast.makeText(AttemptTest.this, "Error adding questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void ids() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("test").document(testID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            RecruiterID = documentSnapshot.getString("RecruiterId");
            JobID = documentSnapshot.getString("jobId");
            studentID = documentSnapshot.getString("studentID");
            fillMcqItemList();
            Log.d("TAG", "onSuccess: " + JobID +" "+RecruiterID);
            fillquestionItemList();
        }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.getMessage()));
    }
    private void fillMcqItemList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("test")
                .document(testID)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "testID "+testID);
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

                                    // Create an AttempMcqItem object and add it to the list
                                    AttempMcqItem mcqItem = new AttempMcqItem(question, option1, option2, option3, option4, "");
                                    mcqItemList.add(mcqItem);
                                }
                            }
                    }
                    }

                    mcqAdapter.notifyDataSetChanged();
                    if(mcqItemList.size() == 0){
                        TextView.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
    private void fillquestionItemList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("test")
                .document(testID)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "testID "+testID);
                    questionItemList.clear();

                    for (QueryDocumentSnapshot questionSnapshot : queryDocumentSnapshots) {
                        if (questionSnapshot.contains("text_questions")) {
                            List<String> textQuestions = (List<String>) questionSnapshot.get("text_questions");

                            for (String question : textQuestions) {
                               Log.d("tag","question "+question);
                                AttemptquestionItem questionItem = new AttemptquestionItem(question,"");

                                // Add the question item to your list
                                questionItemList.add(questionItem);
                            }
                        }
                    }

                    questionAdapter.notifyDataSetChanged();
                    if(questionItemList.size() == 0){
                        TextView2.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
String testID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_test);
        MCQ_recycler = findViewById(R.id.MCQ_recycler_attempt);
        TextView = findViewById(R.id.TextView);
        TextView2 = findViewById(R.id.TextView2);
        testID = getIntent().getStringExtra("testId");
        question_recycler = findViewById(R.id.Questions_Recycler);


        mcqItemList = new ArrayList<>();
        MCQ_recycler.setLayoutManager(new LinearLayoutManager(this));
        mcqAdapter = new AttemptMcqAdapter(MCQ_recycler,this,mcqItemList);
        fillMcqItemList();
        MCQ_recycler.setAdapter(mcqAdapter);

        questionItemList = new ArrayList<>();
        question_recycler.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new AttemptQuestionAdapter(question_recycler,this,questionItemList);
        fillquestionItemList();
        question_recycler.setAdapter(questionAdapter);

    }
    private void fillMcqItemList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("test")
                .document(testID)
                .collection("questions")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "testID "+testID);

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
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "testID "+testID);

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
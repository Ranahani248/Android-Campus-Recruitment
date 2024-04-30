package com.example.androidcampusrecruitmentsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScheduleTestRecruiter extends AppCompatActivity {
    RecyclerView MCQ_recycler, question_recycler;
    McqAdapter Mcqadapter;
    String studentID;
    String jobID;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    questionAdapter questionAdapter;
    Button add_Mcq, add_Question, submit;
    TextView start_date, start_time, end_date, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_test_recruiter);
        Intent intent = getIntent();
        studentID = intent.getStringExtra("studentId");
        jobID = intent.getStringExtra("jobId");

        MCQ_recycler = findViewById(R.id.MCQ_recycler);
        question_recycler = findViewById(R.id.Questions_Recycler);
        add_Mcq = findViewById(R.id.add_Mcq);
        add_Question = findViewById(R.id.add_Question);
        start_date = findViewById(R.id.start_date);
        start_time = findViewById(R.id.start_time);
        end_date = findViewById(R.id.end_date);
        end_time = findViewById(R.id.end_time);
        submit = findViewById(R.id.submit_button);
        mAuth = FirebaseAuth.getInstance();
         currentUser = mAuth.getCurrentUser();

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                showDatePicker(ScheduleTestRecruiter.this, start_date, start_date);
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start_date.getText().toString().isEmpty()) {
                    showTimePicker(ScheduleTestRecruiter.this, start_time);
                } else {
                    Toast.makeText(ScheduleTestRecruiter.this, "Please select start date first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start_date.getText().toString().isEmpty()) {
                    showDatePicker(ScheduleTestRecruiter.this, end_date,start_date);
                } else {
                    Toast.makeText(ScheduleTestRecruiter.this, "Please select start date first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!end_date.getText().toString().isEmpty()) {
                    showTimePicker(ScheduleTestRecruiter.this, end_time);
                } else {
                    Toast.makeText(ScheduleTestRecruiter.this, "Please select end date first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        questionAdapter = new questionAdapter(question_recycler, this);
        question_recycler.setAdapter(questionAdapter);
        question_recycler.setLayoutManager(new LinearLayoutManager(this));
        add_Question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionTestItem questionTestItem = new questionTestItem("");
                questionAdapter.addItem(questionTestItem);
            }
        });


        Mcqadapter = new McqAdapter(MCQ_recycler, this);
        MCQ_recycler.setAdapter(Mcqadapter);
        MCQ_recycler.setLayoutManager(new LinearLayoutManager(this));
        add_Mcq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                McqQuestionItem mcqQuestionItem = new McqQuestionItem("", "", "", "", "");
                Mcqadapter.addItem(mcqQuestionItem);
            }
        });

        submit.setOnClickListener(v -> {
                // Get the selected dates and times
            submit.setEnabled(false);
                String startDateString = start_date.getText().toString();
                String startTimeString = start_time.getText().toString();
                String endDateString = end_date.getText().toString();
                String endTimeString = end_time.getText().toString();

                // Check if any of the fields are empty
                if (startDateString.isEmpty() || startTimeString.isEmpty() || endDateString.isEmpty() || endTimeString.isEmpty()) {
                    Toast.makeText(ScheduleTestRecruiter.this, "Please select start and end date and time", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);

                    return; // Exit the method if any field is empty
                }

                // Convert date and time strings to Calendar objects for comparison
                Calendar startDateTime = getCalendarFromEditText(start_date, start_time);
                Calendar endDateTime = getCalendarFromEditText(end_date, end_time);
                Calendar currentDateTime = Calendar.getInstance();

                // Check if start date and time are not behind current date and time
                if (startDateTime.before(currentDateTime)) {
                    Toast.makeText(ScheduleTestRecruiter.this, "Start date and time cannot be before current date and time", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);

                    return; // Exit the method if start date and time are invalid
                }

                // Check if end date and time are ahead of start date and time
                if (endDateTime.before(startDateTime)) {
                    Toast.makeText(ScheduleTestRecruiter.this, "End date and time must be after start date and time", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);

                    return; // Exit the method if end date and time are invalid
                }

                // If all checks pass, proceed with the submission
                TestITem testITem = new TestITem();
                testITem.setQuestionItemList(questionAdapter.getQuestionTestItemList());
                testITem.setMcqQuestionItemList(Mcqadapter.getMcqQuestionItemList());
                testITem.setStart_date(start_date.getText().toString());
                testITem.setEnd_date(end_date.getText().toString());
                testITem.setStart_time(start_time.getText().toString());
                testITem.setEnd_time(end_time.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new document in the "test" collection
            DocumentReference testRef = db.collection("test").document();

            // Create a map to hold the test data
            Map<String, Object> testData = new HashMap<>();
            testData.put("studentID", studentID);
            testData.put("RecruiterId", currentUser.getUid());
            testData.put("jobId", jobID);
            testData.put("start_date", startDateString);
            testData.put("start_time", startTimeString);
            testData.put("end_date", endDateString);
            testData.put("end_time", endTimeString);

            // Add the test data to Firestore
            testRef.set(testData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Test document successfully added
                            submit.setEnabled(true);

                            Toast.makeText(ScheduleTestRecruiter.this, "Test scheduled successfully", Toast.LENGTH_SHORT).show();

                            // Now add questions to this test
                            addQuestionsToTest(testRef);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error adding test document
                            submit.setEnabled(true);

                            Toast.makeText(ScheduleTestRecruiter.this, "Error scheduling test: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            });



    }
    private void addQuestionsToTest(DocumentReference testRef) {
        // Get questions from adapters
        Map<String, Object> questionsData = new HashMap<>();
        List<String> textQuestions = new ArrayList<>();

        for (int i = 0; i < questionAdapter.getQuestionTestItemList().size(); i++) {
            String question = questionAdapter.getQuestionTestItemList().get(i).getQuestion();
            textQuestions.add(question);
        }

        List<Map<String, Object>> mcqQuestionsData = new ArrayList<>();
        for (int i = 0; i < Mcqadapter.getMcqQuestionItemList().size(); i++) {
            String question = Mcqadapter.getMcqQuestionItemList().get(i).getQuestion();
            String option1 = Mcqadapter.getMcqQuestionItemList().get(i).getOption1();
            String option2 = Mcqadapter.getMcqQuestionItemList().get(i).getOption2();
            String option3 = Mcqadapter.getMcqQuestionItemList().get(i).getOption3();
            String option4 = Mcqadapter.getMcqQuestionItemList().get(i).getOption4();
            Map<String, Object> mcqQuestionData = new HashMap<>();
            mcqQuestionData.put("question", question);
            mcqQuestionData.put("option1", option1);
            mcqQuestionData.put("option2", option2);
            mcqQuestionData.put("option3", option3);
            mcqQuestionData.put("option4", option4);
            mcqQuestionsData.add(mcqQuestionData);
        }

        // Create a map to hold the questions data
        questionsData.put("mcq_questions", mcqQuestionsData);
        questionsData.put("text_questions", textQuestions);

        // Add the questions data to Firestore as a subcollection of the test document
        testRef.collection("questions")
                .add(questionsData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        submit.setEnabled(true);

                        // Questions added successfully
                        Toast.makeText(ScheduleTestRecruiter.this, "Questions added to test", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ScheduleTestRecruiter.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error adding questions
                        submit.setEnabled(true);

                        Toast.makeText(ScheduleTestRecruiter.this, "Error adding questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showDatePicker(Context context, TextView dateEditText, TextView comparisonDateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        Calendar comparisonDate = getCalendarFromEditText(comparisonDateEditText);
                        if(dateEditText != start_date){
                        if (comparisonDate != null && selectedDate.before(comparisonDate)) {
                            Toast.makeText(ScheduleTestRecruiter.this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String formattedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                            dateEditText.setText(formattedDate);
                        }}
                        else {
                            if(!selectedDate.before(calendar)){
                                String formattedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                dateEditText.setText(formattedDate);
                            }
                            else {
                                Toast.makeText(ScheduleTestRecruiter.this, "Start date cannot be before today", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private Calendar getCalendarFromEditText(TextView dateEditText) {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateEditText.getText().toString());
            if (date != null) {
                calendar.setTime(date);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showTimePicker(Context context, TextView timeEditText) {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        boolean sameDay;
        Calendar start = start_date.getText().toString().isEmpty() ? null : Objects.requireNonNull(getCalendarFromEditText(start_date));
        Calendar end = end_date.getText().toString().isEmpty() ? null : Objects.requireNonNull(getCalendarFromEditText(end_date));
        sameDay = start == end;
        TimePickerDialog timePickerDialog;

        if (sameDay) {
            timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                            String selectedTimeStr = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                            if (timeEditText.getId() == R.id.end_time) {
                                Calendar startTime = getCalendarFromEditText(start_date, start_time);
                                if (startTime != null) {
                                    Calendar selectedTime = Calendar.getInstance();
                                    selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    selectedTime.set(Calendar.MINUTE, selectedMinute);
                                    if (selectedTime.before(startTime)) {
                                        Toast.makeText(ScheduleTestRecruiter.this, "End time cannot be before start time", Toast.LENGTH_SHORT).show();
                                    } else {
                                        timeEditText.setText(selectedTimeStr);
                                    }
                                }
                            } else {
                                timeEditText.setText(selectedTimeStr);
                            }
                        }
                    }, hourOfDay, minute, false);
        } else {
            // If start and end dates are not on the same day, no limit for end time
            timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                            timeEditText.setText(selectedTime);
                        }
                    }, hourOfDay, minute, false);
        }
        timePickerDialog.show();
    }


    private Calendar getCalendarFromEditText(TextView dateEditText, TextView timeEditText) {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(dateEditText.getText().toString() + " " + timeEditText.getText().toString());
            if (date != null) {
                calendar.setTime(date);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

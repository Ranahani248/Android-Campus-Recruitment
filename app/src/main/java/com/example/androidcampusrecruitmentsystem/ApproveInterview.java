package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApproveInterview extends AppCompatActivity {
    String from_date, to_date, from_time, to_time,scheduleinterviewID,recruiterID,jobID;

    TextView from_date_text, to_date_text, from_time_text, to_time_text, schedule_date, schedule_time;
    Calendar calendar;

    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_interview);
       from_date = getIntent().getStringExtra("fromDate");
       to_date = getIntent().getStringExtra("toDate");
       from_time = getIntent().getStringExtra("fromTime");
       to_time = getIntent().getStringExtra("toTime");
       scheduleinterviewID = getIntent().getStringExtra("interviewId");
       recruiterID = getIntent().getStringExtra("recruiterId");
       jobID = getIntent().getStringExtra("jobId");

       calendar = Calendar.getInstance();

       from_date_text = findViewById(R.id.from_date);
       to_date_text = findViewById(R.id.to_date);
       from_time_text = findViewById(R.id.from_time);
       to_time_text = findViewById(R.id.to_time);
       schedule_date = findViewById(R.id.date);
       schedule_time = findViewById(R.id.time);
       submit = findViewById(R.id.submit_interview_student);

       from_date_text.setText(from_date);
       to_date_text.setText(to_date);
       from_time_text.setText(from_time);
       to_time_text.setText(to_time);

       schedule_date.setOnClickListener(v -> {showDatePickerDialog(schedule_date);

       });
       schedule_time.setOnClickListener(v -> {showTimePickerDialog(schedule_time);
       });

       submit.setOnClickListener(v ->{
           if (schedule_date.getText().toString().isEmpty()) {
               schedule_date.setError("Field can't be empty");
               schedule_date.requestFocus();
               return;
           }
           if (schedule_time.getText().toString().isEmpty()) {
               schedule_time.setError("Field can't be empty");
               schedule_time.requestFocus();
               return;
           }
           if(!validateDates()){
               Toast.makeText(this, "date is not valid", Toast.LENGTH_SHORT).show();
               return;
           }
           if(!validateTimes()){
               Toast.makeText(this, "Time is not valid", Toast.LENGTH_SHORT).show();
               return;
           }
           FirebaseFirestore firestore = FirebaseFirestore.getInstance();
           Map<String, Object> interview = new HashMap<>();
           FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
           String uid = firebaseAuth.getUid();
           interview.put("studentID", uid);
           interview.put("scheduleDate", schedule_date.getText().toString());
           interview.put("scheduleTime", schedule_time.getText().toString());
           interview.put("recruiterID",recruiterID);
           interview.put("jobID",jobID);
           firestore.collection("Interviews").add(interview)
                   .addOnSuccessListener(documentReference -> {
                       firestore.collection("ScheduleInterview").document(scheduleinterviewID).delete();
                       Toast.makeText(this, "Interview Scheduled", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(ApproveInterview.this, MainActivity.class);
                       startActivity(intent);

                   }) ;


       });

    }
    private void showDatePickerDialog(TextView editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        };

        new DatePickerDialog(
                ApproveInterview.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    private void showTimePickerDialog(TextView editText) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        };

        new TimePickerDialog(
                ApproveInterview.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
                .show();
    }
    public boolean validateDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date fromDate = sdf.parse(from_date_text.getText().toString());
            Date scheduled = sdf.parse(schedule_date.getText().toString());
            Date toDate = sdf.parse(to_date_text.getText().toString());
            if(scheduled.before(fromDate)) {
                return false;
            }
            if(scheduled.after(toDate)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean validateTimes() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date fromTime = sdf.parse(from_time_text.getText().toString());
            Date scheduled = sdf.parse(schedule_time.getText().toString());
            Date toTime = sdf.parse(to_time_text.getText().toString());
            if(scheduled.before(fromTime)) {
                return false;
            }
            if(scheduled.after(toTime)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
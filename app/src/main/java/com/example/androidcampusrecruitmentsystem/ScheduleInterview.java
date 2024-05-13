package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScheduleInterview extends AppCompatActivity {
    TextView from_date, to_date, from_time, to_time;
    Button submit;
    String studentID, JobID;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_interview);
        studentID = getIntent().getStringExtra("studentId");
        JobID = getIntent().getStringExtra("jobId");
        from_date = findViewById(R.id.from_date);
        to_date = findViewById(R.id.to_date);
        from_time = findViewById(R.id.from_time);
        to_time = findViewById(R.id.to_time);
        submit = findViewById(R.id.submit_interview_recruiter);


        calendar = Calendar.getInstance();

        from_date.setOnClickListener(v -> showDatePickerDialog(from_date));
        to_date.setOnClickListener(v -> showDatePickerDialog(to_date));
        from_time.setOnClickListener(v -> showTimePickerDialog(from_time));
        to_time.setOnClickListener(v -> showTimePickerDialog(to_time));

        submit.setOnClickListener(v ->{
            if(from_date.getText().toString().isEmpty()){
                from_date.setError("Field can't be empty");
                from_date.requestFocus();
                return;
            }
            if (to_date.getText().toString().isEmpty()) {
                to_date.setError("Field can't be empty");
                to_date.requestFocus();
                return;
            }
            if (from_time.getText().toString().isEmpty()) {
                from_time.setError("Field can't be empty");
                from_time.requestFocus();
                return;
            }
            if (to_time.getText().toString().isEmpty()) {
                to_time.setError("Field can't be empty");
                to_time.requestFocus();
                return;
            }
            if(!validateInputs()){
                return;
            }

//            submit.setEnabled(false);
            String fromdate = from_date.getText().toString();
            String todate = to_date.getText().toString();
            String fromtime = from_time.getText().toString();
            String totime = to_time.getText().toString();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String uid = firebaseAuth.getUid();
            Map<String, Object> interview = new HashMap<>();
            interview.put("fromdate", fromdate);
            interview.put("todate", todate);
            interview.put("fromtime", fromtime);
            interview.put("totime", totime);
            interview.put("recruiterID", Objects.requireNonNull(uid));
            interview.put("studentID", studentID);
            interview.put("jobID", JobID);
            firestore.collection("ScheduleInterview").add(interview).addOnSuccessListener(documentReference -> {
                Toast.makeText(ScheduleInterview.this, "Interview scheduled successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScheduleInterview.this, MainActivityRecruiter.class);
                startActivity(intent);
            });


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
                ScheduleInterview.this,
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
                ScheduleInterview.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
                .show();
    }

    private boolean validateInputs() {
        if (from_date.getText().toString().isEmpty() || to_date.getText().toString().isEmpty() ||
                from_time.getText().toString().isEmpty() || to_time.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateDates()) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateTimes()) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date fromDate = sdf.parse(from_date.getText().toString());
            Date toDate = sdf.parse(to_date.getText().toString());

            return !toDate.before(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateTimes() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date fromTime = sdf.parse(from_time.getText().toString());
            Date toTime = sdf.parse(to_time.getText().toString());

            return !toTime.before(fromTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}


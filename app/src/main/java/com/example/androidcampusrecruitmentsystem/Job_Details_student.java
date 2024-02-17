package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Job_Details_student extends AppCompatActivity {

    static String jobid;
static TextView jobtitle_studentdetails, jobCompany_studentdetails, joblocation_studentdetails, jobdescription_studentdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_student);
        jobtitle_studentdetails = findViewById(R.id.job_details_student_title);
        jobCompany_studentdetails = findViewById(R.id.job_details_student_company);
        joblocation_studentdetails = findViewById(R.id.job_details_student_location);
        jobdescription_studentdetails = findViewById(R.id.job_details_student_description);
        for(JobItem jobItem: JobAdapter.jobList){
            if(jobItem.getJobid().equals(jobid)){
                jobtitle_studentdetails.setText(jobItem.getJobTitle());
                jobdescription_studentdetails.setText(jobItem.getJobDescription());
                jobCompany_studentdetails.setText(jobItem.getJobCompany());
                joblocation_studentdetails.setText(jobItem.getJobLocation());
                break;
            }
        }

    }
}
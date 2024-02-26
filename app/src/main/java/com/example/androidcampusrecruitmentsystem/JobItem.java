package com.example.androidcampusrecruitmentsystem;

public class JobItem {
    private String  jobid,jobTitle, jobDescription, jobCompany, jobLocation,jobSalary, recruiterid;

    public String getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(String jobSalary) {
        this.jobSalary = jobSalary;
    }

    public String getRecruiterid() {
        return recruiterid;
    }

    public void setRecruiterid(String recruiterid) {
        this.recruiterid = recruiterid;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobCompany() {
        return jobCompany;
    }

    public void setJobCompany(String jobCompany) {
        this.jobCompany = jobCompany;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public JobItem( String jobid,String jobTitle, String jobDescription, String jobCompany,String jobSalary, String jobLocation, String recruiterId) {
       this.jobid = jobid;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobCompany = jobCompany;
        this.jobSalary = jobSalary;
        this.jobLocation = jobLocation;
        this.recruiterid = recruiterId;
    }
    public JobItem() {
        // Default constructor required for Firestore deserialization
    }

    public String getJobTitle() {
        return jobTitle;
    }
}


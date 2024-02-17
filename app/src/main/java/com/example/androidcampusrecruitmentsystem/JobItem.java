package com.example.androidcampusrecruitmentsystem;

public class JobItem {
    private String  jobid,jobTitle, jobDescription, jobCompany, jobLocation;

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

    public JobItem( String jobid,String jobTitle, String jobDescription, String jobCompany, String jobLocation) {
       this.jobid = jobid;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobCompany = jobCompany;
        this.jobLocation = jobLocation;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}


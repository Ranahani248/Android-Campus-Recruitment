package com.example.androidcampusrecruitmentsystem;

public class InterviewItem {
    String from_date, to_date, from_time, to_time, recruiterName, jobTitle,scheduleinterviewID,recruiterID,jobID;

    public InterviewItem(String from_date, String to_date, String from_time, String to_time, String recruiterName, String jobTitle, String scheduleinterviewID, String recruiterID, String jobID) {
        this.from_date = from_date;
        this.to_date = to_date;
        this.from_time = from_time;
        this.to_time = to_time;
        this.recruiterName = recruiterName;
        this.jobTitle = jobTitle;
        this.scheduleinterviewID = scheduleinterviewID;
        this.recruiterID = recruiterID;
        this.jobID = jobID;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getScheduleinterviewID() {
        return scheduleinterviewID;
    }

    public void setScheduleinterviewID(String scheduleinterviewID) {
        this.scheduleinterviewID = scheduleinterviewID;
    }

    public String getRecruiterID() {
        return recruiterID;
    }

    public void setRecruiterID(String recruiterID) {
        this.recruiterID = recruiterID;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
}

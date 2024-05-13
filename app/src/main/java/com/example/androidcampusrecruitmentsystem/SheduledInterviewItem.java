package com.example.androidcampusrecruitmentsystem;

public class SheduledInterviewItem {
    private String startDate;
    private String startTime;
    private String jobtitle;
    private String RecruiterName;

    String outgoingId;
    String interviewId;

    public SheduledInterviewItem(String startDate, String startTime, String jobtitle, String recruiterName, String interviewId, String outgoingId) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.jobtitle = jobtitle;
        this.RecruiterName = recruiterName;
        this.interviewId = interviewId;
        this.outgoingId = outgoingId;
    }

    public String getOutgoingId() {
        return outgoingId;
    }

    public void setOutgoingId(String outgoingId) {
        this.outgoingId = outgoingId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getRecruiterName() {
        return RecruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        RecruiterName = recruiterName;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
    }
}

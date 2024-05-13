package com.example.androidcampusrecruitmentsystem;

public class FeedbackITem {

    String feedback;
    String recruiter;
    String Jobtitle;
    String feedbackId;

    public FeedbackITem(String feedback, String recruiter, String jobtitle,String feedbackId) {
        this.feedback = feedback;
        this.recruiter = recruiter;
        this.Jobtitle = jobtitle;
        this.feedbackId = feedbackId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(String recruiter) {
        this.recruiter = recruiter;
    }

    public String getJobtitle() {
        return Jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        Jobtitle = jobtitle;
    }
}

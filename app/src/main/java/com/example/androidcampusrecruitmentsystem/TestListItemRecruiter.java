package com.example.androidcampusrecruitmentsystem;

public class TestListItemRecruiter {
    private String startDate;
    private String endDate;
    private String StudentName;
    private String RecruiterName;

    String testId;

    public String getTestId() {
        return testId;
    }

    private boolean isApplied;


    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public void setRecruiterName(String recruiterName) {
        RecruiterName = recruiterName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    private String jobTitle;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStudentName() {
        return StudentName;
    }

    public String getRecruiterName() {
        return RecruiterName;
    }

    public boolean isisApplied() {
        return isApplied;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public TestListItemRecruiter(String startDate, String endDate, String studentName, String jobTitle, String testId,boolean isApplied) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.StudentName = studentName;
        this.jobTitle = jobTitle;
        this.isApplied = isApplied;
        this.testId = testId;
    }

    public TestListItemRecruiter( String startDate, String endDate, String recruiterName, String jobTitle,String testId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.RecruiterName = recruiterName;
        this.jobTitle = jobTitle;
        this.testId = testId;
    }
}
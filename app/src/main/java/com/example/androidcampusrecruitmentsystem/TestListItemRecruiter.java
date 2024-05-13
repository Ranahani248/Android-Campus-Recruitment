package com.example.androidcampusrecruitmentsystem;

public class TestListItemRecruiter {

    private String StudentName;
    private String jobTitle;
    String attemptedTestId;

    public TestListItemRecruiter(String studentName, String jobTitle, String attemptedTestId) {
        this.StudentName = studentName;
        this.jobTitle = jobTitle;
        this.attemptedTestId = attemptedTestId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getRecruiterName() {
        return jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAttemptedTestId() {
        return attemptedTestId;
    }

    public void setAttemptedTestId(String testId) {
        this.attemptedTestId = testId;
    }
}

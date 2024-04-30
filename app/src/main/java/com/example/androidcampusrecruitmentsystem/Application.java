package com.example.androidcampusrecruitmentsystem;

public class Application {
    private String jobTitle;
    private String applicantName, profilePictureUrl;

    private String jobid;
    // Add more attributes as needed
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public Application(String jobTitle, String applicantName, String profilePictureUrl, String studentId, String jobid) {
        this.jobTitle = jobTitle;
        this.applicantName = applicantName;
        this.profilePictureUrl = profilePictureUrl;
        this.studentId = studentId;
        this.jobid = jobid;
        // Initialize other attributes as needed
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobid() {
        return jobid;
    }

    public String getApplicantName() {
        return applicantName;
    }

    // Add getter and setter methods for other attributes as needed
}

package com.example.androidcampusrecruitmentsystem;

public class Job {
        public String jobId;
        public String recruiterId;
        public String jobTitle;
        public String companyName;
        public String location;
        public String description;

        public Job() {
        }

        public Job(String jobId, String recruiterId, String jobTitle, String companyName, String location, String description) {
            this.jobId = jobId;
            this.recruiterId = recruiterId;
            this.jobTitle = jobTitle;
            this.companyName = companyName;
            this.location = location;
            this.description = description;
        }
    }



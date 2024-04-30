package com.example.androidcampusrecruitmentsystem;

import java.util.ArrayList;
import java.util.List;

public class SentStudentsSingleton {
    private static SentStudentsSingleton instance;
    private ArrayList<String> sentStudentIds = new ArrayList<>();

    private SentStudentsSingleton() {}

    public static SentStudentsSingleton getInstance() {
        if (instance == null) {
            instance = new SentStudentsSingleton();
        }
        return instance;
    }

    public ArrayList<String> getSentStudentIds() {
        return sentStudentIds;
    }
    public void addSentStudentIds(List<String> studentIds) {
        sentStudentIds.addAll(studentIds);
    }
}
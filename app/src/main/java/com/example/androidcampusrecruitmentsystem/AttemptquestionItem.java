package com.example.androidcampusrecruitmentsystem;

public class AttemptquestionItem {

    public String question;

    public String answer;

    public AttemptquestionItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

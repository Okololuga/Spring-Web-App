package com.example.engine.entity;

import java.util.List;

public class Answer {
    private int[] answer;

    public Answer() {
        answer = new int[]{-1};
    }
    public Answer(int[] answer) {
        this.answer = answer;
    }


    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }
}
